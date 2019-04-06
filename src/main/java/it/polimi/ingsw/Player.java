package it.polimi.ingsw;

/**
 * A class containing attributes that identify a single player
 * and methods used to keep track of user's progress in the game.
 *
 * @author Stefano Formicola
 */
public class Player {
    /**
     * String passed as message of IllegalArgumentException when is asked to add a value to player's points.
     */
    private static final String NEGATIVE_POINTS_EXC = "Cannot add negative points to player's points.";

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
     * Constructor: creates a new Player based on given user and character.
     * @param user is an instance of User
     * @param character is a unique character used by the player during the game
     */
    public Player(User user, Character character) {
        PlayerColor color = character.getColor();
        String nick = user.getUsername();

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
     * @return
     */
    public PlayerHand getPlayerHand() {
        return playerHand;
    }

    /**
     * @return
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
     * @param value the amount of new points to add
     */
    public void addPoints(Integer value) {
        if (value < 0) throw new IllegalArgumentException(NEGATIVE_POINTS_EXC);
        else this.points += value;
    }

}