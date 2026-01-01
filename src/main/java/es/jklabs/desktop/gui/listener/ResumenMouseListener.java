package es.jklabs.desktop.gui.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResumenMouseListener implements MouseListener {

    private static final String CRITICO = "Error Critico";

    private final JLabel pdf;

    public ResumenMouseListener(JLabel pdf) {
        this.pdf = pdf;
    }

    private static URI buildUri(String url) throws URISyntaxException {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            int schemeEnd = url.indexOf("://");
            if (schemeEnd < 0) {
                throw e;
            }
            String scheme = url.substring(0, schemeEnd);
            String rest = url.substring(schemeEnd + 3);
            int pathStart = rest.indexOf('/');
            String authority = pathStart < 0 ? rest : rest.substring(0, pathStart);
            String pathAndMore = pathStart < 0 ? "" : rest.substring(pathStart);
            String fragment = null;
            int fragmentStart = pathAndMore.indexOf('#');
            if (fragmentStart >= 0) {
                fragment = pathAndMore.substring(fragmentStart + 1);
                pathAndMore = pathAndMore.substring(0, fragmentStart);
            }
            String query = null;
            int queryStart = pathAndMore.indexOf('?');
            if (queryStart >= 0) {
                query = pathAndMore.substring(queryStart + 1);
                pathAndMore = pathAndMore.substring(0, queryStart);
            }
            String path = pathAndMore.isEmpty() ? null : pathAndMore;
            String rawPath = path == null ? null : new URI(null, null, path, null).getRawPath();
            String rawQuery = query == null ? null : new URI(null, null, null, query, null).getRawQuery();
            String rawFragment = fragment == null ? null : new URI(null, null, null, null, fragment).getRawFragment();
            StringBuilder rebuilt = new StringBuilder();
            rebuilt.append(scheme).append("://").append(authority);
            if (rawPath != null) {
                rebuilt.append(rawPath);
            }
            if (rawQuery != null) {
                rebuilt.append('?').append(rawQuery);
            }
            if (rawFragment != null) {
                rebuilt.append('#').append(rawFragment);
            }
            return URI.create(rebuilt.toString());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == pdf) {
            try {
                URI uri = buildUri(pdf.getText());
                Desktop.getDesktop().browse(uri);
                pdf.setForeground(Color.red);
            } catch (IOException | URISyntaxException e1) {
                Logger.getLogger("PDF").log(Level.SEVERE, CRITICO, e1);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //
    }
}
