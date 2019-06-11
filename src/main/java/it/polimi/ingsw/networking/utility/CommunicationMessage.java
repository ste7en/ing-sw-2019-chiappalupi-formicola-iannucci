package it.polimi.ingsw.networking.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Messages sent through the network
 *
 * @author Stefano Formicola
 * @author Daniele Chiappalupi
 */
public enum CommunicationMessage {
    /**
     * Ping and Pong messages used to check client connectivity.
     * PING is sent by the server, PONG is the reply to the PING message and is sent by the client
     *
     * Arguments: none
     */
    PING,
    PONG,

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
     *            <User.username_key, value>
     *            <GameLogic.gameID_key, value>
     */
    CHOOSE_CHARACTER,
    CHARACTER_NOT_AVAILABLE,
    CHARACTER_CHOSEN_OK,


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
     * LAST_DAMAGE_DONE is sent from the server to the client to notify that the process of using the weapon is over. It has got only the weapon as argument.
     *
     * Arguments: <Damage.damage_key, damage>, <Weapon.weapon_key, Weapon_name>, <Effect.effect_key, indexOf_effect>
     */
    DAMAGE_TO_MAKE,
    LAST_DAMAGE,
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
    POWERUP_DAMAGE_TO_MAKE;

    /**
     * String constant used in messages between client-server
     */
    public final static String communication_message_key = "COMMUNICATION_MESSAGE";

    /**
     * Log strings
     */
    private static final String EXC_MESS_JSON = "Exception while converting the message to json";
    private static final String EXC_JSON_MESS = "Exception while converting the json to message";

    /**
     * Private class used to create json messages
     */
    protected static class Message {
        int connectionID;
        CommunicationMessage message;
        Map<String, String> arguments;
        UUID gameID;

        Message(int id, CommunicationMessage format, Map<String, String> arguments, UUID gameID) {
            this.connectionID = id;
            this.message = format;
            this.arguments = arguments;
            this.gameID = gameID;
        }

        Message() {}
        @SuppressWarnings("all")
        public int getConnectionID() { return connectionID; }
        public UUID getGameID() { return gameID; }
        public CommunicationMessage getMessage() { return message; }
        @SuppressWarnings("all")
        public Map<String, String> getArguments() { return arguments; }

    }

    /**
     * Returns a json message from a connectionID and a CommunicationMessage identifier
     * @param id connectionID
     * @param format message identifier
     * @return json message
     */
    public static String from(int id, CommunicationMessage format) {
        return from(id, format, new HashMap<>());
    }

    /**
     * Returns a json message from a connectionID, a CommunicationMessage identifier and some arguments
     * @param id connectionID
     * @param format message identifier
     * @param args arguments
     * @return json message
     */
    public static String from(int id, CommunicationMessage format, Map<String, String> args) {
        var message = new Message(id, format, args, UUID.randomUUID());
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
     * Returns a json message from a connectionID, a CommunicationMessage identifier and some arguments
     * @param id connectionID
     * @param format message identifier
     * @param args arguments
     * @param gameID game identifier
     * @return json message
     */
    public static String from(int id, CommunicationMessage format, Map<String, String> args, UUID gameID) {
        var message = new Message(id, format, args, gameID);
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
     * Returns a json message from a connectionID, a CommunicationMessage identifier and some arguments
     * @param id connectionID
     * @param format message identifier
     * @param gameID game identifier
     * @return json message
     */
    public static String from(int id, CommunicationMessage format, UUID gameID) {
        var message = new Message(id, format, new HashMap<>(), gameID);
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

}