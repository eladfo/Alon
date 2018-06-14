package Model;

import Server.ServerStrategyGenerateMaze;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

import java.io.*;
import java.net.InetAddress;
import java.util.Observable;
import IO.MyDecompressorInputStream;
import Server.*;
import Client.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.UnknownHostException;

/**
 * Created by Aviadjo on 6/14/2017.
 */
public class MyModel extends Observable implements IModel {

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
        playMusic("resources/Sounds/backg.mp3");
    }

    public void startServers()
    {
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
        System.out.println("Servers are open...");
    }

    public void stopServers()
    {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
        System.out.println("Servers are closed.");

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
        String tmpDir = "resources/SavedMazes/";
        String tmpFile = tmpDir + name;
        File f = new File(tmpFile);
        try {
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
    public void loadMaze(String name)
    {
        String tmpDir = "resources/SavedMazes/";
        String tmpFile = tmpDir + name;
        File f = new File(tmpFile);
        try {
            if (f.exists()) {
                FileInputStream file = new FileInputStream(tmpFile);
                ObjectInputStream obj = new ObjectInputStream(file);
                maze = (Maze) obj.readObject();
                file.close();
                obj.close();
                setChanged();
                characterPositionRow = maze.getStartPosition().getRowIndex();
                characterPositionColumn = maze.getStartPosition().getColumnIndex();
                maze.setValueByCords(characterPositionRow, characterPositionColumn, 0);
                notifyObservers(1);
            } else
                return;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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

    private boolean isLegal(int row, int col)
    {
        Position p = new Position(row, col , null);
        boolean res =  maze.isLegal(p, 0);
        return res;
    }

    public void playMusic(String songName){
        MediaPlayer player;
        player = new MediaPlayer(new Media(new File(songName).toURI().toString()));
        player.setOnEndOfMedia(()-> {
            player.seek(Duration.ZERO);
        });
        Thread t = new Thread(() -> player.play());
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.start();
    }

}
