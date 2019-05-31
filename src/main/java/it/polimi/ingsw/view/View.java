package it.polimi.ingsw.view;

import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.rmi.ClientRMIConnectionHandler;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.util.*;

import static it.polimi.ingsw.networking.utility.CommunicationMessage.*;


/**
 * View class of the MVC paradigm.
 * It's abstract because several methods will be either overridden or implemented in its children classes.
 *
 * @author Daniele Chiappalupi, Stefano Formicola, Elena Iannucci
 */
public abstract class View implements Observer{

    protected Client client;
    protected UUID gameID;
    protected PlayerColor playerColor;
    protected ClientRMIConnectionHandler clientRMI;
    protected ConnectionType connectionType;

    /**
     * Log strings
     */
    protected static String APPLICATION_STARTED   = "Adrenaline application started. View instance created.";
    protected static String DID_ASK_CONNECTION    = "Connection parameters chosen: ";
    protected static String ONLOGIN_FAILURE       = "Username creation failed.";
    protected static String ONLOGIN_SUCCESS       = "User login completed.";
    protected static String JOIN_WAITING_ROOM     = "Joining the waiting room...";
    protected static String DID_JOIN_WAITING_ROOM = "";
    protected static String DID_CHOOSE_WEAPON     = "Weapon chosen: ";
    protected static String DID_CHOOSE_DAMAGE     = "Damage chosen.";
    protected static String DID_CHOOSE_MODALITY   = "Modality chosen: ";


    public abstract void onViewUpdate();

    public abstract void onFailure();

    public abstract void onStart();

    /**
     * Public method implemented by subclasses when starting a new application instance.
     * The user will be prompted to choose the connection parameters.
     */
    protected abstract void willChooseConnection();

    /**
     * Method called after a user choice of the connection.
     * Shouldn't be reimplemented by a subclass.
     */
    public void didChooseConnection(ConnectionType type, int port, String host) {
        connectionType = type;
        AdrenalineLogger.info(DID_ASK_CONNECTION + type + " " + host + ":" + port);
        if (type==ConnectionType.SOCKET){
            this.client = new Client(type, host, port);
            client.registerObserver(this);
        }
        if (type==ConnectionType.RMI){
            clientRMI = new ClientRMIConnectionHandler(port);
        }

    }

    /**
     * Abstract method implemented by subclasses and used to create a new user
     * logged to the server.
     */
    protected abstract void login();

    /**
     * Called to notify a login failure
     */
    public void onLoginFailure() {
        AdrenalineLogger.error(ONLOGIN_FAILURE);
        login();
    }

    /**
     * Called to notify a successful login
     * @param username username
     */
    public void onLoginSuccess(String username) {
        AdrenalineLogger.success(ONLOGIN_SUCCESS);
        joinWaitingRoom(username);
    }

    /**
     * Called to join the server's waiting room for a new game
     * @param username username
     */
    private void joinWaitingRoom(String username) {
        AdrenalineLogger.info(JOIN_WAITING_ROOM);
        var args = new HashMap<String, String>();
        args.put(User.username_key, username);
        this.client.send(CommunicationMessage.from(0, USER_LOGIN, args));
    }

    /**
     * Ste
     */
    public abstract void didJoinWaitingRoom();

    /**
     * Ele
     */
    public abstract void willChooseCharacter();

    /**
     * Ele
     */
    public abstract void didChooseCharacter();

    /**
     * Ele
     */
    public abstract void willChooseGameSettings();

    /**
     * Ele
     */
    public abstract void didChooseGameSettings();

    /**
     * Ele
     */
    public abstract void willChooseSpawnPoint();

    /**
     * Ele
     */
    public abstract void didChooseSpawnPoint();

    /**
     * Ele
     */
    public abstract void onMove();

    /**
     * Ele
     */
    public abstract void willChooseMovement();

    /**
     * Ele
     */
    public abstract void didChooseMovement();

    /**
     * Ele
     */
    public abstract void willChooseWhatToGrab();

    /**
     * Ele
     */
    public abstract void didChooseWhatToGrab();

    /**
     * Public method implemented by subclasses when choosing a weapon.
     * The user will be prompted to choose which weapon he wants to use.
     *
     * @param weapons it's an ArrayList containing all of the weapons that the player can use.
     */
    public abstract void willChooseWeapon(ArrayList<String> weapons);

    /**
     * Called when the Weapon to be used is chosen.
     *
     * @param weaponSelected it's the weapon that has been selected by the player.
     */
    public void didChooseWeapon(String weaponSelected) {
        AdrenalineLogger.info(DID_CHOOSE_WEAPON + weaponSelected);
        Map args = new HashMap<String, String>();
        args.put(Weapon.weapon_key, weaponSelected);
        args.put(PlayerColor.playerColor_key, playerColor.toString());
        this.client.send(CommunicationMessage.from(0, WEAPON_TO_USE, args, gameID));
    }

    /**
     * Public method implemented by subclasses when choosing the damages to make.
     *
     * @param damagesToChoose it's a map containing all of the possible damages that can be made and the weapon used to make them.
     */
    public abstract void willChooseDamage(Map<String, String> damagesToChoose);

    /**
     * Called when the damages to be done have been chosen by the player.
     *
     * @param damageToDo it's a Map<String, String> containing the damages to make and some other information needed to apply them.
     */
    public void didChooseDamage(Map<String, String> damageToDo) {
        AdrenalineLogger.info(DID_CHOOSE_DAMAGE);
        damageToDo.put(PlayerColor.playerColor_key, playerColor.toString());
        this.client.send(CommunicationMessage.from(0, DAMAGE_TO_MAKE, damageToDo, gameID));
    }

    /**
     * Public method implemented by subclasses when choosing which modality of a selectable weapon does the player want to use.
     *
     * @param modalitiesToChoose it's a map containing the modalities to choose and the weapon that is being used.
     */
    public abstract void willChooseMode(Map<String, String> modalitiesToChoose);

    /**
     * Called when the modality has been chosen by the player.
     *
     * @param modalityChosen it's a map containing the modality chosen and the weapon that is being used.
     */
    public void didChooseMode(Map<String, String> modalityChosen) {
        AdrenalineLogger.info(DID_CHOOSE_MODALITY + modalityChosen.get(Effect.effect_key));
        modalityChosen.put(PlayerColor.playerColor_key, playerColor.toString());
        this.client.send(CommunicationMessage.from(0, EFFECT_TO_USE, modalityChosen, gameID));
    }

    /**
     * Dani
     */
    public abstract void willChooseEffects();

    /**
     * Dani
     */
    public abstract void didChooseEffects();

    /**
     * Ste
     */
    public abstract void onEndTurn();

    /**
     * Dani
     */
    public abstract void willReload();

    /**
     * Dani
     */
    public abstract void didReload();

    /**
     * Ste
     */
    public abstract void willUsePowerup();

    /**
     * Ste
     */
    public abstract void didUsePowerup();

    /**
     * Ste
     */
    public abstract void willChoosePowerup();

    /**
     * Ste
     */
    public abstract void didChoosePowerup();

    /**
     * Ste
     */
    public abstract void willChoosePowerupEffect();

    /**
     * Ste
     */
    public abstract void didChoosePowerupEffect();
}
