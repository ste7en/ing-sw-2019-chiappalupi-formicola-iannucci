package it.polimi.ingsw;

import java.util.*;

public class GameMap {

    private Cell[][] map;
    private HashMap<Player, Cell> playersPosition;
    private int rows;
    private int columns;

    public GameMap(Cell[][] map, HashMap<Player, Cell> playersPosition, int rows, int columns){
        int i,j;
        for (i=0; i<rows; i++) {
            for (j = 0; j < columns; j++) {
                this.map[i][j] = map[i][j];
            }
        }
        this.playersPosition.putAll(playersPosition);
        this.rows=rows;
        this.columns=columns;
    }

    public ArrayList<Cell> getRoomFromCell (Cell cell) {
        ArrayList<Cell> room = new ArrayList<Cell>();
        int i,j;
        for (i=0; i<rows; i++) {
            for(j=0; j<columns; j++){
                if (map[i][j].getColor().equals(cell.getColor())) room.add(map[i][j]);
            }
        }
        return room;
    }

    private Cell getCellFromDirection (Player player, Direction direction){
        int i,j;
        for (i=0; i<rows; i++) {
            for(j=0; j<columns; j++) {
                if (map[i][j].equals(playersPosition.get(player))) {
                    switch (direction){
                        case North:
                            return map[i-1][j];
                        case East:
                            return map[i][j+1];
                        case South:
                            return map[i+1][j];
                        case West:
                            return map[i][j-1];
                    }
                }

            }
        }
        return null;
    }

    private ArrayList<Player> getPlayersFromCell (Cell cell){
        ArrayList<Player> playersInThatCell = new ArrayList<Player>();
        for (Player player : playersPosition.keySet()){
            if (playersPosition.get(player).equals(cell)) playersInThatCell.add(player);
        }
        return playersInThatCell;
    }


    public ArrayList<Player> getTargetsInMyCell (Player player){
        return getPlayersFromCell(playersPosition.get(player));
    }

    public ArrayList<Player> getOneMoveAwayTargets (Player player){
        ArrayList<Player> targets = new ArrayList<Player>();
        for (Direction direction : Direction.values()) {
           if(playersPosition.get(player).adiacency(direction) != Border.wall) {
               targets.addAll(getPlayersFromCell(getCellFromDirection(player, direction)));
           }
        }
        return targets;
    }

    public ArrayList<Player> getTargetsFrom(Cell cell, Border border) {
        return null;

    }

    public ArrayList<Player> getTargetsFrom(Direction direction) { return null; }

    public Cell getPositionFrom(Player player) {

        return null;
    }

    public Cell[][] getMap() {
        return map;
    }

}