package org.github.logof.ZXMapEditorFX.property;

import lombok.Getter;
import lombok.Setter;
import org.github.logof.ZXMapEditorFX.Constans;
import java.util.ArrayList;

@Getter
@Setter
public class TiledMap {
	private int mapWidth;
	private int mapHeight;

	private static TiledMap mapProperty;
    private final ArrayList<TileProperty> propertyList = new ArrayList<>();
    
    public static TiledMap getInstance(){
    	if(mapProperty == null){
    		mapProperty = new TiledMap();
    	}
    	return mapProperty;
    }
    
    public void setMapProperty(int mapWidth, int mapHeight){
    	setMapWidth(mapWidth);
    	setMapHeight(mapHeight);
    }

	public double getRealTileMapWidth(){
		return Constans.TILE_WIDTH * mapWidth;
	}
	
	public double getRealTileMapHeight(){
		return Constans.TILE_HEIGHT * mapHeight;
	}

	// Get the attributes of the specified row and column
	public TileProperty getProperty(int col,int row){
		TileProperty nowProperty = null;
		//Find the properties of cells in a specified row and column
		for(TileProperty tileProperty : propertyList){
			if(tileProperty.getRow() == row && tileProperty.getCol() == col){
				nowProperty = tileProperty;
				break;
			}else {
				nowProperty = null;
			}
		}
		return nowProperty;
	}
}
