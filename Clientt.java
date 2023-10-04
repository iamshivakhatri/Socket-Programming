import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Clientt {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9999; //0- 1023 is reserved  1023 to 65535

    public static void main(String[] args) throws  Exception
    {
        Socket s = new Socket(SERVER_ADDRESS, SERVER_PORT);
        String str = "Navin Reddy";

        //Socket will have input and output port
        OutputStreamWriter os = new OutputStreamWriter(s.getOutputStream());
        PrintWriter out = new PrintWriter(os);
        os.write(str);
        os.flush();


    }
}
