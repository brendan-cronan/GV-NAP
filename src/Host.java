import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.table.*;

class Host extends JPanel {

	private static final Dimension WINDOW_SIZE = new Dimension(1000, 700);
	private static final int SERVER_PORT = 6603, MY_PORT = 6605;
	private static final String[] CONN_TYPE = new String[] { "Ethernet", "Modem", "T1", "T3" };

	private JPanel ConnectPane;
	private JPanel FilePane;
	private JPanel CmdPane;

	// These all belong to the Connect Pane
	private JLabel errorDisplay;
	private JTextField serverName;
	private JTextField portNum;
	private JTextField userName;
	private JTextField hostName;

	private JComboBox<String> connectionType;
	private JButton connectButton;

	// These all belong to the Search Pane
	private JTextField searchField;
	private JTable fileTable;
	private JTable clientTable;

	private JTextArea fileDisplay;
	private static final String[] colNames = new String[] { "File", "Description" };
	private static final String[] clientColNames = new String[] { "Speed", "Username", "IP" };
	private String[][] fileData;
	private String[][] clientData;
	private JButton searchButton;
	private JButton getClientButton;

	// These all belong to the Command Pane
	private JTextField cmdField;
	private JTextArea cmdDisplay;
	private JButton cmdButton;

	private Socket serverSocket;
	private HashMap<NapFile, ArrayList<Client>> clientMap;
	ArrayList<NapFile> files = new ArrayList<NapFile>();

	public Host() {
		this.setLayout(new BorderLayout());
		this.setPreferredSize(WINDOW_SIZE);

		// JPanel Init
		ConnectPane = new JPanel(new BorderLayout());
		FilePane = new JPanel(new BorderLayout());
		CmdPane = new JPanel(new BorderLayout());

		/*
		 * To see the size of the panels ConnectPane.setBackground(Color.RED);
		 * FilePane.setBackground(Color.MAGENTA); CmdPane.setBackground(Color.GREEN);
		 */
		ConnectPane.setPreferredSize(new Dimension(WINDOW_SIZE.width, 120));
		FilePane.setPreferredSize(new Dimension(WINDOW_SIZE.width, 400));
		CmdPane.setPreferredSize(new Dimension(WINDOW_SIZE.width, 200));

		ConnectPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Connect"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		FilePane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Keyword Search"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		CmdPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("FTP"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		ClientListner listen = new ClientListner();

		// BEGIN: Connect Pane

		errorDisplay = new JLabel("");
		errorDisplay.setForeground(Color.RED);
		serverName = new JTextField("localhost",20);
		portNum = new JTextField("6605",10);
		userName = new JTextField("User1",20);
		hostName = new JTextField("User1",20);
		connectionType = new JComboBox<String>(CONN_TYPE);

		connectButton = new JButton("Connect");
		connectButton.addActionListener(listen);
		connectButton.setActionCommand("CONNECT");

		JPanel miniConnect = new JPanel(new FlowLayout());

		miniConnect.add(new JLabel("Name of Server:"));
		miniConnect.add(serverName);
		miniConnect.add(new JLabel("Port Number:"));
		miniConnect.add(portNum);
		miniConnect.add(new JLabel("Username:"));
		miniConnect.add(userName);
		miniConnect.add(new JLabel("Host Name:"));
		miniConnect.add(hostName);
		miniConnect.add(connectionType);
		miniConnect.add(connectButton);

		ConnectPane.add(miniConnect, BorderLayout.CENTER);

		Box box = new Box(BoxLayout.X_AXIS);
		box.add(Box.createHorizontalGlue());
		box.add(errorDisplay);
		box.add(Box.createHorizontalGlue());
		ConnectPane.add(box, BorderLayout.SOUTH);

		// END: Connect Pane

		// BEGIN: File Pane
		searchField = new JTextField(40);
		JPanel tablePanel = new JPanel(new BorderLayout());
		JPanel textPanel = new JPanel(new FlowLayout());

		DefaultTableModel model=new DefaultTableModel(1,1);

		fileTable = new JTable(model);
		clientTable = new JTable(model);



		searchButton = new JButton("Search");
		searchButton.addActionListener(listen);
		searchButton.setActionCommand("Search");

		textPanel.add(new JLabel("Search:"));
		textPanel.add(searchField);
		textPanel.add(searchButton);


		fileDisplay = new JTextArea(100, 10);
		fileDisplay.setEditable(false);
		JScrollPane fileScroll = new JScrollPane(fileTable);
		JScrollPane clientScroll = new JScrollPane(clientTable);

		fileScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		clientScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//tablePanel.add(fileDisplay);

		getClientButton=new JButton("Get Clients");
		getClientButton.addActionListener(listen);
		getClientButton.setActionCommand("CLIENT");

		tablePanel.add(fileScroll, BorderLayout.WEST);
		tablePanel.add(clientScroll, BorderLayout.EAST);
		tablePanel.add(getClientButton,BorderLayout.SOUTH);

		FilePane.add(textPanel, BorderLayout.NORTH);
		FilePane.add(tablePanel, BorderLayout.CENTER);

		// END: File Pane

		// BEGIN: Command Pane
		cmdField = new JTextField(70);
		cmdDisplay = new JTextArea(100, 10);
		cmdDisplay.setEditable(false);
		JScrollPane scroll = new JScrollPane(cmdDisplay);

		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		cmdButton = new JButton("Go");
		cmdButton.addActionListener(listen);
		cmdButton.setActionCommand("COMMAND");

		JPanel mini = new JPanel(new FlowLayout());
		mini.add(cmdField);
		mini.add(cmdButton);
		CmdPane.add(mini, BorderLayout.NORTH);
		CmdPane.add(scroll, BorderLayout.CENTER);

		// END: Command Pane

		this.add(ConnectPane, BorderLayout.NORTH);
		this.add(FilePane, BorderLayout.CENTER);
		this.add(CmdPane, BorderLayout.SOUTH);

	}

	private void initFileTable(ArrayList<NapFile> fileList) {

		ArrayList<String[]> listOfFiles=new ArrayList<String[]>();

		for(NapFile f:fileList){
			//System.out.println("File: "+f);
			String[] temp=new String[2];
			temp[0]=f.FILE_NAME;
			temp[1]=f.DESCRIPTION;
			listOfFiles.add(temp);
		}
		String[][] output=new String[listOfFiles.size()][2];
		int i=0;
		for(String[] arr:listOfFiles){
			output[i]=arr;
			i++;
		}

		DefaultTableModel model=new DefaultTableModel(output,colNames);

		fileTable.setModel(model);
		//ConnectPane.add(fileTable);
		//fileTable.setVisible(true);

	}
	private ArrayList<Client> getClientsWithFile(NapFile file){
		ArrayList<Client> out=new ArrayList<Client>();

		if(clientMap!=null){
			for(NapFile f:clientMap.keySet()){
				if(f.equals(file)){
					out=clientMap.get(f);
					break;
				}
			}
			//out=clientMap.get(file);
		}
		return out;
	}

	private void initClientTable(NapFile file) {

		ArrayList<Client> clientList=new ArrayList<Client>();

		clientList=getClientsWithFile(file);

		//System.out.println("Size: "+clientList.size());
		ArrayList<String[]> clientArr=new ArrayList<String[]>();
		for(Client c:clientList){
			String[] temp=new String[3];

			temp[0] = c.CONNECTION_TYPE;
			temp[1] = c.USERNAME;
			temp[2] = c.IP.toString();

			clientArr.add(temp);
		}
		String[][] output=new String[clientArr.size()][3];
		int i=0;
		for(String[] arr:clientArr){
			output[i]=arr;
			i++;
		}

		DefaultTableModel model=new DefaultTableModel(output,clientColNames);

		clientTable.setModel(model);
		//ConnectPane.add(fileTable);
		//fileTable.setVisible(true);

	}

	private void search() {
		fileDisplay.setText("File Name\t\tFile Description\t\tHost\t\tPort\t\tHost Username\tConnection Type\n");
		clientMap = new HashMap<NapFile, ArrayList<Client>>(100);
		ArrayList<Client> clients;
		ArrayList<NapFile> files = new ArrayList<NapFile>();
		String t = "search " + searchField.getText() + "\n";
		Net_Util.send(serverSocket, t);
		try {
			String[] results = Net_Util.recStrArr(serverSocket), split;
			if (results[0].equals("No Results Found")) {
				// output
			} else {
				for (String s : results) {
					clients = new ArrayList<Client>();
					split = s.split("@@");
					for(String r: split)
						fileDisplay.setText(fileDisplay.getText()  + r + "\t\t");
					Client c = new Client(InetAddress.getByName(split[2].substring(1)), Integer.parseInt(split[3]), split[4], split[5]);
					NapFile f = new NapFile(split[0], split[1]);
				
					if (!files.contains(f)) {
						files.add(f);
					}
					if (!clients.contains(c)) {
						clients.add(c);
					}
					if (clientMap.containsKey(f))
						clientMap.get(f).add(c);
					else
						clientMap.put(f, clients);
					fileDisplay.setText(fileDisplay.getText() + "\n");

				}

				initFileTable(files);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		JFrame f = new JFrame("GV-NAPSTER PROGRAM");
		Host h = new Host();

		// centers the frame.
		Box box = new Box(BoxLayout.Y_AXIS);
		box.add(Box.createVerticalGlue());
		box.add(h);
		box.add(Box.createVerticalGlue());
		f.getContentPane().add(box);
		// sets up Jframe
		// This is to intercept the close call "click on x" to send quit to the server
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		f.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (h.serverSocket != null) {
					String a =  "quit\n" ;
					Net_Util.send(h.serverSocket, a);
				}
				System.exit(0);
			}
		});

		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		h.run();
	}

	/*
	 * send file request to another client
	 */
	private void requestFile(NapFile file, Client fileOwner) {
		try {

			File localCopy = new File("./SharedFiles/" + file.FILE_NAME);

			if (localCopy.createNewFile()) {
				cmdDisplay.setText(cmdDisplay.getText() + "\n" + "Connecting to " + fileOwner.IP.toString().substring(1)
						+ ":" + fileOwner.PORT_NUM);
				Socket OwnerSocket = Net_Util.connectToServer(fileOwner.IP.toString().substring(1), fileOwner.PORT_NUM);
				Net_Util.send(OwnerSocket, file.FILE_NAME);
				String[] contents = Net_Util.recStrArr(OwnerSocket);

				BufferedWriter writer = new BufferedWriter(new FileWriter(localCopy));
				for (String lineContent : contents) {
					writer.write(lineContent + "\n");
				}
				writer.close();
				writer = new BufferedWriter(new FileWriter("./SharedFiles/FileList.txt", true));
				writer.newLine();
				writer.write(file.FILE_NAME + "::" + file.DESCRIPTION);
				writer.close();

				Net_Util.send(serverSocket, "register " + file.FILE_NAME);

				cmdDisplay.setText(cmdDisplay.getText() + "\n" + "File " + file.FILE_NAME + " retrieved sucessfully");
			} else {
				cmdDisplay.setText(cmdDisplay.getText() + "\n" + "You already have a file with this name");
			}

		} catch (Exception e) {
			cmdDisplay.setText(cmdDisplay.getText() + "\n" + "Couldn't send connect to file owner");
		}
	}

	private class ClientListner implements ActionListener {

		// @Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand().toLowerCase()) {
			case "connect":
				if (connect()) {

					errorDisplay.setText("");
					makeFileList();
					try {
						if (Net_Util.recString(serverSocket).equals("Client ID Recieved")) {
							cmdDisplay.setText("Connected to server " + serverName.getText());
							sendFileList();
							search();
							cmdDisplay.setText(cmdDisplay.getText() + "\nRetrieve a file by typing: retr \"File Name\" \"Host Username\"");
						} else {
							errorDisplay.setText("Please Try Again");
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} else {
					errorDisplay.setText("Please Try Again.");
				}

				break;
			case "search":
				search();
				System.out.println();
				break;
			case "client":
				int row=fileTable.getSelectedRow();
				if(row==-1){
					errorDisplay.setText("Please Select a File.");
				}
				else{
				String name=(String)fileTable.getValueAt(row,0);
				String desc=(String)fileTable.getValueAt(row,1);
				NapFile file=new NapFile(name,desc);
				initClientTable(file);
				errorDisplay.setText("");
				}
				break;
			case "command":
				cmdDisplay.setText(cmdDisplay.getText() + "\n" + cmdField.getText());
				String[] command = cmdField.getText().split(" ");
				if (command[0].equalsIgnoreCase("retr")) {
					for (NapFile f : clientMap.keySet()) {
						if (f.FILE_NAME.equals(command[1])) {
							for (Client c : clientMap.get(f)) {
								if (c.USERNAME.equals(command[2])) {
									requestFile(f, c);
									System.out.println("file requested");
								}
							}
						}
					}

				} else if (command[0].equalsIgnoreCase("quit")) {
					if (serverSocket != null) {
						String a = "quit\n";
						Net_Util.send(serverSocket, a);
					}
					System.exit(0);
				}
			}

		}
	}

	public void sendFileList() {
		int i = 0;
		String[] fileData = new String[files.size()];
		for (NapFile file : files) {
			fileData[i] = file.toString();
			i++;
		}
		Net_Util.send(serverSocket, fileData);

	}

	private void makeFileList() {

		boolean fileExists;
		File localStorage = new File("./SharedFiles");

		File fileList = new File("./SharedFiles/FileList.txt");
		if (fileList.exists()) {
			FileInputStream fileStream;
			try {
				fileStream = new FileInputStream(fileList);

				String string;
				String[] split;
				BufferedReader inData = new BufferedReader(new InputStreamReader(fileStream));
				while ((string = inData.readLine()) != null) {
					split = string.split("::");
					files.add(new NapFile(split[0], split[1]));

				}
				File[] localFiles = localStorage.listFiles();
				for (int i = 0; i < files.size(); i++) {
					fileExists = false;
					for (File f : localFiles) {
						if (files.get(i).FILE_NAME.equals(f.getName())) {
							fileExists = true;
						}
					}
					if (!fileExists) {
						files.remove(i);
					}
				}
				inData.close();
			} catch (IOException e) {
				try {
					fileList.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private boolean connect() {
		boolean goodData = true, connectionEstablished = false;
		String[] clientData = new String[4];
		if (!userName.getText().isEmpty() && !hostName.getText().isEmpty() && !portNum.getText().isEmpty()) {
			clientData[0] = userName.getText();
			clientData[1] = CONN_TYPE[connectionType.getSelectedIndex()];
			clientData[2] = hostName.getText();
			clientData[3] = portNum.getText();
			// connectionType.getSelectedIndex(); <-- returns an int
			// then do CONN_TYPE[index];
		} else {
			goodData = false;
		}
		if (goodData)
			try {
				serverSocket = Net_Util.connectToServer(serverName.getText(), SERVER_PORT);
				connectionEstablished = true;
				Net_Util.send(serverSocket, clientData);
			} catch (Exception e) {

			}
		return connectionEstablished;
	}

	// }

	// class clientRun implements Runnable {

	/*
	 * recieve file requests and send files
	 */
	// @Override
	public void run() {

		while (true) {
			try {
				Socket requester = Net_Util.welcomeClient(MY_PORT);
				String requestedFile = Net_Util.recString(requester);
				File readFile = new File("./SharedFiles/" + requestedFile);
				InputStream reader = new FileInputStream(readFile);
				ArrayList<String> content = new ArrayList<String>();

				BufferedReader bufRead = new BufferedReader(new InputStreamReader(reader));
				String lineContent = bufRead.readLine();
				while (null != lineContent) {
					System.out.println(lineContent);
					content.add(lineContent);
					lineContent = bufRead.readLine();

				}
				String[] sendFile = new String[content.size()];
				int i = 0;
				for (String s : content)
					sendFile[i++] = s;

				cmdDisplay.setText(cmdDisplay.getText() + "\n" + "sending file " + requestedFile + " to "
						+ requester.getInetAddress());
				Net_Util.send(requester, sendFile);
				cmdDisplay.setText(cmdDisplay.getText() + "\n" + "File sent sucessfully");
				requester.close();
				reader.close();
				bufRead.close();

			} catch (Exception e) {

				cmdDisplay.setText(cmdDisplay.getText() + "\n" + "Issue receiving connection");
				e.printStackTrace();
			}

		}

	}
}
