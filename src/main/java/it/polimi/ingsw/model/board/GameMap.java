package it.polimi.ingsw.model.board;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.polimi.ingsw.model.utility.*;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Daniele Chiappalupi
 * @author Elena Iannucci
 */
public class GameMap implements Cloneable{

    /**
     * String constants used in messages between client-server
     */
    public static final String gameMap_key = "GAME_MAP";

    /** Static ANSI colors; */
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";

    /** Static GameMap max dimensions */
    private static final int ROWS = 3;
    private static final int COLUMNS = 4;

    /** String that contains the path to where the resources are located. */
    private static final String PATHNAME =
            "src" + File.separator + "main" + File.separator + "resources" + File.separator;

    /** Static gameMap Strings used to print the map; */
    private static final char DOOR_UP                     = '\u039B';
    private static final char DOOR_DOWN                   = 'V';
    private static final char DOOR_RIGHT                  = '<';
    private static final char DOOR_LEFT                   = '>';
    private static final String SPAWN                     = " spawn ";
    private static final String AMMOS                     = " ammos ";
    private static final String BLANK                     = "       ";
    private static final String ALL_WALL                  = "---------";
    private static final String WEST_WALL                 = "|" + BLANK + " ";
    private static final String EAST_WALL                 = " " + BLANK + "|";
    private static final String BOTH_WALL                 = "|" + BLANK + "|";
    private static final String SPACE                     = " " + BLANK + " ";
    private static final String WEST_WALL_SPAWN           = "|" + SPAWN + " ";
    private static final String EAST_WALL_SPAWN           = " " + SPAWN + "|";
    private static final String BOTH_WALL_SPAWN           = "|" + SPAWN + "|";
    private static final String EAST_DOOR_SPAWN           = " " + SPAWN + DOOR_RIGHT;
    private static final String WEST_DOOR_SPAWN           = DOOR_LEFT + SPAWN + " ";
    private static final String BOTH_DOOR_SPAWN           = DOOR_LEFT + SPAWN + DOOR_RIGHT;
    private static final String SPACE_SPAWN               = " " + SPAWN + " ";
    private static final String WEST_WALL_EAST_DOOR_SPAWN = "|" + SPAWN + DOOR_RIGHT;
    private static final String WEST_DOOR_EAST_WALL_SPAWN = DOOR_LEFT + SPAWN + "|";
    private static final String WEST_WALL_AMMOS           = "|" + AMMOS + " ";
    private static final String EAST_WALL_AMMOS           = " " + AMMOS + "|";
    private static final String BOTH_WALL_AMMOS           = "|" + AMMOS + "|";
    private static final String EAST_DOOR_AMMOS           = " " + AMMOS + DOOR_RIGHT;
    private static final String WEST_DOOR_AMMOS           = DOOR_LEFT + AMMOS + " ";
    private static final String BOTH_DOOR_AMMOS           = DOOR_LEFT + AMMOS + DOOR_RIGHT;
    private static final String SPACE_AMMOS               = " " + AMMOS + " ";
    private static final String WEST_WALL_EAST_DOOR_AMMOS = "|" + AMMOS + DOOR_RIGHT;
    private static final String WEST_DOOR_EAST_WALL_AMMOS = DOOR_LEFT + AMMOS + "|";
    private static final String SOUTH_DOOR                = "|   " + DOOR_UP + "   |";
    private static final String NORTH_DOOR                = "|   " + DOOR_DOWN + "   |";
    private static final String FORMAT                    = "%9s";
    private static final String HIGHER_GRID_LEFT          = "|   ";
    private static final String HIGHER_GRID_RIGHT         = "   |";
    private static final String LEFT_GRID                 = "- ";
    private static final String RIGHT_GRID                = " -";

    /** Rep of the game's map through a matrix */
    private Cell[][] map;

    /** Players' positions on the map */
    private LinkedHashMap<Player, Cell> playersPosition;

    /** Configuration's type of the map */
    private MapType mapType;

    /**
     * Method that initializes the map from a json file
     *
     * @return a matrix of cells that will be the map of the game
     */
    private Cell[][] initializeMap(MapType mapType) {
        ObjectMapper objectMapper = new ObjectMapper();
        Cell[][] box = new Cell[ROWS][COLUMNS];
        try {
            File json = new File(PATHNAME + mapType + ".json");
            box = objectMapper.readValue(json, Cell[][].class);

        } catch (IOException e) {
            AdrenalineLogger.error(e.toString());
        }
        return box;
    }

    public GameMap(MapType mapType) {
        this.mapType = mapType;
        this.playersPosition = new LinkedHashMap<>();
        this.map = initializeMap(mapType);
    }

    public GameMap(MapType mapType, Map<Player, Cell> playersPosition) {
        this.mapType = mapType;
        this.playersPosition = new LinkedHashMap<>();
        this.playersPosition.putAll(playersPosition);
        this.map = initializeMap(mapType);
    }

    public GameMap(Cell[][] map, Map<Player, Cell> playersPosition, int rows, int columns) {
        this.map = new Cell[rows][columns];
        this.playersPosition = new LinkedHashMap<>();
        int i;
        int j;
        for (i = 0; i < rows; i++) {
            for (j = 0; j < columns; j++) {
                this.map[i][j] = map[i][j];
            }
        }
        this.playersPosition.putAll(playersPosition);
    }

    @Override
    @SuppressWarnings({"squid:S2975"})
    public Object clone() {
        GameMap clone =
                new GameMap(this.mapType, new HashMap<>(this.playersPosition));
        clone.map = this.map.clone();
        if(clone != this) return clone;
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            AdrenalineLogger.error(e.toString());
        }
        return clone;
    }

    /**
     * Rows getter: needed to know the size of the map in some powerup effects: {@link
     * it.polimi.ingsw.model.cards.Teleporter#use(Player, GameMap, List)}
     *
     * @return the number of ROWS of the GameMap matrix.
     */
    public int getRows() {
        return ROWS;
    }

    /**
     * Columns getter: needed to know the size of the map in some powerup effects: {@link
     * it.polimi.ingsw.model.cards.Teleporter#use(Player, GameMap, List)}
     *
     * @return the number of COLUMNS of the GameMap matrix.
     */
    public int getColumns() {
        return COLUMNS;
    }

    /**
     * Method that gets the cell that is in a given position on the map
     *
     * @param row the row of the position
     * @param column the column of the position
     * @return the map's cell selected
     */
    public Cell getCell(int row, int column) {
        return map[row][column];
    }

    /**
     * Used to print the map in the CLI.
     */
    @Override
    public String toString() {
        StringBuilder mapString = new StringBuilder();
        mapString.append("  ");
        StringBuilder box1 = new StringBuilder();
        StringBuilder box2 = new StringBuilder();
        StringBuilder box3 = new StringBuilder();
        for (int k = 0; k < map[0].length; k++) {
            mapString.append(HIGHER_GRID_LEFT).append(k).append(HIGHER_GRID_RIGHT);
        }
        mapString.append("\n");
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                stringCaseHandler(box1, box2, box3, i, j);
            }
            mapString.append(box1.toString()).append("\n").append(box2.toString()).append("\n").append(box3.toString()).append("\n");
            box1 = new StringBuilder();
            box2 = new StringBuilder();
            box3 = new StringBuilder();
        }
        mapString.append("  ");
        for (int k = 0; k < map[0].length; k++) {
            mapString.append(HIGHER_GRID_LEFT).append(k).append(HIGHER_GRID_RIGHT);
        }
        mapString.append("\n");

        if(!playersPosition.keySet().isEmpty()) {
            mapString.append("\n").append("Players positions:").append("\n");
            List<Player> players = new ArrayList<>(playersPosition.keySet());
            for (Player player : players) {
                mapString.append(player.getNickname()).append(": ").append(playersPosition.get(player).toString()).append("\n");
            }
        }
        return mapString.toString();
    }

    /**
     * Helper method used to reduce the cognitive complexity of the code. Handles the case of the nested for and builds the box strings.
     * @param box1 it's the first StringBuilder.
     * @param box2 it's the second StringBuilder.
     * @param box3 it's the third StringBuilder.
     * @param i it's the row index of the map matrix.
     * @param j it's the column index of the map matrix.
     */
    private void stringCaseHandler(StringBuilder box1, StringBuilder box2, StringBuilder box3, int i, int j) {
        if (j == 0) {
            box1.append(LEFT_GRID);
            box2.append(i).append(" ");
            box3.append(LEFT_GRID);
        }
        if (map[i][j] != null) {
            northDirectionStringFormatter(box1, i, j);
            eastWestDirectionStringFormatter(box2, i, j);
            southDirectionStringFormatter(box3, i, j);
        } else {
            box1.append(SPACE);
            if(map[i][j] == null) box2.append(SPACE);
            else if(map[i][j].isRespawn()) box2.append(SPACE_SPAWN);
            else box2.append(SPACE_AMMOS);
            box3.append(SPACE);
        }
        if (j == map[i].length - 1) {
            box1.append(RIGHT_GRID);
            box2.append(" ").append(i);
            box3.append(RIGHT_GRID);
        }
    }

    /**
     * It's an helper method to reduce the cognitive complexity of the toString method: it computes the lower string of the final string.
     * @param box3 it's the stringBuilder to format
     * @param i it's the row of the map
     * @param j it's the column of the map
     */
    private void southDirectionStringFormatter(StringBuilder box3, int i, int j) {
        switch (map[i][j].adiajency(Direction.South)) {
            case wall:
                box3.append(map[i][j].getANSIColor()).append(ANSI_BLACK).append(String.format(FORMAT, ALL_WALL)).append(ANSI_RESET);
                break;
            case door:
                box3.append(map[i][j].getANSIColor()).append(ANSI_BLACK).append(String.format(FORMAT, SOUTH_DOOR)).append(ANSI_RESET);
                break;
            case space:
                switch (map[i][j].adiajency(Direction.West)) {
                    case wall:
                    case door:
                        switch (map[i][j].adiajency(Direction.East)) {
                            case wall:
                            case door:
                                box3.append(map[i][j].getANSIColor()).append(ANSI_BLACK).append(String.format(FORMAT, BOTH_WALL)).append(ANSI_RESET);
                                break;
                            case space:
                                box3.append(map[i][j].getANSIColor()).append(ANSI_BLACK).append(String.format(FORMAT, String.format(FORMAT, WEST_WALL))).append(ANSI_RESET);
                        }
                        break;
                    case space:
                        toStringHelperLine3(box3, i, j, EAST_WALL, SPACE);
                        break;
                }
                break;
        }
    }

    /**
     * It's an helper method to reduce the cognitive complexity of the toString method: it computes the middle string of the final string.
     * @param box2 it's the string to format
     * @param i it's the row of the map
     * @param j it's the column of the map
     */
    private void eastWestDirectionStringFormatter(StringBuilder box2, int i, int j) {
        switch (map[i][j].adiajency(Direction.West)) {
            case wall:
                if(map[i][j].isRespawn()) toStringHelperLine2(box2, i, j, BOTH_WALL_SPAWN, WEST_WALL_SPAWN, WEST_WALL_EAST_DOOR_SPAWN);
                else toStringHelperLine2(box2, i, j, BOTH_WALL_AMMOS, WEST_WALL_AMMOS, WEST_WALL_EAST_DOOR_AMMOS);
                break;
            case door:
                if(map[i][j].isRespawn()) toStringHelperLine2(box2, i, j, WEST_DOOR_EAST_WALL_SPAWN, WEST_DOOR_SPAWN, BOTH_DOOR_SPAWN);
                else toStringHelperLine2(box2, i, j, WEST_DOOR_EAST_WALL_AMMOS, WEST_DOOR_AMMOS, BOTH_DOOR_AMMOS);
                break;
            case space:
                if(map[i][j].isRespawn()) toStringHelperLine2(box2, i, j, EAST_WALL_SPAWN, SPACE_SPAWN, EAST_DOOR_SPAWN);
                else toStringHelperLine2(box2, i, j, EAST_WALL_AMMOS, SPACE_AMMOS, EAST_DOOR_AMMOS);
                break;
        }
    }

    /**
     * It's an helper method to reduce the cognitive complexity of the toString method: it computes the lower string of the final string.
     * @param box1 it's the string to format
     * @param i it's the row of the map
     * @param j it's the column of the map
     */
    private void northDirectionStringFormatter(StringBuilder box1, int i, int j) {
        switch (map[i][j].adiajency(Direction.North)) {
            case wall:
                box1.append(map[i][j].getANSIColor()).append(ANSI_BLACK).append(String.format(FORMAT, ALL_WALL)).append(ANSI_RESET);
                break;
            case door:
                box1.append(map[i][j].getANSIColor()).append(ANSI_BLACK).append(String.format(FORMAT, NORTH_DOOR)).append(ANSI_RESET);
                break;
            case space:
                switch (map[i][j].adiajency(Direction.West)) {
                    case wall:
                    case door:
                        toStringHelperLine3(box1, i, j, BOTH_WALL, WEST_WALL);
                        break;
                    case space:
                        toStringHelperLine1(box1, i, j, EAST_WALL, SPACE);
                        break;
                }
                break;
        }
    }

    /**
     * Helper method to avoid code repetition
     * @param box1 it's the stringBuilder where the data should be stored
     * @param i it's the row of the map
     * @param j it's the column of the map
     * @param wallType1 it's the wall type to be used in the first case
     * @param wallType2 it's the wall type to be used in the other case
     */
    private void toStringHelperLine1(
            StringBuilder box1, int i, int j, String wallType1, String wallType2) {
        switch (map[i][j].adiajency(Direction.East)) {
            case wall:
            case door:
                box1.append(map[i][j].getANSIColor()).append(ANSI_BLACK).append(String.format(FORMAT, wallType1)).append(ANSI_RESET);
                break;
            case space:
                box1.append(map[i][j].getANSIColor()).append(ANSI_BLACK).append(String.format(FORMAT, wallType2)).append(ANSI_RESET);
        }
    }

    /**
     * Helper method to avoid code repetition
     *
     * @param box3 it's the stringBuilder where the data should be stored
     * @param i it's the row of the map
     * @param j it's the column of the map
     * @param wallType1 it's the wall type to be used in the first case
     * @param wallType2 it's the wall type to be used in the other case
     */
    private void toStringHelperLine3(
            StringBuilder box3, int i, int j, String wallType1, String wallType2) {
        toStringHelperLine1(box3, i, j, wallType1, wallType2);
    }

    /**
     * Helper method to avoid code repetition
     *
     * @param box2 it's the string where the data should be stored
     * @param i it's the row of the map
     * @param j it's the column of the map
     * @param wallType1 it's the wall type to be used in the first case
     * @param wallType2 it's the wall type to be used in the second case
     * @param wallType3 it's the wall type to be used in the third case
     */
    private void toStringHelperLine2(
            StringBuilder box2, int i, int j, String wallType1, String wallType2, String wallType3) {
        switch (map[i][j].adiajency(Direction.East)) {
            case wall:
                box2.append(map[i][j].getANSIColor()).append(ANSI_BLACK).append(String.format(FORMAT, wallType1)).append(ANSI_RESET);
                break;
            case space:
                box2.append(map[i][j].getANSIColor()).append(ANSI_BLACK).append(String.format(FORMAT, wallType2)).append(ANSI_RESET);
                break;
            case door:
                box2.append(map[i][j].getANSIColor()).append(ANSI_BLACK).append(String.format(FORMAT, wallType3)).append(ANSI_RESET);
                break;
        }
    }

    /**
     * Method that gets the cell where a given player is positioned
     *
     * @param player the player of whom we want to know the position in terms of cell
     * @return the cell where the player is positioned
     */
    public Cell getCellFromPlayer(Player player) {
        return playersPosition.get(player);
    }

    /**
     * Method that gets all the cells that belong to a certain room
     *
     * @param cell the cell from which we get the room
     * @return a collection of cells belonging to the room that contains the given cell
     */
    public List<Cell> getRoomFromCell(Cell cell) {
        ArrayList<Cell> room = new ArrayList<>();
        int i, j;
        for (i = 0; i < ROWS; i++) {
            for (j = 0; j < COLUMNS; j++) {
                if (map[i][j] != null && map[i][j].getColor().equals(cell.getColor())) room.add(map[i][j]);
            }
        }
        return room;
    }

    /**
     * Method that gets the cell adjacent to a given cell in a given direction
     *
     * @param cell the cell from which we get the adjacent cell
     * @param direction the direction in which we find the adjacent cell we are looking far
     * @return the adjacent cell
     */
    public Cell getCellFromDirection(Cell cell, Direction direction) {
        int i;
        int j;
        for (i = 0; i < ROWS; i++)
            for (j = 0; j < COLUMNS; j++)
                if (map[i][j] != null && map[i][j].equals(cell)) {
                    if (direction == Direction.North) {
                        if (i > 0) return map[i - 1][j];
                    } else if (direction == Direction.East) {
                        if (j < 3) return map[i][j + 1];
                    } else if (direction == Direction.South) {
                        if (i < 2) return map[i + 1][j];
                    } else if (direction == Direction.West) {
                        if (j > 0) return map[i][j - 1];
                    }
                }
        return null;
    }

    /**
     * Returns the spawn cell of the color provided.
     * @param spawnColor it's the color of the spawn cell looked.
     * @return the spawn cell of that color.s
     */
    public Cell getSpawnPoint(AmmoColor spawnColor) {
        CellColor spawnCellColor = CellColor.blue;
        if(spawnColor == AmmoColor.red) spawnCellColor = CellColor.red;
        else if(spawnColor == AmmoColor.yellow) spawnCellColor = CellColor.yellow;
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLUMNS; j++) {
                if(map[i][j] != null && map[i][j].isRespawn() && map[i][j].getColor() == spawnCellColor) {
                    return map[i][j];
                }
            }
        }
        throw new IllegalArgumentException("This spawn point does not exists!");
    }

    /**
     * Method that gets the players that are placed on a given cell
     *
     * @param cell the cell from which we want to get the players
     * @return a collection of players in the same cell
     */
    @SuppressWarnings("squid:S2864")
    private ArrayList<Player> getPlayersFromCell(Cell cell) {
        ArrayList<Player> playersInThatCell = new ArrayList<>();
        for (Player player : playersPosition.keySet())
            if (playersPosition.get(player).equals(cell)) playersInThatCell.add(player);
        return playersInThatCell;
    }

    /**
     * Method that gets the targets in the same cell as a given player
     *
     * @param player the player that wants to know the targets that are in his cell
     * @return a collection of players in the same cell
     */
    public List<Player> getTargetsInMyCell(Player player) {
        ArrayList<Player> targets = getPlayersFromCell(playersPosition.get(player));
        targets.remove(player);
        return targets;
    }

    /**
     * Method that gets the cells that are at less than a certain distance (in terms of valid steps)
     * from a given cell
     *
     * @param player the player that wants to know the cells that are at less than a certain distance from him
     * @param distance the maximum distance accepted
     * @return a collection of cells at certain values of distance
     */
    public List<Cell> getCellsAtMaxDistance(Player player, int distance) {
        ArrayList<Cell> cells = getCellsAtMaxDistanceHelper(getCellFromPlayer(player), distance);
        cells.remove(getCellFromPlayer(player));
        return cells;
    }

    /**
     * Method that gets the cells that are adjacent to a certain cell and not separated by a wall
     *
     * @param cell the cell of the player that wants to know his adjacent cells
     * @return a collection of adjacent cells
     */
    private List<Cell> getAdjacentCells(Cell cell) {
        List<Cell> adjacentCells = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (cell.adiajency(direction) != Border.wall && getCellFromDirection(cell, direction) != null) {
                adjacentCells.add(getCellFromDirection(cell, direction));
            }
        }
        return adjacentCells;
    }

    /**
     * Method that gets the cells that are at less than a certain distance (in terms of valid steps)
     * from a given cell
     *
     * @param cell the cell from which we get the distance
     * @param distance the maximum distance accepted
     * @return a collection of cells at certain values of distance
     */
    private ArrayList<Cell> getCellsAtMaxDistanceHelper(Cell cell, int distance) {
        ArrayList<Cell> cells = new ArrayList<>();
        if (distance == 0) {
            cells.add(cell);
            return cells;
        }
        for (Direction direction : Direction.values()) {
            if ((getCellFromDirection(cell, direction) != null)
                    && (cell.adiajency(direction) != Border.wall)) {
                cells.addAll(
                        getCellsAtMaxDistanceHelper(getCellFromDirection(cell, direction), distance - 1));
            }
        }
        cells.add(cell);
        cells.addAll(getAdjacentCells(cell));
        Set<Cell> duplicatesEliminator = new LinkedHashSet<>(cells);
        cells.clear();
        cells.addAll(duplicatesEliminator);
        return cells;
    }

    /**
     * Method that gets the targets that are at less than a certain distance (in terms of valid steps)
     * from a given player
     *
     * @param player the player that wants to know his targets
     * @param distance the maximum distance accepted
     * @return a collection of players at certain values of distance
     */
    public List<Player> getTargetsAtMaxDistance(Player player, int distance) {
        List<Player> targets = getTargetsAtMaxDistanceHelper(getCellFromPlayer(player), distance);
        targets.remove(player);
        return targets;
    }

    /**
     * Method that gets the targets that are at less than a certain distance (in terms of valid steps)
     * from a given cell
     *
     * @param cell the cell from which we get the distance
     * @param distance the maximum distance accepted
     * @return a collection of players at certain values of distance
     */
    private List<Player> getTargetsAtMaxDistanceHelper(Cell cell, int distance) {
        List<Player> targets = new ArrayList<>();
        if (distance == 0) {
            targets.addAll(getPlayersFromCell(cell));
            return targets;
        }
        for (Direction direction : Direction.values()) {
            if ((getCellFromDirection(cell, direction) != null)
                    && (cell.adiajency(direction) != Border.wall)) {
                targets.addAll(
                        getTargetsAtMaxDistanceHelper(getCellFromDirection(cell, direction), distance - 1));
            }
        }
        targets.addAll(getPlayersFromCell(cell));
        targets.addAll(getAdjacentTargets(cell));
        Set<Player> duplicatesEliminator = new LinkedHashSet<>(targets);
        targets.clear();
        targets.addAll(duplicatesEliminator);
        targets.sort(Player::compareTo);
        return targets;
    }

    /**
     * Method that gets the targets that are at more than a certain distance (in terms of valid steps)
     * from a given player
     *
     * @param player the player that want to know his targets
     * @param distance the minimum distance accepted
     * @return a collection of players at certain values of distance
     */
    public List<Player> getTargetsAtMinDistance(Player player, int distance) {
        ArrayList<Player> targets = new ArrayList<>();
        for (Player tempPlayer : playersPosition.keySet()) {
            if ((distance > 0
                    && !getTargetsAtMaxDistance(player, distance - 1).contains(tempPlayer)
                    && tempPlayer != player)
                    || (distance == 0 && tempPlayer != player)) {
                targets.add(tempPlayer);
            }
        }
        targets.sort(Player::compareTo);
        return targets;
    }

    /**
     * Method that gets the targets that are adjacent to a certain player in terms of cells
     *
     * @param cell the cell of the player that wants to know his adjacent targets
     * @return a collection of adjacent players
     */
    public List<Player> getAdjacentTargets(Cell cell) {
        ArrayList<Player> targets = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (getCellFromDirection(cell, direction) != null && cell.adiajency(direction) != Border.wall) {
                targets.addAll(getPlayersFromCell(getCellFromDirection(cell, direction)));
            }
        }
        return targets;
    }

    /**
     * Method that gets the targets that are visible to a certain player
     *
     * @param player the player that wants to know his targets in sight
     * @return a collection of visible players
     */
    public List<Player> getSeenTargets(Player player) {
        ArrayList<Player> targets = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (playersPosition.get(player).adiajency(direction) == Border.door && getCellFromDirection(playersPosition.get(player), direction) != null) {
                for (Cell cell :
                        getRoomFromCell(getCellFromDirection(playersPosition.get(player), direction))) {
                    targets.addAll(getPlayersFromCell(cell));
                }
            }
        }
        for (Cell cell1 : getRoomFromCell(playersPosition.get(player))) {
            targets.addAll(getPlayersFromCell(cell1));
        }
        targets.sort(Player::compareTo);
        targets.remove(player);
        return targets;
    }

    /**
     * Method that gets the targets that are invisible to a certain player
     *
     * @param player the player that wants to know his hidden targets
     * @return a collection of not visible players
     */
    public List<Player> getUnseenTargets(Player player) {
        List<Player> targets = new ArrayList<>();
        List<Player> seenTargets = getSeenTargets(player);
        for (Player p : playersPosition.keySet())
            if (!(seenTargets.contains(p)) && p != player) targets.add(p);
        return targets;
    }

    /**
     * Method that gets the targets that are in a certain direction from a given player
     *
     * @param player the player that wants to know his targets in a certain direction
     * @param direction the direction where we want to look for the targets
     * @return a collection of targets in a direction from the player
     */
    public List<Player> getTargetsFromDirection(Player player, Direction direction) {
        ArrayList<Player> targets = new ArrayList<>(getTargetsInMyCell(player));
        Cell cell = getCellFromDirection(playersPosition.get(player), direction);
        while (cell != null) {
            targets.addAll(getPlayersFromCell(cell));
            cell = getCellFromDirection(cell, direction);
        }
        targets.sort(Player::compareTo);
        return targets;
    }

    /**
     * Method that tells us if a player can move to a certain cell in one single step
     *
     * @param player the player that would like to make the move
     * @param cell the destination cell that needs to be validated
     * @return a boolean that is true if the move is valid, false otherwise
     */
    public boolean isAOneStepValidMove(Player player, Cell cell) {
        for (Direction direction : Direction.values()) {
            if (playersPosition.get(player).adiajency(direction) != Border.wall
                    && getCellFromDirection(playersPosition.get(player), direction) != null &&  getCellFromDirection(playersPosition.get(player), direction) == cell) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method that sets the position of a given player to a certain point in the map defined by its
     * raw and column
     *
     * @param player the player whose position is the one we want to set
     * @param i raw of the cell where the player has to be positioned
     * @param j column of the cell where the player has to be positioned
     */
    public void setPlayerPosition(Player player, int i, int j) {
        Cell cell = getCell(i, j);
        playersPosition.put(player, cell);
    }

    /**
     * Method that sets the position of a given player to a given cell
     *
     * @param player the player whose position is the one we want to set
     * @param cell the cell where the player has to be positioned
     */
    public void setPlayerPosition(Player player, Cell cell) {
        playersPosition.put(player, cell);
    }

    /**
     * Player's position getter
     *
     * @param player the player whose position is the one we want to get
     * @return the cell where the player is positioned
     */
    public Cell getPositionFromPlayer(Player player) {
        return playersPosition.get(player);
    }
}
