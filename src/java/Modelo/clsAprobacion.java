package Modelo;

import java.time.LocalDateTime;

public class clsAprobacion {
    private int idAprobacion;
    private int idBoleta;
    private int idJefe;
    private String tipoAprobador;
    private String estado;
    private String observaciones;
    private LocalDateTime fechaRespuesta;
    private String jefeNombre;

    public int getIdAprobacion() {
        return idAprobacion;
    }

    public void setIdAprobacion(int idAprobacion) {
        this.idAprobacion = idAprobacion;
    }

    public int getIdBoleta() {
        return idBoleta;
    }

    public void setIdBoleta(int idBoleta) {
        this.idBoleta = idBoleta;
    }

    public int getIdJefe() {
        return idJefe;
    }

    public void setIdJefe(int idJefe) {
        this.idJefe = idJefe;
    }

    public String getTipoAprobador() {
        return tipoAprobador;
    }

    public void setTipoAprobador(String tipoAprobador) {
        this.tipoAprobador = tipoAprobador;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public String getJefeNombre() {
        return jefeNombre;
    }

    public void setJefeNombre(String jefeNombre) {
        this.jefeNombre = jefeNombre;
    }
}