import java.util.*;
import java.net.*;
import java.io.*;


class Client{

  public final InetAddress IP;
  public final int PORT_NUM;
  public final String USERNAME;
  public final String CONNECTION_TYPE;

  public Client(InetAddress ip,int port,String name,String connectionType){
    IP=ip;
    PORT_NUM=port;
    USERNAME=name;
    CONNECTION_TYPE=connectionType;
  }

  public boolean equals(Client c){
    if(this.IP==c.IP && this.PORT_NUM==c.PORT_NUM && this.USERNAME==c.USERNAME
    && this.CONNECTION_TYPE== c.CONNECTION_TYPE){
      return true;
    }
    return false;
  }


}