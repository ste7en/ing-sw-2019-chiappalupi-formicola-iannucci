package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.model.cards.Powerup;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.view.View;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class AdrenalineGUI extends View {

    private GUIHandler handlerGUI;

    AdrenalineGUI(GUIHandler handlerGUI){
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
        List<String> keys = new ArrayList<>(update.keySet());
        String key = keys.get(0);
        List<String> value = update.get(key);
        switch (key) {
            case GameMap.gameMap_key:
                try {
                    handlerGUI.createGameMap(value.get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Player.playerKey_players:
                try {
                    handlerGUI.addPlayerBoards(value);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                //value è la lista dei colori dei player (me per primo)
                break;
            case Weapon.weapon_key:
                try {
                    handlerGUI.updateWeaponsCards(value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei nomi delle armi in mano al giocatore
                break;
            case Powerup.powerup_key:
                try {
                    handlerGUI.updatePowerups(value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei powerup::toString dei powerup in mano
                break;
            case AmmoColor.ammoColorKey_blue:
                try {
                    handlerGUI.updateBlueAmmos(Integer.parseInt(value.get(0)));
                }catch (Exception e){
                    e.printStackTrace();
                }
                //il primo elemento di value è l'Integer::toString del numero di ammo di quel colore che il giocatore ha in mano
                break;
            case AmmoColor.ammoColorKey_red:
                try {
                    handlerGUI.updateRedAmmos(Integer.parseInt(value.get(0)));
                }catch (Exception e){
                    e.printStackTrace();
                }
                //come sopra
                break;
            case AmmoColor.ammoColorKey_yellow:
                try {
                    handlerGUI.updateYellowAmmos(Integer.parseInt(value.get(0)));
                }catch (Exception e){
                    e.printStackTrace();
                }
                //come sopra
                break;
            case Board.weaponsKey_blue:
                try {
                    handlerGUI.updateDeckAbove(value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei nomi delle armi nello spawn point blue
                break;
            case Board.weaponsKey_red:
                try {
                    handlerGUI.updateDeckLeft(value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei nomi delle armi nello spawn point red
                break;
            case Board.weaponsKey_yellow:
                try {
                    handlerGUI.updateDeckRight(value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei nomi delle armi nello spawn point yellow
                break;
            case Damage.damage_key:
                //value è la lista dei colori dei giocatori che hanno danneggiato il player
                break;
            case Damage.mark_key:
                //value è la lista dei colori dei giocatori che hanno marcato il player
                break;
            case GameMap.ROW_1:
                try {
                    handlerGUI.updateFirstRow(value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista degli ammotile::tostring nella riga 1 della board
                break;
            case GameMap.ROW_2:
                try {
                    handlerGUI.updateSecondRow(value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista degli ammotile::toString nella riga 2 della board
                break;
            case GameMap.ROW_3:
                try {
                    handlerGUI.updateThirdRow(value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista degli ammoTile::toString nella riga 3 della board
                break;
            case Player.playerKey_damages_blue:
                try {
                    handlerGUI.updateDamages("blue", value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei colori dei danni fatti al blue
                break;
            case Player.playerKey_damages_yellow:
                try {
                    handlerGUI.updateDamages("yellow", value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei colori dei danni fatti al yellow
                break;
            case Player.playerKey_damages_green:
                try {
                    handlerGUI.updateDamages("green", value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei colori dei danni fatti al green
                break;
            case Player.playerKey_damages_grey:
                try {
                    handlerGUI.updateDamages("grey", value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei colori dei danni fatti al grey
                break;
            case Player.playerKey_damages_purple:
                try {
                    handlerGUI.updateDamages("purple", value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei colori dei danni fatti al purple
                break;
            case Player.playerKey_marks_blue:
                try {
                    handlerGUI.updateMarks("blue", value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei colori dei marchi fatti al blue
                break;
            case Player.playerKey_marks_yellow:
                try {
                    handlerGUI.updateMarks("yellow", value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei colori dei marchi fatti al yellow
                break;
            case Player.playerKey_marks_green:
                try {
                    handlerGUI.updateMarks("green", value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei colori dei marchi fatti al green
                break;
            case Player.playerKey_marks_grey:
                try {
                    handlerGUI.updateMarks("grey", value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei colori dei marchi fatti al grey
                break;
            case Player.playerKey_marks_purple:
                try {
                    handlerGUI.updateMarks("purple", value);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //value è la lista dei colori dei marchi fatti al purple
                break;
        }
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
        handlerGUI.displayMessage(GRAB_SOMETHING + ASK_POWERUP_TO_GRAB_WEAPON);
    }

    public void grabSomethingResponse(boolean response){
        if(response) this.client.powerupSellingToGrabWeapon();
        else {this.client.askPicks(new ArrayList<>());}
    }

    @Override
    public void sellPowerupToGrabWeapon(List<String> powerups) {
    }

    @Override
    public void willChooseWhatToGrab(List<String> possiblePicks) {
        Map<Integer, Integer> ammoTiles = new HashMap<>();
        List<String> weapons = new ArrayList<>();
        for(String pick : possiblePicks) {
            if(pick.substring(0, 9).equals("Ammo Card")) {
                String s3 = pick.substring(pick.length() - 6, pick.length() - 2);
                s3 = s3.replaceAll(",", "");
                List<Integer> x = Arrays.stream(s3.split("\\s"))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                ammoTiles.put(x.get(0), x.get(1));
            } else weapons.add(pick.substring(0, pick.length() - 17));
        }
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
    public void displayFinalFrenzy() {

    }

    @Override
    public void endOfTheGame(String scoreboard) {

    }

    @Override
    public void moveBeforeShot(List<String> movements) {

    }

    @Override
    public void tagback(List<String> availablePowerup, String nickname) {

    }

}

