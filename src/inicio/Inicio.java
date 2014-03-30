/**
 * 
 */
package inicio;

import gui.Ventana;

/**
 * @author juanky
 * 
 */
public final class Inicio {

	private static Inicio instance = new Inicio();

	public static Inicio getInstance() {
		return instance;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		final Ventana vent = new Ventana();
		vent.setVisible(true);
	}

	private Inicio() {

	}

}
