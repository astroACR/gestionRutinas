package frontend;

import javax.swing.*;
import java.awt.*;
import backend.AdminEjercicios;
import backend.Intensidad;
import backend.Observador;

public class PantallaGeneracion extends JPanel implements Observador {

    private Controlador navegador;
    private AdminEjercicios admin;

    private JSpinner spinCardioBasico, spinCardioInter, spinCardioAvanz, spinCardioAlto;
    private JSpinner spinFuerzaBasico, spinFuerzaInter, spinFuerzaAvanz, spinFuerzaAlto;
    private JSpinner spinSemana;
    
    private JButton btnGenerar, btnAtras;

    public PantallaGeneracion(Controlador navegador, AdminEjercicios admin) {
        this.navegador = navegador;
        this.admin = admin;
        this.admin.registrarObservador(this);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Paso 2: Mezcla Personalizada de Ejercicios", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlCentral = new JPanel(new GridLayout(3, 1, 0, 10));

        JPanel pnlCardio = new JPanel(new GridLayout(2, 4, 10, 5));
        pnlCardio.setBorder(BorderFactory.createTitledBorder("Cantidad Ejercicios de Cardio"));
        
        pnlCardio.add(new JLabel("Básico:", SwingConstants.CENTER));
        pnlCardio.add(new JLabel("Intermedio:", SwingConstants.CENTER));
        pnlCardio.add(new JLabel("Avanzado:", SwingConstants.CENTER));
        pnlCardio.add(new JLabel("Alto Rend.:", SwingConstants.CENTER));
        
        spinCardioBasico = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        spinCardioInter = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        spinCardioAvanz = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        spinCardioAlto = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        
        pnlCardio.add(spinCardioBasico);
        pnlCardio.add(spinCardioInter);
        pnlCardio.add(spinCardioAvanz);
        pnlCardio.add(spinCardioAlto);
        pnlCentral.add(pnlCardio);


        JPanel pnlFuerza = new JPanel(new GridLayout(2, 4, 10, 5));
        pnlFuerza.setBorder(BorderFactory.createTitledBorder("Cantidad Ejercicios de Fuerza"));
        
        pnlFuerza.add(new JLabel("Básico:", SwingConstants.CENTER));
        pnlFuerza.add(new JLabel("Intermedio:", SwingConstants.CENTER));
        pnlFuerza.add(new JLabel("Avanzado:", SwingConstants.CENTER));
        pnlFuerza.add(new JLabel("Alto Rend.:", SwingConstants.CENTER));
        
        spinFuerzaBasico = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        spinFuerzaInter = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        spinFuerzaAvanz = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        spinFuerzaAlto = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        
        pnlFuerza.add(spinFuerzaBasico);
        pnlFuerza.add(spinFuerzaInter);
        pnlFuerza.add(spinFuerzaAvanz);
        pnlFuerza.add(spinFuerzaAlto);
        pnlCentral.add(pnlFuerza);

        JPanel pnlSemana = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlSemana.add(new JLabel("Número de Semana del Año:"));
        spinSemana = new JSpinner(new SpinnerNumberModel(1, 1, 52, 1));
        pnlSemana.add(spinSemana);
        pnlCentral.add(pnlSemana);

        add(pnlCentral, BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAtras = new JButton("<- Atrás");
        btnGenerar = new JButton("Generar Rutina Mixta");
        btnGenerar.setBackground(new Color(40, 167, 69));
        btnGenerar.setForeground(Color.WHITE);

        pnlBotones.add(btnAtras);
        pnlBotones.add(btnGenerar);
        add(pnlBotones, BorderLayout.SOUTH);

        // --- ACCIONES ---
        btnAtras.addActionListener(e -> navegador.cambiarPantalla("MENU_PRINCIPAL"));

        btnGenerar.addActionListener(e -> {
            try {
                admin.reiniciarRutina(); 
                
                int sem = (int) spinSemana.getValue();
                admin.setSemanaActualRutina(sem); 

                
                if ((int)spinCardioBasico.getValue() > 0) admin.agregarEjercicioARutina("Cardio", Intensidad.BASICO, (int)spinCardioBasico.getValue());
                if ((int)spinCardioInter.getValue() > 0)  admin.agregarEjercicioARutina("Cardio", Intensidad.INTERMEDIO, (int)spinCardioInter.getValue());
                if ((int)spinCardioAvanz.getValue() > 0)  admin.agregarEjercicioARutina("Cardio", Intensidad.AVANZADO, (int)spinCardioAvanz.getValue());
                if ((int)spinCardioAlto.getValue() > 0)   admin.agregarEjercicioARutina("Cardio", Intensidad.ALTO_RENDIMIENTO, (int)spinCardioAlto.getValue());

                if ((int)spinFuerzaBasico.getValue() > 0) admin.agregarEjercicioARutina("Fuerza", Intensidad.BASICO, (int)spinFuerzaBasico.getValue());
                if ((int)spinFuerzaInter.getValue() > 0)  admin.agregarEjercicioARutina("Fuerza", Intensidad.INTERMEDIO, (int)spinFuerzaInter.getValue());
                if ((int)spinFuerzaAvanz.getValue() > 0)  admin.agregarEjercicioARutina("Fuerza", Intensidad.AVANZADO, (int)spinFuerzaAvanz.getValue());
                if ((int)spinFuerzaAlto.getValue() > 0)   admin.agregarEjercicioARutina("Fuerza", Intensidad.ALTO_RENDIMIENTO, (int)spinFuerzaAlto.getValue());

                admin.notificar("RUTINA_GENERADA");

            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Disponibilidad", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Override
    public void actualizar(String evento) {
        if (evento.equals("RUTINA_GENERADA")) {
            JOptionPane.showMessageDialog(this, "¡Rutina mixta generada exitosamente!");
            navegador.cambiarPantalla("REVISION");
        }
    }
}