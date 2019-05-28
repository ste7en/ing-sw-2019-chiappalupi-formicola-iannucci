package it.polimi.ingsw.networking.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Messages sent through the network
 *
 * @author Stefano Formicola
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
     * User login messages.
     * CREATE_USER is sent by the client to the server to create a new User, the server will process the request and send a
     * CREATE_USER_OK or CREATE_USER_FAILED in case of success or failure.
     *
     * USER_LOGIN is sent by the client when a CREATE_USER_OK is received, in order to join a waiting room or a game.
     *
     * Arguments: <User.username_key, value>
     */
    CREATE_USER,
    CREATE_USER_OK,
    CREATE_USER_FAILED,
    USER_LOGIN;


    /**
     * Log strings
     */
    private static String EXC_MESS_JSON = "Exception while converting the message to json";
    private static String EXC_JSON_MESS = "Exception while converting the json to message";

    /**
     * Private class used to create json messages
     */
    protected static class Message {
        int connectionID;
        CommunicationMessage message;
        Map<String, String> arguments;

        Message(int id, CommunicationMessage format, Map<String, String> arguments) {
            this.connectionID = id;
            this.message = format;
            this.arguments = arguments;
        }

        Message() {}
        @SuppressWarnings("all")
        public int getConnectionID() { return connectionID; }
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
        var message = new Message(id, format, args);
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
//            throw new RuntimeException(EXC_JSON_MESS);
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

}