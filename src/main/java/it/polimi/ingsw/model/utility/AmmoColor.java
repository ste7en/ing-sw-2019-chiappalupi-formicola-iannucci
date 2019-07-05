package it.polimi.ingsw.model.utility;

import java.io.Serializable;

public enum AmmoColor implements Serializable {
    red,
    yellow,
    blue;

    public static final String ammoColorKey_red = "AMMO_RED";
    public static final String ammoColorKey_yellow = "AMMO_YELLOW";
    public static final String ammoColorKey_blue = "AMMO_BLUE";
}