package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.view.View;

import java.io.PrintWriter;
import java.util.*;

import static it.polimi.ingsw.networking.utility.CommunicationMessage.*;

public class AdrenalineCLI extends View {
    @SuppressWarnings("squid:S106")
    private PrintWriter out = new PrintWriter(System.out, true);
    private Scanner     in  = new Scanner(System.in);

    /**
     * Console prompt strings
     */
    private static final String CHOOSE_CONNECTION   =
            "Please, choose between a \n\t" +
            "1. SOCKET connection\n\t" +
            "2. RMI connection";
    private static final String INCORRECT_CHOICE    = "Incorrect choice. Retry.";
    private static final String CHOOSE_SOCKET_PORT  = "On which port the SOCKET server is listening? ";
    private static final String CHOOSE_RMI_PORT     = "On which port the RMI server is listening? ";
    private static final String CHOOSE_SERVER_ADDR  = "Insert the server host address: ";
    private static final String CHOOSE_USERNAME     = "Please, insert your username to log in: ";
    private static final String USER_NOT_AVAILABLE  = "The username you provided is not available. Try again, please";
    private static final String CHOOSE_WEAPON       = "Which weapon do you want to use?";
    private static final String CHOOSE_DAMAGE       = "What damage do you want to make?";

    /**
     * Private constructor of the Command Line Interface
     */
    private AdrenalineCLI() {
        this.willChooseConnection();
        this.login();
    }

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
            connectionType = ConnectionType.parse(connection);
        }

        out.println(CHOOSE_SERVER_ADDR);
        var hostAddress = in.nextLine();

        if (connectionType == ConnectionType.SOCKET) out.println(CHOOSE_SOCKET_PORT);
        else out.println(CHOOSE_RMI_PORT);

        var port = in.nextInt();

        this.didChooseConnection(connectionType, port, hostAddress);
    }

    @Override
    protected void login() {
        out.flush();
        out.println();
        out.println(CHOOSE_USERNAME);
        var username = in.next();
        if(connectionType == ConnectionType.SOCKET) {
            var args = new HashMap<String, String>();
            args.put(User.username_key, username);
            this.client.send(CommunicationMessage.from(0, CREATE_USER, args));
        }
        if(connectionType == ConnectionType.RMI) {
           clientRMI.login(username);
        }
    }

    @Override
    public void onLoginFailure() {
        out.println(USER_NOT_AVAILABLE);
        super.onLoginFailure();
    }

    @Override
    public void onViewUpdate() {

    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void didJoinWaitingRoom() {

    }

    @Override
    public void willChooseCharacter() {

    }

    @Override
    public void didChooseCharacter() {

    }

    @Override
    public void willChooseGameSettings() {

    }

    @Override
    public void didChooseGameSettings() {

    }

    @Override
    public void willChooseSpawnPoint() {

    }

    @Override
    public void didChooseSpawnPoint() {

    }

    @Override
    public void onMove() {

    }

    @Override
    public void willChooseMovement() {

    }

    @Override
    public void didChooseMovement() {

    }

    @Override
    public void willChooseWhatToGrab() {

    }

    @Override
    public void didChooseWhatToGrab() {

    }

    @Override
    public void willChooseWeapon(ArrayList<String> weaponsAvailable) {
        out.println(CHOOSE_WEAPON);
        boolean selected = false;
        String weaponSelected = "-1";
        int i = 1;
        for(String weapon : weaponsAvailable) {
            out.println(i + ") " + weapon);
            i++;
        }
        var scanInput = in.nextLine();
        try {
            int choice = Integer.parseInt(scanInput);
            if(choice <= weaponsAvailable.size()) {
                weaponSelected = weaponsAvailable.get(choice - 1);
                selected = true;
            }
        } catch(NumberFormatException exception) {
            for(String s : weaponsAvailable)
                if(scanInput.equalsIgnoreCase(s)) {
                    weaponSelected = s;
                    selected = true;
                }
        }
        if(!selected) throw new IllegalArgumentException(INCORRECT_CHOICE);
        this.didChooseWeapon(weaponSelected);
    }

    @Override
    public void willChooseDamage(Map<String, String> damagesToChoose) {
        String weapon = damagesToChoose.get(Weapon.weapon_key);
        damagesToChoose.remove(Weapon.weapon_key);
        String indexOfEffect = damagesToChoose.get(Effect.effect_key);
        damagesToChoose.remove(Effect.effect_key);
        ArrayList<String> possibleDamages = new ArrayList<>(damagesToChoose.values());
        out.println(CHOOSE_DAMAGE);
        int i = 1;
        for(String damage : possibleDamages) {
            out.println(i + ") " + damage);
            i++;
        }
        var scanInput = in.nextLine();
        try {
            int choice = Integer.parseInt(scanInput);
            if(!(choice <= possibleDamages.size()))
                throw new IllegalArgumentException(INCORRECT_CHOICE);
            i = choice;
        } catch(NumberFormatException exception) {
            throw new IllegalArgumentException(INCORRECT_CHOICE);
        }
        Map<String, String> damageToDo = new HashMap<>();
        damageToDo.put(Damage.damage_key, possibleDamages.get(i-1));
        damageToDo.put(Weapon.weapon_key, weapon);
        damageToDo.put(Effect.effect_key, indexOfEffect);
        this.didChooseDamage(damageToDo);
    }

    @Override
    public void willChooseMode() {

    }

    @Override
    public void didChooseMode() {

    }

    @Override
    public void willChooseEffects() {

    }

    @Override
    public void didChooseEffects() {

    }

    @Override
    public void onEndTurn() {

    }

    @Override
    public void willReload() {

    }

    @Override
    public void didReload() {

    }

    @Override
    public void willUsePowerup() {

    }

    @Override
    public void didUsePowerup() {

    }

    @Override
    public void willChoosePowerup() {

    }

    @Override
    public void didChoosePowerup() {

    }

    @Override
    public void willChoosePowerupEffect() {

    }

    @Override
    public void didChoosePowerupEffect() {

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
