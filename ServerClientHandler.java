import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerClientHandler implements Runnable {
    public static ArrayList<ServerClientHandler> clients = new ArrayList<ServerClientHandler>();

    public String username;
    private Socket socketServer;
    private BufferedReader clientInput;
    private BufferedWriter clientOutput;
    private boolean isClient;

    public ServerClientHandler(Socket socket, boolean isClient) {
        try {
            this.socketServer = socket;
            this.isClient = isClient;
            this.clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientOutput = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = clientInput.readLine();
            clients.add(this);
            broadcastMessage("User " + username + " has joined the chat");
        } catch (IOException e) {
            closeEverything(socketServer, clientInput, clientOutput);
        }
    }

    public void showServerMessage(String message) {
        if (isClient) return;
        System.out.println("Server: " + message);
    }

    @Override
    public void run() {
        while (socketServer.isConnected()) {
            try {
                String message = username + ": " + clientInput.readLine();
                showServerMessage(message);
                broadcastMessage(message);
            } catch (IOException e) {
                closeEverything(socketServer, clientInput, clientOutput);
            }
        }
    }

    public void broadcastMessage(String message) {
        for (ServerClientHandler client : clients) {
            try {
                if (client == this) continue;
                showServerMessage("Sending message to " + client.username);
                client.clientOutput.write(message);
                client.clientOutput.newLine();
                client.clientOutput.flush();
            } catch (IOException e) {
                closeEverything(client.socketServer, client.clientInput, client.clientOutput);
            }
        }
    }

    public void removeClient(ServerClientHandler client) {
        clients.remove(client);
        broadcastMessage("User " + client.username + " has left the chat");
    }

    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        removeClient(this);
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
