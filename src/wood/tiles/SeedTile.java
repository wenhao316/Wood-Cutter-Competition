package wood.tiles;

import wood.game.WoodPlayer;
import wood.game.TurnAction;
import wood.graphics.ImageManager;
import wood.item.InventoryItem;
import wood.item.SeedItem;
import wood.util.DistanceUtilities;

import java.awt.*;
import java.util.List;

public class SeedTile extends GrassTile {
    private int initialValue;
    private double redValueMultiplier;
    private double blueValueMultiplier;

    public SeedTile(Point tileLocation, int initialValue, Point redStartLocation, Point blueStartLocation, int boardSize) {
        super(tileLocation);
        this.initialValue = initialValue;
        redValueMultiplier = computeTileValueMultiplier(redStartLocation, boardSize);
        blueValueMultiplier = computeTileValueMultiplier(blueStartLocation, boardSize);
    }

    private double computeTileValueMultiplier(Point otherLocation, int boardSize) {
        int manhattanDistance = DistanceUtilities.getManhattanDistance(this.location, otherLocation);
        return (4.0 * boardSize) / ((5.0 * boardSize) - (2.1 * manhattanDistance));
    }

    @Override
    public TileType getType() {
        return TileType.SEED;
    }

    @Override
    public Tile interact(WoodPlayer playerOnTile, TurnAction actionOnTile) {
        if (actionOnTile == TurnAction.PICK_UP) {
            int pointValueForPlayer = getPointValueForPlayer(playerOnTile);
            InventoryItem seedItem = new SeedItem(pointValueForPlayer);
            playerOnTile.addItemToInventory(seedItem);

            return new EmptyTile(this.location);
        }

        return this;
    }

    @Override
    protected List<Image> getImageOverlays(ImageManager imageManager) {
        List<Image> grassOverlays = super.getImageOverlays(imageManager);
        Image treeOverlay = imageManager.getScaledImage("seed");
        grassOverlays.add(treeOverlay);

        return grassOverlays;
    }

    @Override
    public int getPointValueForPlayer(WoodPlayer player) {
        double valueMultiplier = player.isRedPlayer() ? redValueMultiplier : blueValueMultiplier;
        return (int) (valueMultiplier * initialValue);
    }
}
