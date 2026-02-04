package es.jklabs.desktop.gui.utilidades;

import es.jklabs.utilidades.BaseTest;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

class IconUtilsTest extends BaseTest {

    @Test
    void loadIconReturnsIconWhenResourceExists() {
        Icon icon = IconUtils.loadIcon("info.png");

        assertNotNull(icon, "Expected icon to be loaded for existing resource");
        assertTrue(icon instanceof ImageIcon, "Expected a Swing ImageIcon instance");
    }

    @Test
    void loadIconReturnsNullWhenResourceIsMissing() {
        Icon icon = IconUtils.loadIcon("missing-icon.png");

        assertNull(icon, "Expected null for missing icon resource");
    }

    @Test
    void loadIconCachesPreviouslyLoadedIcons() {
        Icon first = IconUtils.loadIcon("info.png");
        Icon second = IconUtils.loadIcon("info.png");

        assertNotNull(first, "Expected icon to be loaded for existing resource");
        assertSame(first, second, "Expected cached icon instance to be reused");
    }
}
