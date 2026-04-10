package edu.ics372;

import javafx.application.Platform;
import java.nio.file.*;

public class OrderDirectoryWatcher {

    // The folder we are watching for updates (folderPath: src/main/orders/incoming) or whatever we want it to be
    private final Path folderPath;

    // OrderHandler so use the same instance so dealing with same orders
    private final OrderHandler handler;

    // Function that runs when a new order is added when show(stage, handler) is called
    private final Runnable onNewOrder;

    // takes a file path, handler, and a lambda that runs show(stage, handler)
    public OrderDirectoryWatcher(String path, OrderHandler handler, Runnable onNewOrder) {
        // Sets folder path
        this.folderPath = Paths.get(path);

        // Sets handler to handler so it's the same instance with the same orders in it
        this.handler = handler;

        // Save the UI refresh function
        this.onNewOrder = onNewOrder;
    }

    // Starts the background watcher
    public void startWatching() {

        // Run this on a separate thread so it doesn't goof up UI
        new Thread(() -> {
            try {
                // WatchService = Java tool that detects file changes in a folder clutch
                WatchService watchService = FileSystems.getDefault().newWatchService();

                // Register the folder to listen for new files being created
                folderPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

                System.out.println("Watching folder: " + folderPath);

                // Infinite loop that keeps watching for changes forever while program is running
                while (true) {

                    // Wait until something happens
                    WatchKey key = watchService.take();

                    // Loop through all events which are the files created
                    for (WatchEvent<?> event : key.pollEvents()) {

                        // Get the name of the file that was created
                        Path fileName = (Path) event.context();

                        // Convert it into full path of folder + filename
                        Path fullPath = folderPath.resolve(fileName);

                        // Only process JSON or XML files since that's all we can do right now
                        // idk probably bad practice for if we can parse a lot more since would have to add a lot of or ||
                        if (fileName.toString().endsWith(".json") || fileName.toString().endsWith(".xml")) {

                            System.out.println("New file: " + fullPath);

                            // Small delay so file finishes writing
                            Thread.sleep(300);

                            // Load the orders from this file into system
                            handler.loadOrders(fullPath.toString());

                            // JavaFX UI must be updated on the UI thread else uh oh
                            // So Platform.runLater helps update UI from this background thread
                            Platform.runLater(onNewOrder);
                        }
                    }

                    // Reset the key so it continues listening
                    key.reset();
                }

            } catch (Exception e) {
                // Print errors if something goes wrong cause shout out debugging tools
                e.printStackTrace();
            }
        }).start(); // Start the thread :D
    }
}