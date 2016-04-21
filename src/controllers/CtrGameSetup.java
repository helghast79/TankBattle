package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import main.Navigation;
import main.Settings;

public class CtrGameSetup implements Initializable {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;


    //NODES >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    @FXML
    private Pane pane;
    @FXML
    private TextField serverAddressTxt;
    @FXML
    private Label serverAddressLbl;
    @FXML
    private Label newGameLbl;
    @FXML
    private TextField playerNameTxt;
    @FXML
    private Label playerNameLbl;
    @FXML
    private TextField serverPortTxt;
    @FXML
    private Label serverPortLbl;
    @FXML
    private Label infoLbl;
    @FXML
    private Label backLbl;
    @FXML
    private Label startLbl;
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>


    //ON CLICK ===============================================
    @FXML
    void backLblOnClick(MouseEvent event) {
        Navigation.getInstance().loadView("viewInit");
    }

    @FXML
    void paneOnClick(MouseEvent event) {

    }

    @FXML
    void startLblOnClick(MouseEvent event) {

        //from here we always start a new game
        Navigation.getInstance().saveView("viewGame", "CssGame");
        Navigation.getInstance().loadView("viewGame");
        CtrGame.setGameConfig(playerNameTxt.getText(), serverAddressTxt.getText(),serverPortTxt.getText());

        //pass arguments to then viewGame controller
        //Navigation.getInstance().passToViewGame(playerNameTxt.getText(), serverAddressTxt.getText(),serverPortTxt.getText());
    }
    // ========================================================


    //ON ENTER ***********************************************
    @FXML
    void backLblOnEnter(MouseEvent event) {
        hoverNode(backLbl);
    }

    @FXML
    void startLblOnEnter(MouseEvent event) {
        if (!startLbl.isDisable()) {
            hoverNode(startLbl);
        }
    }
    //********************************************************


    //ON EXIT -------------------------------------------------------
    @FXML
    void backLblOnExit(MouseEvent event) {
        deHoverNode(backLbl);
    }

    @FXML
    void startLblOnExit(MouseEvent event) {
        deHoverNode(startLbl);
    }
    //---------------------------------------------------------------


    //ON ACTION ++++++++++++++++++++++++++++++++++++++++++++++++++++
    @FXML
    void playerNameTxtOnAction(ActionEvent event) {
    }

    @FXML
    void serverPortTxtOnAction(ActionEvent event) {
    }

    @FXML
    void serverAddressTxtOnAction(ActionEvent event) {
    }
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    //ON KEY RELEASE &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    @FXML
    void serverPortTxtOnKeyRelease(KeyEvent event) {
        checkIfSettingsAreOk();
    }

    @FXML
    void playerNameTxtOnKeyRelease(KeyEvent event) {
        checkIfSettingsAreOk();
    }

    @FXML
    void serverAddressTxtOnKeyRelease(KeyEvent event) {
        checkIfSettingsAreOk();
    }
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initAssertAllNodes();
        checkIfSettingsAreOk();

        infoLbl.setVisible(false);

    }

    private void initAssertAllNodes() {
        assert pane != null : "fx:id=\"pane\" was not injected: check your FXML file 'viewGameSetup.fxml'.";
        assert serverAddressTxt != null : "fx:id=\"serverAddressTxt\" was not injected: check your FXML file 'viewGameSetup.fxml'.";
        assert serverAddressLbl != null : "fx:id=\"serverAddressLbl\" was not injected: check your FXML file 'viewGameSetup.fxml'.";
        assert newGameLbl != null : "fx:id=\"newGameLbl\" was not injected: check your FXML file 'viewGameSetup.fxml'.";
        assert playerNameTxt != null : "fx:id=\"playerNameTxt\" was not injected: check your FXML file 'viewGameSetup.fxml'.";
        assert playerNameLbl != null : "fx:id=\"playerNameLbl\" was not injected: check your FXML file 'viewGameSetup.fxml'.";
        assert serverPortTxt != null : "fx:id=\"serverPortTxt\" was not injected: check your FXML file 'viewGameSetup.fxml'.";
        assert serverPortLbl != null : "fx:id=\"serverPortLbl\" was not injected: check your FXML file 'viewGameSetup.fxml'.";
        assert infoLbl != null : "fx:id=\"infoLbl\" was not injected: check your FXML file 'viewGameSetup.fxml'.";
        assert backLbl != null : "fx:id=\"backLbl\" was not injected: check your FXML file 'viewGameSetup.fxml'.";
        assert startLbl != null : "fx:id=\"startLbl\" was not injected: check your FXML file 'viewGameSetup.fxml'.";
    }


    private boolean checkAddress() {

        String address = serverAddressTxt.getText();

        Pattern pattern = Pattern.compile(Settings.IPADDRESS_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(address);

        return matcher.matches();
    }

    private boolean checkPort() {
        int port = 0;
        try {
            port = Integer.parseInt(serverPortTxt.getText());
        } catch (NumberFormatException nfe) {
            System.out.println(nfe.getMessage());
            return false;
        }

        if (port <= 0 || port > 65536) {
            return false;
        } else {
            return true;
        }

    }

    private boolean checkName() {
        return playerNameTxt.getText().length() > 1;
    }

    private void checkIfSettingsAreOk() {
        if (checkAddress() && checkPort() && checkName()) {
            startLbl.setOpacity(1.0d);
            startLbl.setDisable(false);
        } else {
            startLbl.setDisable(true);
            startLbl.setOpacity(Settings.START_DISABLE_OPACITY);
        }
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


}
