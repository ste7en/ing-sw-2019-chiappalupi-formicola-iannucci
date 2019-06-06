package it.polimi.ingsw.view;

import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.PotentiableWeapon;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.PlayerColor;
import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.socket.ClientSocket;
import it.polimi.ingsw.networking.rmi.ClientRMI;
import it.polimi.ingsw.networking.utility.CommunicationMessage;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;

import java.util.*;

import static it.polimi.ingsw.networking.utility.CommunicationMessage.*;


/**
 * View class of the MVC paradigm.
 * It's abstract because several methods will be either overridden or implemented in its children classes.
 *
 * @author Daniele Chiappalupi
 * @author Stefano Formicola
 * @author Elena Iannucci
 */
public abstract class View implements Observer{

    protected Client client;
    protected UUID gameID;
    protected PlayerColor playerColor;
    protected ConnectionType connectionType;

    /**
     * Log strings
     */
    protected static final String APPLICATION_STARTED   = "Adrenaline application started. View instance created.";
    protected static final String DID_ASK_CONNECTION    = "Connection parameters chosen: ";
    protected static final String ONLOGIN_FAILURE       = "Username creation failed.";
    protected static final String ONLOGIN_SUCCESS       = "User login completed.";
    protected static final String JOIN_WAITING_ROOM     = "Joining the waiting room...";
    protected static final String DID_JOIN_WAITING_ROOM = "";
    protected static final String DID_CHOOSE_WEAPON     = "Weapon chosen: ";
    protected static final String DID_CHOOSE_DAMAGE     = "Damage chosen.";
    protected static final String DID_CHOOSE_MODALITY   = "Modality chosen: ";
    protected static final String DID_CHOOSE_EFFECTS     = "Effects chosen.";


    public abstract void onViewUpdate();

    public abstract void onFailure();

    /**
     * Method implemented by subclasses when a new game starts.
     * This super method should be called in order to set View's
     * gameID attribute.
     * @param gameID the game identifier used by tbe server
     */
    public void onStart(UUID gameID) {
        this.gameID = gameID;
    }

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
        if (type == ConnectionType.SOCKET){
            this.client = new ClientSocket(host, port);
        }
        if (type == ConnectionType.RMI){
            client = new ClientRMI(host, port);
        }
        client.registerObserver(this);
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
        //this.client.send(CommunicationMessage.from(0, USER_LOGIN, args));
    }

    /**
     * Confirmation message sent by the server when the user
     * is added to the game waiting room
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
    protected void didChooseWeapon(String weaponSelected) {
        AdrenalineLogger.info(DID_CHOOSE_WEAPON + weaponSelected);
        this.client.useWeapon(weaponSelected);
    }

    /**
     * Public method implemented by subclasses when choosing the damages to make.
     *
     * @param damagesToChoose it's a map containing all of the possible damages that can be made and the weapon used to make them.
     */
    public abstract void willChooseDamage(Map<String, String> damagesToChoose);

    /**
     * Called when the damages to be done have been chosen by the player.
     * @param weapon it's the weapon that is being used.
     * @param damage it's the damage that has been selected.
     * @param indexOfEffect it's the index of the effect that is being used.
     * @param forPotentiableWeapon it's the string containing the information about potentiable weapons.
     */
    protected void didChooseDamage(String weapon, String damage, String indexOfEffect, String forPotentiableWeapon) {
        AdrenalineLogger.info(DID_CHOOSE_DAMAGE);
        this.client.makeDamage(weapon, damage, indexOfEffect, forPotentiableWeapon);
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
     * @param weapon it's the weapon that is being used.
     * @param effect it's the effect that has been chosen.
     */
    protected void didChooseMode(String weapon, String effect) {
        AdrenalineLogger.info(DID_CHOOSE_MODALITY + effect);
        this.client.useMode(weapon, effect);
    }

    /**
     * Public method implemented by subclasses when choosing which effects of a potentiable weapon does the player want to use.
     */
    public abstract void willChooseEffects(Map<String, String> effectsToChoose);

    /**
     * Called when the effects to use have been chosen by the player.
     *
     * @param effectsToUse it's a list containing the effects chosen.
     * @param weapon it's the weapon that is being used.
     */
    protected void didChooseEffects(List<String> effectsToUse, String weapon) {
        AdrenalineLogger.info(DID_CHOOSE_EFFECTS);
        this.client.useEffect(weapon, effectsToUse);
    }

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
     * Dani
     */
    public abstract void willUsePowerup();

    /**
     * Dani
     */
    public abstract void didUsePowerup();

    /**
     * Dani
     */
    public abstract void willChoosePowerup();

    /**
     * Dani
     */
    public abstract void didChoosePowerup();

    /**
     * Dani
     */
    public abstract void willChoosePowerupEffect();

    /**
     * Dani
     */
    public abstract void didChoosePowerupEffect();
}
