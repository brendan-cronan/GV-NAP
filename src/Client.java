import java.util.*;
import java.net.*;
import java.io.*;

class Client{

  public final InetAddress IP;
  public final int PORT_NUM;
  public final String USERNAME;
  public final ConnectionType CONNECTION_TYPE;

  public Client(InetAddress ip,int port,String name,ConnectionType c){
    IP=ip;
    PORT_NUM=port;
    USERNAME=name;
    CONNECTION_TYPE=c;
  }
}
