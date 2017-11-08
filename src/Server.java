import java.util.*;
import java.io.*;
import java.net.*;

class Server{
	public static final int PORT = 6603;
  private HashMap<Client,ArrayList<NapFile>> fileMap =new HashMap<Client,ArrayList<NapFile>>(100);
  private HashMap<NapFile,ArrayList<Client>> clientMap =new HashMap<NapFile,ArrayList<Client>>(100);
  
  Server(int port){
	  
	  while(true) {
	  Socket client = Net_Util.welcomeClient(port);
	  
	  
	  ClientHandler handler =  new ClientHandler(client);
	  System.out.println("Connection Established.");
	  handler.start();
	  }
  }
  public void registerClient(Client client, ArrayList<NapFile> files) {
		fileMap.put(client, files);
		for(NapFile file : files) {
			if(!clientMap.containsKey(file)) {
				ArrayList<Client> clients= new ArrayList<Client>();
				clients.add(client);
				clientMap.put(file, clients);
			}else {
				clientMap.get(file).add(client);
			}
	  }
  }
  
class ClientHandler extends Thread {

	public static final int PORT = 6603;
	Socket clientSocket;
	String username, hostname;
	Client client;
	public ClientHandler(Socket socket) {
		clientSocket = socket;
		String[] clientData = new String[0];
		String[] clientFileData = new String[0];
		String connectionType;
		ConnectionType connection;
		
		try {
			while(clientData.length == 0) {
				clientData = Net_Util.recStrArr(clientSocket);
			}
			username = clientData[0];
			connectionType = clientData[1];
			if(connectionType.equalsIgnoreCase("ethernet")) {
				connection = ConnectionType.Ethernet;
			}else if(connectionType.equalsIgnoreCase("Modem")) {
				connection = ConnectionType.Modem;
			}else if(connectionType.equalsIgnoreCase("T1")) {
				connection = ConnectionType.T1;
			}else  {
				connection = ConnectionType.T3;
			}
			hostname = clientData[2];
			client = new Client(clientSocket.getInetAddress(), PORT, username, connection);
			Net_Util.send(clientSocket, "Client ID Recieved");
			while(clientFileData.length == 0) {
				clientFileData = Net_Util.recStrArr(clientSocket);
			}
			ArrayList<NapFile> files = new ArrayList<NapFile>();
			for(int i = 0; i < clientFileData.length; i = i+2) {
				NapFile x = new NapFile(clientFileData[i], clientFileData[i+1]);
				files.add(x);
			}
			registerClient(client, files);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		boolean running = true;
		String[] command;
		while(running){
			
			try {
			command = Net_Util.recString(clientSocket).split(" ");
			
			if(command[0].startsWith("search")) {
				if(command.length == 1) {
				Net_Util.send(clientSocket, fileMap.keySet().toArray());
				} else if(command.length == 2) {
					HashMap<NapFile,ArrayList<Client>> output =new HashMap<NapFile,ArrayList<Client>>(100);

					
					 Set<NapFile> x = clientMap.keySet();	
					 for(NapFile y: x) {
						 if(y.DESCRIPTION.contains(command[1])) {
						 
							 output.put(y,clientMap.get(y));
						 }
					 }
					
					 
					for(Map.Entry<NapFile, ArrayList<Client>> out: output.entrySet()) {
						for(Client ohmygod: out.getValue()) {
							
							
							String[] sendMe = {out.getKey().FILE_NAME, ohmygod.USERNAME, ohmygod.CONNECTION_TYPE.toString()};
							
							Net_Util.send(clientSocket, sendMe);
							
						}
						String[] endMessage={"Done Sending File Locations"};
						Net_Util.send(clientSocket, endMessage);
					}
					
					
				}else {
					//TODO: error handling
				}
			}
			} catch(Exception e) {
			}
			
	
	}
}







  public static void main(String[] args){

  }

}
