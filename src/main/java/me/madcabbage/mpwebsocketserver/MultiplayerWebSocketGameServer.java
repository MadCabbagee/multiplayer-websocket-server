package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.InetSocketAddress;
import java.util.ArrayList;
public class MultiplayerWebSocketGameServer extends WebSocketServer {

    private static final JSONParser parser = new JSONParser();
    public final boolean debugEnabled;
    private final Lobby lobby;

    public MultiplayerWebSocketGameServer(InetSocketAddress address) {
        super(address);
        this.debugEnabled = false;
        this.lobby = new Lobby();
    }

    public MultiplayerWebSocketGameServer(InetSocketAddress address, boolean debug) {
        super(address);
        this.debugEnabled = debug;
        this.lobby = new Lobby();
    }

    public static void main(String[] args) {
        WebSocketServer server = new MultiplayerWebSocketGameServer(new InetSocketAddress("localhost", 82), true);
        server.run();
    }

    public void sendError(WebSocket conn, String msg) {
        JSONObject response = new JSONObject();
        //Make it look like below, should be fine.
        response.put("request", "error");
        response.put("message", msg);
        conn.send(response.toJSONString());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // DEBUGGING
        if (debugEnabled) {
            System.out.println("Connection Accepted");
            System.out.println("\tRemote Address: " + conn.getRemoteSocketAddress());
            System.out.println(); // For spacing
        }

        // check if person connecting is apart of an existing game then reconnect them - on the other hand make sure player data is not kept when another round is started

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // DEBUGGING
        if (debugEnabled) {
            System.out.println("Connection Closed.");
            System.out.println("\tRemote Address: " + conn.getRemoteSocketAddress());
            System.out.println("\tCode: " + code);
            System.out.println("\tReason: " + reason);
            System.out.println("\tRemote: " + remote);
            System.out.println(); // For spacing
        }
    }

    // Todo: Implement JsonWebtokens and add them to the responses to validate? Maybe client can generate some sort of unique token that would be hard to figure out how its generated. and send to server and server process it to validate each request?
    @Override
    public void onMessage(WebSocket conn, String message) {
        // DEBUGGING
        if (debugEnabled) {
            System.out.println("Message Received:");
            System.out.println("\tRemote Address: " + conn.getRemoteSocketAddress());
            System.out.println("\tMessage: " + message);
            System.out.println(); // For spacing

            if (message.equalsIgnoreCase("q")) {
                conn.closeConnection(1, "Connection closed by the user.");
            }
        }

        try {
            JSONObject request = (JSONObject) parser.parse(message);

            String reqType = (String) request.get("request");
            String game = (String) request.get("game");

            switch (reqType.toLowerCase()) {
                case "create":
                    // Create a new room, give it the creator, send back the roomcode
                    String code = lobby.createRoom(game);
                    request.put("roomcode", code);
                    conn.send(request.toJSONString());
                    System.out.println(request.toJSONString());
                    break;

                case "join":
                    String username = (String) request.get("username");
                    var roomCode = (String) request.get("roomcode");
                    boolean success = lobby.addPlayer(game, roomCode, new Player(conn, username));
                    boolean debug = false;
                    if (debug) {

                        if (username == null) {
                            sendError(conn, "No usernane in message, send the username next time");
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
                    }
                    // also used as player id
                    int playerCount = lobby.getPlayerCount(game, roomCode);
                    var joinedRoom = lobby.getRoom(game, roomCode);

                    var response = new JSONObject();
                    response.put("request", "joined");
                    response.put("game", game);
                    response.put("roomcode", roomCode);
                    response.put("player", playerCount);
                    response.put("success", success);


                    // DEBUGGING - print response
                    if (debugEnabled) {
                        System.out.println(response.toJSONString());
                    }
                    if (success) {
                        conn.setAttachment(false);
                        // broadcast if not the only player in the room
                        if (playerCount < 2) {
                            conn.send(response.toJSONString());
                        } else {
                            // broadcast to all that someone joined. (including current connection)
                            joinedRoom.broadcast(response.toJSONString());
                            // get cached responses for this room so we can tell current connection about the other players that have already joined
                            var cachedResponses = (ArrayList<JSONObject>) joinedRoom.getCachedJoinResponses();
                            // tell current connection about each player that is in the room.
                            for (JSONObject cached : cachedResponses) {
                                conn.send(cached.toJSONString());
                            }
                        }
                        // cache the join response
                        joinedRoom.cacheResponse(response);
                    }
                    break;

                case "start":
                    // Broadcast to all players that the game is starting.
                    code = (String) request.get("roomCode");
                    lobby.getRoom(game, code).broadcast(message); // todo: When we get this part of the client working, make sure that the fields in the json are proper to send to all connections, if not change it.
                    break;

                case "end":
                    // end the current game. Wait for another round to start, or delete room if everyone leaves.
                    code = (String) request.get("roomCode");
                    lobby.getRoom(game, code).broadcast(message, conn); //todo same as ln 159
                    break;

                case "relay":
                    // broadcast to all players but the sender. NOTE: client will have to do the checking of what type of relay it is.
                    lobby.getRoom(game, (String) request.get("roomCode")).broadcast(message, conn);
                    break;
                case "view":
                    // add viewer to lobby
                    // req: view, game: chaos, roomcode: code,
                /*case "ready":
                    // letting the other games know who is ready to start, once all click Ready button, it turns green and says waiting,
                    // if its clicked again it unreadies. Once all are ready, the start button will appear.

                    break;*/
                case "unready":

                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // DEBUGGING
        if (debugEnabled) {
            System.out.println("Error Encountered");
            ex.printStackTrace();
            System.out.println(); // For spacing
        }

    }

    @Override
    public void onStart() {
        // DEBUGGING
        if (debugEnabled) {
            InetSocketAddress addr = getAddress();
            System.out.println("Starting Multiplayer WebSocketServer on '" + addr.getHostName() + ":" + addr.getPort() + "'");
            System.out.println(); // For spacing
        }
        setTcpNoDelay(true);
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }
}
