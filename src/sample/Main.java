package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.model.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main extends Application {
    private World world;
    private Stage stage;
    private Group gridPlaceholder;
    private Spinner<Integer> spinnerWidth;
    private Spinner<Integer> spinnerHeight;
    private Timeline timeline;
    private Button[][] buttonGrid;

    private static final Font TITLE_FONT = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 40);
    private static final Font LABEL_FONT = Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 20);
    private static final Duration UPDATE_INTERVAL = Duration.millis(200);

    @Override
    public void start(Stage primaryStage) {
        int width = 40;
        int height = 30;
        this.world = new World(width, height);
        this.stage = primaryStage;
        primaryStage.setTitle("Game Of Life");
        Scene scene = new Scene(initScreen(width, height));
        scene.getStylesheets().add("sample/sample.css");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Evolution timeline
        timeline = new Timeline(new KeyFrame(UPDATE_INTERVAL, e -> {
            world.nextGeneration();
            updateGrid();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private Pane initScreen(int width, int height) {
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        // Title
        Text title = new Text("Conway's Game of Life");
        title.setFont(TITLE_FONT);
        title.setUnderline(true);

        // Button Grid
        gridPlaceholder = new Group();
        initGrid(width, height);

        // Dimension controls
        HBox dimensions = new HBox(10);
        dimensions.setAlignment(Pos.CENTER);
        Label widthLabel = new Label("Width:");
        widthLabel.setFont(LABEL_FONT);
        spinnerWidth = new Spinner<>(5, 80, width, 5);
        spinnerWidth.setEditable(true);

        Label heightLabel = new Label("  Height:");
        heightLabel.setFont(LABEL_FONT);
        spinnerHeight = new Spinner<>(5, 50, height, 5);
        spinnerHeight.setEditable(true);

        Button newDimensions = new Button("Create");
        newDimensions.setOnAction(event -> {
            world = new World(spinnerWidth.getValue(), spinnerHeight.getValue());
            initGrid(world.getWidth(), world.getHeight());
        });
        dimensions.getChildren().addAll(widthLabel, spinnerWidth, heightLabel, spinnerHeight, newDimensions);

        // Bottom row with action buttons
        HBox actions = new HBox(10);
        actions.setPadding(new Insets(10));
        actions.setAlignment(Pos.CENTER);
        Button random = new Button("Random");
        random.setOnAction(this::handleRandom);
        Button loadFile = new Button("Load file..");
        loadFile.setOnAction(this::handleLoadFile);
        Button saveFile = new Button("Save to file..");
        saveFile.setOnAction(this::handleSaveFile);
        Button simulate = new Button("Start simulation");
        simulate.setOnAction(this::handleStartSimulation);
        random.setFont(LABEL_FONT);
        loadFile.setFont(LABEL_FONT);
        saveFile.setFont(LABEL_FONT);
        simulate.setFont(LABEL_FONT);
        actions.getChildren().addAll(random, loadFile, saveFile, simulate);

        layout.getChildren().addAll(title, gridPlaceholder, dimensions, actions);
        return layout;
    }

    private void initGrid(final int width, final int height) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));
        buttonGrid = new Button[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int finalX = x;
                final int finalY = y;
                Button button = new Button();
                button.setMinSize(20, 20);
                button.setPrefSize(20, 20);
                button.setOnAction((event) -> handleButton(event, finalX, finalY));
                grid.add(button, x, y);
                buttonGrid[x][y] = button;
                if (world.isAliveAt(x, y)) {
                    toggleBackgroundButton(button);
                }
            }
        }
        gridPlaceholder.getChildren().clear();
        gridPlaceholder.getChildren().add(grid);
        stage.sizeToScene();
    }

    private void updateGrid() {
        for (int x = 0; x < world.getWidth(); x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                if (world.isAliveAt(x, y)) {
                    setButtonBlack(buttonGrid[x][y]);
                } else {
                    setButtonWhite(buttonGrid[x][y]);
                }
            }
        }
    }

    private void handleButton(ActionEvent event, int x, int y) {
        world.toggleCell(x, y);
        toggleBackgroundButton((Button) event.getSource());
    }

    private void handleRandom(ActionEvent event) {
        world.randomCells();
        updateGrid();
    }

    private void handleLoadFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                this.world = new World(file);
                spinnerWidth.getValueFactory().setValue(world.getWidth());
                spinnerHeight.getValueFactory().setValue(world.getHeight());
                initGrid(world.getWidth(), world.getHeight());
                stage.sizeToScene();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleSaveFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                this.world.saveToFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleStartSimulation(ActionEvent event) {
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.pause();
            ((Button) event.getSource()).setText("Start simulation");
        } else {
            timeline.play();
            ((Button) event.getSource()).setText("Stop simulation");
        }
    }

    private void toggleBackgroundButton(Button b) {
        if (b.getStyle().contains("black")) {
            setButtonWhite(b);
        } else {
            setButtonBlack(b);
        }
    }

    private void setButtonBlack(Button b) {
        b.setStyle("-fx-background-color: black;");
    }

    private void setButtonWhite(Button b) {
        b.setStyle("-fx-background-color: white;");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
