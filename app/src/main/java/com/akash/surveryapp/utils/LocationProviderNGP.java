package com.akash.surveryapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.akash.surveryapp.database.DatabaseAdapter;


public class LocationProviderNGP implements LocationListener,
        android.location.GpsStatus.Listener {

    static final int HIST_LEN = 3;

    boolean mIsFixed = false;
    Context mContext = null;
    Location mHistory[] = null;
    LocationManager locationManager = null;

    /**
     * If we get fixed satellites >= mFixSatellites mFixed => true
     */
    final int mFixSatellites = 2;

    /**
     * If we get location updates with time difference <= mFixTime mFixed =>
     * true
     */
    final int mFixTime = 15;

    int mKnownSatellites = 0;
    int mUsedInLastFixSatellites = 0;

    DatabaseAdapter db;
    public static SharedPreferences sp;
    public static final String MY_PREF = "MyPreferences";

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 1 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1; // 1 second

    private static Location lastKnownLocationLM;

    public LocationProviderNGP(Context context) {
        mContext = context;
        db = new DatabaseAdapter(mContext);
        sp = mContext.getSharedPreferences(MY_PREF, 0);
        mHistory = new Location[HIST_LEN];
    }

    public Location getLastKnownLocationByLM() {

        Location _lastKnownLocationNetworkProvider = null, _lastKnownLocationGPSProvider = null;
        float _cachedNetworkAccuracy = 0, _cachedGPSAccuracy = 0;

        try {
            _lastKnownLocationNetworkProvider = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            _lastKnownLocationGPSProvider = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Exception exc) {
        }

        if (_lastKnownLocationNetworkProvider != null) {
            _cachedNetworkAccuracy = _lastKnownLocationNetworkProvider.getAccuracy();
        }
        if (_lastKnownLocationGPSProvider != null) {
            _cachedGPSAccuracy = _lastKnownLocationGPSProvider.getAccuracy();
        }

        //NOTE: This does not take into account if _cachedGPSTime == _cachedNetworkTime
        //The changes of that happening are small.
        if (_lastKnownLocationGPSProvider != null && _lastKnownLocationNetworkProvider != null) {
            long cachedGPSTime = _lastKnownLocationGPSProvider.getTime();
            long cachedNetworkTime = _lastKnownLocationNetworkProvider.getTime();
            if (cachedGPSTime > cachedNetworkTime && _cachedNetworkAccuracy != 0.0) {
                return _lastKnownLocationGPSProvider;
            } else if (cachedNetworkTime > cachedGPSTime && _cachedGPSAccuracy != 0.0) {
                return _lastKnownLocationNetworkProvider;
            }
        }

        if (_lastKnownLocationGPSProvider == null && _lastKnownLocationNetworkProvider != null) {
            return _lastKnownLocationNetworkProvider;
        }

        if (_lastKnownLocationGPSProvider != null && _lastKnownLocationNetworkProvider == null) {
            return _lastKnownLocationGPSProvider;
        }
        return null;
    }

    public static Location getLastKnownLocation() {

        if (lastKnownLocationLM != null) {
            return lastKnownLocationLM;
        }
        return getLocationFromPreferences();
    }

    private synchronized static Location getLocationFromPreferences() {
        Location targetLocation = new Location("");//provider name is unecessary
        if (!sp.getString("latitude", "0").equals("0")) {
            targetLocation.setLatitude(Double.parseDouble(sp.getString("latitude", "0")));//your coords of course
            targetLocation.setLongitude(Double.parseDouble(sp.getString("longitude", "0")));
            targetLocation.setAccuracy(sp.getFloat("accuracy", 0));
            targetLocation.setTime(sp.getLong("time", 0));
            targetLocation.setBearing(sp.getFloat("bearing", 0));
            targetLocation.setSpeed(sp.getFloat("speed", 0));
            targetLocation.setAltitude(Double.parseDouble(sp.getString("altitude", "0")));
            return targetLocation;
        }
        return null;
    }

    public static void updateLocationObject(Location location, String type) {

        if (location == null) {
            return;
        } else {
            if (lastKnownLocationLM == null) {
                lastKnownLocationLM = location;
            } else if (location.getTime() > lastKnownLocationLM.getTime()) {
                lastKnownLocationLM = location;
            }
            saveLocationInPreference(lastKnownLocationLM);
        }
    }

    private static synchronized void saveLocationInPreference(Location location) {
        //sp = mContext.getSharedPreferences(MY_PREF, 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("latitude", String.valueOf(location.getLatitude()));
        edit.putString("longitude", String.valueOf(location.getLongitude()));
        edit.putFloat("accuracy", location.getAccuracy());
        edit.putFloat("speed", location.getSpeed());
        edit.putFloat("bearing", location.getBearing());
        edit.putString("altitude", String.valueOf(location.getAltitude()));
        edit.putLong("time", location.getTime());
        edit.commit();
    }

    public interface IGetLocationBYLM {
        public void onLocationChanged(Location location);
    }

    private IGetLocationBYLM mGetCurrentLocation;

    public void startGettingLocation(IGetLocationBYLM location) {
        mGetCurrentLocation = location;
        startLocUpdate();
    }

    public void startLocUpdate() {
        clear(true);
        stopLocUpdate();
        LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        try {

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
         } catch (Exception ex) {
            lm = null;
        }

        if (lm != null) {
            locationManager = lm;
            locationManager.addGpsStatusListener(this);
        }
    }

    public void stopLocUpdate() {

        if (locationManager != null) {
            locationManager.removeGpsStatusListener(this);
            locationManager.removeUpdates(this);
            locationManager = null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location == null) {
            return;
        }

        if (location.getLatitude() == 0 && location.getLongitude() == 0) {
            return;
        }

        for (int i = 1; i < HIST_LEN; i++) {
            mHistory[i] = mHistory[i - 1];
        }

        mHistory[0] = location;

        if (location.hasAccuracy()) {
            mIsFixed = true;
            mGetCurrentLocation.onLocationChanged(location);
        } else if (mHistory[1] != null && isBetterLocation(location,mHistory[1])) {
            mIsFixed = true;
            mGetCurrentLocation.onLocationChanged(location);

        } else if (mKnownSatellites >= mFixSatellites) {
            mIsFixed = true;
            mGetCurrentLocation.onLocationChanged(location);
        }
    }


    protected boolean isBetterLocation(Location location, Location currentBestLocation) {

        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > MIN_TIME_BW_UPDATES;
        boolean isSignificantlyOlder = timeDelta < -MIN_TIME_BW_UPDATES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equalsIgnoreCase("gps")) {
            clear(true);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

        if (provider.equalsIgnoreCase("gps")) {
            clear(false);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (provider.equalsIgnoreCase("gps")) {
            if (status == LocationProvider.OUT_OF_SERVICE
                    || status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                clear(true);
            }
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (locationManager == null)
            return;

        android.location.GpsStatus gpsStatus = locationManager
                .getGpsStatus(null);

        if (gpsStatus == null)
            return;

        int cnt0 = 0, cnt1 = 0;
        Iterable<GpsSatellite> list = gpsStatus.getSatellites();
        for (GpsSatellite satellite : list) {
            cnt0++;
            if (satellite.usedInFix()) {
                cnt1++;
            }
        }
        mKnownSatellites = cnt0;
        mUsedInLastFixSatellites = cnt1;
    }

    private void clear(boolean resetIsFixed) {
        if (resetIsFixed) {
            mIsFixed = false;
        }
        mKnownSatellites = 0;
        mUsedInLastFixSatellites = 0;
        for (int i = 0; i < HIST_LEN; i++)
            mHistory[i] = null;
    }

    public boolean isEnabled() {
        LocationManager lm = (LocationManager) mContext
                .getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
