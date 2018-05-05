package temp.navigationapplication;

import java.util.Date;

public class ChatMessage {
    private String messageUser;
    private long messageTime;
    private String messageText;

    public ChatMessage(String MessageUser, String MessageText) {
        this.messageUser = MessageUser;
        this.messageText = MessageText;
        //set MessageTime to the current time
        this.messageTime = new Date().getTime();
    }

    //default c'tor
    public ChatMessage() {
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String MessageUser) {
        this.messageUser = MessageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long MessageTime) {
        this.messageTime = MessageTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String MessageText) {
        this.messageText = MessageText;
    }
}


