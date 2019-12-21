package es.jklabs.gui.paneles;

import es.jklabs.gui.Ventana;
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
import java.util.stream.Collectors;

public class MisNumeros extends JPanel {
    private final Ventana padre;

    public MisNumeros(Ventana padre) {
        super();
        this.padre = padre;
        cargarElementos();
    }

    private void cargarElementos() {
        JPanel panelCombos = new JPanel(new FlowLayout());
        JLabel lbComboSorteo = new JLabel(Mensajes.getMensaje("sorteo"));
        panelCombos.add(lbComboSorteo);
        JComboBox comboSorteos = new JComboBox(Sorteo.values());
        comboSorteos.addActionListener(l -> actualizarPanelNumeros());
        panelCombos.add(comboSorteos);
        JLabel lbComboAnyo = new JLabel(Mensajes.getMensaje("anyo"));
        panelCombos.add(lbComboAnyo);
        JComboBox comboAnyos = new JComboBox(getAnyos());
        comboAnyos.addActionListener(l -> actualizarPanelNumeros());
        panelCombos.add(comboAnyos);
        JPanel panelNumeros = new JPanel();
        super.add(panelCombos, BorderLayout.NORTH);
        super.add(panelNumeros, BorderLayout.CENTER);
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
