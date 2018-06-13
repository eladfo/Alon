package View;

import ViewModel.MyViewModel;
import javafx.stage.Stage;
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
        viewModel.loadMaze(mazeName);
        Stage stage = (Stage) btn_loadMaze.getScene().getWindow();
        stage.close();

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
