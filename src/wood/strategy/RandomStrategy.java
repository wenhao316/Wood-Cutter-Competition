package wood.strategy;

import wood.game.TurnAction;
import wood.item.InventoryItem;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class RandomStrategy implements WoodPlayerStrategy {
    Random random;
    List<TurnAction> allPossibleActions;

    @Override
    public void initialize(int boardSize, int maxInventorySize, int winningScore, Point startTileLocation, boolean isRedPlayer, Random random) {
        this.random = random;
        allPossibleActions = new ArrayList<>(EnumSet.allOf(TurnAction.class));
    }

    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, boolean isRedTurn) {
        int randomTurnIndex = random.nextInt(allPossibleActions.size());
        return allPossibleActions.get(randomTurnIndex);
    }

    @Override
    public void receiveItem(InventoryItem itemReceived) {

    }

    @Override
    public String getName() {
        return "RandomStrategy";
    }

    @Override
    public void endRound(int totalRedPoints, int totalBluePoints) {

    }
}
