/**
 * Sample Skeleton for 'viewGame.fxml' Controller Class
 */

package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import java.util.*;

import gameobjects.Player;
import gameobjects.SpriteAnimation;
import gameobjects.Weapon;
import grid.Cell;

import grid.CellTypes;
import grid.MapBuilder;
import grid.Position;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.util.Duration;
import main.Navigation;
import main.Settings;

public class CtrGame implements Initializable {


    private String playerName;
    private String serverAddress;
    private String portNumber;


    private Cell[][] cells;
    private ImageView[][] tiles;

    private HashMap<Position, Label> myTankMoveOptions;
    private int myTankMoveOptions_row;
    private int myTankMoveOptions_col;


    //Player
    private Player player, opponent;


    //enemy
    private ImageView enemyTank;
    private int enemyTankRow;
    private int enemyTankCol;
    private boolean enemyTankIsMoving = false; //wait until movement is finished


    private SequentialTransition myTank_seqT_Working;
    private SequentialTransition enemyTank_seqT_Working;


    //weapons
    enum weapons {
        CANNON, MISSILE
    }

    int missileMinRange = 2;//inclusive
    int missileMaxRange = 2;//inclusive

    int cannonMinRange = 1;
    int cannonMaxRange = 1;


    //NODES >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private BorderPane borderPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private TextArea textAreaDown;

    @FXML
    private Label labelUp;

    @FXML
    private Button quitBtn;

    @FXML
    private Button endTurnBtn;

    @FXML
    private ImageView armorIcon;

    @FXML
    private Label armorLbl;

    @FXML
    private Label moveLbl;

    @FXML
    private Label attackLbl;

    @FXML
    private ImageView attackIcon;

    @FXML
    private Label armorLbl1;

    @FXML
    private ImageView repairIcon;

    @FXML
    private Label repairLbl;

    @FXML
    private ImageView moveIcon;

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //ON CLICK ===============================================
    @FXML
    void endTurnBtnOnClick(MouseEvent event) {


    }

    @FXML
    void quitBtnOnClick(MouseEvent event) {
        //return to menu
        Navigation.getInstance().loadView("viewInit");
    }

    @FXML
    void attackIconOnClick(MouseEvent event) {
        Weapon w = player.getNextWeapon();

        if (w != null) {
            attackIcon.setImage(w.getImageIcon());
            attackLbl.setText(w.getName());
        }

    }

    @FXML
    void moveIconOnClick(MouseEvent event) {

    }

    @FXML
    void repairIconOnClick(MouseEvent event) {

    }
    // ========================================================


    // GRID ==================================================
    @FXML
    void gridPaneOnClick(MouseEvent event) {

        //still moving tank
        if (player.isMovingNow()) {
            return;
        }

        //click in myTank
        if (event.getTarget() == player.getImageView()) {

            //drawRectangleOnRange(1);
            return;
        }


        int dy = getPositionDy((Node) event.getTarget());
        int dx = getPositionDx((Node) event.getTarget());


        if (dx <= 1 && dx >= -1 && dy <= 1 && dy >= -1) {
            //move the tank
            if (checkIfTankCanMoveToCell((Node) event.getTarget())) {
                moveTank(dy, dx);
            }
        }


    }

    @FXML
    void gridPaneOnEnter(MouseEvent event) {

    }

    @FXML
    void gridPaneOnExit(MouseEvent event) {
    }

    @FXML
    void gridPaneOnMove(MouseEvent event) {

        //still moving tank
        if (player.isMovingNow()) return;


        int dy = getPositionDy((Node) event.getTarget());
        int dx = getPositionDx((Node) event.getTarget());

        //hover in myTank
        if (event.getTarget() == player.getArmorLbl() || event.getTarget() == player.getImageView()) {
            player.getArmorLbl().setContentDisplay(ContentDisplay.CENTER);
            gridPane.setCursor(Cursor.NONE);
            return;
        } else {
            player.getArmorLbl().setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            gridPane.setCursor(Cursor.DEFAULT);

        }


        changeTankSpriteToFollowCursor(dx, dy);


        //check if cell is available for moving
        if (checkIfTankCanMoveToCell((Node) event.getTarget())) {
            gridPane.setCursor(Cursor.CLOSED_HAND);

        } else {
            gridPane.setCursor(Cursor.DEFAULT);
        }


    }
    // ========================================================


    //initialization :::::::::::::::::::::::::::
    @Override //first method called
    public void initialize(URL location, ResourceBundle resources) {

        playerName = "fff";//only during debug because playername is bypassed
        initAssertAllNodes();

        //load map from map.txt and populate the gridpane
        createGrid();

        //------the rest of initialization will be called after the setArgsPassedbyGameSetupController

    }

    //Game setup controller passes this arguments after calling it - runs after initialize method
    public void setArgsPassedByGameSetupController(String playerName, String serverAddress, String portNumber) {

        this.playerName = playerName;
        this.serverAddress = serverAddress;
        this.portNumber = portNumber;

        textAreaDown.appendText(playerName + "\n\r");
        textAreaDown.appendText(serverAddress + "\n\r");
        textAreaDown.appendText(portNumber + "\n\r");

        continueInitialization();
    }

    //Initialization was paused because GameSetup is passing args which are needed for the rest of initialization
    private void continueInitialization() {

        //create player
        createPlayer();

        //poulate the left toolbar
        loadToolbarSprites();

        //add player to the grid
        //gridPane.add(player.getImageView(), player.getCol(), player.getRow());
        gridPane.add(player.getArmorLbl(), player.getCol(), player.getRow()); //to display armor also

        //Tank animation - standby
        anim_TankWorking_Start(myTank_seqT_Working, player.getImageView());

        myTankMoveOptions = new HashMap<Position, Label>();

    }

    //assert FXML objects
    private void initAssertAllNodes() {
        assert borderPane != null : "fx:id=\"borderPane\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert gridPane != null : "fx:id=\"gridPane\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert textAreaDown != null : "fx:id=\"textAreaDown\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert labelUp != null : "fx:id=\"labelUp\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert quitBtn != null : "fx:id=\"quitBtn\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert endTurnBtn != null : "fx:id=\"endTurnBtn\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert armorIcon != null : "fx:id=\"armorIcon\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert armorLbl != null : "fx:id=\"armorLbl\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert moveLbl != null : "fx:id=\"moveLbl\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert attackLbl != null : "fx:id=\"attackLbl\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert attackIcon != null : "fx:id=\"attackIcon\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert armorLbl1 != null : "fx:id=\"armorLbl1\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert repairIcon != null : "fx:id=\"repairIcon\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert repairLbl != null : "fx:id=\"repairLbl\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert moveIcon != null : "fx:id=\"moveIcon\" was not injected: check your FXML file 'viewGame.fxml'.";
    }

    //construct the gridPane with the map from file
    private void createGrid() {

        MapBuilder mapBuilder = new MapBuilder(Settings.FILES_RESOURCES_FOLDER + "map.txt");
        cells = mapBuilder.getCellsArray();

        tiles = new ImageView[Settings.GRID_ROWS][Settings.GRID_COLS];


        for (int row = 0; row < Settings.GRID_ROWS; row++) {
            for (int col = 0; col < Settings.GRID_COLS; col++) {

                gridPane.setPadding(new Insets(0, 0, 0, 0));
                gridPane.setHgap(0);
                gridPane.setVgap(0);


                try {
                    String filePath = Settings.FILES_RESOURCES_FOLDER + cells[row][col].getImagePath();
                    File img = new File(filePath);

                    if (img.exists()) {

                        ImageView iv = new ImageView(new Image(new FileInputStream(img), Settings.CELL_WIDTH, Settings.CELL_HEIGTH, false, true));

                        tiles[row][col] = iv;

                        gridPane.add(iv, col, row);
                    }


                } catch (IOException ie) {
                    System.out.println(ie.getMessage());
                }

            }
        }

        gridPane.setAlignment(Pos.CENTER);

        ColumnConstraints cc = new ColumnConstraints();
        cc.setHalignment(HPos.CENTER);
        cc.setFillWidth(true);
        gridPane.getColumnConstraints().addAll(cc);

        RowConstraints rc = new RowConstraints();
        rc.setValignment(VPos.CENTER);
        rc.setFillHeight(true);
        gridPane.getRowConstraints().addAll(rc);

    }

    //Initialize our player (tank)
    private void createPlayer() {

        player = new Player(this.playerName,
                Settings.TANK_DEFAULT_ARMOR,
                Settings.TANK_DEFAULT_INIT_ROW,
                Settings.TANK_DEFAULT_INIT_COL,
                Settings.TANK_DEFAULT_TOTAL_MOVES);

        //setting images
        player.setImageUp(getImage("tank_mv_up.png"));
        player.setImageDown(getImage("tank_mv_down.png"));
        player.setImageRight(getImage("tank_mv_right.png"));
        player.setImageLeft(getImage("tank_mv_left.png"));
        player.setImageDownRight(getImage("tank_mv_down_right.png"));
        player.setImageDownLeft(getImage("tank_mv_left_down.png"));
        player.setImageUpRight(getImage("tank_mv_right_up.png"));
        player.setImageUpLeft(getImage("tank_mv_up_left.png"));

        //image view
        ImageView imgView = new ImageView();
        imgView.setImage(player.getImageRight()); //initial image
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        imgView.setCache(true);
        imgView.setFitHeight(Settings.TANK_IMAGE_SIZE);
        imgView.setFitWidth(Settings.TANK_IMAGE_SIZE);

        player.setImageView(imgView);

        //add weapons
        player.addWeapon(0, createWeapon(weapons.CANNON));
        player.addWeapon(1, createWeapon(weapons.MISSILE));
        //set current weapon
        player.setCurrentWeaponIndex(0); //cannon

        //armor label
        Label armorLabel = new Label();
        armorLabel.setFont(new Font("Arial", 15));
        armorLabel.setText(player.getArmorString());
        armorLabel.setGraphic(imgView);
        armorLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        armorLabel.setTextFill(Color.WHITE);

        player.setArmorLbl(armorLabel);

    }

    //return a weapon based on the name passed
    private Weapon createWeapon(weapons weapon) {
        Weapon w = null;

        switch (weapon) {
            case MISSILE:
                w = new Weapon("Missile", 20, 1, 1, 5, 2);
                w.setImageIcon(getImage("weapon_missile.png"));
                break;
            case CANNON:
                w = new Weapon("Cannon", 5, 2, 0, 3, 1);
                w.setImageIcon(getImage("weapon_shell.png"));
                break;
        }
        return w;
    }

    //load the left toolbar
    private void loadToolbarSprites() {

        //most properties are initiated in CSS, those that cannot are setArgsPassedByGameSetupController here
        armorIcon.setFitHeight(50);
        armorIcon.setFitWidth(50);

        attackIcon.setFitHeight(50);
        attackIcon.setFitHeight(50);

        //set current weapon
        Weapon w = player.getWeapon(0);
        if (w != null) {
            attackIcon.setImage(w.getImageIcon());
            attackLbl.setText(w.getName());
        }
    }
    //::::::::::::::::::::::::::::::::::::::::::


    //Game methods :::::::::::::::::::::::::::
    //Move player to a new position in gridpane
    private synchronized void moveTank(int rows, int cols) {

        player.setMovingNow(true);

        //gridPane.getChildren().remove(player.getImageView());
        gridPane.getChildren().remove(player.getArmorLbl());
        player.setCol(player.getCol() + cols);
        player.setRow(player.getRow() + rows);

        //gridPane.add(player.getImageView(), player.getCol(), player.getRow());
        gridPane.add(player.getArmorLbl(), player.getCol(), player.getRow());

        TranslateTransition tt = new TranslateTransition(Duration.millis(Settings.TIME_TANK_MOVE), player.getArmorLbl());
        tt.setFromY(-Settings.CELL_HEIGTH * rows);
        tt.setToY(0d);
        tt.setFromX(-Settings.CELL_WIDTH * cols);
        tt.setToX(0d);
        tt.setCycleCount(1);
        tt.setAutoReverse(false);

        tt.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                player.setMovingNow(false);
            }
        });
        tt.play();


    }

    //prior to move check if tank can go to that cell
    private boolean checkIfTankCanMoveToCell(Node node) {
        int row = getGridNodeRow(node);
        int col = getGridNodeCol(node);

        if (row < 0 || col < 0) {
            return false;
        }

        if (cells[row][col].getType() == CellTypes.MOUNTAIN_A ||
                cells[row][col].getType() == CellTypes.MOUNTAIN_B) {
            return false;
        }


        int dy = row - player.getRow();
        int dx = col - player.getCol();


        if (dx > 1 || dx < -1 || dy > 1 || dy < -1) {
            return false;
        }


        return true;
    }

    //the effective draw method for the marks
    private void drawRectangleOnRange(int col, int row) {
/*
        //check if array of marks is not empty
        if (myTankMoveOptions.size() > 0) {

            //don't draw marks if they are already draw
            if (myTankMoveOptions_row == player.getRow() && myTankMoveOptions_col == player.getCol()) {
                return;
            } else {
                clearMarks();
            }

        }


        //save location of myTankOptions tank position
        myTankMoveOptions_col = player.getCol();
        myTankMoveOptions_row = player.getRow();


        //draw marks
        for (int col = -rangeRadious; col < rangeRadious + 1; col++) {
            for (int row = -rangeRadious; row < rangeRadious + 1; row++) {
                if (col == 0 && row == 0) continue;

                //Rectangle mark = new Rectangle(Settings.CELL_WIDTH/2, Settings.CELL_HEIGTH/2, Color.BLACK);
                Label mark = new Label("X");
                mark.setTextFill(Color.WHITE);

                if ((player.getCol() + col < 0 || player.getCol() + col >= Settings.GRID_COLS) ||
                        (player.getRow() + row < 0 || player.getRow() + row >= Settings.GRID_ROWS)) {
                    continue;
                }

                mark.setOpacity(0.9f);

                myTankMoveOptions.put(new Position(player.getRow() + row, player.getCol() + col), mark);

                gridPane.add(mark, player.getCol() + col, player.getRow() + row);

                GridPane.setHalignment(mark, HPos.CENTER);
                GridPane.setValignment(mark, VPos.CENTER);

            }
        }*/


        //save location of myTankOptions tank position
        myTankMoveOptions_col = player.getCol();
        myTankMoveOptions_row = player.getRow();


        //draw marks

        //Rectangle mark = new Rectangle(Settings.CELL_WIDTH/2, Settings.CELL_HEIGTH/2, Color.BLACK);
        Polygon mark = new Polygon(0, 0, Settings.CELL_WIDTH, 0, Settings.CELL_WIDTH, Settings.CELL_HEIGTH, 0, Settings.CELL_HEIGTH);
        //Rectangle2D mark = new Rectangle2D(0,0,Settings.CELL_WIDTH,Settings.CELL_HEIGTH);
        mark.setStroke(Color.BLUE);
        mark.setStrokeWidth(2.0d);
        mark.setVisible(true);
        mark.setOpacity(0.6f);


        gridPane.add(mark, col, row);

        GridPane.setHalignment(mark, HPos.CENTER);
        GridPane.setValignment(mark, VPos.CENTER);


    }

    //get diference between tank position and node position
    private int getPositionDx(Node node) {

        int nodeCol = getGridNodeCol(node);

        //error
        if (nodeCol < 0) {
            return 0;
        }

        return nodeCol - player.getCol();

    }

    //get diference between tank position and node position
    private int getPositionDy(Node node) {

        int nodeRow = getGridNodeRow(node);

        //error
        if (nodeRow < 0) {
            return 0;
        }

        return nodeRow - player.getRow();

    }

    //choose the right icon to follow cursor position
    private void changeTankSpriteToFollowCursor(int dx, int dy) {

        String filename;

        double r = Math.atan2(dy, dx) * (-180) / Math.PI;
        double teta = 45 / 2;

        //facing right
        if (r > -teta && r <= teta) {
            filename = "tank_mv_right.png";

            //facing right up
        } else if (r > teta && r <= 90 - teta) {
            filename = "tank_mv_right_up.png";

            //facing up
        } else if (r > 90 - teta && r <= 90 + teta) {
            filename = "tank_mv_up.png";

            //facing up left
        } else if (r > 90 + teta && r <= 180 - teta) {
            filename = "tank_mv_up_left.png";

            //facing left
        } else if (r <= -teta && r > -90 + teta) {
            filename = "tank_mv_down_right.png";

            //facing down
        } else if (r <= -90 + teta && r > -90 - teta) {
            filename = "tank_mv_down.png";

            //facing down left
        } else if (r <= -90 - teta && r > -180 + teta) {
            filename = "tank_mv_left_down.png";

        } else {
            filename = "tank_mv_left.png";
        }

        changeIcon(player.getImageView(), filename);

    }

    private boolean checkIfWeaponCanShootToCell(Node node) {

        //row and col of the node we're checking
        int nodeRow = getGridNodeRow(node);
        int nodeCol = getGridNodeCol(node);

        int dx = Math.abs(nodeCol - player.getCol());
        int dy = Math.abs(nodeRow - player.getRow());

        //mountain's cannot be shot
        if (!checkIfTankCanMoveToCell(node)) return false;
//Todo:finish this

        return true;
    }

    //::::::::::::::::::::::::::::::::::::::::::


    //utils :::::::::::::::::::::::::::
    //open image file and return an image object
    private Image getImage(String fileNameWithExtension) {
        try {
            String filePath = Settings.FILES_RESOURCES_FOLDER + fileNameWithExtension;
            File imgFile = new File(filePath);

            if (imgFile.exists()) {
                return new Image(new FileInputStream(imgFile));
            }


        } catch (IOException ie) {
            System.out.println(ie.getMessage());
        }

        return null;

    }

    //return the gridPane column where a passed object node is
    private int getGridNodeCol(Node node) {

        //check if its a background
        for (int row = 0; row < Settings.GRID_ROWS; row++) {
            for (int col = 0; col < Settings.GRID_COLS; col++) {
                if (node == tiles[row][col]) {
                    return col;
                }
            }
        }


        return -1;
    }

    //return the gridPane row where a passed object node is
    private int getGridNodeRow(Node node) {

        for (int row = 0; row < Settings.GRID_ROWS; row++) {
            for (int col = 0; col < Settings.GRID_COLS; col++) {
                if (node == tiles[row][col]) {
                    return row;
                }

            }
        }


        return -1;
    }

    //change icon of the player or enemy
    private void changeIcon(ImageView imageView, String imageFileName) {

        imageView.setImage(getImage(imageFileName));
    }

    //delete the marks that indicate possible moves
    private void clearMarks() {

        //clear previous mark
        for (Label l : myTankMoveOptions.values()) {
            gridPane.getChildren().remove(l);
        }

        myTankMoveOptions.clear();

    }


    //::::::::::::::::::::::::::::::::::::::::::


    //Animations :::::::::::::::::::::::::::::::
    private void anim_TankWorking_Start(SequentialTransition seqT, ImageView iv) {

        //PauseTransition pt = new PauseTransition(Duration.millis(200));

        TranslateTransition tt1 = new TranslateTransition(Duration.millis(100));
        tt1.setFromX(0d);
        tt1.setToX(1d);
        tt1.setCycleCount(4);
        tt1.setAutoReverse(true);

        TranslateTransition tt2 = new TranslateTransition(Duration.millis(300));
        tt2.setFromX(0d);
        tt2.setToX(1d);
        tt2.setCycleCount(1);
        tt2.setAutoReverse(true);


       /* TranslateTransition tt3 = new TranslateTransition(Duration.millis(400));
        tt3.setFromX(0d);
        tt3.setToX(1d);
        tt3.setCycleCount(1);
        tt3.setAutoReverse(true);*/

       /* RotateTransition rt = new RotateTransition(Duration.millis(1300));
        rt.setFromAngle(-0.2d);
        rt.setToAngle(0.2d);
        rt.setCycleCount(1);
        rt.setAutoReverse(true);*/

        /*ScaleTransition st = new ScaleTransition(Duration.millis(700));
        st.setByX(0.01d);
        st.setByY(0.01d);
        st.setCycleCount(1);
        st.setAutoReverse(true);*/

        seqT = new SequentialTransition(iv, tt1, tt2);
        seqT.setCycleCount(Animation.INDEFINITE);
        myTank_seqT_Working = seqT;
        seqT.play();

    }

    private void anim_TankShoot() {

        Image IMAGE = getImage("weapon_missile_attack_start.png");

        int COLUMNS = 2;
        int COUNT = 6;
        int OFFSET_X = 0;
        int OFFSET_Y = 0;
        int WIDTH = 100;
        int HEIGHT = 50;


        final ImageView imageView = new ImageView(IMAGE);
        imageView.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, WIDTH, HEIGHT));
        imageView.fitWidthProperty().setValue(100);
        imageView.fitHeightProperty().setValue(50);
        imageView.setY(0);

        final Animation animation = new SpriteAnimation(
                imageView,
                Duration.millis(1000),
                COUNT, COLUMNS,
                OFFSET_X, OFFSET_Y,
                WIDTH, HEIGHT
        );
        animation.setCycleCount(Animation.INDEFINITE);

        animation.play();

        gridPane.add(imageView, player.getCol(), player.getRow());
    }

    private void rotateNode(Node node, double fromAngle, double toAngle, int time, int cycles) {
        RotateTransition rt = new RotateTransition(Duration.millis(time), node);
        rt.setFromAngle(fromAngle);
        rt.setToAngle(toAngle);
        rt.setCycleCount(cycles);

        rt.play();

    }

    private void translateeNode(Node node, double fromX, double toX, double fromY, double toY, int time, int cycles) {
        TranslateTransition tt1 = new TranslateTransition(Duration.millis(time), node);
        tt1.setFromX(fromX);
        tt1.setToX(toX);
        tt1.setFromX(fromY);
        tt1.setToX(toY);
        tt1.setCycleCount(cycles);
        tt1.setAutoReverse(false);

        tt1.play();
    }
    //::::::::::::::::::::::::::::::::::::::::::

}
