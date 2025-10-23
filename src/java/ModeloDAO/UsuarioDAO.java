package ModeloDAO;

import Conexion.conector;
import Interfaces.CRUDUsuario;
import Modelo.clsUsuario;
import java.sql.*;

public class UsuarioDAO implements CRUDUsuario {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    @Override
    public clsUsuario login(String username, String password) {
        clsUsuario usr = null;
        String sql = "SELECT * FROM usuarios WHERE username=? AND password=?";
        try {
            con = conector.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                usr = new clsUsuario();
                usr.setId_usuario(rs.getInt("id_usuario"));
                usr.setNombre(rs.getString("nombre"));
                usr.setDni(rs.getString("dni"));
                usr.setUsername(rs.getString("username"));
                usr.setId_rol(rs.getInt("id_rol"));
                usr.setId_jefe(rs.getInt("id_jefe"));
            }
        } catch (Exception e) {
            System.out.println("Error en login: " + e.getMessage());
        }
        return usr;
    }
}
