import WorldBuilder.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.Dice;
import utils.SQLite;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class NewSectorController {

    public TextField sectorName;
    public TextField yLoc;
    public TextField xLoc;
    public TextField zLoc;
    public RadioButton none;
    public ToggleGroup pop;
    public RadioButton research;
    public RadioButton colonies;
    public RadioButton populated;
    public Button createBtn;
    public Button cancelBtn;
    public Label nameLabel;
    public Label locLabel;
    public HBox locBox;
    public Label popLabel;
    public HBox radioBox1;
    public HBox radioBox2;
    public HBox btnBox;
    public VBox mainBox;

    /**
     * Initializes the sector creating stage
     */
    public void initialize() {
        //find and set example name
        String name = findName();
        sectorName.setText(name);

        // Create btn
        createBtn.setOnMouseClicked(event -> {
            if (checkSector().equalsIgnoreCase("Clear")) {
                createSector();
            } else {
                //todo error checking
            }
        });
        // Cancel btn
        cancelBtn.setOnMouseClicked(event -> {
            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.close();
        });
    }

    /**
     * Searches a text file of names and randomly picks one and returns it.
     */
    private String findName(){
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream("StarNamesPrefix.txt");
        assert is != null;
        Scanner in = new Scanner(is);
        int roll= Dice.Roller(1,1000);
        int i = 1;
        String line = in.nextLine();
        String name = "404";
        while (in.hasNext()){
            if (i==roll){
                name = line.replace(i+" ","");
                break;
            } else {
                i++;
                line = in.nextLine();
            }
        }
        return name;
    }

    /**
     * Checks to make sure inputted values are valid.
     */
    private String checkSector() {
        return "Clear";
    }

    /**
     * Creates the new sector
     */
    private void createSector() {
        Sector.Population population;
        if (none.isSelected()) {
            population = Sector.Population.NONE;
        } else if (research.isSelected()){
            population = Sector.Population.RESEARCH;
        } else if (colonies.isSelected()) {
            population = Sector.Population.COLONIES;
        } else {
            population = Sector.Population.POPULATED;
        }
        int x = Integer.parseInt(xLoc.getText());
        int y = Integer.parseInt(yLoc.getText());
        int z = Integer.parseInt(zLoc.getText());
        Sector sector = new Sector(sectorName.getText(), population, x, y, z);
        //Close window
//        Stage stage = (Stage) cancelBtn.getScene().getWindow();
//        stage.close();
        nameLabel.setText("Adding " + sector.getName() + " to database\nPlease wait.");
        mainBox.getChildren().clear();
        mainBox.getChildren().add(nameLabel);
        ScrollPane scroll = new ScrollPane();
        scroll.setMinWidth(400);
        scroll.setMaxWidth(400);
        scroll.setMinHeight(200);
        scroll.setMaxHeight(200);
        VBox box = new VBox();
        box.getChildren().add(new Label("Adding to database"));
        scroll.setContent(box);
        mainBox.getChildren().add(scroll);
        new Thread( () -> addEntry(sector,box,scroll)).start();
        //Open logging window

    }

    /**
     * Adds a new entry to the database. If its an sector or system it adds the entries within to the database.
     * @param entry GalaxyDataBaseItem to be added.
     */
    public void addEntry(GalaxyDataBaseItem entry, VBox vBox, ScrollPane scroll){
        ArrayList<String> sqls = new ArrayList<>();
        sqls.add(entry.getSQLInsert());
        SQLite.AddRecord("Galaxy",sqls,entry.getTableNames());
        Platform.runLater(() -> {
            mainBox.getChildren().remove(scroll);
            Label text = new Label(entry.getTableNames() + ": " + entry.getName() + " added!");
            vBox.getChildren().add(text);
            scroll.setContent(vBox);
            scroll.setVvalue(1.0);
            mainBox.getChildren().add(scroll);
        });
        if (entry instanceof Sector) {
            Sector sector = (Sector) entry;
            for (int i = 0; i < sector.getGrid().length; i++) {
                for (int j = 0; j < sector.getGrid()[i].length; j++) {
                    if (sector.getGrid()[i][j] != null) {
                        addEntry(sector.getGrid()[i][j],vBox, scroll);
                    }
                }
            }
            Platform.runLater(() -> {
                cancelBtn.setText("Done");
                mainBox.getChildren().add(cancelBtn);
            });
        } else if (entry instanceof StarSystem) {
            StarSystem system = (StarSystem) entry;
            for (Star s:system.getStars()) {
                addEntry(s,vBox, scroll);
            }
            for (Body b:system.getOrderSystem()){
                addEntry(b,vBox, scroll);
            }
        }
    }
}
