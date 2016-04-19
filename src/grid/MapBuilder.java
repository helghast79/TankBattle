package grid;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by codecadet on 18/03/16.
 */
public class MapBuilder {


    private int rows;
    private int cols;

    private String mapPath;

    Cell[][] cells;

    public MapBuilder(String mapPath) {

        this.mapPath = mapPath;
        load();

    }


    private void load() {


        List<String> lines = new ArrayList<String>();
        String line;


        try {
            //handle path string properly to allow in jar access
            URL mapFileURL =this.getClass().getResource(mapPath);

            FileReader fileReader = new FileReader(mapFileURL.getFile());
            BufferedReader bReader = new BufferedReader(fileReader);

            while ((line = bReader.readLine()) != null) {

                lines.add(line);
            }
            bReader.close();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());

        }


        if (lines.size() == 0) {
            return;
        }


        this.rows = lines.size();
        this.cols = lines.get(0).length();

        cells = new Cell[rows][cols];


        for (int row = 0; row < rows; row++) {

            char[] charLine = lines.get(row).toCharArray();

            for (int col = 0; col < cols; col++) {

                cells[row][col] = new Cell(row,col,charLine[col]);

            }//end for
        }//end for
    }//end load


    public Cell[][] getCellsArray(){
        return cells;
    }

}
