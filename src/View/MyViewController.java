package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements Observer, IView {

    @FXML
    private MyViewModel viewModel;
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.Label lbl_num_of_steps;
    public javafx.scene.control.ComboBox<String> combo_world;
    private boolean win=false;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
    }

    private void bindProperties(MyViewModel viewModel) {
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);
        lbl_num_of_steps.textProperty().bind(viewModel.step) ;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            if((int)arg == 1)
            {
                displayMaze(viewModel.getMaze());
                btn_generateMaze.setDisable(false);
            }
            else
            {
                displaySolution(viewModel.getSolution());
                btn_solveMaze.setDisable(false);
            }
        }
    }

    @Override
    public void displayMaze(Maze maze) {
        mazeDisplayer.setMaze(maze);
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        if(characterPositionRow == maze.getGoalPosition().getRowIndex()
                && characterPositionColumn==maze.getGoalPosition().getColumnIndex() && win==false) {
            mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
            this.characterPositionRow.set(characterPositionRow + "");
            this.characterPositionColumn.set(characterPositionColumn + "");
            try {
                Stage stage = new Stage();
                stage.setTitle("Win!!!!!");
                FXMLLoader fxmlLoader = new FXMLLoader();
                Parent root = fxmlLoader.load(getClass().getResource("Win.fxml").openStream());
                Scene scene = new Scene(root, 480, 288);
                stage.setResizable(false);
                scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
                stage.show();
                WinController win__controller;
                win__controller = fxmlLoader.getController();
                win__controller.setViewModel(viewModel);
                viewModel.addObserver(win__controller);
                resetCanvas();
                viewModel.changeMusic(1);
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent windowEvent) {
                        viewModel.changeMusic(2);
                    }});
            } catch (Exception e) {

            }
             win=true;
        }
        else if(!win)
        {
            mazeDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
            this.characterPositionRow.set(characterPositionRow + "");
            this.characterPositionColumn.set(characterPositionColumn + "");
        }
    }

    @Override
    public void displaySolution(Solution solution) {
        mazeDisplayer.setSolution(solution);
    }

    public void generateMaze() {
        win=false;
        String strow = txtfld_rowsNum.getText();
        String stcol = txtfld_columnsNum.getText();
        if(!isNumeric(strow) || !isNumeric(stcol)){
            showAlert("Invalid input, please try again with numbers greater then 3.");
            return;
        }
        int row = Integer.valueOf(strow);
        int col = Integer.valueOf(stcol);
        if(row<3 || col<3){
            showAlert("Invalid input, please try again with numbers greater then 3.");
            return;
        }
        btn_generateMaze.setDisable(true);
        btn_solveMaze.setDisable(false);
        viewModel.generateMaze(row, col);
    }

    public void solveMaze() {
        if(!mazeDisplayer.isMazeNull())
        {
            btn_solveMaze.setDisable(true);
            viewModel.solveMaze();
        }
    }

    public void saveMazeWindow(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Saving Maze");
            stage.setResizable(false);
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("SaveMaze.fxml").openStream());
            Scene scene = new Scene(root, 350, 250);
            scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
            SaveMazeController saveMaze;
            saveMaze = fxmlLoader.getController();
            saveMaze.setViewModel(viewModel);
            viewModel.addObserver(saveMaze);
        } catch (Exception e) {

        }
    }

    public void loadMazeWindow(ActionEvent actionEvent) {
        try {
            win=false;
            Stage stage = new Stage();
            stage.setTitle("Load Maze");
            stage.setResizable(false);
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("LoadMaze.fxml").openStream());
            Scene scene = new Scene(root, 350, 250);
            scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
            LoadMazeController loadMaze;
            loadMaze = fxmlLoader.getController();
            loadMaze.setViewModel(viewModel);
            viewModel.addObserver(loadMaze);
        } catch (Exception e) {

        }
    }

    public void howToPlayWindow() {
        try {
            Stage stage = new Stage();
            stage.setTitle("How To Play");
            stage.setResizable(false);
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("HowToPlay.fxml").openStream());
            Scene scene = new Scene(root, 350, 250);
            scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
            HowToPlayController htp;
            htp = fxmlLoader.getController();
            htp.setViewModel(viewModel);
            viewModel.addObserver(htp);
        } catch (Exception e) {
        }
    }

    public void aboutWindow() {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setResizable(false);
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 350, 250);
            scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
            AboutController about;
            about = fxmlLoader.getController();
            about.setViewModel(viewModel);
            viewModel.addObserver(about);
        } catch (Exception e) {

        }
    }

    public void Properties(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("PropertiesController");
            stage.setResizable(false);
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Properties.fxml").openStream());
            Scene scene = new Scene(root, 450, 350);
            scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
            PropertiesController propertiesController;
            propertiesController = fxmlLoader.getController();
            propertiesController.setViewModel(viewModel);
            viewModel.addObserver(propertiesController);

        } catch (Exception e) {

        }
    }

    public void themeChoose(){
        combo_world.setOnAction(event -> {
            String value = combo_world.getValue();
            switch (value) {
                case "Mario": setMario(); break;
                case "Rick and Morty": setRickAndMorty(); break;
                case "Hearthstone": setHearthstone(); break;
                case "Game of thrones": setGameOfThrones(); break;
                case "Dragon Ball": setDragonBall(); break;
                default: break;
            }
         });
    }

    public void setDragonBall(){
        mazeDisplayer.setImageFileNameCharacter("resources/Images/goku.png");
        mazeDisplayer.setImageFileNameWall("resources/Images/ball.png");
        mazeDisplayer.setImageFileNameTarget("resources/Images/gohan.png");
        mazeDisplayer.redrawMaze();
    }

    public void setRickAndMorty(){
        mazeDisplayer.setImageFileNameCharacter("resources/Images/rick.png");
        mazeDisplayer.setImageFileNameWall("resources/Images/portal.png");
        mazeDisplayer.setImageFileNameTarget("resources/Images/morty.png");
        mazeDisplayer.redrawMaze();
    }

    public void setMario(){
        mazeDisplayer.setImageFileNameCharacter("resources/Images/mario.jpg");
        mazeDisplayer.setImageFileNameWall("resources/Images/block.png");
        mazeDisplayer.setImageFileNameTarget("resources/Images/peach.png");
        mazeDisplayer.redrawMaze();

    }

    public void setGameOfThrones(){
        mazeDisplayer.setImageFileNameCharacter("resources/Images/jon.png");
        mazeDisplayer.setImageFileNameWall("resources/Images/ww.jpg");
        mazeDisplayer.setImageFileNameTarget("resources/Images/dani.png");
        mazeDisplayer.redrawMaze();
    }

    public void setHearthstone(){
        mazeDisplayer.setImageFileNameCharacter("resources/Images/mage.png");
        mazeDisplayer.setImageFileNameWall("resources/Images/card.png");
        mazeDisplayer.setImageFileNameTarget("resources/Images/gold.png");
        mazeDisplayer.redrawMaze();
    }
    //@@@@@@@@@@@@@@@@@@@@@

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        if(!mazeDisplayer.isMazeNull()){
            viewModel.moveCharacter(keyEvent.getCode());
            keyEvent.consume();
        }
    }

    public void setResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
                mazeDisplayer.setWidth((double) newSceneWidth - 200);
                mazeDisplayer.redrawMaze1(1, newSceneWidth.doubleValue() - 200);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
                mazeDisplayer.setHeight((double) newSceneHeight-50 );
                mazeDisplayer.redrawMaze1(0, (double) newSceneHeight-50 );

            }
        });

        // EventHandler<javafx.scene.input.MouseEvent> mouseHandler = new EventHandler<javafx.scene.input.MouseEvent>() {

      //  @Override
       // public void handle(javafx.scene.input.MouseEvent event)
        //{


          //  System.out.println(event.getX());
           // System.out.println(event.getY());



            //}
        };

        // scene.setOnMouseClicked(mouseHandler);
         //scene.setOnMouseDragged(mouseHandler);
        // scene.setOnMouseEntered(mouseHandler);
    private boolean isNumeric(String str){
       try
            {
                Integer.parseInt( str );
                return true;
            }
            catch( Exception e )
            {
                return false;
            }
    }

    public void resetCanvas()
    {
        mazeDisplayer.resetCanvas();
        btn_solveMaze.setDisable(true);
    }

    public void exitApp()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // ... user chose OK
            // Close program
            viewModel.stopServers();
            System.exit(0);
        }

    }

    public void disableMussic(){
        viewModel.changeMusic(0);
    }

    //region String Property for Binding
    public StringProperty characterPositionRow = new SimpleStringProperty();
    public StringProperty characterPositionColumn = new SimpleStringProperty();
    public StringProperty steps = new SimpleStringProperty();

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public StringProperty steps() {return characterPositionRow;}

    public String getsteps() {
        return steps.get();
    }

    public StringProperty characterPositionRowProperty() {
        return characterPositionRow;
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    public StringProperty characterPositionColumnProperty() {
        return characterPositionColumn;
    }


    //endregion

}
