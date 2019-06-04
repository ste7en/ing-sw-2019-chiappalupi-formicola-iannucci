package it.polimi.ingsw;

import it.polimi.ingsw.networking.socket.ClientSocket;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        AdrenalineLogger.LOG_TYPE = "ClientSocket";
        var client = new ClientSocket(ConnectionType.SOCKET, "localhost", 3334);
    }
}
