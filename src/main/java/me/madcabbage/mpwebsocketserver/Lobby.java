package me.madcabbage.mpwebsocketserver;

import java.util.*;

public class Lobby {
    // Code
//    private Map<String, Room> rooms;
    private static final Map<String, Map<String, Room>> lobbies = new HashMap<>();

    public static String createRoom(String game) {
        String code = generateCode();

        if (lobbies.containsKey(game)) {
            Map<String, Room> gameRoom = lobbies.get(game);
            if (! gameRoom.containsKey(code)) {
                Room newRoom = new Room(code);
                gameRoom.put(code, newRoom);
            } else {
                while (gameRoom.containsKey(code)) {
                    code = generateCode();
                }
                Room newRoom = new Room(code);
                gameRoom.put(code, newRoom);
            }
        } else {
            Map<String, Room> gameRooms = new HashMap<>();
            Room newRoom = new Room(code);
            gameRooms.put(code, newRoom);
            lobbies.put("game", gameRooms);
        }

        return code;
    }

    public static String generateCode() {
        // generate a 6 character/digit room code
        final String dict = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        StringBuilder code = new StringBuilder();
        Random rnd = new Random();

        int length = 6;

        for (int i = 0; i < length; i++) {
            // append a random character from dictionary to the code string
            code.append(dict.charAt(rnd.nextInt(dict.length())));
        }

        return code.toString();
    }

    public static Map<String, Map<String, Room>> getLobbies() {
        return lobbies;
    }
}
