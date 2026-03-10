package edu.ics372;
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
     * @param orders - List of Orders to be exported
     * @param filePath - Desired filepath for export
     */
  void exportJSON(List<Order> orders, String exportDir);

}
