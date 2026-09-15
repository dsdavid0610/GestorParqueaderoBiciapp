package edu.unilibre.gui;

import edu.unilibre.datos.Bicicleta;
import edu.unilibre.datos.MetodoPago;
import edu.unilibre.datos.Parqueadero;
import edu.unilibre.datos.ReporteDiario;
import edu.unilibre.operaciones.GestionParqueadero;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
public class VentanaGral extends JFrame {

    private final GestionParqueadero servicio = new GestionParqueadero();
    private final Parqueadero parqueadero = new Parqueadero();

    private JTextField txtCedulaIngreso;
    private JTextField txtNumeroSerie;
    private JTextField txtTipoBicicleta;
    private JTextField txtColor;
    private JTextField txtCedulaSalida;

    private JTextArea txtAreaInformacion;

    private final DateTimeFormatter formatoHora =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public VentanaGral() {
        setTitle("Gestión de Parqueadero de Bicicletas");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initComponentes();
        mostrarEstadoParqueadero(); // Rq05: la vista principal siempre inicia con el estado actual.
    }

    private void initComponentes() {

        JPanel izquierdo = new JPanel();
        izquierdo.setLayout(new BoxLayout(izquierdo, BoxLayout.Y_AXIS));
        izquierdo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Registrar ingreso de bicicleta (Rq01)
        JPanel pnlIngreso = new JPanel(new GridLayout(5, 2, 6, 6));
        pnlIngreso.setBorder(BorderFactory.createTitledBorder("1. Registrar ingreso de bicicleta"));

        pnlIngreso.add(new JLabel("C.C. Dueño:"));
        txtCedulaIngreso = new JTextField();
        pnlIngreso.add(txtCedulaIngreso);

        pnlIngreso.add(new JLabel("Número de serie:"));
        txtNumeroSerie = new JTextField();
        pnlIngreso.add(txtNumeroSerie);

        pnlIngreso.add(new JLabel("Tipo de bicicleta:"));
        txtTipoBicicleta = new JTextField();
        pnlIngreso.add(txtTipoBicicleta);

        pnlIngreso.add(new JLabel("Color:"));
        txtColor = new JTextField();
        pnlIngreso.add(txtColor);

        JButton btnIngreso = new JButton("Registrar Ingreso");
        estiloBoton(btnIngreso);
        pnlIngreso.add(new JLabel());
        pnlIngreso.add(btnIngreso);

        // 2. Registrar salida y cobrar (Rq03/Rq04)
        JPanel pnlSalida = new JPanel(new GridLayout(2, 2, 6, 6));
        pnlSalida.setBorder(BorderFactory.createTitledBorder("2. Registrar salida y cobrar"));

        pnlSalida.add(new JLabel("C.C. Dueño:"));
        txtCedulaSalida = new JTextField();
        pnlSalida.add(txtCedulaSalida);

        JButton btnCobrar = new JButton("Cobrar y liberar cupo");
        estiloBoton(btnCobrar);
        pnlSalida.add(new JLabel());
        pnlSalida.add(btnCobrar);

        // 3. Reporte por día (Rq05)
        JPanel pnlReporte = new JPanel();
        pnlReporte.setLayout(new BoxLayout(pnlReporte, BoxLayout.Y_AXIS));
        pnlReporte.setBorder(BorderFactory.createTitledBorder("3. Reporte por día"));

        JButton btnReporte = new JButton("Generar reporte de hoy");
        estiloBoton(btnReporte);
        btnReporte.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReporte.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnReporte.getPreferredSize().height));

        JButton btnEstado = new JButton("Ver estado del parqueadero");
        estiloBoton(btnEstado);
        btnEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnEstado.getPreferredSize().height));

        pnlReporte.add(btnReporte);
        pnlReporte.add(Box.createVerticalStrut(8));
        pnlReporte.add(btnEstado);

        // Agregar paneles
        izquierdo.add(pnlIngreso);
        izquierdo.add(Box.createVerticalStrut(10));
        izquierdo.add(pnlSalida);
        izquierdo.add(Box.createVerticalStrut(10));
        izquierdo.add(pnlReporte);
        izquierdo.add(Box.createVerticalGlue());

        // Panel derecho: información
        JPanel derecho = new JPanel(new BorderLayout());
        derecho.setBorder(BorderFactory.createTitledBorder("Información"));

        txtAreaInformacion = new JTextArea();
        txtAreaInformacion.setEditable(false);
        txtAreaInformacion.setFont(new Font("Monospaced", Font.PLAIN, 13));

        derecho.add(new JScrollPane(txtAreaInformacion), BorderLayout.CENTER);

        add(izquierdo, BorderLayout.WEST);
        add(derecho, BorderLayout.CENTER);

        // Registrar ingreso
        btnIngreso.addActionListener(e -> {
            String cedula = txtCedulaIngreso.getText().trim();
            String serie = txtNumeroSerie.getText().trim();
            String tipo = txtTipoBicicleta.getText().trim();
            String color = txtColor.getText().trim();
            try {
                Bicicleta bici = servicio.registrarIngreso(parqueadero, cedula, serie, tipo, color);
                txtCedulaIngreso.setText("");
                txtNumeroSerie.setText("");
                txtTipoBicicleta.setText("");
                txtColor.setText("");
                JOptionPane.showMessageDialog(this,
                        "Ingreso registrado.\nCédula: " + bici.obtenerCedulaDueño()
                                + "\nSerie: " + bici.obtenerNumeroSerie()
                                + "\nTipo: " + bici.obtenerTipoBicicleta()
                                + "\nColor: " + bici.obtenerColor()
                                + "\nHora de ingreso: " + bici.obtenerHoraIngreso().format(formatoHora));
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            mostrarEstadoParqueadero();
        });

        // Registrar salida + preguntar método de pago + cobrar + liberar cupo
        btnCobrar.addActionListener(e -> {
            String cedula = txtCedulaSalida.getText().trim();
            try {
                Bicicleta salida = servicio.registrarSalida(parqueadero, cedula);

                // Rq03: preguntarle al dueño el método de pago.
                JComboBox<MetodoPago> cbMetodo = new JComboBox<>(MetodoPago.values());
                int opcion = JOptionPane.showConfirmDialog(this,
                        new Object[]{
                                "Cédula: " + salida.obtenerCedulaDueño(),
                                "Valor a pagar: $" + (int) salida.obtenerValorCobrado(),
                                "Elija el método de pago:",
                                cbMetodo
                        },
                        "Método de pago", JOptionPane.OK_CANCEL_OPTION);

                if (opcion == JOptionPane.OK_OPTION) {
                    MetodoPago metodoElegido = (MetodoPago) cbMetodo.getSelectedItem();
                    double valorCobrado = servicio.registrarPago(parqueadero, cedula, metodoElegido);

                    txtCedulaSalida.setText("");

                    JOptionPane.showMessageDialog(this,
                            "Pago registrado.\nMétodo: " + metodoElegido
                                    + "\nValor cobrado: $" + (int) valorCobrado
                                    + "\nCupo liberado.");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "El pago quedó pendiente. La bicicleta sigue ocupando el cupo hasta que se registre el pago.");
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            mostrarEstadoParqueadero();
        });

        // Generar reporte del día
        btnReporte.addActionListener(e -> mostrarReporteDelDia());

        // Ver estado del parqueadero
        btnEstado.addActionListener(e -> mostrarEstadoParqueadero());
    }

    // Botones azules con letras blancas
    private void estiloBoton(JButton boton) {
        boton.setBackground(new Color(51, 111, 158));
        boton.setForeground(Color.WHITE);
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Arial", Font.BOLD, 13));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        boton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton b = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(51, 111, 158));
                g2.fillRect(0, 0, b.getWidth(), b.getHeight());
                super.paint(g2, c);
                g2.dispose();
            }
        });
    }

    /** Rq05: reporte diario con ganancias y bicicletas retiradas/cobradas. */
    private void mostrarReporteDelDia() {
        StringBuilder sb = new StringBuilder();

        sb.append("=========================================\n");
        sb.append("            REPORTE DEL DÍA\n");
        sb.append("=========================================\n\n");

        ReporteDiario reporte = servicio.obtenerReporteDelDia();
        sb.append("Fecha: ").append(reporte.obtenerFecha()).append("\n\n");

        sb.append(String.format("%-15s -------- %s%n", "#BICICLETAS", "VALOR INGRESADO"));
        sb.append(String.format("%-15d -------- $%,.0f%n",
                reporte.obtenerCantidadBicicletas(), reporte.obtenerValorTotalIngresado()));

        txtAreaInformacion.setText(sb.toString());
    }

    /** Rq05: estado actual del parqueadero (vista por defecto y bajo demanda). */
    private void mostrarEstadoParqueadero() {
        StringBuilder sb = new StringBuilder();

        sb.append("=========================================\n");
        sb.append("          ESTADO DEL PARQUEADERO\n");
        sb.append("=========================================\n\n");

        sb.append("Capacidad máxima: ").append(Parqueadero.CAPACIDAD_MAXIMA).append("\n");
        sb.append("Cupos ocupados  : ").append(parqueadero.obtenerCantidadOcupados()).append("\n");
        sb.append("Cupos libres    : ").append(parqueadero.obtenerCantidadDisponibles()).append("\n\n");

        sb.append("BICICLETAS ACTUALMENTE DENTRO:\n");

        Bicicleta[] bicicletas = parqueadero.obtenerBicicletas();
        boolean hayBicicletas = false;

        for (int i = 0; i < bicicletas.length; i++) {
            Bicicleta b = bicicletas[i];
            if (b != null) {
                hayBicicletas = true;
                if (b.estaEnParqueadero()) {
                    sb.append(String.format(
                            "[%02d] Cédula: %s | Serie: %s | Tipo: %s | Color: %s | Ingreso: %s | Estado: PARQUEADA%n",
                            i, b.obtenerCedulaDueño(), b.obtenerNumeroSerie(), b.obtenerTipoBicicleta(),
                            b.obtenerColor(), b.obtenerHoraIngreso().format(formatoHora)));
                } else {
                    sb.append(String.format(
                            "[%02d] Cédula: %s | Serie: %s | Salida: %s | Valor: $%,.0f | Estado: PENDIENTE DE PAGO%n",
                            i, b.obtenerCedulaDueño(), b.obtenerNumeroSerie(),
                            b.obtenerHoraSalida().format(formatoHora), b.obtenerValorCobrado()));
                }
            }
        }

        if (!hayBicicletas) {
            sb.append("(Ninguna. El parqueadero está vacío).\n");
        }

        txtAreaInformacion.setText(sb.toString());
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo establecer el estilo del sistema.");
        }

        SwingUtilities.invokeLater(() -> new VentanaGral().setVisible(true));
    }
}
