package wood.game;

import wood.replay.Replay;
import wood.strategy.PlayerBoardView;
import wood.strategy.WoodPlayerStrategy;
import wood.tiles.Tile;

import java.awt.Point;
import java.util.Observable;
import java.util.Random;

public class GameEngine extends Observable {
    private static final int MAX_TURNS_PER_GAME = 1000;
    private static final int MIN_SCORE_TO_WIN = 2000;
    private static final double TURNS_PER_SECOND = 50;

    private long randomSeed;
    private GameBoard board;
    private WoodPlayer redPlayer;
    private WoodPlayer bluePlayer;
    private boolean guiEnabled;
    private WoodPlayer playerWhoThrewException;
    private Exception exceptionThrown;

    public GameEngine(int boardSize, WoodPlayerStrategy redPlayerStrategy, WoodPlayerStrategy bluePlayerStrategy) {
        this(boardSize, redPlayerStrategy, bluePlayerStrategy, System.currentTimeMillis());
    }

    public GameEngine(int boardSize, WoodPlayerStrategy redPlayerStrategy, WoodPlayerStrategy bluePlayerStrategy, long randomSeed) {
        this.randomSeed = randomSeed;
        WorldGeneration.setRandomNumberSeed(randomSeed);
        this.board = WorldGeneration.generateBoard(boardSize);
        Tile redStartTile = board.getTileAtLocation(board.getRedStartTileLocation());
        Tile blueStartTile = board.getTileAtLocation(board.getBlueStartTileLocation());

        this.redPlayer = new WoodPlayer(redPlayerStrategy, redStartTile,true);
        this.bluePlayer = new WoodPlayer(bluePlayerStrategy, blueStartTile, false);
        this.guiEnabled = false;

        this.playerWhoThrewException = null;
        this.exceptionThrown = null;
    }

    public GameBoard getBoard() {
        return board;
    }

    public WoodPlayer getRedPlayer() {
        return redPlayer;
    }

    public WoodPlayer getBluePlayer() {
        return bluePlayer;
    }

    public int getRedPlayerScore() {
        return redPlayer.getScore();
    }

    public int getBluePlayerScore() {
        return bluePlayer.getScore();
    }

    public Exception getExceptionThrown() {
        return exceptionThrown;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public void setGuiEnabled(boolean guiEnabled) {
        this.guiEnabled = guiEnabled;
    }

    public Replay getReplay() {
        boolean redThrewException = (playerWhoThrewException == redPlayer);
        boolean blueThrewException = (playerWhoThrewException == bluePlayer);
        return new Replay(board.getSize(), randomSeed, redPlayer.getAllTurnActions(), bluePlayer.getAllTurnActions(),
                          redThrewException, blueThrewException);
    }

    /**
     * Runs through a round of Wood: The Gathering until either the maximum number of turns is reached
     *  or a player achieves the score needed to win. If either player strategy throws an exception at any time,
     *  that strategy will receive a score of -1 and the game will end
     */
    public void runGame() {
        // Wait a few seconds at the start for graphical components to load
        delayBetweenGuiFrames(2000);

        try {
            runGameLoop();
        } catch(Exception e) {
            // It's generally bad practice to catch generic Exceptions, but because a strategy can throw an exception
            // of any type, it's unavoidable here
            playerWhoThrewException.setScore(-1);
            this.exceptionThrown = e;

            // Let anything watching update
            setChanged();
            notifyObservers();
            e.printStackTrace();
        }
    }

    private void runGameLoop() {
        initializePlayer(redPlayer, true);
        initializePlayer(bluePlayer, false);

        int turnNumber = 0;
        boolean isRedTurn = true;
        WoodPlayer firstPlayer;
        WoodPlayer secondPlayer;
        boolean roundHasWinner = (redPlayer.getScore() >= MIN_SCORE_TO_WIN) || (bluePlayer.getScore() >= MIN_SCORE_TO_WIN);

        while (turnNumber < MAX_TURNS_PER_GAME && !roundHasWinner) {
            delayBetweenGuiFrames((long) (1000 / TURNS_PER_SECOND));

            if (isRedTurn) {
                firstPlayer = redPlayer;
                secondPlayer = bluePlayer;
            } else {
                firstPlayer = bluePlayer;
                secondPlayer = redPlayer;
            }

            processTurn(firstPlayer, secondPlayer, isRedTurn);
            processTurn(secondPlayer, firstPlayer, isRedTurn);
            board.update();

            isRedTurn = !isRedTurn;
            turnNumber++;
            roundHasWinner = (redPlayer.getScore() >= MIN_SCORE_TO_WIN) || (bluePlayer.getScore() >= MIN_SCORE_TO_WIN);

            // The state of the engine has changed, let anything observing it (like the GUI) know
            this.setChanged();
            this.notifyObservers();
        }

        // End the round
        int redPlayerScore = redPlayer.getScore();
        int bluePlayerScore = bluePlayer.getScore();
        redPlayer.getStrategy().endRound(redPlayerScore, bluePlayerScore);
        bluePlayer.getStrategy().endRound(bluePlayerScore, redPlayerScore);
        playerWhoThrewException = null;
    }

    private void initializePlayer(WoodPlayer playerToInitialize, boolean isRedPlayer) {
        playerWhoThrewException = playerToInitialize;
        int boardSize = board.getSize();
        int maxInventorySize = WoodPlayer.MAX_ITEMS;
        Point playerStartLocation = playerToInitialize.getCurrentTile().getLocation();
        Point playerStartCopy = new Point(playerStartLocation.x, playerStartLocation.y);
        playerToInitialize.getStrategy().initialize(boardSize, maxInventorySize, MIN_SCORE_TO_WIN,
                                                    playerStartCopy, isRedPlayer, new Random(randomSeed));

        Point startTileLocation;
        if (playerToInitialize.isRedPlayer()) {
            startTileLocation = board.getRedStartTileLocation();
        } else {
            startTileLocation = board.getBlueStartTileLocation();
        }

        board.getTileAtLocation(startTileLocation).onEnter(playerToInitialize);
    }

    private void processTurn(WoodPlayer currentPlayer, WoodPlayer otherPlayer, boolean isRedTurn) {
        playerWhoThrewException = currentPlayer; // If an exception gets thrown, we know who did it

        PlayerBoardView boardView = board.convertToView(currentPlayer, otherPlayer);
        TurnAction playerAction = currentPlayer.getStrategy().getTurnAction(boardView, isRedTurn);
        currentPlayer.addTurnAction(playerAction);

        if (playerAction == null) {
            return;
        }

        switch (playerAction) {
            case MOVE_UP: handleMove(currentPlayer, 0, 1);
                          break;
            case MOVE_DOWN: handleMove(currentPlayer, 0, -1);
                            break;
            case MOVE_RIGHT: handleMove(currentPlayer, 1, 0);
                             break;
            case MOVE_LEFT: handleMove(currentPlayer, -1, 0);
                            break;
            case PICK_UP:
            case PLANT_SEED:
            case CUT_TREE: handleActionOnPlayerTile(currentPlayer, playerAction);
                           break;
            default: System.err.println("Unhandled TurnAction: " + playerAction);
        }
    }

    private void handleMove(WoodPlayer player, int xChange, int yChange) {
        Tile currentTile = player.getCurrentTile();
        Point playerLocation = currentTile.getLocation();

        int nextX = playerLocation.x + xChange;
        int nextY = playerLocation.y + yChange;
        boolean nextXInBounds = (nextX >= 0 && nextX < board.getSize());
        boolean nextYInBounds = (nextY >= 0 && nextY < board.getSize());
        if (!nextXInBounds || !nextYInBounds) {
            // Can't step outside the world
            return;
        }

        Tile nextTile = board.getTileAtLocation(nextX, nextY);
        if (nextTile.getPlayerOnTile() != null) {
            // Can't step on a tile if there's another player there
            return;
        }

        currentTile.onExit(player);
        nextTile.onEnter(player);
        player.setCurrentTile(nextTile);
    }

    private void handleActionOnPlayerTile(WoodPlayer playerPerformingAction, TurnAction action) {
        boolean playerInventoryFull = playerPerformingAction.isInventoryFull();
        Tile currentPlayerTile = playerPerformingAction.getCurrentTile();
        Tile tileAfterAction = currentPlayerTile.interact(playerPerformingAction, action);

        if (tileAfterAction != currentPlayerTile) {
            // Tile has changed as a result of the action
            board.setTileAtLocation(currentPlayerTile.getLocation(), tileAfterAction);
            playerPerformingAction.setCurrentTile(tileAfterAction);

            if (needToGenerateSeedTile(playerInventoryFull, action)) {
                Point boardBottomLeft = new Point(0, 0);
                Point boardTopRight = new Point(board.getSize() - 1, board.getSize() - 1);
                WorldGeneration.generateNewSeedTile(board, boardBottomLeft, boardTopRight);
            }
        }
    }

    private boolean needToGenerateSeedTile(boolean inventoryFull, TurnAction action) {
        // If a tree is cut, or a seed was deleted from the world by being put into a full inventory
        // Then a new seed needs to be generated
        return (action == TurnAction.CUT_TREE) || (inventoryFull && action == TurnAction.PICK_UP);
    }

    private void delayBetweenGuiFrames(long millisecondsToWait) {
        if(!guiEnabled) {
            return;
        }

        try {
            Thread.sleep(millisecondsToWait);
        } catch (InterruptedException e) {
            System.err.println("Sleeping in between turns failed");
        }
    }
}
