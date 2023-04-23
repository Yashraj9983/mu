import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.lang.Math;

class receiver extends Thread {
	private Socket client = null;
	public receiver(Socket client) {
    	this.client = client;    	
	}
    
	@Override
	public void run() {
    	try {
        	DataInputStream din = new DataInputStream(client.getInputStream());
        	String message = "";  

        	while(true){   	 
                message = din.readUTF();                
                String[] res = message.split("[,]", 0);                            
                message="["+message+","+Node.pID+"]";
                System.out.println(message);
                if(!Node.initiator.equals(res[0])){
                    Node.initiator=res[0];
                    if(res[0].equals(Integer.toString(Node.pID))){
                        System.out.println("DEADLOCK DETECTED\n"+
                        "probe is recieved back to the initiator.");
                    }else{
                        Node.sendMsg(Node.sOut,Node.outCount,res[0]);                
                    }
                }
        	}    
    	} catch(EOFException e){  
            System.out.println("Node disconnected");  
        } catch(Exception e){        	
        	e.printStackTrace();
    	}
	}
}


public class Node {

    public static Socket[] sOut = new Socket[10];
    public static int outCount = 0;
    public static int pID;
    public static int portn;
    public static String initiator="none";
	
    public static void sendMsg(Socket[] sOut,int outCount,String message){
    	try{
        	Scanner scan = new Scanner(System.in);
        	int nextNode=0;        	
            message=message+","+pID;
        	if(outCount>0){
                while(nextNode<outCount){
                    DataOutputStream dout = new DataOutputStream(sOut[nextNode].getOutputStream());
                    try{                           	 
                        dout.writeUTF(message);
                        dout.flush();                        
                        nextNode+=1;
                    } catch (Exception e) {                    
                        nextNode+=1;
                        if(nextNode==outCount){
                            System.out.println("msg not sent (No next node found)");   	 
                        }   
                    }
                }
            }else{
                System.out.println("msg not sent (No next node found)");   	 
            }
       	 
    	}catch (Exception e) {        	
        	e.printStackTrace();
    	}
	}
	public static void main(String[] args) {
    	try {
        	System.out.println("enter pID :");
        	Scanner sc = new Scanner(System.in);
        	pID = sc.nextInt();
        	System.out.println("enter self port :");
        	portn = sc.nextInt();
        	ServerSocket s = new ServerSocket(portn);
        	Socket[] sIn = new Socket[10];
        	receiver[] receiverthread = new receiver[10];
        	int inCount = 0;        	
        	int option = 0;        	
        	int right = 0;            
        	while (option < 5) {
            	option=0;                
            	System.out.println("\n1.add incoming connection\n2.add outgoing " 
                        +"connection\n3.send probe\n");
            	try{   	                     
                    option = sc.nextInt();           	 
                    switch (option) {
                        case 1:
                            System.out.println("waiting for connection:\n");
                            sIn[inCount] = s.accept();// establishes connection 
                            receiverthread[inCount] = new receiver(sIn[inCount]);
                            receiverthread[inCount].start();                   	 
                            inCount++;
                            break;
                        case 2:
                            System.out.println("enter port number:");
                            int toPort = sc.nextInt();
                            sOut[outCount] = new Socket("localhost", toPort);
                            System.out.println("node connected\n");
                            outCount++;
                            break;
                        case 3:
                            sendMsg(sOut,outCount,Integer.toString(pID));                   	 
                            break;
                                              
                        default:
                            break;
                    }
                }catch (Exception e) {                    	
                    e.printStackTrace();
            	}
        	}       	 
    	} catch (Exception e) {        	
        	System.out.println(e);
    	}
	}
}