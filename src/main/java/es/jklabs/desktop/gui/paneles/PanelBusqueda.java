package es.jklabs.desktop.gui.paneles;

import es.jklabs.desktop.gui.Ventana;
import es.jklabs.utilidades.Logger;
import es.jklabs.utilidades.Mensajes;
import io.github.jcprieto.lib.loteria.conexion.Conexion;
import io.github.jcprieto.lib.loteria.enumeradores.EstadoSorteo;
import io.github.jcprieto.lib.loteria.enumeradores.Sorteo;
import io.github.jcprieto.lib.loteria.excepciones.PremioDecimoNoDisponibleException;
import io.github.jcprieto.lib.loteria.model.Premio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serial;
import java.util.concurrent.ExecutionException;

/**
 * @author juanky
 */
public class PanelBusqueda extends JPanel implements ActionListener {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3L;
    private final transient Ventana padre;
    private transient JButton buscar;
    private transient JButton limpiar;
    private transient JTextField numero;
    private transient JPanel resultado;
    private final Sorteo sorteo;
    private JTextField cantidad;
    private GridBagConstraints cns;
    private int contador;
    private boolean buscando;

    PanelBusqueda(final Ventana ventana, Sorteo sorteo) {
        super();
        padre = ventana;
        this.sorteo = sorteo;
        cargarElementos();
    }


    public void actionPerformed(final ActionEvent evt) {
        if (evt.getSource() == buscar && !numero.getText().isEmpty()) {
            buscarPremioAsync(numero.getText(), cantidad.getText());
        }
        if (evt.getSource() == limpiar) {
            resultado.removeAll();
            resultado.repaint();
            numero.setText("");
            cantidad.setText("");
            contador = 0;
            padre.pack();
        }
    }

    private void buscarPremioAsync(final String text, String cantidadText) {
        if (buscando) {
            return;
        }
        java.math.BigDecimal cantidadValor = parseCantidad(cantidadText);
        if (cantidadValor == null) {
            showWarning(Mensajes.getMensaje("warning.cantidad.invalida"));
            return;
        }
        setBuscando(true);
        SwingWorker<Premio, Void> worker = new SwingWorker<>() {
            @Override
            protected Premio doInBackground() throws Exception {
                Conexion c = createConexion();
                return c.getPremio(sorteo, text);
            }

            @Override
            protected void done() {
                try {
                    Premio premio = get();
                    if (premio == null || premio.getEstado() == EstadoSorteo.NO_INICIADO) {
                        showWarning(Mensajes.getMensaje("warning.no.datos"));
                    } else {
                        cns.gridy = contador++;
                        resultado.add(new Resultado(text, premio.getCantidad(), cantidadValor), cns);
                        contador++;
                        padre.pack();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof PremioDecimoNoDisponibleException) {
                        showWarning(cause.getMessage());
                    } else {
                        Logger.error("Buscar premio", e);
                        showWarning(Mensajes.getMensaje("warning.problema.servidor"));
                    }
                } catch (Exception e) {
                    Logger.error("Buscar premio", e);
                    showWarning(Mensajes.getMensaje("warning.problema.servidor"));
                } finally {
                    setBuscando(false);
                }
            }
        };
        worker.execute();
    }

    protected Conexion createConexion() {
        return new Conexion();
    }

    protected void showWarning(String message) {
        JOptionPane.showMessageDialog(padre, message, Mensajes.getMensaje("dialogo.atencion"), JOptionPane.WARNING_MESSAGE);
    }

    private void setBuscando(boolean activo) {
        buscando = activo;
        buscar.setEnabled(!activo);
        limpiar.setEnabled(!activo);
        setCursor(activo ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private java.math.BigDecimal parseCantidad(String cantidadText) {
        if (cantidadText == null) {
            return null;
        }
        String normalized = cantidadText.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        try {
            java.math.BigDecimal cantidad = new java.math.BigDecimal(normalized);
            return cantidad.compareTo(java.math.BigDecimal.ZERO) > 0 ? cantidad : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void cargarElementos() {
        super.setBorder(new EmptyBorder(10, 10, 10, 10));
        super.setLayout(new BorderLayout());
        final JPanel entrada = new JPanel();
        numero = new JTextField(5);
        numero.addKeyListener(new KeyListener() {


            public void keyPressed(final KeyEvent evt) {
                //
            }


            public void keyReleased(final KeyEvent evt) {
                //
            }


            public void keyTyped(final KeyEvent evt) {
                final char caracter = evt.getKeyChar();
                if (((caracter < '0') || (caracter > '9'))
                        && (caracter != '\b') || numero.getText().length() > 4) {
                    evt.consume();
                }
            }
        });
        cantidad = new JTextField(3);
        cantidad.addKeyListener(new KeyListener() {


            public void keyTyped(KeyEvent e) {
                final char caracter = e.getKeyChar();
                if (((caracter < '0') || (caracter > '9')) && (caracter != '.')) {
                    e.consume();
                }
            }


            public void keyPressed(KeyEvent e) {
                //
            }


            public void keyReleased(KeyEvent e) {
                //
            }
        });
        buscar = new JButton(Mensajes.getMensaje("panel.buscar.premio"));
        buscar.addActionListener(this);
        entrada.add(new JLabel(Mensajes.getMensaje("panel.numero")));
        entrada.add(numero);
        entrada.add(new JLabel(Mensajes.getMensaje("panel.cantidad.jugada")));
        entrada.add(cantidad);
        entrada.add(new JLabel(Mensajes.getMensaje("panel.euros")));
        entrada.add(buscar);
        limpiar = new JButton(Mensajes.getMensaje("panel.limpiar.resultados"));
        limpiar.addActionListener(this);
        resultado = new JPanel();
        resultado.setLayout(new GridBagLayout());
        cns = new GridBagConstraints();
        cns.gridx = 0;
        cns.gridy = 0;
        cns.gridwidth = 1;
        cns.weightx = 1;
        cns.insets = new Insets(10, 0, 10, 0);
        JScrollPane scrollPane = new JScrollPane(resultado);
        super.add(entrada, BorderLayout.NORTH);
        super.add(limpiar, BorderLayout.SOUTH);
        super.add(scrollPane, BorderLayout.CENTER);
    }

}
