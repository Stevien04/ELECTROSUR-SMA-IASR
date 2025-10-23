-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         10.4.32-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.11.0.7065
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para db_permisos_electrosur
CREATE DATABASE IF NOT EXISTS `db_permisos_electrosur` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `db_permisos_electrosur`;

-- Volcando estructura para tabla db_permisos_electrosur.aprobaciones
CREATE TABLE IF NOT EXISTS `aprobaciones` (
  `id_aprobacion` int(11) NOT NULL AUTO_INCREMENT,
  `id_boleta` int(11) NOT NULL,
  `id_jefe` int(11) NOT NULL,
  `tipo_aprobador` enum('JEFE_AREA','JEFE_RRHH') DEFAULT NULL,
  `estado` enum('APROBADO','DENEGADO') NOT NULL,
  `observaciones` text DEFAULT NULL,
  `fecha_respuesta` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id_aprobacion`),
  KEY `id_boleta` (`id_boleta`),
  KEY `id_jefe` (`id_jefe`),
  CONSTRAINT `aprobaciones_ibfk_1` FOREIGN KEY (`id_boleta`) REFERENCES `boletas_permiso` (`id_boleta`),
  CONSTRAINT `aprobaciones_ibfk_2` FOREIGN KEY (`id_jefe`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla db_permisos_electrosur.aprobaciones: ~0 rows (aproximadamente)

-- Volcando estructura para tabla db_permisos_electrosur.boletas_permiso
CREATE TABLE IF NOT EXISTS `boletas_permiso` (
  `id_boleta` int(11) NOT NULL AUTO_INCREMENT,
  `id_empleado` int(11) NOT NULL,
  `fecha_salida` date NOT NULL,
  `hora_salida` time NOT NULL,
  `fecha_retorno` date NOT NULL,
  `hora_retorno` time NOT NULL,
  `motivo` text NOT NULL,
  `estado` varchar(20) DEFAULT 'ENVIADO',
  PRIMARY KEY (`id_boleta`),
  KEY `id_empleado` (`id_empleado`),
  CONSTRAINT `boletas_permiso_ibfk_1` FOREIGN KEY (`id_empleado`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla db_permisos_electrosur.boletas_permiso: ~0 rows (aproximadamente)

-- Volcando estructura para tabla db_permisos_electrosur.horas_acumuladas
CREATE TABLE IF NOT EXISTS `horas_acumuladas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_empleado` int(11) NOT NULL,
  `horas` int(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `id_empleado` (`id_empleado`),
  CONSTRAINT `horas_acumuladas_ibfk_1` FOREIGN KEY (`id_empleado`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla db_permisos_electrosur.horas_acumuladas: ~0 rows (aproximadamente)

-- Volcando estructura para tabla db_permisos_electrosur.reincidencias
CREATE TABLE IF NOT EXISTS `reincidencias` (
  `id_reincidencia` int(11) NOT NULL AUTO_INCREMENT,
  `id_empleado` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `motivo` varchar(255) DEFAULT 'INCUMPLIMIENTO DE RETORNO',
  PRIMARY KEY (`id_reincidencia`),
  KEY `id_empleado` (`id_empleado`),
  CONSTRAINT `reincidencias_ibfk_1` FOREIGN KEY (`id_empleado`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla db_permisos_electrosur.reincidencias: ~0 rows (aproximadamente)

-- Volcando estructura para tabla db_permisos_electrosur.roles
CREATE TABLE IF NOT EXISTS `roles` (
  `id_rol` int(11) NOT NULL AUTO_INCREMENT,
  `rol_nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla db_permisos_electrosur.roles: ~3 rows (aproximadamente)
INSERT INTO `roles` (`id_rol`, `rol_nombre`) VALUES
	(1, 'EMPLEADO'),
	(2, 'JEFE_AREA'),
	(3, 'JEFE_RRHH');

-- Volcando estructura para tabla db_permisos_electrosur.usuarios
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `dni` varchar(20) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `id_rol` int(11) DEFAULT NULL,
  `id_jefe` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `dni` (`dni`),
  UNIQUE KEY `username` (`username`),
  KEY `id_rol` (`id_rol`),
  KEY `id_jefe` (`id_jefe`),
  CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`),
  CONSTRAINT `usuarios_ibfk_2` FOREIGN KEY (`id_jefe`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Volcando datos para la tabla db_permisos_electrosur.usuarios: ~0 rows (aproximadamente)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
