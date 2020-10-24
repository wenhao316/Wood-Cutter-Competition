package wood.competition;

import javafx.collections.ObservableArray;
import wood.game.GameBoard;
import wood.game.TurnAction;
import wood.game.WoodPlayer;
import wood.item.InventoryItem;
import wood.item.ItemType;
import wood.item.SeedItem;
import wood.item.WoodItem;
import wood.strategy.WoodPlayerStrategy;
import wood.tiles.EmptyTile;
import wood.tiles.Tile;
import wood.tiles.TileType;

import java.awt.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static wood.util.DistanceUtilities.getManhattanDistance;
import wood.strategy.PlayerBoardView;
import wood.util.DistanceUtilities;

public class WoodPlayerStrategies implements WoodPlayerStrategy {

    public int boardSize;
    public int maxInventorySize;
    public int winningScore;
    public Point startTileLocation;
    public boolean isRedPlayer;
    public Random random;
    public ArrayList<InventoryItem> inventories = new ArrayList<>();
    public String name = "Leo";
    public int playerScore;
    public int opponentScore;


    @Override
    public void initialize(int boardSize, int maxInventorySize, int winningScore, Point startTileLocation, boolean isRedPlayer, Random random) {
        this.boardSize = boardSize;
        this.maxInventorySize = maxInventorySize;
        this.winningScore = winningScore;
        this.startTileLocation = startTileLocation;
        this.isRedPlayer = isRedPlayer;
        this.random = random;
    }

    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, boolean isRedTurn) {
        if (boardView.getYourLocation().equals(startTileLocation) && inventories.size() > 0) {
            inventories.clear();
        }

//        if (boardView.getTileTypeAtLocation(boardView.getYourLocation()).equals(TileType.TREE)) {
//            return TurnAction.CUT_TREE;
//        }
//
//        if (shouldGoHome()) {
//            return toMove(boardView, startTileLocation);
//        }
//
//        if (treeCounts(boardView) >= 3) {
//            if (boardView.getYourLocation().equals(closestTree(boardView))) {
//                return TurnAction.CUT_TREE;
//            }
//            return toMove(boardView, closestTree(boardView));
//        } else {
//            if (containSeed()) {
//                if (boardView.getTileTypeAtLocation(boardView.getYourLocation()).equals(TileType.EMPTY)) {
//                    removeSeed();
//                    return TurnAction.PLANT_SEED;
//                }
//                return toMove(boardView, closestEmpty(boardView));
//            } else {
//                if (boardView.getYourLocation().equals(closestSeed(boardView))) {
//                    return TurnAction.PICK_UP;
//                }
//                return toMove(boardView, closestSeed(boardView));
//            }
//        }

        if (shouldGoHome()) {
            return toMove(boardView, startTileLocation);
        }


        if (hasTree(boardView)) {
            if (boardView.getYourLocation().equals(furthestTree(boardView))) {
                return TurnAction.CUT_TREE;
            }
            return toMove(boardView, furthestTree(boardView));
        } else {
            if (containSeed()) {
                if (canPlant(boardView)) {
                    return TurnAction.PLANT_SEED;
                }
            }

            if (!containSeed()) {
                if (boardView.getYourLocation().equals(closestSeed(boardView))) {
                    return TurnAction.PICK_UP;
                }
                return toMove(boardView, closestSeed(boardView));
            }
        }
        return TurnAction.PLANT_SEED;
    }

    @Override
    public void receiveItem(InventoryItem itemReceived) {
        if (inventories.size() >= maxInventorySize) {
            return;
        }
        if (itemReceived != null) {
            inventories.add(itemReceived);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void endRound(int pointsScored, int opponentPointsScored) {
        playerScore += pointsScored;
        opponentScore += opponentPointsScored;
    }


    // check if contains seed
    public boolean containSeed() {
        for (int i = 0; i < inventories.size(); i++) {
            if (inventories.get(i).getType().equals(ItemType.SEED)) {
                return true;
            }
        }
        return false;
    }

    // check if contains wood
    public boolean containWood() {
        for (int i = 0; i < inventories.size(); i++) {
            if (inventories.get(i).getType().equals(ItemType.WOOD)) {
                return true;
            }
        }
        return false;
    }

    // check if board has seed
    public boolean hasSeed(PlayerBoardView boardView) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardView.getTileTypeAtLocation(i, j).equals(TileType.SEED)) {
                    return true;
                }
            }
        }
        return false;
    }

    // check if board has a tree
    public boolean hasTree(PlayerBoardView boardView) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardView.getTileTypeAtLocation(i, j).equals(TileType.TREE)) {
                    return true;
                }
            }
        }
        return false;
    }

    // furthest Tree
    public Point furthestTree(PlayerBoardView boardView) {
        ArrayList<Point> trees = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardView.getTileTypeAtLocation(i, j).equals(TileType.TREE)) {
                    trees.add(new Point(i, j));
                }
            }
        }
        Point far = trees.get(0);
        for (int i = 0; i < trees.size(); i++) {
            if (DistanceUtilities.getManhattanDistance(trees.get(i), boardView.getYourLocation())
                    < DistanceUtilities.getManhattanDistance(far, boardView.getYourLocation())) {
                far = trees.get(i);
            }
        }
        return far;
    }

    // closest Tree
    public Point closestTree(PlayerBoardView boardView) {
        ArrayList<Point> trees = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardView.getTileTypeAtLocation(i , j).equals(TileType.TREE)) {
                    trees.add(new Point(i, j));
                }
            }
        }
        Point close = trees.get(0);
        for (int i = 0; i < trees.size(); i++) {
            if (DistanceUtilities.getManhattanDistance(trees.get(i), boardView.getYourLocation())
                    < DistanceUtilities.getManhattanDistance(close, boardView.getYourLocation())) {
                close = trees.get(i);
            }
        }
        return close;
    }

    // closest seed
    public Point closestSeed(PlayerBoardView boardView) {
        ArrayList<Point> seeds = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardView.getTileTypeAtLocation(i, j).equals(TileType.SEED)) {
                    seeds.add(new Point(i, j));
                }
            }
        }
        Point close = seeds.get(0);
        for (int i = 0; i < seeds.size(); i++) {
            if (DistanceUtilities.getManhattanDistance(seeds.get(i), boardView.getYourLocation())
                    < DistanceUtilities.getManhattanDistance(close, boardView.getYourLocation())) {
                close = seeds.get(i);
            }
        }
        return close;
    }


    // check closest empty
    public Point closestEmpty(PlayerBoardView boardView) {
        ArrayList<Point> empty = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardView.getTileTypeAtLocation(i, j).equals(TileType.EMPTY)) {
                    empty.add(new Point(i, j));
                }
            }
        }
        Point close = empty.get(0);
        for (int i = 0; i < empty.size(); i++) {
            if (DistanceUtilities.getManhattanDistance(empty.get(i), boardView.getYourLocation())
                    < DistanceUtilities.getManhattanDistance(close, boardView.getYourLocation())) {
                close = empty.get(i);
            }
        }
        return close;
    }

    // check full inventory
    public boolean fullInventory() {
        int count = 0;
        for (int i = 0; i < inventories.size(); i++) {
            count++;
        }
        if (count >= maxInventorySize) {
            return true;
        }
        return false;
    }


    // check if inventories if full
    public boolean shouldGoHome() {
        int count = 0;
        for (int i = 0; i < inventories.size(); i++) {
            if (inventories.get(i).getType() == ItemType.WOOD) {
                count++;
            }
        }
        if (count >= 3) {
            return true;
        }
        return false;
    }

    // toMove
    public static TurnAction toMove(PlayerBoardView boardView, Point destination) {
        if (boardView.getYourLocation().x < destination.x) {
            return TurnAction.MOVE_RIGHT;
        } else if (boardView.getYourLocation().x > destination.x) {
            return TurnAction.MOVE_LEFT;
        } else if (boardView.getYourLocation().y < destination.y) {
            return TurnAction.MOVE_UP;
        } else {
            return TurnAction.MOVE_DOWN;
        }
    }


    // remove items
    public void removeSeed() {
        for (int i = 0; i < inventories.size(); i++) {
            if (inventories.get(i).getType() == ItemType.SEED) {
                inventories.remove(i);
                break;
            }
        }
    }

    // count trees
    public int treeCounts(PlayerBoardView boardView) {
        int count = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardView.getTileTypeAtLocation(i, j).equals(TileType.TREE)) {
                    count++;
                }
            }
        }
        return count;
    }


    // check if can plant
    public boolean canPlant(PlayerBoardView boardView) {
        if (boardView.getTileTypeAtLocation(boardView.getYourLocation().x, boardView.getYourLocation().y).equals(TileType.EMPTY)) {
            return true;
        }
        return false;
    }

    // count seeds
    public int seedCounts(PlayerBoardView boardView) {
        int count = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (boardView.getTileTypeAtLocation(i, j).equals(TileType.SEED)) {
                    count++;
                }
            }
        }
        return count;
    }

    // check cut tree
    public boolean cuttable(PlayerBoardView boardView) {
        if (boardView.getTileTypeAtLocation(boardView.getYourLocation()).equals(TileType.TREE)) {
            if (boardView.getCurrentTileValue() >= 50) {
                return true;
            }
        }
        return false;
    }
}