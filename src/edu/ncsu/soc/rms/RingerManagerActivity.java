package edu.ncsu.soc.rms;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class RingerManagerActivity extends MapActivity {
		MapController mapController;
		DetailsOverlay positionOverlay;
	
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		startService(new Intent(this, RingerManagerService.class));
                  
		MapView myMapView = (MapView) findViewById(R.id.myMapView);
	    mapController = myMapView.getController();

	    // Configure the map display options
	    myMapView.setSatellite(true);

	    // Zoom in
	    mapController.setZoom(17);

	    positionOverlay = new DetailsOverlay(this);
	    List<Overlay> overlays = myMapView.getOverlays();
	    overlays.add(positionOverlay);
	    
//	    locationPointsOverlay = new PointsOverlay(this);
//	    overlays.add(locationPointsOverlay);
	    
	    
	    		
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = LocationManager.NETWORK_PROVIDER;        
        LocationListener loclis = new LocationListener(){

			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				updateWithNewLocation(location);
				updateRingerMode(location);
				//markPoints(location);
			}

			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
		};

		locationManager.requestLocationUpdates(provider, 0, 0, loclis); 
						
    }

	protected void updateRingerMode(Location location) {
		// TODO Auto-generated method stub
		if(location != null)
		{
			 String[] colsArray = new String[] {RMSProvider.KEY_PLACE_LAT, RMSProvider.KEY_PLACE_LNG, RMSProvider.KEY_RINGER_MODE, RMSProvider.KEY_MODE_NAME};
		     Uri rmsDB = RMSProvider.CONTENT_URI;
		     Cursor ringerCur = managedQuery(rmsDB, colsArray, null, null, null);
		     
		     TextView myRingerModeText = (TextView) findViewById(R.id.textView1);

		     
		      if (ringerCur.moveToFirst()) {
		      	int latColIndex = ringerCur.getColumnIndex(RMSProvider.KEY_PLACE_LAT);
		      	int lngColIndex = ringerCur.getColumnIndex(RMSProvider.KEY_PLACE_LNG);
		      	int ringerModeColIndex = ringerCur.getColumnIndex(RMSProvider.KEY_RINGER_MODE);
		      	//int modeNameColIndex = ringerCur.getColumnIndex(RMSProvider.KEY_MODE_NAME);
		      	double dist;
		      	
		      	/** This code compares the point coordinates sent via DDMS with all the point 
		      	 * co-ordinates present in the Content Provider. If the distance is less than 50m 
		      	 * and if any alert is available, it will be displayed.  */
		      		 
		      	AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);  
		      	
		      	do{
		      		double latit = ringerCur.getDouble(latColIndex);
		      		double longit = ringerCur.getDouble(lngColIndex);
		      		int ringerModeInDB = ringerCur.getInt(ringerModeColIndex);
		      		//String modeName = ringerCur.getString(modeNameColIndex);
		      		
		      		dist= getDistance(location.getLatitude(), location.getLongitude(), latit/1E6, longit/1E6);
		    		
		      		int currentMode = am.getRingerMode();
		      		//System.out.println(dist+"+ "+ringerModeInDB);
		    		if(dist < 100.00){
		    			if(ringerModeInDB == 0){
		    				am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		    				am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
		    				myRingerModeText.setText("Ringer and Vibrate mode");
		    			}
		    			else if(ringerModeInDB == 1){
		    				am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	    					am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
		    				myRingerModeText.setText("Ringer and No Vibrate mode");
		    			}
	    				else if(ringerModeInDB == 2){
			    				am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		    					am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
			    				myRingerModeText.setText("Silent and Vibrate mode");
	    				}
	    				else if(ringerModeInDB == 3){
		    				am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	    					am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);		    				
	    					myRingerModeText.setText("Silent and No Vibrate mode");
	    				}
		    		}
		    		else{
		    				am.setRingerMode(currentMode);
		    				myRingerModeText.setText("Ringer and Vibrate mode");
		    	 		}	      		
		      	} while (ringerCur.moveToNext());
		      	
		    }  
		}
	}

	public static double getDistance(double lat1, double lon1, double lat2, double lon2) {

		    final double Radius = 6371 * 1E3; // Earth's mean radius

		    double dLat = Math.toRadians(lat2 - lat1);
		    double dLon = Math.toRadians(lon2 - lon1);
		    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
		        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		    return Radius * c;
		  }
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}  
  

    /** Update UI with a new location */
    private void updateWithNewLocation(Location location) {
     TextView myLocationText = (TextView) findViewById(R.id.locText);
      String latLongString;
      if (location != null) {
        // Update the map location.
        Double geoLat = location.getLatitude() * 1E6;
        Double geoLng = location.getLongitude() * 1E6;
        GeoPoint point = new GeoPoint(geoLat.intValue(), geoLng.intValue());
        
        mapController.animateTo(point);

        // update my position marker
        positionOverlay.setLocation(location);

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        latLongString = "Lat:" + lat + "\nLong:" + lng;
      } 
      else {
        latLongString = "No location found";
      }
     myLocationText.setText("Your Current Position is:\n" + latLongString);
    }
}
      
   

