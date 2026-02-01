import java.util.*;

/**
 * 
 * Interface for the JSON Parser
 * 
 * I will try to have this documented best.
 * 
 */

public interface ParserInterface {


  /**
   *
   * @return current json filepath
   */
  String getFilePath();


  /**
   *
   * @param newPath - new directory path to set to
   */
  void setNewPath(String newPath);



  /**
   * @param JSON Filename
   * @return New Order
   */
  Order parseFile(String filePath);

  /**
   * exportJSON file
   * 
   * @param orderList
   * @param exportDir - hardcoded to export to exports folder.
   */
//  String exportJSON(Order orderList,String exportDir);

}
