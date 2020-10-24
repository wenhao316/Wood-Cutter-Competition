package wood.item;

public class WoodItem extends InventoryItem {

    public WoodItem(int value) {
        super(value);
    }

    @Override
    public ItemType getType() {
        return ItemType.WOOD;
    }
}
