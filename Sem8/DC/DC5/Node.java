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
                if(message.charAt(0)=='['){
                    String firstPid=String.valueOf(Node.pID);    
                    if(message.charAt(1)!=firstPid.charAt(0)){
                        System.out.println("msg: "+message+']');
                        Node.sendMsg(Node.sOut,Node.outCount,message);       	 
                    }
                    else if(message.charAt(1)== firstPid.charAt(0)){
                        System.out.println("msg: "+message+']');
                        String str = message;
                        str=str.substring(1,str.length());
                        String[] res = str.split("[,]", 0);
                        int max=0;
                        for(String myStr: res){
                            int val=Integer.parseInt(myStr);
                            if (val>max){
                                max=val;
                            }                
                        }
                        System.out.println("max:"+max);                
                        Node.coordinator=max;                
                        message="coordinator is "+max;
                        System.out.println(message);
                        Node.sendMsg(Node.sOut,Node.outCount,message);
                        // break;
                    }
                }
                else if(message.substring(0,11).equals("coordinator")){
                    int max=Integer.parseInt(message.substring(15));
                    if(Node.coordinator!=max){
                        System.out.println(message);                
                        Node.coordinator=max;
                        Node.sendMsg(Node.sOut,Node.outCount,message); 
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
    public static int coordinator=0;
	
    public static void sendMsg(Socket[] sOut,int outCount,String message){
    	try{
        	Scanner scan = new Scanner(System.in);
        	int nextNode=0;
        	
            if(message.charAt(0)=='['){
                if(message.length()>=2){
                    message+=","+pID;
                }else{
                    message="["+pID;
                }
            }
        	if(outCount>0){
                while(nextNode<outCount){
                    DataOutputStream dout = new DataOutputStream(sOut[nextNode].getOutputStream());
                    try{                           	 
                        dout.writeUTF(message);
                        dout.flush();
                        // System.out.println("nextNode= "+nextNode+"th connection");
                        break;
                    } catch (Exception e) {
                        // System.out.println(nextNode+"th node not connected");
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
                        +"connection\n3.do election \n4.print coordinator\n");
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
                            sendMsg(sOut,outCount,"[");                   	 
                            break;
                        case 4:
                            System.out.println("coordinator is "+coordinator);
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