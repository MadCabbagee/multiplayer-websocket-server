package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.InetSocketAddress;

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
                    //String username = (String) request.get("username");
                    String roomCode = (String) request.get("roomcode");
                    //boolean success = lobby.addPlayer(game, roomCode, new Player(conn, username));
                    JSONObject response = new JSONObject();
                    response.put("request", "joined");
                    response.put("game", game);
                    response.put("roomcode", roomCode);
                    response.put("player", lobby.getPlayerCount(game, roomCode));
                    //response.put("success", success);
                    conn.send(response.toJSONString());
                    break;

                case "start":
                    // Broadcast to all players that the game is starting.
                    break;

                case "end":
                    // disconnect all players from the room and remove from the list
                    break;

                case "relay":
                    // broadcast to all players but the sender.
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

    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }
}
