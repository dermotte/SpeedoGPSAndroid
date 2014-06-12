package at.juggle.speedometer.android;
/*
    Copyright (C) 2014  Mathias Lux, mathias@juggle.at

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    Note that the code for the LocationManager has been adopted from
    GPSSpeedo https://code.google.com/p/gpspeedo/ (GPL v3)
 */

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import at.juggle.speedometer.Speedo.R;

/**
 * Borrowed Code from GPSSpeedo https://code.google.com/p/gpspeedo/
 */

public class Speedo extends Activity {

    private LocationManager lm;
    private LocationListener locationListener;
    private Integer data_points = 2; // how many data points to calculate for
    private Double[][] positions;
    private Long[] times;
    private Boolean mirror_pref, full_screen_pref; // Preference Booleans
    private Integer units; // Preference integers
    private Float text_size; // Preference Float

    protected SpeedView speedView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        speedView = (SpeedView) findViewById(R.id.view);

        // two arrays for position and time.
        positions = new Double[data_points][2];
        times = new Long[data_points];

        // setting and running location manager
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        lm.removeUpdates(locationListener);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private class MyLocationListener implements LocationListener {
        Integer counter = 0;

        public void onLocationChanged(Location loc) {
            if (loc != null) {
                Double d1;
                Long t1;
                Double speed = 0.0;
                d1 = 0.0;
                t1 = 0l;

                positions[counter][0] = loc.getLatitude();
                positions[counter][1] = loc.getLongitude();
                times[counter] = loc.getTime();

                if (loc.hasSpeed()) {
                    speed = loc.getSpeed() * 1.0; // need to * 1.0 to get into a double for some reason...
                } else {
                    try {
                        // get the distance and time between the current position, and the previous position.
                        // using (counter - 1) % data_points doesn't wrap properly
                        d1 = distance(positions[counter][0], positions[counter][1], positions[(counter + (data_points - 1)) % data_points][0], positions[(counter + (data_points - 1)) % data_points][1]);
                        t1 = times[counter] - times[(counter + (data_points - 1)) % data_points];
                    } catch (NullPointerException e) {
                        //all good, just not enough data yet.
                    }
                    speed = d1 / t1; // m/s
                }
                counter = (counter + 1) % data_points;

                // convert from m/s to kmh
//                switch (units) {
//                    case R.id.kmph:
                speed = speed * 3.6d;
//                        break;
//                    case R.id.mph:
//                        speed = speed * 2.23693629d;
//                        break;
//                    case R.id.knots:
//                        speed = speed * 1.94384449d;
//                        break;
//                }
//                displayText(speed.intValue());
                speedView.setSpeed(speed.intValue());
            } else {
                speedView.setSpeed(-1);
            }
        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            Log.i(getResources().getString(R.string.app_name), "Speedo provider disabled : " + provider);
        }


        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            Log.i(getResources().getString(R.string.app_name), "Speedo provider enabled : " + provider);
        }


        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            Log.i(getResources().getString(R.string.app_name), "Speedo status changed : " + extras.toString());
            if (extras.get("satellites") != null) {
                speedView.setSatellites(extras.getInt("satellites"));
            }
        }

        // private functions
        private double distance(double lat1, double lon1, double lat2, double lon2) {
            // haversine great circle distance approximation, returns meters
            double theta = lon1 - lon2;
            double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60; // 60 nautical miles per degree of seperation
            dist = dist * 1852; // 1852 meters per nautical mile
            return (dist);
        }

        private double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
        }

        private double rad2deg(double rad) {
            return (rad * 180.0 / Math.PI);
        }
    }
//    public boolean onTouchEvent(MotionEvent event) {
//        SettingsDialog d = new SettingsDialog(getApplicationContext(), null);
//        d.showDialog(null);
//        return true;
//    }


}
