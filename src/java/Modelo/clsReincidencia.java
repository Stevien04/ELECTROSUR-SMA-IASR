package Modelo;

import java.time.LocalDate;

public class clsReincidencia {
    private int idReincidencia;
    private int idEmpleado;
    private LocalDate fecha;
    private String motivo;

    public int getIdReincidencia() {
        return idReincidencia;
    }

    public void setIdReincidencia(int idReincidencia) {
        this.idReincidencia = idReincidencia;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}