package org.github.logof.ZXMapEditorFX.property;

import java.util.HashMap;

public class TileProperty {
    private int col;
	private int row;
    private final HashMap<String, String> valueMap = new HashMap<>();
    
    public void insertValue(String key,String value){
    	valueMap.put(key, value);
    }
    
    public String getValue(String key){
    	return valueMap.get(key);
    }
    
    public void removeValue(String key){
    	valueMap.remove(key);
    }
    
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}

	public HashMap<String, String> getValueMap() {
		return valueMap;
	}
}
