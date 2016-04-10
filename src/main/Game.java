package main;

import javafx.application.Application;

import javafx.event.EventHandler;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Game extends Application {




    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                //do nothing on close
                event.consume();
            }
        });


        Navigation.getInstance().setStage(primaryStage);

        Navigation.getInstance().saveView("viewInit", "CssInit");
        Navigation.getInstance().loadView("viewInit");

    }


    @Override
    public void init() {


    }

    public static void main(String[] args) {
        launch(args);
    }


}
