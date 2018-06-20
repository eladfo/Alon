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
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;

import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class MyViewController implements Observer, IView {

    @FXML
    private MyViewModel viewModel;
    private Stage stage;
    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    public javafx.scene.control.Label lbl_num_of_steps;
    public javafx.scene.control.ComboBox<String> combo_world;
    public javafx.scene.control.ToggleButton tglbtn_music;

    private double hight_press;
    private double width_press;
    private boolean flag;
    private boolean win=false;
    private double ofset_hight=0;
    private double ofset_width=0;
    private boolean initial =false;
    private int x_old;
    private int y_old;
    private int songPlayingNum;


    public void setViewModel(MyViewModel viewModel, Stage stage) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
        this.stage = stage;
        this.songPlayingNum = 0;
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
            else if((int)arg == 2)
            {
                displaySolution(viewModel.getSolution());
                btn_solveMaze.setDisable(false);
            }
            else if((int)arg == 3)
            {
                displayMaze(viewModel.getMaze());
                btn_generateMaze.setDisable(false);
            }

        }
    }

    @Override
    public void displayMaze(Maze maze) {
        if(!initial) {
            mazeDisplayer.setMaze(maze);
            initial=true;
        }
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        if(characterPositionRow == maze.getGoalPosition().getRowIndex()
                && characterPositionColumn==maze.getGoalPosition().getColumnIndex() && win==false)
        {
            mazeDisplayer.setCharPos(characterPositionRow, characterPositionColumn);
            this.characterPositionRow.set(characterPositionRow + "");
            this.characterPositionColumn.set(characterPositionColumn + "");
            try {
                viewModel.steps = 0;
                Stage stage = new Stage();
                stage.setTitle("!!! Victory !!!");
                initial=false;
                FXMLLoader fxmlLoader = new FXMLLoader();
                Parent root = fxmlLoader.load(getClass().getResource("Win.fxml").openStream());
                Scene scene = new Scene(root, 590, 328);
                stage.setResizable(false);
                scene.getStylesheets().add(getClass().getResource("/ViewStyle.css").toExternalForm());
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
                stage.show();
                WinController win__controller;
                win__controller = fxmlLoader.getController();
                win__controller.setViewModel(viewModel);
                viewModel.addObserver(win__controller);
                resetCanvas();
                if(!tglbtn_music.isSelected())
                    viewModel.changeWinMusic(0);
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent windowEvent) {
                        if(!tglbtn_music.isSelected())
                        {
                            viewModel.changeWinMusic(1);
                        }
                        else
                        {
                            viewModel.changeWinMusic(2);
                        }
                    }});
            } catch (Exception e) {
            }
             win=true;
             viewModel.win();
        }
        else if(!win)
        {
            mazeDisplayer.setCharPos(characterPositionRow, characterPositionColumn);
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
        initial = false;
        steps.set("0");
        viewModel.steps=0;
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
        combo_world.setDisable(false);
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
        FileChooser file_chooser = new FileChooser();
        file_chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze File","*.maze"));
        File file = file_chooser.showSaveDialog(null);
        if(file!= null && !mazeDisplayer.isMazeNull())
        {
            viewModel.saveMaze1(file.getPath());
        }
        else
        {
            if (file == null){
                return;}
            else
            {
                showAlert("Maze does not exist. Try generate a maze before saving.");
                return;
            }
        }
    }

    public void loadMazeWindow(ActionEvent actionEvent) {
        FileChooser file_chooser = new FileChooser();
        file_chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Maze Files","*.maze"));
        File file = file_chooser.showOpenDialog(null);
        if(file != null)
        {
            win = false;
            initial = false;
            viewModel.loadMaze1(file.getPath());
        }
    }

    public void howToPlayWindow() {
        try {
            Stage stage = new Stage();
            stage.setTitle("How To Play");
            stage.setResizable(false);
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("HowToPlay.fxml").openStream());
            Scene scene = new Scene(root, 896, 494);
            scene.getStylesheets().add(getClass().getResource("/ViewStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
            viewModel.changeMusic(6);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent windowEvent) {
                    viewModel.changeMusic(songPlayingNum);
                    isTglBtnMusicOn();
                }});
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
            Scene scene = new Scene(root, 896, 494);
            scene.getStylesheets().add(getClass().getResource("/ViewStyle.css").toExternalForm());
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
            scene.getStylesheets().add(getClass().getResource("/ViewStyle.css").toExternalForm());
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
        String value1 = combo_world.getValue();
        switch (value1) {
            case "Mario": setMario(); break;
            case "Rick and Morty": setRickAndMorty(); break;
            case "Mage": setMage(); break;
            case "Rogue": setRogue(); break;
            case "Game of thrones": setGameOfThrones(); break;
            case "Dragon Ball": setDragonBall(); break;
            default: break;
        }
    }

    private void setDragonBall(){
        songPlayingNum = 3;
        viewModel.changeMusic(3);
        isTglBtnMusicOn();
        mazeDisplayer.setImageFileNameCharacter("/Images/goku.png");
        mazeDisplayer.setImageFileNameWall("/Images/ball.png");
        mazeDisplayer.setImageFileNameTarget("/Images/gohan.png");
        mazeDisplayer.redrawMaze_offset(0,0);
    }

    private void setRickAndMorty(){
        songPlayingNum = 1;
        viewModel.changeMusic(1);
        isTglBtnMusicOn();
        mazeDisplayer.setImageFileNameCharacter("/Images/rick.png");
        mazeDisplayer.setImageFileNameWall("/Images/portal.png");
        mazeDisplayer.setImageFileNameTarget("/Images/morty.png");
        mazeDisplayer.redrawMaze_offset(0,0);
    }

    private void setMario(){
        songPlayingNum = 4;
        viewModel.changeMusic(4);
        isTglBtnMusicOn();
        mazeDisplayer.setImageFileNameCharacter("/Images/mario.jpg");
        mazeDisplayer.setImageFileNameWall("/Images/block.png");
        mazeDisplayer.setImageFileNameTarget("/Images/peach.png");
        mazeDisplayer.redrawMaze_offset(0,0);

    }

    private void setGameOfThrones(){
        songPlayingNum = 2;
        viewModel.changeMusic(2);
        isTglBtnMusicOn();
        mazeDisplayer.setImageFileNameCharacter("/Images/jon.png");
        mazeDisplayer.setImageFileNameWall("/Images/ww.jpg");
        mazeDisplayer.setImageFileNameTarget("/Images/dani.png");
        mazeDisplayer.redrawMaze_offset(0,0);
    }

    private void setMage(){
        if (songPlayingNum != 0)
        {
            viewModel.changeMusic(0);
            songPlayingNum = 0;
        }
        isTglBtnMusicOn();
        mazeDisplayer.setImageFileNameCharacter("/Images/mage.png");
        mazeDisplayer.setImageFileNameWall("/Images/card2.png");
        mazeDisplayer.setImageFileNameTarget("/Images/coin1.png");
        mazeDisplayer.redrawMaze_offset(0,0);
    }

    private void setRogue(){
        if (songPlayingNum != 0)
        {
            viewModel.changeMusic(0);
            songPlayingNum = 0;
        }
        isTglBtnMusicOn();
        mazeDisplayer.setImageFileNameCharacter("/Images/rogue.png");
        mazeDisplayer.setImageFileNameWall("/Images/card4.png");
        mazeDisplayer.setImageFileNameTarget("/Images/roguecoin.png");
        mazeDisplayer.redrawMaze_offset(0,0);
    }

    public void exitFS(){
        stage.setFullScreen(false);
        mazeDisplayer.redrawMaze_offset(0,0);

    }

    public void goFS(){
        stage.setFullScreen(true);
        mazeDisplayer.redrawMaze_offset(0,0);
    }

    public void onOffMusic(ActionEvent actionEvent){
        if (((ToggleButton)actionEvent.getSource()).isSelected()){
            tglbtn_music.setText("Enable Music");
            viewModel.changeMusic(111);
        }
        else {
            tglbtn_music.setText("Disable Music");
            viewModel.changeMusic(100);
        }
    }

    private void isTglBtnMusicOn(){
        if(tglbtn_music.isSelected())
        {
            viewModel.changeMusic(111);
        }
        else
        {
            viewModel.changeMusic(100);
        }
    }

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
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mazeDisplayer.setWidth((double) newSceneWidth - 175);
                mazeDisplayer.setMaze(mazeDisplayer.maze);
                mazeDisplayer.redrawMaze_offset(0,0);
                mazeDisplayer.set();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mazeDisplayer.setHeight((double) newSceneHeight - 45);
                mazeDisplayer.setMaze(mazeDisplayer.maze);
                mazeDisplayer.redrawMaze_offset(0,0);
                mazeDisplayer.set();
            }
        });
        EventHandler<javafx.scene.input.MouseEvent> mouseHandler = new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {
                if(event.getEventType().getName()=="MOUSE_PRESSED")
                {
                    hight_press = event.getY();
                    width_press = event.getX();
                    x_old = found_cordinate_row(hight_press,ofset_hight,ofset_width);
                    y_old = found_cordinate_col(width_press,ofset_hight,ofset_width);
                    double hight_player = mazeDisplayer.get_player_position_hight(ofset_hight,ofset_width);
                    double width_player = mazeDisplayer.get_player_position_width(ofset_hight,ofset_width);
                    double hight_click = hight_press ;
                    double width_click = width_press ;
                    if(hight_click >= hight_player && hight_click <= hight_player+mazeDisplayer.getCellHeight() )
                        if(width_click >= width_player && width_click<= width_player+mazeDisplayer.getCellWidth())
                    {
                        //System.out.println("I touch in the image!!!!");
                        flag = true;
                    }
                }
                if(flag==false && mazeDisplayer.maze!=null && !all_the_picture_draw(mazeDisplayer.getCellWidth(),mazeDisplayer.getCellHeight()) && event.getEventType().getName()=="MOUSE_DRAGGED" )
                {
                    if(event.getX()>= 175 && event.getX() <=mazeDisplayer.getWidth()+175 && event.getY()>= 25 && event.getY() <=mazeDisplayer.getHeight()+25 ) {
                        int x = found_cordinate_row(event.getY(),ofset_hight,ofset_width);
                        int y = found_cordinate_col(event.getX(),ofset_hight,ofset_width);
                        if (x > x_old )
                        {
                            if(mazeDisplayer.last_offset_height + ofset_hight + mazeDisplayer.getCellHeight()>25 ) {
                                return;
                            }
                           // System.out.println("down!");
                            mazeDisplayer.redrawMaze_offset( ofset_hight + mazeDisplayer.getCellHeight() ,  ofset_width);
                            x_old=x;
                            ofset_hight= ofset_hight + mazeDisplayer.getCellHeight();
                        }
                        else if (x < x_old)
                        {
                            if( mazeDisplayer.last_offset_height + ofset_hight - mazeDisplayer.getCellHeight() + mazeDisplayer.maze.getM_rows() * mazeDisplayer.cellHeight <0) {
                                return;
                            }
                            //System.out.println("up!");
                            mazeDisplayer.redrawMaze_offset(ofset_hight - mazeDisplayer.getCellHeight() ,  ofset_width);
                            x_old=x;
                            ofset_hight= ofset_hight -  mazeDisplayer.getCellHeight();
                        }
                        else if (y > y_old)
                        {
                            //System.out.println("right!");
                            if(mazeDisplayer.last_offset_width + ofset_width-mazeDisplayer.getCellWidth()>0){
                                return;
                            }
                            mazeDisplayer.redrawMaze_offset( ofset_hight ,ofset_width + mazeDisplayer.getCellWidth());
                            y_old=y;
                            ofset_width = ofset_width + mazeDisplayer.getCellWidth();
                        }
                        else if (y < y_old)
                        {
                           // System.out.println("left!");
                           // System.out.println(mazeDisplayer.getWidth());
                           // System.out.println(mazeDisplayer.last_offset_width + ofset_width-mazeDisplayer.getCellWidth() + mazeDisplayer.maze.getM_columns() * mazeDisplayer.cellWidth );
                            if( mazeDisplayer.last_offset_width + ofset_width-mazeDisplayer.getCellWidth() + mazeDisplayer.maze.getM_columns() * mazeDisplayer.cellWidth < 0 /*mazeDisplayer.getWidth()-200*/ || mazeDisplayer.last_offset_width + ofset_width-mazeDisplayer.getCellWidth() + mazeDisplayer.maze.getM_columns() * mazeDisplayer.cellWidth < -175) {
                               return;}
                            mazeDisplayer.redrawMaze_offset(ofset_hight ,  ofset_width-mazeDisplayer.getCellWidth());
                            y_old=y;
                            ofset_width= ofset_width -  mazeDisplayer.getCellWidth();
                        }
                    }
                }
                if(event.getEventType().getName()=="MOUSE_RELEASED") {
                    flag = false;
                }
                if(flag) {
                    if (event.getX() >= 175 && event.getX() <= mazeDisplayer.getWidth() + 175 && event.getY() >= 25 && event.getY() <= mazeDisplayer.getHeight() + 25) {
                        int x = found_cordinate_row(event.getY(),ofset_width,ofset_hight);
                        int y = found_cordinate_col(event.getX(),ofset_width,ofset_hight);
                        if (mazeDisplayer.getCharPosRow(ofset_width,ofset_hight) == x || mazeDisplayer.getCharPosRow(ofset_width,ofset_hight) + 1 == x || mazeDisplayer.getCharPosRow(ofset_width,ofset_hight) - 1 == x && mazeDisplayer.getCharPosCol(ofset_width,ofset_hight) == y || mazeDisplayer.getCharPosCol(ofset_width,ofset_hight) + 1 == y || mazeDisplayer.getCharPosCol(ofset_width,ofset_hight) - 1 == y)
                            if (mazeDisplayer.is_free(x, y)) {
                                if (mazeDisplayer.getCharPosRow(ofset_width,ofset_hight) == x - 1 && mazeDisplayer.getCharPosCol(ofset_width,ofset_hight) == y) {
                                    viewModel.moveCharacterByMouse(3);
                                } else if (mazeDisplayer.getCharPosRow(ofset_width,ofset_hight) == x && mazeDisplayer.getCharPosCol(ofset_width,ofset_hight) == y - 1) {
                                    viewModel.moveCharacterByMouse(2);
                                } else if (mazeDisplayer.getCharPosRow(ofset_width,ofset_hight) == x + 1 && mazeDisplayer.getCharPosCol(ofset_width,ofset_hight) == y) {
                                    viewModel.moveCharacterByMouse(1);
                                } else if (mazeDisplayer.getCharPosRow(ofset_width,ofset_hight) == x && mazeDisplayer.getCharPosCol(ofset_width,ofset_hight) == y + 1) {
                                    viewModel.moveCharacterByMouse(4);
                                } else if (mazeDisplayer.getCharPosRow(ofset_width,ofset_hight) == x + 1 && mazeDisplayer.getCharPosCol(ofset_width,ofset_hight) == y - 1) {
                                    viewModel.moveCharacterByMouse(5);
                                } else if (mazeDisplayer.getCharPosRow(ofset_width,ofset_hight) == x - 1 && mazeDisplayer.getCharPosCol(ofset_width,ofset_hight) == y - 1) {
                                    viewModel.moveCharacterByMouse(6);
                                } else if (mazeDisplayer.getCharPosRow(ofset_width,ofset_hight) == x - 1 && mazeDisplayer.getCharPosCol(ofset_width,ofset_hight) == y + 1) {
                                    viewModel.moveCharacterByMouse(7);
                                } else if (mazeDisplayer.getCharPosRow(ofset_width,ofset_hight) == x + 1 && mazeDisplayer.getCharPosCol(ofset_width,ofset_hight) == y + 1) {
                                    viewModel.moveCharacterByMouse(8);
                                }
                            }
                    }
                }
            }
            };
        EventHandler<javafx.scene.input.ScrollEvent> mouseHandler1 = new EventHandler<javafx.scene.input.ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if(event.isControlDown())
                {
                    if(event.getTextDeltaY()>0) {
                        //System.out.println("Zoom in!");
                        ofset_hight=0;
                        ofset_width=0;
                        mazeDisplayer.redrawMaze_zoom(1);
                    }
                    else {
                        //System.out.println("Zoom out!");
                        ofset_hight=0;
                        ofset_width=0;
                        mazeDisplayer.redrawMaze_zoom(-1);
                    }
                }
            }
        };
        scene.setOnScroll(mouseHandler1);
        scene.setOnScrollFinished(mouseHandler1);
        scene.setOnScrollStarted(mouseHandler1);
        scene.setOnMouseClicked(mouseHandler);
        scene.setOnMouseDragged(mouseHandler);
        scene.setOnMousePressed(mouseHandler);
        scene.setOnMouseReleased(mouseHandler);
    }

    private boolean all_the_picture_draw(double w , double h)
    {
        return (w*mazeDisplayer.maze.getM_columns() <=  stage.getWidth()) &&  (h*mazeDisplayer.maze.getM_rows() <= stage.getHeight()) ;
    }

    private  int found_cordinate_row(double value,double ofset_width,double ofset_hight)
    {
       int res=0;
       double temp = 25 + ofset_hight;
       boolean found =false;
       while(temp<mazeDisplayer.getHeight() +25 && found == false)
       {

           if(value>=temp && value<= temp+mazeDisplayer.getCellHeight())
               break;
           res++;
           temp = temp+mazeDisplayer.getCellHeight();
       }
       return res;
    }

    private  int found_cordinate_col(double value,double ofset_width,double ofset_hight)
    {
        int res=0;
        double temp = 175 + ofset_width;
        boolean found =false;

        while(temp<mazeDisplayer.getWidth()+175 && found == false)
        {
            if(value>=temp && value<= temp+mazeDisplayer.getCellWidth())
                break;
            res++;
            temp=temp+mazeDisplayer.getCellWidth();
        }
        return res;
    }

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

    //region String Property for Binding
    public StringProperty characterPositionRow = new SimpleStringProperty();
    public StringProperty characterPositionColumn = new SimpleStringProperty();
    public StringProperty steps = new SimpleStringProperty();

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    //endregion

}
