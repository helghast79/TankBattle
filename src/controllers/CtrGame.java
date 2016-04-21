/**
 * Sample Skeleton for 'viewGame.fxml' Controller Class
 */

package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import java.util.*;

import animations.AnimationUtils;
import animations.Sounds;
import gameobjects.Player;
import gameobjects.Weapon;
import grid.Cell;

import grid.CellTypes;
import grid.MapBuilder;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
    private Polygon[][] tiles_polygon;

    //Player
    private Player player, enemy;


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

    //message linked messageList
    private static List<String> MessageList;
    private static ObservableList<String> ObservableMessageList;
private boolean processingMessage = false; //only process 1 message at a time


    //NODES >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
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
    private Label moveLbl;

    @FXML
    private Label weapon1Lbl;

    @FXML
    private Label weapon2Lbl;

    @FXML
    private Label repairLbl;

    @FXML
    private ToggleButton weapon1Btn;

    @FXML
    private Label weapon1Lbl1;

    @FXML
    private ToggleButton weapon2Btn;

    @FXML
    private ToggleButton repairBtn;

    @FXML
    private ToggleButton moveBtn;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //ON CLICK ===============================================
    @FXML
    void endTurnBtnOnClick(MouseEvent event) {
        ReceiveMessage("1");
        //ReceiveMessage("2");
    }


    @FXML
    void quitBtnOnClick(MouseEvent event) {
        //return to menu
        Navigation.getInstance().loadView("viewInit");
    }


    @FXML
    void moveBtnOnClick(MouseEvent event) {
        //clear rectangle marks
        clearRectangles();

        if (moveBtn.isSelected()) {
            putMarks(player.getTotalMoves(), Settings.MOVE_COLOR);
        } else {
            clearRectangles();
        }
    }

    @FXML
    void repairBtnOnClick(MouseEvent event) {
        //clear rectangle marks
        clearRectangles();

        if (repairBtn.isSelected()) {
            putRectangle(player.getCol(), player.getRow(), Color.BROWN);
        } else {
            clearRectangles();
        }
    }

    @FXML
    void weapon1BtnOnClick(MouseEvent event) {
        clearRectangles();

        if (weapon1Btn.isSelected()) {
            putMarks(Settings.WEAPON_CANNON_RANGE, Settings.WEAPON_CANNON_COLOR);
        } else {
            clearRectangles();
        }


    }

    @FXML
    void weapon2BtnOnClick(MouseEvent event) {
        clearRectangles();

        if (weapon2Btn.isSelected()) {
            putMarks(Settings.WEAPON_MISSILE_RANGE, Settings.WEAPON_MISSILE_COLOR);
        } else {
            clearRectangles();
        }

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


        int row = getPositionRow((Node) event.getTarget());
        int col = getPositionCol((Node) event.getTarget());


        //click in a polygon (only way to validate right click for the tool selected)
        if (event.getTarget().getClass() == Polygon.class) {

            if (((Polygon) (event.getTarget())).getStroke() == Settings.WEAPON_MISSILE_COLOR) {
                fireMissile(player, player.getCol(), player.getRow(), col, row);

            } else if (((Polygon) (event.getTarget())).getStroke() == Settings.WEAPON_CANNON_COLOR) {
                fireCannon(player, player.getCol(), player.getRow(), col, row);


            } else if (((Polygon) (event.getTarget())).getStroke() == Settings.MOVE_COLOR) {
                //move tank
                moveTank(player,player.getCol(), player.getRow(), col, row);

            }


            //clear marks
            clearRectangles();

            //reset btns
            moveBtn.setSelected(false);
            repairBtn.setSelected(false);
            weapon2Btn.setSelected(false);
            weapon1Btn.setSelected(false);

            return;
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

        int row = getPositionRow((Node) event.getTarget());
        int col = getPositionCol((Node) event.getTarget());

        //hover in myTank
        if (event.getTarget() == player.getArmorLbl() || event.getTarget() == player.getImageView()) {
            player.getArmorLbl().setContentDisplay(ContentDisplay.CENTER);
            gridPane.setCursor(Cursor.NONE);
            return;
        } else {
            player.getArmorLbl().setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            gridPane.setCursor(Cursor.DEFAULT);

        }


        changeTankSpriteToFollowCursor(player, col, row);


    }
    // ========================================================


    //initialization :::::::::::::::::::::::::::
    @Override //first method called
    public void initialize(URL location, ResourceBundle resources) {

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

        //init message messageList
        MessageList = new LinkedList<String>();
        ObservableMessageList = FXCollections.observableList(MessageList);
        ObservableMessageList.addListener(new ListChangeListener() {

            @Override
            public void onChanged(ListChangeListener.Change change)  {

                //while(ObservableMessageList.size()>0){
                    //get last message
                    String message = ObservableMessageList.get(ObservableMessageList.size() - 1);

                    processingMessage = true;
                    //process message
                    processMessage(message);

                    //remove last message from list
                    ObservableMessageList.remove(message);
                //}

            }
        });


        //create player
        createPlayer();

        //populate the left toolbar
        loadToolbarSprites();

        //add player to the grid
        //gridPane.add(player.getImageView(), player.getCol(), player.getRow());
        gridPane.add(player.getArmorLbl(), player.getCol(), player.getRow()); //to display armor also

        //Tank animation - idle
        anim_TankWorking_Start(myTank_seqT_Working, player.getImageView());

    }

    //assert FXML objects
    private void initAssertAllNodes() {
        assert borderPane != null : "fx:id=\"borderPane\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert gridPane != null : "fx:id=\"gridPane\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert textAreaDown != null : "fx:id=\"textAreaDown\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert labelUp != null : "fx:id=\"labelUp\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert quitBtn != null : "fx:id=\"quitBtn\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert endTurnBtn != null : "fx:id=\"endTurnBtn\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert moveLbl != null : "fx:id=\"moveLbl\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert weapon1Lbl != null : "fx:id=\"weapon1Lbl\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert repairLbl != null : "fx:id=\"repairLbl\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert weapon1Btn != null : "fx:id=\"weapon1Btn\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert weapon2Lbl != null : "fx:id=\"weapon1Lbl1\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert weapon2Btn != null : "fx:id=\"weapon2Btn\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert repairBtn != null : "fx:id=\"repairBtn\" was not injected: check your FXML file 'viewGame.fxml'.";
        assert moveBtn != null : "fx:id=\"moveBtn\" was not injected: check your FXML file 'viewGame.fxml'.";
    }

    //construct the gridPane with the map from file
    private void createGrid() {
        //only path string needed since MapBuilder class will handle class path in jar properly
        String mapFilePath = Settings.FILES_MAP_FOLDER + "map.txt";
        MapBuilder mapBuilder = new MapBuilder(mapFilePath);

        cells = mapBuilder.getCellsArray();

        tiles = new ImageView[Settings.GRID_ROWS][Settings.GRID_COLS];
        tiles_polygon = new Polygon[Settings.GRID_ROWS][Settings.GRID_COLS];

        for (int row = 0; row < Settings.GRID_ROWS; row++) {
            for (int col = 0; col < Settings.GRID_COLS; col++) {

                gridPane.setPadding(new Insets(0, 0, 0, 0));
                gridPane.setHgap(0);
                gridPane.setVgap(0);


                try {

                    String filePath = Settings.FILES_MAP_FOLDER + cells[row][col].getImagePath();
                    URL fileURL = this.getClass().getResource(filePath);
                    File img = new File(fileURL.getFile());

                    if (img.exists()) {

                        ImageView iv = new ImageView(new Image(new FileInputStream(img), Settings.CELL_WIDTH, Settings.CELL_HEIGTH, false, true));

                        tiles[row][col] = iv;
                        tiles_polygon[row][col] = null;

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
        player.setImageUp(getImage(Settings.FILES_TANK_FOLDER + "tank_mv_up.png"));
        player.setImageDown(getImage(Settings.FILES_TANK_FOLDER + "tank_mv_down.png"));
        player.setImageRight(getImage(Settings.FILES_TANK_FOLDER + "tank_mv_right.png"));
        player.setImageLeft(getImage(Settings.FILES_TANK_FOLDER + "tank_mv_left.png"));
        player.setImageDownRight(getImage(Settings.FILES_TANK_FOLDER + "tank_mv_down_right.png"));
        player.setImageDownLeft(getImage(Settings.FILES_TANK_FOLDER + "tank_mv_left_down.png"));
        player.setImageUpRight(getImage(Settings.FILES_TANK_FOLDER + "tank_mv_right_up.png"));
        player.setImageUpLeft(getImage(Settings.FILES_TANK_FOLDER + "tank_mv_up_left.png"));

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
                w = new Weapon("Missile", 10, 1, 1, Settings.WEAPON_MISSILE_RANGE, 1);
                w.setImageIcon(getImage(Settings.FILES_WEAPONS_FOLDER + "weapon_missile.png"));
                break;
            case CANNON:
                w = new Weapon("Cannon", 20, 1, 0, Settings.WEAPON_CANNON_RANGE, 1);
                w.setImageIcon(getImage(Settings.FILES_WEAPONS_FOLDER + "weapon_shell.png"));
                break;
        }
        return w;
    }

    //load the left toolbar - not needed enymore
    private void loadToolbarSprites() {

        weapon1Lbl.setText(player.getWeapon(0).getName());
        weapon2Lbl.setText(player.getWeapon(1).getName());


        //most properties are initiated in CSS, those that cannot are setArgsPassedByGameSetupController here


        //set current weapon
       /* Weapon w = player.getWeapon(0);
        if (w != null) {
            attackIcon.setImage(w.getImageIcon());
            attackLbl.setText(w.getName());
        }*/
    }
    //::::::::::::::::::::::::::::::::::::::::::


    //Game methods :::::::::::::::::::::::::::
    //fire missile between 2 points
    private void fireMissile(Player anyPlayer, int fromCol, int fromRow, int toCol, int toRow) {
        player.setMovingNow(true);

        //turn tank so it faces the enemy
        changeTankSpriteToFollowCursor(anyPlayer, toCol, toRow);

        anim_TankMissileShoot(fromCol, fromRow, toCol, toRow);
        //Sounds.stopMusic();

    }

    //fire cannon between 2 points
    private void fireCannon(Player anyPlayer, int fromCol, int fromRow, int toCol, int toRow) {
        //turn tank so it faces the enemy
        changeTankSpriteToFollowCursor(anyPlayer, toCol, toRow);


    }

    //Move player to a new position in gridpane
    private synchronized void moveTank(Player anyPlayer, int fromCol, int fromRow, int toCol, int toRow) {

        anyPlayer.setMovingNow(true);
        changeTankSpriteToFollowCursor(anyPlayer, toCol, toRow);

        //save new position in player properties
        anyPlayer.setCol(toCol);
        anyPlayer.setRow(toRow);

        anim_tankMoving(anyPlayer, fromCol, fromRow, toCol, toRow);
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

    //get diference between tank position and node position
    private int getPositionCol(Node node) {

        int nodeCol = getGridNodeCol(node);

        //error
        if (nodeCol < 0) {
            return 0;
        }

        return nodeCol;

    }

    //get diference between tank position and node position
    private int getPositionRow(Node node) {

        int nodeRow = getGridNodeRow(node);

        //error
        if (nodeRow < 0) {
            return 0;
        }

        return nodeRow;

    }

    //choose the right icon to follow cursor position
    private void changeTankSpriteToFollowCursor(Player anyPlayer, int col, int row) {

        String filename;

        int dx = col - anyPlayer.getCol();
        int dy = row - anyPlayer.getRow();


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

        changeIcon(anyPlayer.getImageView(), Settings.FILES_TANK_FOLDER +filename);

    }

    //::::::::::::::::::::::::::::::::::::::::::


    //utils :::::::::::::::::::::::::::
    //open image file and return an image object
    private Image getImage(String fileNameWithExtension) {
        try {

            //String filePath = Settings.FILES_WEAPONS_FOLDER + fileNameWithExtension;
            URL fileURL = this.getClass().getResource(fileNameWithExtension);
            File imgFile = new File(fileURL.getFile());


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
                } else if (node == tiles_polygon[row][col]) {
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
                } else if (node == tiles_polygon[row][col]) {
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
    private void putMarks(int range, Color color) {

        int playerCol = player.getCol();
        int playerRow = player.getRow();


        for (int col = -range; col <= range; col++) {
            for (int row = -range; row <= range; row++) {

                if (col == 0 && row == 0) {
                    continue;
                }
                if (Math.abs(col) != range && Math.abs(row) != range) {
                    continue;
                }
                if (playerCol + col < 0 || playerCol + col > Settings.GRID_COLS - 1) {
                    continue;
                }
                if (playerRow + row < 0 || playerRow + row > Settings.GRID_ROWS - 1) {
                    continue;
                }
                if (cells[playerRow + row][playerCol + col].getType() == CellTypes.MOUNTAIN_A ||
                        cells[playerRow + row][playerCol + col].getType() == CellTypes.MOUNTAIN_B) {
                    continue;
                }

                putRectangle(playerCol + col, playerRow + row, color);

            }
        }

    }

    //add rectangle border to the cell
    private void putRectangle(int col, int row, Color color) {
        //test inser rectangle under the picture
        Polygon border = new Polygon(0, 0, Settings.CELL_WIDTH, 0, Settings.CELL_WIDTH, Settings.CELL_HEIGTH, 0, Settings.CELL_HEIGTH);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(color);
        border.setStrokeWidth(1.0d);
        border.setVisible(true);
        border.setOpacity(0.7f);
        tiles_polygon[row][col] = border;
        gridPane.add(border, col, row);
        //--------------------------------------

    }

    //clear rectangles
    private void clearRectangles() {
        ArrayList<Node> nodes = new ArrayList<Node>();

        for (Node n : gridPane.getChildren()) {

            if (n.getClass() == Polygon.class) {
                nodes.add(n);
            }
        }

        for (int i = 0; i < nodes.size(); i++) {
            gridPane.getChildren().remove(nodes.get(i));
        }


        for (int row = 0; row < Settings.GRID_ROWS; row++) {
            for (int col = 0; col < Settings.GRID_COLS; col++) {
                tiles_polygon[row][col] = null;
            }
        }


    }

    //::::::::::::::::::::::::::::::::::::::::::


    //Animations :::::::::::::::::::::::::::::::
    //move tank animation
    private void anim_tankMoving(Player anyPlayer, int fromCol, int fromRow, int toCol, int toRow) {

        gridPane.getChildren().remove(anyPlayer.getArmorLbl());

        gridPane.add(anyPlayer.getArmorLbl(), toCol, toRow);

        TranslateTransition tt = new TranslateTransition(Duration.millis(Settings.TIME_TANK_MOVE), anyPlayer.getArmorLbl());
        tt.setFromY(-Settings.CELL_HEIGTH * (toRow - fromRow));
        tt.setToY(0d);
        tt.setFromX(-Settings.CELL_WIDTH * (toCol - fromCol));
        tt.setToX(0d);
        tt.setCycleCount(1);
        tt.setAutoReverse(false);

        tt.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                anyPlayer.setMovingNow(false);
                Sounds.stopTankMove();
                if(processingMessage){
                    processingMessage = false;
                }

            }
        });


        //Sounds.stopTankIdle(); //removed tank idle sound
        Sounds.playTankMove();

        tt.play();

    }

    //tank working
    private void anim_TankWorking_Start(SequentialTransition seqT, ImageView iv) {

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

        seqT = new SequentialTransition(iv, tt1, tt2);
        seqT.setCycleCount(Animation.INDEFINITE);
        myTank_seqT_Working = seqT;

        //Sounds.playTankIdle(); //removed tank idle sound
        seqT.play();

    }

    //fire missile
    private void anim_TankMissileShoot(int fromCol, int fromRow, int toCol, int toRow) {


        //calculate translation and rotational parameters based on node origin and destination
        int dx = toCol - fromCol;
        int dy = toRow - fromRow;

        int angleToCell = (int) Math.round(Math.atan2(dy, dx) * (-180) / Math.PI);
        float compensateLength = 0.8f;
        int corrX = 0;
        int corrY = 0;
        float teta = 45 / 2;

        //facing right
        if (angleToCell > -teta && angleToCell <= teta) {
            corrX = 10;
            corrY = -10;

            //facing right up
        } else if (angleToCell > teta && angleToCell <= 90 - teta) {
            corrX = 5;
            corrY = -10;

            //facing up
        } else if (angleToCell > 90 - teta && angleToCell <= 90 + teta) {
            corrX = -10;
            corrY = -10;

            //facing up left
        } else if (angleToCell > 90 + teta && angleToCell <= 180 - teta) {
            corrX = -15;
            corrY = -5;
            compensateLength = 1.0f;

            //facing left
        } else if (angleToCell <= -teta && angleToCell > -90 + teta) {
            corrX = -20;
            corrY = 0;
            compensateLength = 1.0f;

            //facing down
        } else if (angleToCell <= -90 + teta && angleToCell > -90 - teta) {
            corrX = -10;
            corrY = 0;
            compensateLength = 1.0f;

            //facing down left
        } else if (angleToCell <= -90 - teta && angleToCell > -180 + teta) {
            corrX = -10;
            corrY = 0;
            compensateLength = 1.0f;

        }


        //image animation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Image IMAGE = getImage(Settings.FILES_ANIM_FOLDER + "weapon_missile_attack.png");

        ImageView imageView = new ImageView(IMAGE);
        imageView.setViewport(new Rectangle2D(0, 0, 100, 50));
        imageView.fitWidthProperty().setValue(100);
        imageView.fitHeightProperty().setValue(50);
        imageView.setY(0);

        Animation imageAnimation = AnimationUtils.createSpriteAnimation(imageView, 2, 4, 0, 0, 100, 50, Duration.millis(400));
        imageAnimation.setCycleCount(Animation.INDEFINITE);
        imageAnimation.play();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        gridPane.add(imageView, fromCol, fromRow);


        //reposition missile to match tank direction
        TranslateTransition corrPosAnim = AnimationUtils.createTranslateTransition(10, imageView, corrX, corrY, 1);
        RotateTransition corrRotAnim = AnimationUtils.createRotationTransition(10, imageView, -angleToCell, 1);

        //move missile
        int moveX = (int) (Settings.CELL_WIDTH * 0.8 * (toCol - fromCol));
        int moveY = (int) (Settings.CELL_HEIGTH * 0.8 * (toRow - fromRow));
        TranslateTransition moveMissileAnim = AnimationUtils.createTranslateTransition(900, imageView, moveX, moveY, 1);


        ParallelTransition missileAnimation = new ParallelTransition(imageView, moveMissileAnim);


        SequentialTransition finalAnimation = new SequentialTransition(imageView, corrPosAnim, corrRotAnim, missileAnimation);
        finalAnimation.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {


                gridPane.getChildren().remove(imageView);

                //explosion
                anim_explosion(toCol, toRow);

                if(processingMessage){
                    processingMessage = false;
                }

            }
        });
        finalAnimation.setCycleCount(1);

        Sounds.MISSILE_SHOOT.playSound();
        finalAnimation.play();

    }

    //explosion - missile
    private void anim_explosion(int col, int row) {

        //image animation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Image IMAGE = getImage(Settings.FILES_ANIM_FOLDER + "explosion.png");

        ImageView imageView = new ImageView(IMAGE);
        imageView.setViewport(new Rectangle2D(0, 0, 50, 50));
        imageView.fitWidthProperty().setValue(50);
        imageView.fitHeightProperty().setValue(50);
        imageView.setY(0);

        Animation imageAnimation = AnimationUtils.createSpriteAnimation(imageView, 2, 8, 0, 0, 60, 60, Duration.millis(500));
        imageAnimation.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                player.setMovingNow(false);
                gridPane.getChildren().remove(imageView);

            }
        });

        gridPane.add(imageView, col, row);
        imageAnimation.setCycleCount(1);
        Sounds.MISSILE_HIT.playSound();
        imageAnimation.play();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


       /* gridPane.add(imageView, col, row);


        String musicFile = Settings.FILES_RESOURCES_FOLDER + Sounds.MISSILE_HIT.getFilePath();     // For example

        Media media = new Media(new File(musicFile).toURI().toString());
        mediaPlayerFx1 = new MediaPlayer(media);

        mediaPlayerFx1.setOnReady(new Runnable() {
            @Override
            public void run() {
                mediaPlayerFx1.play();
                imageAnimation.play();
            }
        });
*/

    }


    //Messages ::::::::::::::::::::::::::::::::::::::::
    public static void ReceiveMessage(String message){
        ObservableMessageList.add(message);
    }
    private void processMessage(String message){
        //String[] commands = message.split(":");



        if(message.equals("1")){
            moveTank(player, player.getCol(), player.getRow(),player.getCol()+1,player.getRow()-1 );
            //processingMessage is altered at the end of the animation function
        }else if(message.equals("2")){
            fireMissile(player, player.getCol(), player.getRow(),player.getCol()-1,player.getRow()+1 );
            //processingMessage is altered at the end of the animation function
        }


    }
    private void DispatchMessage(String message){

    }
    //// :::::::::::::::::::::::::::::


    //::::::::::::::::::::::::::::::::::::::::::

}