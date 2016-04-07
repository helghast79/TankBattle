package grid;

/**
 * Created by codecadet on 25/02/16.
 */
public class Cell implements Clickable {

    private int row;
    private int col;

    private CellTypes type;
    private boolean occupied;


    public Cell(int row, int col, char charType) {

        this.row = row;
        this.col = col;
        setType(charType);
        this.occupied = false;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setType(char charType) {

        for (CellTypes ct : CellTypes.values()) {
            if (ct.getCode() == charType) {
                this.type = ct;
                return;
            }
        }

    }

    public CellTypes getType() {
        return type;
    }

    public char getCharType(){
        return this.type.getCode();
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public String getImagePath() {
        return type.getFilePath();
    }


}
