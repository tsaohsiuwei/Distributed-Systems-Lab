import java.io.InvalidObjectException;
import java.io.IOException;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.Random;
import java.net.*;


public class ServerSenderThread implements Runnable {
	
    private NSocket[] nSocketList = null;
    private BlockingQueue eventQueue = null;
    private int globalSequenceNumber; 
    
    public ServerSenderThread(NSocket[] nSocketList,
                              BlockingQueue eventQueue){
        this.nSocketList = nSocketList;
        this.eventQueue = eventQueue;
        this.globalSequenceNumber = 0;
		System.out.println("Construct a server sender thread");
    }

    /*
     *Handle the initial joining of players including 
      position initialization
     */
    public void handleHello(){
        
        //The number of players
        int playerCount = nSocketList.length;
        Random randomGen = null;
        Player[] players = new Player[playerCount];
        if(Debug.debug) System.out.println("In handleHello");
        MPacket hello = null;
        
        InetAddress[] IPs = new InetAddress[5];
        int[] ports = new int[5];
        try{        
            for(int i=0; i<playerCount; i++){
                hello = (MPacket)eventQueue.take();
                System.out.println("[" + System.currentTimeMillis() + "]" + " The IPPPPPPPPPPPP is: "+hello.IP + "  Peerport is " + hello.peerPort);
                IPs[i] = hello.IP;
                ports[i] = hello.peerPort;
                //Sanity check 
                if(hello.type != MPacket.HELLO){
                    throw new InvalidObjectException("Expecting HELLO Packet");
                }
                if(randomGen == null){
                   randomGen = new Random(hello.mazeSeed); 
                }
                //Get a random location for player
                Point point =
                    new Point(randomGen.nextInt(hello.mazeWidth),
                          randomGen.nextInt(hello.mazeHeight));
                
                //Start them all facing North
                Player player = new Player(hello.name, point, Player.North);
                players[i] = player;
            }
            
            hello.event = MPacket.HELLO_RESP;
            hello.players = players;
            hello.IPs = IPs;
            hello.ports = ports;
            
            int i=0;
            //Now broadcast the HELLO
            if(Debug.debug) System.out.println("[" + System.currentTimeMillis() + "]" + " Server Sending " + hello);
            
            for(NSocket nSocket: nSocketList){
                System.out.println("boradcasting");
                nSocket.writeObject(hello);   
            }
            
        }catch(InterruptedException e){
            e.printStackTrace();
            Thread.currentThread().interrupt();    
        }catch(IOException e){
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
    
    public void run() {
        MPacket toBroadcast = null;
		System.out.println("Running a server sender thread");
        
        handleHello();
        
        while(true){
            try{
                //Take packet from queue to broadcast
                //to all clients
            	System.out.println("toronto");
                toBroadcast = (MPacket)eventQueue.take();
                System.out.println("ottawa");
                //Tag packet with sequence number and increment sequence number
                toBroadcast.sequenceNumber = this.globalSequenceNumber++;
                if(Debug.debug) System.out.println("Server Sending " + toBroadcast);
                //Send it to all clients
                
               for(NSocket nSocket: nSocketList){
                    nSocket.writeObject(toBroadcast);
                }
            }catch(InterruptedException e){
                System.out.println("Throwing Interrupt");
                Thread.currentThread().interrupt();    
			}
            
        }
    }
}
