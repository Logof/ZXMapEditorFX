package org.github.logof.ZXMapEditorFX.io;

import org.github.logof.ZXMapEditorFX.entity.MapEntity;
import java.io.File;
import java.io.IOException;

public interface MapReader {
    MapEntity read(File mapFile) throws IOException;
}
