package View;

import ViewModel.MyViewModel;

import java.util.Observable;
import java.util.Observer;

public class HowToPlayController implements Observer {

    private MyViewModel viewModel;

    @Override
    public void update(Observable o, Object arg) {

    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

}
