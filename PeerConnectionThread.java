import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.*;
import java.net.*;

public class PeerConnectionThread implements Runnable {

    private MServerSocket mServerSocket = null;
    private int peerCount; //The number of current peers, excluding itself
   
    private BlockingQueue<MPacket> eventQueue = null;
    //private ArrayList<MSocket> mSocketList = null; for dynamic join and leave
    private MSocket[] mSocketList = null;
    private int sequenceNumber;
    private int tokenNumber;
    public static int token; // 0 means no token. 
    private String myName = null;
    private int myIndex;
    public static int flag;
	  private Hashtable<String, Client> clientTable = null;
    private Info info = null;
    
    /*
    * Constructor
    */
    public PeerConnectionThread(int port, BlockingQueue eventQueue, String myName,  Hashtable<String, Client> clientTable, Info info) throws IOException{
      peerCount = 0; 
      mServerSocket = new MServerSocket(port);
      if(Debug.debug) System.out.println("construct a peer connextion thread listening on port:  " + port);
        
      this.eventQueue = eventQueue;
      this.sequenceNumber = 0;
      this.tokenNumber = 0;
      mSocketList = new MSocket[3];
      token = 0;
	    this.myName = myName;
      this.flag = 0;
      this.clientTable = clientTable;
      this.info = info;
    }

    public static synchronized void setToken(int setValue) {
        token = setValue;
    }
    public static synchronized int readToken(){
        return token;
    }
    public static synchronized void setFlag(int setValue) {
        flag = setValue;
    }
    public static synchronized int readFlag(){
        return flag;
    }
  
    public void run(){
     // System.out.println("My name is " + myName);
    //  System.out.println("Info names are " + info.names[0] +" " + info.names[1] + " " + info.names[2] + " " + info.names[3]);
      if(myName.equals(info.names[0]))
      {
        setToken(1);
      }  
      Client client = null;
      if(Debug.debug) System.out.println("Run a peer connextion thread");

		  long timeOut = 200;
		  int fail = 1;
		  
     //mSocketList = new ArrayList<MSocket>(4);

      MPacket toBroadcast = null;
      System.out.println("TOOOOOOOOOOOOOOKKKKEEEENNNNN is "+token);


      try{
            while(peerCount < 3){
              MSocket mSocket = mServerSocket.accept();  
              new Thread(new PeerListenerThread(mSocket, this.clientTable, this.info)).start();
              mSocketList[peerCount] = mSocket;                   
              peerCount++;
            }
          }catch(IOException e){
               e.printStackTrace();
           } 
      
		    while(true){

         try{
        
			    //Listen for new peers
        	//if( ){ //message from the naming server, notifying there is a new peer
            
            // 	MSocket mSocket = MServerSocket.accept();
            //   	new Thread(new peerServerListenerThread(mSocket, eventQueue)).start();
            // 	mSocketList.add(mSocket);                  
            //  	peerCount++;

        	//}
 
          int test = readToken();
         // System.out.println("token is: "+test);
          //peer sender thread
          if(test == 1){
            System.out.println("token is now 1");
            System.out.println(myName+" received token and about to do action");
 				    toBroadcast = (MPacket)eventQueue.take(); 

          
            

		    	  if(Debug.debug) System.out.println("PeerServer Sends " + toBroadcast);
 
                //Tag packet with sequence number and increment sequence number
            int currentSeq = this.sequenceNumber;
            toBroadcast.sequenceNumber = this.sequenceNumber;
            this.sequenceNumber = this.sequenceNumber+1;
            toBroadcast.name = myName;
           
            
           
            

            //display my own action
            client = clientTable.get(myName);
                   if(toBroadcast.event == MPacket.UP)
                      client.forward();
                   else if(toBroadcast.event == MPacket.DOWN)
                      client.backup();
                   else if(toBroadcast.event == MPacket.LEFT)
                      client.turnLeft();
                   else if(toBroadcast.event == MPacket.RIGHT)
                      client.turnRight();
                   else if(toBroadcast.event == MPacket.FIRE)
                      client.fire();
                   else
                      throw new UnsupportedOperationException();


		   		
    				for (int i=0; i<4; i++)
    				{
    					if (info.names[i].equals(myName)) 
              {
    						info.acks[i] = true;
                myIndex = i;
              }
    				}
						
            for(MSocket mSocket: mSocketList){
              mSocket.writeObject(toBroadcast);
            //  System.out.println(ss);
            //  ss++;
            }

            //and retransmit if timeout
            long startTime = System.currentTimeMillis();
    				while (fail == 1)
    				{

    		      while(System.currentTimeMillis() - startTime < timeOut)
    					{
    						if(info.readInfoAck(0) == true && info.readInfoAck(1) == true && info.readInfoAck(2) == true && info.readInfoAck(3) == true)
    						{
    		          fail = 0;
    							break;
    						}	
    					}	
              if (fail == 0)
                break;
    					if (fail == 1)
    					{
                if(info.readInfoAck(0) == true && info.readInfoAck(1) == true && info.readInfoAck(2) == true && info.readInfoAck(3) == true)
                {
                  break;
                }
                System.out.println("Resending now");
    						for(MSocket mSocket: mSocketList){
                  toBroadcast.sequenceNumber = currentSeq;
    		          mSocket.writeObject(toBroadcast);

    		        }
    						startTime = System.currentTimeMillis();
    					}
    				}
            fail = 1;
            info.setInfoAck(0,0);
            info.setInfoAck(1,0);
            info.setInfoAck(2,0);
            info.setInfoAck(3,0);
            info.setInfoAck(myIndex,1);
            //info.acks[myIndex] = true;
            setToken(0);

            MPacket tokenmsg = null;
            System.out.println(myName+" is sending token msg");
            tokenmsg = new MPacket(myName, MPacket.TOKEN, MPacket.TOKEN);
            int currentToken = this.tokenNumber;
            
            tokenmsg.tokenNumber = this.tokenNumber;
            this.tokenNumber = this.tokenNumber +1;

            InetAddress ip = info.IPs[(myIndex+1)%4];
            int port = info.locals[(myIndex+1)%4];
            System.out.println("Next for token: its IP is " + ip.toString() + " port is " + port);

            MSocket tokenS = null;
            
            for(MSocket ss : mSocketList){
               System.out.println("ss.IP is " + ss.IP.toString() + " ss.port is " + ss.port);
           //   if(ss.IP.toString().equals(ip.toString()) && ss.port == port){
                
           //     tokenS = ss;
           //     break;
          //    }
            }
            Random rand = new Random(); 
            int value = rand.nextInt(3); 
            tokenS = mSocketList[value];
            tokenS.writeObject(tokenmsg);
             
            setFlag(1);
           // waiting for the tokenACK
            startTime = System.currentTimeMillis();

            while(readFlag() == 1){
              while(System.currentTimeMillis() - startTime < timeOut){
                   if(readFlag() ==0){
                    break;
                   }
              }
              if(readFlag() == 1){
                System.out.println(myName+" is resending token");
                tokenmsg.tokenNumber = currentToken;
                tokenS.writeObject(tokenmsg);
                startTime = System.currentTimeMillis();
              }
            }
          }
          //System.out.println("token is still 0");
          //token is 0
        }catch(InterruptedException e){
          System.out.println("Throwing Interrupt");
         Thread.currentThread().interrupt(); 
        }
      }
         
    }
}
  

