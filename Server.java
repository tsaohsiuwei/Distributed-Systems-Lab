import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.*;
import java.util.*;
import java.io.*;

/* Lab3 Naming Server */

public class Server {
    
	//The maximum of clients that will join
	//Server waits until the max number of clients to join 
    private static final int MAX_CLIENTS = 4;
    private NServerSocket nServerSocket = null;
	
    private int clientCount; //The number of clients before game starts
    private NSocket[] nSocketList = null; //A list of Sockets
    private BlockingQueue eventQueue = null; //A list of events
    /*
    * Constructor
    */
    public Server(int port) throws IOException{
        clientCount = 0; 
        nServerSocket = new NServerSocket(port);
		
        if(Debug.debug) System.out.println("Listening on port: " + port);
        nSocketList = new NSocket[MAX_CLIENTS];
        eventQueue = new LinkedBlockingQueue<MPacket>();
       
    }
    
    /*
    *Starts the listener and sender threads 
    */
    public void startThreads() throws IOException{
        //Listen for new clients
    	
        while(clientCount < MAX_CLIENTS){
            System.out.println("client count isssssssssssssssssssssssssssssssssssss: " + clientCount);
            //Start a new listener thread for each new client connection	
			NSocket nSocket = nServerSocket.accept();  
			new Thread(new ServerListenerThread(nSocket, eventQueue)).start();
        	nSocketList[clientCount] = nSocket;   								
            clientCount++;
        }
        System.out.println("client count isssssssssssssssssssssssssssssssssssss: " + clientCount);
        try{Thread.sleep(2000);}catch(InterruptedException e){e.printStackTrace();}
        //Start a new sender thread 
        new Thread(new ServerSenderThread(nSocketList, eventQueue)).start();    
    }

        
    /*
    * Entry point for server
    */
    public static void main(String args[]) throws IOException {
        if(Debug.debug) System.out.println("Starting the server");
        int port = Integer.parseInt(args[0]);
        Server server = new Server(port);
                
        server.startThreads();    

    }
}
