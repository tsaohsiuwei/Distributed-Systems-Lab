import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Hashtable;
import java.util.*;
import java.net.*;

public class ClientListenerThread implements Runnable {

    private NSocket nSocket  =  null;
    private Hashtable<String, Client> clientTable = null;
    private int ref;

    public ClientListenerThread(NSocket nSocket,
                                Hashtable<String, Client> clientTable){
        this.nSocket = nSocket;
        this.clientTable = clientTable;
        if(Debug.debug) System.out.println("Construct a Client Listener Thread");
        this.ref = 0;
		
    }

    public void run() {
        
	try{
		if(Debug.debug) System.out.println("Running Client Listener Thread");
        MPacket received = null;
        Client client = null;
        
		ArrayList<MPacket> tempQueue = new ArrayList<MPacket>(1000);
        
		while(true){
                
           System.out.println("In listener");
                received = (MPacket) nSocket.readObject();
                System.out.println("Client Receives " + received);
				tempQueue.add(received);
				Iterator<MPacket> itr = tempQueue.iterator();
      			while(itr.hasNext()) 
				{
         			MPacket m = itr.next();
					InetAddress ip;
					int pp;
					ip = m.IP;
					pp = m.peerPort;
					//System.out.println("IP = " + ip + " peerport is " + pp);
				}
		}

            }catch(IOException e){
                e.printStackTrace();
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }            
  
   }

 }

