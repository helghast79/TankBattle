package gameobjects;

import javafx.scene.image.Image;

/**
 * Created by codecadet on 21/03/16.
 */
public class Weapon {


    private String name;
    private Image imageIcon;
    private int damage;
    private int shotsPerTurn;
    private int shotType; // type 0:radious, 1:strait line
    private int range;
    private int rechargeablePeriod; //zero for non reachargeable (used once) weapon

    private int rechargeableCountdown = 0; //when 0 weapon is available for use, when used it's set to rechargeablePeriod

   public Weapon(){

   }

    public Weapon(String name, int damage, int shotsPerTurn, int shootType, int range, int rechargeablePeriod) {
        this.name = name;
        this.damage = damage;
        this.shotsPerTurn = shotsPerTurn;
        this.shotType = shootType;
        this.range = range;
        this.rechargeablePeriod = rechargeablePeriod;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public int getShotsPerTurn() {
        return shotsPerTurn;
    }

    public int getShotType() {
        return shotType;
    }

    public int getRange() {
        return range;
    }

    public int getRechargeablePeriod() {
        return rechargeablePeriod;
    }

    public void decrementRechargeableCountdaown(){
        rechargeableCountdown = Math.max(0, rechargeableCountdown-1);
    }

    public int getRechargeableCountdown() {
        return rechargeableCountdown;
    }
    public void resetRechargeableCountdown(){
        rechargeableCountdown = rechargeablePeriod;
    }

    public Image getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(Image imageIcon) {
        this.imageIcon = imageIcon;
    }
}
