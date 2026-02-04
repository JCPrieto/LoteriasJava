package es.jklabs.desktop.gui.utilidades;

import javax.swing.*;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IconUtils {

    private static final String ICONS_DIR = "img/icons/";
    private static final Map<String, ImageIcon> ICON_CACHE = new ConcurrentHashMap<>();

    public static Icon loadIcon(String name) {
        return ICON_CACHE.computeIfAbsent(name, IconUtils::loadIconInternal);
    }

    private static ImageIcon loadIconInternal(String name) {
        URL url = IconUtils.class.getClassLoader().getResource(ICONS_DIR + name);
        if (url == null) {
            return null;
        }
        return new ImageIcon(url);
    }
}
