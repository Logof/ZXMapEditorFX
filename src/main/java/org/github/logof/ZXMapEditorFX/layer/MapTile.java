package org.github.logof.ZXMapEditorFX.layer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.github.logof.ZXMapEditorFX.entity.MapLayerEntity;
import java.util.UUID;

@Setter
@Getter
@ToString
public class MapTile {
    private UUID tilesetId = null;
    private int tilesetIndex = -1;

    public void covertFromString(String str) {
        String[] data = str.replace("[", "").replace("]", "").replace(" ", "").split(",");
        tilesetId = UUID.fromString(data[0]);
        tilesetIndex = Integer.parseInt(data[1]);
    }

    public void covertFromTile(MapLayerEntity.Tile tile) {
        tilesetIndex = tile.getTileId();
    }
}
