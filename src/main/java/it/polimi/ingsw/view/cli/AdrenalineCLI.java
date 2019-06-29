package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
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
    private static final String CHOOSE_CONNECTION           = "Please, choose between a \n\t" +
                                                              "1. SOCKET connection\n\t" +
                                                              "2. RMI connection";
    private static final String INCORRECT_CHOICE            = "Incorrect choice. Retry.";
    private static final String CHOOSE_SOCKET_PORT          = "On which port the SOCKET server is listening? ";
    private static final String CHOOSE_RMI_PORT             = "On which port the RMI server is listening? ";
    private static final String CHOOSE_SERVER_ADDR          = "Insert the server host address: ";
    private static final String CHOOSE_USERNAME             = "Please, insert your username to log in: ";
    private static final String USER_NOT_AVAILABLE          = "The username you provided is not available. Try again, please";
    private static final String ON_START                    = "Game started.";
    private static final String CHOOSE_CHARACTER            = "Choose a character who will represent you in the game. Insert the character number: ";
    private static final String CHOOSE_CHARACTER_NOT_OK     = "";
    private static final String SHOOT_PEOPLE_FAILURE        = "You have no weapon in your hand, so you can't shoot anyone.";
    private static final String DAMAGE_FAILURE              = "No damage can be made with the weapon and the effects selected.";
    private static final String CHOOSE_POWERUP_TO_SELL      = "What powerup do you want to sell?";
    private static final String POWERUP_FAILURE             = "You haven't got any powerup!";
    private static final String CHOOSE_WEAPON               = "Which weapon do you want to use?";
    private static final String CHOOSE_DAMAGE               = "What damage do you want to make?";
    private static final String CHOOSE_MODALITY             = "Which modality do you want to use?";
    private static final String CHOOSE_EFFECT               = "What effect/s do you want to use?";
    private static final String WEAPON_USED                 = "The weapon has been used with success.";
    private static final String ASK_POWERUP_AFTER_SHOT      = "Do you want to use any powerup to make additional damage after this shot?";
    private static final String WILL_POWERUP_AFTER_SHOT     = "You selected that you want to use any powerup to make additional damage after this shot\n" + "What powerup would you like to use?";
    private static final String WON_T_POWERUP_AFTER_SHOT    = "No powerups will be used after this shot.";
    private static final String CHOOSE_WEAPONS_TO_RELOAD    = "What weapons do you want to reload? \nMultiple weapons can be provided with commas.";
    private static final String NOT_ENOUGH_AMMOS            = "You have not enough ammos to reload. Please select only weapons you can afford.";
    private static final String ASK_RELOAD                  = "Do you want to reload your weapons? [Y/N]";
    private static final String NO_WEAPON_UNLOADED          = "You have no weapon unloaded in your hand, so you can't reload anything.";
    private static final String WILL_RELOAD                 = "You selected that you want to reload your weapon.";
    private static final String WON_T_RELOAD                = "Your weapons won't be reloaded.";
    private static final String RELOAD_SUCCESS              = "Reload succeeded! Your weapons has been reloaded.";
    private static final String TURN_POWERUP_FAILURE        = "You haven't got any powerup that you can use right now!";
    private static final String CHOOSE_POWERUP              = "Which powerup do you want to use?";
    private static final String CHOOSE_POWERUP_DAMAGE       = "What do you want to do with your powerup?";
    private static final String POWERUP_USED                = "The powerup has been used with success.";
    private static final String USE_ANOTHER_POWERUP         = "Do you want to use another powerup? [Y/N]";
    private static final String WILL_USE_ANOTHER_POWERUP    = "You selected that you want to use another powerup.";
    private static final String WON_T_USE_ANOTHER_POWERUP   = "Powerup using phase finished.";
    private static final String ASK_POWERUP_TO_USE_EFFECTS  = "Do you want use any powerup to afford the cost of the shoot? [Y/N]\n" +
                                                              "Please note that if you select [Y], you will only have the possibility " +
                                                              "to use a list of effects or modes of the weapon where the color/s of the powerup/s that you select is/are involved.";
    private static final String ASK_POWERUP_TO_RELOAD       = "Do you want use any powerup to afford the cost of the the reload? [Y/N]\n" +
                                                              "Please note that if you select [Y], you will only have the possibility " +
                                                              "to reload a list of weapon where the color/s of the powerup/s that you select is/are involved.";
    private static final String MORE_POWERUP_SELLING        = "Do you want use another powerup to afford the cost of the shoot?";

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
        this.willCreateUser();
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
        this.willCreateUser();
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
    protected void willCreateUser() {
        out.flush();
        out.println();
        out.println(CHOOSE_USERNAME);
        var username = in.nextLine();
        createUser(username);
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
    public void onFailure(String message) {
        switch (message) {
            case CHARACTER_NOT_AVAILABLE:
                out.println(message);
                break;
            default:
                out.println(message);
        }
    }

    @Override
    public void onStart() {
        out.println(ON_START);
        super.onStart();
    }

    @Override
    public void didJoinWaitingRoom() {
        out.println(DID_JOIN_WAITING_ROOM);
    }

    @Override
    public void willChooseCharacter(List<String> availableCharacters) {
        Integer choice;
        do { out.println(CHOOSE_CHARACTER);
            var li = availableCharacters.listIterator();
            while (li.hasNext()) out.println(li.nextIndex()+") "+li.next());
            //flushInput();
            choice = in.nextInt();
        } while (choice > availableCharacters.size() || choice < 0);
        didChooseCharacter(availableCharacters.get(choice));
    }

    private void flushInput() {
        if (in.hasNext()) in.nextLine();
    }

    @Override
    public void willChooseGameMap() {

    }

    @Override
    public void onChooseSpawnPoint(ArrayList<String> powerups) {

    }

    @Override
    public void onChooseAction() {

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
    public void onShootPeopleFailure() {
        out.println(SHOOT_PEOPLE_FAILURE);
        this.onChooseAction();
    }

    @Override
    public void willChooseWeapon(List<String> weaponsAvailable) {
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
    public void onDamageFailure() {
        out.println(DAMAGE_FAILURE);
        this.onChooseAction();
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
        String forPotentiableWeapon = null;
        if(damagesToChoose.containsKey(PotentiableWeapon.forPotentiableWeapon_key))
            forPotentiableWeapon = damagesToChoose.get(PotentiableWeapon.forPotentiableWeapon_key);
        this.didChooseDamage(weapon, possibleDamages.get(i-1), indexOfEffect, forPotentiableWeapon);
        out.println(WEAPON_USED);
    }

    @Override
    public void onPowerupInHandFailure() {
        out.println(POWERUP_FAILURE);
        this.onChooseAction();
    }

    @Override
    public void willChoosePowerupSelling(Map<String, String> powerups) {
        out.println(ASK_POWERUP_TO_USE_EFFECTS);
        String weapon = powerups.get(Weapon.weapon_key);
        String commMessage = powerups.get(CommunicationMessage.communication_message_key);
        powerups.remove(weapon);
        powerups.remove(commMessage);
        List<String> availablePowerups = new ArrayList<>(powerups.values());
        List<String> choices = new ArrayList<>();
        String scanInput = in.nextLine();
        if(scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y"))
            choices = powerupSellingHelper(availablePowerups);
        this.didChoosePowerupSelling(weapon, choices);
    }

    /**
     * Helper method to ask the player if and how does he want to use powerups to afford the cost of something.
     * @param availablePowerups it's the list of available powerups.
     * @return the list of choices made by the player.
     */
    @SuppressWarnings({"squid:S3776", "squid:S1168"})
    private List<String> powerupSellingHelper(List<String> availablePowerups) {
        boolean cycleCounter = true;
        List<String> choices = new ArrayList<>();
        while(cycleCounter) {
            if(availablePowerups.isEmpty()) {
                this.onPowerupInHandFailure();
                return null;
            }
            out.println(CHOOSE_POWERUP_TO_SELL);
            String choice = decisionHandlerFromList(availablePowerups);
            while(choice == null) {
                out.println(INCORRECT_CHOICE);
                choice = decisionHandlerFromList(availablePowerups);
            }
            choices.add(choice);
            String toRemove = null;
            for(String powerup : availablePowerups)
                if(choice.equals(powerup))
                    toRemove = powerup;
            availablePowerups.remove(toRemove);
            if(availablePowerups.isEmpty()) cycleCounter = false;
            else {
                out.println(MORE_POWERUP_SELLING);
                String scanInput = in.nextLine();
                cycleCounter = scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y");
            }
        }
        return choices;
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
        String box = effectsSelected.replaceAll("[\\[\\]]", "");
        List<String> effects = List.of(box.split(", "));
        this.didChooseEffects(effects, weapon);
    }

    @Override
    public void askPowerupAfterShot(List<String> powerups) {
        out.println(ASK_POWERUP_AFTER_SHOT);
        String scanInput = in.nextLine();
        if(scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y")) {
            out.println(WILL_POWERUP_AFTER_SHOT);
            this.willChoosePowerup(powerups);
        }
        else out.println(WON_T_POWERUP_AFTER_SHOT);
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
            this.askWastePowerupToReload();
        }
        else out.println(WON_T_RELOAD);
    }

    @Override
    public void willSellPowerupToReload(List<String> powerups) {
        List<String> choices = powerupSellingHelper(powerups);
        this.client.sellPowerupToReload(choices);
    }

    /**
     * Private method called when the player has to decide whether he wants to use any powerup bonus ammo to reload his weapons or not.
     */
    private void askWastePowerupToReload() {
        out.println(ASK_POWERUP_TO_RELOAD);
        String scanInput = in.nextLine();
        if(scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y"))
            this.client.askForPowerupsToReload();
        else this.client.askWeaponToReload();
    }

    @Override
    public void onWeaponUnloadedFailure() {
        out.println(NO_WEAPON_UNLOADED);
        //toDo is onEndTurn right?
        this.onEndTurn();
    }

    @Override
    public void willReload(List<String> weapons) {
        out.println(CHOOSE_WEAPONS_TO_RELOAD);
        String scanInput = in.nextLine();
        scanInput = scanInput.replaceAll(" ", "");
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
    public void onTurnPowerupFailure() {
        out.println(TURN_POWERUP_FAILURE);
    }

    @Override
    public void willChoosePowerup(List<String> availablePowerups) {
        if(availablePowerups.isEmpty()) {
            out.println("You haven't any powerup that you can use right now.");
            return;
        }
        boolean powerupUsing = true;
        while(powerupUsing) {
            out.println(CHOOSE_POWERUP);
            String choice = decisionHandlerFromList(availablePowerups);
            while(choice == null) {
                out.println(INCORRECT_CHOICE);
                out.println(CHOOSE_POWERUP);
                choice = decisionHandlerFromList(availablePowerups);
            }
            this.didChoosePowerup(choice);
            availablePowerups.remove(choice);
            if(availablePowerups.isEmpty()) powerupUsing = false;
            else {
                out.println(USE_ANOTHER_POWERUP);
                String scanInput = in.nextLine();
                if(scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y"))
                    out.println(WILL_USE_ANOTHER_POWERUP);
                else {
                    out.println(WON_T_USE_ANOTHER_POWERUP);
                    powerupUsing = false;
                }
            }
        }
    }

    @Override
    public void willChoosePowerupDamage(Map<String, String> possibleDamages) {
        out.println(CHOOSE_POWERUP_DAMAGE);
        String powerup = possibleDamages.get(Powerup.powerup_key);
        possibleDamages.remove(Powerup.powerup_key);
        String choice = decisionHandlerFromMap(possibleDamages);
        this.didChoosePowerupDamage(choice, powerup);
        out.println(POWERUP_USED);
    }

}
