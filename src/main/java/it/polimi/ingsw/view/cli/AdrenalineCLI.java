package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.utility.MapType;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.view.View;

import java.io.*;
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
    private static final String NO_COMBO_FOUND = "No combinations of effects/costs found for your request.";
    private static final String FINAL_FRENZY = "Final frenzy has begun! Kill as much people as you can, because everyone will get only one turn more.";
    private static final String GAME_ENDED = "The game has ended!";
    private static final String SHOOTING_POSITION = "Choose the position where you want to shoot from.";
    private static final String SHOT = "You have been shot from ";
    private static final String MARK_HIM = "! Do you want to use a tagback grenade to mark him?";
    @SuppressWarnings("squid:S106")
    private PrintWriter out = new PrintWriter(System.out, true);
    private Scanner     in  = new Scanner(System.in);
    private String curSituation;
    private boolean locked;
    private boolean lockedOnPowerup;

    /**
     * Console prompt strings
     */
    private static final String CHOOSE_CONNECTION           = "Please, choose between a \n\t" +
                                                              "1. SOCKET connection\n\t" +
                                                              "2. RMI connection";
    private static final String INCORRECT_CHOICE            = "Incorrect choice. Retry.\n";
    private static final String CHOOSE_SOCKET_PORT          = "On which port the SOCKET server is listening? ";
    private static final String CHOOSE_RMI_PORT             = "On which port the RMI server is listening? ";
    private static final String CHOOSE_SERVER_ADDR          = "Insert the server host address: ";
    private static final String CHOOSE_USERNAME             = "Please, insert your username to log in: ";
    private static final String USER_NOT_AVAILABLE          = "The username you provided is not available. Try again, please";
    private static final String ON_START                    = "Game started \uD83C\uDFAE\n";
    private static final String CHOOSE_CHARACTER            = "Choose a character who will represent you in the game. Insert the character number: ";
    private static final String CHOOSE_CHARACTER_NOT_OK     = "";
    private static final String HAVE_FUN_PT1                = "You have successfully chosen the character: ";
    private static final String HAVE_FUN_PT2                = ". Please wait while all the other players will choose their.\n" +
                                                              "After it, the first player will choose the map and the number of skulls.\n" +
                                                              "As soon as the game begins, you will be notified. Have fun!";
    private static final String CHOOSE_SPAWN_POINT          = "Here are two powerup cards. Choose the one you want to discard: its color will be the color where you will spawn.\n" +
                                                              "You will keep the powerup that you don't discard.";
    private static final String GAME_SITUATION              = "Here is the situation of the game:\n";
    private static final String CHOOSE_SKULLS_NUMBER        = "Choose the number of skulls of your game \uD83D\uDC80\n";
    private static final String SKULLS_CHOSEN               = "You have chosen this number of \uD83D\uDC80: ";
    private static final String RUN_AROUND                  = "Run around";
    private static final String GRAB                        = "Grab something";
    private static final String SHOOT_PEOPLE                = "Shoot people";
    private static final String NO_POWERUPS_IN_HAND         = "You haven't any powerup that you can use right now.";
    private static final String CHOOSE_ACTION               = "Choose your next move between the following:\n";
    private static final String MORE_GRAB_POWERUP_SELLING   = "Do you want use another powerup to afford the cost of the weapon? [Y/N]";
    private static final String PICK_CHOICES                = "Here is what you can grab:";
    private static final String GRAB_SUCCESS                = "Grabbing phase ended with success!";
    private static final String TOO_MUCH_POWERUP            = "You can't grab this powerup because you already have three of them in your hand. Choose which one would you like to discard.";
    private static final String TOO_MUCH_WEAPON             = "You can't grab this weapon because you already have three of them in your hand. Choose which one would you like to discard.";
    private static final String POWERUP_TO_SELL_TO_GRAB     = "Which powerup do you want to sell to afford the cost of the weapon?";
    private static final String GRAB_FAILURE                = "No weapons can be bought using the powerups you wanted to sell.";
    private static final String CHOOSE_MOVEMENT             = "These are the available movements you can do. Please, choose one by selecting its number: ";
    private static final String SHOOT_PEOPLE_FAILURE        = "You have no weapon in your hand, so you can't shoot anyone.";
    private static final String DAMAGE_FAILURE              = "No damage can be done with the weapon and the effects selected.";
    private static final String CHOOSE_POWERUP_TO_SELL      = "What powerup do you want to sell?";
    private static final String POWERUP_FAILURE             = "You haven't got any powerup!";
    private static final String CHOOSE_MAP                  = "Which map do you want to play on?";
    private static final String CHOOSE_WEAPON               = "Which weapon do you want to use?";
    private static final String CHOOSE_DAMAGE               = "What damage do you want to make?";
    private static final String CHOOSE_MODALITY             = "Which modality do you want to use?";
    private static final String CHOOSE_EFFECT               = "What effect/s do you want to use?";
    private static final String WEAPON_USED                 = "The weapon has been used with success.";
    private static final String ASK_POWERUP_AFTER_SHOT      = "Do you want to use any powerup to make additional damage after this shot? [Y/N]";
    private static final String WILL_POWERUP_AFTER_SHOT     = "You selected that you want to use any powerup to make additional damage after this shot.\n";
    private static final String WON_T_POWERUP_AFTER_SHOT    = "No powerups will be used after this shot.";
    private static final String CHOOSE_WEAPONS_TO_RELOAD    = "What weapon do you want to reload? You will have the possibility to choose as much weapons as you wish.";
    private static final String MORE_WEAPONS_TO_RELOAD      = "What other weapons do you want to reload?";
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
                                                              ONE_SHOT +
                                                              "to use a list of effects or modes of the weapon where the color/s of the powerup/s that you select is/are involved.";
    private static final String ASK_POWERUP_TO_RELOAD       = "Do you want use any powerup to afford the cost of the the reload? [Y/N]\n" +
                                                              ONE_SHOT +
                                                              "to reload a list of weapon where the color/s of the powerup/s that you select is/are involved.";
    private static final String MORE_POWERUP_SELLING        = "Do you want use another powerup to afford the cost of the shoot? [Y/N]";
    private static final String TURN_ENDED                  = "Your turn has now come to an end. Wait for the next one...";
    private static final String USE_ANY_OF_YOUR_POWERUP     = "Would you like to use any of your powerup? [Y/N]";
    private static final String TURN_ENDING                 = "Your turn is about to end. You will be now given of the possibilities to reload your unloaded weapons and use any powerup that can be used during this phase.";
    private static final String MOVEMENT_MADE               = "A movement has been made! Check out the new situation on the board:";
    private static final String WILL_PLAY_SOON              = "Keep waiting: soon it will be your turn...";
    private static final String SPAWN_AFTER_DEATH_DECISION  = "You are dead. Choose where you want to respawn! Remember that the powerup you choose will be discarded.";
    private static final String RELOAD_MORE                 = "Do you want to choose another weapon to reload? [Y/N]";

    /**
     * Game parameters
     */
    private static final int MINIMUM_NUMBER_OF_SKULLS       = 5;
    private static final int MAXIMUM_NUMBER_OF_SKULLS       = 8;

    /**
     * Log strings or exceptions
     */
    private static final String INCORRECT_HOSTNAME          = "Incorrect cli argument: hostname";
    private static final String INCORRECT_PORT              = "Incorrect cli argument: server port";
    private static final String INCORRECT_CONN_TYPE         = "Incorrect cli argument: connection type";
    private static final String MISSING_ARGUMENTS           = "Missing CLI arguments. Asking for user insertion.";

    @Override
    public void timeoutHasExpired() {
        out.println(TIMEOUT_EXPIRED);
    }

    /**
     * Private constructor of the Command Line Interface
     */
    private AdrenalineCLI() {
        this.willChooseConnection();
        this.willCreateUser();
    }

    @SuppressWarnings("all")
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

    /**
     * Main method
     * @param args CLI arguments: <--socket|--rmi> <serverAddress> <serverPort> [--debug]
     */
    public static void main(String[] args) {
        var arguments = Arrays.asList(args);
        if (arguments.contains("--debug")) AdrenalineLogger.setDebugMode(true);

        AdrenalineLogger.setLogName("CLI");
        if (arguments.size() < 3) {
            AdrenalineLogger.warning(MISSING_ARGUMENTS);
            new AdrenalineCLI();
        } else {
            var connectionType = arguments.get(0).substring(2);
            var serverName     = arguments.get(1);
            var serverPort     = arguments.get(2);
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

        int port = -1;

        while(port == -1) {
            var scan = in.nextLine();
            try {
                port = Integer.parseInt(scan);
            } catch (NumberFormatException exception) {
                port = -1;
                out.println(INCORRECT_CHOICE);
            }
        }

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
    public void update(Map<String, List<String>> update) {
    }

    @Override
    public void onFailure(String message) {
        out.println(message);
    }

    @Override
    public void onStart() {
        out.println(ON_START);
        AdrenalineLogger.info(ON_START);
        super.onStart();
    }

    @Override
    public void didJoinWaitingRoom() {
        out.println(DID_JOIN_WAITING_ROOM);
    }

    @Override
    public void willChooseCharacter(List<String> availableCharacters) {
        var choice = "";
        do {
            out.println(CHOOSE_CHARACTER);
            choice = decisionHandlerFromList(availableCharacters);
            if (choice == null) out.println(INCORRECT_CHOICE);
        } while (choice == null || choice.equals(""));
        didChooseCharacter(choice);
    }

    @Override
    public void onChooseCharacterSuccess(String characterChosen) {
        out.println(HAVE_FUN_PT1 + characterChosen + HAVE_FUN_PT2);
    }

    @Override
    public void willChooseSkulls() {
        out.println(CHOOSE_SKULLS_NUMBER);
        List<String> options = new ArrayList<>();
        for(int i = MINIMUM_NUMBER_OF_SKULLS; i <= MAXIMUM_NUMBER_OF_SKULLS; i++)
            options.add(Integer.toString(i));
        String choice = decisionHandlerFromList(options);
        while(choice == null) {
            out.println(INCORRECT_CHOICE);
            choice = decisionHandlerFromList(options);
        }
        out.println(SKULLS_CHOSEN + choice);
        this.didChooseSkulls(choice);
    }

    @Override
    public void willChooseGameMap() {
        List<String> options = new ArrayList<>();
        GameMap[] maps = new GameMap[4];
        for(int i = 0; i < 4; i++) {
            maps[i] = new GameMap(MapType.values()[i]);
            options.add(Integer.toString(i));
        }

        out.println(CHOOSE_MAP);

        for(int i = 0; i < 4; i++) {
            out.println(i + ")\n");
            out.println(maps[i].toString());
        }

        String choice = null;
        while(choice == null) {
            var scanInput = in.nextLine();
            choice = selectionChecker(scanInput, options);
            if(choice == null) out.println(INCORRECT_CHOICE);
        }

        MapType chosenMapType = MapType.values()[Integer.parseInt(choice)];

        this.didChooseGameMap(chosenMapType.toString());
    }

    @Override
    public void onChooseSpawnPoint(List<String> powerups) {
        out.println(CHOOSE_SPAWN_POINT);
        String spawnPoint = decisionHandlerFromList(powerups);
        while(spawnPoint == null) {
            out.println(INCORRECT_CHOICE);
            spawnPoint = decisionHandlerFromList(powerups);
        }
        String otherPowerup;
        if(powerups.get(0).equalsIgnoreCase(spawnPoint)) otherPowerup = powerups.get(1);
        else otherPowerup = powerups.get(0);
        this.didChooseSpawnPoint(spawnPoint, otherPowerup);
    }

    @Override
    public void newAction() {
        this.client.newAction();
    }

    @Override
    public void onChooseAction(String map) {
        curSituation = map;
        out.println(map);
        out.println(CHOOSE_ACTION);
        List<String> actions = new ArrayList<>();
        actions.add(RUN_AROUND);
        actions.add(GRAB);
        actions.add(SHOOT_PEOPLE);
        String choice = decisionHandlerFromList(actions);
        while(choice == null) {
            out.println(INCORRECT_CHOICE);
            choice = decisionHandlerFromList(actions);
        }
        switch (choice){
            case RUN_AROUND:
                client.getAvailableMoves();
                break;
            case GRAB:
                grabSomething();
                break;
            case SHOOT_PEOPLE:
                shootPeople();
                break;
            default:
                throw new IllegalArgumentException(INCORRECT_CHOICE);
        }

    }

    @Override
    public void willChooseMovement(List<String> moves) {
        out.println(CHOOSE_MOVEMENT);
        String chosenMovement;
        do {
            chosenMovement = decisionHandlerFromList(moves);
            if (chosenMovement == null) out.println(INCORRECT_CHOICE);
        } while (chosenMovement == null);
        didChooseMovement(chosenMovement);
    }

    @Override
    public void grabSomething() {
        out.println(GRAB_SOMETHING);
        out.println(ASK_POWERUP_TO_GRAB_WEAPON);
        String scanInput = in.nextLine();
        if(scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y"))
            this.client.powerupSellingToGrabWeapon();
        else {
            this.client.askPicks(new ArrayList<>());
        }
    }

    @Override
    public void sellPowerupToGrabWeapon(List<String> powerups) {
        out.println(POWERUP_TO_SELL_TO_GRAB);
        List<String> powerupsSold = new ArrayList<>();
        boolean keepGoing = true;
        while(keepGoing && !powerups.isEmpty()) {
            String choice = decisionHandlerFromList(powerups);
            while(choice == null) {
                out.println(INCORRECT_CHOICE);
                choice = decisionHandlerFromList(powerups);
            }
            powerupsSold.add(choice);
            powerups.remove(choice);
            if(!powerups.isEmpty()) {
                out.println(MORE_GRAB_POWERUP_SELLING);
                String scanInput = in.nextLine();
                keepGoing = scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y");
            }
        }
        this.client.askPicks(powerupsSold);
    }

    @Override
    public void willChooseWhatToGrab(List<String> possiblePicks) {
        out.println(PICK_CHOICES + "\n");
        String choice = decisionHandlerFromList(possiblePicks);
        while(choice == null) {
            out.println(INCORRECT_CHOICE);
            choice = decisionHandlerFromList(possiblePicks);
        }
        this.didChooseWhatToGrab(choice);
    }

    @Override
    public void onGrabSuccess() {
        out.println(GRAB_SUCCESS);
        this.afterAction();
    }

    @Override
    public void onGrabFailure() {
        out.println(GRAB_FAILURE);
        this.onChooseAction(curSituation);
    }

    @Override
    public void onGrabFailurePowerup(List<String> powerup) {
        out.println(TOO_MUCH_POWERUP);
        String choice = decisionHandlerFromList(powerup);
        while(choice == null) {
            out.println(INCORRECT_CHOICE);
            choice = decisionHandlerFromList(powerup);
        }
        onGrabFailurePowerupToDiscard(choice);
    }

    @Override
    public void onGrabFailureWeapon(List<String> weapon) {
        out.println(TOO_MUCH_WEAPON);
        String choice = decisionHandlerFromList(weapon);
        while(choice == null) {
            out.println(INCORRECT_CHOICE);
            choice = decisionHandlerFromList(weapon);
        }
        onGrabFailureWeaponToDiscard(choice);
    }

    @Override
    public void onShootPeopleFailure() {
        out.println(SHOOT_PEOPLE_FAILURE);
        this.onChooseAction(curSituation);
    }

    @Override
    public void willChooseWeapon(List<String> weaponsAvailable) {
        out.println(CHOOSE_WEAPON);
        String weaponSelected = decisionHandlerFromList(weaponsAvailable);
        while(weaponSelected == null) {
            out.println(INCORRECT_CHOICE);
            weaponSelected = decisionHandlerFromList(weaponsAvailable);
        }
        this.didChooseWeapon(weaponSelected);
    }

    @Override
    public void onDamageFailure() {
        out.println(DAMAGE_FAILURE);
        this.onChooseAction(curSituation);
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void willChooseDamage(Map<String, String> damagesToChoose) {
        String forPotentiableWeapon = null;
        if(damagesToChoose.containsKey(PotentiableWeapon.forPotentiableWeapon_key)) {
            forPotentiableWeapon = damagesToChoose.get(PotentiableWeapon.forPotentiableWeapon_key);
            damagesToChoose.remove(PotentiableWeapon.forPotentiableWeapon_key);
        }
        String weapon = damagesToChoose.get(Weapon.weapon_key);
        damagesToChoose.remove(Weapon.weapon_key);
        String indexOfEffect = damagesToChoose.get(Effect.effect_key);
        damagesToChoose.remove(Effect.effect_key);
        List<String> possibleDamages = new ArrayList<>(damagesToChoose.values());
        out.println(CHOOSE_DAMAGE);
        String choice = decisionHandlerFromList(possibleDamages);
        while(choice == null) {
            out.println(INCORRECT_CHOICE);
            choice = decisionHandlerFromList(possibleDamages);
        }
        this.didChooseDamage(weapon, choice, indexOfEffect, forPotentiableWeapon);
        out.println(WEAPON_USED);
    }

    @Override
    public void onPowerupInHandFailure() {
        out.println(POWERUP_FAILURE);
        locked = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void willChoosePowerupSelling(Map<String, String> powerups) {
        out.println(ASK_POWERUP_TO_USE_EFFECTS);
        String weapon = powerups.get(Weapon.weapon_key);
        powerups.remove(Weapon.weapon_key);
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
            availablePowerups.remove(choice);
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
        if(modalitiesToChoose.isEmpty()) this.onWeaponUsingFailure();
        else {
            out.println(CHOOSE_MODALITY);
            String modalitySelected = decisionHandlerFromMap(modalitiesToChoose);
            this.didChooseMode(weapon, modalitySelected);
        }
    }

    /**
     * Private method that handles the decision process when the input parameter is a Map of choices, added to avoid code repetitions.
     * @param args it's the map containing the choices.
     * @return the choice.
     * @throws IllegalArgumentException if an incorrect choice is made.
     */
    private String decisionHandlerFromMap(Map<String, String> args) {
        return decisionHandlerFromList(new ArrayList<>(args.values()));
    }

    /**
     * Private method that handles the decision process when the input parameter is a List of choices, added to avoid code repetitions.
     * @param options it's the list containing the choices.
     * @return the choice, null if the selection was illegal.
     */
    private String decisionHandlerFromList(List<String> options) {
        for(int i = 0; i < options.size(); i++)
            out.println(i + ") " + options.get(i));
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
            if(choice < options.size())
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
        if(effectsToChoose.isEmpty()) this.onWeaponUsingFailure();
        else {
            out.println(CHOOSE_EFFECT);
            String effectsSelected = decisionHandlerFromMap(effectsToChoose);
            while(effectsSelected == null) {
                out.println(INCORRECT_CHOICE);
                effectsSelected = decisionHandlerFromMap(effectsToChoose);
            }
            String box = effectsSelected.replaceAll("[\\[\\]]", "");
            List<String> effects = List.of(box.split(", "));
            this.didChooseEffects(new ArrayList<>(effects), weapon);
        }
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
        this.afterAction();
    }

    @Override
    public void onWeaponUsingFailure() {
        out.println(NO_COMBO_FOUND);
        this.onChooseAction(curSituation);
    }

    @Override
    public void onEndTurn(String curSituation) {
        this.curSituation = curSituation;
        out.println(TURN_ENDING);
        out.println(GAME_SITUATION);
        this.askReload();
        out.println(USE_ANY_OF_YOUR_POWERUP);
        String scanInput = in.nextLine();
        if(scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y")) {
            this.willUsePowerup();
            locked = true;
            while (locked) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        AdrenalineLogger.error(e.toString());
                        AdrenalineLogger.error(e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        out.println(TURN_ENDED);
        locked = true;
        this.client.checkDeaths();
        this.client.turnEnded();
    }

    @Override
    public void askReload() {
        out.println(ASK_RELOAD);
        String scanInput = in.nextLine();
        if(scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y")) {
            out.println(WILL_RELOAD);
            this.askWastePowerupToReload();
        }
        else {
            out.println(WON_T_RELOAD);
            locked = false;
            synchronized (this) {
                this.notifyAll();
            }
        }
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
        locked = true;
        String scanInput = in.nextLine();
        if(scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y"))
            this.client.askForPowerupsToReload();
        else this.client.askWeaponToReload();
        while(locked) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    AdrenalineLogger.error(e.toString());
                    AdrenalineLogger.error(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void onWeaponUnloadedFailure() {
        out.println(NO_WEAPON_UNLOADED);
        locked = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void willReload(List<String> weapons) {
        out.println(CHOOSE_WEAPONS_TO_RELOAD);
        boolean stillReloading = true;
        List<String> weaponsToReload = new ArrayList<>();
        while(stillReloading) {
            out.println(MORE_WEAPONS_TO_RELOAD);
            String choice = decisionHandlerFromList(weapons);
            while(choice == null) {
                out.println(INCORRECT_CHOICE);
                choice = decisionHandlerFromList(weapons);
            }
            weaponsToReload.add(choice);
            out.println(RELOAD_MORE);
            String scanInput = in.nextLine();
            if(!(scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y")))
                stillReloading = false;
            weapons.remove(choice);
        }
        this.didChooseWeaponsToReload(weaponsToReload);
    }

    @Override
    public void onReloadSuccess() {
        out.println(RELOAD_SUCCESS);
        locked = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void onReloadFailure() {
        out.println(NOT_ENOUGH_AMMOS);
        this.askReload();
    }

    @Override
    public void onTurnPowerupFailure() {
        out.println(TURN_POWERUP_FAILURE);
        locked = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void willChoosePowerup(List<String> availablePowerups) {
        if(availablePowerups.isEmpty()) {
            out.println(NO_POWERUPS_IN_HAND);
            locked = false;
            synchronized (this) {
                this.notifyAll();
            }
            return;
        }
        boolean powerupUsing = true;
        while(powerupUsing) {
            out.println(CHOOSE_POWERUP);
            String choice = decisionHandlerFromList(availablePowerups);
            while(choice == null) {
                out.println(INCORRECT_CHOICE);
                choice = decisionHandlerFromList(availablePowerups);
            }
            this.didChoosePowerup(choice);
            lockedOnPowerup = true;
            while(lockedOnPowerup) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        AdrenalineLogger.error(e.toString());
                        AdrenalineLogger.error(e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
            }
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
        locked = false;
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public void willChoosePowerupDamage(Map<String, String> possibleDamages) {
        out.println(CHOOSE_POWERUP_DAMAGE);
        String powerup = possibleDamages.get(Powerup.powerup_key);
        possibleDamages.remove(Powerup.powerup_key);
        String choice = decisionHandlerFromMap(possibleDamages);
        while(choice == null) {
            out.println(INCORRECT_CHOICE);
            choice = decisionHandlerFromMap(possibleDamages);
        }
        this.didChoosePowerupDamage(choice, powerup);
        out.println(POWERUP_USED);
        lockedOnPowerup = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void displayChange(String change) {
        if(!locked) {
            out.println(MOVEMENT_MADE);
            out.println(change);
            out.println(WILL_PLAY_SOON);
        }
        locked = false;
    }

    @Override
    public void willSpawnAfterDeath(List<String> powerupsToSpawn) {
        out.println(SPAWN_AFTER_DEATH_DECISION);
        String choice = decisionHandlerFromList(powerupsToSpawn);
        while(choice == null) {
            out.println(INCORRECT_CHOICE);
            choice = decisionHandlerFromList(powerupsToSpawn);
        }
        this.didChooseSpawnAfterDeath(choice);
    }

    @Override
    protected void didChooseSpawnAfterDeath(String powerupChosen) {
        super.didChooseSpawnAfterDeath(powerupChosen);
    }

    @Override
    public void displayFinalFrenzy() {
        out.println(FINAL_FRENZY);
    }

    @Override
    public void endOfTheGame(String scoreboard) {
        out.println(GAME_ENDED);
        out.println(scoreboard);
    }

    @Override
    public void moveBeforeShot(List<String> movements) {
        out.println(SHOOTING_POSITION);
        String choice = decisionHandlerFromList(movements);
        while(choice == null) {
            out.println(INCORRECT_CHOICE);
            choice = decisionHandlerFromList(movements);
        }
        this.didChooseMovementBeforeShot(choice);
    }

    @Override
    public void tagback(List<String> availablePowerup, String nickname) {
        locked = true;
        out.println(SHOT + nickname + MARK_HIM);
        String scanInput = in.nextLine();
        if(scanInput.equalsIgnoreCase("yes") || scanInput.equalsIgnoreCase("y")) {
            out.println(CHOOSE_POWERUP);
            String choice = decisionHandlerFromList(availablePowerup);
            while(choice == null) {
                out.println(INCORRECT_CHOICE);
                choice = decisionHandlerFromList(availablePowerup);
            }
            this.didChooseTagback(choice);
        }
        else this.didNotUseTagback();
    }

    @Override
    public void awake() {
        //unused
    }

}
