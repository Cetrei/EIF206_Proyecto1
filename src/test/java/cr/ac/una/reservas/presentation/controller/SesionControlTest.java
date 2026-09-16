package cr.ac.una.reservas.presentation.controller;

import cr.ac.una.reservas.model.Administrador;
import cr.ac.una.reservas.model.Funcionario;
import cr.ac.una.reservas.model.RolUsuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SesionControlTest {

    @AfterEach
    void cerrarSesion() {
        SesionControl.obtenerInstancia().cerrarSesion();
    }

    @Test
    void sinSesionNoHayUsuarioNiRol() {
        assertFalse(SesionControl.obtenerInstancia().haySesionActiva());
        assertNull(SesionControl.obtenerInstancia().usuarioActual());
        assertNull(SesionControl.obtenerInstancia().rolActual());
        assertFalse(SesionControl.obtenerInstancia().esAdministrador());
    }

    @Test
    void reconoceAlAdministradorQueIniciaSesion() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "123"));

        assertTrue(SesionControl.obtenerInstancia().haySesionActiva());
        assertTrue(SesionControl.obtenerInstancia().esAdministrador());
        assertEquals(RolUsuario.ADMINISTRADOR, SesionControl.obtenerInstancia().rolActual());
    }

    @Test
    void noTomaAlFuncionarioComoAdministrador() {
        SesionControl.obtenerInstancia().iniciarSesion(new Funcionario("111", "111", "Juan Perez", "3323"));

        assertTrue(SesionControl.obtenerInstancia().haySesionActiva());
        assertFalse(SesionControl.obtenerInstancia().esAdministrador());
        assertEquals(RolUsuario.FUNCIONARIO, SesionControl.obtenerInstancia().rolActual());
        assertEquals("111", SesionControl.obtenerInstancia().usuarioActual().getId());
    }

    @Test
    void cerrarSesionLimpiaElUsuarioActual() {
        SesionControl.obtenerInstancia().iniciarSesion(new Administrador("admin", "123"));

        SesionControl.obtenerInstancia().cerrarSesion();

        assertFalse(SesionControl.obtenerInstancia().haySesionActiva());
        assertNull(SesionControl.obtenerInstancia().usuarioActual());
    }

    @Test
    void siempreDevuelveLaMismaInstanciaDeSesion() {
        assertSame(SesionControl.obtenerInstancia(), SesionControl.obtenerInstancia());
    }
}
