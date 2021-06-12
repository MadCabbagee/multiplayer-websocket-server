package me.madcabbage.mpwebsocketserver;

import java.util.*;

public class Lobby {
    // Code
//    private Map<String, Room> rooms;
    //private static final Dictionary<String, Map<String, Room>> lobbies = new Hashtable<>();
    private static final Map<String, Map<String, Room>> lobbies = new HashMap<>();
    private static final Random rnd = new Random();

    public Lobby() {

    }

    public String createRoom(String game) {
        var code = generateCode();

/*        var gameRoom = lobbies.get(game);
        if (gameRoom.containsKey(code)) {
            Room newRoom = new Room(code);
            gameRoom.put(code, newRoom);
        } else {
            while (gameRoom.containsKey(code)) {
                code = generateCode();
            }
            Room newRoom = new Room(code);
            gameRoom.put(code, newRoom);
        }*/

        if (lobbies.containsKey(game)) {
            Map<String, Room> gameRoom = lobbies.get(game);

            if (! gameRoom.containsKey(code)) {
                var newRoom = new Room(code);
                gameRoom.put(code, newRoom);

            } else {
                while (gameRoom.containsKey(code)) {
                    code = generateCode();
                }
                var newRoom = new Room(code);
                gameRoom.put(code, newRoom);
            }

        } else {
            Map<String, Room> gameRooms = new HashMap<>();
            var newRoom = new Room(code);
            gameRooms.put(code, newRoom);
            lobbies.put("game", gameRooms);
        }

        return code;
    }

    public String generateCode() {
        // generate a 6 character/digit room code
        final var dict = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final var code = new StringBuilder();
        var length = 4;

        for (var i = 0; i < length; i++) {
            // append a random character from dictionary to the code string
            code.append(dict.charAt(rnd.nextInt(dict.length())));
        }

        return code.toString();
    }

    public static Map<String, Map<String, Room>> getLobbies() {
        return lobbies;
    }

    public boolean addPlayer(String game, String roomCode, Player player) {
        var room = lobbies.get(game).get(roomCode);
        if (room != null) {
            room.join(player);
            return true;

        } else return false;
    }

    public int getPlayerCount(String game, String roomCode) {

        return lobbies.get(game).get(roomCode).getPlayerCount();
    }
}
