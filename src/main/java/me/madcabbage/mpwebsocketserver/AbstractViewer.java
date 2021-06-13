package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;

public class AbstractViewer {

    private WebSocket connection;
    private Room currentRoom;


    public AbstractViewer(WebSocket connection, Room currentRoom) {
        this.connection = connection;
        this.currentRoom = currentRoom;
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
}
