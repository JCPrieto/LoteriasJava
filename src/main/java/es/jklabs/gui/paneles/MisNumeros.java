package es.jklabs.gui.paneles;

import es.jklabs.gui.Ventana;
import es.jklabs.gui.utilidades.listener.IntegerKeyListener;
import es.jklabs.json.loteria.Numero;
import es.jklabs.json.loteria.Numeros;
import es.jklabs.lib.loteria.enumeradores.Sorteo;
import es.jklabs.utilidades.Mensajes;
import es.jklabs.utilidades.UtilidadesNumeros;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MisNumeros extends JPanel {
    private final Ventana padre;
    private JComboBox comboSorteos;
    private JComboBox comboAnyos;
    private JTextField txNumero;

    public MisNumeros(Ventana padre) {
        super();
        this.padre = padre;
        cargarElementos();
    }

    private void cargarElementos() {
        JPanel panelCombos = new JPanel(new FlowLayout());
        JLabel lbComboSorteo = new JLabel(Mensajes.getMensaje("sorteo"));
        panelCombos.add(lbComboSorteo);
        comboSorteos = new JComboBox(Sorteo.values());
        comboSorteos.addActionListener(l -> actualizarPanelNumeros());
        panelCombos.add(comboSorteos);
        JLabel lbComboAnyo = new JLabel(Mensajes.getMensaje("anyo"));
        panelCombos.add(lbComboAnyo);
        comboAnyos = new JComboBox(getAnyos());
        comboAnyos.setSelectedItem(LocalDate.now().getYear());
        comboAnyos.addActionListener(l -> actualizarPanelNumeros());
        panelCombos.add(comboAnyos);
        JLabel lbTextNumero = new JLabel(Mensajes.getMensaje("numero"));
        panelCombos.add(lbTextNumero);
        txNumero = new JTextField(5);
        txNumero.addKeyListener(new IntegerKeyListener(txNumero));
        panelCombos.add(txNumero);
        JButton jbAddNumero = new JButton();
        jbAddNumero.setToolTipText(Mensajes.getMensaje("add.numero"));
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/icons/plus.png")));
        Image image = imageIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
        jbAddNumero.setIcon(new ImageIcon(image));
        jbAddNumero.addActionListener(l -> addNumero());
        panelCombos.add(jbAddNumero);
        JPanel panelNumeros = new JPanel();
        super.add(panelCombos, BorderLayout.NORTH);
        super.add(panelNumeros, BorderLayout.CENTER);
    }

    private void addNumero() {
        Numeros numeros = UtilidadesNumeros.getNumeros();
        if (numeros.getNumeroList().stream().noneMatch(n ->
                Objects.equals(n.getSorteo(), comboSorteos.getSelectedItem()) &&
                        Objects.equals(n.getAnyo(), comboAnyos.getSelectedItem()) &&
                        Objects.equals(n.getNum(), txNumero.getText()))) {
            Numero numero = new Numero();
            numero.setSorteo((Sorteo) comboSorteos.getSelectedItem());
            numero.setAnyo((Integer) comboAnyos.getSelectedItem());
            numero.setNum(txNumero.getText());
        } else {

        }
        actualizarPanelNumeros();
    }

    private void actualizarPanelNumeros() {
        //ToDo
    }

    private Integer[] getAnyos() {
        Numeros numeros = UtilidadesNumeros.getNumeros();
        List<Integer> anyos = numeros.getNumeroList().stream().map(Numero::getAnyo).collect(Collectors.toList());
        if (!anyos.contains(LocalDate.now().getYear())) {
            anyos.add(LocalDate.now().getYear());
        }
        anyos.sort(Comparator.naturalOrder());
        return anyos.toArray(new Integer[0]);
    }
}
