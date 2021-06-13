package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;

public class Player extends AbstractViewer {

    private String username;
    private String currentGame;

    public Player(WebSocket connection, String username) {
        super(connection);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(String currentGame) {
        this.currentGame = currentGame;
    }

}
