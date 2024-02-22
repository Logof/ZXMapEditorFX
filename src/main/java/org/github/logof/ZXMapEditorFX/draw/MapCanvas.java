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
import lombok.Getter;
import lombok.Setter;
import org.github.logof.ZXMapEditorFX.Constans;
import org.github.logof.ZXMapEditorFX.dialog.PropertyData;
import org.github.logof.ZXMapEditorFX.dialog.PropertyDialog;
import org.github.logof.ZXMapEditorFX.dialog.PropertyDialog.OnPropertyDialogActionListener;
import org.github.logof.ZXMapEditorFX.layer.MapTile;
import org.github.logof.ZXMapEditorFX.layer.TiledMapLayer;
import org.github.logof.ZXMapEditorFX.property.TileProperty;
import org.github.logof.ZXMapEditorFX.property.TiledMap;
import org.github.logof.ZXMapEditorFX.property.TilesetResourceManager;
import java.util.ArrayList;
import java.util.List;

public class MapCanvas extends Canvas {
    private static final int rectIndex = -100;
    private final SimpleListProperty<Integer> nowChooseProperty = new SimpleListProperty<>();
    private final SimpleIntegerProperty nowSelectLayerProperty = new SimpleIntegerProperty(-1);
    private final SimpleIntegerProperty brushTypeProperty = new SimpleIntegerProperty(0);
    private final SimpleBooleanProperty showGridProperty = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty showProProperty = new SimpleBooleanProperty(true);
    private final SimpleDoubleProperty scaleProperty = new SimpleDoubleProperty(1.0);
    private final PropertyDialog mPropertyDialog = new PropertyDialog();
    private GraphicsContext graphicsContext;
    @Setter
    @Getter
    private TilesetResourceManager.TilesetResource nowTilesetResource;
    @Getter
    @Setter
    private List<TiledMapLayer> mapLayerList = new ArrayList<>();
    @Getter
    private int mouseCols = 0;
    @Getter
    private int mouseRows = 0;
    private int nowPropertyCols = 0, nowPropertyRows = 0;
    private double mouseX, mouseY;
    private boolean isDrawTilesetList = true;

    public MapCanvas(int width, int height) {
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
                mTitledPane.setText("Property list:" + nowPropertyCols + "," + nowPropertyRows);
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
        graphicsContext = getGraphicsContext2D();
        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                fillTheMap(e);
            }
            if (e.getButton() == MouseButton.SECONDARY) {
                nowPropertyCols = (int) (e.getX() / (Constans.TILE_WIDTH * getScale()));
                nowPropertyRows = (int) (e.getY() / (Constans.TILE_HEIGHT * getScale()));
                contextMenu.show(MapCanvas.this, e.getScreenX(), e.getScreenY());
            }
        });
        setOnMouseDragged(e -> {
            fillTheMap(e);
            isDrawTilesetList = false;
        });
        setOnMouseReleased(e -> isDrawTilesetList = true);

        setOnMouseMoved(e -> {
            mouseCols = (int) (e.getX() / (Constans.TILE_WIDTH * getScale()));
            mouseRows = (int) (e.getY() / (Constans.TILE_HEIGHT * getScale()));
            mouseX = e.getX();
            mouseY = e.getY();
        });

        setOnMouseEntered(e -> isDrawTilesetList = true);

        setOnMouseExited(e -> isDrawTilesetList = false);

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
                        mapTile.setTilesetIndex(nowChooseProperty.get(0));
                        mapTile.setTilesetId(nowTilesetResource.getTilesetId());
                        layer.setMapTile(x, y, mapTile);
                    }
                }
            }
        }
    }

    private void fillTheMap(double x, double y) {
        int cols = (int) (x / (Constans.TILE_WIDTH * getScale()));
        int rows = (int) (y / (Constans.TILE_HEIGHT * getScale()));
        if (getNowSelectLayer() >= 0 && getNowSelectLayer() < mapLayerList.size()) {
            TiledMapLayer layer = mapLayerList.get(getNowSelectLayer());

            for (int i = 0; i < nowChooseProperty.getSize(); i++) {
                int index = nowChooseProperty.get(i);
                Image image = nowTilesetResource.getImage();
                if (index != -1) {
                    int cellX = (int) (image.getWidth() / Constans.TILE_WIDTH);
                    int col = index % cellX;
                    int row = index / cellX;

                    int startCol = nowChooseProperty.get(0) % cellX;
                    int startRow = nowChooseProperty.get(0) / cellX;

                    MapTile mapTile = new MapTile();
                    mapTile.setTilesetIndex(index);
                    mapTile.setTilesetId(nowTilesetResource.getTilesetId());
                    layer.setMapTile(cols + col - startCol, rows + row - startRow, mapTile);

                }
            }
        }
    }

    private void fillTheMap(double x, double y, int index) {
        int cols = (int) (x / (Constans.TILE_WIDTH * getScale()));
        int rows = (int) (y / (Constans.TILE_HEIGHT * getScale()));
        if (getNowSelectLayer() >= 0 && getNowSelectLayer() < mapLayerList.size()) {
            TiledMapLayer layer = mapLayerList.get(getNowSelectLayer());
            if (index != -1) {
                MapTile mapTile = new MapTile();
                mapTile.setTilesetIndex(index);
                mapTile.setTilesetId(null);
                layer.setMapTile(cols, rows, mapTile);
            }
        }
    }

    private void eraserTheMap(double x, double y) {
        int cols = (int) (x / (Constans.TILE_WIDTH * getScale()));
        int rows = (int) (y / (Constans.TILE_HEIGHT * getScale()));
        if (getNowSelectLayer() >= 0 && getNowSelectLayer() < mapLayerList.size()) {
            TiledMapLayer layer = mapLayerList.get(getNowSelectLayer());
            MapTile mapTile = new MapTile();
            mapTile.setTilesetIndex(-1);
            mapTile.setTilesetId(null);
            layer.setMapTile(cols, rows, mapTile);
            System.out.println(cols + "," + rows + "," + mapTile.getTilesetIndex());
        }
    }

    public void draw() {
        graphicsContext.save();
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.clearRect(0, 0, getWidth(), getHeight());
        graphicsContext.setStroke(Color.BLACK);

        TiledMap tiledMap = TiledMap.getInstance();

        // Draw multi-layer maps
        int length = mapLayerList.size();
        if (length == 0) {
            return;
            // Возможно нужен graphicsContext.restore();
        }

        for (int layerId = length - 1; layerId >= 0; layerId--) {
            TiledMapLayer mapLayer = mapLayerList.get(layerId);

            if (mapLayer.isVisible()) {
                MapTile[][] tiles = mapLayer.getMapTiles();
                if (tiles != null) {
                    for (int y = 0; y < tiles.length; y++) {
                        for (int x = 0; x < tiles[0].length; x++) {
                            if (tiles[y][x] != null) {
                                TilesetResourceManager.TilesetResource resource = TilesetResourceManager.getInstance()
                                                                                                        .getResourceById(
                                                                                                                tiles[y][x].getTilesetId());
                                if (resource != null) {
                                    Image image = resource.getImage();
                                    int index = tiles[y][x].getTilesetIndex();
                                    if (index != -1) {
                                        int cellX = (int) (image.getWidth() / Constans.TILE_WIDTH);
                                        int col = index % cellX;
                                        int row = index / cellX;
                                        graphicsContext.drawImage(image, col * Constans.TILE_WIDTH, row * Constans.TILE_HEIGHT, Constans.TILE_WIDTH,
                                                Constans.TILE_HEIGHT, x * Constans.TILE_WIDTH * getScale(),
                                                y * Constans.TILE_HEIGHT * getScale(), Constans.TILE_WIDTH * getScale(), Constans.TILE_HEIGHT
                                                        * getScale());
                                    }
                                } else {
                                    int index = tiles[y][x].getTilesetIndex();
                                    if (index == rectIndex) {
                                        graphicsContext.save();
                                        graphicsContext.setGlobalAlpha(0.6f);
                                        graphicsContext.setFill(Color.RED);
                                        graphicsContext.fillRect(x * Constans.TILE_WIDTH * getScale(),
                                                y * Constans.TILE_HEIGHT * getScale(), Constans.TILE_WIDTH * getScale(), Constans.TILE_HEIGHT
                                                        * getScale());
                                        graphicsContext.restore();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isShowGrid()) {
            drawGrid(tiledMap);
        }

        if (isDrawTilesetList) {
            int cols = (int) (mouseX / (Constans.TILE_WIDTH * getScale()));
            int rows = (int) (mouseY / (Constans.TILE_HEIGHT * getScale()));
            // Draw the affected mesh
            graphicsContext.setGlobalAlpha(0.6f);
            if (nowChooseProperty.getSize() != 0) {
                for (int i = 0; i < nowChooseProperty.getSize(); i++) {
                    int index = nowChooseProperty.get(i);
                    Image image = nowTilesetResource.getImage();
                    if (index != -1) {
                        int cellX = (int) (image.getWidth() / Constans.TILE_WIDTH);
                        int col = index % cellX;
                        int row = index / cellX;

                        int startCol = nowChooseProperty.get(0) % cellX;
                        int startRow = nowChooseProperty.get(0) / cellX;
                        graphicsContext.fillRect((cols + col - startCol) * Constans.TILE_WIDTH * getScale(),
                                (rows + row - startRow) * Constans.TILE_HEIGHT * getScale(), Constans.TILE_WIDTH * getScale(),
                                Constans.TILE_HEIGHT * getScale());
                    }
                }
            } else {
                if (brushTypeProperty.get() == 3) {
                    graphicsContext.fillRect(cols * Constans.TILE_WIDTH * getScale(), rows * Constans.TILE_HEIGHT * getScale(), Constans.TILE_WIDTH
                            * getScale(), Constans.TILE_HEIGHT * getScale());
                }
            }
            // 绘制要填充的贴图
            graphicsContext.setGlobalAlpha(0.8f);
            for (int i = 0; i < nowChooseProperty.getSize(); i++) {
                int index = nowChooseProperty.get(i);
                Image image = nowTilesetResource.getImage();
                if (index != -1) {
                    int cellX = (int) (image.getWidth() / Constans.TILE_WIDTH);
                    int col = index % cellX;
                    int row = index / cellX;

                    int startCol = nowChooseProperty.get(0) % cellX;
                    int startRow = nowChooseProperty.get(0) / cellX;

                    graphicsContext.drawImage(image, col * Constans.TILE_WIDTH, row * Constans.TILE_HEIGHT, Constans.TILE_WIDTH, Constans.TILE_HEIGHT, (cols
                            + col - startCol)
                            * Constans.TILE_WIDTH * getScale(), (rows + row - startRow) * Constans.TILE_HEIGHT * getScale(), Constans.TILE_WIDTH
                            * getScale(), Constans.TILE_HEIGHT * getScale());
                }
            }
        }

        // Draw attribute grid
        // TODO типы объектов выводить в разные цвета
        if (isShowProperty()) {
            ArrayList<TileProperty> propertyList = tiledMap.getPropertyList();
            if (!propertyList.isEmpty()) {
                graphicsContext.setFill(Color.PURPLE);
                graphicsContext.setGlobalAlpha(0.5f);
                for (TileProperty tileProperty : propertyList) {
                    int rows = tileProperty.getRow();
                    int cols = tileProperty.getCol();
                    graphicsContext.fillRect(
                            cols * Constans.TILE_WIDTH * getScale(),
                            rows * Constans.TILE_HEIGHT * getScale(),
                            Constans.TILE_WIDTH * getScale(),
                            Constans.TILE_HEIGHT * getScale());
                }
            }
        }
        graphicsContext.restore();
    }

    private void drawGrid(TiledMap tiledMap) {
        graphicsContext.setGlobalAlpha(1.0f);
        graphicsContext.setLineWidth(1.0f);

        for (int x = 0; x <= tiledMap.getMapWidth(); x++) {
            if (x % Constans.SCREEN_TILES_ON_WIDTH == 0) {
                graphicsContext.setStroke(Color.BLACK);
            } else {
                graphicsContext.setStroke(Color.DARKGREY);
            }
            graphicsContext.strokeLine(
                    x * Constans.TILE_WIDTH * getScale(),
                    0,
                    x * Constans.TILE_WIDTH * getScale(),
                    tiledMap.getRealTileMapHeight() * getScale());
        }

        for (int y = 0; y <= tiledMap.getMapHeight(); y++) {
            if (y % Constans.SCREEN_TILES_ON_HEIGHT == 0) {
                graphicsContext.setStroke(Color.BLACK);
            } else {
                graphicsContext.setStroke(Color.DARKGREY);
            }
            graphicsContext.strokeLine(
                    0,
                    y * Constans.TILE_HEIGHT * getScale(),
                    tiledMap.getRealTileMapWidth() * getScale(),
                    y * Constans.TILE_HEIGHT * getScale());
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

    public SimpleIntegerProperty NowSelectLayerProperty() {
        return nowSelectLayerProperty;
    }

    public int getNowSelectLayer() {
        return nowSelectLayerProperty.get();
    }

    public void setNowSelectLayer(int nowSelect) {
        nowSelectLayerProperty.set(nowSelect);
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

    public SimpleBooleanProperty ShowProProperty() {
        return showProProperty;
    }

    public double getScale() {
        return scaleProperty.get();
    }

    public void setScale(double scale) {
        scaleProperty.set(scale);
    }

    public boolean isShowGrid() {
        return showGridProperty.get();
    }

    public void setShowGrid(boolean isShowGrid) {
        this.showGridProperty.set(isShowGrid);
    }

    public boolean isShowProperty() {
        return showProProperty.get();
    }

    public void setShowProperty(boolean isShowProperty) {
        this.showProProperty.set(isShowProperty);
    }

    public int getBrushType() {
        return brushTypeProperty.get();
    }

}
