package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.ArrayList;

public class MazeDisplayer extends Canvas{

    public Maze maze;
    public Solution solution;
    private int charPosRow = 1;
    private int charPosCol = 1;
    public double cellHeight;
    public double cellWidth;
    private GraphicsContext gc = getGraphicsContext2D();
    private int[] tmp = new int[2];
    private String sol;
    public double last_offset_height;
    public double last_offset_width ;

    public void setMaze(Maze maze) {
        if(maze!=null) {
            this.maze = maze;
            cellHeight = getHeight() / maze.getM_rows();
            cellWidth = getWidth() / maze.getM_columns();
            last_offset_height =0;
            last_offset_width=0;
            redrawMaze_offset(0,0);
        }
    }

    public void set() {
    if(maze != null) {
        cellHeight = getHeight() / maze.getM_rows();
        cellWidth = getWidth() / maze.getM_columns();
        redrawMaze_offset(0 , 0);
        }
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        redrawSolution();
    }

    public void setCharPos(int row, int column) {
        charPosRow = row;
        charPosCol = column;
        redrawMaze_offset(0,0);
    }

    public double getCharPosRow(double ofset_width, double ofset_hight) {
        return charPosRow;
    }

    public double getCharPosCol(double ofset_width, double ofset_hight) {
        return charPosCol;
    }

    public void redrawMaze_offset(double offset_hight , double offset_width) {
        if (maze != null && offset_hight==0 && offset_width==0 ) {
            Image wallImage = new Image(this.getClass().getResourceAsStream(ImageFileNameWall.get()));
            Image characterImage = new Image(this.getClass().getResourceAsStream(ImageFileNameCharacter.get()));
            Image targetImage = new Image(this.getClass().getResourceAsStream(ImageFileNameTarget.get()));

            gc.clearRect(0   , 0  , getWidth(), getHeight());
            //Draw Maze
            for (int i = 0; i < maze.getM_rows(); i++) {
                for (int j = 0; j < maze.getM_columns(); j++) {
                    if (maze.getPositionValue(i, j) == 1) {
                        gc.drawImage(wallImage,  last_offset_width + offset_width + j * cellWidth, last_offset_height + offset_hight + i * cellHeight, cellWidth, cellHeight);
                    }
                }
            }
            //Draw Character
            gc.drawImage(characterImage,   last_offset_width + offset_width + charPosCol * cellWidth, last_offset_height + offset_hight + charPosRow * cellHeight, cellWidth, cellHeight);
            //Draw Target
            gc.drawImage(targetImage,   last_offset_width + offset_width + maze.getGoalPosition().getColumnIndex()  * cellWidth, last_offset_height + offset_hight + maze.getGoalPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);

        }

        else if (maze!= null){
            Image wallImage = new Image(this.getClass().getResourceAsStream(ImageFileNameWall.get()));
            Image characterImage = new Image(this.getClass().getResourceAsStream(ImageFileNameCharacter.get()));
            Image targetImage = new Image(this.getClass().getResourceAsStream(ImageFileNameTarget.get()));

            gc.clearRect(0   , 0  , getWidth(), getHeight());
            //Draw Maze
            for (int i = 0; i < maze.getM_rows(); i++) {
                for (int j = 0; j < maze.getM_columns(); j++) {
                    if (maze.getPositionValue(i, j) == 1) {
                        gc.drawImage(wallImage,   offset_width + j * cellWidth,   offset_hight + i * cellHeight, cellWidth, cellHeight);
                    }
                }
            }
            //Draw Character
            gc.drawImage(characterImage,   offset_width + charPosCol * cellWidth,   offset_hight + charPosRow * cellHeight, cellWidth, cellHeight);
            //Draw Target
            gc.drawImage(targetImage,  offset_width + maze.getGoalPosition().getColumnIndex()  * cellWidth,   offset_hight + maze.getGoalPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);

        }
        if(offset_hight!= 0 || offset_width != 0) {
            last_offset_height =   offset_hight;
            last_offset_width =   offset_width;
        }
    }

    public void redrawMaze_zoom(double zoom) {
        if(zoom==1) {
            cellWidth =  cellWidth * 2;
            cellHeight =  cellHeight * 2;
            last_offset_height =0;
            last_offset_width=0;
        }
        else
        {
            cellWidth =  cellWidth * 0.5;
            cellHeight =  cellHeight * 0.5;
            last_offset_height =0;
            last_offset_width=0;
        }
        if (maze != null) {
            Image wallImage = new Image(this.getClass().getResourceAsStream(ImageFileNameWall.get()));
            Image characterImage = new Image(this.getClass().getResourceAsStream(ImageFileNameCharacter.get()));
            Image targetImage = new Image(this.getClass().getResourceAsStream(ImageFileNameTarget.get()));
            gc.clearRect(0   , 0  , getWidth(), getHeight());
            //Draw Maze
            for (int i = 0; i < maze.getM_rows(); i++) {
                for (int j = 0; j < maze.getM_columns(); j++) {
                    if (maze.getPositionValue(i, j) == 1) {
                        gc.drawImage(wallImage,j * cellWidth ,i * cellHeight, cellWidth, cellHeight);
                    }
                }
            }
            //Draw Character
            gc.drawImage(characterImage,   charPosCol * cellWidth,   charPosRow * cellHeight, cellWidth, cellHeight);
            //Draw Target
            gc.drawImage(targetImage,   maze.getGoalPosition().getColumnIndex() * cellWidth,   maze.getGoalPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);

        }
    }

    public void redrawSolution() {
        if (maze != null) {
           {
                setMaze(maze);
                //Image solutionImage = new Image(this.getClass().getResourceAsStream(ImageFileNameSolution.get()));
                Image targetImage = new Image(this.getClass().getResourceAsStream(ImageFileNameTarget.get()));
                Image characterImage = new Image(this.getClass().getResourceAsStream(ImageFileNameCharacter.get()));
                sol = ImageFileNameSolution.get();
                Image solutionImage = new Image(this.getClass().getResourceAsStream(sol));
                ArrayList<AState> mazeSolutionSteps = solution.getSolutionPath();
                for (int i = 0; i < mazeSolutionSteps.size(); i++) {
                    tmp = astetaToInt(mazeSolutionSteps.get(i));
                    gc.drawImage(solutionImage, tmp[1] * cellWidth, tmp[0] * cellHeight, cellWidth, cellHeight);
                }
                //Draw Char
                gc.drawImage(characterImage, charPosCol * cellWidth, charPosRow * cellHeight, cellWidth, cellHeight);
                //Draw Target
                gc.drawImage(targetImage, maze.getGoalPosition().getColumnIndex() * cellWidth, maze.getGoalPosition().getRowIndex() * cellHeight, cellWidth, cellHeight);
           }
        }
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
        gc.clearRect(0, 0, getWidth(), getHeight());
        maze = null;
    }

    public boolean isMazeNull()
    {
        return maze == null;
    }

    public Double get_player_position_hight(double ofset_hight,double ofset_width)
    {
        return (cellHeight* charPosRow + 25 +  + ofset_hight);
    }

    public Double get_player_position_width(double ofset_hight,double ofset_width)
    {
        return (cellWidth* charPosCol + 175 +  + ofset_width);
    }

    public Double getCellHeight()
    {
        return cellHeight;
    }

    public Double getCellWidth()
    {
        return cellWidth;
    }

    public boolean is_free(int row , int col)
    {
        if(maze.getPositionValue(row,col) == 0)
            return true;
        return false;
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

    public String getImageFileNameCharacter() {return ImageFileNameCharacter.get();
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

