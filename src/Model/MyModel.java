package Model;

import Server.ServerStrategyGenerateMaze;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.BestFirstSearch;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import IO.MyDecompressorInputStream;
import Server.*;
import Client.*;
import java.net.UnknownHostException;

/**
 * Created by Aviadjo on 6/14/2017.
 */
public class MyModel extends Observable implements IModel {

    //private ExecutorService threadPool = Executors.newCachedThreadPool();
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;
    private Maze maze;
    private Solution solution;
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    private int steps=0;

    public MyModel()
    {
        //Raise the servers
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    }

    public void startServers()
    {
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
    }

    public void stopServers()
    {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }

    @Override
    public void generateMaze(int row, int col) {
        //Generate maze
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy(){
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{row, col};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[100000000 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
            setChanged();
            characterPositionRow = maze.getStartPosition().getRowIndex();
            characterPositionColumn = maze.getStartPosition().getColumnIndex();
            maze.setValueByCords(characterPositionRow, characterPositionColumn, 0);
            notifyObservers(1);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void solveMaze()
    {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        solution = mazeSolution;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
            setChanged();
            notifyObservers(2);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveMaze(String name)
    {
        try {
            String tmpDir = "resources/SavedMazes/";
            String tmpFile = tmpDir + name;
            File f = new File(tmpFile);
            /**if (f.exists()) {
                FileInputStream file = new FileInputStream(tmpFile);
                ObjectInputStream obj = new ObjectInputStream(file);
                solution = (Solution) obj.readObject();
                file.close();
                obj.close();
            } else {**/

            f.createNewFile();
            FileOutputStream outFile = new FileOutputStream(tmpFile);
            ObjectOutputStream outObj = new ObjectOutputStream(outFile);
            outObj.writeObject(maze);
            outFile.close();
            outObj.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Solution getSolution() {
        return solution;
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void moveCharacter(KeyCode movement)
    {
        switch (movement) {
            case NUMPAD8:
                if(isLegal(characterPositionRow-1,characterPositionColumn)) {
                    characterPositionRow--;
                    steps++;
                }
                break;
            case NUMPAD2:
                if(isLegal(characterPositionRow+1,characterPositionColumn)) {
                    characterPositionRow++;
                    steps++;
                }
                break;
            case NUMPAD6:
                if(isLegal(characterPositionRow,characterPositionColumn+1)) {
                    characterPositionColumn++;
                    steps++;
                }
                break;
            case NUMPAD4:
                if(isLegal(characterPositionRow,characterPositionColumn-1)) {
                    characterPositionColumn--;
                    steps++;
                }
                break;
            case NUMPAD3:
                if(isLegal(characterPositionRow+1,characterPositionColumn+1))
                {
                    characterPositionColumn++;
                    characterPositionRow++;
                    steps++;
                }
                break;
            case NUMPAD1:
                if(isLegal(characterPositionRow+1,characterPositionColumn-1))
                {
                    characterPositionColumn--;
                    characterPositionRow++;
                    steps++;
                }
                break;
            case NUMPAD7:
                if(isLegal(characterPositionRow-1,characterPositionColumn-1))
                {
                    characterPositionColumn--;
                    characterPositionRow--;
                    steps++;
                }
                break;
            case NUMPAD9:
                if(isLegal(characterPositionRow-1,characterPositionColumn+1))
                {
                    characterPositionColumn++;
                    characterPositionRow--;
                    steps++;
                }
                break;
        }
        setChanged();
        notifyObservers(1);
    }

    public boolean isLegal(int row, int col)
    {
        Position p = new Position(row, col , null);
        boolean res =  maze.isLegal(p, 0);

        return res;
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }

    @Override
    public int get_Num_of_steps() {
        return steps;
    }
}
