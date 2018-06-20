package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

public interface IView {
    void displayMaze(Maze maze);
    void displaySolution(Solution solution);

}
