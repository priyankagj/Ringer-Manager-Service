package edu.ncsu.soc.rms;

public class RMSLocation {

  String ringerMode;
  int lat; // micro-degrees
  int lng; // micro-degrees

  public RMSLocation(String _alert, int _lat, int _lng) {
    ringerMode = _alert;
    lat = _lat;
    lng = _lng;
  }

  public String getAlert() {
    return ringerMode;
  }
  

  public void setAlert(String alert) {
    this.ringerMode = alert;
  }

  public int getLat() {
    return lat;
  }

  public void setLat(int lat) {
    this.lat = lat;
  }

  public int getLng() {
    return lng;
  }

  public void setLng(int lng) {
    this.lng = lng;
  }

  @Override
  public String toString() {
    return "(" + lat + ", " + lng + "): " + ringerMode;
  }
}