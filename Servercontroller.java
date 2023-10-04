import java.io.*;
import java.net.*;
import java.util.*;

public class Servercontroller {
    private static final int CONTROLLER_PORT = 12346; // Choose a different port
    private static final Set<PrintWriter> serverWriters = new HashSet<>();
    private static BufferedReader controllerInput;

    public static void main(String[] args) {
        try (ServerSocket controllerSocket = new ServerSocket(CONTROLLER_PORT)) {
            System.out.println("ServerController is running on port " + CONTROLLER_PORT);

            controllerInput = new BufferedReader(new InputStreamReader(System.in));
            Thread controllerInputThread = new Thread(() -> {
                try {
                    while (true) {
                        String controllerMessage = controllerInput.readLine();
                        if (controllerMessage != null) {
                            broadcastMessage(controllerMessage);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            controllerInputThread.start();

            while (true) {
                new ServerControllerHandler(controllerSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ServerControllerHandler extends Thread {
        private Socket controllerSocket;
        private PrintWriter out;

        public ServerControllerHandler(Socket socket) {
            this.controllerSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(controllerSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(controllerSocket.getInputStream()));

                synchronized (serverWriters) {
                    serverWriters.add(out);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Server: " + message);
                    broadcastMessage( message + "  recieved by the Server Controller");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                    controllerSocket.close();
                    synchronized (serverWriters) {
                        serverWriters.remove(out);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void broadcastMessage(String message) {
        synchronized (serverWriters) {
            for (PrintWriter writer : serverWriters) {
                writer.println(message);
            }
        }
    }
}
