import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;


class Host extends JPanel{


  private static final Dimension windowSize=new Dimension(1000, 700);
  private static final int PORT_NUM=6603;

  private JPanel ConnectPane;
  private JPanel FilePane;
  private JPanel CmdPane;


  private JTextField serverName;
  private JTextField portNum;
  private JTextField userName;
  private JTextField hostName;

  private JButton connectButton;







  private Socket serverSocket;


  public Host(){
    this.setLayout(new BorderLayout());
    this.setPreferredSize(windowSize);
    ConnectPane=new JPanel(new FlowLayout());
    FilePane=new JPanel(new BorderLayout());
    CmdPane=new JPanel(new BorderLayout());

    ConnectPane.setBorder(BorderFactory.createCompoundBorder(
                       BorderFactory.createTitledBorder("Connect"),
                       BorderFactory.createEmptyBorder(5,5,5,5)));




    ClientListner listen =new ClientListner();





    serverName=new JTextField(20);
    portNum=new JTextField(10);
    userName=new JTextField(20);
    hostName=new JTextField(20);

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






  }

  private class ClientListner implements ActionListener {


		    //@Override
		    public void actionPerformed(ActionEvent e) {
          switch(e.getActionCommand().toLowerCase()){
            case "connect":
              hostName.getText();
              break;


          }


        }








  }
}
