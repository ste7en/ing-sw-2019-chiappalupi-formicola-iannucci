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

    public abstract void willChooseConnection();

    public abstract void didChooseConnection();

    public abstract void login();

    public abstract void onLoginFailure();

    public abstract void onLoginSuccess();

    public abstract void joinWaitingRoom();

    public abstract void didJoinWaitingRoom();

    public abstract void willChooseCharacter();

    public abstract void didChooseCharacter();

    public abstract void willChooseGameSettings();

    public abstract void didChooseGameSettings();

    public abstract void willChooseSpawnPoint();

    public abstract void didChooseSpawnPoint();

    public abstract void onMove();

    public abstract void willChooseMovement();

    public abstract void didChooseMovement();

    public abstract void willChooseWhatToGrab();

    public abstract void didChooseWhatToGrab();

    public abstract void willChooseWeapon();

    public abstract void didChooseWeapon();

    public abstract void willChooseDamage();

    public abstract void didChooseDamage();

    public abstract void willChooseMode();

    public abstract void didChooseMode();

    public abstract void willChooseEffects();

    public abstract void didChooseEffects();

    public abstract void onEndTurn();

    public abstract void willReload();

    public abstract void didReload();

    public abstract void willUsePowerup();

    public abstract void didUsePowerup();

    public abstract void willChoosePowerup();

    public abstract void didChoosePowerup();

    public abstract void willChoosePowerupEffect();

    public abstract void didChoosePowerupEffect();
}
