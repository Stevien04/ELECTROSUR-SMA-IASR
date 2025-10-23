package Interfaces;

import Modelo.clsUsuario;

public interface CRUDUsuario {
    public clsUsuario login(String username, String password);
}
