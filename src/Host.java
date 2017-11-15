import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;



class Host extends JPanel{


  private static final Dimension WINDOW_SIZE = new Dimension(1000, 700);
  private static final int PORT_NUM=6603;

  private JPanel ConnectPane;
  private JPanel FilePane;
  private JPanel CmdPane;

  //These all belong to the Connect Pane
  private JLabel errorDisplay;
  private JTextField serverName;
  private JTextField portNum;
  private JTextField userName;
  private JTextField hostName;
  private JTextField connectionType;
  private JButton connectButton;

  //These all belong to the Search Pane


  //These all belong to the Command Pane
  private JTextField cmdField;
  private JTextArea cmdDisplay;
  private JButton cmdButton;



  private Socket serverSocket;


  public Host(){
    this.setLayout(new BorderLayout());
    this.setPreferredSize(WINDOW_SIZE);

    //JPanel Init
    ConnectPane=new JPanel(new BorderLayout());
    FilePane=new JPanel(new BorderLayout());
    CmdPane=new JPanel(new BorderLayout());

    /* To see the size of the panels
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
    FilePane.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder("Keyword Search"),
      BorderFactory.createEmptyBorder(5,5,5,5)));
    CmdPane.setBorder(BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder("FTP"),
      BorderFactory.createEmptyBorder(5,5,5,5)));


    ClientListner listen =new ClientListner();



    //BEGIN: Connect Pane

    errorDisplay=new JLabel("");
    errorDisplay.setForeground(Color.RED);
    serverName=new JTextField(20);
    portNum=new JTextField(10);
    userName=new JTextField(20);
    hostName=new JTextField(20);
    connectionType=new JTextField(10);

    connectButton=new JButton("Connect");
    connectButton.addActionListener(listen);
    connectButton.setActionCommand("CONNECT");

    JPanel miniConnect=new JPanel(new FlowLayout());

    miniConnect.add(new JLabel("Name of Server:"));
    miniConnect.add(serverName);
    miniConnect.add(new JLabel("Port Number:"));
    miniConnect.add(portNum);
    miniConnect.add(new JLabel("Username:"));
    miniConnect.add(userName);
    miniConnect.add(new JLabel("Host Name:"));
    miniConnect.add(hostName);
    miniConnect.add(connectButton);

    ConnectPane.add(miniConnect,BorderLayout.CENTER);


    Box box = new Box(BoxLayout.X_AXIS);
		box.add(Box.createHorizontalGlue());
		box.add(errorDisplay);
		box.add(Box.createHorizontalGlue());
    ConnectPane.add(box,BorderLayout.SOUTH);

    //END: Connect Pane


    // BEGIN: File Pane

    // END: File Pane


    // BEGIN: Command Pane
    cmdField=new JTextField(70);
    //HELLOO
    cmdDisplay=new JTextArea(100,10);
    cmdDisplay.setEditable(false);

    cmdButton=new JButton("Go");
    cmdButton.addActionListener(listen);
    cmdButton.setActionCommand("COMMAND");

    JPanel mini=new JPanel(new FlowLayout());
    mini.add(cmdField);
    mini.add(cmdButton);
    CmdPane.add(mini,BorderLayout.NORTH);
    CmdPane.add(cmdDisplay,BorderLayout.CENTER);




    // END: Command Pane



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






  }

  private class ClientListner implements ActionListener {


		    //@Override
		    public void actionPerformed(ActionEvent e) {
          switch(e.getActionCommand().toLowerCase()){
            case "connect":
              errorDisplay.setText("Please Try Again.");
              //errorDisplay.setText("");
              hostName.getText();
              break;


          }


        }








  }
}
