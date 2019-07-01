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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Cell grabbingCell;
    private Powerup grabbingPowerup;
    private Weapon grabbingWeapon;

    /**
     * This method is used to find the possible picks of a player.
     * @param player it's the player that wants to grab something
     * @param board it's the board of the game
     * @return the list of AmmoTile::toString of possiblePicks. It also adds the possible weapons that he can take from any spawn point where he can arrive.
     */
    public List<String> getPicks(Player player, Board board) {
        List<String> possiblePicks = new ArrayList<>();
        List<Cell> possibleMovements = board.getMap().getCellsAtMaxDistance(player, player.getPlayerBoard().getStepsBeforeGrabbing());
        List<Cell> spawns = new ArrayList<>();
        for(Cell cell : possibleMovements) {
            if(cell.isRespawn()) spawns.add(cell);
            else if(cell.getAmmoCard() != null) possiblePicks.add(cell.toStringAmmos());
        }
        for(Cell spawn : spawns) {
            AmmoColor color = AmmoColor.blue;
            if(spawn.getColor() == CellColor.red) color = AmmoColor.red;
            else if(spawn.getColor() == CellColor.yellow) color = AmmoColor.yellow;
            List<Weapon> weaponsInSpawn = board.showWeapons(color);
        for (Weapon spawnWeapon : weaponsInSpawn)
            possiblePicks.add(spawnWeapon.getName() + spawn.toStringCondensed());
        }
        return possiblePicks;
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
                    String box = spawnWeapon.getName() + cell.toStringCondensed();
                    if(box.equals(pick)) {
                        pickedCell = cell;
                        grabbedWeapon = spawnWeapon;
                    }
                }
            }
        }

        if(pickedCell == null) throw new IllegalArgumentException("No cell like the one picked can be reached from the player!");

        grabbingCell = pickedCell;
        grabbingWeapon = grabbedWeapon;

        Map<String, String> checker = grabbingHelper(player, decks, board, pickedCell, grabbedWeapon);
        if (!checker.isEmpty()) return checker;

        board.getMap().setPlayerPosition(player, pickedCell);
        grabbingCell = null;
        grabbingWeapon = null;
        grabbingPowerup = null;

        checker.put(GameMap.gameMap_key, board.toStringFromPlayer(player));
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
            board.pickWeapon(grabbedWeapon);
            List<AmmoColor> weaponCost = new ArrayList<>();
            if(grabbedWeapon != null) weaponCost = grabbedWeapon.getCost();
            weaponCost.remove(0);
            for(AmmoColor color : weaponCost) {
                int boxAmmos = player.getPlayerHand().getAmmosAmount(color) - 1;
                player.getPlayerHand().updateAmmos(color, boxAmmos);
            }
        }
        return returnMap;
    }

    /**
     * Method called when a powerup is being discarded to let space for another one that is being grabbed.
     * @param powerup it's the Powerup::toString of the powerup that is being discarded
     * @param player it's the player that is discarding the powerup
     * @param decks it's the decksHandler of the game
     * @param board it's the board of the game
     * @return the current situation of the board in the form of String
     */
    public String powerupToDiscard(String powerup, Player player, DecksHandler decks, Board board) {
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
        } else {
            player.getPlayerHand().wastePowerup(toDiscard);
            decks.wastePowerup(toDiscard);
            player.getPlayerHand().addPowerup(grabbingPowerup);
            grabbingPowerup = null;
            grabbingWeapon = null;
            grabbingCell = null;
        }

        for(AmmoColor color : box.getAmmoColors()) {
            int boxAmmos = player.getPlayerHand().getAmmosAmount(color) + 1;
            if(boxAmmos > 3) boxAmmos = 3;
            player.getPlayerHand().updateAmmos(color, boxAmmos);
        }

        return board.toStringFromPlayer(player);
    }

    /**
     * Method called when a powerup is being discarded to let space for another one that is being grabbed.
     * @param weapon it's the Powerup::toString of the powerup that is being discarded
     * @param player it's the player that is discarding the powerup
     * @param board it's the board of the game
     * @return the current situation of the board in the form of String
     */
    public String weaponToDiscard(String weapon, Player player, Board board) {
        List<Weapon> weaponsInHand = player.getPlayerHand().getWeapons();
        Weapon toDiscard = null;
        for(Weapon w : weaponsInHand)
            if(w.getName().equals(weapon))
                toDiscard = w;

        if(grabbingCell == null) throw new NullPointerException(CELL_NOT_INITIALIZED_EXC);
        if(toDiscard == null) throw new IllegalArgumentException(WEAPON_NOT_IN_HAND);
        board.getMap().setPlayerPosition(player, grabbingCell);

        weaponsInHand.remove(toDiscard);
        weaponsInHand.add(grabbingWeapon);
        player.getPlayerHand().setWeapons(weaponsInHand);


        grabbingPowerup = null;
        grabbingWeapon = null;
        grabbingCell = null;

        return board.toStringFromPlayer(player);
    }
}
