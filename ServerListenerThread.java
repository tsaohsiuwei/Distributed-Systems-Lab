import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.*;
import java.net.Socket;

public class ServerListenerThread implements Runnable {

    private NSocket nSocket =  null;
    private BlockingQueue eventQueue = null;
    private int ref;
  

    public ServerListenerThread( NSocket nSocket, BlockingQueue eventQueue){
        this.nSocket = nSocket;
        this.eventQueue = eventQueue;
        this.ref = 0;
        
		System.out.println("Construct a server listerner thread");
    }

    public void run() {
		if(Debug.debug) System.out.println("Run a server listener thread");
		try{
		    MPacket received = null;
		      
		    ArrayList<MPacket> tempQueue = new ArrayList<MPacket>(10);
		    while(true){
		        
		            received = (MPacket) nSocket.readObject();
		            System.out.println("[" + System.currentTimeMillis() + "]" + " Server Receives " + received);
		            tempQueue.add(received);
					Iterator<MPacket> itr = tempQueue.iterator();
		            while(itr.hasNext())
		            {
				        MPacket m = itr.next();
				        if(m.clientsequenceNumber == ref){
				          eventQueue.put(m);
					      System.out.println("In, server listener thread, eventqueue now has some shit");
				          itr.remove();
				          ref++;
		    			}
					}
			}

         }catch(InterruptedException e){
             e.printStackTrace();
         }catch(IOException e){
             e.printStackTrace();
         }catch(ClassNotFoundException e){
             e.printStackTrace();
         }
            
     }
    
}
