package Controlador;

import Modelo.clsUsuario;
import ModeloDAO.UsuarioDAO;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

public class UsuarioControlador extends HttpServlet {

    UsuarioDAO dao = new UsuarioDAO();
    clsUsuario usr = new clsUsuario();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("txtuser");
        String password = request.getParameter("txtpass");

        usr = dao.login(username, password);

        if (usr != null) {
            HttpSession session = request.getSession();
            session.setAttribute("usuarioLogeado", usr);

            // Redirección según el rol del usuario
            switch (usr.getId_rol()) {
                case 1: // EMPLEADO
                    response.sendRedirect("VistaEmpleado/empleadoPrincipal.jsp");
                    break;

                case 2: // JEFE DE AREA
                    response.sendRedirect("VistaJefeArea/jefeAreaPrincipal.jsp");
                    break;

                case 3: // RRHH
                    response.sendRedirect("VistaJefeRRHH/rrhhPrincipal.jsp");
                    break;

                default:
                    response.sendRedirect("index.jsp");
                    break;
            }

        } else {
            request.setAttribute("mensaje", "Usuario o contraseña incorrectos");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}
