import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.*;
import java.net.*;
import java.util.ArrayList;



class Host extends JPanel{


  private static final Dimension WINDOW_SIZE = new Dimension(1000, 700);
  private static final int PORT_NUM=6603;

  private JPanel ConnectPane;
  private JPanel FilePane;
  private JPanel CmdPane;


  private JTextField serverName;
  private JTextField portNum;
  private JTextField userName;
  private JTextField hostName;
  private JTextField connectionType;


  private JButton connectButton;







  private Socket serverSocket;


  public Host(){
    this.setLayout(new BorderLayout());
    this.setPreferredSize(WINDOW_SIZE);
    ConnectPane=new JPanel(new FlowLayout());
    FilePane=new JPanel(new BorderLayout());
    CmdPane=new JPanel(new BorderLayout());

    /*
    ConnectPane.setBackground(Color.RED);
    FilePane.setBackground(Color.MAGENTA);
    CmdPane.setBackground(Color.GREEN);
    */

    ConnectPane.setPreferredSize(new Dimension(WINDOW_SIZE.width,120));
    FilePane.setPreferredSize(new Dimension(WINDOW_SIZE.width,400));
    CmdPane.setPreferredSize(new Dimension(WINDOW_SIZE.width,200));


    ConnectPane.setBorder(BorderFactory.createCompoundBorder(
                       BorderFactory.createTitledBorder("Connect"),
                       BorderFactory.createEmptyBorder(5,5,5,5)));




    ClientListner listen =new ClientListner();





    serverName=new JTextField(20);
    portNum=new JTextField(10);
    userName=new JTextField(20);
    hostName=new JTextField(20);
    connectionType=new JTextField(10);

    connectButton=new JButton("Connect");
    connectButton.addActionListener(listen);
    connectButton.setActionCommand("CONNECT");


    ConnectPane.add(new JLabel("Name of Server:"));
    ConnectPane.add(serverName);
    ConnectPane.add(new JLabel("Port Number:"));
    ConnectPane.add(portNum);
    ConnectPane.add(new JLabel("Username:"));
    ConnectPane.add(userName);
    ConnectPane.add(new JLabel("Host Name:"));
    ConnectPane.add(hostName);
    ConnectPane.add(connectButton);




    this.add(ConnectPane,BorderLayout.NORTH);
    this.add(FilePane,BorderLayout.CENTER);
    this.add(CmdPane,BorderLayout.SOUTH);


  }















  public static void main(String[] args){
    JFrame f= new JFrame("GV-NAPSTER PROGRAM");
    Host h= new Host();

    //centers the frame.
    Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createVerticalGlue());
		box.add(h);
		box.add(Box.createVerticalGlue());
		f.getContentPane().add(box);
    //sets up Jframe
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.pack();
    f.setLocationRelativeTo(null);
    f.setVisible(true);
    clientRun cSpot = new clientRun();
    cSpot.run();





  }
  
 private void requestFile(String fileName, Client fileOwner){
	 try{
		 Socket OwnerSocket = Net_Util.connectToServer("" + fileOwner.IP, fileOwner.PORT_NUM);
		 Net_Util.send(OwnerSocket, fileName);
		
		 
		 try{
			 
			 File localCopy = new File("./sharingFiles/" + fileName);
			 FileOutputStream writer = new FileOutputStream(localCopy);
			 String[] contents = Net_Util.recStrArr(OwnerSocket);
			 
			 if(localCopy.createNewFile()){
				 for(String lineContent: contents){
					 byte[] cont = lineContent.getBytes();
					 writer.write(cont);
				 }
				 
				 
			 }
			 
			 
			 
			 
		 }catch(Exception e){
			 System.out.println("Problem sending request");
		 }
		 
		 
		 
	 }catch(Exception e){
		 System.out.println("couldn't send request");
	 }
	 
	 
 }

  private class ClientListner implements ActionListener {


		    //@Override
		    public void actionPerformed(ActionEvent e) {
          switch(e.getActionCommand().toLowerCase()){
            case "connect":
              hostName.getText();
              break;
            
            case "retrieve":
            	//FIXME: requestFile() needs two input parameters: a file name and a client object.
            	//We need some way to get that information out of the GUI.
            	//example: requestFile(fileName, clientToRetrieveFrom);


          }
        }
  }
}



 class clientRun implements Runnable{

	@Override
	public void run() {
		
		while (true){
			try{
			Socket requester = Net_Util.welcomeClient(6603);
			String requestedFile = Net_Util.recString(requester);
			File readFile = new File("./sharingFiles/" + requestedFile);
			InputStream reader = new FileInputStream(readFile);
			ArrayList<String> content = new ArrayList<String>();
			
			BufferedReader bufRead = new BufferedReader(new InputStreamReader(reader));
			String lineContent = bufRead.readLine();
			while(null != lineContent){
				content.add(lineContent);
				lineContent = bufRead.readLine();
			}
			String[] sendFile = (String[])content.toArray();
			 Net_Util.send(requester, sendFile);
			 requester.close();
			 reader.close();
			 bufRead.close();
			 
			
		}
			catch(Exception e){
				System.out.println("Issue receiving connection");
			}
		
	}
	
	
	
	
}
 }
