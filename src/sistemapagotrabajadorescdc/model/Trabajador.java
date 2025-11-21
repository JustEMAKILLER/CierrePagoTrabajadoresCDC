package sistemapagotrabajadorescdc.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Trabajador {
    private String fechaUltimaModificacion;
    private String grupoEscala;
    private int tarifa;
    private double pagoTotal;
    private String cargo;
    private String depto;
    private int codigo;
    private String nombre;
    private String ocupBarco;
    private ArrayList<Proyecto> proyectos;
    private boolean esBaja;
    private boolean tieneClave271;
    private boolean tieneClave269;
    private boolean nadaCertificado;
    private int cantidadClaves269;
    private boolean tieneClave278;

    public Trabajador(String nombre, int codigo) {
        this.nombre = nombre;
        this.codigo = codigo;
    }

    public Trabajador(String grupoEscala, String cargo, String depto,
                     int codigo, String nombre, String ocupBarco, ArrayList<Proyecto> proyectos, 
                     boolean esBaja, boolean tieneClave271, boolean tieneClave278, boolean tieneClave269) {
                    this.grupoEscala = grupoEscala;
                    this.cargo = cargo;
                    this.depto = depto;
                    this.codigo = codigo;
                    this.nombre = nombre;
                    this.ocupBarco = ocupBarco;
                    this.proyectos = proyectos != null ? new ArrayList<>(proyectos) : new ArrayList<>();
                    this.esBaja = esBaja;
                    this.tieneClave271 = tieneClave271;
                    this.tieneClave269 = tieneClave269;
                    this.cantidadClaves269 = tieneClave269 ? 1 : 0;
                    this.tieneClave278 = tieneClave278;
                    this.tarifa = calcularTarifa(grupoEscala, ocupBarco);
                    this.pagoTotal = calcularPagoTotal();
                    this.fechaUltimaModificacion = "No modificado aún";
    }

// Método para verificar derecho a cobro
    public boolean tieneDerechoACobro() {
        // No tiene derecho si tiene proyectos no certificados
        if (isNadaCertificado()) {
            return false;
        }

        // No tiene derecho si está de baja
        if (this.esBaja) {
            return false;
        }
        
        // No tiene derecho si tiene alguna clave 271 o 278
        if (this.tieneClave271 || this.tieneClave278) {
            return false;
        }
        
        // Para la clave 269, necesita tener 2 o más
        if (this.cantidadClaves269 >= 2) {
            return false;
        }
        return true;
    }
    
        // Método para mostrar estado de pago
    public void mostrarEstadoPago(StringBuilder sb) {
        sb.append("\n--- Estado de pago ---\n");
        if (!tieneDerechoACobro()) {
            sb.append("El trabajador NO tiene derecho a cobro\nMotivos:\n");
            if (isNadaCertificado()) sb.append("- No tiene proyectos certificados\n");
            if (isEsBaja()) sb.append("- Está dado de baja\n");
            if (isTieneClave271()) sb.append("- Tiene clave 271\n");
            if (isTieneClave278()) sb.append("- Tiene clave 278\n");
            if (getCantidadClaves269() >= 2)
                sb.append("- Tiene ").append(getCantidadClaves269()).append(" claves 269\n");
        } else {
            sb.append("El trabajador tiene derecho a cobro\n");
        }
    }
    
   public int calcularTarifa(String grupoEscala, String ocupBarco) {
        if (grupoEscala.equalsIgnoreCase("II") && "D".equalsIgnoreCase(ocupBarco)) return 35;
        if (grupoEscala.equalsIgnoreCase("IV") && "D".equalsIgnoreCase(ocupBarco)) return 37;
        if (grupoEscala.equalsIgnoreCase("V") && "D".equalsIgnoreCase(ocupBarco)) return 38;
        if (grupoEscala.equalsIgnoreCase("VI") && "D".equalsIgnoreCase(ocupBarco)) return 40;
        if (grupoEscala.equalsIgnoreCase("VII") && "D".equalsIgnoreCase(ocupBarco)) return 45;
        if (grupoEscala.equalsIgnoreCase("VIII") && "D".equalsIgnoreCase(ocupBarco)) return 50;
        if (grupoEscala.equalsIgnoreCase("X") && "D".equalsIgnoreCase(ocupBarco)) return 55;
        if ((grupoEscala.equalsIgnoreCase("XI") || grupoEscala.equalsIgnoreCase("XIV") || grupoEscala.equalsIgnoreCase("XV") || grupoEscala.equalsIgnoreCase("XVII")) && "D".equalsIgnoreCase(ocupBarco)) return 60;
        if (grupoEscala.equalsIgnoreCase("II") && "DA".equalsIgnoreCase(ocupBarco)) return 33;
        if (grupoEscala.equalsIgnoreCase("IV") && "DA".equalsIgnoreCase(ocupBarco)) return 35;
        if (grupoEscala.equalsIgnoreCase("V") && "DA".equalsIgnoreCase(ocupBarco)) return 36;
        if (grupoEscala.equalsIgnoreCase("VI") && "DA".equalsIgnoreCase(ocupBarco)) return 37;
        if (grupoEscala.equalsIgnoreCase("VII") && "DA".equalsIgnoreCase(ocupBarco)) return 38;
        if (grupoEscala.equalsIgnoreCase("X") && "DA".equalsIgnoreCase(ocupBarco)) return 40;
        if (grupoEscala.equalsIgnoreCase("XI") && "DA".equalsIgnoreCase(ocupBarco)) return 42;
        if (grupoEscala.equalsIgnoreCase("XIV") && "DA".equalsIgnoreCase(ocupBarco)) return 43;
        if (grupoEscala.equalsIgnoreCase("XV") && "DA".equalsIgnoreCase(ocupBarco)) return 44;
        if (grupoEscala.equalsIgnoreCase("XVI") && "DA".equalsIgnoreCase(ocupBarco)) return 46;
        if (grupoEscala.equalsIgnoreCase("XVII") && "DA".equalsIgnoreCase(ocupBarco)) return 49;
        if (grupoEscala.equalsIgnoreCase("XVIII") && "DA".equalsIgnoreCase(ocupBarco)) return 52;
        if ((grupoEscala.equalsIgnoreCase("XIX") || grupoEscala.equalsIgnoreCase("XX")) && "D".equalsIgnoreCase(ocupBarco)) return 54;
        if (grupoEscala.equalsIgnoreCase("XXI") && "DA".equalsIgnoreCase(ocupBarco)) return 57;
        if (grupoEscala.equalsIgnoreCase("XXII") && "DA".equalsIgnoreCase(ocupBarco)) return 60;
        else return 0;
    }
    
    public double calcularPagoTotal() {
        return calcularPagoTotalHTrab() + calcularPagoTotalHExtra();
    }
    
    public double calcularPagoTotalHExtra() {
       double PagoTotalHExtra = 0;
        for (Proyecto proyecto : proyectos){
            if(proyecto.isEsCertificado()) PagoTotalHExtra += proyecto.getHorasExtra() * tarifa;
        }
        return PagoTotalHExtra;
    }
   
    public double calcularPagoTotalHTrab() {
       double PagoTotalHTrab = 0;
        for (Proyecto proyecto : proyectos){
            if(proyecto.isEsCertificado()) PagoTotalHTrab += proyecto.getHorasTrab() * tarifa;
        }
        return PagoTotalHTrab;
    }
   
    public void mostrarProyectos(StringBuilder sb) {
        sb.append("--- Proyectos ---\n");
        if (proyectos.isEmpty()){
            sb.append("El trabajador no tiene ningún proyecto asociado.\n");
            return;
        }
        sb.append("Total: ").append(proyectos.size()).append("\n");
        for (Proyecto proyecto : getProyectos()) {
            sb.append("Proyecto ").append(proyecto.getNumeroProyecto())
                    .append(" -> Certificado: ").append(proyecto.isEsCertificado() ? "Sí" : "No").append(" | Horas trabajadas: ").append(proyecto.getHorasTrab()).append(" | Pago por horas trabajadas: ").append(proyecto.getHorasTrab() * tarifa).append(" CUP | Horas extra: ").append(proyecto.getHorasExtra())
                    .append(" | Pago por horas extra: ").append(proyecto.getHorasExtra() * tarifa)
                    .append(" CUP\n");
        }
            sb.append("-----------\n");
    }
    
public void mostrarProyectosNoCertificados(StringBuilder sb) {
    boolean tieneNoCertificados = false;
    for (Proyecto proyecto : proyectos) {
        if (!proyecto.isEsCertificado()) {
            if (!tieneNoCertificados) {
                sb.append("Proyectos NO certificados:\n");
                tieneNoCertificados = true;
            }
            sb.append("- Proyecto ").append(proyecto.getNumeroProyecto()).append(" | Horas trabajadas: ").append(proyecto.getHorasTrab()).append(" | Pago por horas trabajadas: ").append(proyecto.getHorasTrab() * tarifa).append(" CUP | Horas Extra: ").append(proyecto.getHorasExtra()).append(" | Pago por horas extra: ").append(proyecto.getHorasExtra() * tarifa).append(" CUP\n");
        }
    }
    if (!tieneNoCertificados) {
        sb.append("El trabajador no tiene proyectos sin certificar.\n");
    }
}
    
    public double hallarCantTotalHExtra(){
        double totalHExtra = 0;
        for (Proyecto proyecto : proyectos){
            double calculo = proyecto.getHorasExtra();
            totalHExtra += calculo;
    }
        return totalHExtra;
    }
    
    public double hallarCantTotalHTrab() {
    double totalHTrab = 0;
    for (Proyecto proyecto : proyectos) {
        totalHTrab += proyecto.getHorasTrab();
    }
    return totalHTrab;
}

    public void mostrarInformacionCompleta(StringBuilder sb) {
        // Construir el texto con los detalles
        sb.append("--- Datos del Trabajador ---\n");
        sb.append("Nombre: ").append(this.nombre).append("\n");
        sb.append("Departamento: ").append(this.depto).append("\n");
        sb.append("Código: ").append(this.codigo).append("\n");
        sb.append("Cargo: ").append(this.cargo).append("\n");
        sb.append("Grupo Escala: ").append(this.grupoEscala.toUpperCase()).append("\n");
        sb.append("Tarifa: ").append(this.tarifa).append("\n");
        sb.append("Horas trabajadas: ").append(hallarCantTotalHTrab()).append("\n");
        sb.append("Pago horas trabajadas: ").append(calcularPagoTotalHTrab()).append(" CUP\n");
        sb.append("Horas extras: ").append(hallarCantTotalHExtra()).append("\n");
        sb.append("Pago horas extras: ").append(calcularPagoTotalHExtra()).append(" CUP\n");
        sb.append("Ocupación Barco: ").append(this.ocupBarco.toUpperCase()).append("\n");
        sb.append("Baja: ").append(isEsBaja() ? "Sí" : "No").append("\n");

        this.Claves(sb);

        sb.append("Pago total: ").append(this.pagoTotal).append(" CUP\n");
        sb.append("Última modificación: ").append(this.fechaUltimaModificacion).append("\n\n");

        this.mostrarProyectos(sb);

        sb.append("Pago total por horas trabajadas y horas extras: ").append(this.calcularPagoTotal()).append(" CUP\n");
    }

    void Claves(StringBuilder sb) {
        sb.append(tieneClave269 ? "Tiene clave 269 " + "(" + getCantidadClaves269() + ")\n" : "No tiene clave 269\n");
        sb.append(tieneClave271 ? "Tiene clave 271\n" : "No tiene clave 271\n");
        sb.append(tieneClave278 ? "Tiene clave 278\n" : "No tiene clave 278\n");
    }

    // Getters y Setters
    public String getGrupoEscala() {
        return grupoEscala;
    }

    public void setGrupoEscala(String grupoEscala) {
        this.grupoEscala = grupoEscala;
    }

    public void setTarifa(int tarifa) {
        this.tarifa = tarifa;
    }

    public double getPagoTotal() {
        return pagoTotal;
    }

    public void setPagoTotal(double pagoTotal) {
        this.pagoTotal = pagoTotal;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDepto() {
        return depto;
    }

    public void setDepto(String depto) {
        this.depto = depto;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getOcupBarco() {
        return ocupBarco;
    }

    public void setOcupBarco(String ocupBarco) {
        this.ocupBarco = ocupBarco;
    }

    public ArrayList<Proyecto> getProyectos() {
        return proyectos;
    }

    public void setProyectos(ArrayList<Proyecto> proyectos) {
        this.proyectos = proyectos;
    }

    public boolean isEsBaja() {
        return esBaja;
    }

    public void setEsBaja(boolean esBaja) {
        this.esBaja = esBaja;
    }

    public boolean isTieneClave271() {
        return tieneClave271;
    }

    public boolean isNadaCertificado() {
        nadaCertificado = proyectos.stream().noneMatch(Proyecto::isEsCertificado);
        return nadaCertificado;
    }
    public void setTieneClave271(boolean tieneClave271) {
        this.tieneClave271 = tieneClave271;
    }

    public boolean isTieneClave269() {
        return tieneClave269;
    }

    public void setTieneClave269(boolean tieneClave269) {
        this.tieneClave269 = tieneClave269;
    }

    public boolean isTieneClave278() {
        return tieneClave278;
    }

    public void setTieneClave278(boolean tieneClave278) {
        this.tieneClave278 = tieneClave278;
    }
    
    public String getFechaUltimaModificacion() {
        return fechaUltimaModificacion;
    }

    public void setFechaUltimaModificacion(String fecha) {
        this.fechaUltimaModificacion = fecha;
    }
    public void setFechaUltimaModificacion() {
        this.fechaUltimaModificacion = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public int getCantidadClaves269() {
        return cantidadClaves269;
    }

    // Método para agregar claves 269
    public void setCantidadClaves269(int cantidad) {
        this.cantidadClaves269 = cantidad;
        this.tieneClave269 = cantidad > 0;
    }

    //Sobreescritura del método toString para mostrarlo en la GUI
    @Override
    public String toString() {
        return nombre + " (Código: " + codigo + ")";
    }
}