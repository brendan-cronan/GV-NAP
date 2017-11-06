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
	Socket clientSocket;
	String username, speed, hostname;
	public ClientHandler(Socket socket) {
		clientSocket = socket;
		String[] clientData; 
		
		try {
			clientData = Net_Util.recStrArr(clientSocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}







  public static void main(String[] args){

  }

}
