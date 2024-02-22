package org.github.logof.ZXMapEditorFX.layer;

import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import org.github.logof.ZXMapEditorFX.entity.MapLayerEntity;
import org.github.logof.ZXMapEditorFX.property.TiledMap;

public class TiledMapLayer {
    private final SimpleStringProperty layerName = new SimpleStringProperty();
    @Getter
    private MapTile[][] mapTiles;
    private boolean isVisible = true;
    private boolean isCollider = false;

    public TiledMapLayer(int width, int height) {
        mapTiles = new MapTile[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                mapTiles[i][j] = new MapTile();
            }
        }
    }

    public TiledMapLayer() {
        this(TiledMap.getInstance().getMapWidth(), TiledMap.getInstance().getMapHeight());
    }

    public void setMapTile(int x, int y, MapTile mapTile) {
        mapTiles[y][x] = mapTile;
    }

    public void setMapTile(MapTile[][] mapTiles) {
        this.mapTiles = mapTiles;
    }

    public SimpleStringProperty LayerNameProperty() {
        return layerName;
    }

    public String getLayerName() {
        return layerName.get();
    }

    public void setLayerName(String name) {
        layerName.set(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < mapTiles.length; y++) {
            for (int x = 0; x < mapTiles[0].length; x++) {
                if (y == mapTiles.length - 1 && x == mapTiles[0].length - 1) {
                    sb.append(mapTiles[y][x].toString());
                } else {
                    sb.append(mapTiles[y][x].toString() + "T");
                }
            }
        }
        return sb.toString();
    }

    public void convertFromString(String str) {
        String[] data = str.split("T");
        System.out.println("Data length:" + data.length);
        int mapWidth = TiledMap.getInstance().getMapWidth();
        int mapHeight = TiledMap.getInstance().getMapHeight();
        if (mapTiles == null) {
            mapTiles = new MapTile[mapHeight][mapWidth];
        }
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                mapTiles[y][x] = new MapTile();
                mapTiles[y][x].covertFromString(data[y * mapWidth + x]);
            }
        }
    }

    public void convertFromTileList(MapLayerEntity.Tile[] tileList) {
        System.out.println("Data length:" + tileList.length);
        int mapWidth = TiledMap.getInstance().getMapWidth();
        int mapHeight = TiledMap.getInstance().getMapHeight();
        if (mapTiles == null) {
            mapTiles = new MapTile[mapHeight][mapWidth];
        }
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                mapTiles[y][x] = new MapTile();
                mapTiles[y][x].covertFromTile(tileList[y * mapWidth + x]);
            }
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isCollider() {
        return isCollider;
    }

    public void setCollider(boolean isCollider) {
        this.isCollider = isCollider;
    }
}
