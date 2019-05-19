package it.polimi.ingsw.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.stream.Collectors;

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
    PONG;

    private static String EXC_MESS_JSON = "Exception while converting the message to json";
    private static String EXC_JSON_MESS = "Exception while converting the json to message";


    private static class Message {
        int connectionID;
        CommunicationMessage message;
        String[] arguments;

        public Message(int id, CommunicationMessage format, String[] arguments) {
            this.connectionID = id;
            this.message = format;
            this.arguments = arguments;
        }

        public Message() {}

        public int getConnectionID() { return connectionID; }
        public CommunicationMessage getMessage() { return message; }
        public String[] getArguments() { return arguments; }
    }

    public static String from(int id, CommunicationMessage format) {
        return from(id, format, new String[0]);
    }

    public static String from(int id, CommunicationMessage format, String[] args) {
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

}