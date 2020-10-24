package wood.replay;

import wood.game.TurnAction;
import wood.item.InventoryItem;
import wood.strategy.PlayerBoardView;
import wood.strategy.WoodPlayerStrategy;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class ReplayStrategy implements WoodPlayerStrategy {
    private Replay gameToReplay;
    private Queue<TurnAction> actionsToReplay;
    private boolean exceptionThrown;
    private boolean isRedPlayer;

    public ReplayStrategy(Replay gameToReplay) {
        this.gameToReplay = gameToReplay;
    }

    @Override
    public void initialize(int boardSize, int maxInventorySize, int winningScore, Point startTileLocation,
                           boolean isRedPlayer, Random random) {
        if(isRedPlayer) {
            actionsToReplay = new LinkedList<>(gameToReplay.getRedPlayerActions());
            exceptionThrown = gameToReplay.redThrewException();
        } else {
            actionsToReplay = new LinkedList<>(gameToReplay.getBluePlayerActions());
            exceptionThrown = gameToReplay.blueThrewException();
        }

        this.isRedPlayer = isRedPlayer;
    }

    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, boolean isRedTurn) {
        if(actionsToReplay.size() == 0 && exceptionThrown) {
            String exceptionPlayer = isRedPlayer ? "Red" : "Blue";
            throw new RuntimeException("An exception from the " + exceptionPlayer + " Player happened on this turn");
        }

        return actionsToReplay.poll();
    }

    @Override
    public String getName() {
        return "It's rewind time";
    }

    @Override
    public void receiveItem(InventoryItem itemReceived) {
        // Don't care
    }

    @Override
    public void endRound(int totalRedPoints, int totalBluePoints) {
        // Doesn't matter
    }
}
