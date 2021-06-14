package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Room {

    private final String code;
    private final List<Player> players;
    private final List<Spectator> spectators;
    private final List<JSONObject> cachedResponses;
    private int playerCount;

    public Room(String roomCode) {
        this.code = roomCode;
        players = new ArrayList<>();
        spectators = new ArrayList<>();
        playerCount = 0;
        cachedResponses = new ArrayList<>();
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

    public void broadcast(String message, WebSocket... exclusion) {
        if (players.isEmpty()) return;
        for (Player p : players) {
            var connection = p.getConnection();
            for (WebSocket ex : exclusion) {
                if (!connection.equals(ex)) {
                    connection.send(message);
                }
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
        for (Player p : players) {
            connections.add(p.getConnection());
        }
        return connections;
    }

    public synchronized int getPlayerCount() {
        return playerCount;
    }

    public void cacheResponse(JSONObject response) {
        cachedResponses.add(response);
    }

    public List<JSONObject> getCachedJoinResponses() {
        return cachedResponses;
    }

    public List<AbstractViewer> getViewers() {
        return Stream.concat(players.stream(), spectators.stream()).collect(Collectors.toList());
    }
}
