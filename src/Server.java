import java.util.*;
import java.io.*;
import java.net.*;

class Server{
	public static final int PORT = 6603;
  private HashMap<Client,ArrayList<NapFile>> fileMap =new HashMap<Client,ArrayList<NapFile>>(100);
  private HashMap<NapFile,ArrayList<Client>> clientMap =new HashMap<NapFile,ArrayList<Client>>(100);
  
  Server(){
	  
	  while(true) {
	  Socket client = Net_Util.welcomeClient(PORT);
	  
	  
	  ClientHandler handler =  new ClientHandler(client);
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
  
  public void removeClient(Client client) {
		for(NapFile file: fileMap.get(client)) {
			clientMap.get(file).remove(client);
			if(clientMap.get(file).isEmpty()) {
				clientMap.remove(file);
			}
		}
		fileMap.remove(client);
	  }
  
  
class ClientHandler extends Thread {

	public static final int PORT = 6603;
	Socket clientSocket;
	String username, hostname, connectionType;
	Client client;
	public ClientHandler(Socket socket) {
		clientSocket = socket;
		String[] clientData = new String[0];
		String[] clientFileData = new String[0];
		String[] temp;
		
		try {
				clientData = Net_Util.recStrArr(clientSocket);
			username = clientData[0];
			connectionType = clientData[1];
			hostname = clientData[2];
			client = new Client(clientSocket.getInetAddress(), PORT, username, connectionType);
			Net_Util.send(clientSocket, "Client ID Recieved");
			
				clientFileData = Net_Util.recStrArr(clientSocket);
			ArrayList<NapFile> files = new ArrayList<NapFile>();
			for(int i = 0; i < clientFileData.length; i++) {
				NapFile x = new NapFile(clientFileData[i].split("@@")[0], clientFileData[i].split("@@")[1]);
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
		ArrayList<String> results = new ArrayList<String>();
		while(running){
			
			try {
			command = Net_Util.recString(clientSocket).split(" ");
			
			if(command[0].startsWith("search")) {
				if(command.length == 1) {
					for(NapFile file: clientMap.keySet()) {
						for(Client client : clientMap.get(file)) {
							results.add(file.FILE_NAME + "@@" + file.DESCRIPTION + "@@" + client.IP + "@@" + client.USERNAME + "@@" + client.CONNECTION_TYPE);
						}
					}
				} else {	
					System.out.println("2 args");
					 for(NapFile file: clientMap.keySet()) {
						 if(file.DESCRIPTION.contains(command[1])) {
							 for(Client client: clientMap.get(file))
								 results.add(file.FILE_NAME + "@@" + file.DESCRIPTION + "@@" + client.IP + "@@" + client.USERNAME + "@@" + client.CONNECTION_TYPE);
						 }
					 }
				}System.out.println(results.size());
					
					 
					if(results.isEmpty()) {
						System.out.println("empty");
						String[] message={"No results found"};
						Net_Util.send(clientSocket, message);
					} else {
						int i = 0;
						String[] message = new String[results.size()];
						System.out.println("yes");
						for(String s:results)
							message[i++] = s;
						Net_Util.send(clientSocket, message);
					}
					
					
					
				
			}
			if(command[0].startsWith("quit")) {
				removeClient(client);
				clientSocket.close();
				running = false;
			}
			} catch(Exception e) {
			}
			
	
	}
}
}






  public static void main(String[] args){
Server s = new Server();
  }

}


