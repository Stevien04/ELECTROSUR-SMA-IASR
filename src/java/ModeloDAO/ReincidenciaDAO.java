package ModeloDAO;

import Conexion.conector;
import Modelo.clsReincidencia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReincidenciaDAO {

    public boolean registrar(int idEmpleado, String motivo) {
        String sql = "INSERT INTO reincidencias (id_empleado, fecha, motivo) VALUES (?, ?, ?)";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ps.setString(3, motivo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar reincidencia: " + e.getMessage());
        }
        return false;
    }

    public boolean tieneReincidencias(int idEmpleado) {
        String sql = "SELECT COUNT(*) FROM reincidencias WHERE id_empleado = ?";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar reincidencias: " + e.getMessage());
        }
        return false;
    }
}