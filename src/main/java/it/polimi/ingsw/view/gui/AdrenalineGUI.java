package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.JavaFXApp;
import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.view.View;

import java.util.ArrayList;
import java.util.Observable;

public class AdrenalineGUI extends View {

    public void launch() {
        this.willChooseConnection();
        //this.login();
    }

    public static void main(String[] args) {
        AdrenalineGUI adrenalineGUI = new AdrenalineGUI();
        adrenalineGUI.launch();

    }

    @Override
    protected void willChooseConnection() {
        JavaFXApp startConnection = new JavaFXApp();
        String[] arguments = new String[] {"123"};
        startConnection.main(arguments);
    }

    @Override
    protected void login() {

    }

    @Override
    public void onLoginFailure() {
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
    public void willChooseWeapon(ArrayList<String> weapons) {
    }

    @Override
    public void willChooseDamage(ArrayList<ArrayList<Damage>> possibleDamages) {

    }

    @Override
    public void didChooseDamage() {

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

