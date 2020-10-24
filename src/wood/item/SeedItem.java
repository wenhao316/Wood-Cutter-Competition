package wood.item;

public class SeedItem extends InventoryItem {

    public SeedItem(int value) {
        super(value);
    }

    @Override
    public ItemType getType() {
        return ItemType.SEED;
    }
}
