package cr.ac.una.reservas.presentation.mvc;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import cr.ac.una.reservas.presentation.mvc.componentes.BotonPrimario;
import cr.ac.una.reservas.presentation.mvc.componentes.CampoTexto;
import cr.ac.una.reservas.presentation.mvc.componentes.ComboBoxTematizado;
import cr.ac.una.reservas.presentation.mvc.componentes.Tarjeta;
import cr.ac.una.reservas.presentation.mvc.iconos.IconoSemantico;
import cr.ac.una.reservas.presentation.mvc.tema.CambioTemaListener;
import cr.ac.una.reservas.presentation.mvc.tema.GestorTema;
import cr.ac.una.reservas.presentation.mvc.tema.Tema;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class TabConfiguracionIa implements CambioTemaListener {

    public static final String MODELO_GEMINI_3_FLASH_PREVIEW = "gemini-3-flash-preview";
    public static final String MODELO_GEMINI_2_5_FLASH_LITE = "gemini-2.5-flash-lite";

    private JPanel TabConfiguracionIa;
    private JPanel TarjetaTitulo;
    private JPanel TarjetaConfiguracion;

    private Tarjeta tarjetaTituloReal;
    private Tarjeta tarjetaConfiguracionReal;

    private CampoTexto campoApiKeyReal;
    private JComboBox<String> comboModeloReal;
    private BotonPrimario botonGuardarReal;

    public TabConfiguracionIa() {
        $$$setupUI$$$();
        GestorTema.obtenerInstancia().agregarListener(this);
        aplicarTema(GestorTema.obtenerInstancia().temaActivo());
    }

    private void createUIComponents() {
        armarTarjetaTitulo();
        armarTarjetaConfiguracion();
    }

    private void armarTarjetaTitulo() {
        tarjetaTituloReal = new Tarjeta();
        tarjetaTituloReal.setIcono(IconoSemantico.AJUSTES_CUENTA.icono());
        tarjetaTituloReal.setTitulo("Configuración de IA");
        tarjetaTituloReal.setSubtitulo("Configure la API key y el modelo usados para la extracción de reservas por IA");

        TarjetaTitulo = tarjetaTituloReal.obtenerPanel();
    }

    private void armarTarjetaConfiguracion() {
        tarjetaConfiguracionReal = new Tarjeta();
        tarjetaConfiguracionReal.setTitulo("Credenciales de Gemini");
        tarjetaConfiguracionReal.setSubtitulo("Esta información se guarda localmente y no se sube al repositorio");

        campoApiKeyReal = new CampoTexto(true);
        campoApiKeyReal.setEtiqueta("API KEY");
        campoApiKeyReal.setPlaceholder("Ej: AQ.Ab8...");

        comboModeloReal = ComboBoxTematizado.crear(GestorTema.obtenerInstancia().temaActivo());
        comboModeloReal.addItem(MODELO_GEMINI_3_FLASH_PREVIEW);
        comboModeloReal.addItem(MODELO_GEMINI_2_5_FLASH_LITE);

        botonGuardarReal = new BotonPrimario();
        botonGuardarReal.setTexto("Guardar");
        botonGuardarReal.setIcono(IconoSemantico.GUARDAR.icono());

        JPanel contenido = tarjetaConfiguracionReal.obtenerPanelContenido();
        contenido.setLayout(new GridBagLayout());
        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.fill = GridBagConstraints.HORIZONTAL;
        restricciones.weightx = 1;
        restricciones.gridx = 0;

        restricciones.gridy = 0;
        restricciones.insets = new Insets(0, 0, 10, 0);
        contenido.add(campoApiKeyReal.obtenerPanel(), restricciones);

        restricciones.gridy = 1;
        restricciones.insets = new Insets(0, 0, 16, 0);
        contenido.add(comboModeloReal, restricciones);

        JPanel filaBotones = new JPanel(new BorderLayout());
        filaBotones.setOpaque(false);
        filaBotones.add(botonGuardarReal.obtenerPanel(), BorderLayout.EAST);

        restricciones.gridy = 2;
        restricciones.insets = new Insets(0, 0, 0, 0);
        contenido.add(filaBotones, restricciones);

        TarjetaConfiguracion = tarjetaConfiguracionReal.obtenerPanel();
    }

    public String obtenerApiKey() {
        return campoApiKeyReal.obtenerTexto();
    }

    public void mostrarApiKey(String apiKey) {
        campoApiKeyReal.mostrarValor(apiKey);
    }

    public String obtenerModelo() {
        return (String) comboModeloReal.getSelectedItem();
    }

    public void mostrarModelo(String modelo) {
        if (modelo != null) {
            comboModeloReal.setSelectedItem(modelo);
        }
    }

    public void alGuardar(Runnable accion) {
        botonGuardarReal.alHacerClick(accion);
    }

    public JPanel obtenerPanel() {
        return TabConfiguracionIa;
    }

    @Override
    public void onCambioTema(Tema tema) {
        aplicarTema(tema);
    }

    private void aplicarTema(Tema tema) {
        if (TabConfiguracionIa == null) return;

        TabConfiguracionIa.setOpaque(true);
        TabConfiguracionIa.setBackground(tema.colorFondoVentana());
        TabConfiguracionIa.setBorder(new EmptyBorder(0, 0, 0, 0));
        ComboBoxTematizado.aplicar(comboModeloReal, tema);
        TabConfiguracionIa.repaint();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        TabConfiguracionIa = new JPanel();
        TabConfiguracionIa.setLayout(new GridLayoutManager(3, 1, new Insets(20, 20, 20, 20), -1, 16));
        TabConfiguracionIa.add(TarjetaTitulo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        TabConfiguracionIa.add(TarjetaConfiguracion, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        TabConfiguracionIa.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return TabConfiguracionIa;
    }

}
