package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.view.View;

import java.io.PrintWriter;
import java.util.Scanner;

public class AdrenalineCLI extends View {
    @SuppressWarnings("squid:S106")
    private PrintWriter out = new PrintWriter(System.out, true);
    private Scanner     in  = new Scanner(System.in);

    private static final String CHOOSE_CONNECTION   =
            "Please, choose between a \n\t" +
            "1. SOCKET connection\n\t" +
            "2. RMI connection\n";
    private static final String INCORRECT_CHOICE    = "Incorrect choice. Retry.";
    private static final String CHOOSE_SOCKET_PORT  = "On which port the SOCKET server is listening? ";
    private static final String CHOOSE_RMI_PORT     = "On which port the RMI server is listening? ";
    private static final String CHOOSE_SERVER_ADDR  = "Insert the server host address: ";

    /**
     * Private constructor of the Command Line Interface
     */
    private AdrenalineCLI() {this.willChooseConnection();}

    public static void main(String[] args) {
        new AdrenalineCLI();
    }

    @Override
    protected void willChooseConnection() {
        ConnectionType connectionType;
        out.println(CHOOSE_CONNECTION);
        var connection = in.nextLine();
        try {
            var choice = Integer.parseInt(connection);
            switch (choice) {
                case 1:
                    connectionType = ConnectionType.SOCKET;
                    break;
                case 2:
                    connectionType = ConnectionType.RMI;
                    break;
                default:
                    throw new IllegalArgumentException(INCORRECT_CHOICE);
            }
        } catch (NumberFormatException exc) {
            connectionType = ConnectionType.valueOf(connection);
        }

        out.print(CHOOSE_SERVER_ADDR);
        var hostAddress = in.nextLine();

        if (connectionType == ConnectionType.SOCKET) out.print(CHOOSE_SOCKET_PORT);
        else out.print(CHOOSE_RMI_PORT);

        var port = in.nextInt();

        this.didChooseConnection(connectionType, port, hostAddress);
    }


}
