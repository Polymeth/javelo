package ch.epfl.javelo.gui;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Executable;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TileManagerTest {

    @Test
    void imageForTileAt() throws Exception {
        TileManager manager = new TileManager(Path.of("cache"), "tile.openstreetmap.org");
        manager.imageForTileAt(new TileManager.TileId(19, 271725, 185422));
    }
}