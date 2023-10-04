import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static final Set<PrintWriter> clientWriters = new HashSet<>();
    private static BufferedReader serverInput;

    private static final String CONTROLLER_ADDRESS = "localhost";
    private static final int CONTROLLER_PORT = 12346; // Use the chosen port





    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            // Connect to ServerController as a client
            try (Socket controllerSocket = new Socket(CONTROLLER_ADDRESS, CONTROLLER_PORT);
                 PrintWriter controllerOut = new PrintWriter(controllerSocket.getOutputStream(), true);
                 BufferedReader controllerIn = new BufferedReader(new InputStreamReader(controllerSocket.getInputStream()));
                 BufferedReader controllerConsoleInput = new BufferedReader(new InputStreamReader(System.in))) {

                // Start a thread for reading input from ServerController
                Thread controllerReaderThread = new Thread(() -> {
                    try {
                        String controllerMessage;
                        while ((controllerMessage = controllerIn.readLine()) != null) {
                            System.out.println(controllerMessage);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                controllerReaderThread.start();

                // Create a thread for server input
                serverInput = new BufferedReader(new InputStreamReader(System.in));
                Thread serverInputThread = new Thread(() -> {
                    try {
                        while (true) {
                            String serverMessage = serverInput.readLine();
                            if (serverMessage != null) {
                                if (serverMessage.startsWith("#")) {
                                    // Send message to ServerController
                                    controllerOut.println(serverMessage.substring(1));

                                } else {
                                    broadcastMessage(serverMessage);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                serverInputThread.start();

                while (true) {
                    new ClientHandler(serverSocket.accept()).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Client: " + message);
                    broadcastMessage(message + "recieved by the client."); // this is also sending message to the client
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                    clientSocket.close();
                    synchronized (clientWriters) {
                        clientWriters.remove(out);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void broadcastMessage(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }


}






/**
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    static final Set<PrintWriter> clientWriters = new HashSet<>();
    private static BufferedReader serverInput;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            // Create a thread for server input
            serverInput = new BufferedReader(new InputStreamReader(System.in));
            Thread serverInputThread = new Thread(() -> {
                try {
                    while (true) {
                        String serverMessage = serverInput.readLine();
                        if (serverMessage != null) {
                            broadcastMessage(serverMessage);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            serverInputThread.start();

            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    broadcastMessage("Client: " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                    clientSocket.close();
                    synchronized (clientWriters) {
                        clientWriters.remove(out);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void broadcastMessage(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
**/