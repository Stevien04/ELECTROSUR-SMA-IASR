package Modelo;

public class clsUsuario {
    private int id_usuario;
    private String nombre;
    private String dni;
    private String username;
    private String password;
    private int id_rol;
    private int id_jefe;

    public clsUsuario() {}

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getId_rol() { return id_rol; }
    public void setId_rol(int id_rol) { this.id_rol = id_rol; }

    public int getId_jefe() { return id_jefe; }
    public void setId_jefe(int id_jefe) { this.id_jefe = id_jefe; }
}
