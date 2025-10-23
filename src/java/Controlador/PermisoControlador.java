package Controlador;

import Modelo.clsAprobacion;
import Modelo.clsBoletaPermiso;
import Modelo.clsUsuario;
import ModeloDAO.AprobacionDAO;
import ModeloDAO.BoletaPermisoDAO;
import ModeloDAO.HorasAcumuladasDAO;
import ModeloDAO.ReincidenciaDAO;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "PermisoControlador", urlPatterns = {"/PermisoControlador"})
public class PermisoControlador extends HttpServlet {

    private final BoletaPermisoDAO boletaDAO = new BoletaPermisoDAO();
    private final AprobacionDAO aprobacionDAO = new AprobacionDAO();
    private final HorasAcumuladasDAO horasDAO = new HorasAcumuladasDAO();
    private final ReincidenciaDAO reincidenciaDAO = new ReincidenciaDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        clsUsuario usuario = session != null ? (clsUsuario) session.getAttribute("usuarioLogeado") : null;

        if (usuario == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        switch (action == null ? "" : action) {
            case "registrarBoleta":
                registrarBoleta(request, response, usuario);
                break;
            case "resolverJefe":
                resolverJefe(request, response, usuario);
                break;
            case "resolverRRHH":
                resolverRRHH(request, response, usuario);
                break;
            case "registrarReincidencia":
                registrarReincidencia(request, response, usuario);
                break;
            default:
                response.sendRedirect("index.jsp");
        }
    }

    private void registrarBoleta(HttpServletRequest request, HttpServletResponse response, clsUsuario usuario)
            throws IOException {
        try {
            LocalDate fechaSalida = LocalDate.parse(request.getParameter("fecha_salida"));
            LocalTime horaSalida = LocalTime.parse(request.getParameter("hora_salida"));
            LocalDate fechaRetorno = LocalDate.parse(request.getParameter("fecha_retorno"));
            LocalTime horaRetorno = LocalTime.parse(request.getParameter("hora_retorno"));
            String motivo = request.getParameter("motivo");

            clsBoletaPermiso boleta = new clsBoletaPermiso();
            boleta.setIdEmpleado(usuario.getId_usuario());
            boleta.setFechaSalida(fechaSalida);
            boleta.setHoraSalida(horaSalida);
            boleta.setFechaRetorno(fechaRetorno);
            boleta.setHoraRetorno(horaRetorno);
            boleta.setMotivo(motivo);
            boleta.setEstado("PENDIENTE_JEFE");

            boolean ok = boletaDAO.registrar(boleta);
            response.sendRedirect("VistaEmpleado/empleadoPrincipal.jsp?creado=" + (ok ? "1" : "0"));
        } catch (Exception e) {
            response.sendRedirect("VistaEmpleado/empleadoPrincipal.jsp?creado=0");
        }
    }

    private void resolverJefe(HttpServletRequest request, HttpServletResponse response, clsUsuario usuario)
            throws IOException {
        if (usuario.getId_rol() != 2) {
            response.sendRedirect("index.jsp");
            return;
        }
        int idBoleta = Integer.parseInt(request.getParameter("id_boleta"));
        String decision = request.getParameter("decision");
        String observaciones = request.getParameter("observaciones");

        if (!"APROBAR".equals(decision) && (observaciones == null || observaciones.trim().isEmpty())) {
            response.sendRedirect("VistaJefeArea/jefeAreaPrincipal.jsp?faltaObs=1");
            return;
        }

        clsAprobacion aprobacion = new clsAprobacion();
        aprobacion.setIdBoleta(idBoleta);
        aprobacion.setIdJefe(usuario.getId_usuario());
        aprobacion.setTipoAprobador("JEFE_AREA");
        aprobacion.setObservaciones(observaciones);

        if ("APROBAR".equals(decision)) {
            aprobacion.setEstado("APROBADO");
            boletaDAO.actualizarEstado(idBoleta, "PENDIENTE_RRHH");
        } else {
            aprobacion.setEstado("DENEGADO");
            boletaDAO.actualizarEstado(idBoleta, "DENEGADO_JEFE");
        }
        aprobacionDAO.registrarOActualizar(aprobacion);
        response.sendRedirect("VistaJefeArea/jefeAreaPrincipal.jsp");
    }

    private void resolverRRHH(HttpServletRequest request, HttpServletResponse response, clsUsuario usuario)
            throws IOException {
        if (usuario.getId_rol() != 3) {
            response.sendRedirect("index.jsp");
            return;
        }
        int idBoleta = Integer.parseInt(request.getParameter("id_boleta"));
        String decision = request.getParameter("decision");
        String observaciones = request.getParameter("observaciones");

        if (!"APROBAR".equals(decision) && (observaciones == null || observaciones.trim().isEmpty())) {
            response.sendRedirect("VistaJefeRRHH/rrhhPrincipal.jsp?faltaObs=1");
            return;
        }

        clsBoletaPermiso boleta = boletaDAO.obtenerPorId(idBoleta);
        if (boleta == null) {
            response.sendRedirect("VistaJefeRRHH/rrhhPrincipal.jsp");
            return;
        }

        int horas = horasDAO.obtenerHorasPorEmpleado(boleta.getIdEmpleado());
        if (horas > 50 && "APROBAR".equals(decision)) {
            response.sendRedirect("VistaJefeRRHH/rrhhPrincipal.jsp?superaHoras=1");
            return;
        }

        clsAprobacion aprobacion = new clsAprobacion();
        aprobacion.setIdBoleta(idBoleta);
        aprobacion.setIdJefe(usuario.getId_usuario());
        aprobacion.setTipoAprobador("JEFE_RRHH");
        aprobacion.setObservaciones(observaciones);

        if ("APROBAR".equals(decision)) {
            aprobacion.setEstado("APROBADO");
            boletaDAO.actualizarEstado(idBoleta, "APROBADO_RRHH");
        } else {
            aprobacion.setEstado("DENEGADO");
            boletaDAO.actualizarEstado(idBoleta, "DENEGADO_RRHH");
        }
        aprobacionDAO.registrarOActualizar(aprobacion);
        response.sendRedirect("VistaJefeRRHH/rrhhPrincipal.jsp");
    }

    private void registrarReincidencia(HttpServletRequest request, HttpServletResponse response, clsUsuario usuario)
            throws IOException {
        if (usuario.getId_rol() != 3) {
            response.sendRedirect("index.jsp");
            return;
        }
        int idEmpleado = Integer.parseInt(request.getParameter("id_empleado"));
        String observacion = request.getParameter("observacion_reincidencia");
        if (observacion == null || observacion.trim().isEmpty()) {
            observacion = "INCUMPLIMIENTO DE RETORNO";
        }
        reincidenciaDAO.registrar(idEmpleado, observacion);
        response.sendRedirect("VistaJefeRRHH/rrhhPrincipal.jsp?reincidencia=1");
    }
}