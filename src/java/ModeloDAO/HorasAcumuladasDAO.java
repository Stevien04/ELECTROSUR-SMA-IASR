package ModeloDAO;

import Conexion.conector;
import Modelo.clsHorasAcumuladas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HorasAcumuladasDAO {

    public int obtenerHorasPorEmpleado(int idEmpleado) {
        String sql = "SELECT horas FROM horas_acumuladas WHERE id_empleado = ?";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("horas");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener horas acumuladas: " + e.getMessage());
        }
        return 0;
    }

    public boolean registrarOActualizar(clsHorasAcumuladas horas) {
        String sql = "INSERT INTO horas_acumuladas (id_empleado, horas) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE horas = VALUES(horas)";
        try (Connection con = conector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, horas.getIdEmpleado());
            ps.setInt(2, horas.getHoras());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar horas acumuladas: " + e.getMessage());
        }
        return false;
    }
}