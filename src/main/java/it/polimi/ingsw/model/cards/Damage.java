package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.player.Player;

public class Damage implements Comparable<Damage> {

    private Player target;
    private Cell position;
    private int damage;
    private int marks;

    public Damage() {
        target = null;
        position = null;
        damage = 0;
        marks = 0;
    }

    public Player getTarget() {
        return target;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public Cell getPosition() {
        return position;
    }

    public void setPosition(Cell position) {
        this.position = position;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getMarks() {
        return marks;
    }

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