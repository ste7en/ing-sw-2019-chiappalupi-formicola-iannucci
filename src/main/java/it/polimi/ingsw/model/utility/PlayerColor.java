package it.polimi.ingsw.model.utility;

public enum PlayerColor {
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