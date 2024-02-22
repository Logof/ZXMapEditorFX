package org.github.logof.ZXMapEditorFX.io;

import org.github.logof.ZXMapEditorFX.entity.MapEntity;
import java.io.File;
import java.io.IOException;

public interface MapWriter {

    void write(File projectFile, MapEntity tileMap) throws IOException;
}
