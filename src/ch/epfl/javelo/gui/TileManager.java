package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cache system for map tiles images
 *
 * @author Rayan BOUCHENY (327575)
 * @author Loris Tran (341214)
 */
public final class TileManager {
    private final static int RAM_CACHE_CAPACITY = 4;

    private final Path path;
    private final String serverName;

    private final Map<TileId, Image> cache = new LinkedHashMap<>(RAM_CACHE_CAPACITY, 0.75f, true);

    /**
     * Creates the cache system
     *
     * @param path       path of the directory where the cache is stored
     * @param ServerName URL of the server used to download images
     * @throws IOException if there is a problem while creating the cache directory
     */
    public TileManager(Path path, String ServerName) throws IOException {
        this.serverName = ServerName;
        this.path = path;
        if (!Files.exists(path)) Files.createDirectories(path);
    }

    /**
     * The cache system image loader
     *
     * @param tileId TileId(ZoomLevel, X position, Y position) object of the desired tile
     * @return the image, either loaded from the RAM cache, from the DISK cache or freshly downloaded and loaded
     * to the cache.
     * @throws IOException
     */
    public Image imageForTileAt(TileId tileId) throws IOException {
        // check if the RAM cache already has the tile saved
        if (cache.containsKey(tileId)) {
            return cache.get(tileId);
        } else {

            // check if the RAM cache is too big
            if (cache.size() == RAM_CACHE_CAPACITY) {
                cache.remove(cache.entrySet().iterator().next().getKey());
            }

            // check if DISK cache already has the tile saved
            Path imagePath = path.resolve(tileId.zoomLevel + "/" + (int) tileId.x);
            if (!Files.exists(imagePath.resolve((int) tileId.y + ".png"))) {
                WriteDiskCache(tileId, imagePath);
            }

            Image imageNotInMemory = GetImageAtPath(imagePath.resolve((int) tileId.y + ".png"));
            cache.put(tileId, imageNotInMemory);
            return imageNotInMemory;
        }
    }

    /**
     * Download an image from the server to the correct directory
     *
     * @param tileId    TileId(ZoomLevel, X position, Y position) object of the desired tile
     * @param imagePath the path of the image
     * @throws IOException if there is a problem while creating the image file (corrupted, wrong path...)
     */
    private void WriteDiskCache(TileId tileId, Path imagePath) throws IOException {
        URL u = new URL("https://" + serverName + "/" + tileId.zoomLevel + "/" + (int) tileId.x + "/" + (int) tileId.y + ".png");
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");

        if (!Files.exists(imagePath)) Files.createDirectories(imagePath);

        OutputStream o = new FileOutputStream(imagePath.resolve((int) tileId.y + ".png").toString());
        try (InputStream i = c.getInputStream()) {
            i.transferTo(o);
        }
    }

    /**
     * @param path the path of the image you want to get
     * @return returns an Image using a path
     * @throws IOException if there is a problem while reading the image file (corrupted, wrong path...)
     */
    private Image GetImageAtPath(Path path) throws IOException {
        Image image;
        try (InputStream input = new FileInputStream(path.toString())) {
            image = new Image(input);
        }
        return image;
    }

    /**
     * Object that correspond to a tile, with 3 informations packed in
     */
    public record TileId(int zoomLevel, double x, double y) {

        /**
         * @param zoomLevel zoom level of the tile (0-19)
         * @param x         x-coordinates on the tile grid
         * @param y         y-coordinates on the tile grid
         * @throws IllegalArgumentException if the x or y coordinates don't exist in the grid tile
         *                                  at this zoom level.
         */
        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, x, y));
        }

        /**
         * @param zoomLevel zoom level of the tile (0-19)
         * @param x         x-coordinates on the tile grid
         * @param y         y-coordinates on the tile grid
         * @return whether or not the tile exist at this zoom level
         */
        public static boolean isValid(int zoomLevel, double x, double y) {
            int maxCoordinates = (int) Math.pow(2, 2 + zoomLevel) / 4;
            return (x < maxCoordinates && y < maxCoordinates);
        }
    }
}
