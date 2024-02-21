package org.github.logof.ZXMapEditorFX.draw;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.github.logof.ZXMapEditorFX.Constans;

public class TilesetCanvas extends Canvas {
	private GraphicsContext graphicsContext;
	private final SimpleIntegerProperty cellXCountProperty = new SimpleIntegerProperty(0);
	private final SimpleIntegerProperty cellYCountProperty = new SimpleIntegerProperty(0);
	@Getter
	private Image image;

	private final SimpleListProperty<Integer> nowChooseProperty = new SimpleListProperty<>();
	private final ObservableList<Integer> chooseList = FXCollections.observableArrayList();
	private final SimpleBooleanProperty showGridProperty = new SimpleBooleanProperty(true);
	private final SimpleIntegerProperty brushTypeProperty = new SimpleIntegerProperty(0);

	private double startX;
	private double startY;
	private double mouseX;
	private double mouseY;
	private boolean isDrag = false;

	public TilesetCanvas(double width, double height) {
		super(width, height);
		init();
	}

	public TilesetCanvas() {
		this(0, 0);
	}

	private void init() {
		graphicsContext = getGraphicsContext2D();
		nowChooseProperty.set(chooseList);
		setOnMouseClicked(e -> {
			switch (brushTypeProperty.get()) {
			case 0:
			case 1:
				if (e.getButton() == MouseButton.PRIMARY) {
					double x = e.getX();
					double y = e.getY();
					int index = (int) (y / Constans.TILE_HEIGHT) * cellXCountProperty.get() + (int) (x / Constans.SCREEN_TILES_ON_WIDTH);
					if (!chooseList.contains(index))
						chooseList.add(index);
				} else if (e.getButton() == MouseButton.SECONDARY) {
					chooseList.clear();
				}
				break;
			}
		});
		setOnMouseDragged(e -> {
            if (brushTypeProperty.get() == 0) {
                mouseX = e.getX();
                mouseY = e.getY();
                isDrag = true;
                int minX = (int) Math.min(mouseX, startX);
                int maxX = (int) Math.max(mouseX, startX);
                int minY = (int) Math.min(mouseY, startY);
                int maxY = (int) Math.max(mouseY, startY);
                chooseList.clear();
                for (int y = (minY / Constans.TILE_HEIGHT); y < (maxY / Constans.TILE_HEIGHT) + 1; y++) {
                    for (int x = (minX / Constans.TILE_WIDTH); x < (maxX / Constans.TILE_WIDTH) + 1; x++) {
                        int index = y * cellXCountProperty.get() + x;
                        if (!chooseList.contains(index))
                            chooseList.add(index);
                    }
                }
            }
		});
		setOnMouseDragExited(e -> {

		});
		setOnMousePressed(e -> {
			startX = e.getX();
			startY = e.getY();
			chooseList.clear();
		});
		setOnMouseReleased(e -> {
			startX = 0;
			startY = 0;
			isDrag = false;
		});
	}

	public void draw() {
		graphicsContext.save();
		graphicsContext.setFill(Color.WHITE);
		graphicsContext.clearRect(0, 0, getWidth(), getHeight());
		graphicsContext.setStroke(Color.BLACK);

		if (image != null) {
			graphicsContext.drawImage(image, 0, 0);
			if (getNowChoose() != null && !getNowChoose().isEmpty()) {
				graphicsContext.setGlobalAlpha(0.5f);
				graphicsContext.setFill(Color.YELLOW);
				for (Integer index : getNowChoose()) {
					graphicsContext.fillRect(
							index % cellXCountProperty.get() * Constans.TILE_WIDTH,
							(double) index / cellXCountProperty.get() * Constans.TILE_HEIGHT,
							Constans.TILE_WIDTH,
							Constans.TILE_HEIGHT);
				}
			}
			if (isShowGrid()) {
				graphicsContext.setGlobalAlpha(1.0f);
				graphicsContext.setLineWidth(0.5f);
				for (int i = 0; i < cellXCountProperty.get(); i++) {
					for (int j = 0; j < cellYCountProperty.get(); j++) {
						graphicsContext.strokeRect(i * Constans.TILE_WIDTH, j * Constans.TILE_HEIGHT, Constans.TILE_WIDTH, Constans.TILE_HEIGHT);
					}
				}
			}
			if (isDrag) {
				graphicsContext.setGlobalAlpha(1.0f);
				graphicsContext.setStroke(Color.GREENYELLOW);
				graphicsContext.strokeRect(Math.min(mouseX, startX), Math.min(mouseY, startY), Math.abs(mouseX - startX),
						Math.abs(mouseY - startY));
			}
		}
		graphicsContext.restore();
	}

	public void setImage(Image image) {
		this.image = image;
		if (image != null) {
			widthProperty().bind(image.widthProperty());
			heightProperty().bind(image.heightProperty());
			setCellXCount((int) (image.getWidth() / Constans.TILE_WIDTH));
			setCellYCount((int) (image.getHeight() / Constans.TILE_HEIGHT));
		}
	}

	public ObservableList<Integer> getNowChoose() {
		return nowChooseProperty.get();
	}

	public void setNowChoose(ObservableList<Integer> nowChoose) {
		this.nowChooseProperty.set(nowChoose);
	}

	public SimpleListProperty<Integer> NowChooseProperty() {
		return nowChooseProperty;
	}

	public SimpleIntegerProperty CellXCountProperty() {
		return cellXCountProperty;
	}

	public void setCellXCount(int cellXCount) {
		cellXCountProperty.set(cellXCount);
	}

	public SimpleIntegerProperty CellYCountProperty() {
		return cellYCountProperty;
	}

	public void setCellYCount(int cellYCount) {
		cellYCountProperty.set(cellYCount);
	}

	public SimpleBooleanProperty ShowGridProperty() {
		return showGridProperty;
	}

	public boolean isShowGrid() {
		return showGridProperty.get();
	}

	public void setShowGrid(boolean isShowGrid) {
		this.showGridProperty.set(isShowGrid);
	}

	public SimpleIntegerProperty BrushTypeProperty() {
		return brushTypeProperty;
	}
}
