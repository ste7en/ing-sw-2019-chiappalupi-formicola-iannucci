package it.polimi.ingsw.view.gui;

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
    public void onStart(){

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
    public void didJoinWaitingRoom() {

    }

    @Override
    public void onViewUpdate() {

    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void willChooseCharacter(List<String> availableCharacters) {
        try {
            handlerGUI.onChooseCharacter(availableCharacters);
        } catch (Exception e){
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
    public void onChooseAction() {
        try {
            handlerGUI.chooseAction();
        } catch (FileNotFoundException e){
            System.err.println("ClientRMI exception: " + e.toString());
        }
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
    public void onShootPeopleFailure() {

    }

    @Override
    public void willChooseWeapon(List<String> weapons) {
    }

    @Override
    public void onDamageFailure() {

    }

    @Override
    public void willChooseDamage(Map<String, String> damagesToChoose) {

    }

    @Override
    public void onPowerupInHandFailure() {

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
    public void askPowerupAfterShot(List<String> powerups) {

    }

    @Override
    public void onEndTurn() {

    }

    @Override
    public void askReload() {

    }

    @Override
    public void willSellPowerupToReload(List<String> powerups) {

    }

    @Override
    public void onWeaponUnloadedFailure() {

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
    public void onTurnPowerupFailure() {

    }

    @Override
    public void willChoosePowerup(List<String> availablePowerups) {

    }

    @Override
    public void willChoosePowerupDamage(Map<String, String> possibleDamages) {

    }

}

