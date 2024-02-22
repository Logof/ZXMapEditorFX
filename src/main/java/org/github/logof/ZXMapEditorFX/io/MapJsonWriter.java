package org.github.logof.ZXMapEditorFX.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.github.logof.ZXMapEditorFX.entity.MapEntity;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class MapJsonWriter implements MapWriter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void write(File projectFile, MapEntity tileMap) {
        try {
            FileWriter fileWriter = new FileWriter(projectFile);
            fileWriter.write(objectMapper.writeValueAsString(tileMap));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
