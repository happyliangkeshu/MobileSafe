package com.trees.mobilesafe.service;



import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;

public class GPSService extends Service {
	private MylocationListener listener;
	private LocationManager lm;
	private SharedPreferences sp;
	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		listener = new MylocationListener();		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);		
		String provider  = lm.getBestProvider(criteria, true);	
		lm.requestLocationUpdates("gps", 0, 0, listener);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		lm.removeUpdates(listener);
		listener = null;
	}
	private class MylocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			String longitude = "jingdu:" + location.getLongitude() + "\n";
			String latitude = "weidu:"	+ location.getLatitude() + "\n";
			String accuracy = "jingquedu:" + location.getAccuracy() + "\n";
			// 
			Editor editor = sp.edit();
			editor.putString("lastlocation", longitude + latitude + accuracy); 
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
			
		}
}
}
