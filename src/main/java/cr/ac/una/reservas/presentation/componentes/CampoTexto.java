package cr.ac.una.reservas.presentation.componentes;

import cr.ac.una.reservas.presentation.iconos.Icono;
import cr.ac.una.reservas.presentation.iconos.IconoAplicador;
import cr.ac.una.reservas.presentation.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.tema.GestorTema;
import cr.ac.una.reservas.presentation.tema.Tema;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;

public class CampoTexto implements CambioTemaListener, RespondeAEnter {
    private JPanel CampoTexto;
    private JPanel pnlCaja;
    private JTextField txtCampo;
    private JLabel lblCampo;
    private JLabel lblIcon;

    private final boolean esContrasena;
    private boolean soloLectura;

    private String placeholder = "";
    private boolean mostrandoPlaceholder;

    public CampoTexto() {
        this(false);
    }

    public CampoTexto(boolean esContrasena) {
        this.esContrasena = esContrasena;
        CampoTexto.setOpaque(false);
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        txtCampo = esContrasena ? new JPasswordField() : new JTextField();
        txtCampo.setBorder(null);
        txtCampo.setOpaque(false);
        // JTextField no tiene placeholder nativo: se simula con texto gris que se borra al enfocar.
        txtCampo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evento) {
                if (mostrandoPlaceholder) {
                    mostrandoPlaceholder = false;
                    txtCampo.setText("");
                    txtCampo.setForeground(GestorTema.obtenerInstancia().temaActivo().colorTexto());
                }
                pnlCaja.setBorder(bordeCaja(GestorTema.obtenerInstancia().temaActivo().colorBordeEnfocado()));
            }

            @Override
            public void focusLost(FocusEvent evento) {
                if (obtenerTextoCrudo().isEmpty()) {
                    mostrarPlaceholder();
                }
                pnlCaja.setBorder(bordeCaja(GestorTema.obtenerInstancia().temaActivo().colorBorde()));
            }
        });

        pnlCaja = new JPanel(new BorderLayout(8, 0));
        pnlCaja.setOpaque(true);
        pnlCaja.add(txtCampo, BorderLayout.CENTER);

        iniciarPerdidaFocoAlClickAfuera();
    }

    @Override
    public void alConfirmar(Runnable accion) {
        txtCampo.addActionListener(evento -> accion.run());
    }

    private void iniciarPerdidaFocoAlClickAfuera() {
        AWTEventListener listenerClickAfuera = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent evento) {
                if (evento.getID() != MouseEvent.MOUSE_PRESSED) return;
                if (!txtCampo.isFocusOwner()) return;
                Component origen = ((MouseEvent) evento).getComponent();
                if (origen == null || !SwingUtilities.isDescendingFrom(origen, txtCampo)) {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                }
            }
        };

        txtCampo.addAncestorListener(new javax.swing.event.AncestorListener() {
            @Override
            public void ancestorAdded(javax.swing.event.AncestorEvent evento) {
                Toolkit.getDefaultToolkit().addAWTEventListener(listenerClickAfuera, AWTEvent.MOUSE_EVENT_MASK);
            }

            @Override
            public void ancestorRemoved(javax.swing.event.AncestorEvent evento) {
                Toolkit.getDefaultToolkit().removeAWTEventListener(listenerClickAfuera);
            }

            @Override
            public void ancestorMoved(javax.swing.event.AncestorEvent evento) {
            }
        });
    }

    public void setEtiqueta(String texto) {
        lblCampo.setText(texto);
    }

    public void setPlaceholder(String texto) {
        this.placeholder = texto == null ? "" : texto;
        if (mostrandoPlaceholder || obtenerTextoCrudo().isEmpty()) {
            mostrarPlaceholder();
        }
    }

    public void mostrarValor(String valor) {
        if (valor == null || valor.isBlank()) {
            mostrarPlaceholder();
            return;
        }
        mostrandoPlaceholder = false;
        txtCampo.setText(valor);
        txtCampo.setForeground(colorTextoActual(GestorTema.obtenerInstancia().temaActivo()));
    }

    private void mostrarPlaceholder() {
        mostrandoPlaceholder = true;
        txtCampo.setText(placeholder);
        txtCampo.setForeground(GestorTema.obtenerInstancia().temaActivo().colorTextoSecundario());
    }

    private String obtenerTextoCrudo() {
        if (txtCampo instanceof JPasswordField) {
            return new String(((JPasswordField) txtCampo).getPassword());
        }
        return txtCampo.getText();
    }

    // No usa setEnabled(false): el color lo debe seguir controlando el tema activo, no Swing.
    public void setSoloLectura(boolean soloLectura) {
        this.soloLectura = soloLectura;
        txtCampo.setEditable(!soloLectura);
        txtCampo.setFocusable(!soloLectura);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    public void setIcono(Icono icono) {
        lblIcon = new JLabel();
        IconoAplicador.aplicar(lblIcon, icono, 14f, GestorTema.obtenerInstancia().temaActivo().colorTextoSecundario());
        lblIcon.setBorder(new EmptyBorder(0, 2, 0, 0));
        pnlCaja.add(lblIcon, BorderLayout.WEST);
        pnlCaja.revalidate();
    }

    public JTextField obtenerCampoTexto() {
        return txtCampo;
    }

    // Usa getPassword() en campos de contrasena, no el getText()
    public String obtenerTexto() {
        if (mostrandoPlaceholder) {
            return "";
        }
        return obtenerTextoCrudo();
    }

    public JPanel obtenerPanel() {
        return CampoTexto;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        lblCampo.setForeground(tema.colorTextoSecundario());
        lblCampo.setFont(tema.fuenteTexto());

        pnlCaja.setBackground(soloLectura ? tema.colorFondoVentana() : tema.colorFondoCampo());
        pnlCaja.setBorder(bordeCaja(tema.colorBorde()));

        txtCampo.setForeground(colorTextoActual(tema));
        txtCampo.setCaretColor(tema.colorTexto());
        txtCampo.setFont(tema.fuenteTexto());

        if (lblIcon != null) {
            lblIcon.setForeground(tema.colorTextoSecundario());
        }

        pnlCaja.repaint();
    }

    private Color colorTextoActual(Tema tema) {
        if (soloLectura) {
            return tema.colorTextoSecundario();
        }
        return mostrandoPlaceholder ? tema.colorTextoSecundario() : tema.colorTexto();
    }

    private static javax.swing.border.Border bordeCaja(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1, true),
                new EmptyBorder(5, 8, 5, 10)
        );
    }
}
