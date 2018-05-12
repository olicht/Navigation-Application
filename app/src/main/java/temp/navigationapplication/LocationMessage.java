package temp.navigationapplication;

import java.util.Date;

class LocationMessage {
    private String messageUser;
    private long messageTime;
    public double currentLatitude;
    public double currentLongitude;

    public LocationMessage(String messageUser, double currentLatitude, double currentLongitude) {
        this.messageUser = messageUser;
        this.messageTime = messageTime = new Date().getTime();
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
    }

    //default c'tor
    public LocationMessage() {
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
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
