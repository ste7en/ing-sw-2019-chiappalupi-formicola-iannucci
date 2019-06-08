package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.model.cards.Powerup;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.networking.Client;
import it.polimi.ingsw.networking.utility.CommunicationMessage;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ClientRMI extends Client implements ClientInterface {

    private Registry registry;
    private ServerInterface server;
    private boolean flag;

    public ClientRMI(String host, Integer port){
        super(host, port);
        flag = false;
    }

    private static String CLIENT_RMI_EXCEPTION = "ClientRMI exception: ";

    @Override
    protected void setupConnection() {

        try{
            registry = LocateRegistry.getRegistry(connectionPort);
            System.out.println(registry);
            server = (ServerInterface) registry.lookup("rmiInterface");
            try {
                exportClient();
            } catch (RemoteException e) {
                System.err.println("ClientRMI exception: " + e.toString());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
            e.printStackTrace();
        }
    }

    public void exportClient() throws RemoteException{
        if (flag == false) {
            ClientInterface stub = (ClientInterface) UnicastRemoteObject.exportObject(this, 0);
            registry.rebind("ClientInterface", stub);
            flag = true;
        }
    }

    @Override
    public void createUser(String username){
        try{
            if(!server.createUserRMIHelper(username)) {
                viewObserver.onLoginFailure();
            }
            else viewObserver.onLoginSuccess(username);
        } catch (RemoteException e){
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void joinWaitingRoom(String username) {
        try {
            server.joinWaitingRoom(username);
        } catch (RemoteException e){
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void askForCharacters(){
        ArrayList<String> availableCharacters = new ArrayList<>();
        try {
            availableCharacters = server.getAvailableCharacters(gameID);
        } catch (RemoteException e) {
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
            e.printStackTrace();
        }
        viewObserver.willChooseCharacter(availableCharacters);
    }

    @Override
    public void choseCharacter(String character){
        try {
            server.choseCharacter(userID, gameID, character);
        } catch (RemoteException e) {
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
            e.printStackTrace();
        }
        viewObserver.willChooseGameMap();
    }

    @Override
    public void choseGameMap(String configuration){
        try {
            server.choseGameMap(gameID, configuration);
        } catch (RemoteException e) {
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
            e.printStackTrace();
        }
        viewObserver.willChooseSpawnPoint();
    }

    @Override
    public void askForPowerups(){
        ArrayList<String> powerups = new ArrayList<>();
        try {
            powerups = server.getPowerups(userID, gameID);
        } catch (RemoteException e) {
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
            e.printStackTrace();
        }
        viewObserver.onChooseSpawnPoint(powerups);
    }

    @Override
    public void choseSpawnPoint(String powerup){
        try {
            server.choseSpawnPoint(userID, gameID, powerup);
        } catch (RemoteException e) {
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void useWeapon(String weaponSelected) {
        try{
            Map<String, String> weaponUsingProcess = this.server.useWeapon(userID, gameID, weaponSelected);
            switch(CommunicationMessage.valueOf(weaponUsingProcess.get(CommunicationMessage.communication_message_key))) {
                case DAMAGE_LIST: {
                    weaponUsingProcess.remove(CommunicationMessage.communication_message_key);
                    this.viewObserver.willChooseDamage(weaponUsingProcess);
                    break;
                }
                case MODES_LIST: {
                    weaponUsingProcess.remove(CommunicationMessage.communication_message_key);
                    this.viewObserver.willChooseMode(weaponUsingProcess);
                    break;
                }
                case EFFECTS_LIST: {
                    weaponUsingProcess.remove(CommunicationMessage.communication_message_key);
                    this.viewObserver.willChooseEffects(weaponUsingProcess);
                    break;
                }
            }
        } catch (RemoteException e){
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void makeDamage(String weapon, String damage, String indexOfEffect, String forPotentiableWeapon) {
        try{
            this.server.makeDamage(userID, forPotentiableWeapon, indexOfEffect, gameID, damage, weapon);
        } catch (RemoteException e){
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void useMode(String weapon, String effect) {
        try {
            Map<String, String> damageList = this.server.useEffect(userID, gameID, null, effect, weapon);
            this.viewObserver.willChooseDamage(damageList);
        } catch (RemoteException e) {
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void useEffect(String weapon, List<String> effectsToUse) {
        try {
            HashMap<String, String> args = new HashMap<>();
            args.put(Weapon.weapon_key, weapon);
            while (!effectsToUse.isEmpty()) {
                boolean forPotentiableWeapon;
                forPotentiableWeapon = effectsToUse.size() == 1;
                String potentiableBoolean = Boolean.toString(forPotentiableWeapon);
                String effect = effectsToUse.get(0);
                Map<String, String> damageList = this.server.useEffect(userID, gameID, potentiableBoolean, effect, weapon);
                this.viewObserver.willChooseDamage(damageList);
                effectsToUse.remove(0);
            }
        } catch (RemoteException e) {
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void askWeaponToReload() {
        try {
            this.viewObserver.willReload(this.server.getWeaponInHand(userID, gameID));
        } catch (RemoteException e) {
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void reloadWeapons(List<String> weaponsToReload) {
        try {
            boolean didReload =  this.server.reload(weaponsToReload, userID, gameID);
            if(didReload) this.viewObserver.onReloadSuccess();
            else this.viewObserver.onReloadFailure();
        } catch (RemoteException e) {
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void askForPowerup() {
        try {
            List<String> powerups = this.server.getUsablePowerups(userID, gameID);
            this.viewObserver.willChoosePowerup(powerups);
        } catch (RemoteException e) {
            System.err.println(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void askPowerupDamages(String powerup) {
        try {
            List<String> possibleDamages = this.server.getPowerupDamages(userID, gameID, powerup);
            Map<String, String> damagesMap = new HashMap<>();
            for(String damage : possibleDamages) damagesMap.put(Integer.toString(possibleDamages.indexOf(damage)), damage);
            damagesMap.put(Powerup.powerup_key, powerup);
            this.viewObserver.willChoosePowerupDamage(damagesMap);
        } catch (RemoteException e) {
            System.err.print(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void usePowerup(String powerup, String damage) {
        try {
            server.applyPowerupDamage(userID, gameID, powerup, damage);
        } catch (RemoteException e) {
            System.err.print(CLIENT_RMI_EXCEPTION + e.toString());
        }
    }

    @Override
    public void gameStarted(String gameID){
        this.viewObserver.onStart(UUID.fromString(gameID));
        this.gameID = UUID.fromString(gameID);
    }
}

