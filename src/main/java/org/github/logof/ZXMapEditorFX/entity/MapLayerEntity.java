package org.github.logof.ZXMapEditorFX.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapLayerEntity {
    @JsonProperty("tiles")
    Tile[] tileList = new Tile[10];
    @JsonProperty("name")
    private String layerName;
    @JsonProperty("visible")
    private boolean visible;
    @JsonProperty("collider")
    private boolean collider;
    @JsonProperty("active")
    private boolean active;

    @Getter
    @Setter
    public static class Tile {
        @JsonProperty("id")
        private UUID tilesetId;

        @JsonProperty("tileId")
        private Integer tileId;
    }
}
