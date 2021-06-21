package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;

public class Player extends AbstractViewer {

    private final int ID;
    private String username;
    private boolean readyState;
    private int avatarNo = 0;


    public Player(WebSocket connection, String username, int ID) {
        super(connection);
        this.username = username;
        this.ID = ID;
        readyState = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getID() {
        return ID;
    }

    public void setAvatarNo(int v) {
        avatarNo = v;
    }

    public int getAvatarNo() {
        return avatarNo;
    }

    public boolean isReady() {
        return readyState;
    }

    public void setReadyState(boolean readyState) {
        this.readyState = readyState;
    }
}

