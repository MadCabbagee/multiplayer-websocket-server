package me.madcabbage.mpwebsocketserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Room {

    private String code;
    private List<Player> players;

    public Room(String roomCode) {
        this.code = roomCode;
        players = new ArrayList<>();
    }

    public void join(Player joiner) {
        players.add(joiner);
    }
}
