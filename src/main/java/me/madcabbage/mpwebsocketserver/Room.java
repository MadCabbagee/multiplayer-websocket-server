package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.*;

public class Room {

    private String roomCode;
    private String gameCode;
    private final List<Player> players;
    private final List<Spectator> spectators;
    private final List<JSONObject> cachedResponses;
    private int playerCount;
    private Date lastUse;

    public Room(String iGameCode, String iRoomCode) {
        //Set our time.........
        lastUse = new Date();
        gameCode = iGameCode;
        roomCode = iRoomCode;
        players = new ArrayList<>();
        spectators = new ArrayList<>();
        playerCount = 0;
        cachedResponses = new ArrayList<>();
    }

    public void OnRequest(WebSocket conn, JSONObject request) {
        String reqType = (String) request.get(Keys.Request);
        switch (reqType.toLowerCase()) {

        }
    }

    public Date GetLastUse() {
        return lastUse;
    }

    public void join(Player joiner) {
        players.add(joiner);
        playerCount = players.size();
    }

    public void leave(Player player) {
        players.remove(player);
        playerCount = players.size();
    }

    public void broadcast(String message) {
        if (players.isEmpty()) {
            return;
        }
        for (Player p : players) {
            try {
                p.getConnection().send(message);
            } catch (Exception e) {
                //Oops
            }
        }
    }

    public void broadcastEx(String message, WebSocket... exclusion) {
        if (getViewers().isEmpty()) return;
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
        return roomCode;
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

    public boolean isReady() {
        for (Player p : players) {
            // if any player is not ready, return false. Otherwise they are all ready so it returns true.
            if (!p.isReady()) {
                return false;
            }
        }

        return true;
    }
}
