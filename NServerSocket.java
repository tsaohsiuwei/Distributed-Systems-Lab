import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

public class NServerSocket{
    /*
    * This is the serverSocket equivalent to 
    * MSocket
    */
 
    private ServerSocket serverSocket = null;
    
    /*
     *This creates a server socket
     */    
    public NServerSocket(int port) throws IOException{
        serverSocket = new ServerSocket(port);
    }
    
    public NSocket accept() throws IOException{
        Socket socket = serverSocket.accept(); 
        NSocket nSocket = new NSocket(socket);
        return nSocket;
    }

}
