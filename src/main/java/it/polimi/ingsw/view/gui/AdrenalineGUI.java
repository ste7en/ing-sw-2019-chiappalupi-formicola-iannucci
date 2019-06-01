package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.rmi.ClientRMIConnectionHandler;
import it.polimi.ingsw.view.View;

import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;

public class AdrenalineGUI extends View {

    private GUIHandler GUIHandler;

    public AdrenalineGUI(GUIHandler GUIHandler){
        this.GUIHandler = GUIHandler;
    }

    @Override
    protected void willChooseConnection() {

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
    public void willChooseDamage(Map<String, String> damagesToChoose) {

    }

    @Override
    public void willChooseMode(Map<String, String> modalitiesToChoose) {

    }

    @Override
    public void willChooseEffects(Map<String, String> effectsToChoose) {

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

    Client getClient(){
        return this.client;
    }

    ClientRMIConnectionHandler getClientRMIConnectionHandler() {
        return this.clientRMI;
    }

}

