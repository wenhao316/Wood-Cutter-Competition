package wood.tiles;

import wood.game.WoodPlayer;
import wood.game.TurnAction;
import wood.graphics.ImageManager;

import java.awt.*;
import java.util.List;

public abstract class Tile {
    WoodPlayer playerOnTile;
    Point location;

    protected Tile(Point tileLocation) {
        this.playerOnTile = null;
        this.location = tileLocation;
    }

    public WoodPlayer getPlayerOnTile() {
        return playerOnTile;
    }

    public Point getLocation() {
        return location;
    }

    /**
     * @return The TileType enum that represents this Tile
     */
    public abstract TileType getType();

    /**
     * Called when an action is performed on a Tile by a player to determine
     *   A. What happens for that action and B. What changes are made to the Tile as a result of that action
     *
     * @param playerOnTile The player performing the action on this tile
     * @param actionOnTile The TurnAction being performed on this tile
     * @return A Tile representing what the Tile at this location on the game board should be set to after this turn.
     *          Return this Tile if no change should happen
     *          Return a new Tile of the next type if the action does result in a change on the tile
     */
    public Tile interact(WoodPlayer playerOnTile, TurnAction actionOnTile) {
        return this;
    }

    /**
     * Called every turn for tiles to update their internal state
     */
    public void update() {
        // By default tiles don't do anything
    }

    /**
     * Gets the point value of this Tile (most likely corresponding to the item you get from an action on the tile)
     * This value may or may not change based on what player is passed in
     *
     * @param player The player to get the point value for
     * @return The corresponding point value of the tile for the player
     */
    public int getPointValueForPlayer(WoodPlayer player) {
        return 0; // By default, tiles aren't worth anything
    }

    /**
     * Called when a player enters this tile on a turn
     *
     * @param playerEnteringTile The player entering the tile
     */
    public void onEnter(WoodPlayer playerEnteringTile) {
        playerOnTile = playerEnteringTile;
    }

    /**
     * Called when a player exits a tile on a turn. The player parameter is somewhat redundant,
     *
     * @param playerExitingTile The player leaving the tile.
     */
    public void onExit(WoodPlayer playerExitingTile) {
        playerOnTile = null;
    }

    /**
     * Gets the List of Images that encodes how to render this Tile
     * Images later in the List will be rendered on top of Images earlier in the List
     *
     * @param imageManager The ImageManager object that manages all images for the JPanel component rendering this Tile
     * @return The List of Images in order of rendering on top of each other
     */
    protected abstract List<Image> getImageOverlays(ImageManager imageManager);

    /**
     * Specifies how to render this Tile on the Graphics object passed in
     *
     * @param brush The Graphics object on which to render this Tile
     * @param boardSize The size of the board for use in finding the starting point on the screen to render
     * @param imageManager The ImageManager object that manages all images for the JPanel component rendering this Tile
     */
    public void paint(Graphics2D brush, int boardSize, ImageManager imageManager) {
        int imageWidth = imageManager.getImageWidth();
        int imageHeight = imageManager.getImageHeight();
        Point screenIndex = this.getScreenIndexFromLocation(imageWidth, imageHeight, boardSize);

        // Draw overlays
        for(Image overlayToDraw : this.getImageOverlays(imageManager)) {
            brush.drawImage(overlayToDraw, screenIndex.x, screenIndex.y, null);
        }

        // Draw players if they're standing on this tile
        if(playerOnTile != null) {
            Image playerImage = playerOnTile.getImage(imageManager);
            brush.drawImage(playerImage, screenIndex.x, screenIndex.y, null);
        }
    }

    /**
     * Computes the (x, y) coordinates of the pixel on the screen corresponding to the top left of this Tile
     *
     * @param imageWidth The width of the image to be rendered
     * @param imageHeight The height of the image to be rendered
     * @param boardSize The size of the board
     * @return The (x, y) coordinates on the screen of the top left image pixel
     */
    protected Point getScreenIndexFromLocation(int imageWidth, int imageHeight, int boardSize) {
        int xScreenIndex = this.location.x * imageWidth;
        int yScreenIndex = ((boardSize - 1) - location.y) * imageHeight;
        return new Point(xScreenIndex, yScreenIndex);
    }
}
