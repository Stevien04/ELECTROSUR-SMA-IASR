package ModeloDAO;

import Conexion.conector;
import Modelo.clsAprobacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AprobacionDAO {

    public boolean registrarOActualizar(clsAprobacion aprobacion) {
        clsAprobacion existente = obtenerPorBoletaYTipo(aprobacion.getIdBoleta(), aprobacion.getTipoAprobador());
        if (existente != null) {
            String sql = "UPDATE aprobaciones SET estado = ?, observaciones = ?, fecha_respuesta = NOW(), id_jefe = ? "
                    + "WHERE id_aprobacion = ?";
            try (Connection con = conector.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, aprobacion.getEstado());
                ps.setString(2, aprobacion.getObservaciones());
                ps.setInt(3, aprobacion.getIdJefe());
                ps.setInt(4, existente.getIdAprobacion());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("Error al actualizar aprobación: " + e.getMessage());
            }
        } else {
            String sql = "INSERT INTO aprobaciones (id_boleta, id_jefe, tipo_aprobador, estado, observaciones) "
                    + "VALUES (?,?,?,?,?)";
            try (Connection con = conector.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, aprobacion.getIdBoleta());
                ps.setInt(2, aprobacion.getIdJefe());
                ps.setString(3, aprobacion.getTipoAprobador());
                ps.setString(4, aprobacion.getEstado());
                ps.setString(5, aprobacion.getObservaciones());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("Error al registrar aprobación: " + e.getMessage());
            }
        }
        return false;
    }

    public clsAprobacion obtenerPorBoletaYTipo(int idBoleta, String tipo) {
        String sql = "SELECT a.*, u.nombre AS jefe_nombre FROM aprobaciones a "
                + "LEFT JOIN usuarios u ON a.id_jefe = u.id_usuario WHERE a.id_boleta = ? AND a.tipo_aprobador = ?";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBoleta);
            ps.setString(2, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener aprobación: " + e.getMessage());
        }
        return null;
    }

    public List<clsAprobacion> listarPorBoleta(int idBoleta) {
        List<clsAprobacion> lista = new ArrayList<>();
        String sql = "SELECT a.*, u.nombre AS jefe_nombre FROM aprobaciones a "
                + "LEFT JOIN usuarios u ON a.id_jefe = u.id_usuario WHERE a.id_boleta = ?";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBoleta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar aprobaciones: " + e.getMessage());
        }
        return lista;
    }

    private clsAprobacion mapear(ResultSet rs) throws SQLException {
        clsAprobacion aprobacion = new clsAprobacion();
        aprobacion.setIdAprobacion(rs.getInt("id_aprobacion"));
        aprobacion.setIdBoleta(rs.getInt("id_boleta"));
        aprobacion.setIdJefe(rs.getInt("id_jefe"));
        aprobacion.setTipoAprobador(rs.getString("tipo_aprobador"));
        aprobacion.setEstado(rs.getString("estado"));
        aprobacion.setObservaciones(rs.getString("observaciones"));
        if (rs.getTimestamp("fecha_respuesta") != null) {
            aprobacion.setFechaRespuesta(rs.getTimestamp("fecha_respuesta").toLocalDateTime());
        }
        aprobacion.setJefeNombre(rs.getString("jefe_nombre"));
        return aprobacion;
    }
}