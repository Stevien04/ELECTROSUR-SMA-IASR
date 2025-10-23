<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List" %>
<%@page import="javax.servlet.http.HttpSession" %>
<%@page import="Modelo.clsUsuario" %>
<%@page import="Modelo.clsBoletaPermiso" %>
<%@page import="Modelo.clsAprobacion" %>
<%@page import="ModeloDAO.BoletaPermisoDAO" %>
<%@page import="ModeloDAO.AprobacionDAO" %>
<%@page import="ModeloDAO.HorasAcumuladasDAO" %>
<%
    HttpSession sesion = request.getSession(false);
    clsUsuario usuario = sesion != null ? (clsUsuario) sesion.getAttribute("usuarioLogeado") : null;
    if (usuario == null || usuario.getId_rol() != 3) {
        response.sendRedirect("../index.jsp");
        return;
    }
    BoletaPermisoDAO boletaDAO = new BoletaPermisoDAO();
    AprobacionDAO aprobacionDAO = new AprobacionDAO();
    HorasAcumuladasDAO horasDAO = new HorasAcumuladasDAO();
    List<clsBoletaPermiso> pendientes = boletaDAO.listarPendientesRRHH();
    List<clsBoletaPermiso> historial = boletaDAO.listarHistorialRRHH();
    String superaHoras = request.getParameter("superaHoras");
    String faltaObs = request.getParameter("faltaObs");
    String reincidencia = request.getParameter("reincidencia");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Portal Recursos Humanos</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 20px; }
            header { display: flex; justify-content: space-between; align-items: center; }
            section { margin-top: 25px; }
            table { border-collapse: collapse; width: 100%; }
            th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
            th { background: #f2f2f2; }
            .mensaje { padding: 10px; border-radius: 4px; margin-bottom: 15px; }
            .alerta { background: #fffbe6; color: #a87b00; }
            .error { background: #ffe6e6; color: #c70000; }
            .exito { background: #e6ffed; color: #027a1c; }
            textarea { width: 100%; box-sizing: border-box; }
            form { margin-top: 10px; }
            .acciones { display: flex; gap: 10px; margin-top: 10px; }
            button { padding: 8px 12px; }
            ul { margin: 0; padding-left: 18px; }
        </style>
    </head>
    <body>
        <header>
            <div>
                <h2>Jefe de RRHH: <%= usuario.getNombre() %></h2>
            </div>
            <div>
                <a href="../UsuarioControlador?action=logout">Cerrar sesión</a>
            </div>
        </header>

        <% if ("1".equals(superaHoras)) { %>
            <div class="mensaje alerta">El empleado supera las 50 horas acumuladas. No es posible aprobar el permiso.</div>
        <% } %>
        <% if ("1".equals(faltaObs)) { %>
            <div class="mensaje alerta">Para denegar un permiso debes registrar observaciones.</div>
        <% } %>
        <% if ("1".equals(reincidencia)) { %>
            <div class="mensaje exito">Se registró la reincidencia del empleado.</div>
        <% } %>

        <section>
            <h3>Permisos pendientes por Recursos Humanos</h3>
            <% if (pendientes.isEmpty()) { %>
                <p>No hay boletas pendientes.</p>
            <% } else { %>
                <% for (clsBoletaPermiso boleta : pendientes) {
                    int horas = horasDAO.obtenerHorasPorEmpleado(boleta.getIdEmpleado());
                %>
                    <article style="border:1px solid #ccc; padding:15px; margin-bottom:15px;">
                        <strong>Empleado:</strong> <%= boleta.getEmpleadoNombre() %> (DNI: <%= boleta.getEmpleadoDni() %>)<br>
                        <strong>Horas acumuladas:</strong> <%= horas %> horas<br>
                        <strong>Salida:</strong> <%= boleta.getFechaSalida() %> <%= boleta.getHoraSalida() %><br>
                        <strong>Retorno:</strong> <%= boleta.getFechaRetorno() %> <%= boleta.getHoraRetorno() %><br>
                        <strong>Motivo:</strong> <%= boleta.getMotivo() %>
                        <form action="../PermisoControlador" method="post">
                            <input type="hidden" name="action" value="resolverRRHH" />
                            <input type="hidden" name="id_boleta" value="<%= boleta.getIdBoleta() %>" />
                            <label>Observaciones (obligatorias si deniegas)</label>
                            <textarea name="observaciones" rows="2"></textarea>
                            <div class="acciones">
                                <button type="submit" name="decision" value="APROBAR" <%= horas > 50 ? "disabled" : "" %>>Aprobar permiso</button>
                                <button type="submit" name="decision" value="DENEGAR">Denegar permiso</button>
                            </div>
                        </form>
                        <% if (horas > 50) { %>
                            <div class="mensaje error">El permiso debe ser denegado por exceso de horas acumuladas.</div>
                        <% } %>
                    </article>
                <% } %>
            <% } %>
        </section>

        <section>
            <h3>Historial de boletas atendidas</h3>
            <table>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Empleado</th>
                        <th>Salida</th>
                        <th>Retorno</th>
                        <th>Estado</th>
                        <th>Respuestas</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (historial.isEmpty()) { %>
                        <tr><td colspan="7">Aún no se registran decisiones.</td></tr>
                    <% } else {
                        for (clsBoletaPermiso boleta : historial) {
                            List<clsAprobacion> aprobaciones = aprobacionDAO.listarPorBoleta(boleta.getIdBoleta());
                    %>
                        <tr>
                            <td><%= boleta.getIdBoleta() %></td>
                            <td><%= boleta.getEmpleadoNombre() %></td>
                            <td><%= boleta.getFechaSalida() %> <%= boleta.getHoraSalida() %></td>
                            <td><%= boleta.getFechaRetorno() %> <%= boleta.getHoraRetorno() %></td>
                            <td><%= boleta.getEstado() %></td>
                            <td>
                                <% if (aprobaciones.isEmpty()) { %>
                                    Sin respuestas
                                <% } else { %>
                                    <ul>
                                        <% for (clsAprobacion ap : aprobaciones) { %>
                                            <li><strong><%= ap.getTipoAprobador() %></strong> - <%= ap.getJefeNombre() != null ? ap.getJefeNombre() : "Pendiente" %>: <%= ap.getEstado() %>
                                                <% if (ap.getObservaciones() != null) { %>
                                                    - <em><%= ap.getObservaciones() %></em>
                                                <% } %>
                                            </li>
                                        <% } %>
                                    </ul>
                                <% } %>
                            </td>
                            <td>
                                <form action="../PermisoControlador" method="post">
                                    <input type="hidden" name="action" value="registrarReincidencia" />
                                    <input type="hidden" name="id_empleado" value="<%= boleta.getIdEmpleado() %>" />
                                    <textarea name="observacion_reincidencia" rows="2" placeholder="Detalle incumplimiento"></textarea>
                                    <button type="submit">Registrar reincidencia</button>
                                </form>
                            </td>
                        </tr>
                    <% }
                    } %>
                </tbody>
            </table>
        </section>
    </body>
</html>