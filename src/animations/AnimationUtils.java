package animations;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Created by macha on 10/04/2016.
 */
public class AnimationUtils {


    public static TranslateTransition createTranslateTransition(int millis,Node node, int byX, int byY,int cycles){
        TranslateTransition t = new TranslateTransition(Duration.millis(millis),node);
        t.setByX(byX);
        t.setByY(byY);
        t.setCycleCount(cycles);
        return t;

    }

    public static RotateTransition createRotationTransition(int millis,Node node, int byAngle, int cycles){
        RotateTransition r = new RotateTransition(Duration.millis(millis),node);
        r.setByAngle(byAngle);
        r.setCycleCount(cycles);
        return r;

    }



}
