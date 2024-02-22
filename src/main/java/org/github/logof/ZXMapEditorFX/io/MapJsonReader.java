package org.github.logof.ZXMapEditorFX.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.github.logof.ZXMapEditorFX.entity.MapEntity;
import java.io.File;
import java.io.IOException;

public class MapJsonReader implements MapReader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public MapEntity read(File projectFile) throws IOException {
        return objectMapper.readValue(projectFile, MapEntity.class);
    }
}
