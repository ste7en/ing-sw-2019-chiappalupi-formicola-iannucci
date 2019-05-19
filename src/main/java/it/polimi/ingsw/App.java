package it.polimi.ingsw;

import it.polimi.ingsw.networking.Client;
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
        AdrenalineLogger.setLogName("Client");
        var client = new Client(ConnectionType.SOCKET, "localhost", 3334);
    }
}
