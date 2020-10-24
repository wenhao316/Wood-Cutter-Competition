package wood.strategy;

import wood.tiles.TileType;
import wood.util.DistanceUtilities;

import java.awt.*;

public class PlayerBoardView {
    private TileType[][] tiles;
    private Point thisPlayerLocation;
    private Point otherPlayerLocation;
    private int otherPlayerScore;
    private int currentTileValue;

    public PlayerBoardView(TileType[][] tiles, Point thisPlayerLocation, Point otherPlayerLocation,
                           int otherPlayerScore, int currentTileValue) {
        this.tiles = tiles;
        this.thisPlayerLocation = thisPlayerLocation;
        this.otherPlayerLocation = otherPlayerLocation;
        this.otherPlayerScore = otherPlayerScore;
        this.currentTileValue = currentTileValue;
    }

    /**
     * Gets the type of tile at the specified location
     *
     * @param location The Point at which to get the type of the tile
     * @return The type of tile at the location, or null if the location is outside the board
     */
    public TileType getTileTypeAtLocation(Point location) {
        return this.getTileTypeAtLocation(location.x, location.y);
    }

    /**
     * Gets the type of tile at the specified (x, y) coordinates
     *
     * @param x The x coordinate of the tile to get
     * @param y The y coordinate of the tile to get
     * @return The type of tile at the (x, y) Cartesian coordinates, or null if the coordinates are outside the board
     */
    public TileType getTileTypeAtLocation(int x, int y) {
        // The index on the board, and the Cartesian coordinates are mirror opposites in the y direction
        int xIndex = x;
        int yIndex = (tiles.length - 1) - y;

        boolean xIndexInBounds = (xIndex >= 0 && xIndex < tiles.length);
        boolean yIndexInBounds = (yIndex >= 0 && yIndex < tiles.length);
        if(xIndexInBounds && yIndexInBounds) {
            return tiles[yIndex][xIndex];
        }

        return null;
    }

    /**
     * Gives you your location so you don't have to keep track of when you actually successfully move or not
     *
     * @return A Point representing your player's location, (0, 0) is the bottom left of the board
     */
    public Point getYourLocation() {
        return this.thisPlayerLocation;
    }

    /**
     * Gets the other player's exact location but only if they are within viewing range of your player.
     *
     * @return A Point representing your opponent's location, or null if they are outside viewing range
     */
    public Point getOtherPlayerLocation() {
        // The player can see ~1/3rd of the length and width of the board
        int maxViewDistance = tiles.length / 3;

        int distanceToOtherPlayer = DistanceUtilities.getManhattanDistance(thisPlayerLocation, otherPlayerLocation);
        if(distanceToOtherPlayer <= maxViewDistance) {
            return otherPlayerLocation;
        }

        // Too far away to see :(
        return null;
    }

    /**
     * Gets the score of the other player. If you want to know your score you have to track that for yourself
     *
     * @return The current score of the other player
     */
    public int getOtherPlayerScore() {
        return otherPlayerScore;
    }

    /**
     * You can only see the exact value of a tile when your player is standing on top of it. This is mostly applicable
     *  for seed and tree tiles where the value of a seed is (initial value * multiplier for distance from your start
     *  square) and the value of a tree is (value of seed when planted + number of turns since planting)
     *
     * @return The value of the tile your player is standing on, or 0 if the tile has no value
     */
    public int getCurrentTileValue() {
        return this.currentTileValue;
    }
}