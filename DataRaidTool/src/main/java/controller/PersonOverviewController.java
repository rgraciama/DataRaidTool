package controller;

import domain.Champion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import main.MainApp;
import org.controlsfx.control.Notifications;
import utils.DateUtil;

import java.net.URL;
import java.util.ResourceBundle;


public class PersonOverviewController implements Initializable {
    @FXML
    private TableView<Champion> personTable;
    @FXML
    private TableColumn<Champion, String> firstNameColumn;
    @FXML
    private TableColumn<Champion, String> lastNameColumn;

    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label streetLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label birthdayLabel;
    @FXML
    private ImageView championView;

    public void refreshImage(String name) {
        String championName = name;
        if (championName=="") {
            championName = firstNameLabel.getText()==""?"Alure":firstNameLabel.getText();
            championName = championName.replace(" ","_");
        }

        championView.setImage(new Image(getClass().getResourceAsStream("../images/"+championName+".png")));
    }

    @FXML
    private void handleDeletePerson() {
        int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            personTable.getItems().remove(selectedIndex);
        } else {
            // Nothing selected.
            Notifications.create()
                    .title("No Selection")
                    .text("Please select a person in the table.")
                    .hideAfter(Duration.seconds(10))
                    .show();
        }
    }




    @FXML
    private void handlePRUEBA() {

        String filename1 = "src/main/resources/images/Armiger.png";
        String filename2 = "src/main/resources/images/armiger_small.png";


        // Nothing selected.
            Notifications.create()
                    .title("No Selection")
                    .text("Please select a person in the table.")
                    .hideAfter(Duration.seconds(10))
                    .show();
    }

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PersonOverviewController() {
    }


    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        personTable.setItems(mainApp.getChampionData());
    }

    /**
     * Fills all text fields to show details about the person.
     * If the specified person is null, all text fields are cleared.
     *
     * @param champion the person or null
     */
    private void showPersonDetails(Champion champion) {
        if (champion != null) {
            // Fill the labels with info from the person object.
            firstNameLabel.setText(champion.getName());
            lastNameLabel.setText(champion.getLastName());
            streetLabel.setText(champion.getStreet());
            postalCodeLabel.setText(Integer.toString(champion.getPostalCode()));
            cityLabel.setText(champion.getCity());

            birthdayLabel.setText(DateUtil.format(champion.getBirthday()));

            refreshImage("");
        } else {
            // Person is null, remove all the text.
            firstNameLabel.setText("");
            lastNameLabel.setText("");
            streetLabel.setText("");
            postalCodeLabel.setText("");
            cityLabel.setText("");
            birthdayLabel.setText("");
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new person.
     */
    @FXML
    private void handleNewPerson() {
        Champion tempChampion = new Champion();
        boolean okClicked = mainApp.showPersonEditDialog(tempChampion);
        if (okClicked) {
            mainApp.getChampionData().add(tempChampion);
        }
    }


    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected person.
     */
    @FXML
    private void handleEditPerson() {
        Champion selectedChampion = personTable.getSelectionModel().getSelectedItem();
        if (selectedChampion != null) {
            boolean okClicked = mainApp.showPersonEditDialog(selectedChampion);
            if (okClicked) {
                showPersonDetails(selectedChampion);
            }

        } else {
            // Nothing selected.
            Notifications.create()
                    .title("No Selection")
                    .text("Please select a person in the table.")
                    .hideAfter(Duration.seconds(10))
                    .show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the person table with the two columns.
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        // Clear person details.
        showPersonDetails(null);

        // Listen for selection changes and show the person details when changed.
        personTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPersonDetails(newValue));

        refreshImage("");
        initTable();
    }

    private void initTable() {

    }
}
