package es.jklabs.gui.utilidades.listener;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class IntegerKeyListener implements KeyListener {

    private final JTextField jTextField;

    public IntegerKeyListener(JTextField jTextField) {
        this.jTextField = jTextField;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        final char caracter = e.getKeyChar();
        if ((caracter < '0') || (caracter > '9') || jTextField.getText().length() == 5) {
            e.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent evt) {

    }
}
