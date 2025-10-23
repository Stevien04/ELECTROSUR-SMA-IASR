<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List" %>
<%@page import="javax.servlet.http.HttpSession" %>
<%@page import="Modelo.clsUsuario" %>
<%@page import="Modelo.clsBoletaPermiso" %>
<%@page import="Modelo.clsAprobacion" %>
<%@page import="ModeloDAO.BoletaPermisoDAO" %>
<%@page import="ModeloDAO.AprobacionDAO" %>
<%@page import="ModeloDAO.ReincidenciaDAO" %>
<%
    HttpSession sesion = request.getSession(false);
    clsUsuario usuario = sesion != null ? (clsUsuario) sesion.getAttribute("usuarioLogeado") : null;
    if (usuario == null || usuario.getId_rol() != 1) {
        response.sendRedirect("../index.jsp");
        return;
    }
    BoletaPermisoDAO boletaDAO = new BoletaPermisoDAO();
    AprobacionDAO aprobacionDAO = new AprobacionDAO();
    ReincidenciaDAO reincidenciaDAO = new ReincidenciaDAO();
    List<clsBoletaPermiso> boletas = boletaDAO.listarPorEmpleado(usuario.getId_usuario());
    boolean esReincidente = reincidenciaDAO.tieneReincidencias(usuario.getId_usuario());
    String creado = request.getParameter("creado");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Portal del Empleado</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 20px; }
            header { display: flex; justify-content: space-between; align-items: center; }
            table { border-collapse: collapse; width: 100%; margin-top: 20px; }
            th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
            th { background: #f2f2f2; }
            .mensaje { padding: 10px; margin-top: 10px; border-radius: 4px; }
            .exito { background: #e6ffed; color: #027a1c; }
            .error { background: #ffe6e6; color: #c70000; }
            .alerta { background: #fffbe6; color: #a87b00; }
            form { margin-top: 15px; }
            label { display: block; margin-top: 10px; font-weight: bold; }
            input, textarea, select { width: 100%; padding: 8px; box-sizing: border-box; }
            button { margin-top: 15px; padding: 10px 15px; }
        </style>
    </head>
    <body>
         <header>
            <div>
                <h2>Bienvenido, <%= usuario.getNombre() %></h2>
                <p>DNI: <%= usuario.getDni() %></p>
            </div>
            <div>
                <a href="../UsuarioControlador?action=logout">Cerrar sesión</a>
            </div>
        </header>

        <% if ("1".equals(creado)) { %>
            <div class="mensaje exito">La boleta de permiso fue enviada al jefe inmediato.</div>
        <% } else if ("0".equals(creado)) { %>
            <div class="mensaje error">No se pudo registrar la boleta, revisa la información ingresada.</div>
        <% } %>

        <% if (esReincidente) { %>
            <div class="mensaje alerta">Tienes reincidencias registradas. Los próximos permisos serán evaluados con mayor rigurosidad.</div>
        <% } %>

        <section>
            <h3>Nueva boleta de permiso</h3>
            <form action="../PermisoControlador" method="post">
                <input type="hidden" name="action" value="registrarBoleta" />
                <label>Fecha de salida</label>
                <input type="date" name="fecha_salida" required />

                <label>Hora de salida</label>
                <input type="time" name="hora_salida" required />

                <label>Fecha de retorno</label>
                <input type="date" name="fecha_retorno" required />

                <label>Hora de retorno</label>
                <input type="time" name="hora_retorno" required />

                <label>Motivo del permiso</label>
                <textarea name="motivo" rows="3" required></textarea>

                <button type="submit">Enviar boleta</button>
            </form>
        </section>

        <section>
            <h3>Mis boletas enviadas</h3>
            <table>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Fecha salida</th>
                        <th>Hora salida</th>
                        <th>Fecha retorno</th>
                        <th>Hora retorno</th>
                        <th>Motivo</th>
                        <th>Estado</th>
                        <th>Aprobaciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (boletas.isEmpty()) { %>
                        <tr>
                            <td colspan="8">No tienes boletas registradas.</td>
                        </tr>
                    <% } else {
                        for (clsBoletaPermiso boleta : boletas) {
                            List<clsAprobacion> aprobaciones = aprobacionDAO.listarPorBoleta(boleta.getIdBoleta());
                            boolean aprobadaJefe = false;
                            boolean aprobadaRRHH = false;
                    %>
                        <tr>
                            <td><%= boleta.getIdBoleta() %></td>
                            <td><%= boleta.getFechaSalida() %></td>
                            <td><%= boleta.getHoraSalida() %></td>
                            <td><%= boleta.getFechaRetorno() %></td>
                            <td><%= boleta.getHoraRetorno() %></td>
                            <td><%= boleta.getMotivo() %></td>
                            <td><%= boleta.getEstado() %></td>
                            <td>
                                <% if (aprobaciones.isEmpty()) { %>
                                    En revisión
                                <% } else { %>
                                    <ul>
                                        <% for (clsAprobacion ap : aprobaciones) {
                                            if ("JEFE_AREA".equals(ap.getTipoAprobador()) && "APROBADO".equals(ap.getEstado())) {
                                                aprobadaJefe = true;
                                            }
                                            if ("JEFE_RRHH".equals(ap.getTipoAprobador()) && "APROBADO".equals(ap.getEstado())) {
                                                aprobadaRRHH = true;
                                            }
                                        %>
                                        <li><strong><%= ap.getTipoAprobador() %></strong> - <%= ap.getJefeNombre() != null ? ap.getJefeNombre() : "Pendiente" %>: <%= ap.getEstado() %>
                                            <% if (ap.getObservaciones() != null) { %>
                                                - <em><%= ap.getObservaciones() %></em>
                                            <% } %>
                                        </li>
                                        <% } %>
                                    </ul>
                                <% } %>
                                <% if (aprobadaJefe && aprobadaRRHH) { %>
                                    <div class="mensaje exito">Boleta aprobada por ambas jefaturas. Puedes ejecutar el permiso.</div>
                                <% } %>
                            </td>
                        </tr>
                    <% }
                    } %>
                </tbody>
            </table>
        </section>
    </body>
</html>