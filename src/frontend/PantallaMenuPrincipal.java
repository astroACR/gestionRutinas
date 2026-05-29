package frontend;

import javax.swing.*;
import java.awt.*;
import backend.AdminEjercicios;

public class PantallaMenuPrincipal extends JPanel {

    private AdminEjercicios admin;

    public PantallaMenuPrincipal(Controlador navegador, AdminEjercicios admin) {
        this.admin = admin;

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));


        JLabel lblTitulo = new JLabel("Sistema de Gestión de Entrenamientos", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitulo, BorderLayout.NORTH);


        JPanel pnlBotones = new JPanel(new GridLayout(4, 1, 0, 15));
        
        JButton btnCargar = new JButton("1. Cargar Base de Datos");
        JButton btnEditar = new JButton("2. Administrar Ejercicios (Editar/Añadir/Eliminar)");
        JButton btnGenerar = new JButton("3. Generar Rutina Personalizada");
        JButton btnRevisar = new JButton("4. Revisar Rutina Actual");

 
        Font fontBotones = new Font("Arial", Font.PLAIN, 15);
        btnCargar.setFont(fontBotones);
        btnEditar.setFont(fontBotones);
        btnGenerar.setFont(fontBotones);
        btnRevisar.setFont(fontBotones);

        pnlBotones.add(btnCargar);
        pnlBotones.add(btnEditar);
        pnlBotones.add(btnGenerar);
        pnlBotones.add(btnRevisar);
        add(pnlBotones, BorderLayout.CENTER);

        btnCargar.addActionListener(e -> navegador.cambiarPantalla("CARGA"));
        btnEditar.addActionListener(e -> navegador.cambiarPantalla("EDITAR_CLIENTE")); 
        btnGenerar.addActionListener(e -> navegador.cambiarPantalla("GENERACION"));
        
        btnRevisar.addActionListener(e -> {
            
            admin.prepararRevision(); 
            navegador.cambiarPantalla("REVISION");
        });
    }
}