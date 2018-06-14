package View;

import ViewModel.MyViewModel;

import java.util.Observable;
import java.util.Observer;

public class Win_Window_Controller implements Observer
{
    private MyViewModel viewModel;

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }


    @Override
    public void update(Observable o, Object arg) {

    }
}
