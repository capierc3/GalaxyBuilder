import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GalaxyBuilderController {

    @FXML
    private Label txt;

    @FXML
    protected void onBtnClick() {
        txt.setText("It Worked!!!");
    }


}
