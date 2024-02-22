package org.github.logof.ZXMapEditorFX.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class MapEntity {

    @JsonProperty("layers")
    private final List<MapLayerEntity> mapLayerList = new ArrayList<>();
    @JsonProperty("width")
    private int mapWidthInTiles;
    @JsonProperty("height")
    private int mapHeightInTiles;
    @JsonProperty("tileset")
    private String tilesetPath;

}
