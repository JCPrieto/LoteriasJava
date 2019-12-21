package es.jklabs.json.loteria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Numeros implements Serializable {

    private static final long serialVersionUID = 621282241381911509L;

    private List<Numero> numeroList;

    public Numeros() {
        numeroList = new ArrayList<>();
    }

    public List<Numero> getNumeroList() {
        return numeroList;
    }

    public void setNumeroList(List<Numero> numeroList) {
        this.numeroList = numeroList;
    }
}
