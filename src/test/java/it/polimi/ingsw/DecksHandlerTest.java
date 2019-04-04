package it.polimi.ingsw;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class DecksHandlerTest {

    private ArrayList<Weapon> weapons = new ArrayList<>();
    private ArrayList<AmmoTile> ammoTiles  = new ArrayList<>();
    private ArrayList<Powerup> powerups  = new ArrayList<>();

    @Before
    public void setUp() {
        Random rand = new Random();
        int size = rand.nextInt(20);
        for (int i=0; i < size; i++) {
            WeaponType type = WeaponType.values()[new Random().nextInt(3)];
            weapons.add(new Weapon("TestWeapon"+i, new ArrayList<>(), "TestNotes"+i, type, new ArrayList<>()));
        }

        size = rand.nextInt(20);
        for (int i=0; i < size; i++) {
            ArrayList<AmmoColor> colors = new ArrayList<>();
            int costSize = new Random().nextInt(3);
            for(int j=0; j < costSize; j++) colors.add(AmmoColor.values()[new Random().nextInt(3)]);
            ammoTiles.add(new AmmoTile(colors, new Random().nextBoolean() ? null : new Powerup("name", "description", AmmoColor.values()[new Random().nextInt(3)])));
        }

        size = rand.nextInt(20);
        for (int i=0; i < size; i++) {
            powerups.add(new Powerup("TestPowerup"+i, "TestDescription"+i, AmmoColor.values()[new Random().nextInt(3)]));
        }
    }

    @Test
    public void testSetUp() {
        for(Weapon weapon : weapons) {
            System.out.println("Weapon Name: " + weapon.getName());
            System.out.println("Weapon Notes: " + weapon.getNotes());
            System.out.println("Weapon Type: " + weapon.getType().toString());
        }

        for(Powerup powerup : powerups) {
            System.out.println("Powerup Name: " + powerup.getName());
            System.out.println("Powerup Description: " + powerup.getDescription());
            System.out.println("Powerup Color: " + powerup.getColor().toString());
        }

        for(AmmoTile ammoTile : ammoTiles) {
            System.out.println("AmmoTile Ammos: " + ammoTile.getAmmoColors().toString());
            System.out.println("AmmoTile Powerup: " + ammoTile.hasPowerup().toString());
        }
    }
}
