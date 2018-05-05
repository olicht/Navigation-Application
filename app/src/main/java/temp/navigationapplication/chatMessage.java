package temp.navigationapplication;

import java.util.Date;

public class chatMessage {
    private String mUser;
    private long mTime;
    private String mContent;

    public chatMessage(String mUser, String mContent) {
        this.mUser = mUser;
        this.mContent = mContent;
        //set mTime to the current time
        this.mTime = new Date().getTime();
    }

    //default c'tor
    public chatMessage() {
    }

    public String getmUser() {
        return mUser;
    }

    public void setmUser(String mUser) {
        this.mUser = mUser;
    }

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }
}
