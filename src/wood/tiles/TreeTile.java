package wood.tiles;

import wood.game.WoodPlayer;
import wood.game.TurnAction;
import wood.graphics.ImageManager;
import wood.item.InventoryItem;
import wood.item.WoodItem;

import java.awt.*;
import java.util.List;

public class TreeTile extends GrassTile {
    private static final int[] treeGrowthMilestones = new int[]{50, 100, 200, Integer.MAX_VALUE};
    private static final String[] treeImageNames = new String[]{"tree_small", "tree_medium", "tree_large", "tree_massive"};

    protected int woodValue;

    public TreeTile(Point tileLocation, int initialWoodValue) {
        super(tileLocation);
        this.woodValue = initialWoodValue;
    }

    @Override
    public TileType getType() {
        return TileType.TREE;
    }

    @Override
    public Tile interact(WoodPlayer playerOnTile, TurnAction actionOnTile) {
        if (actionOnTile == TurnAction.CUT_TREE) {
            InventoryItem cutWoodItem = new WoodItem(woodValue);
            playerOnTile.addItemToInventory(cutWoodItem);

            return new EmptyTile(this.location);
        }

        return this;
    }

    @Override
    public void update() {
        this.woodValue += 1;
    }

    @Override
    protected List<Image> getImageOverlays(ImageManager imageManager) {
        List<Image> grassOverlays = super.getImageOverlays(imageManager);

        // Find what stage of growth the tree is at
        int treeGrowthStage = 0;
        while(this.woodValue > treeGrowthMilestones[treeGrowthStage]) {
            treeGrowthStage++;
        }

        Image treeOverlay = imageManager.getScaledImage(treeImageNames[treeGrowthStage]);
        grassOverlays.add(treeOverlay);
        return grassOverlays;
    }

    @Override
    public int getPointValueForPlayer(WoodPlayer player) {
        return this.woodValue;
    }
}
