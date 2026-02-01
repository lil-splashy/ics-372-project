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
   * @param argument for export directory. current directory is default.
   */
  public <T> exportJSON(T exportDir);

}
