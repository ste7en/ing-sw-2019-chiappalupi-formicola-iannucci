package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.player.Character;
import it.polimi.ingsw.model.utility.*;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main controller. It will handle everything in regards to the communication between view and model.
 * It will manipulate the model in the way that the players ask.
 *
 * @author Daniele Chiappalupi
 */
@SuppressWarnings("squid:S00115")
public class GameLogic implements Externalizable {

    /**
     * Configuration parameters and string of exceptions,
     */
    private static final int NUM_OF_WEAPONS_IN_SPAWNS       = 3;
    private static final int DEATH_BLOW_PLACE_ON_THE_BOARD  = 10;
    private static final int MAX_SIZE_OF_THE_BOARD          = 12;
    private static final String NO_PLAYER_WITH_THIS_COLOR   = "No player in the game has this color.";
    private static final String POWERUP_NOT_IN_HAND         = "This powerup is not one that you have in your hand!";
    private static final String WINNER = "The winner is: ";
    private static final String CONGRATULATIONS = "! Congratulations!\n";
    private static final String POSITION = "Position ";
    private static final String REMAINING_SKULLS = "Remaining skulls on the skulls track: ";
    private static final String NONE = "none";
    private static final String PLAYERS_BLOOD = "Players Blood:";
    private static final String NUMBER_OF_REMAINING_ACTIONS = "Number of remaining actions: ";

    private int numberOfPlayers;
    private int minNumberOfPlayers;
    private boolean finalFrenzy;
    private List<Player> players;
    private Board board;
    private UUID gameID;
    private DecksHandler decks;
    private WeaponController weaponController;
    private PowerupController powerupController;
    private GrabController grabController;
    private Player firstPlayer;
    private Player finalPlayer;
    private int numOfRemainingActions;
    private int numOfKillsDuringThisTurn;
    private boolean isUsingPowerup;
    private boolean hasAlreadyUpdated;

    /**
     * Instance variable used to distinguish the (temporary) absence
     * of connected users, after a GameLogic deserialization
     */
    private boolean _gameLoadedFromAPreviousSavedState;

    /**
     * Used to check if the game is finished or not
     */
    private boolean gameEnded = false;

    /**
     * Log strings
     */
    private static final String RECONNECTED     = " reconnected and resumed the game.";

    /**
     * String constants used in messages between client-server
     */
    public static final String gameID_key       = "GAME_ID";
    public static final String available_moves  = "MOVES";
    public static final String movement         = "MOVEMENT";

    /**
     * Is the player currently playing
     */
    private Player currentPlayer;

    /**
     * Game Logic constructor.
     *
     * @param gameID it's the gameID.
     */
    public GameLogic(UUID gameID, int minNumberOfPlayers, int numberOfPlayers) {
        this.decks = new DecksHandler();
        this.minNumberOfPlayers = minNumberOfPlayers;
        this.finalFrenzy = false;
        this.gameID = gameID;
        this.weaponController = new WeaponController();
        this.powerupController = new PowerupController();
        this.grabController = new GrabController();
        this.players = new ArrayList<>();
        this.numberOfPlayers = numberOfPlayers;
        this.numOfRemainingActions = -1;
        this.numOfKillsDuringThisTurn = 0;
        this.isUsingPowerup = false;

        this._gameLoadedFromAPreviousSavedState = false;
    }

    /**
     * Private constructor for deserialization
     */
    public GameLogic() {}

    /**
     * Starts the process of actions of a player
     * @return the number of actions that the player can still do
     */
    public String newAction(User user) {
        if(numOfRemainingActions == -1) numOfRemainingActions = lookForPlayerFromUser(user).getPlayerBoard().getNumOfActions();
        return this.situation(user);
    }

    public boolean isGameEnded() { return this.gameEnded; }

    /**
     * Number of remaining actions updater: called when a player has done an action successfully
     * @param user it's the user that is playing right now
     * @return the number of actions that the player can still do
     */
    public String updateNumOfRemainingActions(User user) {
        numOfRemainingActions--;
        if(numOfRemainingActions < 1) numOfRemainingActions = -1;
        if(numOfRemainingActions != -1) return null;
        return this.situation(user);
    }

    /**
     * It's the game UUID getter.
     *
     * @return the {@link UUID} of the game.
     */
    public UUID getGameID() {
        return gameID;
    }

    /**
     * Method called when a connected user disconnects
     * @param user old user instance
     */
    public void userDidDisconnect(User user) {
        Optional.of(lookForPlayerFromUser(user)).ifPresent(Player::disablePlayer);
    }

    /**
     * Method called when a disabled user reconnects
     * @param user new user instance
     */
    public void userDidConnect(User user) {
        Optional.of(lookForPlayerFromUser(user)).ifPresent(player -> player.reEnablePlayer(user));
        AdrenalineLogger.success(user.getUsername()+RECONNECTED);
    }

    /**
     * It's the grab controller getter.
     *
     * @return the {@link GrabController} of the game.
     */
    public GrabController getGrabController() {
        return grabController;
    }

    /**
     * It's the powerup controller getter.
     *
     * @return the {@link PowerupController} of the game.
     */
    public PowerupController getPowerupController() {
        return powerupController;
    }

    /**
     * It's the weapon controller getter.
     *
     * @return the {@link WeaponController} of the game.
     */
    public WeaponController getWeaponController() {
        return weaponController;
    }

    /**
     * It's the player list getter.
     *
     * @return a clone of the list of the player in game.
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }


    /**
     * @return the number of active players
     */
    public synchronized int numberOfActivePlayers() { return players.stream().filter(Player::isActive).toArray().length; }

    /**
     * First player getter
     * @return the first player in the game
     */
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * First active player getter
     * @return the first player in the game
     */
    @SuppressWarnings("all")
    public synchronized Player getFirstActivePlayer() {
        return players.stream().filter(Player::isActive).findFirst().get();
    }

    /**
     * First player setter
     * @param firstPlayer it's the player to set
     */
    private void setFirstPlayer(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    /**
     * Adds a player to the list of players in game.
     *
     * @param player it's the player to be added.
     * @return true if the numberOfPlayers Players have been added
     */
    public synchronized boolean addPlayer(Player player) {
        if (players.isEmpty()) {
            setFirstPlayer(player);
            currentPlayer = player;
        }
        this.players.add(player);
        return this.players.size() == numberOfPlayers;
    }

    /**
     * Board getter.
     *
     * @return the Board of the game.
     */
    public DecksHandler getDecks() {
        return decks;
    }

    /**
     * Board getter.
     *
     * @return the Board of the game.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Initializes the map of a game when the configuration has been chosen.
     *
     * @param mapType it's the MapType.toString() of the chosen configuration.
     */
    public void initializeMap(MapType mapType) {
        GameMap map = new GameMap(mapType);
        Map<AmmoColor, List<Weapon>> weapons = new EnumMap<>(AmmoColor.class);
        List<AmmoColor> spawnColorList = new ArrayList<>();
        spawnColorList.add(AmmoColor.blue);
        spawnColorList.add(AmmoColor.red);
        spawnColorList.add(AmmoColor.yellow);
        for(AmmoColor color : spawnColorList) {
            List<Weapon> weaponTrio = new ArrayList<>();
            for(int i = 0; i < NUM_OF_WEAPONS_IN_SPAWNS; i++)
                weaponTrio.add(decks.drawWeapon());
            weapons.put(color, weaponTrio);
        }
        for(int i = 0; i < map.getRows(); i++)
            for(int j = 0; j < map.getColumns(); j++)
                if(map.getCell(i, j) != null)
                    map.getCell(i, j).setAmmoCard(decks.drawAmmoTile());
        this.board = new Board(map, weapons);
    }

    /**
     * This method let an user spawn in the decided color and adds the other powerup to the user's hand.
     * @param user it's the user who is spawning.
     * @param spawnPoint it's the Powerup::toString of the powerup that the player has chosen to discard.
     * @param otherPowerup it's the Powerup::toString of the powerup that the player will have in his hand.
     */
    public void spawn(User user, String spawnPoint, String otherPowerup) {
        if(!isAlreadyUpdated()) hasAlreadyUpdated = true;
        Player player = lookForPlayerFromUser(user);

        Powerup spawnPowerup = decks.drawPowerup();
        while(!spawnPowerup.toString().equalsIgnoreCase(spawnPoint)) {
            decks.wastePowerup(spawnPowerup);
            spawnPowerup = decks.drawPowerup();
        }

        Powerup powerupInHand = decks.drawPowerup();
        while(!powerupInHand.toString().equalsIgnoreCase(otherPowerup)) {
            decks.wastePowerup(powerupInHand);
            powerupInHand = decks.drawPowerup();
        }

        AmmoColor spawnColor = spawnPowerup.getColor();

        this.board.getMap().setPlayerPosition(player, this.board.getMap().getSpawnPoint(spawnColor));
        player.getPlayerHand().addPowerup(powerupInHand);
        this.decks.wastePowerup(spawnPowerup);
    }

    /**
     * Skulls setter.
     *
     * @param skulls it's the number of skulls that will be in the game.
     */
    public void setSkulls(int skulls) {
        this.board.setSkulls(skulls);
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

    /**
     * This method is used to get the available characters.
     * @return a list of available player colors.
     */
    public synchronized List<String> getAvailableCharacters() {
        return Character.getCharacters()
                .stream()
                .filter(c -> !players.stream()
                        .map(Player::getCharacter)
                        .collect(Collectors.toList())
                        .contains(c))
                .map(Character::toString)
                .collect(Collectors.toList());
    }

    public List<String> getAvailableMoves(User user){
        ArrayList<String> availableMoves = new ArrayList<>();
        List<Cell> availableCells = getBoard().getMap().getCellsAtMaxDistance(lookForPlayerFromUser(user), lookForPlayerFromUser(user).getPlayerBoard().getStepsOfMovement());
        for(Cell cell : availableCells){
            availableMoves.add(cell.toString());
        }
        return availableMoves;
    }

    /**
     * Moves the player to the specified cell in the map
     * @param user user
     * @param movement string description of the movement
     */
    public void movePlayer(User user, String movement) {
        var map = getBoard().getMap();
        var player = lookForPlayerFromUser(user);
        var availableCells = map.getCellsAtMaxDistance(player, player.getPlayerBoard().getStepsOfMovement());
        availableCells.forEach( cell -> { if (cell.toString().equalsIgnoreCase(movement))map.setPlayerPosition(player, cell); });
    }

    /**
     * This method is used to return the string containing the situation of the board.
     * @param user it's the user who the situation is being looked for
     * @return a String containing the situation of the user
     */
    private String situation(User user) {
        return toStringFromPlayers(user) + "\n" + NUMBER_OF_REMAINING_ACTIONS + numOfRemainingActions + "\n";
    }

    /**
     * Method that stamps the situation of the board.
     * @param user it's the user that is playing right now.
     * @return the String containing the board situation.
     */
    public String toStringFromPlayers(User user) {
        StringBuilder stringBuilder = new StringBuilder(board.toStringFromPlayer(lookForPlayerFromUser(user)));
        stringBuilder.append(PLAYERS_BLOOD).append("\n");
        for(Player player : players) {
            List<PlayerColor> blood = player.getPlayerBoard().getDamage();
            List<PlayerColor> marks = player.getPlayerBoard().getMarks();
            String bloodString = NONE;
            if(!blood.isEmpty()) bloodString = blood.toString();
            String markString = NONE;
            if(!blood.isEmpty()) markString = marks.toString();
            stringBuilder.append("\t").append(player.getNickname()).append(": ").append(" blood -> ").append(bloodString).append("; maxPoints -> ").append(player.getPlayerBoard().getMaxPoints()).append("; marks -> ").append(markString).append(";\n");
        }
        stringBuilder.append(REMAINING_SKULLS).append(board.skullsLeft()).append(";\n");
        return stringBuilder.toString();
    }

    /**
     * This method is used to know which one is the active player after the one who is playing
     * @param user it's the user who is playing right now
     * @return the next user
     */
    public synchronized User getNextPlayer(User user) {
        Player actualPlayer = lookForPlayerFromUser(user);
        int index = players.indexOf(actualPlayer);
        Player nextPlayer = null;
        index++;
        while(index < players.size()) {
            if(players.get(index).isActive()) nextPlayer = players.get(index);
            if(nextPlayer != null) index = players.size();
            else index++;
        }
        if(nextPlayer != null) return nextPlayer.getUser();
        index = 0;
        while(index < players.size()) {
            if(players.get(index).isActive()) nextPlayer = players.get(index);
            if(nextPlayer != null) index = players.size();
            else index++;
        }
        if(nextPlayer == actualPlayer || nextPlayer == null) throw new IllegalArgumentException("No next player found!");

        currentPlayer = nextPlayer;

        return nextPlayer.getUser();
    }

    /**
     * This method is used to know if this was the last turn;
     */
    public boolean isThisTheEnd(User user, User nextUser) {
        if (numberOfActivePlayers() < minNumberOfPlayers) {
            gameEnded = true;
            return true;
        }
        Player actualPlayer = lookForPlayerFromUser(user);
        Player nextPlayer = lookForPlayerFromUser(nextUser);
        if (nextPlayer.equals(finalPlayer)) {
            gameEnded = true;
            return true;
        }
        int indexOfActualPlayer = players.indexOf(actualPlayer);
        int indexOfNextPlayer = players.indexOf(nextPlayer);
        int indexOfLastPlayer = players.indexOf(finalPlayer);
        if (indexOfNextPlayer > indexOfLastPlayer && indexOfActualPlayer < indexOfLastPlayer) {
            gameEnded = true;
            return true;
        } else {
            gameEnded = (indexOfNextPlayer < indexOfActualPlayer && indexOfActualPlayer < indexOfLastPlayer);
            return gameEnded;
        }
    }

    /**
     * This method is used to return all the players that are playing except the selected one.
     * @param user it's the user who is playing right now
     * @return a list containing the users looked for
     */
    public List<User> getOtherUsers(User user) {
        List<User> otherUsers = new ArrayList<>();
        for(Player player : players)
            if(player.getUser() != user && player.isActive())
                otherUsers.add(player.getUser());
        return otherUsers;
    }

    /**
     * This method returns the info about if the user has already spawned
     * @param user it's the user to get the information from
     * @return true if the user has already spawned, false otherwise
     */
    public Boolean isAlreadyInGame(User user) {
        return board.getMap().getPlayersPosition().containsKey(lookForPlayerFromUser(user));
    }

    /**
     * Restores weapons and ammo tiles where they are missing
     * @param nextUser it's the player that will play the next turn
     */
    public void endTurn(User nextUser) {
        this.weaponController.restore(players);
        this.grabController.restore();
        this.powerupController.clearPowerupTargets();
        this.board.refillWeapons(decks);
        this.board.getMap().refillAmmoTiles(decks);
        this.numOfKillsDuringThisTurn = 0;
        if(!finalFrenzy && this.board.isFinalFrenzy()) turnToFinalFrenzy(lookForPlayerFromUser(nextUser));
    }

    /**
     * Looks for a player from its color.
     * @param color it's the color of the player.
     * @return the player with that color
     * @throws IllegalArgumentException if no player with this color exists
     */
    private Player lookForPlayerFromColor(PlayerColor color) {
        for(Player player : players)
            if(player.getCharacter().getColor().equals(color))
                return player;
        throw new IllegalArgumentException(NO_PLAYER_WITH_THIS_COLOR);
    }

    /**
     * Assigns the point corresponding to a player death
     * @param user it's the user that is dying
     */
    private void assignPointFromUser(User user) {
        Player player = lookForPlayerFromUser(user);
        board.getMap().setPlayerPosition(player, null);
        List<PlayerColor> damage = player.getPlayerBoard().getDamage();
        List<Integer> points = player.getPlayerBoard().getMaxPoints();
        if(this.numOfKillsDuringThisTurn == 1 && !damage.isEmpty()) lookForPlayerFromColor(damage.get(DEATH_BLOW_PLACE_ON_THE_BOARD)).addPoints(1);
        numOfKillsDuringThisTurn++;
        assignPointsFromDamage(damage, points);
        if(!finalFrenzy && !damage.isEmpty()) Optional.of(damage.get(0)).ifPresent(playerColor -> lookForPlayerFromColor(playerColor).addPoints(1));
        int numOfBloodInTheSkullsTrack = 1;
        if(damage.size() == MAX_SIZE_OF_THE_BOARD) {
            lookForPlayerFromColor(damage.get(MAX_SIZE_OF_THE_BOARD - 1)).getPlayerBoard().addMarks(player.getCharacter().getColor(), 1);
            numOfBloodInTheSkullsTrack = 2;
        }
        if(!finalFrenzy && !damage.isEmpty() && damage.size() >= DEATH_BLOW_PLACE_ON_THE_BOARD) board.addBloodFrom(damage.get(DEATH_BLOW_PLACE_ON_THE_BOARD), numOfBloodInTheSkullsTrack);
        player.getPlayerBoard().death(finalFrenzy);
    }

    /**
     * Helper method to avoid cognitive complexity: assigns the points to the corresponding players.
     * @param damage it's the list of damage that the dead player has taken.
     * @param points it's the list of points that should be given.
     */
    @SuppressWarnings({"squid:S3776", "Duplicates"})
    private void assignPointsFromDamage(List<PlayerColor> damage, List<Integer> points) {
        Map<PlayerColor, Integer> pointsFromColor = new EnumMap<>(PlayerColor.class);
        for(PlayerColor color : damage) {
            Integer oldPoints = pointsFromColor.get(color);
            if(oldPoints == null) oldPoints = 1;
            else oldPoints++;
            pointsFromColor.put(color, oldPoints);
        }
        while(!pointsFromColor.isEmpty()) {
            List<PlayerColor> bests = new ArrayList<>();
            int max = -1;
            for(Map.Entry<PlayerColor, Integer> entry : pointsFromColor.entrySet())
                if(entry.getValue() > max)
                    max = entry.getValue();
            for(Map.Entry<PlayerColor, Integer> entry : pointsFromColor.entrySet())
                if(entry.getValue() == max)
                    bests.add(entry.getKey());
            PlayerColor bestColor;
            if(bests.size() == 1) bestColor = bests.get(0);
            else {
                int firstOccurrence = damage.size() + 1;
                for(PlayerColor color : bests)
                    if(damage.indexOf(color) < firstOccurrence)
                        firstOccurrence = damage.indexOf(color);
                bestColor = damage.get(firstOccurrence);
            }
            Player bestPlayer = lookForPlayerFromColor(bestColor);
            bestPlayer.addPoints(points.get(0));
            if(points.size() > 1) points.remove(0);
            pointsFromColor.remove(bestColor);
        }
    }

    /**
     * Public method that checks if a player is death after he has been shot.
     * @param player it's the player to check.
     */
    public void checkDeath(Player player) {
        if(player.isDead()) board.getMap().setPlayerPosition(player, null);
    }

    /**
     * Public method that returns the list of dead users to let them respawn.
     * @return the list of user whose player is dead.
     */
    public List<User> deathList() {
        List<User> dead = new ArrayList<>();
        for(Player player : players)
            if(player.isDead() || (board.getMap().getPlayersPosition().containsKey(player) && board.getMap().getPositionFromPlayer(player) == null))
                dead.add(player.getUser());
        for(User user : dead) assignPointFromUser(user);
        return dead;
    }

    public boolean isEveryoneAlive() {
        List<Player> deads = new ArrayList<>();
        for(Player player : players) {
            if(player.isDead() || ((board.getMap().getPlayersPosition().containsKey(player) && board.getMap().getPositionFromPlayer(player) == null)))
                deads.add(player);
        }
        return deads.isEmpty();
    }

    /**
     * Public method that returns the list of Powerup::toString that can be used to respawn.
     * @param user it's the user that should respawn.
     * @return the list of Powerup::toString looked for.
     */
    public List<String> getPowerupToSpawn(User user) {
        List<String> spawnPows = new ArrayList<>();
        Player player = lookForPlayerFromUser(user);
        player.getPlayerHand().addPowerup(decks.drawPowerup());
        List<Powerup> pows = player.getPlayerHand().getPowerups();
        for(Powerup pow : pows) spawnPows.add(pow.toString());
        return spawnPows;
    }

    /**
     * Public method that lets a player spawn.
     * @param user it's the user of the player that should spawn.
     * @param powerup it's the powerup chosen to spawn.
     */
    public void spawnAfterDeath(User user, String powerup) {
        List<Powerup> powerupsInHand = lookForPlayerFromUser(user).getPlayerHand().getPowerups();
        Powerup spawn = null;
        for(Powerup pow : powerupsInHand)
            if(pow.toString().equals(powerup))
                spawn = pow;
        if(spawn == null) throw new IllegalArgumentException(POWERUP_NOT_IN_HAND);
        lookForPlayerFromUser(user).getPlayerHand().wastePowerup(spawn);
        Cell spawnCell = board.getMap().getSpawnPoint(spawn.getColor());
        board.getMap().setPlayerPosition(lookForPlayerFromUser(user), spawnCell);
        decks.wastePowerup(spawn);
    }

    /**
     * Public method that turns the game to final frenzy mode.
     * @param nextPlayer it's the player that will play the next turn; (the last player of the game)
     */
    private void turnToFinalFrenzy(Player nextPlayer) {
        this.finalFrenzy = true;
        for(Player player : players)
            if(player.getPlayerBoard().getDamage().isEmpty())
                player.getPlayerBoard().turnToFinalFrenzy(player.equals(firstPlayer));
        this.finalPlayer = nextPlayer;
    }

    /**
     * Returns the state of the game
     * @return true if the game is in final frenzy mode
     */
    public boolean isFinalFrenzy() {
        return finalFrenzy;
    }

    public String endOfTheGame() {
        for(Player player : players)
            assignPointFromUser(player.getUser());
        this.board.assignBlood();
        int points = -1;
        StringBuilder scoreBoardBuilder = new StringBuilder();
        List<Player> playerLeaderboard = new ArrayList<>();
        List<Player> playersBox = new ArrayList<>(players);
        while(playerLeaderboard.size() != players.size()) {
            int box = -1;
            for(Player player : playersBox)
                if(player.getPoints() > box) box = player.getPoints();
            List<Player> toRemove = new ArrayList<>();
            for(Player player : playersBox)
                if(player.getPoints() == box) {
                    playerLeaderboard.add(player);
                    toRemove.add(player);
                }
            playersBox.removeAll(toRemove);
        }
        scoreBoardBuilder.append(WINNER).append(playerLeaderboard.get(0).getNickname()).append(CONGRATULATIONS);
        for(int i = 1; i < playerLeaderboard.size(); i++) {
            scoreBoardBuilder.append(POSITION).append(i+1).append(": ").append(playerLeaderboard.get(i).getNickname()).append(";\n");
        }
        return scoreBoardBuilder.toString();
    }

    public List<String> movesBeforeShot(User user) {
        List<String> movements = new ArrayList<>();
        int possibleMoves = lookForPlayerFromUser(user).getPlayerBoard().getStepsBeforeShooting();
        List<Cell> movementsInCell = new ArrayList<>();
        if(possibleMoves > 0) movementsInCell = board.getMap().getCellsAtMaxDistance(lookForPlayerFromUser(user), possibleMoves);
        if(!movementsInCell.isEmpty()) {
            movementsInCell.add(board.getMap().getCellFromPlayer(lookForPlayerFromUser(user)));
            for(Cell move : movementsInCell)
                movements.add(move.toString());
        }
        return movements;
    }

    public Map<String, List<String>> getUpdates(User user) {
        Map<String, List<String>> updates = new HashMap<>();
        Player player = lookForPlayerFromUser(user);

        List<String> updateString = new ArrayList<>();
        List<Weapon> weaponInHand = player.getPlayerHand().getWeapons();
        for(Weapon weapon : weaponInHand) updateString.add(weapon.getName());
        updates.put(Weapon.weapon_key, updateString);

        updateString = new ArrayList<>();
        List<Powerup> powerupInHand = player.getPlayerHand().getPowerups();
        for(Powerup powerup : powerupInHand) updateString.add(powerup.toString());
        updates.put(Powerup.powerup_key, updateString);

        for(AmmoColor color : AmmoColor.values()) {
            updateString = new ArrayList<>();
            updateString.add(Integer.toString(player.getPlayerHand().getAmmosAmount(color)));
            String key = "";
            switch(color) {
                case red:
                    key = AmmoColor.ammoColorKey_red;
                    break;
                case yellow:
                    key = AmmoColor.ammoColorKey_yellow;
                    break;
                case blue:
                    key = AmmoColor.ammoColorKey_blue;
                    break;
            }
            updates.put(key, updateString);

            updateString = new ArrayList<>();
            List<Weapon> weaponsInRespawn = board.showWeapons(color);
            for(Weapon weapon : weaponsInRespawn)
                if(weapon != null)
                    updateString.add(weapon.getName());
            updates.put(board.getWeaponKeyFromColor(color), updateString);
        }

        List<PlayerColor> damageInHand;

        for(Player p : players) {
                updateString = new ArrayList<>();
                damageInHand = p.getPlayerBoard().getDamage();
                for(PlayerColor color : damageInHand) updateString.add(color.toString());
                updates.put(p.keyDamage(), updateString);
        }

        List<PlayerColor> marksOnBoard;

        for(Player p : players) {
            updateString = new ArrayList<>();
            marksOnBoard = player.getPlayerBoard().getMarks();
            for(PlayerColor color : marksOnBoard) updateString.add(color.toString());
            updates.put(p.keyMark(), updateString);
        }

        for(int i = 0; i < 3; i++) {
            updateString = new ArrayList<>();
            List<AmmoTile> tilesInARow = board.getMap().tilesInARow(i);
            for(AmmoTile tile : tilesInARow) updateString.add(tile.guiString());
            String key = GameMap.ROW_1;
            if(i == 1) key = GameMap.ROW_2;
            else if(i == 2) key = GameMap.ROW_3;
            updates.put(key, updateString);
        }

        return updates;
    }

    public boolean playerCanSeeOther(Player player, Player target) {
        return board.getMap().getSeenTargets(player).contains(target);
    }

    public User getCurrentUser() {
        return currentPlayer.getUser();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(true);
        out.writeBoolean(gameEnded);
        out.writeInt(minNumberOfPlayers);
        out.writeInt(numberOfPlayers);
        out.writeBoolean(finalFrenzy);
        out.writeObject(currentPlayer);
        out.writeObject(players);
        out.writeObject(board);
        out.writeObject(gameID);
        out.writeObject(decks);
        out.writeObject(weaponController);
        out.writeObject(powerupController);
        out.writeObject(grabController);
        out.writeObject(firstPlayer);
        out.writeObject(finalPlayer);
        out.writeInt(numOfRemainingActions);
        out.writeInt(numOfKillsDuringThisTurn);

    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        _gameLoadedFromAPreviousSavedState  = in.readBoolean();
        gameEnded                           = in.readBoolean();
        minNumberOfPlayers                  = in.readInt();
        numberOfPlayers                     = in.readInt();
        finalFrenzy                         = in.readBoolean();
        currentPlayer                       = (Player)in.readObject();
        players                             = (List<Player>) in.readObject();
        board                               = (Board) in.readObject();
        gameID                              = (UUID) in.readObject();
        decks                               = (DecksHandler) in.readObject();
        weaponController                    = (WeaponController) in.readObject();
        powerupController                   = (PowerupController) in.readObject();
        grabController                      = (GrabController) in.readObject();
        firstPlayer                         = (Player) in.readObject();
        finalPlayer                         = (Player) in.readObject();
        numOfRemainingActions               = in.readInt();
        numOfKillsDuringThisTurn            = in.readInt();

        this.players.forEach(Player::disablePlayer);
    }

    public boolean isUsingPowerup() {
        return isUsingPowerup;
    }

    public void setUsingPowerup(boolean usingPowerup) {
        isUsingPowerup = usingPowerup;
    }

    public boolean isAlreadyUpdated() { return hasAlreadyUpdated; }
}