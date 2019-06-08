package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.DecksHandler;
import it.polimi.ingsw.model.board.Cell;
import it.polimi.ingsw.model.board.GameMap;
import it.polimi.ingsw.model.cards.Damage;
import it.polimi.ingsw.model.cards.Powerup;
import it.polimi.ingsw.model.player.Character;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.User;
import it.polimi.ingsw.model.utility.MapType;
import it.polimi.ingsw.model.utility.PlayerColor;
import org.junit.*;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Test for {@link it.polimi.ingsw.model.cards.Newton} class
 *
 * @author Daniele Chiappalupi
 */
public class TeleporterTest {

    private static Powerup teleporter;
    private static GameMap map;
    private static Player p1;
    private static Player p2;
    private static Player p3;
    private static Player p4;
    private static List<Player> players;

    /**
     * Initializes a decks handler where to draw newton, and a map with all the players and the newton powerup.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        players = new ArrayList<>();
        p1 = new Player(new User("username1"), new Character("name1", PlayerColor.green, "description1"));
        p2 = new Player(new User("username2"), new Character("name2", PlayerColor.blue, "description2"));
        p3 = new Player(new User("username3"), new Character("name3", PlayerColor.yellow, "description3"));
        p4 = new Player(new User("username4"), new Character("name4", PlayerColor.grey, "description4"));
        LinkedHashMap<Player, Cell> playersPosition = new LinkedHashMap<>();
        playersPosition.put(p1, null);
        playersPosition.put(p2, null);
        playersPosition.put(p3, null);
        playersPosition.put(p4, null);
        map = new GameMap(MapType.conf_4, playersPosition);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);
        String newtonToString = "Teleporter - Blue";
        DecksHandler decks = new DecksHandler();
        teleporter = decks.drawPowerup();
        while(!teleporter.toString().equalsIgnoreCase(newtonToString)) teleporter = decks.drawPowerup();
    }

    /**
     * Tests the use method of newton.
     *
     * @see Powerup#use(Player, GameMap, List)
     */
    @Test
    public void testUse() {
        map.setPlayerPosition(p1, 1, 0);
        map.setPlayerPosition(p2, 0, 0);
        map.setPlayerPosition(p3, 1, 1);
        map.setPlayerPosition(p4, 0, 0);
        List<Damage> possibleDamages = new ArrayList<>();
        for(int i = 0; i < 12; i++) {
            Damage d = new Damage();
            d.setTarget(p1);
            possibleDamages.add(d);
        }
        possibleDamages.get(0).setPosition(map.getCell(0, 0));
        possibleDamages.get(1).setPosition(map.getCell(0, 1));
        possibleDamages.get(2).setPosition(map.getCell(0, 2));
        possibleDamages.get(3).setPosition(map.getCell(0, 3));
        possibleDamages.get(4).setPosition(map.getCell(1, 0));
        possibleDamages.get(5).setPosition(map.getCell(1, 1));
        possibleDamages.get(6).setPosition(map.getCell(1, 2));
        possibleDamages.get(7).setPosition(map.getCell(1, 3));
        possibleDamages.get(8).setPosition(map.getCell(2, 0));
        possibleDamages.get(9).setPosition(map.getCell(2, 1));
        possibleDamages.get(10).setPosition(map.getCell(2, 2));
        possibleDamages.get(11).setPosition(map.getCell(2, 3));
        possibleDamages.sort(Damage::compareTo);
        assertEquals(possibleDamages, teleporter.use(p1, map, players));

        for(int i = 0; i < 12; i++) {
            possibleDamages.get(i).setTarget(p2);
        }
        possibleDamages.sort(Damage::compareTo);
        assertEquals(possibleDamages, teleporter.use(p2, map, players));

        for(int i = 0; i < 12; i++) {
            possibleDamages.get(i).setTarget(p3);
        }
        possibleDamages.sort(Damage::compareTo);
        assertEquals(possibleDamages, teleporter.use(p3, map, players));

        for(int i = 0; i < 12; i++) {
            possibleDamages.get(i).setTarget(p4);
        }
        possibleDamages.sort(Damage::compareTo);
        assertEquals(possibleDamages, teleporter.use(p4, map, players));
    }

}
