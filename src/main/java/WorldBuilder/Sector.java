package WorldBuilder;

import utils.Dice;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Class that has the utilities to build Sectors that are 10X10. Also holds the information of the sector.
 *
 * @author Chase
 * @version 1.0
 */
public class Sector implements GalaxyDataBaseItem {
    /**SQL information*/
    public static final String tableName = "Sectors";
    public static final String[] keys = {"Name","X","Y","Z","Systems","ID"};
    private String id;

    /** Gird that holds all the Systems in the Sector*/
    private StarSystem[][] grid;
    /**Amount of systems in the sector*/
    private int amtSystems;
    /** String that holds the name**/
    private String name;
    /** Position of the sector based on the origin of (0,0,0) **/
    private int x;
    private int y;
    private int z;
    /**Shows if sector is the origin or main sector*/
    boolean origin;
    public enum Population {POPULATED, COLONIES, RESEARCH, NONE}


    /**
     * Empty Constructor for use with the database creation only
     */
    public Sector(){

    }

    /**
     * constructor for the sector, takes an input on how the sector is populated and then builds a random amount of Star Systems
     * max 10 in a 10X10 sector.
     * @param population enum of population
     * @param x int x location
     * @param y int y location
     * @param z int z location
     */
    public Sector(String name, Population population,int x, int y, int z){
        this.name = name;
        // sets location data
        this.x = x;
        this.y = y;
        this.z = z;
        this.origin = x == 0 && y == 0 && z == 0;
        // creates blank grid
        grid = new StarSystem[10][10];
        // randomize amount of systems
        amtSystems = Dice.Roller(1,10)+20;
        // loop to create systems and place into the sector
        for (int i = 0; i < amtSystems; i++) {
            // get random sector location
            int row = Dice.Roller(1,10)-1;
            int col = Dice.Roller(1,10)-1;
            while (true) {
                //check if free
                if (grid[row][col] == null) {
                    grid[row][col] = new StarSystem(name,population,col,row,x,y,z);
                    break; // place and leave while loop
                } else {
                    // gets now locations to check
                    row = Dice.Roller(1,10) - 1;
                    col = Dice.Roller(1,10) - 1;
                }
            }
        }
        id = "Sec"+x+y+z;
    }

    /**
     * Getter that returns the Sector's name.
     * @return String of the Sector's name
     */
    public String getName() {
        return name;
    }

    @Override
    public void readSQL(String[] values) {
        name = values[0];
        x = Integer.parseInt(values[1]);
        y = Integer.parseInt(values[2]);
        z = Integer.parseInt(values[3]);
        amtSystems = Integer.parseInt(values[4]);
        id = values[4];
    }

    /**
     * Getter that returns the grid array that holds all the Sector's Systems
     * @return StarSystem[][] grid
     */
    public StarSystem[][] getGrid() {
        return grid;
    }

    /**
     * Getter that returns the coordinates of the Sector
     * @return int[] x,y,z
     */
    public int[] getCoordinates(){
        return new int[]{x,y,z};
    }

    /**Overridden methods for the interface GalaxyDataBaseItem.*/
    @Override
    public String getTableNames() {
        return tableName;
    }
    @Override
    public String[] getKeys() {
        return keys;
    }
    @Override
    public String getSQLInsert() {
        return " INSERT INTO Sectors" + "(Name,X,Y,Z,Systems,ID)" +
                "VALUES ('" + name + "','" + x + "','" + y + "','" + z + "','" + amtSystems + "','" + id + "');";
    }
}
