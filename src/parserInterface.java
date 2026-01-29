import java.util.*;

/**
 * 
 * Interface for the JSON Parser
 * 
 * I will try to have this documented best.
 * 
 */

public interface parserInterface
{

  /**
   * @param JSON Filename
   * @return General items
   */
  public <T> parseFile(String filePath);

  /**
   * exportJSON file
   * 
   * @param orderList
   * @param exportDir - hardcoded to export to exports folder.
   */
  public void exportJSON(T orderList,String exportDir);

}
