package wood.game;

import wood.tiles.EmptyTile;
import wood.tiles.SeedTile;
import wood.tiles.StartTile;
import wood.tiles.Tile;
import wood.tiles.TileType;

import java.awt.Point;
import java.util.Random;

public class WorldGeneration {
    private static final Random rng = new Random();
    private static final int SEED_INITIAL_VALUE_MIN = 3;
    private static final int SEED_INITIAL_VALUE_MAX = 12;

    /**
     * Sets the random number generator's seed. Not to be confused with seeds in the game, giving a random number
     *  generator a seed value means that it will always generate the same "random" sequence whenever it is provided
     *  with the same value for a seed. This allows use to generate an unpredictable GameBoard, but also generate
     *  that same exact GameBoard if necessary for replaying a match / reproducing bugs / other purposes
     *
     * @param newSeedValue The value to set as the random number generator's seed
     */
    protected static void setRandomNumberSeed(long newSeedValue) {
        rng.setSeed(newSeedValue);
    }

    /**
     * Generates a GameBoard by filling it with empty tiles, adding start tiles, and then generating seed tiles
     *
     * @param boardSize The size of the board to generate
     * @return A GameBoard object ready for use in a round of Wood: The Gathering
     */
    protected static GameBoard generateBoard(int boardSize) {
        // Fill board with empty tiles to begin with
        Tile[][] tilesOnBoard = new Tile[boardSize][boardSize];
        for (int i = 0; i < tilesOnBoard.length; i++) {
            for (int j = 0; j < tilesOnBoard[i].length; j++) {
                // The top left corner is index (0, 0) but location (0, maxY)
                // This is so MOVE_UP actually moves up relative to the bottom of the screen
                Point tileLocation = new Point(j, (boardSize - 1) - i);
                tilesOnBoard[i][j] = new EmptyTile(tileLocation);
            }
        }

        GameBoard board = new GameBoard(tilesOnBoard);
        addStartTiles(board);
        generateInitialSeedTiles(board);

        return board;
    }

    private static void addStartTiles(GameBoard board) {
        Point redStartPoint = new Point(0, 0);
        Point blueStartPoint = new Point(board.getSize() - 1, board.getSize() - 1);
        Tile redStartTile = new StartTile(redStartPoint, true);
        Tile blueStartTile = new StartTile(blueStartPoint, false);

        board.setTileAtLocation(redStartPoint, redStartTile);
        board.setTileAtLocation(blueStartPoint, blueStartTile);
        board.setRedStartLocation(redStartPoint);
        board.setBlueStartLocation(blueStartPoint);
    }

    private static void generateInitialSeedTiles(GameBoard board) {
        // Ensure the board is an even size so it has non-overlapping quadrants
        int boardSize = board.getSize();
        assert (boardSize % 2 == 0);

        // To reduce initial variation, 1/4th of the seeds are generated in each quadrant of the board
        int halfBoard = boardSize / 2;
        generateSeedsForQuadrant(board, new Point(0, 0), new Point(halfBoard - 1, halfBoard - 1));
        generateSeedsForQuadrant(board, new Point(0, boardSize - 1), new Point(halfBoard - 1, halfBoard));
        generateSeedsForQuadrant(board, new Point(boardSize - 1, 0), new Point(halfBoard, halfBoard - 1));
        generateSeedsForQuadrant(board, new Point(boardSize - 1, boardSize - 1), new Point(halfBoard, halfBoard));
    }

    private static void generateSeedsForQuadrant(GameBoard board, Point firstCorner, Point secondCorner) {
        int numSeedsToGenerate = board.getSize() / 2;

        for(int i = 0; i < numSeedsToGenerate; i++) {
            generateNewSeedTile(board, firstCorner, secondCorner);
        }
    }

    /**
     * Generates a new seed tile in the rectangle formed between the provided locations
     *
     * @param board The GameBoard object on which to generate the SeedTiles
     * @param location1 The first point defining one corner of the rectangular area in which to generate seeds
     * @param location2 The second point defining the other corner of the rectangular area
     */
    protected static void generateNewSeedTile(GameBoard board, Point location1, Point location2) {
        int minX = Math.min(location1.x, location2.x);
        int minY = Math.min(location1.y, location2.y);
        int maxX = Math.max(location1.x, location2.x);
        int maxY = Math.max(location1.y, location2.y);

        // Get a random empty tile location
        int randomX;
        int randomY;
        do {
            randomX = rng.nextInt(maxX - minX + 1) + minX;
            randomY = rng.nextInt(maxY - minY + 1) + minY;
        } while (board.getTileAtLocation(randomX, randomY).getType() != TileType.EMPTY);

        // Plop a seed down at the random tile with a random initial value
        Point randomLocation = new Point(randomX, randomY);
        int randomSeedValue = rng.nextInt(SEED_INITIAL_VALUE_MAX - SEED_INITIAL_VALUE_MIN + 1) + SEED_INITIAL_VALUE_MIN;
        Point redStart = board.getRedStartTileLocation();
        Point blueStart = board.getBlueStartTileLocation();
        int boardSize = board.getSize();
        board.setTileAtLocation(randomLocation, new SeedTile(randomLocation, randomSeedValue, redStart, blueStart, boardSize));
    }
}
