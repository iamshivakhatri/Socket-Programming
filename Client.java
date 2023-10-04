import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in)))
        {

            System.out.println("Connected to the server. Type 'exit' to quit.");

            Thread readerThread = new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            readerThread.start();

            String userInput;
            while ((userInput = stdin.readLine()) != null) {
                out.println(userInput);
                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }
            }

            readerThread.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
