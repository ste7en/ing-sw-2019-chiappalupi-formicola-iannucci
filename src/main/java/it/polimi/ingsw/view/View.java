package it.polimi.ingsw.view;
import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.socket.ClientSocket;
import it.polimi.ingsw.networking.rmi.ClientRMI;
import it.polimi.ingsw.networking.utility.ConnectionType;
import it.polimi.ingsw.utility.AdrenalineLogger;
import java.util.*;


/**
 * View class of the MVC paradigm.
 * It's abstract because several methods will be either overridden or implemented in its children classes.
 *
 * @author Daniele Chiappalupi
 * @author Stefano Formicola
 * @author Elena Iannucci
 */
public abstract class View {

    protected Client client;

    /**
     * Log strings
     */
    protected static final String APPLICATION_STARTED           = "Adrenaline application started. View instance created.";
    private   static final String DID_ASK_CONNECTION            = "Connection parameters chosen: ";
    protected static final String ONLOGIN_FAILURE               = "Username creation failed.";
    private   static final String ONLOGIN_SUCCESS               = "User createUser completed.";
    private   static final String JOIN_WAITING_ROOM             = "Joining the waiting room...";
    protected static final String DID_JOIN_WAITING_ROOM         = "You joined a waiting room. Waiting for other players, the game will start soon...";
    private   static final String DID_CHOOSE_WEAPON             = "Weapon chosen: ";
    private   static final String DID_CHOOSE_DAMAGE             = "Damage chosen.";
    private   static final String DID_CHOOSE_MODALITY           = "Modality chosen: ";
    private   static final String DID_CHOOSE_EFFECTS            = "Effects chosen.";
    private   static final String DID_SELECT_WEAPONS_TO_RELOAD  = "Weapons selected to reload: ";
    private   static final String DID_USE_WEAPON                = "Weapon used with success.";

    /**
     * Failure messages
     */
    public static final String CHARACTER_NOT_AVAILABLE          = "The character you chose isn't available.";

    public abstract void onViewUpdate();

    public abstract void onFailure(String message);

    /**
     * Method implemented by subclasses when a new game starts.
     * This super method should be called in order to set View's
     * gameID attribute.
     */
    public void onStart() {
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
        AdrenalineLogger.info(DID_ASK_CONNECTION + type + " " + host + ":" + port);
        if (type == ConnectionType.SOCKET) {
            client = new ClientSocket(host, port);
        }
        if (type == ConnectionType.RMI) {
            client = new ClientRMI(host, port);
        }
        client.registerObserver(this);
    }

    /**
     * Abstract method implemented by subclasses and used to ask for username to clients
     */
    protected abstract void willCreateUser();

    /**
     * Abstract method implemented by subclasses and used to create a new user
     * logged to the server.
     */
    public void createUser(String username){
        client.createUser(username);
    }

    /**
     * Called to notify a createUser failure
     */
    public void onLoginFailure() {
        AdrenalineLogger.error(ONLOGIN_FAILURE);
        willCreateUser();
    }

    /**
     * Called to notify a successful createUser
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
        client.joinWaitingRoom(username);
    }

    /**
     * Confirmation message sent by the server when the user
     * is added to the game waiting room
     */
    public abstract void didJoinWaitingRoom();

    /**
     * When the client is asked to choose a character
     */
    public abstract void willChooseCharacter(List<String> availableCharacters);

    /**
     * When the client chooses a character
     */
    public void didChooseCharacter(String character) {
        client.choseCharacter(character);
    }

    /**
     * Public method implemented by subclasses when choosing a map configuration.
     * The user will be prompted to choose which map configuration does he wants to pick.
     */
    public abstract void willChooseGameMap();

    /**
     * Called when the map configuration has been chosen by the player.
     */
    public void didChooseGameMap(String configuration) {
        client.choseGameMap(configuration);
    }

    /**
     * Called to begin the process of choosing a spawn point.
     */
    public void willChooseSpawnPoint() {
        client.askForPossibleSpawnPoints();
    }

    /**
     * Called when the server has sent the two powerups that will be used to choose the spawn point.
     * @param powerups it's the array of powerup that will be used to choose the spawn point.
     */
    public abstract void onChooseSpawnPoint(List<String> powerups);

    /**
     * Public method implemented by subclasses when choosing a weapon.
     * @param powerupChosenAsSpawnPoint it's the powerup that has been chosen from the player: it will be discarded and used to let him spawn.
     * @param otherPowerup it's the other powerup: it will be added to the hand of the player.
     */
    public void didChooseSpawnPoint(String powerupChosenAsSpawnPoint, String otherPowerup) {
        client.choseSpawnPoint(powerupChosenAsSpawnPoint, otherPowerup);
    }

    /**
     * Ele
     */
    public abstract void onChooseAction();

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
     * Public method used to start the process of using weapons.
     */
    public void shootPeople() {
        this.client.askWeapons();
    }

    /**
     * Public method implemented by subclasses when the player doesn't have any weapon in his hand but has started the process of using them.
     */
    public abstract void onShootPeopleFailure();

    /**
     * Public method implemented by subclasses when choosing a weapon.
     * The user will be prompted to choose which weapon he wants to use.
     *
     * @param weapons it's an ArrayList containing all of the weapons that the player can use.
     */
    public abstract void willChooseWeapon(List<String> weapons);

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
     * Public method implemented by subclasses and called when no possible damage can be done with the weapon and the effect selected.
     */
    public abstract void onDamageFailure();

    /**
     * Public method implemented by subclasses when choosing the damages to make.
     *
     * @param damagesToChoose it's a map containing all of the possible damages that can be made and the weapon used to make them.
     */
    public abstract void willChooseDamage(Map<String, String> damagesToChoose);

    /**
     * Public method implemented by subclasses and called when a player has selected to use powerups but hasn't got any of them.
     */
    public abstract void onPowerupInHandFailure();

    /**
     * Public method implemented by subclasses when choosing if using a powerup to get its ammo or not.
     *
     * @param powerups it's a map containing the powerups that the player has in his hand and the next thing to do after the powerups choosing.
     */
    public abstract void willChoosePowerupSelling(Map<String, String> powerups);

    /**
     * Called when the player has decided if he wants to use any powerup to afford the cost of the weapon.
     *
     * @param weapon it's the weapon that is being used.
     * @param powerup it's a list containing the powerups chosen by the player, empty if none has been chosen.
     */
    protected void didChoosePowerupSelling(String weapon, List<String> powerup) {
        this.client.useWeaponAfterPowerupAsking(weapon, powerup);
    }

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
     * Called when the player has to decide if use a powerup to make additional damage after the shoot.
     * @param powerups it's a list containing the powerup that can be used.
     */
    public abstract void askPowerupAfterShot(List<String> powerups);

    /**
     * Called when the process of using a weapon has ended.
     */
    public void didUseWeapon() {
        AdrenalineLogger.info(DID_USE_WEAPON);
    }

    /**
     * Ste
     */
    public abstract void onEndTurn();

    /**
     * Public method implemented by subclasses when the player has to decide whether he wants to reload his weapons or not.
     */
    public abstract void askReload();

    /**
     * Public method called when the player has decided which powerup does he want to sell to reload his weapons.
     * @param powerups it's the list of powerups that has been chosen by the player.
     */
    public abstract void willSellPowerupToReload(List<String> powerups);

    /**
     * Public method implemented by subclasses and called when the player has no weapons unloaded in his hand but has chosen to reload them.
     */
    public abstract void onWeaponUnloadedFailure();

    /**
     * Public method implemented by subclasses when the player has to decide what weapon does he want to reload.
     *
     * @param weapons it's the list of names of the weapons that the player has in his hand and that are not loaded.
     */
    public abstract void willReload(List<String> weapons);

    /**
     * Called when the weapons to be reloaded have been chosen by the player.
     *
     * @param weaponsToReload it's the list of names of the weapons that the player want to reload.
     */
    protected void didChooseWeaponsToReload(List<String> weaponsToReload) {
        AdrenalineLogger.info(DID_SELECT_WEAPONS_TO_RELOAD);
        this.client.reloadWeapons(weaponsToReload);
    }

    /**
     * Public method implemented by subclasses, called when the reload process has ended with success.
     */
    public abstract void onReloadSuccess();

    /**
     * Public method implemented by subclasses, called when the reload process has ended with failure.
     */
    public abstract void onReloadFailure();

    /**
     * Starts the process of using a powerup.
     */
    public void willUsePowerup() {
        this.client.askForUsablePowerups();
    }

    /**
     * Public method implemented by subclasses, called when a player has no powerup that can be used during his turn in his hand but has selected that he wants to use them.
     */
    public abstract void onTurnPowerupFailure();

    /**
     * Public method implemented by subclasses, called when the player has to decide which powerup does he wants to use.
     *
     * @param availablePowerups it's the list of powerups owned by the player.
     */
    public abstract void willChoosePowerup(List<String> availablePowerups);

    /**
     * Called when the powerup to be used has been chosen by the player.
     *
     * @param powerup it's the powerup that has been chosen.
     */
    protected void didChoosePowerup(String powerup) {
        this.client.askPowerupDamages(powerup);
    }

    /**
     * Public method implemented by subclasses, called when the player has to decide what damage does he want to do with the powerup he is using.
     *
     * @param possibleDamages it's a map containing both the possible damages and the powerup used to do them.
     */
    public abstract void willChoosePowerupDamage(Map<String, String> possibleDamages);

    /**
     * Called when the damage to be done with the powerup has been chosen from the player.
     *
     * @param choice it's the damage chosen by the player.
     * @param powerup it's the Powerup::toString of the powerup that has been chosen.
     */
    protected void didChoosePowerupDamage(String choice, String powerup) {
        this.client.usePowerup(powerup, choice);
    }
}
