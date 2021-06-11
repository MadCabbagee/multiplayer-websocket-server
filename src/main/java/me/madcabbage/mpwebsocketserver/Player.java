package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;

public class Player {

    private String username;
    private WebSocket connection;
    private Room currentRoom;
    private String currentGame;

    public Player(WebSocket connection, String username) {
        this.connection = connection;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public String getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(String currentGame) {
        this.currentGame = currentGame;
    }

    public WebSocket getConnection() {
        return connection;
    }
}
