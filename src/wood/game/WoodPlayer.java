package wood.game;

import wood.graphics.ImageManager;
import wood.item.InventoryItem;
import wood.item.ItemType;
import wood.strategy.WoodPlayerStrategy;
import wood.tiles.Tile;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class WoodPlayer {
    protected static final int MAX_ITEMS = 5;
    private static final Map<TurnAction, String> moveToDirectionMap = new HashMap<>();
    static {
        moveToDirectionMap.put(TurnAction.MOVE_UP, "back");
        moveToDirectionMap.put(TurnAction.MOVE_DOWN, "front");
        moveToDirectionMap.put(TurnAction.MOVE_LEFT, "left");
        moveToDirectionMap.put(TurnAction.MOVE_RIGHT, "right");
    }

    private WoodPlayerStrategy strategy;
    private List<InventoryItem> inventory;
    private Tile currentTile;
    private boolean isRedPlayer;
    private int score;
    private TurnAction lastMove;
    private List<TurnAction> actions;

    public WoodPlayer(WoodPlayerStrategy strategy, Tile startingTile, boolean isRedPlayer) {
        this.strategy = strategy;
        this.inventory = new ArrayList<>(MAX_ITEMS);
        this.currentTile = startingTile;
        this.isRedPlayer = isRedPlayer;
        this.score = 0;
        this.lastMove = TurnAction.MOVE_DOWN;
        this.actions = new LinkedList<>();
    }

    public WoodPlayerStrategy getStrategy() {
        return strategy;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(Tile newTile) {
        this.currentTile = newTile;
    }

    public void addItemToInventory(InventoryItem itemToAdd) {
        if (inventory.size() < MAX_ITEMS) {
            this.strategy.receiveItem(itemToAdd);
            inventory.add(itemToAdd);
        }
    }

    public InventoryItem removeFirstItemOfType(ItemType typeToRemove) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getType() == typeToRemove) {
                return inventory.remove(i);
            }
        }

        return null;
    }

    public void addToScore(int amountToAdd) {
        this.score += amountToAdd;
    }

    public boolean isRedPlayer() {
        return isRedPlayer;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int newScore) {
        this.score = newScore;
    }

    public void addTurnAction(TurnAction actionTaken) {
        actions.add(actionTaken);

        if(actionTaken == TurnAction.MOVE_DOWN || actionTaken == TurnAction.MOVE_UP ||
                actionTaken == TurnAction.MOVE_LEFT || actionTaken == TurnAction.MOVE_RIGHT) {
            lastMove = actionTaken;
        }
    }

    public List<TurnAction> getAllTurnActions() {
        return actions;
    }

    public boolean isInventoryFull() {
        return inventory.size() == MAX_ITEMS;
    }

    /**
     * Gets the Image corresponding the direction the player is currently facing
     *
     * @param imageManager The ImageManager object that manages all images for the JPanel component rendering this Player
     * @return An Image for the direction the player is facing
     */
    public Image getImage(ImageManager imageManager) {
        String playerColor = isRedPlayer ? "red_bot_" : "blue_bot_";
        String directionName = moveToDirectionMap.get(lastMove);

        return imageManager.getScaledImage(playerColor + directionName);
    }
}
