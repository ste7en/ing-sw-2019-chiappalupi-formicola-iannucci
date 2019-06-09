package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.player.Player;

public class Damage implements Comparable<Damage> {

    private Player target;
    private Cell position;
    private int damage;
    private int marks;

    /**
     * String constants used in messages between client-server
     */
    public final static String damage_key = "DAMAGE";
    public final static String no_damage = "NO_DAMAGE";

    public Damage() {
        target = null;
        position = null;
        damage = 0;
        marks = 0;
    }

    /**
     * Target getter.
     * @return the Player target of the Damage.
     */
    public Player getTarget() {
        return target;
    }

    /**
     * Sets the target of the Damage.
     * @param target it's the player to set as target.
     */
    public void setTarget(Player target) {
        this.target = target;
    }

    /**
     * Position getter.
     * @return the Cell where the target has to be moved; null if no movement has to be done.
     */
    public Cell getPosition() {
        return position;
    }

    /**
     * Sets the position where the target has to be moved.
     * @param position it's the cell to be set as position.
     */
    public void setPosition(Cell position) {
        this.position = position;
    }

    /**
     * Damage getter.
     * @return the damage that has to be applied to the target.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Sets the damage that has to be applied to the target.
     * @param damage it's an inteher containing the damage to set.
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Marks getter.
     * @return the marks that has to be applied to the target.
     */
    public int getMarks() {
        return marks;
    }

    /**
     * Sets the marks that has to be applied to the target.
     * @param marks it's an integer containing the marks to set.
     */
    public void setMarks(int marks) {
        this.marks = marks;
    }

    @Override
    public String toString() {
        String s = "Damage:\n";
        if(target != null) s = s + "target: " + target.getNickname();
        if(position != null) s = s + "\nposition: " + position;
        if(damage != 0) s = s + "\ndamages: " + damage;
        if(marks != 0) s = s + "\nmarks: " + marks;
        return s;
    }

    @Override
    public boolean equals(Object o) {
        Damage d = (Damage) o;
        if(d.getTarget() != target || d.getDamage() != damage || d.getMarks() != marks || d.getPosition() != position) return false;
        return true;
    }

    @Override
    public int compareTo(Damage damage) {
        if (target != damage.target) return target.compareTo(damage.getTarget());
        if (position != null && damage.getPosition()!= null) return position.compareTo(damage.getPosition());
        return this.compareTo(damage);
    }
}