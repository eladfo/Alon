package View;

import ViewModel.MyViewModel;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
        viewModel.saveMaze(mazeName);
        Stage stage = (Stage) btn_saveMaze.getScene().getWindow();
        stage.close();

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
