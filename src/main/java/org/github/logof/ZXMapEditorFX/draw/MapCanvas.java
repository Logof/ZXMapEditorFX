package org.github.logof.ZXMapEditorFX.draw;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.github.logof.ZXMapEditorFX.dialog.PropertyData;
import org.github.logof.ZXMapEditorFX.dialog.PropertyDialog;
import org.github.logof.ZXMapEditorFX.dialog.PropertyDialog.OnPropertyDialogActionListener;
import org.github.logof.ZXMapEditorFX.layer.MapTile;
import org.github.logof.ZXMapEditorFX.layer.TiledMapLayer;
import org.github.logof.ZXMapEditorFX.property.AltasResourceManager;
import org.github.logof.ZXMapEditorFX.property.TileProperty;
import org.github.logof.ZXMapEditorFX.property.TiledMap;
import java.util.ArrayList;
import java.util.List;

public class MapCanvas extends Canvas {
	private GraphicsContext gContext2D;
	private double tileWidth, tileHeight;
	private AltasResourceManager.AltasResource nowAltasResource;
	private List<TiledMapLayer> mapLayerList = new ArrayList<>();
	private final SimpleListProperty<Integer> nowChooseProperty = new SimpleListProperty<>();

	private final SimpleIntegerProperty nowSelectLayerProperty = new SimpleIntegerProperty(-1);
	private final SimpleIntegerProperty brushTypeProperty = new SimpleIntegerProperty(0);
	private final SimpleBooleanProperty showGridProperty = new SimpleBooleanProperty(true);
	private final SimpleBooleanProperty showProProperty = new SimpleBooleanProperty(true);
	private final SimpleDoubleProperty scaleProperty = new SimpleDoubleProperty(1.0);
	private int mouseCols = 0, mouseRows = 0;
	private int nowPropertyCols = 0, nowPropertyRows = 0;
	private double mouseX, mouseY;
	private boolean isDrawAltasList = true;
	private static final int rectIndex = -100;

	private final PropertyDialog mPropertyDialog = new PropertyDialog();

	public MapCanvas(double width, double height) {
		super(width, height);
		init();
	}

	public MapCanvas() {
		this(0, 0);
	}

	private void init() {
		mPropertyDialog.setOnPropertyDialogActionListener(new OnPropertyDialogActionListener() {

			@Override
			public void onDeletePropertyAction() {
			}

			@Override
			public void onAddPropertyAction() {
			}

			@Override
			public void onOkDialogAction(ObservableList<PropertyData> propertyDatas) {
				// 获取指定行列的属性
				TileProperty nowProperty = TiledMap.getInstance().getProperty(nowPropertyCols, nowPropertyRows);
				if (!propertyDatas.isEmpty()) {
					// 如果没有则创建一个
					if (nowProperty == null) {
						nowProperty = new TileProperty();
						nowProperty.setRow(nowPropertyRows);
						nowProperty.setCol(nowPropertyCols);
						TiledMap.getInstance().getPropertyList().add(nowProperty);
					}
					// 清空属性
					nowProperty.getValueMap().clear();
					// 将键值对属性存入
					for (PropertyData data : propertyDatas) {
						nowProperty.insertValue(data.getKey(), data.getValue());
					}
				} else {
					TiledMap.getInstance().getPropertyList().remove(nowProperty);
				}
			}

			@Override
			public void onInit(ObservableList<PropertyData> propertyDatas, TitledPane mTitledPane) {
				System.out.println(nowPropertyCols + "," + nowPropertyRows);
				// 获取指定行列的属性
				TileProperty nowProperty = TiledMap.getInstance().getProperty(nowPropertyCols, nowPropertyRows);
				propertyDatas.clear();
				mTitledPane.setText("属性列表:" + nowPropertyCols + "," + nowPropertyRows);
				// 如果没有则创建一个
				if (nowProperty != null) {
                    for (String key : nowProperty.getValueMap().keySet()) {
                        String value = nowProperty.getValueMap().get(key);
                        propertyDatas.add(new PropertyData(key, value));
                    }
				}
			}
		});
		final ContextMenu contextMenu = new ContextMenu();
		MenuItem propertyItem = new MenuItem("Attributes");
		propertyItem.setOnAction(e -> mPropertyDialog.showDialog());
		contextMenu.getItems().add(propertyItem);
		gContext2D = getGraphicsContext2D();
		setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				fillTheMap(e);
			}
			if (e.getButton() == MouseButton.SECONDARY) {
				nowPropertyCols = (int) (e.getX() / (tileWidth * getScale()));
				nowPropertyRows = (int) (e.getY() / (tileHeight * getScale()));
				contextMenu.show(MapCanvas.this, e.getScreenX(), e.getScreenY());
			}
		});
		setOnMouseDragged(e -> {
			fillTheMap(e);
			isDrawAltasList = false;
		});
		setOnMouseReleased(e -> isDrawAltasList = true);

		setOnMouseMoved(e -> {
			mouseCols = (int) (e.getX() / (tileWidth * getScale()));
			mouseRows = (int) (e.getY() / (tileHeight * getScale()));
			mouseX = e.getX();
			mouseY = e.getY();
		});

		setOnMouseEntered(e -> isDrawAltasList = true);

		setOnMouseExited(e -> isDrawAltasList = false);

		setWidth(TiledMap.getInstance().getRealTileMapWidth());
		setHeight(TiledMap.getInstance().getRealTileMapHeight());
	}

	private void fillTheMap(MouseEvent e) {
		switch (getBrushType()) {
		case 0:
			if (getNowSelectLayer() >= 0) {
				fillTheMap(e.getX(), e.getY());
			}
			break;
		case 1:
			brushAllMap();
			break;
		case 2:
			if (getNowSelectLayer() >= 0) {
				eraserTheMap(e.getX(), e.getY());
			}
			break;
		case 3:
			fillTheMap(e.getX(), e.getY(), rectIndex);
			break;
		}
	}

	private void brushAllMap() {
		if (getNowSelectLayer() >= 0 && getNowSelectLayer() < mapLayerList.size()) {
			TiledMapLayer layer = mapLayerList.get(getNowSelectLayer());
			MapTile[][] tiles = layer.getMapTiles();
			if (tiles != null) {
				for (int y = 0; y < tiles.length; y++) {
					for (int x = 0; x < tiles[0].length; x++) {
						MapTile mapTile = new MapTile();
						mapTile.setAltasIndex(nowChooseProperty.get(0));
						mapTile.setAltasId(nowAltasResource.getAltasId());
						layer.setMapTile(x, y, mapTile);
					}
				}
			}
		}
	}

	private void fillTheMap(double x, double y) {
		int cols = (int) (x / (tileWidth * getScale()));
		int rows = (int) (y / (tileHeight * getScale()));
		if (getNowSelectLayer() >= 0 && getNowSelectLayer() < mapLayerList.size()) {
			TiledMapLayer layer = mapLayerList.get(getNowSelectLayer());

			for (int i = 0; i < nowChooseProperty.getSize(); i++) {
				int index = nowChooseProperty.get(i);
				Image image = nowAltasResource.getImage();
				if (index != -1) {
					int cellX = (int) (image.getWidth() / tileWidth);
					int col = index % cellX;
					int row = index / cellX;

					int startCol = nowChooseProperty.get(0) % cellX;
					int startRow = nowChooseProperty.get(0) / cellX;

					MapTile mapTile = new MapTile();
					mapTile.setAltasIndex(index);
					mapTile.setAltasId(nowAltasResource.getAltasId());
					layer.setMapTile(cols + col - startCol, rows + row - startRow, mapTile);

				}
			}
		}
	}

	private void fillTheMap(double x, double y, int index) {
		int cols = (int) (x / (tileWidth * getScale()));
		int rows = (int) (y / (tileHeight * getScale()));
		if (getNowSelectLayer() >= 0 && getNowSelectLayer() < mapLayerList.size()) {
			TiledMapLayer layer = mapLayerList.get(getNowSelectLayer());
			if (index != -1) {
				MapTile mapTile = new MapTile();
				mapTile.setAltasIndex(index);
				mapTile.setAltasId("");
				layer.setMapTile(cols, rows, mapTile);
			}
		}
	}

	private void eraserTheMap(double x, double y) {
		int cols = (int) (x / (tileWidth * getScale()));
		int rows = (int) (y / (tileHeight * getScale()));
		if (getNowSelectLayer() >= 0 && getNowSelectLayer() < mapLayerList.size()) {
			TiledMapLayer layer = mapLayerList.get(getNowSelectLayer());
			MapTile mapTile = new MapTile();
			mapTile.setAltasIndex(-1);
			mapTile.setAltasId(null);
			layer.setMapTile(cols, rows, mapTile);
			System.out.println(cols + "," + rows + "," + mapTile.getAltasIndex());
		}
	}

	public void draw() {
		gContext2D.save();
		gContext2D.setFill(Color.WHITE);
		gContext2D.clearRect(0, 0, getWidth(), getHeight());
		gContext2D.setStroke(Color.BLACK);

		TiledMap tiledMap = TiledMap.getInstance();
		tileWidth = tiledMap.getTileWidth();
		tileHeight = tiledMap.getTileHeight();
		// 绘制多图层地图
		int length = mapLayerList.size();
		if (length > 0) {
			for (int i = length - 1; i >= 0; i--) {
				TiledMapLayer mapLayer = mapLayerList.get(i);
				if (mapLayer.isVisible()) {
					MapTile[][] tiles = mapLayer.getMapTiles();
					gContext2D.setGlobalAlpha(mapLayer.getAlpha());
					if (tiles != null) {
						for (int y = 0; y < tiles.length; y++) {
							for (int x = 0; x < tiles[0].length; x++) {
								if (tiles[y][x] != null) {
									AltasResourceManager.AltasResource resource = AltasResourceManager.getInstance().getResourceById(
											tiles[y][x].getAltasId());
									if (resource != null) {
										Image image = resource.getImage();
										int index = tiles[y][x].getAltasIndex();
										if (index != -1) {
											int cellX = (int) (image.getWidth() / tileWidth);
											int col = index % cellX;
											int row = index / cellX;
											gContext2D.drawImage(image, col * tileWidth, row * tileHeight, tileWidth,
													tileHeight, x * tileWidth * getScale(),
													y * tileHeight * getScale(), tileWidth * getScale(), tileHeight
															* getScale());
										}
									} else {
										int index = tiles[y][x].getAltasIndex();
										if (index == rectIndex) {
											gContext2D.save();
											gContext2D.setGlobalAlpha(0.6f);
											gContext2D.setFill(Color.RED);
											gContext2D.fillRect(x * tileWidth * getScale(),
													y * tileHeight * getScale(), tileWidth * getScale(), tileHeight
															* getScale());
											gContext2D.restore();
										}
									}
								}
							}
						}
					}
				}
			}
			if (isShowGrid()) {
				// 绘制网格
				gContext2D.setGlobalAlpha(1.0f);
				gContext2D.setLineWidth(0.5f);
				for (int i = 0; i < tiledMap.getMapWidth(); i++) {
					for (int j = 0; j < tiledMap.getMapHeight(); j++) {
						gContext2D.strokeRect(i * tileWidth * getScale(), j * tileHeight * getScale(), tileWidth
								* getScale(), tileHeight * getScale());
					}
				}
			}
			if (isDrawAltasList) {
				int cols = (int) (mouseX / (tileWidth * getScale()));
				int rows = (int) (mouseY / (tileHeight * getScale()));
				// 绘制受影响的网格
				gContext2D.setGlobalAlpha(0.6f);
				if (nowChooseProperty.getSize() != 0) {
					for (int i = 0; i < nowChooseProperty.getSize(); i++) {
						int index = nowChooseProperty.get(i);
						Image image = nowAltasResource.getImage();
						if (index != -1) {
							int cellX = (int) (image.getWidth() / tileWidth);
							int col = index % cellX;
							int row = index / cellX;

							int startCol = nowChooseProperty.get(0) % cellX;
							int startRow = nowChooseProperty.get(0) / cellX;
							gContext2D.fillRect((cols + col - startCol) * tileWidth * getScale(),
									(rows + row - startRow) * tileHeight * getScale(), tileWidth * getScale(),
									tileHeight * getScale());
						}
					}
				} else {
					if (brushTypeProperty.get() == 3) {
						gContext2D.fillRect(cols * tileWidth * getScale(), rows * tileHeight * getScale(), tileWidth
								* getScale(), tileHeight * getScale());
					}
				}
				// 绘制要填充的贴图
				gContext2D.setGlobalAlpha(0.8f);
				for (int i = 0; i < nowChooseProperty.getSize(); i++) {
					int index = nowChooseProperty.get(i);
					Image image = nowAltasResource.getImage();
					if (index != -1) {
						int cellX = (int) (image.getWidth() / tileWidth);
						int col = index % cellX;
						int row = index / cellX;

						int startCol = nowChooseProperty.get(0) % cellX;
						int startRow = nowChooseProperty.get(0) / cellX;

						gContext2D.drawImage(image, col * tileWidth, row * tileHeight, tileWidth, tileHeight, (cols
								+ col - startCol)
								* tileWidth * getScale(), (rows + row - startRow) * tileHeight * getScale(), tileWidth
								* getScale(), tileHeight * getScale());
					}
				}
			}

			// 绘制属性网格
			if (isShowProperty()) {
				ArrayList<TileProperty> propertyList = tiledMap.getPropertyList();
				if (!propertyList.isEmpty()) {
					gContext2D.setFill(Color.PURPLE);
					gContext2D.setGlobalAlpha(0.5f);
					for (TileProperty tileProperty : propertyList) {
						int rows = tileProperty.getRow();
						int cols = tileProperty.getCol();
						gContext2D.fillRect(cols * tileWidth * getScale(), rows * tileHeight * getScale(), tileWidth
								* getScale(), tileHeight * getScale());
					}
				}
			}
		}
		gContext2D.restore();
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

	public List<TiledMapLayer> getMapLayerList() {
		return mapLayerList;
	}

	public void setMapLayerList(List<TiledMapLayer> mapLayerList) {
		this.mapLayerList = mapLayerList;
	}

	public AltasResourceManager.AltasResource getNowAltasResource() {
		return nowAltasResource;
	}

	public void setNowAltasResource(AltasResourceManager.AltasResource nowAltasResource) {
		this.nowAltasResource = nowAltasResource;
	}

	public SimpleIntegerProperty NowSelectLayerProperty() {
		return nowSelectLayerProperty;
	}

	public void setNowSelectLayer(int nowSelect) {
		nowSelectLayerProperty.set(nowSelect);
	}

	public int getNowSelectLayer() {
		return nowSelectLayerProperty.get();
	}

	public SimpleIntegerProperty BrushTypeProperty() {
		return brushTypeProperty;
	}

	public SimpleBooleanProperty ShowGridProperty() {
		return showGridProperty;
	}

	public SimpleDoubleProperty ScaleProperty() {
		return scaleProperty;
	}
	
	public SimpleBooleanProperty ShowProProperty(){
		return showProProperty;
	}

	public void setScale(double scale) {
		scaleProperty.set(scale);
	}

	public double getScale() {
		return scaleProperty.get();
	}

	public boolean isShowGrid() {
		return showGridProperty.get();
	}

	public void setShowGrid(boolean isShowGrid) {
		this.showGridProperty.set(isShowGrid);
	}
	
	public boolean isShowProperty(){
		return showProProperty.get();
	}
	
	public void setShowProperty(boolean isShowProperty){
		this.showProProperty.set(isShowProperty);		
	}

	public int getBrushType() {
		return brushTypeProperty.get();
	}

	public int getMouseCols() {
		return mouseCols;
	}

	public int getMouseRows() {
		return mouseRows;
	}
}
