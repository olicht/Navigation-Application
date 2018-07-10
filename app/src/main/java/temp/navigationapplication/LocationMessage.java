package temp.navigationapplication;

import java.util.Date;

// a class which represent a location message
class LocationMessage {
    private String messageUid;
    private long messageTime;
    public double currentLatitude;
    public double currentLongitude;

    public LocationMessage(String messageUid, long messageTime, double currentLatitude, double currentLongitude) {
        this.messageUid = messageUid;
        this.messageTime = messageTime;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
    }

    public LocationMessage(String messageUid, double currentLatitude, double currentLongitude) {
        this.messageUid = messageUid;
        this.messageTime = messageTime = new Date().getTime();
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
    }

    //default c'tor
    public LocationMessage() {
    }

    public String getmessageUid() {
        return messageUid;
    }

    public void setmessageUid(String messageUid) {
        this.messageUid = messageUid;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }
}
