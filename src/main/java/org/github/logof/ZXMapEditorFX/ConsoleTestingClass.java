package org.github.logof.ZXMapEditorFX;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.github.logof.ZXMapEditorFX.entity.MapEntity;
import org.github.logof.ZXMapEditorFX.entity.MapLayerEntity;
import java.io.File;

public class ConsoleTestingClass {

    @SneakyThrows
    public static void main(String[] args) {
        File jsonFile = new File("/home/user/mojontwins/ZXMapEditorFX/demo.json");

        MapEntity newMap = new MapEntity();
        newMap.setMapHeightInTiles(2);
        newMap.setMapWidthInTiles(3);

        MapLayerEntity mapLayer = MapLayerEntity.builder()
                                                .layerName("Layer 1")
                                                .visible(true)
                                                .collider(true)
                                                .active(true)
                                                .tileList(new MapLayerEntity.Tile[10 * 15 * 2 * 3])
                                                .build();
        newMap.getMapLayerList().add(mapLayer);

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(newMap));

    }
}
