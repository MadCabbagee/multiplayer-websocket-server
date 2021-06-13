package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;

public class AbstractViewer {

    private final WebSocket connection;
    private Room currentRoom;
    private String currentGame;


    public AbstractViewer(WebSocket connection) {
        this.connection = connection;
    }

    public WebSocket getConnection() {
        return connection;
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
}
