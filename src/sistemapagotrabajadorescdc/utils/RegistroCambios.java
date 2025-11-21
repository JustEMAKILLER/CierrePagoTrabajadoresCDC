package sistemapagotrabajadorescdc.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistroCambios {
    public static final String ARCHIVO_LOG = "historial_cambios.log";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void registrarCambio(String accion, int codigoTrabajador, String detalles) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_LOG, true))) {
            String entrada = String.format("[%s] %s - Trabajador %d - %s%n",
                    dateFormat.format(new Date()),
                    accion,
                    codigoTrabajador,
                    detalles);
            writer.print(entrada);
        } catch (IOException e) {
            System.err.println("Error al registrar cambio: " + e.getMessage());
        }
    }

    public static void mostrarHistorial() {
        File archivo = new File(ARCHIVO_LOG);
        if (!archivo.exists()) {
            System.out.println("No hay historial de cambios registrado");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_LOG))) {
            System.out.println("\n=== HISTORIAL DE CAMBIOS ===");
            String linea;
            while ((linea = reader.readLine()) != null) {
                System.out.println(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al leer historial: " + e.getMessage());
        }
    }
}