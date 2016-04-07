package grid;

/**
 * Created by codecadet on 18/03/16.
 */
public enum CellTypes {

    GREEN_A('A', "greenA.jpeg"),
    GREEN_B('B', "greenB.jpeg"),
    GREEN_C('C', "greenC.jpeg"),
    GREEN_D('D', "greenD.jpeg"),
    MOUNTAIN_A('M', "mountainA.jpeg"),
    MOUNTAIN_B('N', "mountainB.jpeg"),
    TREE_A('T', "treeA.jpeg"),
    TREE_B('R', "treeB.jpeg");


    private char code;
    private String filePath;

    CellTypes(char code, String filePath) {

       this.code = code;
        this.filePath = filePath;
    }


    public char getCode() {
        return code;
    }

    public String getFilePath() {
        return filePath;
    }
}
