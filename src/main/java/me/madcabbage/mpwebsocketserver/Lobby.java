package me.madcabbage.mpwebsocketserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Lobby {
    // Code
//    private Map<String, Room> rooms;
    //private static final Dictionary<String, Map<String, Room>> lobbies = new Hashtable<>();
    private static final Map<String, Map<String, Room>> lobbies = new HashMap<>();
    private static final Random rnd = new Random();
    private final Map<String, Room> defaultRooms = new HashMap<>();


    public Lobby() {
        defaultRooms.put("default", new Room("chaos"));
    }

    public static Map<String, Map<String, Room>> getLobbies() {
        return lobbies;
    }

    public String createRoom(String game) {
        String code = generateCode();
        //Testing 1234
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
        var asdf = "asdf";
        if (lobbies.containsKey(game)) {
            Map<String, Room> gameRoom = lobbies.get(game);

            if (!gameRoom.containsKey(code)) {
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
            lobbies.put(game, gameRooms);
        }

        return code;
    }

    public String generateCode() {
        // generate a 6 character/digit room code
        final var dict = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final var code = new StringBuilder();
        final var length = 4;

        for (var i = 0; i < length; i++) {
            // append a random character from dictionary to the code string
            code.append(dict.charAt(rnd.nextInt(dict.length())));
        }

        return code.toString();
    }

    public boolean addPlayer(String game, String roomCode, Player player) {
        var room = lobbies.get(game).get(roomCode);
        if (room != null) {
            room.join(player);
            return true;

        } else return false;
    }

    public int getPlayerCount(String game, String roomCode) {
        var debug = false;
        if (debug) {
            var test = lobbies.get(game);
            if (test != null) {
                Room test2 = test.get(roomCode);
                if (test2 != null) {
                    int test3 = test2.getPlayerCount();
                }
            }
        }

        return lobbies.getOrDefault(game, defaultRooms).get(roomCode).getPlayerCount();
    }

    public Room getRoom(String game, String roomCode) {
        return lobbies.getOrDefault(game, defaultRooms).get(roomCode);
    }

    public void deleteRoom(String game, String roomCode) {
        if (lobbies.containsKey(game)) {
            var rooms = lobbies.getOrDefault(game, defaultRooms);

            rooms.remove(roomCode);

        }
    }
}
