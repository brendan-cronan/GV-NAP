import java.util.*;
import java.net.*;
import java.io.*;


class Client{

  public final InetAddress IP;
  public final String USERNAME;
  public final String CONNECTION_TYPE;

  public Client(InetAddress ip,int port,String name,String connectionType){
    IP=ip;
    USERNAME=name;
    CONNECTION_TYPE=connectionType;
  }

  public boolean equals(Client c){
    if(this.IP==c.IP  && this.USERNAME.equals(c.USERNAME)
    && this.CONNECTION_TYPE.equals(c.CONNECTION_TYPE)){
      return true;
    }
    return false;
  }


}
