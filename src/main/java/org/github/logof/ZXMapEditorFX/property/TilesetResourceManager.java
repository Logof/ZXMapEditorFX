package org.github.logof.ZXMapEditorFX.property;

import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TilesetResourceManager {
    private static TilesetResourceManager tilesetResourceManager;
    private final List<TilesetResource> tilesetResources = new ArrayList<>();

    public static TilesetResourceManager getInstance() {
        if (tilesetResourceManager == null) {
            tilesetResourceManager = new TilesetResourceManager();
        }
        return tilesetResourceManager;
    }

    public static UUID createTilesetId() {
        return UUID.randomUUID();
    }

    public void addResource(UUID tilesetId, String path, Image image) {
        tilesetResources.add(new TilesetResource(tilesetId, path, image));
    }

    public void removeResource(UUID
                                       tilesetId) {
        TilesetResource ar = getResourceById(tilesetId);
        if (ar != null) {
            tilesetResources.remove(ar);
        }
    }

    public void removeResource(int index) {
        tilesetResources.remove(index);
    }

    public void removeAll() {
        tilesetResources.clear();
    }

    public TilesetResource getResourceById(UUID tilesetId) {
        TilesetResource ar = null;
        for (TilesetResource resource : tilesetResources) {
            if (resource.getTilesetId().equals(tilesetId)) {
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

    public List<TilesetResource> getResources() {
        return tilesetResources;
    }

    @Setter
    @Getter
    public static class TilesetResource {
        private UUID tilesetId;
        private String pathStr;
        private Image image;

        public TilesetResource(UUID tilesetId, String pathStr, Image image) {
            this.tilesetId = tilesetId;
            this.pathStr = pathStr;
            this.image = image;
        }

    }
}
