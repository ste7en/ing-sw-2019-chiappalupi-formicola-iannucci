package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.utility.PlayerColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Board used by each player to keep track of his points, damages, cubes and
 * every other detail needed to proceed during the game.
 *
 * @author Stefano Formicola
 */
public class PlayerBoard {

    private static String COLOR_DAMAGE_MARKS_EXCEPTION = "An attempt to add the same color of the player to his damages or marks has been found. A player can't infer damages or marks to himself.";
    private static String INVALID_DAMAGES_NUMBER = "The number of damages should be greater than zero.";
    private static Integer MAX_ASSIGNABLE_POINTS = 8;
    private static Integer ADRENALINE_1_MIN = 3;
    private static Integer ADRENALINE_2_MIN = 6;

    /**
     * Color corresponding to the player and his character.
     */
    private final PlayerColor boardColor;

    /**
     * It is the structure that keeps track of the player's damage points
     * received from other players. It has been implemented using an array of
     * values corresponding to other players' colors.
     */
    private List<PlayerColor> damage;

    /**
     * The maximum number of points other players can get by killing a player.
     */
    private Integer maxPoints;

    /**
     * Steps of movement getter.
     * @return the steps that the player can do through his movement.
     */
    public int getStepsOfMovement() {
        return stepsOfMovement;
    }

    /**
     * Steps of movement setter.
     * @param stepsOfMovement it's the new parameter to set.
     */
    public void setStepsOfMovement(int stepsOfMovement) {
        this.stepsOfMovement = stepsOfMovement;
    }

    /**
     * Steps before shooting getter.
     * @return the steps that the player can do before the shot.
     */
    public int getStepsBeforeShooting() {
        return stepsBeforeShooting;
    }

    /**
     * Steps before shooting setter.
     * @param stepsBeforeShooting it's the new parameter to set.
     */
    public void setStepsBeforeShooting(int stepsBeforeShooting) {
        this.stepsBeforeShooting = stepsBeforeShooting;
    }

    /**
     * Steps before grabbing getter.
     * @return the steps that the player can do before the pick.
     */
    public int getStepsBeforeGrabbing() {
        return stepsBeforeGrabbing;
    }

    /**
     * Steps before grabbing setter.
     * @param stepsBeforeGrabbing it's the new parameter to set.
     */
    public void setStepsBeforeGrabbing(int stepsBeforeGrabbing) {
        this.stepsBeforeGrabbing = stepsBeforeGrabbing;
    }

    /**
     * It is the structure that keeps track of the player's marks
     * received from other players. It has been implemented using an array of
     * values corresponding to other players' colors.
     */
    private List<PlayerColor> marks;

    /**
     * These attributes are what characterize the action of a player during its turn.
     */
    private int stepsOfMovement;
    private int stepsBeforeShooting;
    private int stepsBeforeGrabbing;

    /**
     * Constructor for a player's board.
     *
     * @param boardColor corresponds to the color of the player and his character
     */
    public PlayerBoard(PlayerColor boardColor) {
        this.boardColor = boardColor;
        this.damage = new ArrayList<>();
        this.maxPoints = MAX_ASSIGNABLE_POINTS;
        this.marks = new ArrayList<>();
        this.stepsOfMovement = 3;
        this.stepsBeforeGrabbing = 1;
        this.stepsBeforeShooting = 0;
    }

    /**
     * Returns the collection of the player's damage points
     *
     * @return collection of the player's damage points
     */
    public List<PlayerColor> getDamage() {
        return new ArrayList<>(damage);
    }

    /**
     * This method is responsible for adding damages to the player board
     * and checking the correctness of the parameters.
     *
     * @param color color of the player's board and character who inferred the damage
     * @param n number of damages inferred (can't be negative)
     * @throws IllegalArgumentException if the same color of the player is asked to be added
     *         to the array or the number of damages is less than zero
     *
     */
    public boolean appendDamage(PlayerColor color, Integer n) {
        if (color == this.boardColor) { throw new IllegalArgumentException(COLOR_DAMAGE_MARKS_EXCEPTION); }
        if (n <= 0) { throw new IllegalArgumentException(INVALID_DAMAGES_NUMBER); }

        ArrayList<PlayerColor> toAdd = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            toAdd.add(color);
        }
        return damage.addAll(toAdd);
    }

    /**
     * Maximum number of points other players can get by killing a player
     * @return an integer corresponding to the maximum number of points other players can get by killing a player
     */
    public Integer getMaxPoints() {
        return maxPoints;
    }

    /**
     * Returns the collection of the player's marks
     *
     * @return collection of the player's marks
     */
    public List<PlayerColor> getMarks() {
        return new ArrayList<>(marks);
    }

    /**
     * This method is responsible for setting marks to the player board
     * and checking the correctness of the parameter.
     *
     * @param value collection of the player's marks
     * @throws IllegalArgumentException if the same color of the player is asked to be added to the array
     */
    public void setMarks(List<PlayerColor> value) {
        if (value.contains(this.boardColor)) { throw new IllegalArgumentException(COLOR_DAMAGE_MARKS_EXCEPTION); }
        this.marks = value;
    }

    /**
     * A method responsible for resetting the board, changing the
     * maximum number of points and wiping out damages or marks.
     */
    public void death() {
        flushDamage();
        flushMarks();
        decreaseMaxPoints();
    }

    /**
     * It decreases the maximum number of points another player can get
     * when the player who owns the board is dead.
     */
    private void decreaseMaxPoints() {
        maxPoints = maxPoints <= 2 ? 1 : maxPoints - 2;
    }

    /**
     * Wipes out the array of damages
     */
    private void flushDamage() {
        damage.clear();
    }

    /**
     * Wipes out the array of marks
     */
    private void flushMarks() {
        marks.clear();
    }

    /**
     * A boolean indicating if the player reached the first adrenaline phase
     * @return a boolean indicating if the player reached the first adrenaline phase
     */
    public boolean isAdrenalinic1() {
        return damage.size() >= ADRENALINE_1_MIN;
    }

    /**
     * A boolean indicating if the player reached the second adrenaline phase
     * @return a boolean indicating if the player reached the second adrenaline phase
     */
    public boolean isAdrenalinic2() {
        return damage.size() >= ADRENALINE_2_MIN;
    }

}