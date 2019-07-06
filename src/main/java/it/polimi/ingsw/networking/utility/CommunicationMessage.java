package it.polimi.ingsw.networking.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Messages sent through the network
 *
 * @author Stefano Formicola
 * @author Daniele Chiappalupi
 */
public enum CommunicationMessage implements Serializable {
    /**
     * Ping and Pong messages used to check client connectivity.
     * PING is sent by the server, PONG is the reply to the PING message and is sent by the client
     *
     * Arguments: none
     */
    PING,
    PONG,

    /**
     * An operation's timeoutInSeconds associated to a user expired
     */
    TIMEOUT_DID_EXPIRE,

    /**
     * User createUser messages.
     * CREATE_USER is sent by the client to the server to create a new User, the server will process the request and send a
     * CREATE_USER_OK or CREATE_USER_FAILED in case of success or failure.
     *
     * USER_JOIN_WAITING_ROOM is sent by the client when a CREATE_USER_OK is received, in order to join a waiting room or a game and a
     * USER_JOINED_WAITING_ROOM confirmation message will be sent.
     *
     * Arguments: <User.username_key, value>
     */
    CREATE_USER,
    CREATE_USER_OK,
    CREATE_USER_FAILED,
    USER_JOIN_WAITING_ROOM,
    USER_JOINED_WAITING_ROOM,

    /**
     * Sent by the server to the user when a new game is started or the user reconnects
     * Arguments: <User.username_key, value>, <GameLogic.gameID_key, value>
     */
    USER_JOINED_GAME,

    /**
     * Messages sent to handle the selection of a character for the game
     *
     * CHOOSE_CHARACTER is sent by the server to let the user choose a character,
     * the user will respond with a CHOOSE_CHARACTER message containing a the selected character.
     * A confirmation message will be sent.
     *
     * Arguments: <Character.character_list, value> in CHOOSE_CHARACTER server ---> client,
     *            <Character.character, value> in CHOOSE_CHARACTER client ---> server, CHARACTER_NOT_AVAILABLE and CHARACTER_CHOSEN_OK
     */
    CHOOSE_CHARACTER,
    CHARACTER_NOT_AVAILABLE,
    CHARACTER_CHOSEN_OK,

    /**
     * Messages sent to handle the selection of a map configuration
     *
     * CHOOSE_MAP is sent from the server to the client to ask the selection of a game map and it has no arguments
     * MAP_CHOSEN is sent from the client to the server to notify that the map has been chosen
     *
     * Arguments: <GameMap.gameMap_key, configuration>
     */
    CHOOSE_MAP,
    MAP_CHOSEN,

    /**
     * Messages sent to handle the selection of a spawn point
     *
     * CHOOSE_SPAWN_POINT is sent from the server to the client to notify that the the player should choose the spawn point (no arguments)
     * ASK_FOR_SPAWN_POINT is sent from the client to the server to ask for the powerups that will lead to a spawn point decision (no arguments)
     * DRAWN_SPAWN_POINT is sent from the server to the client to provide the two powerup cards that will be used to choose the spawn point
     * SPAWN_POINT_CHOSEN is sent from the client to the server to notify that the spawn point has been chosen from the user
     * SPAWN_AFTER_DEATH is sent from the server to the client to notify that the player is dead and should respawn,
     * SPAWN_POINT_AFTER_DEATH_CHOSEN is sent from the client to the server to notify that the decision of spawn point has been made
     *
     * Arguments: <Integer.toString(index), Powerup.toString> for DRAWN_SPAWN_POINT, SPAWN_AFTER_DEATH
     *            <Powerup.powerup_key/Powerup.spawnPowerup_key, Powerup::toString> for SPAWN_POINT_CHOSEN, SPAWN_POINT_AFTER_DEATH_CHOSEN
     */
    CHOOSE_SPAWN_POINT,
    ASK_FOR_SPAWN_POINT,
    DRAWN_SPAWN_POINT,
    SPAWN_POINT_CHOSEN,

    SPAWN_AFTER_DEATH,
    SPAWN_POINT_AFTER_DEATH_CHOSEN,

    /**
     * Skulls number decision message
     *
     * CHOOSE_SKULLS is sent from the server to the client to notify that the number of skulls should be chosen. (no arguments)
     * SKULLS_CHOSEN is sent from the client to the server to notify that the number of skulls has been chosen.
     *
     * Arguments: <Board.skulls_key, Integer::toString>
     */
    CHOOSE_SKULLS,
    SKULLS_CHOSEN,

    /**
     * Actions handling messages
     *
     * NEW_ACTION is sent from the client to the server to notify the start of a new action or turn from a player (no arguments)
     * CHOOSE_ACTION is sent from the server to the client to notify that the the player should choose the action to do
     * KEEP_ACTION is sent from the server to the client to notify that the player has to do other actions to complete his turn (no arguments)
     * END_ACTION is sent from the client to the server to notify that an action has been executed correctly from a player (no arguments)
     * AFTER_ACTION is sent from the server to the client to notify what to do after an action has been made (no arguments)
     * END_TURN is sent from the server to the client to notify that his turn has come to an end
     * TURN_ENDED is sent from the client to the server to notify that the turn has been successfully ended (no arguments)
     *
     * Arguments: <GameMap.gameMap_key, GameMap::toString> for END_TURN
     */
    NEW_ACTION,
    CHOOSE_ACTION,
    KEEP_ACTION,
    END_ACTION,
    AFTER_ACTION,
    END_TURN,
    TURN_ENDED,

    /**
     * Messages sent to start the process of grabbing something
     *
     * CHOOSE_ACTION is sent from the client to the server to notify that the the player has selected that he wants to grab something (no arguments)
     * POSSIBLE_PICKS is sent from the server to the client to provide the possible picks of the player
     * DID_CHOOSE_WHAT_TO_GRAB is sent from the client to the server to notify that the player has selected what does he want to grab
     * GRAB_SUCCESS is sent from the server to the client to notify that the process ended well
     * GRAB_FAILURE_WEAPON is sent from the server to the client to notify that the player has too much weapons to grab another one
     * GRAB_FAILURE_POWERUP is sent from the server to the client to notify that the player has too much powerups to grab another one
     * SELL_POWERUP_TO_GRAB is sent from the client to the server to notify that the player wants to sell his powerups to afford the cost of a weapon (no arguments)
     * AVAILABLE_POWERUP_TO_SELL_TO_GRAB is sent from the server to provide the list of powerups that the player has in his hand
     * GRAB_FAILURE is sent from the server to the client to notify that the process has gone wrong (no arguments)
     *
     * Arguments: <indexOf, possiblePick::toString> for POSSIBLE_PICKS, <AmmoTile.ammoTile_key, AmmoTile::toString> for DID_CHOOSE,
     *            <GameMap.gameMap_key, GameMap::toString> for GRAB_SUCCESS, <indexOf, Weapon::getName/Powerup::toString> for GRAB_FAILURES,
     *            <Powerup.powerup_key, Powerup::toString> for DISCARD_POWERUP, <Weapon.weapon_key, Weapon::getName> for DISCARD_WEAPON,
     *            <indexOf, Powerup::toString> for AVAILABLE_POWERUP, <indexOf, Powerup::toString> for POWERUP_TO_BE_SOLD
     */
    GRAB_SOMETHING,
    POSSIBLE_PICKS,
    DID_CHOOSE_WHAT_TO_GRAB,
    GRAB_SUCCESS,
    GRAB_FAILURE_WEAPON,
    GRAB_FAILURE_POWERUP,
    GRAB_DISCARD_POWERUP,
    GRAB_DISCARD_WEAPON,
    SELL_POWERUP_TO_GRAB,
    AVAILABLE_POWERUP_TO_SELL_TO_GRAB,
    GRAB_FAILURE,

    /**
     * Messages sent to implement player's movements on the board
     *
     * GET_AVAILABLE_MOVES is sent by the client without arguments
     * CHOOSE_MOVEMENT is sent by the server with arguments: <GameLogic.available_moves, values>
     * where values is a list of Strings joined as a single string with a comma separator
     * MOVE is sent by the client when a movement has been chosen with arguments: <GameLogic.movement, value>
     * GET_MOVES_BEFORE_SHOOT is sent from the client to the server to notify that the player has requested to shoot
     * MOVES_BEFORE_SHOOT is sent from the server to the client with the list of possible shots
     * MOVE_CHOSEN_BEFORE_SHOT is sent from the client to the server to notify the players decision
     *
     */
    GET_AVAILABLE_MOVES,
    CHOOSE_MOVEMENT,
    MOVE,
    GET_MOVES_BEFORE_SHOOT,
    MOVES_BEFORE_SHOOT,
    MOVE_CHOSEN_BEFORE_SHOT,
    NO_MOVES_BEFORE_SHOT,

    /**
     * Weapon using message.
     *
     * SHOOT_PEOPLE is sent both by the client to the server and vice versa to start the process of weapon using.
     *
     * Arguments: <indexOf_Weapon, Weapon_name>
     */
    SHOOT_PEOPLE,

    /**
     * Weapon failure messages.
     *
     * SHOOT_PEOPLE_FAILURE is sent by the server to the client if the player who wants to shoot doesn't have any weapon.
     * DAMAGE_FAILURE is sent by the server to the client if no damage can be done with the combination of effect and weapon selected.
     *
     * Arguments: none.
     */
    SHOOT_PEOPLE_FAILURE,
    DAMAGE_FAILURE,

    /**
     * Weapon selection message.
     *
     * WEAPON_TO_USE is sent by the client to the server to notify the selection of the Weapon to use in game.
     *
     * Arguments: <Weapon.weapon_key, Weapon_name>
     */
    WEAPON_TO_USE,

    /**
     * Damage selection message.
     *
     * DAMAGE_LIST is sent from the server to the client to ask for the damage to make with the Weapon that is being used.
     *
     * Arguments: <Integer index, Damages>, <Weapon.weapon_key, Weapon_name>, <Effect.effect_key, indexOf_effect>
     */
    DAMAGE_LIST,

    /**
     * Damage apply message.
     *
     * DAMAGE_TO_MAKE is sent from the client to the server to notify the selection of the damage that has to be applied to the other players.
     * LAST_DAMAGE acts just like DAMAGE_TO_MAKE, but signals that it will be the last damage of the weapon to be done.
     * DAMAGE_DONE is sent from the server to the client to notify that damage has been done
     * LAST_DAMAGE_DONE is sent from the server to the client to notify that the process of using the weapon is over. It has got only the weapon as argument.
     *
     * Arguments: <Damage.damage_key, damage>, <Weapon.weapon_key, Weapon_name>, <Effect.effect_key, indexOf_effect>
     */
    DAMAGE_TO_MAKE,
    LAST_DAMAGE,
    DAMAGE_DONE,
    LAST_DAMAGE_DONE,

    /**
     * Weapon using messages.
     *
     * POWERUP_SELLING_LIST is sent from the server to the client to ask if the player wants to use any powerup to pay the effect cost.
     * POWERUP_SELLING_DECIDED is sent from the client to the server to notify that the player has decided if and how sell his powerups.
     *
     * Arguments: <Integer index, powerup><Weapon.weapon_key, Weapon_name>
     */
    POWERUP_SELLING_LIST,
    POWERUP_SELLING_DECIDED,

    /**
     * Modalities selection message.
     *
     * MODE_LIST is sent from the server to the client to ask which modality does the player want to use.
     *
     * Arguments: <Integer index, Modality>
     */
    MODES_LIST,

    /**
     * Effects selection message.
     *
     * EFFECTS_LIST is sent from the server to the client to ask which effects does the player want to use.
     *
     * Arguments: <Integer index, Effect>
     */
    EFFECTS_LIST,

    /**
     * Effect selection message.
     *
     * EFFECT_TO_USE is sent by the client to the server to notify the selection of the Effect to use in game.
     *
     * Arguments: <Effect.effect_key, Effect_name>, <Weapon.weapon_key, Weapon_name>
     *     if (PotentiableWeapon): <PotentiableWeapon.potentiableWeapon_key, boolean> to save if the damage made has to be carried through the attack.
     */
    EFFECT_TO_USE,

    /**
     * Weapon using message.
     *
     * ASK_WEAPONS is sent by the client to the server to ask the weapons that the player has in his hand. The request has no arguments.
     * WEAPON_LIST is sent by the server to the client to provide the weapons that the player has in his hand.
     * NO_WEAPON_UNLOADED_IN_HAND is sent by the server to the client when there are no weapons unloaded in the hand of the player, but he wanted to reload.
     *
     * Arguments: <indexOf_Weapon, Weapon_name>
     */
    ASK_WEAPONS,
    WEAPON_LIST,
    NO_WEAPON_UNLOADED_IN_HAND,

    /**
     * Weapon reloading messages.
     *
     * WEAPON_TO_RELOAD is sent by the client to the server to notify the weapon that the player wants to reload in game.
     * RELOAD_WEAPON_OK or RELOAD_WEAPON_FAILED in case of success or failure.
     *
     * Arguments: <indexOf_Weapon, Weapon_name>
     */
    WEAPON_TO_RELOAD,
    RELOAD_WEAPON_OK,
    RELOAD_WEAPON_FAILED,

    /**
     * Weapon reloading messages.
     *
     * ASK_POWERUP_TO_RELOAD is sent by the client to the server to ask the list of powerup that the player has in his hand.
     * POWERUP_TO_RELOAD is sent by the server to the client to notify the list of powerup that the player has in his hand.
     * SELL_POWERUP is sent by the client to the server to send the list of powerup that the player has decided to sell to reload.
     *
     * Arguments: <indexOf_Powerup, Powerup::toString>
     */
    ASK_POWERUP_TO_RELOAD,
    POWERUP_TO_RELOAD,
    SELL_POWERUP,


    /**
     * Powerup selection messages.
     *
     * ASK_POWERUPS is sent by the client to the server to ask the powerups that the player has in his hand and can use during its turn. The request has no arguments.
     * NO_TURN_POWERUP is sent by the server to the client to notify that the latter hasn't got any powerup usable during the turn in his hand.
     * POWERUPS_LIST is sent by the server to the client to provide the powerups that the player has in his hand and can use during its turn.
     *
     * Arguments: <indexOf_Powerup, Powerup::toString>
     */
    ASK_POWERUPS,
    NO_TURN_POWERUP,
    POWERUP_LIST,

    /**
     * Powerup using messages.
     *
     * ASK_POWERUP_DAMAGES is sent by the client to the server to ask which damage can be done using the powerup in the arguments.
     *
     * Arguments: <Powerup.powerup_key, Powerup::toString>
     */
    ASK_POWERUP_DAMAGES,

    /**
     * Powerup using messages.
     *
     * POWERUP_DAMAGES_LIST is sent by the server to the client to provide the list of damages that can be done with the powerup.
     *
     * Arguments: <Powerup.powerup_key, Powerup::toString>, <indexOf_Damage, Damage::toString>
     */
    POWERUP_DAMAGES_LIST,

    /**
     * Powerup using messages.
     *
     * POWERUP_DAMAGE_TO_MAKE is sent by the client to the server to notify the selection of damage to make from the player.
     *
     * Arguments: <Powerup.powerup_key, Powerup::toString>, <Damage.damage_key, Damage::toString>
     */
    POWERUP_DAMAGE_TO_MAKE,

    /**
     * Tagback powerup using messages.
     *
     * TAGBACK_CHOSEN is sent from the client to the server to notify the selection of a powerup.
     * WILL_CHOOSE_TAGBACK is sent from the server to the client to notify the possibility of tagging back.
     * WONT_USE_TAGBACK is sent from the client to the server to notify that no tagging back will happen.
     *
     * Arguments: <Powerup.powerup_key, Powerup::toString> for CHOSEN, <indexOf(Powerup), Powerup::toString, <Player.player_key, Player.nickname> for WILL_CHOOSE
     */
    TAGBACK_CHOSEN,
    WILL_CHOOSE_TAGBACK,
    WONT_USE_TAGBACK,

    /**
     * View update messages.
     *
     * UPDATE_SITUATION is sent from the server to the client to notify that some other player has done something in his turn.
     * UPDATE_ALL is sent from the server to the client to notify any change.
     * DISPLAY_FINAL_FRENZY is sent from the server to the client to notify that final frenzy game has begun. Arguments: none
     * AWAKE is sent from the server to the client to notify all
     *
     * Arguments: <GameMap.gameMap_key, Board::toStringFromPlayer>
     */
    UPDATE_SITUATION,
    DISPLAY_FINAL_FRENZY,
    UPDATE_ALL,
    AWAKE,

    /**
     * Check deaths message.
     *
     * CHECK_DEATHS_AFTER_TURN is sent from the server to the client to notify that the turn is about to finish and the dead players should respawn.
     */
    CHECK_DEATHS_AFTER_TURN,

    /**
     * Signals the end of the game.
     *
     * GAME_ENDED is sent from the server to the client.
     *
     * Arguments: <GameMap.gameMap_key, Leaderboard>
     */
    GAME_ENDED;

    /**
     * String constant used in messages between client-server
     */
    public static final String communication_message_key = "COMMUNICATION_MESSAGE";

    /**
     * Log strings
     */
    private static final String EXC_MESS_JSON = "Exception while converting the message to json";
    private static final String EXC_JSON_MESS = "Exception while converting the json to message";

    /**
     * List delimiter when parsing strings to list and vice-versa
     */
    public static final String listDelimiter = ", ";

    /**
     * Private class used to create json messages
     */
    @SuppressWarnings("all")
    protected static class Message {
        int connectionID;
        CommunicationMessage message;
        Map<String, String> arguments;
        UUID gameID;
        int timeoutInSeconds;

        Message(int id, CommunicationMessage format, Map<String, String> arguments, UUID gameID) {
            this.connectionID = id;
            this.message = format;
            this.arguments = arguments;
            this.gameID = gameID;
        }

        Message(int id, CommunicationMessage format, Map<String, String> arguments) {
            this.connectionID = id;
            this.message = format;
            this.arguments = arguments;
        }

        Message(int id, CommunicationMessage format, Map<String, String> arguments, int timeoutInSeconds) {
            this.connectionID = id;
            this.message = format;
            this.arguments = arguments;
            this.timeoutInSeconds = timeoutInSeconds;
        }

        Message(int id, CommunicationMessage format, Map<String, String> arguments, UUID gameID, int timeoutInSeconds) {
            this.connectionID = id;
            this.message = format;
            this.arguments = arguments;
            this.gameID = gameID;
            this.timeoutInSeconds = timeoutInSeconds;
        }

        Message(int id, CommunicationMessage format, int timeoutInSeconds) {
            this.connectionID = id;
            this.message = format;
            this.timeoutInSeconds = timeoutInSeconds;
        }

        Message() {}

        public int getConnectionID() { return connectionID; }
        public Map<String, String> getArguments() { return arguments; }
        public int getTimeoutInSeconds() { return timeoutInSeconds; }
        public CommunicationMessage getMessage() { return message; }
        public UUID getGameID() { return gameID; }

    }

    private static String toJson(Message message) {
        var mapper = new ObjectMapper();
        var jsonMessage = "";

        try {
            jsonMessage = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            AdrenalineLogger.errorException(EXC_MESS_JSON, e);
        }
        return jsonMessage;
    }

    /**
     * Returns a json message from a connectionID and a CommunicationMessage identifier
     * @param id connectionID
     * @param format message identifier
     * @return json message
     */
    public static String from(int id, CommunicationMessage format) {
        return from(id, format, argsFactory());
    }

    /**
     * Returns a json message from a connectionID and a CommunicationMessage identifier
     * @param id connectionID
     * @param format message identifier
     * @param timeout timeout
     * @return json message
     */
    public static String from(int id, CommunicationMessage format, int timeout) {
        var message = new Message(id, format, timeout);
        return toJson(message);
    }

    /**
     * Returns a json message from a connectionID, a CommunicationMessage identifier and some arguments
     * @param id connectionID
     * @param format message identifier
     * @param args arguments
     * @return json message
     */
    public static String from(int id, CommunicationMessage format, Map<String, String> args) {
        var message = new Message(id, format, args);
        return toJson(message);
    }

    /**
     * Returns a json message from a connectionID, a CommunicationMessage identifier and some arguments
     * @param id connectionID
     * @param format message identifier
     * @param args arguments
     * @param timeout timeout
     * @return json message
     */
    public static String from(int id, CommunicationMessage format, Map<String, String> args, int timeout) {
        var message = new Message(id, format, args, timeout);
        return toJson(message);
    }

    /**
     * Returns a json message from a connectionID, a CommunicationMessage identifier and some arguments
     * @param id connectionID
     * @param format message identifier
     * @param args arguments
     * @param gameID game identifier
     * @return json message
     */
    public static String from(int id, CommunicationMessage format, Map<String, String> args, UUID gameID) {
        var message = new Message(id, format, args, gameID);
        return toJson(message);
    }

    /**
     * Returns a json message from a connectionID, a CommunicationMessage identifier and some arguments
     * @param id connectionID
     * @param format message identifier
     * @param args arguments
     * @param gameID game identifier
     * @param timeout timeout
     * @return json message
     */
    public static String from(int id, CommunicationMessage format, Map<String, String> args, UUID gameID, int timeout) {
        var message = new Message(id, format, args, gameID, timeout);
        return toJson(message);
    }

    /**
     * Returns a json message from a connectionID, a CommunicationMessage identifier and some arguments
     * @param id connectionID
     * @param format message identifier
     * @param gameID game identifier
     * @return json message
     */
    public static String from(int id, CommunicationMessage format, UUID gameID) {
        var message = new Message(id, format, argsFactory(), gameID);
        return toJson(message);
    }


    /**
     * Helper method when constructing an arguments map on socket communication
     * @return an empty <String, String> map
     */
    public static Map<String, String> argsFactory() {
        return new HashMap<>();
    }

    /**
     * Helper method when constructing an arguments map on socket communication
     * @param values the values in the map in the repeating order KEY, VALUE
     * @return a map <String, String> with the given arguments
     */
    public static Map<String, String> argsFrom(String... values) {
        var map = argsFactory();
        var li = Arrays.asList(values).iterator();

        while (li.hasNext()) map.put(li.next(), li.next());
        return map;
    }

    /**
     * Returns a Message instance from a json message
     * @param json message
     * @return Message object
     */
    private static Message getMessageFrom(String json) {
        var message = new Message();
        try {
            message = new ObjectMapper().readValue(json, Message.class);
            return message;
        } catch (IOException e) {
            AdrenalineLogger.errorException(EXC_JSON_MESS, e);
        }
        return message;
    }

    /**
     * Returns a message identifier
     * @param json message
     * @return message identifier
     */
    public static CommunicationMessage getCommunicationMessageFrom(String json) {
        var message = getMessageFrom(json);
        return message.getMessage();
    }

    /**
     * Returns the connectionID of the message
     * @param json message
     * @return connectionID of the message
     */
    public static int getConnectionIDFrom(String json) {
        return getMessageFrom(json).getConnectionID();
    }

    /**
     * Returns the arguments of the message
     * @param json message
     * @return message arguments
     */
    public static Map<String, String> getMessageArgsFrom(String json) {
        return getMessageFrom(json).getArguments();
    }

    /**
     * Returns the UUID of the game
     * @param json message
     * @return game identifier
     */
    public static UUID getMessageGameIDFrom(String json) {
        return getMessageFrom(json).getGameID();
    }

    /**
     * Returns a timeoutInSeconds for timed operations
     * @param json message
     * @return timeoutInSeconds
     */
    public static int getTimeoutFrom(String json) {
        return getMessageFrom(json).getTimeoutInSeconds();
    }
}