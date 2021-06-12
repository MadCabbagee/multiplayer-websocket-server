package me.madcabbage.mpwebsocketserver;

import org.apache.commons.cli.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLOutput;
import java.util.Scanner;

public class TestClient extends WebSocketClient {

    static boolean run = true;

    public TestClient(URI serverUri) {
        super(serverUri);
    }

    public TestClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("Hello Server.");
        System.out.println("A new connection was successfully established.");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Message Received: " +
                "\n\tMessage: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed." +
                "\n\tExit code: '" + code + "'." +
                "\n\tReason: '" + reason + "'.");
        run = false;
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("An error was encountered: " + ex.getMessage());
        ex.printStackTrace();
    }

    public static void cmain(String[] args) {
        try (Scanner cin = new Scanner(System.in)) {

            CommandLine cmdLine = parseArgs(args);
            String host = cmdLine.getOptionValue("host");
            int port = Integer.parseInt(cmdLine.getOptionValue("port"));

            WebSocketClient client = new TestClient(new URI(String.format("ws://%s:%s", host, port)));
            client.connect();

            // Giving server time to respond before printing anything else;
            Thread.sleep(1500);

            TestClient.run = true;
            while (run) {
                System.out.println("Enter a message to send to the server: ");
                client.send(cin.nextLine());
            }

        } catch (URISyntaxException | IllegalArgumentException | WebsocketNotConnectedException | InterruptedException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static CommandLine parseArgs(String[] args) {
        Options options = new Options();

        Option host = new Option("h", "host", true, "Define the host to connect to.");
        host.setRequired(true);
        options.addOption(host);

        Option port = new Option("p", "port", true, "Define the target port for the defined host.");
        port.setRequired(true);
        options.addOption(port);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Test WebSocket Client", options);

            System.exit(1);
        }

        return cmd;
    }
}
