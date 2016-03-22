import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.*;


public class PeerClientListenerThread implements Runnable {

    private MSocket mSocket =  null;
    //private BlockingQueue eventQueue = null;
    private int ref;
    private int tokenRef;
    private Hashtable<String, Client> clientTable;
  	private Info info = null;
  	private String myName;

    public PeerClientListenerThread(MSocket mSocket, Hashtable<String, Client> clientTable, Info info, String myName){
        this.mSocket = mSocket;
    	this.clientTable = clientTable;
        this.ref = 0;
        this.tokenRef = 0;
        this.info = info;
        this.myName = myName;
		System.out.println("Construct a peer client listerner thread");
    }
    //when token is 0
    public void run() {

    	Client client = null;
		if(Debug.debug) System.out.println("Run a peer client listener thread");
		try{
		    MPacket received = null;
		      
		    while(true){
		        
		        received = (MPacket)mSocket.readObject();
		        
		        //System.out.println("PeerClient Receives " + received);
				
				if (received.type == MPacket.TOKEN) //waiting for token msg
				{
					if(true){//received.tokenNumber == tokenRef
						System.out.println(myName+" received token");
						
						PeerConnectionThread.setToken(1);
						
						MPacket tokenack = new MPacket(myName, MPacket.ACK, MPacket.TOKEN);
						System.out.println(myName+" is about to send token ack");
					
						mSocket.writeObject(tokenack);
						System.out.println("My token number is now: " + PeerConnectionThread.readToken());
						System.out.println(myName+" has sent token ack");
						tokenRef = tokenRef +1;
				   }
				   /*else {
				   	    System.out.println(myName+" has received the same TOKEN AGAIN");
				   	    MPacket tokenack = null;
						tokenack = new MPacket(myName,MPacket.ACK, MPacket.TOKEN);

						mSocket.writeObject(tokenack);
						System.out.println(myName+" has sent token ack AGAIN");
				   }*/

				}
				else if (received.type == MPacket.ACTION) //waiting for action msg
				{
					System.out.println(myName+" received action");
					MPacket actionack = new MPacket(myName,MPacket.ACK, MPacket.ACTION);
					mSocket.writeObject(actionack);
                    System.out.println("PeerClient has sent action ack " + actionack);
					if(ref == received.sequenceNumber)
					{
                    	 client = clientTable.get(received.name);
		            	 if(received.event == MPacket.UP)
		                	client.forward();
		            	 else if(received.event == MPacket.DOWN)
		                	client.backup();
		            	 else if(received.event == MPacket.LEFT)
		                	client.turnLeft();
		            	 else if(received.event == MPacket.RIGHT)
		                	client.turnRight();
		            	 else if(received.event == MPacket.FIRE)
		                	client.fire();
		            	 else
		                	throw new UnsupportedOperationException();
		               	 ref = ref +1;
					 }
				} 
	
			}

        // }catch(InterruptedException e){
           //  e.printStackTrace();
         
         }catch(IOException e){
             e.printStackTrace();
         }catch(ClassNotFoundException e){
             e.printStackTrace();
         }
            
     }
    
}
