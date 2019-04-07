package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Board {

    private GameMap map;
    private HashMap<AmmoColor, List<Weapon>> weapons;
    private HashMap<Integer, ArrayList<PlayerColor>> skullTrack;

    public void refillWeapons() { }

    public List<Weapon> showWeapons(AmmoColor Color) { return null; }

    public Weapon pickWeapon(Weapon weapon) { return null; }

    public Integer skullsLeft() { return null; }

    public void addBloodFrom(PlayerColor player, Integer count) { }

}