import java.io.*;
import java.util.*;
class NapFile{

  public final String DESCRIPTION;
  public final String FILE_NAME;

  public NapFile(String name,String description){
    FILE_NAME=name;
    DESCRIPTION=description;
  }
  public boolean equals(NapFile other){
    if(this.DESCRIPTION==other.DESCRIPTION && this.FILE_NAME==other.FILE_NAME)
      return true;
    return false;
  }

}
