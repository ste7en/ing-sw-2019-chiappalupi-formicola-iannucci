package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.utility.AdrenalineLogger;
import it.polimi.ingsw.utility.Loggable;
import it.polimi.ingsw.view.View;

import java.util.*;
import java.util.stream.Collectors;

public class AdrenalineGUI extends View implements Loggable {

    private GUIHandler handlerGUI;
    private Random r;
    private boolean isBuilt;

    AdrenalineGUI(GUIHandler handlerGUI){
        this.handlerGUI = handlerGUI;
        this.r = new Random();
        this.isBuilt = false;
    }

    @Override
    public void onStart(){
    }

    @Override
    public void timeoutHasExpired() {

    }

    @Override
    protected void willChooseConnection() {
        handlerGUI.chooseConnection();
    }

    @Override
    protected void willCreateUser() {
        //unused
    }

    @Override
    public void onLoginFailure(){
        AdrenalineLogger.error(ONLOGIN_FAILURE);
        handlerGUI.handleLoginFailure();
    }

    @Override
    public void didJoinWaitingRoom() {
        //unused
    }

    @Override
    public void update(Map<String, List<String>> update) {
        try {
            List<String> keys = new ArrayList<>(update.keySet());
            String key = keys.get(0);
            List<String> value = update.get(key);
            switch (key) {
                case GameMap.gameMap_key:
                    try {
                        handlerGUI.createGameMap(value.get(0));
                    } catch (Exception e) {
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Player.playerKey_players:
                    try {
                        handlerGUI.addPlayerBoards(value);
                    }catch (Exception e) {
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Weapon.weapon_key:
                    try {
                        if(isBuilt) handlerGUI.updateWeaponsCards(value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Powerup.powerup_key:
                    try {
                        if(isBuilt) handlerGUI.updatePowerups(value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case AmmoColor.ammoColorKey_blue:
                    try {
                        handlerGUI.updateBlueAmmos(Integer.parseInt(value.get(0)));
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case AmmoColor.ammoColorKey_red:
                    try {
                        handlerGUI.updateRedAmmos(Integer.parseInt(value.get(0)));
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case AmmoColor.ammoColorKey_yellow:
                    try {
                        handlerGUI.updateYellowAmmos(Integer.parseInt(value.get(0)));
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Board.weaponsKey_blue:
                    try {
                        handlerGUI.updateDeckAbove(value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Board.weaponsKey_red:
                    try {
                        handlerGUI.updateDeckLeft(value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Board.weaponsKey_yellow:
                    try {
                        handlerGUI.updateDeckRight(value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case GameMap.ROW_1:
                    try {
                        handlerGUI.updateFirstRow(value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case GameMap.ROW_2:
                    try {
                        handlerGUI.updateSecondRow(value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case GameMap.ROW_3:
                    try {
                        handlerGUI.updateThirdRow(value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Player.playerKey_damages_blue:
                    try {
                        handlerGUI.updateDamages("blue", value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Player.playerKey_damages_yellow:
                    try {
                        handlerGUI.updateDamages("yellow", value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Player.playerKey_damages_green:
                    try {
                        handlerGUI.updateDamages("green", value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Player.playerKey_damages_grey:
                    try {
                        handlerGUI.updateDamages("grey", value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Player.playerKey_damages_purple:
                    try {
                        handlerGUI.updateDamages("purple", value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Player.playerKey_marks_blue:
                    try {
                        handlerGUI.updateMarks("blue", value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Player.playerKey_marks_yellow:
                    try {
                        handlerGUI.updateMarks("yellow", value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Player.playerKey_marks_green:
                    try {
                        handlerGUI.updateMarks("green", value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Player.playerKey_marks_grey:
                    try {
                        handlerGUI.updateMarks("grey", value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                case Player.playerKey_marks_purple:
                    try {
                        handlerGUI.updateMarks("purple", value);
                    }catch (Exception e){
                        logOnException(e.getMessage(), e);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logOnException(e.getMessage(), e);
        }
    }

    @Override
    public void onFailure(String message) {
        //unused
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
        this.client.didChooseSkulls("5");
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
    public void didChooseSpawnPoint(String powerupChosenAsSpawnPoint, String otherPowerup) {
        super.didChooseSpawnPoint(powerupChosenAsSpawnPoint, otherPowerup);
    }

    @Override
    public void newAction() {
            this.onChooseAction(null);
    }

    @Override
    public void onChooseAction(String map) {
        try {
        handlerGUI.chooseAction();
        } catch (Exception e) {System.out.println(e.toString()); System.out.println(e.getMessage());}
    }

    void didChooseAction(String action) {
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
        try {
        Map<String, Map<Integer, Integer>> movements = new HashMap<>();
        for(String move : moves) {
            String s = move.replaceAll("Row: ", "").replaceAll("Column: ", "").replaceAll(";", "");
            List<Integer> x = Arrays.stream(s.split("\\s"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            Map<Integer, Integer> cell = new HashMap<>();
            cell.put(x.get(0), x.get(1));
            movements.put(move, cell);
        }
        handlerGUI.moveOptions(movements);
        }
        catch (Exception e) {
            logOnException(e.getMessage(), e);
        }
    }

    @Override
    public void grabSomething() {
        this.client.askPicks(new ArrayList<>());
    }

    void grabSomethingResponse(boolean response){
        if(response) this.client.powerupSellingToGrabWeapon();
        else {this.client.askPicks(new ArrayList<>());}
    }

    @Override
    public void sellPowerupToGrabWeapon(List<String> powerups) {
        return;
    }

    @Override
    public void willChooseWhatToGrab(List<String> possiblePicks) {
        try {
        Map<String, Map<Integer, Integer>> ammoTiles = new HashMap<>();
        Map<String, String> weapons = new HashMap<>();
        for(String pick : possiblePicks) {
            if(pick.substring(0, 9).equals("Ammo Card")) {
                String s3 = pick.substring(pick.length() - 7, pick.length() - 3);
                s3 = s3.replaceAll(",", "");
                List<Integer> x = Arrays.stream(s3.split("\\s"))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                Map<Integer, Integer> couple = new HashMap<>();
                couple.put(x.get(0), x.get(1));
                ammoTiles.put(pick, couple);
            } else weapons.put(pick, pick.substring(0, pick.length() - 18));
        }
        handlerGUI.pickOptions(ammoTiles, weapons); } catch (Exception e) {
            logOnException(e.getMessage(), e);
        }
    }

    @Override
    public void onGrabSuccess() {
        this.afterAction();
    }

    @Override
    public void onGrabFailure() {
        this.newAction();
    }

    @Override
    public void onGrabFailurePowerup(List<String> powerup) {
        onGrabFailurePowerupToDiscard(powerup.get(r.nextInt(powerup.size())));
    }

    @Override
    public void onGrabFailureWeapon(List<String> weapon) {
        onGrabFailureWeaponToDiscard(weapon.get(r.nextInt(weapon.size())));
    }

    @Override
    protected void shootPeople() {
        this.client.askWeapons();
    }

    @Override
    public void onShootPeopleFailure() {
        this.handlerGUI.turnNormal();
    }

    @Override
    public void willChooseWeapon(List<String> weapons) {
        handlerGUI.chooseWeapon(new ArrayList<>(weapons));
    }

    @Override
    public void onDamageFailure() {
        this.handlerGUI.turnNormal();
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void willChooseDamage(Map<String, String> damagesToChoose) {
        String forPotentiableWeapon = null;
        if(damagesToChoose.containsKey(PotentiableWeapon.forPotentiableWeapon_key)) {
            forPotentiableWeapon = damagesToChoose.get(PotentiableWeapon.forPotentiableWeapon_key);
            damagesToChoose.remove(PotentiableWeapon.forPotentiableWeapon_key);
        }
        String weapon = damagesToChoose.get(Weapon.weapon_key);
        damagesToChoose.remove(Weapon.weapon_key);
        String indexOfEffect = damagesToChoose.get(Effect.effect_key);
        damagesToChoose.remove(Effect.effect_key);
        handlerGUI.chooseDamage(new ArrayList<>(damagesToChoose.values()), indexOfEffect, weapon, forPotentiableWeapon);
    }

    @Override
    public void didUseWeapon(List<String> powerups) {
        this.afterAction();
    }

    @Override
    public void onPowerupInHandFailure() {
        return;
    }

    @Override
    public void willChoosePowerupSelling(Map<String, String> powerups) {
        this.client.useWeaponAfterPowerupAsking(powerups.get(Weapon.weapon_key), new ArrayList<>());
    }

    public void built() {
        this.isBuilt = true;
    }

    @Override
    public void willChooseMode(Map<String, String> modalitiesToChoose) {
        String weapon = modalitiesToChoose.get(Weapon.weapon_key);
        modalitiesToChoose.remove(Weapon.weapon_key);
        if(modalitiesToChoose.isEmpty()) this.onWeaponUsingFailure();
        else handlerGUI.chooseMode(new ArrayList<>(modalitiesToChoose.values()), weapon);
    }

    @Override
    public void willChooseEffects(Map<String, String> effectsToChoose) {
        String weapon = effectsToChoose.get(Weapon.weapon_key);
        effectsToChoose.remove(Weapon.weapon_key);
        handlerGUI.chooseEffect(new ArrayList<>(effectsToChoose.values()), weapon);
    }

    @Override
    public void askPowerupAfterShot(List<String> powerups) {

    }

    @Override
    public void onWeaponUsingFailure() {
        this.onChooseAction(null);
    }

    @Override
    public void onEndTurn(String curSituation) {
        this.handlerGUI.setCounter(0);
        this.client.checkDeaths();
        this.client.turnEnded();
    }

    @Override
    public void askReload() {
        return;
    }

    @Override
    public void willSellPowerupToReload(List<String> powerups) {
        return;
    }

    @Override
    public void onWeaponUnloadedFailure() {
        return;
    }

    @Override
    public void willReload(List<String> weapons) {
        return;
    }

    @Override
    public void onReloadSuccess() {
        return;
    }

    @Override
    public void onReloadFailure() {
        return;
    }

    @Override
    public void onTurnPowerupFailure() {
        return;
    }

    @Override
    public void willChoosePowerup(List<String> availablePowerups) {
        return;
    }

    @Override
    public void willChoosePowerupDamage(Map<String, String> possibleDamages) {
        return;
    }

    @Override
    public void displayChange(String change) {
        return;
    }

    @Override
    public void willSpawnAfterDeath(List<String> powerupsToSpawn) {
        List<String> pows = new ArrayList<>();
        for(int i = 0; i < 2; i++)
            pows.add(powerupsToSpawn.get(i));
        this.handlerGUI.drawTwoPowerups(pows);
    }

    @Override
    public void displayFinalFrenzy() {
        return;
    }

    @Override
    public void endOfTheGame(String scoreboard) {
        return;
    }

    @Override
    public void moveBeforeShot(List<String> movements) {
        return;
    }

    @Override
    public void tagback(List<String> availablePowerup, String nickname) {
        return;
    }

    @Override
    public void awake() {
        synchronized (this) {
            this.notifyAll();
        }
    }

}

