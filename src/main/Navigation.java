package main;

import controllers.CtrGame;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by codecadet on 17/03/16.
 */
public class Navigation {

    //store loaders to be possible to comunicate between them
    private FXMLLoader ctrGameFXMLloader;


    // static instance of this class
    private static Navigation instance = null;


    private static HashMap<String, Scene> myScenes = new HashMap<String, Scene>();

    //private LinkedList<Scene> scenes = new LinkedList<Scene>(); // Navigation History
    // private Map<String, Initializable> controllers = new HashMap<>(); //Container of controllers

    private Stage stage; // reference to the application window


    // private constructor so its not possible to instantiate from outside
    private Navigation() {
    }

    // static method that returns the instance
    public static Navigation getInstance() {

        // the instance is created only the first time this method is called
        if (instance == null) {
            instance = new Navigation();
        }

        // it always return the same instance, there is no way to have another one
        return instance;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }


    private void setScene(Scene scene) {

        // setArgsPassedByGameSetupController the scene
        stage.setScene(scene);

        // show the stage to reload
        stage.show();
    }


    public void saveView(String view, String css) {
        try {

            // Instantiate the view and the controller
            FXMLLoader fxmlLoader;
            fxmlLoader = new FXMLLoader(getClass().getResource(Settings.FILES_FXML_FOLDER + view + ".fxml"));
            Parent root = fxmlLoader.load();


            
            //store the loader because it's necessary to comunicate between controllers
            if (view.equals("viewGame")) {
                ctrGameFXMLloader = fxmlLoader;
            }




            //if css is passed load it
            if (!css.equals("")) {
                root.getStylesheets().add(getClass().getResource(Settings.FILES_CSS_FOLDER + css + ".css").toString());
            }

            //Store the controller
            //controllers.put(view, fxmlLoader.getController());

            // Create a new scene and add it to the stack
            Scene scene = new Scene(root, Settings.SCENE_WIDTH, Settings.SCENE_HEIGHT);

            //store scene in map
            myScenes.put(view, scene);

            // Put the scene on the stage
            setScene(scene);


            //stage.show();

        } catch (IOException e) {
            System.out.println("Failure to load view " + view + " : " + e.getMessage());
        }
    }


    public void loadView(String view) {

        Scene sceneToLoad = myScenes.get(view);

        if (sceneToLoad == null) return;

        setScene(sceneToLoad);

    }

    public boolean deleteView(String view) {

        if (myScenes.containsKey(view)) {
            myScenes.remove(view);
            return true;

        }

        return false;
    }

    public boolean viewExists(String view) {
        if (myScenes.containsKey(view)) {
            return true;
        }

        return false;
    }


    public void passToViewGame(String playerName, String serverAddress, String portNumber) {

        CtrGame controller =
                ctrGameFXMLloader.<CtrGame>getController();
        controller.setArgsPassedByGameSetupController(playerName, serverAddress, portNumber);


    }


}
