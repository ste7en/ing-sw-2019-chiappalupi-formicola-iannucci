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
     * Ping and Pong messages used to
     * check client connectivity
     */
    PING,
    PONG,
    /**
     * User login messages
     */
    CREATE_USER,
    CREATE_USER_OK,
    CREATE_USER_FAILED,
    USER_LOGIN;

    private static String EXC_MESS_JSON = "Exception while converting the message to json";
    private static String EXC_JSON_MESS = "Exception while converting the json to message";


    private static class Message {
        int connectionID;
        CommunicationMessage message;
        Map<String, String> arguments;

        Message(int id, CommunicationMessage format, Map<String, String> arguments) {
            this.connectionID = id;
            this.message = format;
            this.arguments = arguments;
        }

        Message() {}

        int getConnectionID() { return connectionID; }
        public CommunicationMessage getMessage() { return message; }
        Map<String, String> getArguments() { return arguments; }
    }

    public static String from(int id, CommunicationMessage format) {
        return from(id, format, new HashMap<>());
    }

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

    public static CommunicationMessage getCommunicationMessageFrom(String json) {
        var message = getMessageFrom(json);
        return message.getMessage();
    }

    public static int getConnectionIDFrom(String json) {
        return getMessageFrom(json).getConnectionID();
    }

    public static Map<String, String> getMessageArgsFrom(String json) {
        return getMessageFrom(json).getArguments();
    }

}