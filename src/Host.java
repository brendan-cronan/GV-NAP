import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.io.*;

class Host extends JPanel {

	private static final Dimension WINDOW_SIZE = new Dimension(1000, 700);
	private static final int PORT_NUM = 6603;
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

	// These all belong to the Command Pane
	private JTextField cmdField;
	private JTextArea cmdDisplay;
	private JButton cmdButton;

	private Socket serverSocket;
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
		serverName = new JTextField(20);
		portNum = new JTextField(10);
		userName = new JTextField(20);
		hostName = new JTextField(20);
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
    ConnectPane.add(box,BorderLayout.SOUTH);

    //END: Connect Pane


    // BEGIN: File Pane
    searchField=new JTextField(40);
    JPanel tablePanel=new JPanel(new BorderLayout());
    JPanel textPanel=new JPanel(new FlowLayout());

    fileTable = new JTable();
    clientTable=new JTable();
    fileTable.setVisible(false);
    clientTable.setVisible(false);
    textPanel.add(new JLabel("Search:"));
    textPanel.add(searchField);
    tablePanel.add(fileTable,BorderLayout.WEST);
    tablePanel.add(clientTable,BorderLayout.EAST);
    FilePane.add(textPanel,BorderLayout.NORTH);
    FilePane.add(tablePanel,BorderLayout.CENTER);



    // END: File Pane


		// BEGIN: Command Pane
		cmdField = new JTextField(70);
		cmdDisplay = new JTextArea(100, 10);
		cmdDisplay.setEditable(false);

		cmdButton = new JButton("Go");
		cmdButton.addActionListener(listen);
		cmdButton.setActionCommand("COMMAND");

		JPanel mini = new JPanel(new FlowLayout());
		mini.add(cmdField);
		mini.add(cmdButton);
		CmdPane.add(mini, BorderLayout.NORTH);
		CmdPane.add(cmdDisplay, BorderLayout.CENTER);

		// END: Command Pane

		this.add(ConnectPane, BorderLayout.NORTH);
		this.add(FilePane, BorderLayout.CENTER);
		this.add(CmdPane, BorderLayout.SOUTH);

	}



  private void initFileTable(ArrayList<NapFile> fileList){
    fileData=new String[fileList.size()][2];

    int i=0;
    for(NapFile n:fileList){
      fileData[i][0]=n.FILE_NAME;
      fileData[i][1]=n.DESCRIPTION;
      i++;
    }
    fileTable=new JTable(fileData,colNames);

  }





  private void initClientTable(ArrayList<Client> clientList){
    clientData=new String[clientList.size()][3];
    int i=0;
    for(Client c:clientList){
      clientData[i][0]=c.CONNECTION_TYPE;
      clientData[i][1]=c.USERNAME;
      clientData[i][2]=c.IP.toString();

      i++;
    }
    clientTable=new JTable(clientData,clientColNames);
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
		// sets up Jframe
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);

	}

	private class ClientListner implements ActionListener {

		// @Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand().toLowerCase()) {
			case "connect":
				if (connect()) {
					errorDisplay.setText("");

				} else {
					errorDisplay.setText("Please Try Again.");
				}

				break;

			}

		}

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
				for (NapFile n : files) {
					fileExists = false;
					for (File f : localFiles) {
						if (n.FILE_NAME.equals(f.getName())) {
							fileExists = true;
						}
					}
					if (!fileExists) {
						files.remove(n);
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
		//initFileTable();
	}

	private boolean connect() {
		boolean goodData = true, connectionEstablished = false;
		String[] clientData = new String[3];
		if (!userName.getText().isEmpty() && !hostName.getText().isEmpty() && !portNum.getText().isEmpty()) {
			clientData[0] = userName.getText();
			clientData[1] = CONN_TYPE[connectionType.getSelectedIndex()];
			clientData[2] = hostName.getText();
			// connectionType.getSelectedIndex(); <-- returns an int
			// then do CONN_TYPE[index];
		} else {
			goodData = false;
		}
		if (goodData)
			try {
				serverSocket = new Socket(serverName.getText(), PORT_NUM);
				connectionEstablished = true;
			} catch (Exception e) {

			}
		return connectionEstablished;
	}

}
