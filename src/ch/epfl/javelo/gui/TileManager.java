package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class TileManager {

    Map<Integer, Image> cache = new LinkedHashMap<>();

    public TileManager(Path path, String ServerName){

    }

    // todo: mieux gérer les exceptions
    public Image imageForTileAt(int tileId) throws Exception {
        if(cache.get(tileId) != null){
            return cache.get(tileId);

        } else { // todo: bouger ça dans une condition "if not dans le cache disk"
            URL u = new URL("https://tile.openstreetmap.org/19/271725/185422.png");
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");

            OutputStream o = new FileOutputStream("cache/test/yolo.png");
            try(InputStream i = c.getInputStream()) {
                i.transferTo(o);
            }



        }


        return null;
    }


    public record TileId(int zoomLevel, double x, double y) {
        public static boolean isValid(int zoomLevel, double x, double y) {
            return false;
        }
    }
}
