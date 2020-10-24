package wood.graphics;

import wood.game.GameBoard;
import wood.game.GameEngine;
import wood.game.WoodPlayer;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class UserInterface {
    /**
     * Instantiates a JFrame and all the JPanel components necessary to render the game in real time.
     *  This method must be called from the Event Dispatch Thread
     *
     * @param engine The GameEngine to be rendered in the JFrame created
     * @param preferredGuiWidth The preferred width of the JFrame
     */
    public static void instantiateGUI(GameEngine engine, int preferredGuiWidth) {
        assert SwingUtilities.isEventDispatchThread();

        ImageManager imageManager;
        try {
            String currentDirectoryPath = System.getProperty("user.dir");
            String[] directoriesToImages = new String[]{currentDirectoryPath, "src", "wood", "image_files/"};
            String imagesDirectoryPath = String.join(File.separator, directoriesToImages);
            imageManager = new ImageManager(imagesDirectoryPath);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading image resources");
            engine.setGuiEnabled(false);
            return;
        }

        // Create the application window that displays the game
        JFrame gameDisplayFrame = new JFrame("Wood: The Gathering");
        gameDisplayFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameDisplayFrame.setLayout(new BoxLayout(gameDisplayFrame.getContentPane(), BoxLayout.Y_AXIS));
        gameDisplayFrame.setResizable(false);
        gameDisplayFrame.setVisible(true);

        // Create the scoreboard panel first so the BoxLayout renders it on top
        WoodPlayer redPlayer = engine.getRedPlayer();
        WoodPlayer bluePlayer = engine.getBluePlayer();
        GameBoard board = engine.getBoard();
        PlayerScoreDisplayPanel scoreDisplayPanel = new PlayerScoreDisplayPanel(preferredGuiWidth, board.getSize(),
                redPlayer, bluePlayer);
        gameDisplayFrame.add(scoreDisplayPanel);

        // Create the panel that actually renders the game board
        GameBoardDisplayPanel gameBoardDisplayPanel = new GameBoardDisplayPanel(preferredGuiWidth, board, imageManager);
        gameDisplayFrame.add(gameBoardDisplayPanel);

        // Tell the application window to resize its contents to their preferred dimensions
        gameDisplayFrame.pack();

        // Set these panels to update when the engine tells them something changed
        engine.addObserver(gameBoardDisplayPanel);
        engine.addObserver(scoreDisplayPanel);
    }
}
