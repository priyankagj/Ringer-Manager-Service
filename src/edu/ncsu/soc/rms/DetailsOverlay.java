package edu.ncsu.soc.rms;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class DetailsOverlay extends Overlay {
  
Context context;

  public DetailsOverlay(Context _context) {
    this.context = _context;
  }

  /** Get the position location */
  public Location getLocation() {
    return location;
  }

  /** Set the position location */
  public void setLocation(Location location) {
    this.location = location;
  }

  Location location;

  private final int mRadius = 5;

  @Override
  public void draw(Canvas canvas, MapView mapView, boolean shadow) {
    Projection projection = mapView.getProjection();
    
    if (location == null)
      return;

    if (shadow == false) {
      // Get the current location
      Double latitude = location.getLatitude() * 1E6;
      Double longitude = location.getLongitude() * 1E6;
      GeoPoint geoPoint = new GeoPoint(latitude.intValue(), longitude.intValue());

      // Convert the location to screen pixels
      Point point = new Point();
      projection.toPixels(geoPoint, point);

      RectF oval = new RectF(point.x - mRadius, point.y - mRadius, point.x + mRadius, point.y
          + mRadius);

      // Setup the paint
      Paint paint = new Paint();
      paint.setARGB(255, 255, 255, 255);
      paint.setAntiAlias(true);
      paint.setFakeBoldText(true);

      Paint backPaint = new Paint();
      backPaint.setARGB(180, 50, 50, 50);
      backPaint.setAntiAlias(true);

      RectF backRect = new RectF(point.x + 2 + mRadius, point.y - 3 * mRadius, point.x + 65,
          point.y + mRadius);

      // Draw the marker
      canvas.drawOval(oval, paint);
      canvas.drawRoundRect(backRect, 5, 5, backPaint);
      canvas.drawText("Here I Am", point.x + 2 * mRadius, point.y, paint);
      
      String[] colsArray = new String[] {RMSProvider.KEY_PLACE_LAT, RMSProvider.KEY_PLACE_LNG, RMSProvider.KEY_RINGER_MODE, RMSProvider.KEY_MODE_NAME};
      Uri rmsDB = RMSProvider.CONTENT_URI;
      Cursor ringerCur = context.getContentResolver().query(rmsDB, colsArray, null, null, null);
      
      if (ringerCur.moveToFirst()) {
      	int latColIndex = ringerCur.getColumnIndex(RMSProvider.KEY_PLACE_LAT);
      	int lngColIndex = ringerCur.getColumnIndex(RMSProvider.KEY_PLACE_LNG);
      	int modeNameColIndex = ringerCur.getColumnIndex(RMSProvider.KEY_MODE_NAME);
      	      	
      	double latit;
      	double longit;
      	String modeName;
      	
      	do{
      		latit = ringerCur.getDouble(latColIndex);
      		longit = ringerCur.getDouble(lngColIndex);
      		modeName =ringerCur.getString(modeNameColIndex);      	
      		
      		geoPoint = new GeoPoint((int) (latit), (int) (longit));
	      	point = new Point();
	      	projection.toPixels(geoPoint, point);

	         oval = new RectF(point.x - mRadius, point.y - mRadius, point.x + mRadius, point.y
	             + mRadius);

	         // Setup the paint
	         paint = new Paint();
	         paint.setARGB(255, 255, 255, 255);
	         paint.setAntiAlias(true);
	         paint.setFakeBoldText(true);

	         backPaint = new Paint();
	         backPaint.setARGB(180, 50, 50, 50);
	         backPaint.setAntiAlias(true);

	         backRect = new RectF(point.x + 2 + mRadius, point.y - 3 * mRadius, point.x + 65,
	             point.y + mRadius);
	         
	         Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.map_red);
	         canvas.drawBitmap(bmp, point.x - mRadius, point.y - 64, null);
	         
	         // Draw the marker
	         //canvas.drawOval(oval, paint);
	         canvas.drawRoundRect(backRect, 5, 5, backPaint);
	         canvas.drawText(modeName, point.x + 2 * mRadius, point.y, paint);
	      	
      	
      	} while (ringerCur.moveToNext());
     }
      
      
    }
    super.draw(canvas, mapView, false);
  }

  @Override
  public boolean onTap(GeoPoint point, MapView mapView) {
	  
	  final float latitude = point.getLatitudeE6();
	  final float longitude = point.getLongitudeE6();
	  
	  Intent intent = new Intent(context, RMSDetailsActivity.class);
	  intent.putExtra("LAT", latitude);
	  intent.putExtra("LNG", longitude);
	  context.startActivity(intent);
	  return true;
  } 
  }