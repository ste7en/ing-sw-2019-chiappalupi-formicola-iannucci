package it.polimi.ingsw;

import java.util.ArrayList;

public class Damage {

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
        if(target != null) s = s + "target: " + target.getNickname() + "\n";
        if(position != null) s = s + "position: " + position + "\n";
        if(damage != 0) s = s + "damages: " + damage + "\n";
        if(marks != 0) s = s + "marks: " + marks + "\n";
        return s;
    }

    @Override
    public boolean equals(Object o) {
        Damage d = (Damage) o;
        if(d.getTarget() != target || d.getDamage() != damage || d.getMarks() != marks || d.getPosition() != position) return false;
        return true;
    }
}