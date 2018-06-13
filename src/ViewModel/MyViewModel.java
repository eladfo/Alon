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

    public void loadMaze(String name){ model.loadMaze(name); }

    public void saveMaze(String mazeName)
    {
        model.saveMaze(mazeName);
    }

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

    public void print()
    {
        System.out.println(getPoolSize());
        lbl_size_of_pool.set(String.valueOf(getPoolSize()));
        lbl_Kind_of_algo.set(getSearchingAlgorithm());
        lbl_Kind_of_genarate.set(getMazeGenerator());
    }

    public void stopServers()
    {
        model.stopServers();
    }

    //Binding and Properties
    public StringProperty lbl_Kind_of_genarate = new SimpleStringProperty(""); //For Binding
    public StringProperty lbl_Kind_of_algo = new SimpleStringProperty(""); //For Binding
    public StringProperty lbl_size_of_pool = new SimpleStringProperty(""); //For Binding

    static
    {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            String filename = "resources/config.properties";
            input = new FileInputStream(filename);
            //input = Server.class.getClassLoader().getResourceAsStream(filename);
            if(input==null){
                System.out.println("Sorry, unable to find " + filename);
            }
            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value and print it out
            poolSize = prop.getProperty("PoolSize");
            mazeGenerator = prop.getProperty("MazeGenerator");
            searchingAlgorithm = prop.getProperty("SearchingAlgorithm");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int getPoolSize() {
        try {
            int size = Integer.valueOf(poolSize);
            if (size > 0)
                return size;
            return 1;
        }
        catch (NumberFormatException e) {
            return 1;
        }
    }

    public static String getMazeGenerator() {
        return mazeGenerator;
    }

    public static String getSearchingAlgorithm() {
        return searchingAlgorithm;
    }



}
