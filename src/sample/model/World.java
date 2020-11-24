package sample.model;

import sample.Cell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class World {
    private int height, width;
    /*
     * We have created a new class called Cell which determines whether the cell is alive or not and whether it lives in the nextGeneration.
     */
    private Cell[][] Generation;

    /*
     * This Methode reads the configurations from a file given by the user and sets the cells alive as per the pattern in the file.
     * The file also contains the dimensions of the world that will be created.
     */
    public World(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        int width = sc.nextInt();
        int height = sc.nextInt();
        Generation = new Cell[width + 2][height + 2];
        for (int i = 0; i < width + 2; ++i) {
            for (int j = 0; j < height + 2; ++j) {
                Generation[i][j] = new Cell();
            }
        }
        this.height = height;
        this.width = width;
        int a=0;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == 'O') {
                    Generation[i + 1][a].toggleLife();
                }
            }
            a++;
        }
    }

    /*
     * We basically extended the world virtually because for each 3 x 3 matrix we are checking the neighbours of the middle position.
     * This means without extending the world we cannot check the neighbours of the cells at the edges.
     */
    public World(int width, int height) {
        int widthExtended = width + 2;
        int heightExtended = height + 2;
        Generation = new Cell[widthExtended][heightExtended];
        for (int i = 0; i < widthExtended; ++i) {
            for (int j = 0; j < heightExtended; ++j) {
                Generation[i][j] = new Cell();
            }
        }
        this.height = height;
        this.width = width;
    }

    public void toggleCell(int x, int y) {
        x += 1;
        y += 1;
        Generation[x][y].toggleLife();
    }

    /*
     * This methode writes the pattern/configuration created by the user to a .txt/.csv file which can be selected by the user itself.
     */
    public void saveToFile(final File file) throws IOException {
        FileWriter write = new FileWriter(file);
        write.write(getWidth() + " " + getHeight() + "\n");
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (isAliveAt(i , j )) {
                    write.write('O' );
                }
                else {
                    write.write('*');
                }
            }
            write.write("\n");
        }
        write.close();
        System.out.println("Written");
    }

    /*
     * We could have used one loop with 2 matrixes: one for the current generation and the other to save the next generation.
     * At the end of the loop we need to rewrite the current generation, so we can save the next generation to continue the loop.
     * This means when copying the matrixes we are copying 1 cel at a time which can be seen as a second loop.
     * We have chose to use 2 loops: One for calculating the value for the next generation (saving this value internally in the class Cell).
     *                               Second loop to save the nextGeneration in current generation.
     */
    public void nextGeneration() {
        for (int i = 1; i < width + 1; i++) {
            for (int j = 1; j < height + 1; j++) {
                int aantalburen = checkburen(i,j);
                Generation[i][j].calculatenextGen(aantalburen);
            }
        }
        for (int i = 1; i < width + 1; i++) {
            for (int j = 1; j < height + 1; j++) {
                Generation[i][j].goNextGen();
            }
        }
    }

    /*
     * Returns the width of the world (NOT the width of the extended world but the one given by the user)
     */
    public int getWidth() {
        return width;
    }

    /*
     * Returns the height of the world (NOT the height of the extended world but the one given by the user)
     */
    public int getHeight() {
        return height;
    }

    /*
     * This methode checks whether a cell is alive or not
     */
    public boolean isAliveAt(int x, int y) {
        return (Generation[x + 1][y + 1].isAlive());
    }

    /*
     * This methode makes random cells in the world with the dimensions given by user alive.
     */
    public void randomCells() {
        for (int i = 1; i < width + 1; i++) {
            for (int j = 1; j < height + 1; j++) {
                if (Math.random() > 0.5)
                    /*
                     * if random number is less then 0.5 cell will be dead (default value) otherwise cell will be alive
                     */
                    Generation[i][j].toggleLife();
            }
        }
    }

    /*
     * This methode checks (for the cell in the middle of each 3 x 3 matrix) how many neighbours it has.
     * This will determine whether the cell lives or dies in the next Generation.
     * The index x and y can never be the indexes of the edges of the extended matrix.
     *
     */
    private int checkburen(int x, int y) {
        int aantal = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                aantal += (Generation[i][j].isAlive() ? 1 : 0);
            }
        }
        if(Generation[x][y].isAlive()){
            aantal -= 1;
        }
        return aantal;
    }
}