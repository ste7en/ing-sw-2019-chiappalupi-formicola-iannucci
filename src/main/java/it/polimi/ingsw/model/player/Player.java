package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.utility.PlayerColor;

import java.io.Serializable;

/**
 * A class containing attributes that identify a single player
 * and methods used to keep track of user's progress in the game.
 *
 * @author Stefano Formicola
 */
public class Player implements Comparable<Player>, Serializable {
    /**
     * String passed as message of IllegalArgumentException when is asked to add a value to player's points.
     */
    private static final String NEGATIVE_POINTS_EXC = "Cannot add negative points to player's points.";

    public static final String playerKey_damages_yellow    = "DAMAGES-PLAYER-YELLOW";
    public static final String playerKey_damages_blue      = "DAMAGES-PLAYER-BLUE";
    public static final String playerKey_damages_green     = "DAMAGES-PLAYER-GREEN";
    public static final String playerKey_damages_grey      = "DAMAGES-PLAYER-GREY";
    public static final String playerKey_damages_purple    = "DAMAGES-PLAYER-PURPLE";

    public static final String playerKey_marks_yellow      = "DAMAGES-MARKS-YELLOW";
    public static final String playerKey_marks_blue        = "DAMAGES-MARKS-BLUE";
    public static final String playerKey_marks_green       = "DAMAGES-MARKS-GREEN";
    public static final String playerKey_marks_grey        = "DAMAGES-MARKS-GREY";
    public static final String playerKey_marks_purple      = "DAMAGES-MARKS-PURPLE";
    public static final String playerKey_players           = "PLAYERS-IN-GAME";
    public static final String playerKey_player            = "PLAYER";

    private static final String KEY_DOES_NOT_EXISTS = "Player key does not exists.";

    /**
     * It is the board used to keep track of a player's
     * points, damages, marks during the game.
     */
    private final PlayerBoard playerBoard;

    /**
     * It is the class responsible of handling player's
     * weapons, powerups and cubes used during the game.
     */
    private final PlayerHand playerHand = new PlayerHand();

    /**
     * It is a unique character identifying the
     * player during the game.
     */
    private final Character character;

    /**
     * The user joining the multiplayer game
     * and identified by a unique identifier.
     */
    private User user;

    /**
     * Nickname of the player.
     */
    private String nickname;

    /**
     * Points achieved during the game.
     */
    private Integer points = 0;

    /**
     * checks if the user is connected
     */
    private boolean _isActive = true;

    /**
     * Called when a user disconnects
     */
    public synchronized void disablePlayer() {
        _isActive = false;
    }

    /**
     * Called when a user reconnects
     * @param user new user instance
     */
    public synchronized void reEnablePlayer(User user) {
        this.user = user;
        _isActive = true;
    }

    /**
     * @return true if the user is connected to the server, false otherwise
     */
    public boolean isActive() {
        return _isActive;
    }

    /**
     * Constructor: creates a new Player based on given user and character.
     * @param user is an instance of User
     * @param character is a unique character used by the player during the game
     */
    public Player(User user, Character character) {
        PlayerColor color = character.getColor();
        String nick = user.getUsername();

        this.user = user;
        this.character = character;
        this.playerBoard = new PlayerBoard(color);
        this.nickname = nick;
    }

    /**
     * @return a PlayerBoard instance
     */
    public PlayerBoard getPlayerBoard() {
        return playerBoard;
    }

    /**
     * @return player's playerHand
     */
    public PlayerHand getPlayerHand() {
        return playerHand;
    }

    /**
     * @return player's character
     */
    public Character getCharacter() {
        return character;
    }

    /**
     * @return the nickname used by the player
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return the amount of points of the player
     */
    public Integer getPoints() {
        return points;
    }

    /**
     * @return the user associated to the Player
     */
    public User getUser() { return user; }

    /**
     * @param value the amount of new points to add
     * @throws IllegalArgumentException if a value less than zero is passed as parameter
     */
    public void addPoints(Integer value) {
        if (value < 0) throw new IllegalArgumentException(NEGATIVE_POINTS_EXC);
        else this.points += value;
    }

    @Override
    public int compareTo(Player anotherPlayer) {
        return this.nickname.compareToIgnoreCase(anotherPlayer.getNickname());
    }

    /**
     * Returns the state of life of the player.
     * @return true if the player is death, false otherwise.
     */
    public boolean isDead() {
        return playerBoard.getDamage().size() > 10;
    }

    /**
     * Overrode toString for debugging purposes
     * @return the nickname of the player
     */
    @Override
    public String toString() {
        return nickname;
    }

    public String keyDamage() {
        return getString(playerKey_damages_yellow, playerKey_damages_blue, playerKey_damages_purple, playerKey_damages_grey, playerKey_damages_green);
    }

    public String keyMark() {
        return getString(playerKey_marks_yellow, playerKey_marks_blue, playerKey_marks_purple, playerKey_marks_grey, playerKey_marks_green);
    }

    private String getString(String yellowKey, String blueKey, String purpleKey, String greyKey, String greenKey) {
        switch(character.getColor()) {
            case yellow:
                return yellowKey;
            case blue:
                return blueKey;
            case purple:
                return purpleKey;
            case grey:
                return greyKey;
            case green:
                return greenKey;
        }
        throw new NullPointerException(KEY_DOES_NOT_EXISTS);
    }

}