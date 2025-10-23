package ModeloDAO;

import Conexion.conector;
import Modelo.clsBoletaPermiso;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BoletaPermisoDAO {

    public boolean registrar(clsBoletaPermiso boleta) {
        String sql = "INSERT INTO boletas_permiso (id_empleado, fecha_salida, hora_salida, fecha_retorno, hora_retorno, motivo, estado) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, boleta.getIdEmpleado());
            ps.setDate(2, java.sql.Date.valueOf(boleta.getFechaSalida()));
            ps.setTime(3, java.sql.Time.valueOf(boleta.getHoraSalida()));
            ps.setDate(4, java.sql.Date.valueOf(boleta.getFechaRetorno()));
            ps.setTime(5, java.sql.Time.valueOf(boleta.getHoraRetorno()));
            ps.setString(6, boleta.getMotivo());
            ps.setString(7, boleta.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar boleta: " + e.getMessage());
        }
        return false;
    }

    public List<clsBoletaPermiso> listarPorEmpleado(int idEmpleado) {
        List<clsBoletaPermiso> lista = new ArrayList<>();
        String sql = "SELECT bp.*, u.nombre AS empleado_nombre, u.dni AS empleado_dni "
                + "FROM boletas_permiso bp INNER JOIN usuarios u ON bp.id_empleado = u.id_usuario "
                + "WHERE bp.id_empleado = ? ORDER BY bp.fecha_salida DESC, bp.hora_salida DESC";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearBoleta(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar boletas por empleado: " + e.getMessage());
        }
        return lista;
    }

    public List<clsBoletaPermiso> listarPendientesPorJefe(int idJefe) {
        List<clsBoletaPermiso> lista = new ArrayList<>();
        String sql = "SELECT bp.*, u.nombre AS empleado_nombre, u.dni AS empleado_dni "
                + "FROM boletas_permiso bp INNER JOIN usuarios u ON bp.id_empleado = u.id_usuario "
                + "WHERE u.id_jefe = ? AND bp.estado = 'PENDIENTE_JEFE' ORDER BY bp.fecha_salida ASC";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idJefe);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearBoleta(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pendientes por jefe: " + e.getMessage());
        }
        return lista;
    }

    public List<clsBoletaPermiso> listarHistorialPorJefe(int idJefe) {
        List<clsBoletaPermiso> lista = new ArrayList<>();
        String sql = "SELECT bp.*, u.nombre AS empleado_nombre, u.dni AS empleado_dni "
                + "FROM boletas_permiso bp INNER JOIN usuarios u ON bp.id_empleado = u.id_usuario "
                + "WHERE u.id_jefe = ? AND bp.estado <> 'PENDIENTE_JEFE' ORDER BY bp.fecha_salida DESC";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idJefe);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearBoleta(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar historial por jefe: " + e.getMessage());
        }
        return lista;
    }

    public List<clsBoletaPermiso> listarPendientesRRHH() {
        List<clsBoletaPermiso> lista = new ArrayList<>();
        String sql = "SELECT bp.*, u.nombre AS empleado_nombre, u.dni AS empleado_dni "
                + "FROM boletas_permiso bp INNER JOIN usuarios u ON bp.id_empleado = u.id_usuario "
                + "WHERE bp.estado = 'PENDIENTE_RRHH' ORDER BY bp.fecha_salida ASC";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearBoleta(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pendientes RRHH: " + e.getMessage());
        }
        return lista;
    }

    public List<clsBoletaPermiso> listarHistorialRRHH() {
        List<clsBoletaPermiso> lista = new ArrayList<>();
        String sql = "SELECT bp.*, u.nombre AS empleado_nombre, u.dni AS empleado_dni "
                + "FROM boletas_permiso bp INNER JOIN usuarios u ON bp.id_empleado = u.id_usuario "
                + "WHERE bp.estado IN ('APROBADO_RRHH','DENEGADO_RRHH') ORDER BY bp.fecha_salida DESC";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearBoleta(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar historial RRHH: " + e.getMessage());
        }
        return lista;
    }

    public clsBoletaPermiso obtenerPorId(int idBoleta) {
        String sql = "SELECT bp.*, u.nombre AS empleado_nombre, u.dni AS empleado_dni "
                + "FROM boletas_permiso bp INNER JOIN usuarios u ON bp.id_empleado = u.id_usuario "
                + "WHERE bp.id_boleta = ?";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idBoleta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearBoleta(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener boleta por id: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarEstado(int idBoleta, String estado) {
        String sql = "UPDATE boletas_permiso SET estado = ?, fecha_retorno = fecha_retorno WHERE id_boleta = ?";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idBoleta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de boleta: " + e.getMessage());
        }
        return false;
    }

    private clsBoletaPermiso mapearBoleta(ResultSet rs) throws SQLException {
        clsBoletaPermiso boleta = new clsBoletaPermiso();
        boleta.setIdBoleta(rs.getInt("id_boleta"));
        boleta.setIdEmpleado(rs.getInt("id_empleado"));
        boleta.setFechaSalida(toLocalDate(rs.getDate("fecha_salida")));
        boleta.setHoraSalida(toLocalTime(rs.getTime("hora_salida")));
        boleta.setFechaRetorno(toLocalDate(rs.getDate("fecha_retorno")));
        boleta.setHoraRetorno(toLocalTime(rs.getTime("hora_retorno")));
        boleta.setMotivo(rs.getString("motivo"));
        boleta.setEstado(rs.getString("estado"));
        boleta.setEmpleadoNombre(rs.getString("empleado_nombre"));
        boleta.setEmpleadoDni(rs.getString("empleado_dni"));
        return boleta;
    }

    private LocalDate toLocalDate(java.sql.Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    private LocalTime toLocalTime(java.sql.Time time) {
        return time != null ? time.toLocalTime() : null;
    }
}