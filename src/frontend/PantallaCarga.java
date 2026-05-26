package frontend;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import backend.AdminEjercicios;
import backend.Observador;
import backend.Ejercicio;
import java.io.IOException;


public class PantallaCarga extends JPanel implements Observador {

    private Controlador navegador;
    private AdminEjercicios admin;

    private JButton btnBuscar;
    private JButton btnSiguiente;
    private JLabel lblTotal;
    private JLabel lblCardio;
    private JLabel lblFuerza;
    private JLabel lblIntensidadesDetalle; // Agregado para cumplir desglose del enunciado
    private JLabel lblTiempoDisponible;    // Agregado para el tiempo total del pool

    public PantallaCarga(Controlador navegador, AdminEjercicios admin) {
        this.navegador = navegador;
        this.admin = admin;
        
        this.admin.registrarObservador(this);

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 

        JLabel lblTitulo = new JLabel("Paso 1: Carga de Base de Datos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        
        JPanel pnlEstadisticas = new JPanel(new GridLayout(5, 1, 10, 10));
        pnlEstadisticas.setBorder(BorderFactory.createTitledBorder("Estadísticas del Archivo"));

        lblTotal = new JLabel("Total Ejercicios: 0");
        lblCardio = new JLabel("Ejercicios de Cardio: 0");
        lblFuerza = new JLabel("Ejercicios de Fuerza: 0");
        lblIntensidadesDetalle = new JLabel("Intensidades -> Básicos: 0 | Intermedios: 0 | Avanzados: 0 | Alto Rendimiento: 0");
        lblTiempoDisponible = new JLabel("Tiempo Total Disponible en Pool: 0 min.");

        Font fontLabels = new Font("Arial", Font.PLAIN, 14);
        lblTotal.setFont(fontLabels);
        lblCardio.setFont(fontLabels);
        lblFuerza.setFont(fontLabels);
        lblIntensidadesDetalle.setFont(fontLabels);
        lblTiempoDisponible.setFont(fontLabels);

        pnlEstadisticas.add(lblTotal);
        pnlEstadisticas.add(lblCardio);
        pnlEstadisticas.add(lblFuerza);
        pnlEstadisticas.add(lblIntensidadesDetalle);
        pnlEstadisticas.add(lblTiempoDisponible);
        add(pnlEstadisticas, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBuscar = new JButton("Buscar Archivo .txt");
        btnSiguiente = new JButton("Siguiente ->");
        btnSiguiente.setEnabled(false);

        pnlBotones.add(btnBuscar);
        pnlBotones.add(btnSiguiente);
        add(pnlBotones, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> {
            JFileChooser selector = new JFileChooser();
            int resultado = selector.showOpenDialog(this);
            
            if (resultado == JFileChooser.APPROVE_OPTION) {
                String ruta = selector.getSelectedFile().getAbsolutePath();
                try {
                    admin.cargarPoolDesdeArchivo(ruta);
                    admin.notificar("CARGA_EXITOSA");
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(this, "Archivo no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de formato", JOptionPane.WARNING_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al modificar", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error Desconocido", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSiguiente.addActionListener(e -> {
            navegador.cambiarPantalla("MENU_PRINCIPAL");
        });
    }


    @Override
    public void actualizar(String evento) {
        if (evento.equals("CARGA_EXITOSA") || evento.equals("POOL_MODIFICADO")) {
            
            int totalCardio = admin.contarTipo("Cardio");
            int totalFuerza = admin.contarTipo("Fuerza");
            int totalEjercicios = totalCardio + totalFuerza;

            int basico = admin.contarIntensidad(backend.Intensidad.BASICO);
            int intermedio = admin.contarIntensidad(backend.Intensidad.INTERMEDIO);
            int avanzado = admin.contarIntensidad(backend.Intensidad.AVANZADO);
            int altoRend = admin.contarIntensidad(backend.Intensidad.ALTO_RENDIMIENTO);

            int tiempoTotalPoolMinutos = 0;
            if (admin.getPoolEjercicios() != null) {
                for (Ejercicio ej : admin.getPoolEjercicios()) {
                    tiempoTotalPoolMinutos += ej.getTiempoEstimado();
                }
            }


            lblTotal.setText("Total Ejercicios en BD: " + totalEjercicios);
            lblCardio.setText("Ejercicios de Cardio: " + totalCardio);
            lblFuerza.setText("Ejercicios de Fuerza: " + totalFuerza);
            
            lblIntensidadesDetalle.setText(String.format("Intensidades -> Básicos: %d | Intermedios: %d | Avanzados: %d | Alto Rendimiento: %d", 
                    basico, intermedio, avanzado, altoRend));
            
            lblTiempoDisponible.setText("Tiempo Total Disponible en Pool: " + tiempoTotalPoolMinutos + " min.");


            btnSiguiente.setEnabled(true);
            btnBuscar.setEnabled(false);

            JOptionPane.showMessageDialog(this, "Ejercicios cargados correctamente", "Carga Exitosa", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}