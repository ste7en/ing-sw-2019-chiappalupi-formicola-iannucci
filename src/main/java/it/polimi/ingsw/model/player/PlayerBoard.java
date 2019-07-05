package it.polimi.ingsw.model.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Board used by each player to keep track of his points, damages, cubes and
 * every other detail needed to proceed during the game.
 *
 * @author Stefano Formicola
 */
public class PlayerBoard implements Serializable {

    private static final String COLOR_DAMAGE_MARKS_EXCEPTION                = "An attempt to add the same color of the player to his damages or marks has been found. A player can't infer damages or marks to himself.";
    private static final String INVALID_DAMAGES_NUMBER                      = "The number of damages should be greater than zero.";
    private static final Integer NUM_OF_MAX_ASSIGNABLE_POINTS               = 6;
    private static final Integer NUM_OF_MAX_ASSIGNABLE_POINTS_ADRENALINIC   = 4;
    private static final Integer ADRENALINE_1_MIN                           = 3;
    private static final Integer ADRENALINE_2_MIN                           = 6;

    /**
     * String that contains the path information to where the resources are located.
     */
    private static final String PATHNAME                 = "src" + File.separator + "main" + File.separator + "resources" + File.separator;
    private static final String PATHNAME_NOT_ADRENALINIC = "max_points_list.json";
    private static final String PATHNAME_ADRENALINIC     = "max_points_list_adrenalinic.json";

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
    private List<Integer> maxPoints;

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
    private int numOfActions;
    private boolean finalFrenzy;

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
     * Steps before grabbing getter.
     * @return the steps that the player can do before the pick.
     */
    public int getStepsBeforeGrabbing() {
        return stepsBeforeGrabbing;
    }

    /**
     * Number of possible action getter.
     * @return the number of actions that the player can do in his turn.
     */
    public int getNumOfActions() {
        return numOfActions;
    }

    /**
    * Number of possible action setter.
    * @param numOfActions the updated number of actions that the player can do in his turn.
    */
    public void setNumOfActions(int numOfActions) {
        this.numOfActions = numOfActions;
    }

    /**
     * Initializes the list of max points reading them from a json file.
     * @param adrenaline it's a boolean containing the information about the phase of the game
     */
    private void initializeMaxPoints(boolean adrenaline) {
        if(adrenaline) this.finalFrenzy = true;
        ObjectMapper objectMapper = new ObjectMapper();
        int numOfPoints = NUM_OF_MAX_ASSIGNABLE_POINTS;
        String jsonName = PATHNAME_NOT_ADRENALINIC;
        if(adrenaline) {
            numOfPoints = NUM_OF_MAX_ASSIGNABLE_POINTS_ADRENALINIC;
            jsonName = PATHNAME_ADRENALINIC;
        }
        Integer[] box = new Integer[numOfPoints];
        try {
            File json = new File(PATHNAME + jsonName);
            box = objectMapper.readValue(json, Integer[].class);
        } catch (IOException e) {
            AdrenalineLogger.error(e.toString());
        }
        this.maxPoints = new ArrayList<>(Arrays.asList(box));
    }

    /**
     * Updates the max points that can be taken from a player
     * @param finalFrenzy true if the game is in final frenzy
     */
    private void updateMaxPoints(boolean finalFrenzy) {
        if(finalFrenzy && !this.finalFrenzy) this.initializeMaxPoints(true);
        else if(this.maxPoints.size() > 1) this.maxPoints.remove(0);
    }

    /**
     * Sets the board to final frenzy mode.
     * @param firstPlayer it's the boolean containing the information about the first player
     */
    public void turnToFinalFrenzy(boolean firstPlayer) {
        if(damage.isEmpty())
            initializeMaxPoints(true);
        if(firstPlayer) {
            numOfActions = 1;
            stepsOfMovement = 0;
            stepsBeforeGrabbing = 3;
            stepsBeforeShooting = 2;
        } else {
            numOfActions = 2;
            stepsOfMovement = 4;
            stepsBeforeGrabbing = 2;
            stepsBeforeShooting = 1;
        }
    }

    /**
     * Constructor for a player's board.
     *
     * @param boardColor corresponds to the color of the player and his character
     */
    public PlayerBoard(PlayerColor boardColor) {
        this.boardColor = boardColor;
        this.damage = new ArrayList<>();
        initializeMaxPoints(false);
        this.marks = new ArrayList<>();
        this.stepsOfMovement = 3;
        this.stepsBeforeGrabbing = 1;
        this.stepsBeforeShooting = 0;
        this.numOfActions = 2;
        this.finalFrenzy = false;
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
    public void appendDamage(PlayerColor color, Integer n) {
        if (color == this.boardColor) { throw new IllegalArgumentException(COLOR_DAMAGE_MARKS_EXCEPTION); }
        if (n <= 0) { throw new IllegalArgumentException(INVALID_DAMAGES_NUMBER); }

        while(n > 0 && damage.size() < 13) {
            damage.add(color);
            n--;
        }

        if(isAdrenalinic1()) this.stepsBeforeGrabbing = 2;
        if(isAdrenalinic2()) this.stepsBeforeShooting = 1;
    }

    /**
     * Restores the situation of adrenaline after a death
     */
    private void backToReality() {
        this.stepsOfMovement = 3;
        this.stepsBeforeGrabbing = 1;
        this.stepsBeforeShooting = 0;
    }

    /**
     * Maximum number of points that other players can get by killing a player
     * @return a list of Integer that corresponds to the list of maximum number of points other players can get by killing a player
     */
    public List<Integer> getMaxPoints() {
        return new ArrayList<>(maxPoints);
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
     * This method adds marks to the player board
     * and checks the correctness of the addictions.
     *
     * @param color it's the color of the marks to add
     * @param value it's the number of marks to add
     */
    public void addMarks(PlayerColor color, Integer value) {
        if (color.equals(this.boardColor)) { throw new IllegalArgumentException(COLOR_DAMAGE_MARKS_EXCEPTION); }
        while(value > 0 && this.marks.size() <= 3) {
            marks.add(color);
            value--;
        }
    }

    /**
     * A method responsible for resetting the board, changing the
     * maximum number of points and wiping out damages or marks.
     * @param finalFrenzy it's true if the game is in final frenzy mode.
     */
    public void death(boolean finalFrenzy) {
        flushDamage();
        updateMaxPoints(finalFrenzy);
        if(!finalFrenzy) backToReality();
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