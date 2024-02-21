package org.github.logof.ZXMapEditorFX.property;

import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/**
 * 贴图集资源管理
 * @author Wing Mei
 */
public class TilesetResourceManager {
	private final List<TilesetResource> tilesetResources = new ArrayList<>();
	private static TilesetResourceManager tilesetResourceManager;

	public static TilesetResourceManager getInstance() {
		if (tilesetResourceManager == null) {
			tilesetResourceManager = new TilesetResourceManager();
		}
		return tilesetResourceManager;
	}

	public void addResource(String altasId, String path,Image image) {
		tilesetResources.add(new TilesetResource(altasId, path, image));
	}

	public void removeResource(String altasId) {
		TilesetResource ar = getResourceById(altasId);
		if (ar != null) {
			tilesetResources.remove(ar);
		}
	}
	
	public void removeResource(int index){
		tilesetResources.remove(index);
	}
	
	public void removeAll(){
		tilesetResources.clear();
	}

	public TilesetResource getResourceById(String altasId) {
		TilesetResource ar = null;
		for (TilesetResource resource : tilesetResources) {
			if (resource.getAltasId().equals(altasId)) {
				ar = resource;
			}
		}
		return ar;
	}
	
	public TilesetResource getResourceByPath(String path) {
		TilesetResource ar = null;
		for (TilesetResource resource : tilesetResources) {
			if (resource.getPathStr().equals(path)) {
				ar = resource;
			}
		}
		return ar;
	}
	

	public static String createAltasId() {
		return UUID.randomUUID().toString();
	}
	
	public List<TilesetResource> getResources(){
		return tilesetResources;
	}

	public static class TilesetResource {
		private String altasId;
		private String pathStr;
        private Image image;
		public TilesetResource(String altasId, String pathStr, Image image) {
			this.altasId = altasId;
			this.pathStr = pathStr;
			this.image = image;
		}

		public String getAltasId() {
			return altasId;
		}

		public void setAltasId(String altasId) {
			this.altasId = altasId;
		}

		public String getPathStr() {
			return pathStr;
		}

		public void setPathStr(String pathStr) {
			this.pathStr = pathStr;
		}

		public Image getImage() {
			return image;
		}

		public void setImage(Image image) {
			this.image = image;
		}

	}
}
