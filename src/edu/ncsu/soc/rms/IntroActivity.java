package edu.ncsu.soc.rms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

public class IntroActivity extends Activity {
	public static int setting=1;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.intro);
		    
        Button startRMS = (Button) findViewById(R.id.StartRMS);
        startRMS.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
            	Intent intent = new Intent(v.getContext() ,RingerManagerActivity.class);
                startActivity(intent);      
            }
        });
        
        Button settings = (Button) findViewById(R.id.Settings);
        settings.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
				// TODO Auto-generated method stub
        		final FrameLayout fl = new FrameLayout(v.getContext());        		
        		final RadioGroup input = new RadioGroup(v.getContext());
        	    input.setGravity(Gravity.CENTER);
        	        	    
        	    final CharSequence[] items = {"SMS", "Email"};        	    
        	    
        	    fl.addView(input, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
        	        FrameLayout.LayoutParams.WRAP_CONTENT));
                
                new AlertDialog.Builder(v.getContext())
	            .setView(fl)
	            .setTitle("Ringer Manager Settings")
	            .setSingleChoiceItems(items, setting , new DialogInterface.OnClickListener() {
	            		public void onClick(DialogInterface dialog, int item) {
	            			ListView lv = ((AlertDialog)dialog).getListView();
	                        lv.setTag(new Integer(item));	            			
	            			Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
	            		}
	            })
	            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface d, int which) {
	            	  ListView lv = ((AlertDialog)d).getListView();
	                  Integer selected = (Integer)lv.getTag();
	                  if(selected != null) {
	                	  setting = selected;
	                	  System.out.println(setting);
	                  }
	                d.dismiss();
	                Toast.makeText(getApplicationContext(),"Settings Saved", Toast.LENGTH_LONG)
	                        .show();
	              }
	            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	              public void onClick(DialogInterface d, int which) {
	                d.dismiss();
	              }
	            }).create().show();
 			}
		});
     }	
}
