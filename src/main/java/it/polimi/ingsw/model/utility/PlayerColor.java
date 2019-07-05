package it.polimi.ingsw.model.utility;

import java.io.Serializable;

public enum PlayerColor implements Serializable {
    yellow,
    green,
    purple,
    grey,
    blue;

    /**
     * String constant used in messages between client-server
     */
    public final static String playerColor_key = "PLAYER_COLOR";
}