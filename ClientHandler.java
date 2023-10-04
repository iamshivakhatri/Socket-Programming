import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            synchronized (Server.clientWriters) {
                Server.clientWriters.add(out);
            }

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received: " + message);
                Server.broadcastMessage("Client: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                clientSocket.close();
                synchronized (Server.clientWriters) {
                    Server.clientWriters.remove(out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void broadcastMessage(String message) {
        synchronized (Server.clientWriters) {
            for (PrintWriter writer : Server.clientWriters) {
                writer.println(message);
            }
        }
    }
}
