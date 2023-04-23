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
                if(message.equals("ok")){
                    Node.recvCount+=1;
                    System.out.println("ok - "+Node.recvCount);
                    if(Node.recvCount==Node.inCount){
                        Node.CS=true;
                        System.out.println("acquired CS");
                        Node.recvCount=0;
                        Node.reqCS=false;
                    }
                }
                else{
                    int RpID=Integer.parseInt(message.substring(4,5));
                    int newTS=Integer.parseInt(message.substring(9));
                    System.out.println("recvPid:"+RpID+" ts:"+newTS);
                    if(Node.CS==false){
                        if(Node.reqCS==false || (Node.reqCS==true && Node.reqTs>newTS)){
                            Node.sendReply(Node.sOut[RpID],"ok");        
                        }else{
                            Node.RD[Node.RDcount]=RpID;
                            Node.RDcount+=1;
                        }                            
                        if(Node.ts<=newTS){
                            Node.ts=newTS+1;
                        }
                    }else{
                        Node.RD[Node.RDcount]=RpID;
                        Node.RDcount+=1;
                    }
                }
                
        	}    
    	} catch(EOFException|SocketException e){  
            System.out.println("Node disconnected");  
        } catch(Exception e){        	
        	e.printStackTrace();
    	}
	}
}


public class Node {

    public static Socket[] sOut = new Socket[10];
    public static int ts,pID,portn,outCount,RDcount,inCount,recvCount,reqTs;
    public static int[] RD= new int[10];
    public static boolean CS,reqCS;
    
	
    public static void reqAll(Socket[] sOut,String message){
    	try{
        	for(Socket s: sOut){
                try{
                if(s!=null){
                    DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                    try{                           	 
                        dout.writeUTF(message);
                        dout.flush();
                    } catch (Exception e) {
                        System.out.println("line 74");
                    }
                }
                }catch(NullPointerException e){
                    System.out.println("line 78");
                    continue;
                }
                catch(Exception e){
                    System.out.println("line 82");
                    e.printStackTrace();
                }
            }
    	}catch (Exception e) {        	
        	e.printStackTrace();
    	}
	}

    public static void sendReply(Socket s,String message){
    	try{        	                
            if(s.isConnected()){
                DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                try{                           	 
                    dout.writeUTF(message);
                    dout.flush();
                } catch (Exception e) {
                    System.out.println("line 141");
                }
            }            
    	}catch (Exception e) {        	
        	e.printStackTrace();
    	}
	}


	public static void main(String[] args) {
    	try {
            CS=false;
            reqCS=false;
            ts=0;
            outCount=0;
            RDcount=0;
        	System.out.println("enter pID :");
        	Scanner sc = new Scanner(System.in);
        	pID = sc.nextInt();
        	System.out.println("enter self port :");
        	portn = sc.nextInt();
        	ServerSocket s = new ServerSocket(portn);
        	Socket[] sIn = new Socket[10];
        	receiver[] receiverthread = new receiver[10];
        	inCount = 0;        	
        	int option = 0;        	
        	int right = 0;            
        	while (option < 5) {
            	option=0;                
            	System.out.println("\n1.add incoming connection\n2.add outgoing " 
                        +"connection\n3.request CS\n4.release CS\n");
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
                            System.out.println("enter reciever pID:");
                            int pIDrecv = sc.nextInt();
                            System.out.println("enter port number:");
                            int toPort = sc.nextInt();
                            sOut[pIDrecv] = new Socket("localhost", toPort);
                            System.out.println("node "+pIDrecv+" connected\n");
                            outCount++;
                            break;
                        case 3:
                            if(CS==false){
                                recvCount=0;
                                reqCS=true;
                                reqTs=ts;
                                reqAll(sOut,"pID:"+pID+" ts:"+ts);                   	 
                                ts+=1;
                            }else{
                                System.out.println("process is already holding CS");
                            }
                            break;
                        case 4:
                            if(CS==true){
                                CS=false;
                                System.out.println("CS released");
                                ts+=1;
                                for(int i =0;i<RDcount;i++){
                                    sendReply(sOut[RD[i]],"ok");
                                }
                                RDcount=0;
                            }
                            else{
                                System.out.println("process is not holding CS");
                            }
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
