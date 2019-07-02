package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.cards.AmmoTile;
import it.polimi.ingsw.model.cards.Powerup;
import it.polimi.ingsw.model.cards.Weapon;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utility.AmmoColor;
import it.polimi.ingsw.model.utility.CellColor;

import java.util.*;

/**
 * Grabbing controller class.
 * It handles every request and model manipulation that is done during the phase of grabbing something.
 *
 * @author Daniele Chiappalupi
 */
@SuppressWarnings("squid:S3776")
public class GrabController {

    /**
     * Constant String used in exceptions
     */
    private static final String CELL_NOT_INITIALIZED_EXC = "The cell of the grab was not initialized!";
    private static final String WEAPON_NOT_IN_HAND = "This weapon is not in your hand!";
    private static final String NO_CELL_OF_THIS_KIND = "No cell like the one picked can be reached from the player!";

    private Cell grabbingCell;
    private Powerup grabbingPowerup;
    private Weapon grabbingWeapon;
    private List<Powerup> powerupSold;

    GrabController() {
        powerupSold     = new ArrayList<>();
        grabbingCell    = null;
        grabbingWeapon  = null;
    }

    /**
     * This method is used to find the possible picks of a player.
     * @param player it's the player that wants to grab something
     * @param board it's the board of the game
     * @param powerupToSell it's the list of powerups that the player wants to sell
     * @return the list of AmmoTile::toString of possiblePicks. It also adds the possible weapons that he can take from any spawn point where he can arrive.
     */
    public List<String> getPicks(Player player, Board board, List<String> powerupToSell) {
        List<String> possiblePicks = new ArrayList<>();
        List<Cell> possibleMovements = board.getMap().getCellsAtMaxDistance(player, player.getPlayerBoard().getStepsBeforeGrabbing());
        possibleMovements.add(board.getMap().getCellFromPlayer(player));
        List<Cell> spawns = new ArrayList<>();
        List<Powerup> soldPowerup = new ArrayList<>();
        if(!powerupToSell.isEmpty()) {
            List<Powerup> powerupInHand = player.getPlayerHand().getPowerups();
            for(Powerup p : powerupInHand)
                for(String s : powerupToSell)
                    if(p.toString().equals(s))
                        soldPowerup.add(p);
        }
        for(Cell cell : possibleMovements) {
            if(cell.isRespawn()) spawns.add(cell);
            else if(cell.getAmmoCard() != null && powerupToSell.isEmpty()) possiblePicks.add(cell.toStringAmmos());
        }
        for(Cell spawn : spawns) {
            AmmoColor color = AmmoColor.blue;
            if(spawn.getColor() == CellColor.red) color = AmmoColor.red;
            else if(spawn.getColor() == CellColor.yellow) color = AmmoColor.yellow;
            List<Weapon> weaponsInSpawn = board.showWeapons(color);
            for (Weapon spawnWeapon : weaponsInSpawn)
                if(spawnWeapon != null && checkCost(spawnWeapon, soldPowerup, player)) possiblePicks.add(spawnWeapon.getName() + " " + spawn.toStringCondensed());
        }
        powerupSold.clear();
        powerupSold.addAll(soldPowerup);
        return possiblePicks;
    }

    /**
     * Private method that checks if the cost of a weapon can be afforded from the player
     * @param weapon it's the weapon to be checked
     * @param powerupToSell it's the list of powerup that the player want to sell
     * @param player it's the player to be checked
     * @return true if the cost can be afforded, false otherwise
     */
    @SuppressWarnings("Duplicates")
    private boolean checkCost(Weapon weapon, List<Powerup> powerupToSell, Player player) {
        Map<AmmoColor, Integer> totalCost = new EnumMap<>(AmmoColor.class);
        Map<AmmoColor, Integer> powerupAmmos = new EnumMap<>(AmmoColor.class);
        for(AmmoColor color : AmmoColor.values()) {
            totalCost.put(color, 0);
            powerupAmmos.put(color, 0);
        }
        for(Powerup powerup : powerupToSell) {
            AmmoColor color = powerup.getColor();
            powerupAmmos.put(color, powerupAmmos.get(color) + 1);
        }
        List<AmmoColor> weaponCost = weapon.getCost();
        weaponCost.remove(0);
        for(AmmoColor color : weaponCost)
            totalCost.put(color, totalCost.get(color) + 1);
        for(AmmoColor color : AmmoColor.values()) {
            while(powerupAmmos.get(color) > 0 && totalCost.get(color) > 0) {
                powerupAmmos.put(color, powerupAmmos.get(color) - 1);
                totalCost.put(color, totalCost.get(color) - 1);
            }
        }
        for(AmmoColor color : AmmoColor.values())
            if(totalCost.get(color) > player.getPlayerHand().getAmmosAmount(color) || powerupAmmos.get(color) > 0)
                return false;
        return true;
    }

    /**
     * Private method that updates the ammos of a player after he grabbed a weapon
     * @param weapon it's the weapon to be bought
     * @param player it's the player that is buying the weapon
     * @param decks it's the decksHandler of the game
     */
    @SuppressWarnings("Duplicates")
    private void payCost(Weapon weapon, Player player, DecksHandler decks) {
        Map<AmmoColor, Integer> totalCost = new EnumMap<>(AmmoColor.class);
        Map<AmmoColor, Integer> powerupAmmos = new EnumMap<>(AmmoColor.class);
        for(AmmoColor color : AmmoColor.values()) {
            totalCost.put(color, 0);
            powerupAmmos.put(color, 0);
        }
        for(Powerup powerup : powerupSold) {
            AmmoColor color = powerup.getColor();
            powerupAmmos.put(color, powerupAmmos.get(color) + 1);
        }
        List<AmmoColor> weaponCost = weapon.getCost();
        weaponCost.remove(0);
        for(AmmoColor color : weaponCost)
            totalCost.put(color, totalCost.get(color) + 1);
        for(AmmoColor color : AmmoColor.values()) {
            while(powerupAmmos.get(color) > 0 && totalCost.get(color) > 0) {
                powerupAmmos.put(color, powerupAmmos.get(color) - 1);
                totalCost.put(color, totalCost.get(color) - 1);
            }
        }
        for(AmmoColor color : AmmoColor.values()) {
            int newValue = player.getPlayerHand().getAmmosAmount(color) - totalCost.get(color);
            player.getPlayerHand().updateAmmos(color, newValue);
        }
        for(Powerup pow : powerupSold) {
            player.getPlayerHand().wastePowerup(pow);
            decks.wastePowerup(pow);
        }
        powerupSold.clear();
    }

    /**
     * This method is used when the player has decided what does he want to grab
     * @param pick it's the toString of the object that the player is grabbing
     * @param player it's the player that is grabbing
     * @param decks it's the deckHandler of the game
     * @param board it's the board of the game
     * @return a map containing the information about the error (if any error occurred) or the game situation after the grab
     */
    public Map<String, String> didGrab(String pick, Player player, DecksHandler decks, Board board) {
        List<Cell> possibleMovements = board.getMap().getCellsAtMaxDistance(player, player.getPlayerBoard().getStepsBeforeGrabbing());
        possibleMovements.add(board.getMap().getCellFromPlayer(player));
        Cell pickedCell = null;
        Weapon grabbedWeapon = null;
        List<Cell> spawns = new ArrayList<>();
        for(Cell cell : possibleMovements) {
            if(cell.isRespawn()) spawns.add(cell);
            else if(cell.getAmmoCard() != null && pick.equalsIgnoreCase(cell.toStringAmmos()))
                pickedCell = cell;
        }

        if(pickedCell == null) {
            for(Cell cell : spawns) {
                AmmoColor color = AmmoColor.blue;
                if(cell.getColor() == CellColor.red) color = AmmoColor.red;
                else if(cell.getColor() == CellColor.yellow) color = AmmoColor.yellow;
                List<Weapon> weaponsInSpawn = board.showWeapons(color);
                for (Weapon spawnWeapon : weaponsInSpawn) {
                    if(spawnWeapon != null) {
                        String box = spawnWeapon.getName() + " " + cell.toStringCondensed();
                        if(box.equals(pick)) {
                            pickedCell = cell;
                            grabbedWeapon = spawnWeapon;
                        }
                    }
                }
            }
        }

        if(pickedCell == null) throw new IllegalArgumentException(NO_CELL_OF_THIS_KIND);

        grabbingCell = pickedCell;
        grabbingWeapon = grabbedWeapon;

        Map<String, String> checker = grabbingHelper(player, decks, board, pickedCell, grabbedWeapon);
        if (!checker.isEmpty()) return checker;

        board.getMap().setPlayerPosition(player, pickedCell);
        grabbingCell = null;
        grabbingWeapon = null;
        grabbingPowerup = null;

        return checker;
    }

    /**
     * Helper method to reduce cognitive complexity
     * @param player it's the player who is grabbing
     * @param decks it's the decksHandler of the game
     * @param board it's the board of the game
     * @param pickedCell it's the cell where the player is moving to grab something
     * @param grabbedWeapon it's the weapon that the player has grabbed (null if no weapon has been grabbed)
     * @return a map containing information about the success of the grab
     */
    private Map<String, String> grabbingHelper(Player player, DecksHandler decks, Board board, Cell pickedCell, Weapon grabbedWeapon) {
        Map<String, String> returnMap = new HashMap<>();
        if(!pickedCell.isRespawn()) {
            AmmoTile box = pickedCell.getAmmoCard();
            if(box.hasPowerup() && player.getPlayerHand().getPowerups().size() >= 3) {
                List<Powerup> powerupsInHand = player.getPlayerHand().getPowerups();
                grabbingPowerup = decks.drawPowerup();
                for(Powerup p : powerupsInHand) {
                    returnMap.put(Integer.toString(powerupsInHand.indexOf(p)), p.toString());
                }
                returnMap.put(Powerup.powerup_key, grabbingPowerup.toString());
                return returnMap;
            }
            pickedCell.setAmmoCard(null);
            for(AmmoColor color : box.getAmmoColors()) {
                int boxAmmos = player.getPlayerHand().getAmmosAmount(color) + 1;
                if(boxAmmos > 3) boxAmmos = 3;
                player.getPlayerHand().updateAmmos(color, boxAmmos);
            }
            if(box.hasPowerup()) player.getPlayerHand().addPowerup(decks.drawPowerup());
            decks.wasteAmmoTile(box);
        } else {
            List<Weapon> playerWeapons = player.getPlayerHand().getWeapons();
            if(playerWeapons.size() >= 3) {
                List<Weapon> weaponsInHand = player.getPlayerHand().getWeapons();
                for(Weapon w : weaponsInHand) {
                    returnMap.put(Integer.toString(weaponsInHand.indexOf(w)), w.getName());
                }
                String w = returnMap.get(Integer.toString(0));
                returnMap.remove(Integer.toString(0));
                returnMap.put(Weapon.weapon_key, w);
                return returnMap;
            }
            playerWeapons.add(grabbedWeapon);
            player.getPlayerHand().setWeapons(playerWeapons);
            board.pickWeapon(grabbedWeapon);
            payCost(grabbedWeapon, player, decks);
        }
        return returnMap;
    }

    /**
     * Method called when a powerup is being discarded to let space for another one that is being grabbed.
     * @param powerup it's the Powerup::toString of the powerup that is being discarded
     * @param player it's the player that is discarding the powerup
     * @param decks it's the decksHandler of the game
     * @param board it's the board of the game
     */
    public void powerupToDiscard(String powerup, Player player, DecksHandler decks, Board board) {
        List<Powerup> powerupsInHand = player.getPlayerHand().getPowerups();
        Powerup toDiscard = null;
        for(Powerup p : powerupsInHand)
            if(p.toString().equals(powerup))
                toDiscard = p;

        if(grabbingCell == null) throw new NullPointerException(CELL_NOT_INITIALIZED_EXC);
        AmmoTile box = grabbingCell.getAmmoCard();
        grabbingCell.setAmmoCard(null);

        if(toDiscard == null) {
            decks.wastePowerup(grabbingPowerup);
            grabbingPowerup = null;
            grabbingWeapon = null;
            grabbingCell = null;
            powerupSold.clear();
        } else {
            player.getPlayerHand().wastePowerup(toDiscard);
            decks.wastePowerup(toDiscard);
            player.getPlayerHand().addPowerup(grabbingPowerup);
            grabbingPowerup = null;
            grabbingWeapon = null;
            grabbingCell = null;
            powerupSold.clear();
        }

        for(AmmoColor color : box.getAmmoColors()) {
            int boxAmmos = player.getPlayerHand().getAmmosAmount(color) + 1;
            if(boxAmmos > 3) boxAmmos = 3;
            player.getPlayerHand().updateAmmos(color, boxAmmos);
        }
    }

    /**
     * Method called when a powerup is being discarded to let space for another one that is being grabbed.
     * @param weapon it's the Powerup::toString of the powerup that is being discarded
     * @param player it's the player that is discarding the powerup
     * @param board it's the board of the game
     * @param decks it's the decksHandler of the game
     */
    public void weaponToDiscard(String weapon, Player player, Board board, DecksHandler decks) {
        List<Weapon> weaponsInHand = player.getPlayerHand().getWeapons();
        Weapon toDiscard = null;
        for(Weapon w : weaponsInHand)
            if(w.getName().equals(weapon))
                toDiscard = w;

        if(grabbingCell == null) throw new NullPointerException(CELL_NOT_INITIALIZED_EXC);
        if(toDiscard == null) throw new IllegalArgumentException(WEAPON_NOT_IN_HAND);
        board.getMap().setPlayerPosition(player, grabbingCell);

        payCost(grabbingWeapon, player, decks);

        weaponsInHand.remove(toDiscard);
        weaponsInHand.add(grabbingWeapon);
        player.getPlayerHand().setWeapons(weaponsInHand);


        grabbingPowerup = null;
        grabbingWeapon = null;
        grabbingCell = null;
        powerupSold.clear();
    }
}
