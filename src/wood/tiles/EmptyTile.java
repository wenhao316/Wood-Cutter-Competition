package wood.tiles;

import wood.game.WoodPlayer;
import wood.game.TurnAction;
import wood.item.InventoryItem;
import wood.item.ItemType;

import java.awt.*;

public class EmptyTile extends GrassTile {

    public EmptyTile(Point tileLocation) {
        super(tileLocation);
    }

    @Override
    public TileType getType() {
        return TileType.EMPTY;
    }

    @Override
    public Tile interact(WoodPlayer playerOnTile, TurnAction actionOnTile) {
        if (actionOnTile == TurnAction.PLANT_SEED) {
            InventoryItem firstSeed = playerOnTile.removeFirstItemOfType(ItemType.SEED);

            if (firstSeed != null) {
                // Player has a seed, plant it
                return new TreeTile(this.location, firstSeed.getValue());
            }
        }

        return this;
    }
}
