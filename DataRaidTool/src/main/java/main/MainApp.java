package main;

import controller.PersonEditDialogController;
import controller.PersonOverviewController;
import domain.Champion;
import domain.ChampionKeypoints;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.ASIFTMatchingExample;
import utils.CopyImagetoClipBoard;
import utils.FileManagement;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class MainApp extends Application implements Initializable {

    private Stage primaryStage;
    private BorderPane rootLayout;
    Map<String, ChampionKeypoints> hashChampionKeysPoints;

    PersonOverviewController personOverviewController;

    /**
     * The data as an observable list of Persons.
     */
    private ObservableList<Champion> championData = FXCollections.observableArrayList();

    /**
     * Constructor
     */
    public MainApp() throws IOException {
        // Add some sample data
        List<Champion> championList = FileManagement.generateChampionList();

        String[] championNames = ASIFTMatchingExample.getChampionList("images");
//        hashChampionKeysPoints = ASIFTMatchingExample.generateChampionsKeyPoints(championNames);

        hashChampionKeysPoints = ASIFTMatchingExample.getHashKeypointsDeserializer();

        for (Champion p : championList) {
            championData.add(p);
        }
    }

    /**
     * Returns the data as an observable list of Persons.
     * @return
     */
    public ObservableList<Champion> getChampionData() {
        return championData;
    }

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");

        // Set the application icon.
//        this.primaryStage.getIcons().add(new Image(MainApp.class.getResourceAsStream(("/images/file_32.png"))));

        initRootLayout();

        showPersonOverview();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);

            KeyCombination keyCombinationWin = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
            scene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
                if (keyCombinationWin.match((javafx.scene.input.KeyEvent) event)) {
                    CopyImagetoClipBoard.copyPortionImage();
                    try {
                        //TODO seleccionar personaje
                        String championName = ASIFTMatchingExample.compareClipboardImageByChampionRepositoryImages(hashChampionKeysPoints);
                        System.out.println(championName);
                        //remove ext
                        personOverviewController.refreshImage(championName.substring(0, championName.lastIndexOf('.')));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            scene.setOnKeyPressed(ke -> {
                KeyCode keyCode = ke.getCode();
                if (keyCode.equals(KeyCode.A) && keyCode.equals(KeyCode.CONTROL)) {
                    CopyImagetoClipBoard.copyPortionImage();
                }
            });
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showPersonOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/PersonOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);

            // Give the controller access to the main app.
            personOverviewController = loader.getController();
            personOverviewController.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     *
     * @param champion the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showPersonEditDialog(Champion champion) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/PersonEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setChampion(champion);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return
     */
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     *
     * @param file the file or null to remove the path
     */
    public void setPersonFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
            primaryStage.setTitle("AddressApp - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Update the stage title.
            primaryStage.setTitle("AddressApp");
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public void setRootLayout(BorderPane rootLayout) {
        this.rootLayout = rootLayout;
    }

    public Map<String, ChampionKeypoints> getHashChampionKeysPoints() {
        return hashChampionKeysPoints;
    }

    public void setHashChampionKeysPoints(Map<String, ChampionKeypoints> hashChampionKeysPoints) {
        this.hashChampionKeysPoints = hashChampionKeysPoints;
    }

    public void setChampionData(ObservableList<Champion> championData) {
        this.championData = championData;
    }
}
