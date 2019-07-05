package it.polimi.ingsw;

import it.polimi.ingsw.networking.socket.ClientSocket;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        var client = new ClientSocket("localhost", 3334);
    }
}
