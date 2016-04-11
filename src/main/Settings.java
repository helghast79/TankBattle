package main;

import javafx.scene.paint.Color;

/**
 * Created by macha on 20/03/2016.
 */
public class Settings {


    //JavaFx Scenes
    public static final int SCENE_WIDTH = 800; //pixel size of main canvas
    public static final int SCENE_HEIGHT = 600; //pixel size of main canvas

    //Player
    public static final int PLAYER_HEALTH = 100;

    //Files
    public static final String FILES_FXML_FOLDER = "/views/";
    public static final String FILES_CSS_FOLDER = "";    //empty because CSS files are in root
    public static final String FILES_RESOURCES_FOLDER = "resources/";


    //Grid
    public static final int GRID_ROWS = 10;
    public static final int GRID_COLS = 12;


    //Animations
    public static final double CELL_WIDTH=60; //for the animation
    public static final double CELL_HEIGTH=47; //for the animation
    public static final int TIME_TANK_MOVE = 2000; //time in millis to move tank

    //Player
    public static final int TANK_IMAGE_SIZE = 65;
    public static final int TANK_DEFAULT_ARMOR = 100;
    public static final int TANK_DEFAULT_INIT_ROW = 9;
    public static final int TANK_DEFAULT_INIT_COL = 0;
    public static final int TANK_DEFAULT_TOTAL_MOVES = 1;

    //weapons
    public static final int WEAPON_MISSILE_RANGE = 2;
    public static final int WEAPON_CANNON_RANGE = 1;
    public static final Color WEAPON_MISSILE_COLOR = Color.YELLOW;
    public static final Color WEAPON_CANNON_COLOR = Color.RED;
    public static final Color MOVE_COLOR = Color.BLUE;

    //Miscellaneous
    public static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    public static final double START_DISABLE_OPACITY = 0.5d;
    public static final double SCALE_FONT = 1.1d;
}
