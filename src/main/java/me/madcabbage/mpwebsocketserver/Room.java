package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private final String code;
    private final List<Player> players;
    private final List<Spectator> spectators;
    private int playerCount;

    public Room(String roomCode) {
        this.code = roomCode;
        players = new ArrayList<>();
        spectators = new ArrayList<>();
        playerCount = 0;
    }

    public void join(Player joiner) {
        players.add(joiner);
        playerCount++;
    }

    public void leave(Player player) {
        players.remove(player);
        playerCount--;
    }

    public void broadcast(String message) {
        if (players.isEmpty()) return;
        for (Player p : players) {
            p.getConnection().send(message);
        }
    }

    public void broadcast(String message, WebSocket exclusion) {
        if (players.isEmpty()) return;
        for (Player p : players) {
            var connection = p.getConnection();
            if (! connection.equals(exclusion)) {
                connection.send(message);
            }
        }
    }

    public String getCode() {
        return code;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<WebSocket> getConnections() {
        ArrayList<WebSocket> connections = new ArrayList<>();
        if (! players.isEmpty()) {
            for (Player p : players) {
                connections.add(p.getConnection());
            }
        }
        return connections;
    }

    public synchronized int getPlayerCount() {
        return playerCount;
    }
}
