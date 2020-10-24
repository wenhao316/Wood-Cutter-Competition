package wood.tiles;

import wood.game.WoodPlayer;
import wood.graphics.ImageManager;
import wood.item.InventoryItem;
import wood.item.ItemType;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class StartTile extends Tile {
    boolean isRedStartTile;

    public StartTile(Point tileLocation, boolean isRedStartTile) {
        super(tileLocation);
        this.isRedStartTile = isRedStartTile;
    }

    @Override
    public TileType getType() {
        return TileType.START;
    }

    @Override
    public void onEnter(WoodPlayer playerEnteringTile) {
        super.onEnter(playerEnteringTile);

        boolean redPlayerOnRedStart = isRedStartTile && playerEnteringTile.isRedPlayer();
        boolean bluePlayerOnBlueStart = !isRedStartTile && !playerEnteringTile.isRedPlayer();
        if(!redPlayerOnRedStart && !bluePlayerOnBlueStart) {
            // Wrong start tile for this player, don't do anything
            return;
        }

        InventoryItem woodFromInventory = playerEnteringTile.removeFirstItemOfType(ItemType.WOOD);
        while (woodFromInventory != null) {
            playerEnteringTile.addToScore(woodFromInventory.getValue());
            woodFromInventory = playerEnteringTile.removeFirstItemOfType(ItemType.WOOD);
        }
    }

    @Override
    protected List<Image> getImageOverlays(ImageManager imageManager) {
        String backgroundTileName = isRedStartTile ? "red_start" : "blue_start";

        if(!imageManager.hasScaledImageWithName(backgroundTileName)) {
            Color backgroundColor = isRedStartTile ? new Color(179, 0, 0) : new Color(0, 0, 179);;
            ImageIcon backgroundImage = imageManager.generateBackgroundImageIcon(backgroundColor);
            imageManager.addImageFromIcon(backgroundTileName, backgroundImage);
        }

        List<Image> startTileOverlays = new LinkedList<>();
        startTileOverlays.add(imageManager.getScaledImage(backgroundTileName));
        startTileOverlays.add(imageManager.getScaledImage("house"));
        return startTileOverlays;
    }
}
