package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.PotentiableWeapon;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.view.View;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Command Line Interface version of the game.
 *
 * @author Daniele Chiappalupi
 * @author Stefano Formicola
 * @author Elena Iannucci
 */
public class AdrenalineCLI extends View {
    @SuppressWarnings("squid:S106")
    private PrintWriter out = new PrintWriter(System.out, true);
    private Scanner     in  = new Scanner(System.in);

    /**
     * Console prompt strings
     */
    private static final String CHOOSE_CONNECTION           =
            "Please, choose between a \n\t" +
            "1. SOCKET connection\n\t" +
            "2. RMI connection";
    private static final String INCORRECT_CHOICE            = "Incorrect choice. Retry.";
    private static final String CHOOSE_SOCKET_PORT          = "On which port the SOCKET server is listening? ";
    private static final String CHOOSE_RMI_PORT             = "On which port the RMI server is listening? ";
    private static final String CHOOSE_SERVER_ADDR          = "Insert the server host address: ";
    private static final String CHOOSE_USERNAME             = "Please, insert your username to log in: ";
    private static final String USER_NOT_AVAILABLE          = "The username you provided is not available. Try again, please";
    private static final String DID_JOIN_WAITING_R          = "Waiting Room joined successfully. A new game will start as soon as other players will createUser.";
    private static final String ON_START                    = "Game started.";
    private static final String CHOOSE_WEAPON               = "Which weapon do you want to use?";
    private static final String CHOOSE_DAMAGE               = "What damage do you want to make?";
    private static final String CHOOSE_MODALITY             = "Which modality do you want to use?";
    private static final String CHOOSE_EFFECT               = "What effect/s do you want to use?";
    private static final String CHOOSE_WEAPONS_TO_RELOAD    = "What weapons do you want to reload? \nMultiple weapons can be provided with commas.";
    private static final String NOT_ENOUGH_AMMOS            = "You have not enough ammos to reload. Please select only weapons you can afford.";
    private static final String ASK_RELOAD                  = "Do you want to reload your weapons? [Y/N]";
    private static final String WILL_RELOAD                 = "You selected that you want to reload your weapon.";
    private static final String WON_T_RELOAD                = "Your weapons won't be reloaded.";
    private static final String RELOAD_SUCCESS              = "Reload succeeded! Your weapons has been reloaded.";
    private static final String CHOOSE_POWERUP              = "Which powerup do you want to use?";

    /**
     * Log strings or exceptions
     */
    private static final String INCORRECT_HOSTNAME          = "Incorrect cli argument: hostname";
    private static final String INCORRECT_PORT              = "Incorrect cli argument: server port";
    private static final String INCORRECT_CONN_TYPE         = "Incorrect cli argument: connection type";
    private static final String MISSING_ARGUMENTS           = "Missing CLI arguments. Asking for user insertion.";


    /**
     * Private constructor of the Command Line Interface
     */
    private AdrenalineCLI() {
        this.willChooseConnection();
        this.createUser();
    }

    private AdrenalineCLI(String connectionType, String hostname, String port) {
        try {
            var connection = ConnectionType.parse(connectionType);
            var serverPort  = Integer.parseInt(port);
            Inet4Address.getByName(hostname);
            this.didChooseConnection(connection, serverPort, hostname);
        } catch (NumberFormatException e) {
            AdrenalineLogger.errorException(INCORRECT_PORT, e);
            this.willChooseConnection();
        } catch (IllegalArgumentException e) {
            AdrenalineLogger.errorException(INCORRECT_CONN_TYPE, e);
            this.willChooseConnection();
        } catch (UnknownHostException e) {
            AdrenalineLogger.errorException(INCORRECT_HOSTNAME, e);
            this.willChooseConnection();
        }
        this.createUser();
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            AdrenalineLogger.warning(MISSING_ARGUMENTS);
            new AdrenalineCLI();
        } else {
            var connectionType = args[0];
            var serverName     = args[1];
            var serverPort     = args[2];
            new AdrenalineCLI(connectionType, serverName, serverPort);
        }
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
    protected void createUser() {
        out.flush();
        out.println();
        out.println(CHOOSE_USERNAME);
        var username = in.nextLine();

        this.client.createUser(username);
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
    public void onStart(UUID gameID) {
        super.onStart(gameID);
        out.println(ON_START);
    }

    @Override
    public void didJoinWaitingRoom() {
        out.println(DID_JOIN_WAITING_R);
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
            if(choice > possibleDamages.size())
                throw new IllegalArgumentException(INCORRECT_CHOICE);
            i = choice;
        } catch(NumberFormatException exception) {
            throw new IllegalArgumentException(INCORRECT_CHOICE);
        }
        Map<String, String> damageToDo = new HashMap<>();
        String forPotentiableWeapon = null;
        if(damagesToChoose.containsKey(PotentiableWeapon.forPotentiableWeapon_key))
            forPotentiableWeapon = damagesToChoose.get(PotentiableWeapon.forPotentiableWeapon_key);
        damageToDo.put(Damage.damage_key, possibleDamages.get(i-1));
        damageToDo.put(Weapon.weapon_key, weapon);
        damageToDo.put(Effect.effect_key, indexOfEffect);
        this.didChooseDamage(weapon, possibleDamages.get(i-1), indexOfEffect, forPotentiableWeapon);
    }

    @Override
    public void willChooseMode(Map<String, String> modalitiesToChoose) {
        String weapon = modalitiesToChoose.get(Weapon.weapon_key);
        modalitiesToChoose.remove(Weapon.weapon_key);
        out.println(CHOOSE_MODALITY);
        String modalitySelected = decisionHandlerFromMap(modalitiesToChoose);
        this.didChooseMode(weapon, modalitySelected);
    }

    /**
     * Private method that handles the decision process when the input parameter is a Map of choices, added to avoid code repetitions.
     * @param args it's the map containing the choices.
     * @return the choice.
     * @throws IllegalArgumentException if an incorrect choice is made.
     */
    private String decisionHandlerFromMap(Map<String, String> args) {
        ArrayList<String> options = new ArrayList<>(args.values());
        String optionSelected = decisionHandlerFromList(options);
        if(optionSelected == null) throw new IllegalArgumentException(INCORRECT_CHOICE);
        return optionSelected;
    }

    /**
     * Private method that handles the decision process when the input parameter is a List of choices, added to avoid code repetitions.
     * @param options it's the list containing the choices.
     * @return the choice, null if the selection was illegal.
     */
    private String decisionHandlerFromList(List<String> options) {
        for(int i = 1; i <= options.size(); i++)
            out.println(i + ") " + options.get(i-1));
        var scanInput = in.nextLine();
        return selectionChecker(scanInput, options);
    }

    /**
     * Private method that checks if the selection from the input was legal. Added to avoid code repetitions.
     * @param scan it's the string scanned from te input, to be checked.
     * @param options it's the list containing all of the possible options.
     * @return the option selected, null if the selection was illegal.
     */
    private String selectionChecker(String scan, List<String> options) {
        String optionSelected = null;
        try {
            int choice = Integer.parseInt(scan);
            if(choice <= options.size())
                optionSelected = options.get(choice);
        } catch (NumberFormatException exception) {
            for(String s : options)
                if(scan.equalsIgnoreCase(s))
                    optionSelected = s;
        }
        return optionSelected;
    }

    @Override
    public void willChooseEffects(Map<String, String> effectsToChoose) {
        String weapon = effectsToChoose.get(Weapon.weapon_key);
        effectsToChoose.remove(Weapon.weapon_key);
        out.println(CHOOSE_EFFECT);
        String effectsSelected = decisionHandlerFromMap(effectsToChoose);
        String box = effectsSelected.replaceAll("\\[|\\]", "");
        List<String> effects = List.of(box.split(", "));
        this.didChooseEffects(effects, weapon);
    }

    @Override
    public void onEndTurn() {

    }

    @Override
    public void askReload() {
        out.println(ASK_RELOAD);
        String scanInput = in.nextLine();
        if(scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y")) {
            out.println(WILL_RELOAD);
            this.client.askWeaponToReload();
        }
        out.println(WON_T_RELOAD);
    }

    @Override
    public void willReload(List<String> weapons) {
        out.println(CHOOSE_WEAPONS_TO_RELOAD);
        String scanInput = in.nextLine();
        scanInput.replaceAll(" ", "");
        List<String> selections = List.of(scanInput.split(","));
        Set<String> choices = new HashSet<>();
        for(String selection : selections) {
            String box = selectionChecker(selection, weapons);
            if(box == null) throw new IllegalArgumentException(INCORRECT_CHOICE);
            choices.add(box);
        }
        List<String> weaponsToReload = new ArrayList<>(choices);
        this.didChooseWeaponsToReload(weaponsToReload);
    }

    @Override
    public void onReloadSuccess() {
        out.println(RELOAD_SUCCESS);
    }

    @Override
    public void onReloadFailure() {
        out.println(NOT_ENOUGH_AMMOS);
        this.askReload();
    }

    @Override
    public void didUsePowerup() {

    }

    //toDo: do you want to use another powerup?
    @Override
    public void willChoosePowerup(List<String> availablePowerups) {
        if(availablePowerups.isEmpty()) {
            out.println("You haven't any powerup that you can use right now.");
            return;
        }
        out.println(CHOOSE_POWERUP);
        String choice = decisionHandlerFromList(availablePowerups);
        while(choice == null) {
            out.println(INCORRECT_CHOICE);
            out.println(CHOOSE_POWERUP);
            choice = decisionHandlerFromList(availablePowerups);
        }
        this.didChoosePowerup(choice);
    }

    @Override
    public void willChoosePowerupDamage(List<String> possibleDamages) {

    }

    @Override
    public void didChoosePowerupEffect() {

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
