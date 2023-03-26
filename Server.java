import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private boolean isClient;

    public static void main(String[] args) {
        try {
            Server server = new Server(5000, false);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(int port, boolean client) throws IOException {
        serverSocket = new ServerSocket(port);
        isClient = client;
    }

    public void serverMessage(String message) {
        if (isClient) return;
        System.out.println("Server: " + message);
    }

    public void startServer() throws IOException {
        System.out.println("Server is running on port: " + serverSocket.getLocalPort());
        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            ServerClientHandler clientHandler = new ServerClientHandler(socket, isClient);
            serverMessage("User has connected " + clientHandler.username + ":" + socket.getInetAddress().getHostAddress());
            new Thread(clientHandler).start();
        }
    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
