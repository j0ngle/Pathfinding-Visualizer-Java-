package project;

import static project.Visualizer.STATE.EMPTY;
import static project.Visualizer.STATE.FILLED;

import java.net.URL;
import java.util.List;

import javafx.application.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.geometry.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import java.util.*;

public abstract class Visualizer extends Application{
	protected static final int GRID_COLUMNS = 25, GRID_ROWS = 25;
	
	private Cell[][] cells = new Cell[GRID_ROWS][GRID_COLUMNS];
	
//	private IntegerProperty elapsed;
//	private ObjectProperty<Player> turn;
	//private StringProperty setup, AI;
	
	//Establish Stage (Window)
	@Override
	public final void start(final Stage stage) {
		stage.setTitle("Pathfiniding Visualizer");
		
		final BorderPane pane = new BorderPane(); {
			pane.getStyleClass().add("pane");
			
			final MenuBar bar = new MenuBar(); {
				pane.setTop(bar);
				
				final Menu options = new Menu("_Options");
				bar.getMenus().add(options);
				
				final MenuItem clear = new MenuItem("_Clear Board");
				clear.setAccelerator(KeyCombination.keyCombination("SHORTCUT+X"));
				clear.setOnAction(event -> clearBoard());		//Runs method when key is pressed
				options.getItems().add(clear);
			}
			
			final GridPane grid = new GridPane(); {
				pane.setCenter(grid);
				
				grid.setPadding(new Insets(5));				//Changes margin size
				grid.setHgap(2);							//Changes spacing between squares
				grid.setVgap(2);
				
				final RowConstraints rcons = new RowConstraints();
				rcons.setPercentHeight(1.0/GRID_ROWS*100);			//Determines maximum window size
				rcons.setVgrow(Priority.ALWAYS);
					for (int r = 1; r <= GRID_ROWS; r++)
						grid.getRowConstraints().add(rcons);
				
				final ColumnConstraints ccons = new ColumnConstraints();
				ccons.setPercentWidth(1.0/GRID_COLUMNS*100);		
				ccons.setHgrow(Priority.ALWAYS);
					for (int c = 1; c <= GRID_COLUMNS; c++)
						grid.getColumnConstraints().add(ccons);
					
				for (int r = 0; r < GRID_ROWS; r++) {
					for (int c = 0; c < GRID_COLUMNS; c++) {
						final int row = r;
						final int column = c;
						final Button button = new Button();
						button.setOnMouseClicked(event -> colorSquare(row, column));
						button.getStyleClass().add("cell-none");
						button.styleProperty().bind(Bindings.concat("-fx-alignment: center;", "-fx-font-size: ", 
								button.heightProperty().divide(1.5).asString(), "px;"));
						button.setMinSize(15,  15);		//Button Sizes
						button.setPrefSize(45,  45);
						button.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
						grid.add(button,  c,  r);
						cells[r][c] = new Cell(button);
					}
				}
			}
			
			//SETTING CHOICE BOXES
			
			final GridPane dash = new GridPane(); {
				pane.setBottom(dash);
				pane.getBottom().setStyle("-fx-font-size: 1.5em;");
				
				dash.setPadding(new Insets(0, 5, 5, 5));
				dash.setHgap(15);
				
				final ColumnConstraints dcons = new ColumnConstraints();
				dcons.setPercentWidth(1.0/3*100);
				dcons.setHgrow(Priority.ALWAYS);for (int c = 1; c <= 3; c++) {
					dash.getColumnConstraints().add(dcons);
				}
				
				final ChoiceBox<String> placement = new ChoiceBox<>();
				final ChoiceBox<String> algorithms = new ChoiceBox<>();
				
				placement.getItems().add("Place Endpoints");
				placement.getItems().add("Place Walls");
				algorithms.getItems().add("A*");	
				
				for (ChoiceBox<String> box: List.of(placement, algorithms)) {
					box.getSelectionModel().selectFirst();
					box.setStyle("-fx-backgound-radius: 0;");
					box.setMaxSize(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
				}
				
				placement.getStyleClass().addAll("cell-color");
				dash.add(placement,  0,  0);
				
				algorithms.getStyleClass().addAll("cell-color");
				dash.add(algorithms,  2,  0);
				
				placement.setOnAction(event -> System.out.println(setup.get()));
		}
		
		
		final Scene scene = new Scene(pane);
		//CSS INFORMATION
		final URL css = Visualizer.class.getResource("style.css");
		if (css == null) {
			System.err.println("Stylesheet resource is missing: src/project/style.css");
			System.exit(1);
		}
		scene.getStylesheets().add(css.toExternalForm());
		stage.setScene(scene);

		
		stage.setResizable(true);
		stage.setOnShown(event -> {
			final Screen screen = Screen.getPrimary();
			final double ratio = (screen.getVisualBounds().getHeight() * .85) / stage.getHeight();
			stage.setHeight(stage.getHeight() * ratio);
			stage.setWidth(stage.getWidth() * ratio);
			stage.setX((screen.getVisualBounds().getWidth() - stage.getWidth()) / 2);
			stage.setY((screen.getVisualBounds().getHeight() - stage.getHeight()) / 2);
		});
		
		stage.show();
	}
	
	private final class Cell {
		private Button button;
		public STATE state;
		
		public Cell(Button button) {
			this.button = button;
			this.state = EMPTY;
		}
		
		private final void setColor(STATE state) {
			if (state == FILLED) {
				this.state = FILLED;
				button.getStyleClass().clear();
				button.getStyleClass().add("cell-endpoints");
			}
			if (state == EMPTY) {
				this.state = EMPTY;
				button.getStyleClass().clear();
				button.getStyleClass().add("cell-none");
			}
		}
	}
	
	private final void clearBoard() {
		for (int r = 0; r < GRID_ROWS; r++) {
			for (int c = 0; c < GRID_COLUMNS; c++) {
				cells[r][c].setColor(EMPTY);
			}
		}
	}
	
	//GRID INTERACTIONS
	public enum STATE {EMPTY, FILLED}
	
	private final void colorSquare(int row, int column) {
		final Cell selected = cells[row][column];
		if (selected.state == EMPTY)
			selected.setColor(FILLED);
		else
			selected.setColor(EMPTY);
	}
}
