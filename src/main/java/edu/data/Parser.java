package edu.ics372;

import java.util.List;

/**
 * Dispatcher that selects the correct parser (JSON or XML) based on file extension.
 * Implements ParserInterface so callers can use it transparently.
 */
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
    public Order parseFile(String filePath) {
        ParserInterface parser = resolveParser(filePath);
        if (parser == null) return null;
        return parser.parseFile(filePath);
    }


    @Override
    public void exportJSON(List<Order> orders, String filePath) {
        ParserInterface parser = resolveParser(filePath);
        if (parser == null) return;
        parser.exportJSON(orders, filePath);
    }

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
