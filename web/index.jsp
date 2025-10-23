<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Electrosur</title>
</head>
<body>
    <h2>LOGIN</h2>
    <form action="UsuarioControlador" method="POST">
        <input type="text" name="txtuser" placeholder="Usuario" required><br><br>
        <input type="password" name="txtpass" placeholder="Contraseña" required><br><br>
        <button type="submit">Ingresar</button>
    </form>

    <p style="color:red;">
        <%= request.getAttribute("mensaje") != null ? request.getAttribute("mensaje") : "" %>
    </p>
</body>
</html>
