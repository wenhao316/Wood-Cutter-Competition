package wood.game;

import wood.graphics.ImageManager;
import wood.strategy.PlayerBoardView;
import wood.tiles.Tile;
import wood.tiles.TileType;

import java.awt.*;

public class GameBoard {
    private Tile[][] board;
    private Point redStartLocation;
    private Point blueStartLocation;

    public GameBoard(Tile[][] tiles) {
        this.board = tiles;
    }

    public int getSize() {
        return board.length;
    }

    public Point getRedStartTileLocation() {
        return redStartLocation;
    }

    public Point getBlueStartTileLocation() {
        return blueStartLocation;
    }

    public void setRedStartLocation(Point redStartLocation) {
        this.redStartLocation = redStartLocation;
    }

    public void setBlueStartLocation(Point blueStartLocation) {
        this.blueStartLocation = blueStartLocation;
    }

    /**
     * Gets the tile at the specified location in Cartesian (x, y) coordinates with (0, 0) as the bottom left tile
     *  and (boardSize - 1, boardSize - 1) as the top right tile
     *
     * @param location A Point representing (x, y) coordinates of the tile to get
     * @return The Tile at the specified location on the board
     */
    public Tile getTileAtLocation(Point location) {
        return getTileAtLocation(location.x, location.y);
    }

    /**
     * Gets the tile at the specified location in Cartesian (x, y) coordinates with (0, 0) as the bottom left tile
     *  and (boardSize - 1, boardSize - 1) as the top right tile
     *
     * @param x The x coordinate of the tile to get
     * @param y The y coordinate of the tile to get
     * @return The Tile at the specified location on the board
     */
    public Tile getTileAtLocation(int x, int y) {
        if(isValidLocation(x, y)) {
            return board[(board.length - 1) - y][x];
        }
        return null;
    }

    /**
     * Updates the tile at the specified location to be the new tile, handling logic like calling Tile.onEnter()
     *  if necessary
     *
     * @param location A Point representing (x, y) coordinates of the tile to set
     * @param newTile The Tile that will be set at the specified location
     */
    public void setTileAtLocation(Point location, Tile newTile) {
        setTileAtLocation(location.x, location.y, newTile);
    }

    /**
     * Updates the tile at the specified location to be the new tile, handling logic like calling Tile.onEnter()
     *  if necessary
     *
     * @param x The x coordinate of the tile to set
     * @param y The y coordinate of the tile to set
     * @param newTile The Tile that will be set at the specified location
     */
    public void setTileAtLocation(int x, int y, Tile newTile) {
        if(isValidLocation(x, y)) {
            Tile oldTile = board[(board.length - 1) - y][x];
            WoodPlayer playerOnTile = oldTile.getPlayerOnTile();

            if(playerOnTile != null) {
                oldTile.onExit(playerOnTile);
                newTile.onEnter(playerOnTile);
                playerOnTile.setCurrentTile(newTile);
            }

            board[(board.length - 1) - y][x] = newTile;
        }
    }

    private boolean isValidLocation(Point location) {
        return isValidLocation(location.x, location.y);
    }

    private boolean isValidLocation(int x, int y) {
        int xIndex = x;
        int yIndex = (board.length - 1) - y;

        boolean xIndexInBounds = (xIndex >= 0 && xIndex < board.length);
        boolean yIndexInBounds = (yIndex >= 0 && yIndex < board.length);

        return xIndexInBounds && yIndexInBounds;
    }

    /**
     * Called every turn for the board to update its internal state
     */
    public void update() {
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board.length; j++) {
                Tile currentTile = board[i][j];
                currentTile.update();
            }
        }
    }

    /**
     * Converts this GameBoard into a restricted information view of the board relative to what one player is
     *  allowed to know
     *
     * @param playerReceivingView The player who will receive this restricted information view
     * @param otherPlayer The other player, so the player receiving the view can know score information
     * @return A PlayerBoardView that contains all the information about this GameBoard for this turn which a
     *          player strategy is allowed to know
     */
    public PlayerBoardView convertToView(WoodPlayer playerReceivingView, WoodPlayer otherPlayer) {
        TileType[][] tileView = new TileType[board.length][board.length];
        for(int i = 0; i < tileView.length; i++) {
            for(int j = 0; j < tileView[i].length; j++) {
                tileView[i][j] = board[i][j].getType();
            }
        }

        Point playerLocation = playerReceivingView.getCurrentTile().getLocation();
        Point otherPlayerLocation = otherPlayer.getCurrentTile().getLocation();

        Point playerLocationCopy = new Point(playerLocation.x, playerLocation.y);
        Point otherLocationCopy = new Point(otherPlayerLocation.x, otherPlayerLocation.y);
        int otherPlayerScore = otherPlayer.getScore();
        int currentTileValue = playerReceivingView.getCurrentTile().getPointValueForPlayer(playerReceivingView);
        return new PlayerBoardView(tileView, playerLocationCopy, otherLocationCopy, otherPlayerScore, currentTileValue);
    }

    /**
     * Specifies how to render this GameBoard on the Graphics object passed in
     *
     * @param brush The Graphics object on which to render this GameBoard
     * @param imageManager The ImageManager object that manages all images for the JPanel component rendering this GameBoard
     */
    public void paint(Graphics2D brush, ImageManager imageManager) {
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                Tile currentTile = board[i][j];
                currentTile.paint(brush, board.length, imageManager);
            }
        }
    }
}
