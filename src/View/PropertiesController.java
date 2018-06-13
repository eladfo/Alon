package View;

import ViewModel.MyViewModel;

/**
 * Created by aviadd on 6/18/2017.
 */
public class PropertiesController
{
    private MyViewModel viewModel;
    public javafx.scene.control.Label lbl_Kind_of_genarate;
    public javafx.scene.control.Label lbl_Kind_of_algo;
    public javafx.scene.control.Label lbl_size_of_pool;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);

    }
    private void bindProperties(MyViewModel viewModel) {
        lbl_Kind_of_genarate.textProperty().bind(viewModel.characterPositionRow);
        lbl_Kind_of_algo.textProperty().bind(viewModel.characterPositionColumn);
        lbl_size_of_pool.textProperty().bind(viewModel.step) ;
    }

}
