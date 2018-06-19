package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import javafx.scene.input.KeyCode;

/**
 * Created by Aviadjo on 6/14/2017.
 */
public interface IModel {
    void generateMaze(int width, int height);
    void solveMaze();
    void moveCharacter(KeyCode movement);
    Maze getMaze();
    Solution getSolution();
    int getCharacterPositionRow();
    int getCharacterPositionColumn();
    int get_Num_of_steps();
    void stopServers();
    void changeMusic(int num);
   // void win();
    void saveMaze1(String name);
    void loadMaze1(String name);
    void moveCharacterByMouse(int movement);
}
