package gameobjects;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.*;

/**
 * Created by codecadet on 21/03/16.
 */
public class Player {


    private ImageView imageView;
    private Image imageUp;
    private Image imageDown;
    private Image imageLeft;
    private Image imageRight;
    private Image imageDownRight;
    private Image imageDownLeft;
    private Image imageUpRight;
    private Image imageUpLeft;
private Label armorLbl;

    public Image getImageDownRight() {
        return imageDownRight;
    }

    public void setImageDownRight(Image imageDownRight) {
        this.imageDownRight = imageDownRight;
    }

    public Image getImageDownLeft() {
        return imageDownLeft;
    }

    public void setImageDownLeft(Image imageDownLeft) {
        this.imageDownLeft = imageDownLeft;
    }

    public Image getImageUpRight() {
        return imageUpRight;
    }

    public void setImageUpRight(Image imageUpRight) {
        this.imageUpRight = imageUpRight;
    }

    public Image getImageUpLeft() {
        return imageUpLeft;
    }

    public void setImageUpLeft(Image imageUpLeft) {
        this.imageUpLeft = imageUpLeft;
    }



    private ArrayList<Weapon> weapons;
    private int currentWeaponIndex;

    private int row;
    private int col;
    private boolean movingNow; //wait until movement is finished

    private String name;
    private int armor;

    private int totalMoves;
    private int movesLeft;


    public Player(String name, int armor, int row, int col, int totalMoves) {

        this.name = name;
        this.armor = armor;
        this.row = row;
        this.col = col;

        this.totalMoves = totalMoves;
        this.movesLeft = this.totalMoves;

        //this.imageView = new ImageView(); //use setArgsPassedByGameSetupController instead
        this.weapons = new ArrayList<Weapon>();
        this.movingNow = false;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Image getImageUp() {
        return imageUp;
    }

    public Image getImageDown() {
        return imageDown;
    }

    public Image getImageLeft() {
        return imageLeft;
    }

    public Image getImageRight() {
        return imageRight;
    }

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }

    public int getCurrentWeaponIndex() {
        return currentWeaponIndex;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isMovingNow() {
        return movingNow;
    }

    public String getName() {
        return name;
    }

    public int getArmor() {
        return armor;
    }
    public String getArmorString() {
        return Integer.toString(armor);
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public int getMovesLeft() {
        return movesLeft;
    }

    public void setImageUp(Image imageUp) {
        this.imageUp = imageUp;
    }

    public void setImageDown(Image imageDown) {
        this.imageDown = imageDown;
    }

    public void setImageLeft(Image imageLeft) {
        this.imageLeft = imageLeft;
    }

    public void setImageRight(Image imageRight) {
        this.imageRight = imageRight;
    }

    public void setCurrentWeaponIndex(int currentWeaponIndex) {
        this.currentWeaponIndex = currentWeaponIndex;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setMovingNow(boolean movingNow) {
        this.movingNow = movingNow;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public void setMovesLeft(int movesLeft) {
        this.movesLeft = movesLeft;
    }





    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setImageViewImage(Image image) {
        this.imageView.setImage(image);
    }



   //Label
    public Label getArmorLbl() {
        return armorLbl;
    }

    public void setArmorLbl(Label armorLbl) {
        this.armorLbl = armorLbl;
    }





    public void addWeapon(Weapon weapon) {
        this.weapons.add(weapon);

    }
    public void addWeapon(int index, Weapon weapon) {
        this.weapons.add(index, weapon);

    }

    public void removeWeapon(int index) {
        this.weapons.remove(index);
    }
    public void removeWeapon(Weapon weapon) {
        this.weapons.remove(weapon);
    }

    public Weapon getNextWeapon() {
        //no weapons
        if(weapons.size()==0) return null;

        //go back to first weapon
        if(currentWeaponIndex == weapons.size()-1){
            currentWeaponIndex = 0;
        }else{ //or next
            currentWeaponIndex +=1;
        }

        return weapons.get(currentWeaponIndex);
    }
    public Weapon getWeapon(int index) {
        //no weapons
        if(index <0 || index >=weapons.size()) return null;

        return weapons.get(index);
    }
}
