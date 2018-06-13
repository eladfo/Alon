package View;

import ViewModel.MyViewModel;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class LoadMazeController implements Observer {

    private MyViewModel viewModel;
    public javafx.scene.control.TextField txtfld_loadName;
    public javafx.scene.control.Button btn_loadMaze;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void loadMaze()
    {
        String mazeName = txtfld_loadName.getText();
        String tmpDir = "resources/SavedMazes/";
        String tmpFile = tmpDir + mazeName;
        File f = new File(tmpFile);
        if(mazeName.equals("")){
            showAlert("Empty maze name, please try again.");
            return;
        }
        if(!f.exists())
        {
            showAlert("Maze name does not exist, please try again.");
            return;
        }
        viewModel.loadMaze(mazeName);
        Stage stage = (Stage) btn_loadMaze.getScene().getWindow();
        stage.close();

    }

    @Override
    public void update(Observable o, Object arg) {

    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(alertMessage);
        alert.show();
    }
}
