package frontend;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import backend.AdminEjercicios;
import backend.Ejercicio;
import backend.EjercicioCardio;
import backend.EjercicioFuerza;
import backend.Observador;

public class PantallaResumen extends JPanel implements Observador {

    private Controlador navegador;
    private AdminEjercicios admin;

    private JTable tablaEjercicios;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalEjercicios, lblTiempoTotal, lblSemanaAsignada;
    private JButton btnConfirmar, btnDescartar;

    public PantallaResumen(Controlador navegador, AdminEjercicios admin) {
        this.navegador = navegador;
        this.admin = admin;
        this.admin.registrarObservador(this);

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel pnlNorte = new JPanel(new GridLayout(2, 1));
        JLabel lblTitulo = new JLabel("Paso 3: Resumen y Confirmación de la Rutina", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(0, 48, 87));
        
        JLabel lblSub = new JLabel("Verifique los ejercicios seleccionados y su volumen antes de guardar los cambios.", SwingConstants.LEFT);
        lblSub.setFont(new Font("Arial", Font.ITALIC, 13));
        lblSub.setForeground(Color.GRAY);
        
        pnlNorte.add(lblTitulo);
        pnlNorte.add(lblSub);
        add(pnlNorte, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre", "Tipo", "Intensidad", "Tiempo (Min)", "Detalles Especiales"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaEjercicios = new JTable(modeloTabla);
        tablaEjercicios.setRowHeight(25);
        tablaEjercicios.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollTabla = new JScrollPane(tablaEjercicios);
        add(scrollTabla, BorderLayout.CENTER);

        JPanel pnlSur = new JPanel(new BorderLayout(10, 10));

        JPanel pnlCards = new JPanel(new GridLayout(1, 3, 15, 0));
        pnlCards.setBorder(BorderFactory.createTitledBorder("Métricas Clave de la Sesión"));

        lblTotalEjercicios = new JLabel("Ejercicios Totales: 0", SwingConstants.CENTER);
        lblTotalEjercicios.setFont(new Font("Arial", Font.BOLD, 14));
        
        lblTiempoTotal = new JLabel("Tiempo Estimado: 0 min", SwingConstants.CENTER);
        lblTiempoTotal.setFont(new Font("Arial", Font.BOLD, 14));
        
        lblSemanaAsignada = new JLabel("Semana del Año: -", SwingConstants.CENTER);
        lblSemanaAsignada.setFont(new Font("Arial", Font.BOLD, 14));

        pnlCards.add(lblTotalEjercicios);
        pnlCards.add(lblTiempoTotal);
        pnlCards.add(lblSemanaAsignada);
        pnlSur.add(pnlCards, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        btnDescartar = new JButton("Descartar y Volver");
        btnDescartar.setBackground(new Color(220, 53, 69));
        btnDescartar.setForeground(Color.WHITE);
        btnDescartar.setFont(new Font("Arial", Font.BOLD, 12));

        btnConfirmar = new JButton("Confirmar y Guardar Rutina");
        btnConfirmar.setBackground(new Color(40, 167, 69));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 12));

        pnlBotones.add(btnDescartar);
        pnlBotones.add(btnConfirmar);
        pnlSur.add(pnlBotones, BorderLayout.SOUTH);

        add(pnlSur, BorderLayout.SOUTH);

        btnDescartar.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de descartar esta rutina? Deberá seleccionar los ejercicios nuevamente.", 
                "Confirmar Descarte", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (opcion == JOptionPane.YES_OPTION) {
                admin.reiniciarRutina();
                admin.notificar("PREPARAR_REVISION");
                navegador.cambiarPantalla("GENERACION");
            }
        });

        btnConfirmar.addActionListener(e -> {
            try {
                for (Ejercicio ej : admin.getRutinaActual()) {
                    ej.setUltimaSemanaUsado(admin.getSemanaActualRutina());
                }
                
                admin.commitCambios();
                
                JOptionPane.showMessageDialog(this, 
                    "¡Rutina almacenada con éxito!\nLos historiales de uso se han actualizado en la base de datos.", 
                    "Sincronización Exitosa", JOptionPane.INFORMATION_MESSAGE);
                
                admin.notificar("PREPARAR_REVISION");
                navegador.cambiarPantalla("MENU_PRINCIPAL");
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error crítico al escribir en el archivo de texto: " + ex.getMessage(), 
                    "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Override
    public void actualizar(String evento) {
        if (evento.equals("RUTINA_GENERADA")) {
            modeloTabla.setRowCount(0);
            int tiempoAcumulado = 0;
            
            for (Ejercicio ej : admin.getRutinaActual()) {
                tiempoAcumulado += ej.getTiempoEstimado();
                String detallesEspeciales = "";
                
                if (ej instanceof EjercicioCardio) {
                    EjercicioCardio c = (EjercicioCardio) ej;
                    detallesEspeciales = "Distancia: " + c.getDistancia() + " Km";
                } else if (ej instanceof EjercicioFuerza) {
                    EjercicioFuerza f = (EjercicioFuerza) ej;
                    detallesEspeciales = f.getSeries() + " Series x " + f.getReps() + " Reps (" + f.getPeso() + " Kg)";
                }
                
                Object[] fila = {
                    ej.getId(),
                    ej.getNombre(),
                    ej.getTipo(),
                    ej.getIntensidad(),
                    ej.getTiempoEstimado(),
                    detallesEspeciales
                };
                
                modeloTabla.addRow(fila);
            }
            
            lblTotalEjercicios.setText("Ejercicios Totales: " + admin.getRutinaActual().size());
            lblTiempoTotal.setText("Tiempo Estimado: " + tiempoAcumulado + " min");
            lblSemanaAsignada.setText("Semana del Año: " + admin.getSemanaActualRutina());
        }
    }
}