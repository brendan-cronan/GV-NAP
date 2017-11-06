import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Host extends JPanel{


  private static final Dimension windowSize=new Dimension(500, 700);
  private static final int PORT_NUM=6603;

  private JPanel ConnectPane;
  private JPanel FilePane;
  private JPanel CmdPane;
  private 


  public Host(){
    this.setLayout(new BorderLayout());
    this.setPreferredSize(windowSize);
    //setBorder(BorderFactory.createCompoundBorder(
    //                    BorderFactory.createTitledBorder(""),
    //                    BorderFactory.createEmptyBorder(5,5,5,5)));
    ConnectPane=new JPanel(new BorderLayout());
    FilePane=new JPanel(new BorderLayout());
    CmdPane=new JPanel(new BorderLayout());
    this.add(ConnectPane);
    this.add(FilePane);
    this.add(CmdPane);

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

        }








  }
}
