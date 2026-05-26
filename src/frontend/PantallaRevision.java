package frontend;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;
import backend.AdminEjercicios;
import backend.Ejercicio;
import backend.Observador;
import backend.EjercicioCardio;
import backend.EjercicioFuerza;

public class PantallaRevision extends JPanel implements Observador {

    private Controlador navegador;
    private AdminEjercicios admin;
    
    private Vector<Ejercicio> rutinaFiltrada;
    private int indiceActual;


    private JLabel lblContador;
    private JLabel lblNombre;
    private JLabel lblTipo;
    private JLabel lblIntensidad;
    private JLabel lblDetalleDinamico; 
    private JTextArea txtDescripcion; 

    private JButton btnAnterior;
    private JButton btnSiguiente;
    private JButton btnFinalizar;

    public PantallaRevision(Controlador navegador, AdminEjercicios admin) {
        this.navegador = navegador;
        this.admin = admin;
        this.admin.registrarObservador(this);
        
        this.rutinaFiltrada = new Vector<>();
        this.indiceActual = 0;

 
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));


        JPanel pnlNorte = new JPanel(new GridLayout(2, 1, 0, 2));
        JLabel lblTitulo = new JLabel("Paso 3: Revisión de la Rutina", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblContador = new JLabel("Ejercicio 0 de 0", SwingConstants.CENTER);
        lblContador.setFont(new Font("Arial", Font.ITALIC, 12));
        pnlNorte.add(lblTitulo);
        pnlNorte.add(lblContador);
        add(pnlNorte, BorderLayout.NORTH);

        JPanel pnlFicha = new JPanel();
        pnlFicha.setLayout(new BoxLayout(pnlFicha, BoxLayout.Y_AXIS));
        pnlFicha.setBorder(BorderFactory.createTitledBorder("Detalles del Ejercicio"));

        lblNombre = new JLabel("Nombre: ---");
        lblTipo = new JLabel("Tipo: ---");
        lblIntensidad = new JLabel("Intensidad: ---");
        lblDetalleDinamico = new JLabel("Prescripción: ---");

        Font fontDatos = new Font("Arial", Font.PLAIN, 13);
        lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
        lblTipo.setFont(fontDatos);
        lblIntensidad.setFont(fontDatos);
        lblDetalleDinamico.setFont(new Font("Arial", Font.BOLD, 13));
        lblDetalleDinamico.setForeground(new Color(0, 102, 204));

        txtDescripcion = new JTextArea("Descripción: ---");
        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 12));
        txtDescripcion.setLineWrap(true);       
        txtDescripcion.setWrapStyleWord(true);   
        txtDescripcion.setEditable(false);       
        txtDescripcion.setBackground(pnlFicha.getBackground()); 

        pnlFicha.add(lblNombre);
        pnlFicha.add(Box.createVerticalStrut(4));
        pnlFicha.add(lblTipo);
        pnlFicha.add(Box.createVerticalStrut(4));
        pnlFicha.add(lblIntensidad);
        pnlFicha.add(Box.createVerticalStrut(4));
        pnlFicha.add(lblDetalleDinamico);
        pnlFicha.add(Box.createVerticalStrut(8)); 
        
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setBorder(BorderFactory.createEmptyBorder()); 
        pnlFicha.add(scrollDesc);
        add(pnlFicha, BorderLayout.CENTER);


        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnAnterior = new JButton("<< Anterior");
        btnSiguiente = new JButton("Siguiente >>");
        btnFinalizar = new JButton("Terminar");
        
        btnFinalizar.setBackground(new Color(220, 53, 69));
        btnFinalizar.setForeground(Color.WHITE);

        pnlBotones.add(btnAnterior);
        pnlBotones.add(btnSiguiente);
        pnlBotones.add(btnFinalizar);
        add(pnlBotones, BorderLayout.SOUTH);

    
        btnAnterior.addActionListener(e -> {
            if (indiceActual > 0) {
                indiceActual--;
                mostrarEjercicioActual();
            }
        });


        btnSiguiente.addActionListener(e -> {
            if (btnSiguiente.getText().equals("Resumen de la rutina")) { 
                navegador.cambiarPantalla("RESUMEN");
                
            } else {
 
                if (indiceActual < rutinaFiltrada.size() - 1) {
                    indiceActual++;
                    mostrarEjercicioActual();
                }
            }
        });


        btnFinalizar.addActionListener(e -> {
            int opcion = JOptionPane.showConfirmDialog(this, "¿Deseas volver al menú de carga?", "Finalizar", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {

                navegador.cambiarPantalla("MENU_PRINCIPAL");
            }
        });
    }

    private void mostrarEjercicioActual() {
        if (rutinaFiltrada == null || rutinaFiltrada.isEmpty()) {
            return;
        }

        Ejercicio ej = rutinaFiltrada.get(indiceActual);
        

        lblContador.setText("Mostrando Ejercicio " + (indiceActual + 1) + " de " + rutinaFiltrada.size());
        lblNombre.setText("Nombre: " + ej.getNombre()); 
        lblTipo.setText("Tipo: " + ej.getTipo()); 
        lblIntensidad.setText("Intensidad: " + ej.getIntensidad()); 
        txtDescripcion.setText("Descripción: " + ej.getDescripcion()); 
        

        if (ej instanceof EjercicioCardio) {
            EjercicioCardio ejCardio = (EjercicioCardio) ej;
            int tiempoSegundos = ejCardio.getTiempoEstimado(); 
            double distancia = ejCardio.getDistancia();
            
            int minutos = tiempoSegundos / 60;
            int segundos = tiempoSegundos % 60;
            String tiempoFormateado = String.format("%02d:%02d min", minutos, segundos);
            
            lblDetalleDinamico.setText("Prescripción: " + tiempoFormateado + " | Distancia: " + distancia + " km"); 
            
        } else if (ej instanceof EjercicioFuerza) {
            EjercicioFuerza ejFuerza = (EjercicioFuerza) ej;
            int series = ejFuerza.getSeries();
            int repeticiones = ejFuerza.getReps(); 
            double peso = ejFuerza.getPeso();
            
            lblDetalleDinamico.setText("Prescripción: " + series + " series x " + repeticiones + " reps | Carga: " + peso + " kg");
        } else {
            lblDetalleDinamico.setText("Prescripción: " + ej.getTiempoEstimado() + " min."); 
        }

        btnAnterior.setEnabled(indiceActual > 0); 

        if (indiceActual == rutinaFiltrada.size() - 1) {
            btnSiguiente.setText("Resumen de la rutina"); 
            btnSiguiente.setEnabled(true);
        } else {
            btnSiguiente.setText("Siguiente >>");
            btnSiguiente.setEnabled(true);
        }
    }

    public void reiniciarVista() {
        this.indiceActual = 0;
        this.rutinaFiltrada = admin.getRutinaActual();
        
        if (this.btnSiguiente != null) {
            this.btnSiguiente.setText("Siguiente >>");
        }
        
        mostrarEjercicioActual(); 
        
        this.revalidate();
        this.repaint();
    }

    @Override
    public void actualizar(String evento) {
        if (evento.equals("RUTINA_GENERADA") || evento.equals("PREPARAR_REVISION")) {
            reiniciarVista();
        }
    }
}