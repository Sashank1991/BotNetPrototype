import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.text.*;

class sockConn{
String 	regDate;
String 	targetHost;
String 	targetName;
int	targetPort;
Socket 	targetSock;
  sockConn() {
	Date date = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	String format = formatter.format(date);
	regDate=format;
  }
}

public class MasterBot1 {
  public static volatile String userCommand;
  public static ArrayList<sockConn> connList;
  public static void main(String[] args) throws Exception {
      connList = new ArrayList<sockConn>();
	if (args.length<2){
	  System.err.println("Error in the arguments provided. port number should be provided for server.");
	 System.exit(-1);	
	}
	    try {
	String port="";
        if (args[0].equals("-p")){	
      		port=args[1];
	}
	else{
	System.err.println("Error in the arguments provided. port number should be provided for server.");
	System.exit(-1);
	}
      SocketThread cliThread = new SocketThread(Integer.parseInt(port));
      cliThread.start();   
	while(true){
		System.out.print(">");
		 BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
		 userCommand= userInput.readLine();

		      if (userCommand !="" && ! userCommand.equals("list") ) {
 			String[] serverCmd = userCommand.split(" ");
					if(serverCmd[0].equals("connect")){
						if(serverCmd.length<4) {
							System.out.println("connect expects minimum 3 arguments");
						}
						else{
						if(serverCmd[1] != null && serverCmd[2]!= null && serverCmd[3]!= null){    	
							int numConn=1;
							String strUrl="";
							int Isurl=0;
							if(serverCmd.length>4){
								try {
									numConn= Integer.parseInt(serverCmd[4]);
								} catch (NumberFormatException n) {
									if(serverCmd[4].toLowerCase().contains("url=")){
										Isurl=1;
										strUrl=serverCmd[4];

									}
									if(serverCmd[4].toLowerCase().contains("keepalive")){
										Isurl=2;

									}
									
								}


							}
							
							if(serverCmd.length>5){
								if(serverCmd[5].toLowerCase().contains("url=")){
									Isurl=1;
									strUrl=serverCmd[5];
								}
								if(serverCmd[5].toLowerCase().contains("keepalive")){
									Isurl=2;
								}	
							}

							Iterator<sockConn> i = connList.iterator();
							int arg2Type;
							
							if (serverCmd[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){
								arg2Type=1;
							}
							else if(serverCmd[1].equals("all") ){	arg2Type=3; }
							else {arg2Type=2;}

							switch (arg2Type) {
							    case 1:  
								while (i.hasNext()) {
								   	sockConn currentSock = i.next(); 
									if(currentSock.targetHost.equals(serverCmd[1])){
									try{
									 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSock.targetSock.getOutputStream()));
									netOut.println("connect "+ serverCmd[2] + " "+ serverCmd[3] + " "+numConn+ " "+Isurl+ " "+strUrl);
  									netOut.flush();
									}
									 catch (Exception e) {
									System.err.println("Error connecting while sending command to socket with "+ currentSock.targetHost+" " + currentSock.targetPort);
									}
									}
									}
								     break;
							    case 2:  
								while (i.hasNext()) {
								   	sockConn currentSock = i.next(); 
									if(currentSock.targetName.equals(serverCmd[1])){
									try{
									 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSock.targetSock.getOutputStream()));
									netOut.println("connect "+ serverCmd[2] + " "+ serverCmd[3] + " "+numConn+ " "+Isurl+ " "+strUrl);
  									netOut.flush();
									}
									 catch (Exception e) {
									System.err.println("Error connecting while sending command to socket with "+ currentSock.targetHost+" " + currentSock.targetPort);
									}
									}
									}
								     break;
							    case 3: 
								while (i.hasNext()) {
							   	sockConn currentSock = i.next();
									try{
									 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSock.targetSock.getOutputStream()));
									netOut.println("connect "+ serverCmd[2] + " "+ serverCmd[3] + " "+numConn+ " "+Isurl+ " "+strUrl);
  									netOut.flush();
									}
									 catch (Exception e) {
									System.err.println("Error connecting while sending command to socket with "+ currentSock.targetHost+" " + currentSock.targetPort);
									}	
									}
								     break;
							}


			
						}
						else{
							System.out.println(" no  proper arguments for connect");
						}
					}
					}
					else if(serverCmd[0].equals("disconnect")){

						if(serverCmd.length<3) {
							System.out.println("disconnect expects minimum 2 arguments");
						}
						else{
						if(serverCmd[1] != null && serverCmd[2]!= null){    	
							int disPort=0;
							if(serverCmd.length>3){
								disPort= Integer.parseInt(serverCmd[3]);
							}

							Iterator<sockConn> i = connList.iterator();
							int arg2Type;
							
							if (serverCmd[1].matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){
								arg2Type=1; }
							else if(serverCmd[1].equals("all") ) {	arg2Type=3; }
							else {	arg2Type=2;}

							switch (arg2Type) {
							    case 1:  
								while (i.hasNext()) {
								   	sockConn currentSock = i.next(); 
									if(currentSock.targetHost.equals(serverCmd[1])){
									try{
									 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSock.targetSock.getOutputStream()));
									netOut.println("disconnect "+ serverCmd[2] + " "+ disPort);
  									netOut.flush();
									}
									 catch (Exception e) {
									System.err.println("Error connecting while sending command to socket with "+ currentSock.targetHost+" " + currentSock.targetPort);
									}
									}
									}
								     break;
							    case 2:  
								while (i.hasNext()) {
								   	sockConn currentSock = i.next(); 
									if(currentSock.targetName.equals(serverCmd[1])){
									try{
									 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSock.targetSock.getOutputStream()));
									netOut.println("disconnect "+ serverCmd[2] + " "+ disPort);
  									netOut.flush();
									}
									 catch (Exception e) {
									System.err.println("Error connecting while sending command to socket with "+ currentSock.targetHost+" " + currentSock.targetPort);
									}
									}
									}
								     break;
							    case 3: 
								while (i.hasNext()) {
							   	sockConn currentSock = i.next();
									try{
									 PrintWriter   netOut = new PrintWriter(new OutputStreamWriter(currentSock.targetSock.getOutputStream()));
									netOut.println("disconnect "+ serverCmd[2] + " "+ disPort);
  									netOut.flush();
									}
									 catch (Exception e) {
									System.err.println("Error connecting while sending command to socket with "+ currentSock.targetHost+" " + currentSock.targetPort);
									}	
									}
								     break;
							}


			
						}
						else{
							System.out.println(" no  proper arguments for disconnect");
						}
						}	
					}

			}
		 if(userCommand.equals("list")){

					for(int i=0;i<connList.size(); i++){
		  					System.out.println(connList.get(i).targetName+" "+connList.get(i).targetHost + " "+ connList.get(i).targetPort + " "+ connList.get(i).regDate);

					}
			}

	    } 
		}

		catch (Exception e) {
	      	e.printStackTrace();
		System.exit(-1);	    
}

  }
	
}


class SocketThread extends Thread {
  int portnum;
  sockConn sockList;
  SocketThread(int port) {
    portnum = port;
  }
  public void run() {
	    try {
		   ServerSocket m_ServerSocket = new ServerSocket(portnum);
		    while (true) {
			      Socket clientSocket = m_ServerSocket.accept();
				    sockList=new sockConn();
				    sockList.targetHost=clientSocket.getInetAddress().getHostAddress();
				    sockList.targetName=clientSocket.getInetAddress().getHostName();
				    sockList.targetPort=clientSocket.getPort();
				    sockList.targetSock=clientSocket;
				    MasterBot1.connList.add(sockList);
		    }
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
  }
}
