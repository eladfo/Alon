package View;

import ViewModel.MyViewModel;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class SaveMazeController implements Observer {

    private MyViewModel viewModel;
    public javafx.scene.control.TextField txtfld_mazeName;
    public javafx.scene.control.Button btn_saveMaze;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void saveMaze()
    {
        String mazeName = txtfld_mazeName.getText();
        if(mazeName.equals("")){
            showAlert("Empty maze name, please try again.");
            return;
        }
        viewModel.saveMaze(mazeName);
        Stage stage = (Stage) btn_saveMaze.getScene().getWindow();
        stage.close();
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    public void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(alertMessage);
        alert.show();
    }
    public void save()
    {
        String path;
        FileChooser file_chooser = new FileChooser();
        File file = file_chooser.showSaveDialog(null);
        if(file != null)
        {
            viewModel.saveMaze1(file.getPath());
        }
    }
}
