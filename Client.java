import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static Thread server;

    private Socket socketServer;
    private BufferedReader inputServer;
    private BufferedWriter outputServer;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String ip = getInput("Enter server IP: ", scanner);
            String username = getInput("Enter username: ", scanner);
            ip = ip.isEmpty() ? "127.0.0.1" : ip;

            System.out.println("Ip: " + ip + "Username: " + username);

            System.out.println("Connecting to " + ip);
            if (ip.equals("localhost")) {
                server = new Thread(new Server(5000, true));
                server.start();
                System.out.println("Starting Server...");
            }
            System.out.println("Starting client...");
            new Client(new Socket(ip, 5000), username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getInput(String message, Scanner scanner) {
        System.out.print(message);
        return scanner.nextLine();
    }

    public Client(Socket socket, String username) {
        this.socketServer = socket;
        try {
            inputServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            sendToServerMessage(username); // Initializes the connection
            System.out.println("Connected to server");
            listenForServerMessage();
            waitForInput();
        } catch (IOException e) {
            closeEverything(socket, inputServer, outputServer);
        }
    }

    public void sendToServerMessage(String message) throws IOException {
        outputServer.write(message);
        outputServer.newLine();
        outputServer.flush();
    }

    public void waitForInput() throws IOException {
        try (Scanner scanner = new Scanner(System.in)) {
            while (socketServer.isConnected()) {
                sendToServerMessage(scanner.nextLine());
            }
        }
    }

    public void listenForServerMessage() {
        new Thread() {
            public void run() {
                while (socketServer.isConnected()) {
                    try {
                        String message = inputServer.readLine();
                        System.out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void closeEverything(Socket socket, BufferedReader input, BufferedWriter output) {
        try {
            if (socket != null)
                socket.close();
            if (input != null)
                input.close();
            if (output != null)
                output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
