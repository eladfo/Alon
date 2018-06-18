package ViewModel;

import Model.IModel;
import Model.MyModel;
import Server.Server;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

/**
 * Created by Aviadjo on 6/14/2017.
 */
public class MyViewModel extends Observable implements Observer {

    private IModel model;
    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;
    public int steps;

    private static String poolSize;
    private static String mazeGenerator;
    private static String searchingAlgorithm;

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
            else  if((int)arg ==2) {
                setChanged();
                notifyObservers(2);
            }


        }
    }

    public void generateMaze(int row, int col){
        model.generateMaze(row, col);
    }

    public void solveMaze(){ model.solveMaze(); }



    public void loadMaze1(String name){ model.loadMaze1(name); }



    public void saveMaze1(String mazeName)
    {
        model.saveMaze1(mazeName);
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public void moveCharacterByMouse(int movement){
        model.moveCharacterByMouse(movement);
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

    public void print()
    {

        lbl_size_of_pool.set(String.valueOf(Server.getPoolSize()));
        lbl_Kind_of_algo.set(Server.getSearchingAlgorithm());
        lbl_Kind_of_genarate.set(Server.getMazeGenerator());
    }

    public void stopServers()
    {
        model.stopServers();
    }

    public void changeMusic(int num){
        model.changeMusic(num);
    }

    //Binding and Properties
    public StringProperty lbl_Kind_of_genarate = new SimpleStringProperty(""); //For Binding
    public StringProperty lbl_Kind_of_algo = new SimpleStringProperty(""); //For Binding
    public StringProperty lbl_size_of_pool = new SimpleStringProperty(""); //For Binding



    public void win()
    {
        steps=0;
    }

}
