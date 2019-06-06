package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.PlayerColor;

import java.util.*;

public class GameLogic {

    private ArrayList<Player> players;
    private Board board;
    private boolean finalFrenzy;
    private UUID gameID;
    private ArrayList<Damage> forPotentiableWeapon;

    /**
     * String constants used in messages between client-server
     */
    public static final String gameID_key = "GAME_ID";

    //TODO: - Game Logic constructor and method implementation

    public UUID getGameID() {
        return gameID;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void move(Player player) { }

    private void grabStuff(Player player) { }

    private void shootPeople(Player player) { }

    private void death(Player player) { }

    private void spawn(Player player) { }

    private void round(Player player) { }

    private void finalFrenzyRound(Player player) { }

    public void gameOver() { }

    public void addPlayer(Player player) { }

    /**
     * Getter of the list of damage that has already been done with the weapon that is being used.
     *
     * @return an array list containing the damage.
     */
    public ArrayList<Damage> getForPotentiableWeapon() {
        return forPotentiableWeapon;
    }

    /**
     * Adds damages to the list of damages that is being done using a potentiable weapon.
     *
     * @param damage it's the damage that is being done.
     */
    public void appendPotentiableWeapon(ArrayList<Damage> damage) {
        if(forPotentiableWeapon == null)
            forPotentiableWeapon = new ArrayList<>(damage);
        else forPotentiableWeapon.addAll(damage);
    }

    /**
     * Clears the damage that has been done with a potentiable weapon after that it has been used.
     */
    public void wipePotentiableWeapon() {
        if(forPotentiableWeapon != null || forPotentiableWeapon.size() != 0) forPotentiableWeapon.clear();
    }

    /**
     * This method is used to get a Player from its color.
     * @param playerColor it's the color of the player to return.
     * @return the player looked for.
     * @throws IllegalArgumentException if the playerColor provided doesn't correspond to any player in the game.
     */
    public Player getPlayer(PlayerColor playerColor) {
        for(Player player : players)
            if(player.getCharacter().getColor() == playerColor) return player;
        throw new IllegalArgumentException("This player is not playing!");
    }

    /**
     * This method is used to compute the possible damages that a weapon can do when used by a certain player.
     * @param weapon it's the weapon that is being used.
     * @param effect it's the effect that is being used.
     * @param shooter it's the player who is using the weapon.
     * @param forPotentiableWeapons it's an ArrayList containing all of the previous damages made by the other weapons.
     * @return an array list containing all of the possible damage alternatives when using the effect of the weapon.
     */
    public ArrayList<ArrayList<Damage>> useEffect(Weapon weapon, Effect effect, Player shooter, ArrayList<Damage> forPotentiableWeapons) {
        return weapon.useEffect(shooter, effect, forPotentiableWeapons, board.getMap(), players);
    }

    /**
     * This method is used to apply damages when a player shoots to another.
     * @param damage it's the damage that has to be applied.
     * @param shooter it's the color of the player who is shooting.
     */
    public void applyDamage(Damage damage, PlayerColor shooter) {
        Player target = damage.getTarget();
        if(damage.getPosition() != null) board.getMap().setPlayerPosition(target, damage.getPosition());
        if(damage.getDamage() != 0) target.getPlayerBoard().appendDamage(shooter, damage.getDamage());
        if(damage.getMarks() != 0) {
            ArrayList<PlayerColor> marks = new ArrayList<>();
            for(int i = 0; i < damage.getMarks(); i++)
                marks.add(shooter);
            target.getPlayerBoard().setMarks(marks);
        }
    }

    /**
     * This method is used to find a player from its user.
     * @param user it's the user that is wanted to bind with its player.
     * @return the player that is being searched.
     */
    public Player lookForPlayerFromUser(User user) {
        Player player = null;
        for(Player p : players)
            if(p.getUser().equals(user))
                player = p;
        if(player == null) throw new NullPointerException("This user doesn't exists.");
        return player;
    }

}