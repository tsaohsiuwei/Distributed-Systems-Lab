import java.net.InetAddress;
import java.lang.*;

public class Info{

	public String[] names;
	public InetAddress[] IPs; 
	public int[] ports; 
	public int[] locals;
	public boolean[] acks;

	public Info(){
		this.names = new String[4];
		this.IPs = new InetAddress[4];
		this.ports = new int[4];
		this.locals = new int[4];
		this.acks = new boolean[4];
	}
 public synchronized void setInfoAck(int index,int value) {
 	   if(value == 1)
        {
        	this.acks[index]=true;
        }
        else{
        	this.acks[index]=false;
        } 
    }


  public synchronized boolean readInfoAck(int index) {
 	  
           return this.acks[index];
       
    }

} 
