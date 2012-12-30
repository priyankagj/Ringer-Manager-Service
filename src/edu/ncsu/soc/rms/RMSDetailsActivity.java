package edu.ncsu.soc.rms;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class RMSDetailsActivity extends Activity{
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterdetails);
		//startService(new Intent(this, RingerManagerService.class));
		    
        Button cancel = (Button) findViewById(R.id.buttonCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        
        Button ok = (Button) findViewById(R.id.buttonOK);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent inte = getIntent();
            	Bundle extras = inte.getExtras();
            	
            	float latitude = 0;
            	float longitude = 0;
            	
            	EditText modeNameEditText = (EditText)findViewById(R.id.txtModeName);
                String modeName = modeNameEditText.getText().toString();
            	
                int ringerMode = 0;                
                
                RadioGroup ringerToneButtonIdChecked = (RadioGroup)findViewById(R.id.radioGroup1);
                RadioGroup vibrateButtonIdChecked = (RadioGroup)findViewById(R.id.radioGroup2);
                        
                RadioButton ringerToneButtonID1 = (RadioButton)findViewById(R.id.radioNormal);
                RadioButton ringerToneButtonID2 = (RadioButton)findViewById(R.id.radioSilent);

                RadioButton vibrateButtonID1 = (RadioButton)findViewById(R.id.radioYes);
                RadioButton vibrateButtonID2 = (RadioButton)findViewById(R.id.radioNo);
                
                if(ringerToneButtonIdChecked.getCheckedRadioButtonId() == ringerToneButtonID1.getId())      
                {
                	if(vibrateButtonIdChecked.getCheckedRadioButtonId() == vibrateButtonID1.getId())
                	{
                		ringerMode = 0;
                	}
                	else if(vibrateButtonIdChecked.getCheckedRadioButtonId() == vibrateButtonID2.getId())
                	{
                		ringerMode = 1;
                	}
                }
                else if(ringerToneButtonIdChecked.getCheckedRadioButtonId() == ringerToneButtonID2.getId())      
                {
                	if(vibrateButtonIdChecked.getCheckedRadioButtonId() == vibrateButtonID1.getId())
                	{
                		ringerMode = 2;
                	}
                	else if(vibrateButtonIdChecked.getCheckedRadioButtonId() == vibrateButtonID2.getId())
                	{
                		ringerMode = 3;
                	}
                }
                System.out.println(ringerMode);
                if(extras !=null)
            	{
            		latitude = extras.getFloat("LAT");
            		longitude = extras.getFloat("LNG");
               	}

            	ContentValues values = new ContentValues();
        		values.put(RMSProvider.KEY_PLACE_LAT, latitude);
        		values.put(RMSProvider.KEY_PLACE_LNG, longitude);
        		values.put(RMSProvider.KEY_RINGER_MODE, ringerMode);
        		values.put(RMSProvider.KEY_MODE_NAME, modeName);    
        		
        		getContentResolver().insert(RMSProvider.CONTENT_URI, values);
        		finish();
            }
        });
    }
}
