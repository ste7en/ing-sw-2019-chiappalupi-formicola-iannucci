package it.polimi.ingsw.view;

import it.polimi.ingsw.model.PlayerColor;
import it.polimi.ingsw.networking.Client;

import java.util.Observer;


/**
 * View class of the MVC paradigm.
 * It's abstract because several methods will be either overridden or implemented in its children classes.
 *
 * @authors Daniele Chiappalupi, Stefano Formicola, Elena Iannucci
 */
public abstract class View implements Observer{

    private Client client;

    private PlayerColor playerColor;

    public abstract void onViewUpdate();

    public abstract void onFailure();

    /**
     * Ste
     */
    public abstract void willChooseConnection();

    /**
     * Ste
     */
    public abstract void didChooseConnection();

    /**
     * Ste
     */
    public abstract void login();

    /**
     * Ste
     */
    public abstract void onLoginFailure();

    /**
     * Ste
     */
    public abstract void onLoginSuccess();

    /**
     * Ste
     */
    public abstract void joinWaitingRoom();

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
     * Dani
     */
    public abstract void willChooseWeapon();

    /**
     * Dani
     */
    public abstract void didChooseWeapon();

    /**
     * Dani
     */
    public abstract void willChooseDamage();

    /**
     * Dani
     */
    public abstract void didChooseDamage();

    /**
     * Dani
     */
    public abstract void willChooseMode();

    /**
     * Dani
     */
    public abstract void didChooseMode();

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
