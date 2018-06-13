package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static java.awt.Color.BLACK;

/**
 * Created by Aviadjo on 3/9/2017.
 */
public class MazeDisplayer extends Canvas {

    private Maze maze;
    private Solution solution;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;

    public void setMaze(Maze maze) {
        this.maze = maze;
        redrawMaze();
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        redrawSolution();
    }

    public void setCharacterPosition(int row, int column) {
        characterPositionRow = row;
        characterPositionColumn = column;
        redrawMaze();
    }

    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    public void redrawMaze() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.getM_rows();
            double cellWidth = canvasWidth / maze.getM_columns();
            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                Image targetImage = new Image(new FileInputStream(ImageFileNameTarget.get()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(), getHeight());
                //Draw Maze
                for (int i = 0; i < maze.getM_rows(); i++) {
                    for (int j = 0; j < maze.getM_columns(); j++) {
                        if (maze.getPositionValue(i, j) == 1) {
                            gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }
                //Draw Character
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);
                //Draw Target
                gc.drawImage(targetImage, maze.getGoalPosition().getColumnIndex() * cellWidth, maze.getGoalPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);

            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }

    public void redrawMaze1(int flag , double parameter) {
        if (maze != null) {
            double canvasHeight;
            double canvasWidth;
            if(flag==1) {
                System.out.println(getHeight());
                canvasHeight = getHeight();
                canvasWidth = getWidth();
            }
            else
                {
                 canvasHeight = getHeight();
                    canvasWidth = getWidth();
            }
            double cellHeight = canvasHeight / maze.getM_rows();
            double cellWidth = canvasWidth / maze.getM_columns();
            try {
                Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
                Image characterImage = new Image(new FileInputStream(ImageFileNameCharacter.get()));
                Image targetImage = new Image(new FileInputStream(ImageFileNameTarget.get()));
                GraphicsContext gc = getGraphicsContext2D();
                gc.clearRect(0, 0, getWidth(),  getHeight());
                //Draw Maze
                for (int i = 0; i < maze.getM_rows(); i++) {
                    for (int j = 0; j < maze.getM_columns(); j++) {
                        if (maze.getPositionValue(i, j) == 1) {
                            gc.drawImage(wallImage, j * cellWidth, i * cellHeight, cellWidth, cellHeight);
                        }
                    }
                }
                //Draw Character
                //gc.setFill(Color.RED);
                //gc.fillOval(characterPositionColumn * cellHeight, characterPositionRow * cellWidth, cellHeight, cellWidth);
                gc.drawImage(characterImage, characterPositionColumn * cellWidth, characterPositionRow * cellHeight, cellWidth, cellHeight);

                //Draw Target
                gc.drawImage(targetImage, maze.getGoalPosition().getColumnIndex() * cellWidth, maze.getGoalPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);

            } catch (FileNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }

    private void redrawSolution() {
        if (maze != null) {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            double cellHeight = canvasHeight / maze.getM_rows();
            double cellWidth = canvasWidth / maze.getM_columns();

            try {
                Image solutionImage = new Image(new FileInputStream(ImageFileNameSolution.get()));
                Image targetImage = new Image(new FileInputStream(ImageFileNameTarget.get()));
                GraphicsContext gc = getGraphicsContext2D();

                ArrayList<AState> mazeSolutionSteps = solution.getSolutionPath();
                int[] tmp = new int[2];
                for (int i = 0; i < mazeSolutionSteps.size(); i++) {
                    tmp = astetaToInt(mazeSolutionSteps.get(i));
                    gc.drawImage(solutionImage, tmp[1] * cellWidth, tmp[0] * cellHeight, cellWidth, cellHeight);

                }
                //Draw Target
                gc.drawImage(targetImage, maze.getGoalPosition().getColumnIndex() * cellWidth, maze.getGoalPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);

                //redrawMaze();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //try {
        //Thread.sleep(6000);
        //  redrawMaze();
        //}// catch (InterruptedException e) {
        // e.printStackTrace();
        //}

    }

    private int[] astetaToInt(AState a)
    {
        int[] res = new int[2];
        int count = 0;
        String st = a.toString();
        String row = "", col = "";
        for (int i = 0; i < st.length() ; i++) {
            if(st.charAt(i) == '=')
            {
                count++;
                i++;
                if(count == 2)
                    while(st.charAt(i) != ',') {
                        row += st.charAt(i);
                        i++;
                    }
                if(count == 3)
                    while(st.charAt(i) != '}') {
                        col += st.charAt(i);
                        i++;
                    }
            }
        }
        res[0] = Integer.parseInt(row);
        res[1] = Integer.parseInt(col);
        return res;
    }

    public void resetCanvas()
    {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        maze = null;
    }
    public boolean isMazeNull()
    {
        return maze == null;
    }
    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();
    private StringProperty ImageFileNameTarget = new SimpleStringProperty();
    private StringProperty ImageFileNameSolution = new SimpleStringProperty();

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public void setImageFileNameCharacter(String imageFileNameCharacter) {
        this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public String getImageFileNameTarget() {
        return ImageFileNameTarget.get();
    }

    public void setImageFileNameTarget(String imageFileNameTarget) {
        this.ImageFileNameTarget.set(imageFileNameTarget);
    }

    public String getImageFileNameSolution() {
        return ImageFileNameSolution.get();
    }

    public void setImageFileNameSolution(String imageFileNameSolution) {
        this.ImageFileNameSolution.set(imageFileNameSolution);
    }

    //endregion

}
