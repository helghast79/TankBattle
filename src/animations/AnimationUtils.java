package animations;


import javafx.animation.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


/**
 * Created by macha on 10/04/2016.
 */
public class AnimationUtils {


    public static TranslateTransition createTranslateTransition(int millis, Node node, int byX, int byY, int cycles) {
        TranslateTransition t = new TranslateTransition(Duration.millis(millis), node);
        t.setByX(byX);
        t.setByY(byY);
        t.setCycleCount(cycles);
        return t;

    }

    public static RotateTransition createRotationTransition(int millis, Node node, int byAngle, int cycles) {
        RotateTransition r = new RotateTransition(Duration.millis(millis), node);
        r.setByAngle(byAngle);
        r.setCycleCount(cycles);
        return r;

    }

    public static Animation createSpriteAnimation(ImageView imageView,
                                                  int spriteColumns,
                                                  int spriteCount,
                                                  int spriteOffset_X,
                                                  int spriteOffset_Y,
                                                  int spriteWidth,
                                                  int spriteHeight,
                                                  Duration duration) {

return new SpriteAnimation(
        imageView,
        duration,
        spriteCount,
        spriteColumns,
        spriteOffset_X,
        spriteOffset_Y,
        spriteWidth,
        spriteHeight);



    }


    private static class SpriteAnimation extends Transition {

        private final ImageView imageView;
        private final int count;
        private final int columns;
        private final int offsetX;
        private final int offsetY;
        private final int width;
        private final int height;

        private int lastIndex;

        public SpriteAnimation(
                ImageView imageView,
                Duration duration,
                int count, int columns,
                int offsetX, int offsetY,
                int width, int height) {
            this.imageView = imageView;
            this.count = count;
            this.columns = columns;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.width = width;
            this.height = height;
            setCycleDuration(duration);
            setInterpolator(Interpolator.LINEAR);
        }

        protected void interpolate(double k) {
            final int index = Math.min((int) Math.floor(k * count), count - 1);
            if (index != lastIndex) {
                final int x = (index % columns) * width + offsetX;
                final int y = (index / columns) * height + offsetY;
                imageView.setViewport(new Rectangle2D(x, y, width, height));
                lastIndex = index;
            }
        }
    }


}
