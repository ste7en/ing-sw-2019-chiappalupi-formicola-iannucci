package it.polimi.ingsw.model.utility;

import it.polimi.ingsw.model.utility.MapType;
import it.polimi.ingsw.model.player.Player;

import java.util.UUID;

/**
 *
 * @author Elena Iannucci
 */

public class GameSettings {

    private Player firstPlayer;
    private MapType mapType;
    private UUID gameID;

    public GameSettings(UUID gameID){
        this.gameID=gameID;
    }

    public Player getFirstPlayer() { return firstPlayer; }

    public void setFirstPlayer(Player player) {
        if (player==null) throw new NullPointerException("Player object references to null");
        else this.firstPlayer=player;
    }

    public MapType getMapType() { return mapType; }

    public void setMapType(MapType value) {
        if (value==null) throw new NullPointerException("MapType references to null");
        else this.mapType=value;
    }

}