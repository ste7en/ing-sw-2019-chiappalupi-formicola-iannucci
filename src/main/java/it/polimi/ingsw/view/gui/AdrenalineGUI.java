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
    public void timeoutHasExpired() {

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
    public void update(Map<String, List<String>> update) {

    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void willChooseCharacter(List<String> availableCharacters) {
        try {
            List<String> fixedCharacter = new ArrayList<>();
            for(String character : availableCharacters) {
                character = character.replaceAll("[^a-zA-Z\\-]", "").replaceAll("m", "");
                fixedCharacter.add(character);
            }
            handlerGUI.onChooseCharacter(fixedCharacter);
        } catch (Exception e){
            System.err.println("ClientRMI exception: " + e.toString());
        }

    }

    @Override
    public void onChooseCharacterSuccess(String characterChosen) {
        handlerGUI.onChooseCharacterSuccess();
    }

    @Override
    public void willChooseSkulls() {

    }

    @Override
    public void willChooseGameMap() {
            handlerGUI.chooseGameMap();
    }

    @Override
    public void onChooseSpawnPoint(List<String> powerups) {
            handlerGUI.drawTwoPowerups(powerups);
    }

    @Override
    public void newAction() {

    }

    @Override
    public void onChooseAction(String map) {
        try {
            handlerGUI.chooseAction();
        } catch (FileNotFoundException e){
            System.err.println("ClientRMI exception: " + e.toString());
        }
    }

    public void didChooseAction(String action) {
        switch (action){
            case "move":
                client.getAvailableMoves();
                break;
            case "grab":
                grabSomething();
                break;
            case "shoot":
                shootPeople();
                break;
        }
    }

    @Override
    public void willChooseMovement(List<String> moves) {

    }

    @Override
    public void grabSomething() {

    }

    @Override
    public void sellPowerupToGrabWeapon(List<String> powerups) {

    }

    @Override
    public void willChooseWhatToGrab(List<String> possiblePicks) {

    }

    @Override
    public void onGrabSuccess() {

    }

    @Override
    public void onGrabFailure() {

    }

    @Override
    public void onGrabFailurePowerup(List<String> powerup) {

    }

    @Override
    public void onGrabFailureWeapon(List<String> weapon) {

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
    public void onWeaponUsingFailure() {

    }

    @Override
    public void afterAction() {

    }

    @Override
    public void onEndTurn(String curSituation) {

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

    @Override
    public void displayChange(String change) {

    }

    @Override
    public void willSpawnAfterDeath(List<String> powerupsToSpawn) {

    }

    @Override
    public void canContinue() {

    }

    @Override
    public void displayFinalFrenzy() {

    }

    @Override
    public void endOfTheGame(String scoreboard) {

    }

    @Override
    public void moveBeforeShot(List<String> movements) {

    }

}

