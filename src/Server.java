import java.util.*;
import java.io.*;
import java.net.*;

class Server{
	//public static int PORT = 6603;
  public static HashMap<Client,ArrayList<NapFile>> fileMap =new HashMap<Client,ArrayList<NapFile>>(100);
  public static HashMap<NapFile,ArrayList<Client>> clientMap =new HashMap<NapFile,ArrayList<Client>>(100);

  public static void main(String[] args){

  
	try {
		ServerSocket welcome = new ServerSocket(6603);
	
		
	
	  
	  
	  while(true) {
		  
	  //Socket client = Net_Util.welcomeClient(6603);
	  Socket client;
	try {
		client = welcome.accept();
		System.out.println("New client connecting");
	ClientHandler handler =  new ClientHandler(client);
	
	  new Thread(handler).start();
	  
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		welcome.close();
	}
	  }
	  } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

	  
  }
  
}
class ClientHandler implements Runnable {

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
			client = new Client(clientSocket.getInetAddress(), 6603, username, connectionType);
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
	public void registerClient(Client client, ArrayList<NapFile> files) {
		Server.fileMap.put(client, files);
		for(NapFile file : files) {
			if(!Server.clientMap.containsKey(file)) {
				ArrayList<Client> clients= new ArrayList<Client>();
				clients.add(client);
				Server.clientMap.put(file, clients);
			}else {
				Server.clientMap.get(file).add(client);
			}
	  }
  }

  public void removeClient(Client client) {
		for(NapFile file: Server.fileMap.get(client)) {
			Server.clientMap.get(file).remove(client);
			if(Server.clientMap.get(file).isEmpty()) {
				Server.clientMap.remove(file);
			}
		}
		Server.fileMap.remove(client);
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
					for(NapFile file: Server.clientMap.keySet()) {
						for(Client client : Server.clientMap.get(file)) {
							results.add(file.FILE_NAME + "@@" + file.DESCRIPTION + "@@" + client.IP + "@@" + client.USERNAME + "@@" + client.CONNECTION_TYPE);
						}
					}
				} else {	
					System.out.println("2 args");
					 for(NapFile file: Server.clientMap.keySet()) {
						 if(file.DESCRIPTION.contains(command[1])) {
							 for(Client client: Server.clientMap.get(file))
								 results.add(file.FILE_NAME + "@@" + file.DESCRIPTION + "@@" + client.IP + "@@" + client.USERNAME + "@@" + client.CONNECTION_TYPE);
						 }
					 }
				}System.out.println(results.size());


					if(results.isEmpty()) {
						String[] message={"No results found"};
						Net_Util.send(clientSocket, message);
					} else {
						int i = 0;
						String[] message = new String[results.size()];
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









