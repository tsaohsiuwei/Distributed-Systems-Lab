import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;


public class ClientSenderThread implements Runnable {

    private NSocket nSocket = null;
    private BlockingQueue<MPacket> eventQueue = null;
    private int clientSequenceNumber; 
	
    
    public ClientSenderThread(NSocket nSocket,
                              BlockingQueue eventQueue){
        this.nSocket = nSocket;
        this.eventQueue = eventQueue;
        this.clientSequenceNumber = 0; 
	
		if(Debug.debug) System.out.println("Construct a client sender Thread");
    }
    
    public void run() {

		try{
			if(Debug.debug) System.out.println("Running a Client Sender Thread");
		    
		    MPacket toServer = null;
		    
		    while(true){
		                        
		            //Take packet from queue
		            toServer = (MPacket)eventQueue.take(); 
		            if(Debug.debug) System.out.println("Client Sends " + toServer);
		            toServer.clientsequenceNumber = this.clientSequenceNumber;
		            this.clientSequenceNumber++;
		            nSocket.writeObject(toServer); 
		    }
        }catch(InterruptedException e){
            e.printStackTrace();
            Thread.currentThread().interrupt(); 
        }
            
    }
}
