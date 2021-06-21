package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.HashMap;
import java.util.Random;
import java.io.*;

//Now this is what we want...........
public class Game {
    private String gameName;
    //each game/room is unique for now.
    //chaos:abcd != rummy:abcd
    private Map<String, Room> rooms = new HashMap<String, Room>();

    public Game(String iName) {
        gameName = iName;
    }

    //Just clears the room, it doesn't disconnect.
    public void Clear() {
        rooms.clear();
    }

    public String generateCode() {
        // generate a 4 character room code
        Random random = new Random();
        final var dict = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final var code = new StringBuilder();
        final var length = 4;

        for (var i = 0; i < length; i++) {
            // append a random character from dictionary to the code string
            code.append(dict.charAt(random.nextInt(dict.length())));
        }
        return code.toString();
    }

    //Make a new room, return roomCode
    public String createRoom() {
        int loops = 0;
        while (loops < 100) {
            String roomCode = generateCode();
            if (!rooms.containsKey((roomCode))) {
                rooms.put(roomCode, new Room(gameName, roomCode));
                return roomCode;
            }
            loops++;
        }
        //We failed, so lets throw them into error room;
        String roomCode = "ERROR";
        if (!rooms.containsKey(roomCode)) {
            rooms.put(roomCode, new Room(gameName,"ERROR"));
        }
        //We aren't going to be watching this other than now and then.
        //Just fail silently, it'll be okay for now.
        //Can toss exception later IF needed.
        return roomCode;
    }

    public void OnRequest(WebSocket conn, JSONObject request) {
        String reqType = (String) request.get(Keys.Request);
        switch (reqType.toLowerCase()) {
            case Requests.Create: //Creating a new room......
                // DEBUGGING xxxc - get rid of the if, make a function that checks it and do:
                // blah.debugOut(string asdf);
/*                if (debugEnabled) {
                    System.out.println("Create request received: " + request.toJSONString());
                }*/

                // Create a new room, give it the creator, send back the roomcode
                String newRoomCode = createRoom();

                request.put(Keys.RoomCode, newRoomCode);
                conn.send(request.toJSONString());

                // DEBUGGING
                /*
                if (debugEnabled) {
                    System.out.println("Generated create response: " + request.toJSONString());
                }*/
                break;

            case Requests.Join: {
                String username = (String) request.get(Keys.Username);
                var roomCode = (String) request.get(Keys.RoomCode);
                int playerCount = 0; //lobby.getPlayerCount(game, roomCode) + 1; // account for this connection cause it hasnt been added yet
                var joiningPlayer = new Player(conn, username, playerCount);
                //boolean success = lobby.addPlayer(game, roomCode, joiningPlayer);
/*
                if (debugEnabled) {

                    if (username == null) {
                        sendError(conn, "No username in message, send the username next time");
                        break;
                    }
                    if (roomCode == null) {
                        sendError(conn, "No roomcode in message, send the username next time");
                        break;
                    }
                    if (!success) {
                        sendError(conn, "Something else is wrong... Add player failed.");
                        break;
                    }

                    System.out.println("");
                }
*/
                var joinedRoom = rooms.get(roomCode);
                var response = new JSONObject();

                response.put(Keys.Request, Requests.Joined);
                response.put(Keys.Game, gameName);
                response.put(Keys.RoomCode, roomCode);
                response.put(Keys.Player, playerCount);


                // DEBUGGING - print response
                /*if (debugEnabled) {
                    System.out.println("Generated join response: " + response.toJSONString());
                }*/
                conn.setAttachment(joiningPlayer);
                // broadcast if not the only player in the room - gee - if it's 1, it only sends to 1
                // xxxc above broadcast to all that someone joined. (including current connection)
                joinedRoom.join(joiningPlayer);
                joinedRoom.broadcast(response.toJSONString());
                // get cached responses for this room so we can tell current connection about the other players that have already joined
                    /* xxxc temp remove
                        var cachedResponses = (ArrayList<JSONObject>) joinedRoom.getCachedJoinResponses();
                            // tell current connection about each player that is in the room.
                        for (JSONObject cached : cachedResponses) {
                            conn.send(cached.toJSONString());
                            }
                        }
                        // cache the join response
                        joinedRoom.cacheResponse(response);
                    }
                    */
                break;
            }
            case Requests.Leave: {
                //xxxc Figure it out....
                break;
            }
            default: {
                //This is where we just send it to the room.
                var roomCode = (String) request.get(Keys.RoomCode);
                var joinedRoom = rooms.get(roomCode);
                joinedRoom.OnRequest(conn, request);
                break;
            }
        }
    }

    public void DisplayStatus() {
        System.out.println("Game: " + gameName + "\n" + "-------------------------------------------\n");
        for (var iRoom : rooms.entrySet()) {
            Room value = iRoom.getValue();
            System.out.println("Code: " + value.getCode());
        }
    }
}
