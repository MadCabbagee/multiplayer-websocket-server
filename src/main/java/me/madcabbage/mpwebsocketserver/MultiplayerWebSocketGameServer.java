package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.InetSocketAddress;
import java.util.Arrays;

public class MultiplayerWebSocketGameServer extends WebSocketServer {

    public final boolean debugEnabled;

    private static final JSONParser parser = new JSONParser();;

    public MultiplayerWebSocketGameServer(InetSocketAddress address) {
        super(address);
        this.debugEnabled = false;

    }

    public MultiplayerWebSocketGameServer(InetSocketAddress address, boolean debug) {
        super(address);
        this.debugEnabled = debug;
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
                    String code = Lobby.createRoom(game);
                    request.put("roomcode", code);
                    conn.send(request.toJSONString());
                    System.out.println(request.toJSONString());
                    break;

                case "join":
                    String username = (String) request.get("username");
                    String roomCode = (String) request.get("roomcode");
                    Lobby.addPlayer(game, roomCode, new Player(conn, username));
                    break;

                case "start":

                    break;

                case "end":
                    break;

                case "relay":
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

    public static void main(String[] args) {
        WebSocketServer server = new MultiplayerWebSocketGameServer(new InetSocketAddress("localhost", 82), true);
        server.run();
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }
}
