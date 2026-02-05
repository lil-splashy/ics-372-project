

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



  /// @param filePath -
  /// @return New Order
  Order parseFile(String filePath);

  /**
   * @param order - Order to be exported
   * @param exportDir - Desired filepath for export
   */
  void exportJSON(Order order,String exportDir);

}
