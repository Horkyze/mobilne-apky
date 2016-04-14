package sk.stuba.fiit.revizori.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import sk.stuba.fiit.revizori.Revizori;

public class LocationService {
    private LocationManager locationManager;
    private List<String> providers;
    Location bestLocation;

    private static LocationService ourInstance = new LocationService();

    public static LocationService getInstance() {
        return ourInstance;
    }

    private LocationService() {
        locationManager = (LocationManager) Revizori.getAppContext().getSystemService(Context.LOCATION_SERVICE);
        providers = locationManager.getProviders(true);
    }

    public Location getBestLocation() {
        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(Revizori.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(Revizori.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }
}
