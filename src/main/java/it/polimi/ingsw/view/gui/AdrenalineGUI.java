package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.view.View;

import java.io.FileNotFoundException;
import java.util.*;

public class AdrenalineGUI extends View {

    private GUIHandler handlerGUI;

    public AdrenalineGUI(GUIHandler handlerGUI){
        this.handlerGUI = handlerGUI;
    }

    @Override
    protected void willChooseConnection() {

    }

    @Override
    protected void createUser() {
    }

    @Override
    public void onLoginFailure(){
        AdrenalineLogger.error(ONLOGIN_FAILURE);
        handlerGUI.handleLoginFailure();
    }

    @Override
    public void onLoginSuccess(String username) {
        didJoinWaitingRoom();
    }



    @Override
    public void onViewUpdate() {

    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onStart(UUID gameID) {

    }

    @Override
    public void willChooseCharacter(ArrayList<String> availableCharacters) {
        try {
            handlerGUI.characterChoice(availableCharacters);
        } catch (FileNotFoundException e){
            System.err.println("ClientRMI exception: " + e.toString());
        }

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
    public void askReload() {

    }

    @Override
    public void willReload(List<String> weapons) {

    }

    @Override
    public void onReloadSuccess() {

    }

    @Override
    public void onReloadFailure() {

    }

    @Override
    public void didUsePowerup() {

    }

    @Override
    public void willChoosePowerup(List<String> availablePowerups) {

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

    Client getClient(){
        return this.client;
    }

}

