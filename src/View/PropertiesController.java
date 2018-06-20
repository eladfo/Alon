package View;

import ViewModel.MyViewModel;
import java.util.Observable;
import java.util.Observer;

public class PropertiesController implements Observer
{
    private MyViewModel viewModel;
    public javafx.scene.control.Label lbl_Kind_of_genarate;
    public javafx.scene.control.Label lbl_Kind_of_algo;
    public javafx.scene.control.Label lbl_size_of_pool;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);

    }

    private void bindProperties(MyViewModel viewModel)
    {
        lbl_Kind_of_genarate.textProperty().bind(viewModel.lbl_Kind_of_genarate);
        lbl_Kind_of_algo.textProperty().bind(viewModel.lbl_Kind_of_algo);
        lbl_size_of_pool.textProperty().bind(viewModel.lbl_size_of_pool) ;
        viewModel.print();
    }

    @Override
    public void update(Observable o, Object arg)
    {


    }
}
