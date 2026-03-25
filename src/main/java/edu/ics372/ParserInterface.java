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
   * @param filePath The file path of the file to be parsed
   * @return List of orders contained within the given file
   */
  List<Order> parseFile(String filePath);


     /**
     * @param orders - List of Orders to be exported
     * @param exportDir - The directory in which the file containing the orders will be located in
     */
  void exportOrders(List<Order> orders, String exportDir);


}

