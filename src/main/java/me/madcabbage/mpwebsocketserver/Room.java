package me.madcabbage.mpwebsocketserver;

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
        //return playerCount;
    }

    public void leave(Player player) {
        players.remove(player);
        playerCount--;
    }

    public String getCode() {
        return code;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public synchronized int getPlayerCount() {
        return playerCount;
    }
}
