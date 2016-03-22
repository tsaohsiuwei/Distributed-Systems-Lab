import java.io.Serializable;
import java.net.InetAddress;

public class MPacket implements Serializable {

    /*The following are the type of events*/
    public static final int HELLO = 100; //?
    public static final int ACTION = 200; // client Movement 
    public static final int TOKEN = 300; 
	public static final int ACK = 400;
	public static final int REQUEST = 500; 
	
    /*The following are the specific action 
    for each type*/
    /*Initial Hello*/
    public static final int HELLO_INIT = 101;
    /*Response to Hello*/
    public static final int HELLO_RESP = 102;

    /*Action*/
    public static final int UP = 201;
    public static final int DOWN = 202;
    public static final int LEFT = 203;
    public static final int RIGHT = 204;
    public static final int FIRE = 205;
    

    /*REQUEST*/
	public static final int JOIN = 501;
	public static final int LEAVE = 502;
	
    //These fields characterize the event  
    public int type;
    public int event; 

    //The name determines the client that initiated the event
    public String name;
    public InetAddress IP;
    public InetAddress[] IPs;
    public int[] ports;
	public int peerPort;

    //The sequence number of the event
    public int sequenceNumber;
    public int tokenNumber;
    public int clientsequenceNumber;

    //These are used to initialize the board
    public int mazeSeed;
    public int mazeHeight;
    public int mazeWidth; 
    public Player[] players;

    public MPacket(int type, int event){
        this.type = type;
        this.event = event;
    }
    
    public MPacket(String name, int type, int event){
        this.name = name;
        this.type = type;
        this.event = event;
    }

    public MPacket(String name, int type, int event, InetAddress IP, int peerPort){
        this.name = name;
        this.type = type;
        this.event = event;
        this.IP = IP;
		this.peerPort = peerPort;
   
    }
    
    public String toString(){
        String typeStr;
        String eventStr;
        
        switch(type){
            case 100:
                typeStr = "HELLO";
                break;
            case 200:
                typeStr = "ACTION";
                break;
            case 300:
                typeStr = "TOKEN";
                break;
            case 400:
                typeStr = "ACK";
                break;
            case 500:
                typeStr = "REQUEST";
                break;
            default:
                typeStr = "ERROR";
                break;        
        }
        switch(event){
            case 101:
                eventStr = "HELLO_INIT";
                break;
            case 102:
                eventStr = "HELLO_RESP";
                break;
            case 201:
                eventStr = "UP";
                break;
            case 202:
                eventStr = "DOWN";
                break;
            case 203:
                eventStr = "LEFT";
                break;
            case 204:
                eventStr = "RIGHT";
                break;
            case 205:
                eventStr = "FIRE";
                break;
            case 200:
                eventStr = "ACTION";
                break;
            case 300:
                eventStr = "TOKEN";
                break;
            case 400:
                eventStr = "ACK";
                break;
            default:
                eventStr = "ERROR";
                break;        
        }
        //MPACKET(NAME: name, <typestr: eventStr>, SEQNUM: sequenceNumber)
        String retString = String.format("MPACKET(NAME: %s, <%s: %s>, SEQNUM: %s)", name, 
            typeStr, eventStr, sequenceNumber);
        return retString;
    }

}
