package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Aviadjo on 6/14/2017.
 */
public class MyViewModel extends Observable implements Observer {

    private IModel model;

    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;
    public int steps;

    public StringProperty characterPositionRow = new SimpleStringProperty(""); //For Binding
    public StringProperty characterPositionColumn = new SimpleStringProperty(""); //For Binding
    public StringProperty step = new SimpleStringProperty(""); //For Binding

    public MyViewModel(IModel model){
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            if((int)arg == 1)
            {
                characterPositionRowIndex = model.getCharacterPositionRow();
                characterPositionRow.set(characterPositionRowIndex + "");
                characterPositionColumnIndex = model.getCharacterPositionColumn();
                characterPositionColumn.set(characterPositionColumnIndex + "");
                steps = model.get_Num_of_steps();
                step.set(steps + "");
                setChanged();
                notifyObservers(1);
            }
            else{
                setChanged();
                notifyObservers(2);
            }


        }
    }

    public void generateMaze(int row, int col){
        model.generateMaze(row, col);
    }

    public void solveMaze(){ model.solveMaze(); }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public Solution getSolution(){
        return model.getSolution();
    }

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }
}
