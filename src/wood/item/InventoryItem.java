package wood.item;

public abstract class InventoryItem {
    private int value;

    public InventoryItem(int value) {
        this.value = value;
    }

    public abstract ItemType getType();

    public int getValue() {
        return value;
    }
}
