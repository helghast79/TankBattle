/**
 * Sample Skeleton for 'viewInit.fxml' Controller Class
 */

package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import main.Navigation;
import main.Settings;

public class CtrInit implements Initializable {

    private ImageView myTank;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;


    //NODES >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @FXML
    private Pane pane;
    @FXML
    private Label infoLbl;
    @FXML
    private Label newGameLbl;
    @FXML
    private Label continueGameLbl;
    @FXML
    private Label exitLbl;
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>




    //ON ENTER ***********************************************
    @FXML
    void newGameLblOnEnter(MouseEvent event) {
        hoverNode(newGameLbl);
    }
    @FXML
    void continueGameLblOnEnter(MouseEvent event) {

        if (Navigation.getInstance().viewExists("viewGame")) {
           continueGameLbl.setDisable(false);
            hoverNode(continueGameLbl);
        }else{
            continueGameLbl.setDisable(true);
        }


    }
    @FXML
    void exitLblOnEnter(MouseEvent event) {
        hoverNode(exitLbl);
    }
    //********************************************************


    //ON EXIT -------------------------------------------------------
    @FXML
    void newGameLblExit(MouseEvent event) {
        deHoverNode(newGameLbl);
    }
    @FXML
    void continueGameLblOnExit(MouseEvent event) {
        deHoverNode(continueGameLbl);
    }
    @FXML
    void exitLblOnExit(MouseEvent event) {
        deHoverNode(exitLbl);
    }
    //---------------------------------------------------------------



    //ON MOVE $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
    @FXML
    void paneOnMove(MouseEvent event) {

        if (Navigation.getInstance().viewExists("viewGame")) {
           continueGameLbl.setDisable(false);
        }else{
            continueGameLbl.setDisable(true);
        }
    }
    //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$



    //ON CLICK ===============================================
    @FXML
    void paneOnClick(MouseEvent event) {

    }
    @FXML
    void newGameLblOnClick(MouseEvent event) {

        infoLbl.setVisible(true);
        //infoLbl.setText("Creating new game. Waiting for opponents to join in...");

        //check if view already exists and show it (to keep old values)
        if (!Navigation.getInstance().viewExists("viewGameSetup")) {
            Navigation.getInstance().saveView("viewGameSetup", "CssGameSetup");
        }
        Navigation.getInstance().loadView("viewGameSetup");

    }
    @FXML
    void continueGameLblOnClick(MouseEvent event) {

        Navigation.getInstance().loadView("viewGame");
    }
    @FXML
    void exitLblOnClick(MouseEvent event) {

        exitApplication(Navigation.getInstance().getStage());

    }
    // ========================================================








    @Override
    public void initialize(URL location, ResourceBundle resources) {

        assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'viewInit.fxml'.";
        assert infoLbl != null : "fx:id=\"infoLbl\" was not injected: check your FXML file 'viewInit.fxml'.";
        assert continueGameLbl != null : "fx:id=\"continueGameLbl\" was not injected: check your FXML file 'viewInit.fxml'.";
        assert newGameLbl != null : "fx:id=\"newGameLbl\" was not injected: check your FXML file 'viewInit.fxml'.";
        assert exitLbl != null : "fx:id=\"exitLbl\" was not injected: check your FXML file 'viewInit.fxml'.";

        infoLbl.setVisible(false);
        continueGameLbl.setDisable(true);

        loadTank();
    }





    private void hoverNode(Node node) {


        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setRadius(20d);

        node.setEffect(shadow);
        ((Label) (node)).setTextFill(Color.RED);


        ScaleTransition scaleTransition =
                new ScaleTransition(Duration.millis(200), node);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(Settings.SCALE_FONT);
        scaleTransition.setToY(Settings.SCALE_FONT);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();

    }

    private void deHoverNode(Node node) {
        node.setEffect(null);

        ((Label) (node)).setTextFill(Color.WHITE);

        ScaleTransition scaleTransition =
                new ScaleTransition(Duration.millis(200), node);
        scaleTransition.setFromX(Settings.SCALE_FONT);
        scaleTransition.setFromY(Settings.SCALE_FONT);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }


    private void exitApplication(Stage primaryStage) {


        //hide primaryStage
        primaryStage.setOpacity(0.4d);
        //primaryStage.hide();

        //Stage init
        final Stage dialog = new Stage();

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.centerOnScreen();
        dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                dialog.close();
                //primaryStage.show();
                primaryStage.setOpacity(1.0d);
                event.consume();
            }
        });

        //size and location
        dialog.setWidth(200);
        dialog.setHeight(200);
        dialog.setX(primaryStage.getX() + primaryStage.getWidth() / 2 - dialog.getWidth() / 2);
        dialog.setY(primaryStage.getY() + primaryStage.getHeight() / 2 - dialog.getHeight() / 2);


        // confirm - Label
        Label label = new Label("Do you really want to quit?");

        // YES
        Button okBtn = new Button("Yes");
        okBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
                primaryStage.close();
                event.consume();
            }
        });

        // NO
        Button cancelBtn = new Button("No");
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialog.close();
                //primaryStage.show();
                primaryStage.setOpacity(1.0d);
                event.consume();
            }
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        grid.add(label, 0, 0, 3, 1);
        grid.add(okBtn, 0, 2, 1, 1);
        grid.add(cancelBtn, 2, 2, 1, 1);
        grid.setVgap(20);
        grid.setHgap(40);

        dialog.setScene(new Scene(grid));

        dialog.show();
    }

    private void loadTank(){
        try {
            String filePath = Settings.FILES_RESOURCES_FOLDER + "tank.gif";
            File img = new File(filePath);
            if (img.exists()) {


                myTank = new ImageView(new Image(new FileInputStream(img)));
                myTank.setPreserveRatio(true);
                myTank.setSmooth(true);
                myTank.setCache(true);

                myTank.setFitHeight(Settings.TANK_IMAGE_SIZE);
                myTank.setFitWidth(Settings.TANK_IMAGE_SIZE);

                myTank.setX(0);
                myTank.setY(560);

                pane.getChildren().add(myTank);

            }


        } catch (IOException ie) {
            System.out.println(ie.getMessage());
        }


        TranslateTransition tt = new TranslateTransition(Duration.millis(12000), myTank);
        tt.setFromX(-150);
        tt.setToX(950);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setAutoReverse(false);


        tt.play();


    }

}








