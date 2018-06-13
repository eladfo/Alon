package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

/**
 * Created by Aviadjo on 6/14/2017.
 */
public interface IView {
    void displayMaze(Maze maze);
    void displaySolution(Solution solution);

}
