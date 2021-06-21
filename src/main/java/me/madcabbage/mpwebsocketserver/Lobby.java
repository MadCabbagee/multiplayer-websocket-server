package me.madcabbage.mpwebsocketserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

//A lobby stores a map of game name to a Room class.
/*
public class Lobby {
    private static Map<String, Room>  lobbies = new HashMap<String, Room>();
    private static final Random rnd = new Random();

    public Lobby() {
    }

    public static Map<String, Room> getLobbies() {
        return lobbies;
    }
    //Make a new room with a unique ID in the appropriate lobby (game)
    //Return the room code
    public String createRoom(String game) {
        Boolean found = false;

        //Let's see if that game exists
        if (!lobbies.containsKey((game))) {
            lobbies.put(game,new Room(game));
        }

        Lobby ourLobby = lobbies.get(game);



        String gameCode = generateCode();

        while (!found) {

        }
        String code = generateCode();


        if (lobbies.containsKey(game)) {
            Map<String, Room> gameRoom = lobbies.getOrDefault(game, defaultRooms);

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
*/