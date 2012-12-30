package edu.ncsu.soc.rms;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.telephony.*;

public class RingerManagerService extends Service {
  private static final String TAG = "RingerManagerService";
  String number;
  String stateString="N/A" ; 
  

  @Override
  public IBinder onBind(Intent arg0) {
    return null;
  }

  @Override
  public void onCreate() {
    Log.i(TAG, "Service created");
    TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
          number = incomingNumber;
          switch (state) {
          case TelephonyManager.CALL_STATE_IDLE:
        	if(stateString.equals("Ringing")){
        		System.out.println("Missed Call");
        		if(IntroActivity.setting == 0 )
        			sendSMS();
        		else
        			sendEmail();
        	}
            stateString = "Idle";
            break;
          case TelephonyManager.CALL_STATE_OFFHOOK:        	
            stateString = "Off Hook";
            break;
          case TelephonyManager.CALL_STATE_RINGING:        	  
            stateString = "Ringing";
            break;
          }
        }		
      };
      
      tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
  }
  
  protected void sendSMS() {
	  // TODO Auto-generated method stub
	  System.out.println("sending SMS");
      LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      SmsManager sm = SmsManager.getDefault();  
      String message = "The user cannot receive the call now since he is at location: " + "latitude: " +
        				  				location.getLatitude() + " longitude: " + location.getLongitude();
      System.out.println(number);
      sm.sendTextMessage(number, null, message, null, null);
   }
	

	protected void sendEmail() {
	// TODO Auto-generated method stub
		System.out.println("Sending Email");
		String id;
		String emailIdOfContact="";
		ContentResolver cr = getContentResolver();
		String phoneNum = "";
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cur.moveToNext())
        {
        	phoneNum = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));      	
        	id = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
        	if(phoneNum.replaceAll("-", "").equals(number)){
                Cursor emails = cr.query(Email.CONTENT_URI, null,
                        Email.CONTACT_ID + " = ?", new String[]{ id }, null);
                while (emails.moveToNext()) {
                    emailIdOfContact = emails.getString(emails.getColumnIndex(Email.DATA));
                }
                emails.close();
                break;
        	}
        }
        cur.close();
			  
  		  LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
  		  Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
  		  
  		  Intent intent = new Intent(Intent.ACTION_SENDTO); 
  		  intent.setType("text/plain");
  		  intent.putExtra(Intent.EXTRA_SUBJECT, "Missed call message");
  		  intent.putExtra(Intent.EXTRA_TEXT, "Hi, I'm unable to recieve your call right now since I'm at location " + 
  				  	"latitude: " + location.getLatitude() + " " +
  				  	"longitude: " + location.getLongitude());
  		  intent.setData(Uri.parse("mailto:"+emailIdOfContact)); 
  		  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
  		  startActivity(intent);
  		}
	}









  