package animations;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import main.Settings;

import java.net.URL;

/**
 * Created by macha on 17/04/2016.
 */
public enum Sounds {

    MUSIC("music.mp3"),
    //TANK_IDLE("tank_idle.mp3"),
    TANK_MOVING("tank_maneuvers.mp3"),
    MISSILE_SHOOT("sound_missile2.wav"),
    MISSILE_HIT("sound_explosion2.wav"),
    CANNON_SHOOT("map/mountainA.jpeg"),
    CANNON_HIT("map/mountainB.jpeg");

    private static AudioClip TankMoveClip = new AudioClip(Sounds.class.getResource(Settings.FILES_SOUNDS_FOLDER + Sounds.TANK_MOVING.getFilePath()).toString());
    private static  MediaPlayer MediaPlayerMusic;// = new MediaPlayer(  new Media(new File(Settings.FILES_RESOURCES_FOLDER + Sounds.MUSIC.getFilePath()).toURI().toString()) );

    private String filePath;


    Sounds(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void playSound(){
        AudioClip audioClip = new AudioClip(Sounds.class.getResource(Settings.FILES_SOUNDS_FOLDER + filePath).toString());
        audioClip.play();
    }
    public void playSound(int cycleCount,double volume){
        AudioClip audioClip = new AudioClip(Sounds.class.getResource(Settings.FILES_SOUNDS_FOLDER + filePath).toString());
        audioClip.setCycleCount(cycleCount);
        audioClip.play(volume);
    }

    public static void playTankMove(){
        TankMoveClip.setCycleCount(-1);
        TankMoveClip.setVolume(0.5d);
        TankMoveClip.play();
    }
    public static void stopTankMove(){
        TankMoveClip.stop();
    }

    public static void playMusic() {
        String filePath = Settings.FILES_SOUNDS_FOLDER + Sounds.MUSIC.getFilePath();
        URL fileURL = Sounds.class.getResource(filePath);

        Media media = new Media(fileURL.toString());
        MediaPlayerMusic = new MediaPlayer( media );
        MediaPlayerMusic.setAutoPlay(true);
        MediaPlayerMusic.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                playMusic();
            }
        });
        MediaPlayerMusic.play();

    }
    public static void stopMusic() {
        MediaPlayerMusic.setOnEndOfMedia(null);
        MediaPlayerMusic.stop();
    }
}
