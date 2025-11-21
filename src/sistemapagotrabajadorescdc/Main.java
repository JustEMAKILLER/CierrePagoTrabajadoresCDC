
package sistemapagotrabajadorescdc;

import sistemapagotrabajadorescdc.viewer.GUI;
import javax.swing.SwingUtilities;

public class Main {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI interfaz = new GUI();
            interfaz.setVisible(true);
        });       
    }
}
