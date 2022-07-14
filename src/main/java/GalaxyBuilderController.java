import WorldBuilder.GalaxyDataBase;
import WorldBuilder.GalaxyViewer;
import WorldBuilder.Sector;
import WorldBuilder.StarSystem;
import com.sun.javafx.geom.Rectangle;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class GalaxyBuilderController {

    public MenuItem menuNew;
    public MenuItem menuExit;
    public Label leftLabel;
    public Button leftBtn;
    public ScrollPane leftList;
    public Label rightLabel;
    public ScrollPane rightList;
    //public TextArea mainTxt;
    public Pane viewPane;
    private String sector;
    private String system;

    /**
     * Initialize the fresh app
     */
    public void initialize() {
        leftLabel.setText("Sectors");
        leftBtn.setText("New");
        leftBtn.setOnMouseClicked(event -> newClick());
        //mainTxt.setText("Pick a sector to view");
        ArrayList<Sector> sectors = GalaxyViewer.getSectors();
        VBox list = new VBox();
        for (Sector sector: sectors) {
            Label name = new Label();
            name.setText(sector.getName());
            name.setOnMouseClicked(event -> sectorClick(event.getSource().toString()));
            list.getChildren().add(name);
        }
        setView(sectors);
        leftList.setContent(list);
        rightLabel.setText("");
        rightList.setContent(null);
    }

    private void setView(ArrayList<Sector> sectors) {

        Group root = new Group();

        Sphere star = new Sphere(50);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.YELLOW);
        star.setMaterial(material);
        root.getChildren().add(star);

        final int width = 789;
        final int height = 747;
        SubScene viewer = new SubScene(root,width,height);
        viewer.setFill(Color.BLACK);

        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setTranslateX(-width/2.0);
        camera.setTranslateY(-height/2.0);
        camera.setTranslateZ(0);
        viewer.setCamera(camera);

        viewPane.getChildren().add(viewer);

    }

    /**
     * Event handler for clicking on a sector name
     * @param source event source string
     */
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
        //mainTxt.setText("Select a System");
    }

    /**
     * Event handler for clicking on a System name
     * @param source event source
     */
    private void systemClick(String source) {
        // set left, if not already changed
        if (leftLabel.getText().equalsIgnoreCase("Sectors")) {
            leftBtn.setText("Back");
            leftBtn.setOnMouseClicked(event -> initialize());
            leftLabel.setText(rightLabel.getText());
            leftList.setContent(rightList.getContent());
        }
        // set center txt
        String system = source.split("'")[1].replace("'","");
        StarSystem sys = GalaxyDataBase.findSystem(system);
        //mainTxt.setText("Name: " + sys.getName() + "\n" +
                //"Number of Stars: " + sys.getStars().length + "\n" +
                //"Size: " + sys.getSize() + " AU \n" +
                //"Habitable Zone: (" + sys.getHabitZone()[0] + " - " + sys.getHabitZone()[1] + ")\n");

        // set right
        rightLabel.setText(system);
        VBox rList = new VBox();
        for (int i = 0; i < sys.getOrderSystem().size(); i++) {
            Label name = new Label(sys.getOrderSystem().get(i).getName());
            name.setId(system + ":" + i);
            name.setOnMouseClicked(event -> bodyClick(event.getSource().toString()));
            rList.getChildren().add(name);
        }
        rightList.setContent(rList);
    }

    /**
     * Event handler for clicking on a system body
     * @param source event source
     */
    private void bodyClick(String source) {
        String values = source.substring(source.indexOf("[") + 1, source.indexOf("]"))
                .split(",")[0]
                .split("=")[1];
        int idx = Integer.parseInt(values.split(":")[1]);
        String system = values.split(":")[0];
        StarSystem sys = GalaxyDataBase.findSystem(system);
        //mainTxt.setText(sys.getOrderSystem().get(idx).toString());
    }

    /**
     * Opens a new window to create a new Sector
     */
    private void newClick() {
        FXMLLoader fxmlLoader = new FXMLLoader(GalaxyBuilderApp.class.getResource("newSector.fxml"));
        try {
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 640, 400);
            stage.setTitle("Create new Sector");
            stage.setScene(scene);
            stage.setOnHidden(event -> initialize());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
