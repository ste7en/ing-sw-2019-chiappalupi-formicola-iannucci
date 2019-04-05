package it.polimi.ingsw;

import java.lang.reflect.Array;
import java.util.*;

public class Board {

    private GameMap map;
    private HashMap<AmmoColor, ArrayList<Weapon>> weapons;
    private HashMap<Integer, ArrayList<PlayerColor>> skullTrack;


    public void refillWeapons() {

    }

    public ArrayList<Weapon> showWeapons(AmmoColor Color) { return null; }

    public Weapon pickWeapon(Weapon weapon) { return null; }

    public Integer skullsLeft() { return null; }

    public void addBloodFrom(PlayerColor player, Integer count) { }

}