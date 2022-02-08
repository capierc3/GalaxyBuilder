import WorldBuilder.GalaxyDataBase;
import WorldBuilder.GalaxyViewer;
import WorldBuilder.Sector;
import WorldBuilder.StarSystem;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class GalaxyBuilderController {

    public MenuItem menuNew;
    public MenuItem menuExit;
    public Label leftLabel;
    public Button leftBtn;
    public ScrollPane leftList;
    public Label rightLabel;
    public ScrollPane rightList;
    public TextArea mainTxt;

    private String sector;
    private String system;
    private int index;

    public void initialize() {
        leftLabel.setText("Sectors");
        leftBtn.setText("New");
        ArrayList<Sector> sectors = GalaxyViewer.getSectors();
        VBox list = new VBox();
        for (Sector sector: sectors) {
            Label name = new Label();
            name.setText(sector.getName());
            name.setOnMouseClicked(event -> sectorClick(event.getSource().toString()));
            list.getChildren().add(name);
        }
        leftList.setContent(list);
        rightLabel.setText("");
        rightList.setContent(null);
    }

    public void sectorClick(String source) {
        sector = source.split("'")[1].replace("'","");
        ArrayList<String> systemNames = GalaxyViewer.getSystemNames(sector);
        rightLabel.setText(sector);
        VBox list = new VBox();
        for (String systemName: systemNames) {
            system = GalaxyDataBase.findSystem(systemName).getName();
            Label name = new Label();
            name.setText(system);
            name.setOnMouseClicked(event -> systemClick(event.getSource().toString()));
            list.getChildren().add(name);
        }
        rightList.setContent(list);
    }

    private void systemClick(String source) {
        // set left
        leftBtn.setText("Back");
        leftBtn.setOnMouseClicked(event -> initialize());
        ArrayList<String> systemNames = GalaxyViewer.getSystemNames(sector);
        leftLabel.setText(sector);
        VBox lList = new VBox();
        for (String systemName: systemNames) {
            system = GalaxyDataBase.findSystem(systemName).getName();
            Label name = new Label();
            name.setText(system);
            name.setOnMouseClicked(event -> systemClick(event.getSource().toString()));
            lList.getChildren().add(name);
        }
        leftList.setContent(lList);

        // set right
        String system = source.split("'")[1].replace("'","");
        rightLabel.setText(system);
        StarSystem sys = GalaxyDataBase.findSystem(system);
        VBox rList = new VBox();
        for (int i = 0; i < sys.getOrderSystem().size(); i++) {
            Label name = new Label(sys.getOrderSystem().get(i).getName());
            name.setId(system + ":" + i);
            name.setOnMouseClicked(event -> bodyClick(event.getSource().toString()));
            rList.getChildren().add(name);
        }
        rightList.setContent(rList);
    }

    private void bodyClick(String source) {
        String values = source.substring(source.indexOf("[") + 1, source.indexOf("]"))
                .split(",")[0]
                .split("=")[1];
        int idx = Integer.parseInt(values.split(":")[1]);
        String system = values.split(":")[0];
        StarSystem sys = GalaxyDataBase.findSystem(system);
        mainTxt.setText(sys.getOrderSystem().get(idx).toString());
    }
}
