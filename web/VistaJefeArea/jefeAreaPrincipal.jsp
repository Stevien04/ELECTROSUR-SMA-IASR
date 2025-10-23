<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List" %>
<%@page import="javax.servlet.http.HttpSession" %>
<%@page import="Modelo.clsUsuario" %>
<%@page import="Modelo.clsBoletaPermiso" %>
<%@page import="Modelo.clsAprobacion" %>
<%@page import="ModeloDAO.BoletaPermisoDAO" %>
<%@page import="ModeloDAO.AprobacionDAO" %>
<%
    HttpSession sesion = request.getSession(false);
    clsUsuario usuario = sesion != null ? (clsUsuario) sesion.getAttribute("usuarioLogeado") : null;
    if (usuario == null || usuario.getId_rol() != 2) {
        response.sendRedirect("../index.jsp");
        return;
    }
    BoletaPermisoDAO boletaDAO = new BoletaPermisoDAO();
    AprobacionDAO aprobacionDAO = new AprobacionDAO();
    List<clsBoletaPermiso> pendientes = boletaDAO.listarPendientesPorJefe(usuario.getId_usuario());
    List<clsBoletaPermiso> historial = boletaDAO.listarHistorialPorJefe(usuario.getId_usuario());
    String faltaObs = request.getParameter("faltaObs");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Portal Jefe de Área</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 20px; }
            header { display: flex; justify-content: space-between; align-items: center; }
            section { margin-top: 25px; }
            table { border-collapse: collapse; width: 100%; }
            th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
            th { background: #f2f2f2; }
            .mensaje { padding: 10px; border-radius: 4px; margin-bottom: 15px; }
            .alerta { background: #fffbe6; color: #a87b00; }
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
                <h2>Jefe de Área: <%= usuario.getNombre() %></h2>
            </div>
            <div>
                <a href="../UsuarioControlador?action=logout">Cerrar sesión</a>
            </div>
        </header>

        <% if ("1".equals(faltaObs)) { %>
            <div class="mensaje alerta">Para denegar un permiso debes completar las observaciones.</div>
        <% } %>

        <section>
            <h3>Permisos pendientes de tu área</h3>
            <% if (pendientes.isEmpty()) { %>
                <p>No existen boletas pendientes por revisar.</p>
            <% } else { %>
                <% for (clsBoletaPermiso boleta : pendientes) { %>
                    <article style="border:1px solid #ccc; padding:15px; margin-bottom:15px;">
                        <strong>Empleado:</strong> <%= boleta.getEmpleadoNombre() %> (DNI: <%= boleta.getEmpleadoDni() %>)<br>
                        <strong>Salida:</strong> <%= boleta.getFechaSalida() %> a las <%= boleta.getHoraSalida() %><br>
                        <strong>Retorno:</strong> <%= boleta.getFechaRetorno() %> a las <%= boleta.getHoraRetorno() %><br>
                        <strong>Motivo:</strong> <%= boleta.getMotivo() %>
                        <form action="../PermisoControlador" method="post">
                            <input type="hidden" name="action" value="resolverJefe" />
                            <input type="hidden" name="id_boleta" value="<%= boleta.getIdBoleta() %>" />
                            <label>Observaciones (obligatorias si deniegas)</label>
                            <textarea name="observaciones" rows="2"></textarea>
                            <div class="acciones">
                                <button type="submit" name="decision" value="APROBAR">Aprobar y enviar a RRHH</button>
                                <button type="submit" name="decision" value="DENEGAR">Denegar permiso</button>
                            </div>
                        </form>
                    </article>
                <% } %>
            <% } %>
        </section>

        <section>
            <h3>Historial de boletas revisadas</h3>
            <table>
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Empleado</th>
                        <th>Salida</th>
                        <th>Retorno</th>
                        <th>Estado</th>
                        <th>Detalle RRHH</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (historial.isEmpty()) { %>
                        <tr><td colspan="6">Aún no has revisado boletas.</td></tr>
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
                        </tr>
                    <% }
                    } %>
                </tbody>
            </table>
        </section>
    </body>
</html>