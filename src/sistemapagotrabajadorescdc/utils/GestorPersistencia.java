package sistemapagotrabajadorescdc.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import sistemapagotrabajadorescdc.model.Proyecto;
import sistemapagotrabajadorescdc.model.Trabajador;

import javax.swing.*;

public class GestorPersistencia {
    private static final String ARCHIVO_DATOS = "trabajadores.dat";
    private static final String ARCHIVO_LOG = "historial_cambios.log";
    public static void guardarTrabajadores(LinkedList<Trabajador> trabajadores) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_DATOS))) {
            for (Trabajador t : trabajadores) {
                writer.println(serializarTrabajador(t));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar los datos: ", e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public static LinkedList<Trabajador> cargarTrabajadores() {
        LinkedList<Trabajador> trabajadores = new LinkedList<>();
        File archivo = new File(ARCHIVO_DATOS);
        
        if (!archivo.exists()) {
            return trabajadores;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_DATOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                Trabajador t = deserializarTrabajador(linea);
                if (t != null) {
                    trabajadores.add(t);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return trabajadores;
    }

    // Serializar Trabajador
    private static String serializarTrabajador(Trabajador t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.getCodigo()).append("|");
        sb.append(t.getNombre()).append("|");
        sb.append(t.getDepto()).append("|");
        sb.append(t.getCargo()).append("|");
        sb.append(t.getGrupoEscala()).append("|");
        sb.append(t.getOcupBarco()).append("|");
        sb.append(t.isEsBaja()).append("|");
        sb.append(t.isTieneClave269()).append("|");
        sb.append(t.isTieneClave271()).append("|");
        sb.append(t.isTieneClave278()).append("|");
        sb.append(t.getFechaUltimaModificacion()).append("|"); // Marca de tiempo
        
        // Serializar proyectos
        sb.append("[");
        for (Proyecto p : t.getProyectos()) {
            sb.append(p.getNumeroProyecto()).append(",");
            sb.append(p.isEsCertificado()).append(",");
            sb.append(p.getHorasTrab()).append(",");
            sb.append(p.getHorasExtra()).append(";");
        }
        sb.append("]");
        
        return sb.toString();
    }

        // Deserializar Trabajador
    private static Trabajador deserializarTrabajador(String linea) {
        try {
            String[] partes = linea.split("\\|", -1); // Separar elementos por cada "|" e incluir espacios vacíos del final de la cadena

            int codigo = Integer.parseInt(partes[0]);
            String nombre = partes[1];
            String depto = partes[2];
            String cargo = partes[3];
            String grupoEscala = partes[4];
            String ocupBarco = partes[5];
            boolean esBaja = Boolean.parseBoolean(partes[6]);
            boolean clave269 = Boolean.parseBoolean(partes[7]);
            boolean clave271 = Boolean.parseBoolean(partes[8]);
            boolean clave278 = Boolean.parseBoolean(partes[9]);
            String fechaModificacion = partes[10];

            // Deserializar proyectos
            ArrayList<Proyecto> proyectos = new ArrayList<>();
            String proyectosStr = partes[11].substring(1, partes[11].length() - 1);
            if (!proyectosStr.isEmpty()) {
                String[] proyectosArray = proyectosStr.split(";");
                for (String p : proyectosArray) {
                    if (!p.isEmpty()) {
                        String[] datosProyecto = p.split(",");
                        int numProyecto = Integer.parseInt(datosProyecto[0]);
                        boolean certificado = Boolean.parseBoolean(datosProyecto[1]);
                        double horasTrabProyecto = Double.parseDouble(datosProyecto[2]);
                        double horasExtraProyecto = Double.parseDouble(datosProyecto[3]);
                        proyectos.add(new Proyecto(numProyecto, certificado, horasTrabProyecto, horasExtraProyecto));
                    }
                }
            }

            Trabajador t = new Trabajador(grupoEscala, cargo, depto,
                                         codigo, nombre, ocupBarco, proyectos, esBaja,
                                         clave271, clave278, clave269);

            if (!fechaModificacion.equals("No modificado aún")) {
                t.setFechaUltimaModificacion(fechaModificacion);
            }

            return t;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,"Hubo un error al cargar los datos de un trabajador: " + e.getMessage(), "Error al cargar la aplicación",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public static String obtenerHistorialCambios() {
        File archivo = new File(ARCHIVO_LOG);
        if (!archivo.exists()) {
            return "No hay historial de cambios registrado";
        }

        StringBuilder historial = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_LOG))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                historial.append(linea).append("\n");
            }
        } catch (IOException e) {
            return "Error al leer historial: " + e.getMessage();
        }
        return historial.toString();
    }

    public static void limpiarHistorial() throws IOException {
        File archivo = new File(ARCHIVO_LOG);
        if (archivo.exists()) {
            archivo.delete();
        }
    }
    
        public static void resetBaseDatos() throws IOException {
         File archivo = new File(ARCHIVO_DATOS);
            if (archivo.exists()) {
                archivo.delete();
            }
        }

}