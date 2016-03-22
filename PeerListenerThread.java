import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.*;


public class PeerListenerThread implements Runnable {

    private MSocket mSocket =  null;
    //private BlockingQueue eventQueue = null;
    private int ref;
    private Hashtable<String, Client> clientTable;
  	private Info info = null;

    public PeerListenerThread(MSocket mSocket, Hashtable<String, Client> clientTable, Info info){
        this.mSocket = mSocket;
    	this.clientTable = clientTable;
        this.ref = 0;
        this.info = info;
		System.out.println("Construct a peer server listerner thread");
    }
    // peer server listener thread. when token is 1
    public void run() {

    	Client client = null;
		if(Debug.debug) System.out.println("Run a peer server listener thread");
		try{
		    MPacket received = null;
		      
		    while(true){
		        
		        received = (MPacket)mSocket.readObject();
		        
		        System.out.println("PeerServer Receives " + received);
				if (received.type == MPacket.ACK && received.event == MPacket.ACTION) //waiting for action ACK
				{
					//System.out.println("INFO ACK LISTS 1 " + info.acks[0]+" "+info.acks[1]+ " " +info.acks[2] + " " + info.acks[3]);
				   	for(int i =0; i<4; i++){				
					    if (received.name.equals(info.names[i]))
					    {
							//info.acks[i] = true;
							info.setInfoAck(i,1);
							//System.out.println("INFO ACK LISTS 2 " +info.acks[0]+" "+info.acks[1]+ " " +info.acks[2] + " " + info.acks[3]);
							break;
						}  
					}
					System.out.println("has received ACTION ACK from " + received.name);
				}
				else if (received.type == MPacket.ACK && received.event == MPacket.TOKEN) //waiting for token ACK
				{
						System.out.println("has received TOKEN ACK from " + received.name);
						PeerConnectionThread.setFlag(0);
					
				} 
			}

         //}catch(InterruptedException e){
            // e.printStackTrace();
         
         }catch(IOException e){
             e.printStackTrace();
         }catch(ClassNotFoundException e){
             e.printStackTrace();
         }
            
     }
    
}
