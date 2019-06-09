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
        try {
            handlerGUI.chooseConnection();
        } catch (FileNotFoundException e){

        }
    }

    @Override
    protected void willCreateUser() {
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
            handlerGUI.chooseCharacter(availableCharacters);
        } catch (FileNotFoundException e){
            System.err.println("ClientRMI exception: " + e.toString());
        }

    }

    @Override
    public void willChooseGameMap() {
        try {
            handlerGUI.chooseGameMap();
        } catch (FileNotFoundException e){
            System.err.println("ClientRMI exception: " + e.toString());
        }
    }

    @Override
    public void onChooseSpawnPoint(ArrayList<String> powerups) {
        try {
            handlerGUI.chooseSpawnPoint(powerups);
        } catch (FileNotFoundException e ){
            System.err.println("ClientRMI exception: " + e.toString());
        }
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
    public void willChoosePowerupSelling(Map<String, String> powerups) {

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
    public void willChoosePowerup(List<String> availablePowerups) {

    }

    @Override
    public void willChoosePowerupDamage(Map<String, String> possibleDamages) {

    }

    @Override
    public void update(Observable o, Object arg) {

    }


}

