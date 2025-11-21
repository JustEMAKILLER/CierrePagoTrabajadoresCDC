package sistemapagotrabajadorescdc.controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;
import sistemapagotrabajadorescdc.model.Proyecto;
import sistemapagotrabajadorescdc.model.Trabajador;
import sistemapagotrabajadorescdc.utils.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JOptionPane;

public class SistemaPagoTrabajadoresCDC {
    private final LinkedList<Trabajador> trabajadores;
    
    public SistemaPagoTrabajadoresCDC() {
        trabajadores = GestorPersistencia.cargarTrabajadores();
    }

    public LinkedList<Trabajador> getTrabajadores() {
        return trabajadores;
    }
    
    public void agregarTrabajador(Trabajador trabajador) {
        trabajadores.add(trabajador);
        GestorPersistencia.guardarTrabajadores(trabajadores);
        RegistroCambios.registrarCambio("ALTA", trabajador.getCodigo(), 
            "Nuevo trabajador: " + trabajador.getNombre() + " - Depto: " + trabajador.getDepto());
    }
    
    public void modificarTrabajador(Trabajador trabajador) {
        trabajador.setFechaUltimaModificacion();
        GestorPersistencia.guardarTrabajadores(trabajadores);
        RegistroCambios.registrarCambio("MODIFICACION", trabajador.getCodigo(), 
            "Modificación de datos del trabajador");
    }
    
    public void eliminarTrabajador(Trabajador trabajador) {
        int codigo = trabajador.getCodigo();
        String nombre = trabajador.getNombre();
        trabajadores.remove(trabajador);
        GestorPersistencia.guardarTrabajadores(trabajadores);
        RegistroCambios.registrarCambio("BAJA", codigo, "Trabajador eliminado: " + nombre);
    }
    
    // Métodos para generar resúmenes
    public String generarResumenTrabajadores() {
        if (trabajadores.isEmpty()) {
            return "No hay trabajadores registrados en el sistema.";
        }

        int totalConDerecho = 0;
        int totalSinDerecho = 0;
        double montoTotalConDerecho = 0;
        LinkedList<Trabajador> trabajadoresConDerecho = new LinkedList<>();
        LinkedList<Trabajador> trabajadoresSinDerecho = new LinkedList<>();

        for (Trabajador t : trabajadores) {
            if (t.tieneDerechoACobro()) {
                trabajadoresConDerecho.add(t);
                totalConDerecho++;
                montoTotalConDerecho += t.getPagoTotal();
            } else {
                trabajadoresSinDerecho.add(t);
                totalSinDerecho++;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMEN DE TRABAJADORES CON DERECHO A COBRO ===\n");
        sb.append("Total: ").append(totalConDerecho).append("\n");

        if (trabajadoresConDerecho.isEmpty()) {
            sb.append("No hay trabajadores con derecho a cobro en el sistema.\n");
        } else {
            for (Trabajador t : trabajadoresConDerecho) {
                sb.append("- ").append(t.getNombre())
                  .append(" (Código: ").append(t.getCodigo())
                  .append(") | Depto: ").append(t.getDepto())
                  .append(" | Pago total: ").append(t.getPagoTotal()).append(" CUP\n");
                t.mostrarProyectosNoCertificados(sb);
                sb.append("-----------------------\n");
            }
            sb.append("Total Barco Extranjero: ").append(montoTotalConDerecho).append(" CUP\n");
        }

        sb.append("\n=== RESUMEN DE TRABAJADORES SIN DERECHO A COBRO ===\n");
        sb.append("Total: ").append(totalSinDerecho).append("\n");

        if (trabajadoresSinDerecho.isEmpty()) {
            sb.append("No hay trabajadores sin derecho a cobro en el sistema.\n");
        } else {
            for (Trabajador t : trabajadoresSinDerecho) {
                sb.append("- ").append(t.getNombre())
                  .append(" (Código: ").append(t.getCodigo())
                  .append(") | Depto: ").append(t.getDepto()).append("\n");
                t.mostrarProyectos(sb);
                t.mostrarEstadoPago(sb);
            }
        }

        sb.append("\n=== RESUMEN GENERAL ===\n");
        sb.append("Cantidad total de trabajadores: ").append(totalConDerecho + totalSinDerecho).append("\n");

        double montoTotalD = calcularMontoTotalPorOcupacion("D", trabajadoresConDerecho);
        double montoTotalDA = calcularMontoTotalPorOcupacion("DA", trabajadoresConDerecho);
        
        sb.append("Monto total del personal D: ").append(montoTotalD).append(" CUP\n");
        sb.append("Monto total del personal DA: ").append(montoTotalDA).append(" CUP\n");

        double totalHorasTrabProyCert = calcularTotalHorasTrabajadasCertificadas(trabajadoresConDerecho);
        sb.append("Total de horas trabajadas de los proyectos certificados: ")
          .append(totalHorasTrabProyCert).append("h\n");
        sb.append("Monto total de los proyectos certificados: ")
          .append(montoTotalConDerecho).append(" CUP\n");

        return sb.toString();
    }
    
    private double calcularMontoTotalPorOcupacion(String ocupacion, List<Trabajador> trabajadores) {
        return trabajadores.stream()
            .filter(t -> t.getOcupBarco().equals(ocupacion))
            .mapToDouble(Trabajador::calcularPagoTotal)
            .sum();
    }
    
    private double calcularTotalHorasTrabajadasCertificadas(List<Trabajador> trabajadores) {
        return trabajadores.stream()
            .flatMap(t -> t.getProyectos().stream()) // Flujo de todos los proyectos de todos los trabajadores
            .filter(Proyecto::isEsCertificado)
            .mapToDouble(Proyecto::getHorasTrab)
            .sum();
    }
    
    public Map<String, List<Trabajador>> agruparTrabajadoresPorDepartamento() {
        Map<String, List<Trabajador>> trabajadoresPorDepto = new TreeMap<>();
        for (Trabajador t : trabajadores) {
            trabajadoresPorDepto
                .computeIfAbsent(t.getDepto(), k -> new LinkedList<>()) // Si no existe el depto de t en el flujo se crea
                .add(t);
        }
        return trabajadoresPorDepto;
    }
    
    public String obtenerHistorialCambios() {
        return GestorPersistencia.obtenerHistorialCambios();
    }
    
    public void limpiarHistorial() {
        try {
            GestorPersistencia.limpiarHistorial();
        }
        catch (IOException e){
            JOptionPane.showMessageDialog(null,"Error al eliminar el historial: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
    }
    
    public void imprimirResumen(){
        String contenido = generarResumenTrabajadores();
try (BufferedWriter writer = new BufferedWriter(new FileWriter("Resumen.txt"))) {
        writer.write(contenido);
                     JOptionPane.showMessageDialog(null, 
                    "Resumen exportado exitosamente.",
                    "Exportación exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,
                            "Error al generar archivo de resumen: " + e.getMessage(),
                            "Exportación fallida",
                            JOptionPane.ERROR_MESSAGE);
    }
    }
}