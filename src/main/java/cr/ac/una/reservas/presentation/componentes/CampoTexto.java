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
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class CampoTexto implements CambioTemaListener {

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

    /**
     * @param esContrasena si es true, txtCampo se crea como
     *                     JPasswordField real (no un JTextField con
     *                     echo character simulado), para que el texto
     *                     quede efectivamente censurado, incluyendo
     *                     proteccion nativa contra copiarlo tal cual.
     */
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
        // El placeholder es texto real dentro del campo (no hay una
        // API nativa de "placeholder" en JTextField), asi que se
        // simula: aparece en gris al perder el foco si el campo esta
        // vacio, y se limpia apenas el usuario hace click/tab para
        // escribir, en vez de quedar ahi estorbando como texto que hay
        // que borrar a mano.
        txtCampo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evento) {
                if (mostrandoPlaceholder) {
                    mostrandoPlaceholder = false;
                    txtCampo.setText("");
                    txtCampo.setForeground(GestorTema.obtenerInstancia().temaActivo().colorTexto());
                }
                // Resalta el borde de la caja mientras el campo tiene
                // foco, para que se note claramente cuando lo pierde
                // (antes el borde no cambiaba nunca, y el campo daba
                // la sensacion de seguir "enfocado" aunque el usuario
                // ya hubiera hecho click en otra parte).
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
    }

    public void setEtiqueta(String texto) {
        lblCampo.setText(texto);
    }

    /**
     * Define el texto de ejemplo a mostrar cuando el campo esta vacio
     * y sin foco (por ejemplo "Ej: Proyector HDMI 4K"). A diferencia
     * de antes, esto YA NO escribe el texto de forma permanente: se
     * muestra en gris solo mientras el campo este vacio, desaparece
     * apenas el usuario le da click/tab para escribir, y vuelve a
     * aparecer si lo deja vacio de nuevo al salir.
     */
    public void setPlaceholder(String texto) {
        this.placeholder = texto == null ? "" : texto;
        if (mostrandoPlaceholder || obtenerTextoCrudo().isEmpty()) {
            mostrarPlaceholder();
        }
    }

    /**
     * Pone un valor real en el campo (por ejemplo, al cargar una
     * categoria existente para editarla), en vez de escribir
     * directamente en obtenerCampoTexto(). Si el valor es nulo o
     * vacio, muestra el placeholder configurado (si el campo es solo
     * lectura, se ve permanentemente en gris, ya que nunca puede
     * recibir foco para "activarse" como en un campo editable).
     */
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

    /**
     * Deshabilita la edicion del campo sin usar setEnabled(false), para
     * que el color de texto/fondo lo siga controlando el tema activo
     * (ver aplicarTema) en vez de que Swing lo pinte gris por su cuenta.
     * Uso tipico: el ID autogenerado en la vista de detalle (ver
     * ejemplo "Funcionarios (Administrador)" del enunciado), que se
     * muestra pero no se puede editar.
     */
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

    /**
     * Valor actual del campo. Si se esta mostrando el placeholder (el
     * campo esta vacio y sin foco), devuelve cadena vacia en vez del
     * texto de ejemplo, que nunca debe tratarse como un valor real
     * ingresado por el usuario. Si es un campo de contrasena, lee el
     * valor via JPasswordField.getPassword() (no el getText() heredado
     * y deprecado) para no depender de una API marcada insegura.
     */
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
