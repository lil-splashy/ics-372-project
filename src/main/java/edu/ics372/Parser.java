package edu.ics372;
import java.util.ArrayList;
import java.util.List;

public class Parser implements ParserInterface {

    private String filePath = "";

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public void setNewPath(String newPath) {
        this.filePath = newPath;
    }

    @Override
    public List<Order> parseFile(String filePath) {
        ParserInterface parser = resolveParser(filePath);
        if (parser == null) return new ArrayList<>();
        return parser.parseFile(filePath);
    }



    @Override
    public void exportOrders(List<Order> orders, String filePath) {
        ParserInterface parser = resolveParser(filePath);
        if (parser == null) return;
        parser.exportOrders(orders, filePath);
    }

    /**
     * Determines the parser to be used on a given file
     *
     * @param filePath The file to be parsed
     * @return A parser that implements the ParserInterface
     */
    private ParserInterface resolveParser(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            System.out.println("FileParser: file path is empty.");
            return null;
        }

        String lower = filePath.toLowerCase();

        if (lower.endsWith(".json")) {
            return new JsonParser();
        } else if (lower.endsWith(".xml")) {
            return new XmlParser();
        } else {
            System.out.println("FileParser: unsupported file type for \"" + filePath + "\". Use .json or .xml");
            return null;
        }
    }
}
