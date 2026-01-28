

 /**

    Interface for the JSON Parser

    I will try to have this documented best.

  */


 public interface json_parser_interface {

     /**
      * @param JSON Filename
      * @return General items
      */
     public T parseFile(fileName);


     /** exportJSON file
      * @param argument for export directory. current directory is default.
      */
     public void exportJSON(exportDir);


}