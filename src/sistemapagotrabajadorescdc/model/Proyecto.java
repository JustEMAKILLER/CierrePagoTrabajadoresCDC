package sistemapagotrabajadorescdc.model;

public class Proyecto {
    private int numeroProyecto;
    private boolean esCertificado;
    private double horasTrab;
    private double horasExtra;

    public Proyecto(int numeroProyecto, boolean esCertificado, double horasTrab, double horasExtra) {
        this.numeroProyecto = numeroProyecto;
        this.esCertificado = esCertificado;
        this.horasTrab = horasTrab;
        this.horasExtra = horasExtra;
    }

    // Getters y Setters
    public double getHorasTrab() {
        return horasTrab;
    }

    public void setHorasTrab(double horasTrab) {
        this.horasTrab = horasTrab;
    }

    public double getHorasExtra() {
        return horasExtra;
    }

    public void setHorasExtra(double horasExtra) {
        this.horasExtra = horasExtra;
    }

    public int getNumeroProyecto() {
        return numeroProyecto;
    }

    public void setNumeroProyecto(int numeroProyecto) {
        this.numeroProyecto = numeroProyecto;
    }

    public boolean isEsCertificado() {
        return esCertificado;
    }

    public void setEsCertificado(boolean esCertificado) {
        this.esCertificado = esCertificado;
    }

    //Sobreescritura del método toString para mostrarlo en la GUI
    @Override
    public String toString(){
        return this.getNumeroProyecto() + " - " + (this.isEsCertificado() ? "Certificado" : "No certificado");
    }
}