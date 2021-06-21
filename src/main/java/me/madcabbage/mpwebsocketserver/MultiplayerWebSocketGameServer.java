package me.madcabbage.mpwebsocketserver;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



class Keys {
    private Keys() {
    }

    public static final String Request = "request";
    public static final String Game = "game";
    public static final String RoomCode = "roomcode";
    public static final String Player = "player";
    public static final String Username = "username";
    public static final String Message = "message";
    public static final String Success = "success";
    public static final String Payload = "payload";
}

class Requests {
    private Requests() {
    }

    //Handled by Game
    public static final String Create = "create";
    public static final String Join = "join";
    public static final String Leave = "leave";  //Leaving a room

    //Can sort the rest after I read all this.
    public static final String Start = "start";
    public static final String Starting = "starting";
    public static final String Relay = "relay";
    public static final String End = "end";
    public static final String Joined = "joined";
    public static final String Error = "error";
    public static final String Spectate = "view";
    public static final String Ready = "ready";
    public static final String UnReady = "unready";
}

class Games {

    private Games() {
    }

    public static final String DefaultCode = "default";
    public static final String Chaos = "chaos";
    public static final String Pool = "pool";
    public static final String GinRummy = "ginrummy";
}

class Messages {

    private Messages() {
    }

    public static final String ConnectionAccepted = "Connection Accepted";
    public static final String RemoteAddress = "\tRemote Address: ";
    public static final String ConnectionClosed = "Connection Closed.";
    public static final String Code = "\tCode: ";
    public static final String Reason = "\tReason: ";
    public static final String Remote = "\tRemote: ";
    public static final String MessageReceived = "Message Received:";
    public static final String Message = "\tMessage: ";
    public static final String Quit = "quit";
    public static final String ErrorEncountered = "Error Encountered";
}

public class MultiplayerWebSocketGameServer extends WebSocketServer {

    private static final JSONParser parser = new JSONParser();
    public final boolean debugEnabled;
    //No lobby needed

    private Map<String, Game> games = new HashMap<String, Game>();

    public MultiplayerWebSocketGameServer(InetSocketAddress address) {
        super(address);
        this.debugEnabled = false;
    }

    public MultiplayerWebSocketGameServer(InetSocketAddress address, boolean debug) {
        super(address);
        this.debugEnabled = debug;
        //this.lobby = new Lobby();
    }

    public static void main(String[] args) {
        WebSocketServer server = new MultiplayerWebSocketGameServer(new InetSocketAddress("0.0.0.0", 82), true);
        server.run();
    }

    public void sendError(WebSocket conn, String msg) {
        JSONObject response = new JSONObject();
        //Make it look like below, should be fine.
        response.put(Keys.Request, Requests.Error);
        response.put(Keys.Message, msg);
        conn.send(response.toJSONString());
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // DEBUGGING
        if (debugEnabled) {
            System.out.println(Messages.ConnectionAccepted);
            System.out.println(Messages.RemoteAddress + conn.getRemoteSocketAddress());
            System.out.println(); // For spacing
        }

        // check if person connecting is apart of an existing game then reconnect them - on the other hand make sure player data is not kept when another round is started

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // DEBUGGING
        if (debugEnabled) {
            System.out.println(Messages.ConnectionClosed);
            System.out.println(Messages.RemoteAddress + conn.getRemoteSocketAddress());
            System.out.println(Messages.Code + code);
            System.out.println(Messages.Reason + reason);
            System.out.println(Messages.Remote + remote);
            System.out.println(); // For spacing
        }
    }

    // Todo: Implement JsonWebtokens and add them to the responses to validate? Maybe client can generate some sort of unique token that would be hard to figure out how its generated. and send to server and server process it to validate each request?
    @Override
    public void onMessage(WebSocket conn, String message) {
        // DEBUGGING
        if (debugEnabled) {
            System.out.println(Messages.MessageReceived);
            System.out.println(Messages.RemoteAddress + conn.getRemoteSocketAddress());
            System.out.println(Messages.Message + message);
            System.out.println(); // For spacing

            // Ignore this, this message will only be used by the testing client.
            if (message.equalsIgnoreCase(Messages.Quit)) {
                System.out.println("Quit message received, closing connection");
                conn.closeConnection(1, "Connection closed by the user.");
            }
        }

        try {
            JSONObject request = (JSONObject) parser.parse(message);

            String reqType = (String) request.get(Keys.Request);
            String game = (String) request.get(Keys.Game);

            //This will always have a valid value, even if "ERROR"
            if (!games.containsKey(game)) {
                Game insertGame = new Game(game);
                games.put(game,insertGame);
                //ames.get(game) = new Game(game);
            }
            try {
                games.get(game).OnRequest(conn, request);
            } catch (Exception e) {
                //This is later for fallback when something bad happens.
            }

/*
                case Requests.Start:
                    // Broadcast to all players that the game is starting.
                    Player player = conn.getAttachment(); // todo: add readyState and id to player class and attach player object
                    player.setReadyState(true);

                    code = (String) request.get(Keys.RoomCode);
                    var room = lobby.getRoom(game, code);
                    if (room.isReady()) {
                        JSONObject startingResponse = new JSONObject();
                        startingResponse.put(Keys.Request, Requests.Starting);
                        startingResponse.put(Keys.Game, Games.Chaos);
                        startingResponse.put(Keys.RoomCode, code);
                        startingResponse.put(Keys.Player, player.getID());
                        startingResponse.put(Keys.Username, player.getUsername());
                        room.broadcast(startingResponse.toJSONString());
                    } else {
                        room.broadcast(message); // todo: When we get this part of the client working, make sure that the fields in the json are proper to send to all connections, if not change it.
                    }
                    break;

                case Requests.End:
                    // end the current game. Wait for another round to start, or delete room if everyone leaves.
                    lobby.getRoom(game, (String) request.get(Keys.RoomCode)).broadcast(message); //todo same as ln 159
                    break;

                case Requests.Relay:
                    // broadcast to all players but the sender. NOTE: client will have to do the checking of what type of relay it is.
                    lobby.getRoom(game, (String) request.get(Keys.RoomCode)).broadcast(message, conn);
                    break;
                case Requests.Spectate:
                    // add viewer to lobby
                    // req: view, game: chaos, roomcode: code,
                    // refactor to check for spectator in username. (client has checkbox that sets username to spectator or something)
                    break;
                case Requests.Ready:
                    // letting the other games know who is ready to start, once all click Ready button, it turns green and says waiting,
                    // if its clicked again it unreadies. Once all are ready, the start button will appear.
                    // note refactor to using more specific messages like this later.

                    break;
                case Requests.UnReady:

                    break;
            }*/
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // DEBUGGING
        if (debugEnabled) {
            System.out.println(Messages.ErrorEncountered);
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
