package frontend;

import javax.swing.*;
import java.awt.*;
import backend.AdminEjercicios;
import backend.Ejercicio;
import backend.EjercicioCardio;
import backend.EjercicioFuerza;
import backend.Intensidad;
import backend.Observador;
import java.awt.event.ActionEvent;

public class PantallaEditarCliente extends JPanel implements Observador {

    private AdminEjercicios admin;
    
    private JList<Ejercicio> listaEjercicios;
    private DefaultListModel<Ejercicio> modeloLista;
    
    private JButton btnAnadir;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnVolver;

    public PantallaEditarCliente(Controlador navegador, AdminEjercicios admin) {
        this.admin = admin;
        this.admin.registrarObservador(this);

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Administración BD Ejercicios (MySQL)", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        modeloLista = new DefaultListModel<>();
        listaEjercicios = new JList<>(modeloLista);
        listaEjercicios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaEjercicios.setFont(new Font("Monospaced", Font.PLAIN, 12));

        listaEjercicios.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lblItem = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lblItem.setFont(new Font("Monospaced", Font.PLAIN, 12));
                lblItem.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

                if (value instanceof Ejercicio) {
                    Ejercicio ej = (Ejercicio) value;
                    if (ej instanceof EjercicioCardio) {
                        EjercicioCardio cardio = (EjercicioCardio) ej;
                        lblItem.setText(String.format("[CARDIO] ID: %-5s | %-20s | %-12s | %-3d min | Sem: %-2d | Distancia: %.1f km | Obs: %s", 
                                cardio.getId(), cardio.getNombre(), cardio.getIntensidad(), 
                                cardio.getTiempoEstimado(), cardio.getUltimaSemanaUsado(), cardio.getDistancia(), cardio.getDescripcion()));
                    } else if (ej instanceof EjercicioFuerza) {
                        EjercicioFuerza fuerza = (EjercicioFuerza) ej;
                        lblItem.setText(String.format("[FUERZA] ID: %-5s | %-20s | %-12s | %-3d min | Sem: %-2d | Series: %d | Reps: %-2d | Peso: %.1f kg | Obs: %s", 
                                fuerza.getId(), fuerza.getNombre(), fuerza.getIntensidad(), fuerza.getTiempoEstimado(), fuerza.getUltimaSemanaUsado(),
                                fuerza.getSeries(), fuerza.getReps(), fuerza.getPeso(), fuerza.getDescripcion()));
                    }
                }
                
                if (isSelected) {
                    lblItem.setBackground(list.getSelectionBackground());
                    lblItem.setForeground(list.getSelectionForeground());
                } else {
                    lblItem.setBackground(list.getBackground());
                    lblItem.setForeground(list.getForeground());
                }
                return lblItem;
            }
        });
        
        JScrollPane scrollLista = new JScrollPane(listaEjercicios);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Ejercicios en el Sistema"));
        add(scrollLista, BorderLayout.CENTER);

        JPanel pnlAcciones = new JPanel(new GridLayout(4, 1, 0, 10));
        btnAnadir = new JButton("Añadir Ejercicio");
        btnEditar = new JButton("Editar Seleccionado");
        btnEliminar = new JButton("Eliminar");
        btnVolver = new JButton("<- Menú Principal");
        
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);

        pnlAcciones.add(btnAnadir);
        pnlAcciones.add(btnEditar);
        pnlAcciones.add(btnEliminar);
        pnlAcciones.add(btnVolver);
        
        JPanel pnlContenedorAcciones = new JPanel(new BorderLayout());
        pnlContenedorAcciones.add(pnlAcciones, BorderLayout.NORTH);
        add(pnlContenedorAcciones, BorderLayout.EAST);

        btnVolver.addActionListener(e -> navegador.cambiarPantalla("MENU_PRINCIPAL"));

        btnEliminar.addActionListener((ActionEvent e) -> {
            Ejercicio seleccionado = listaEjercicios.getSelectedValue();
            if (seleccionado != null) {
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar: " + seleccionado.getNombre() + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                if (confirmado == JOptionPane.YES_OPTION) {
                    try {
                        admin.eliminarEjercicio(seleccionado);
                        admin.commitCambios(); // Sincroniza el vector con MySQL
                    } catch (Exception er) {
                        JOptionPane.showMessageDialog(this, "Error en la Base de Datos al eliminar: " + er.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un ejercicio de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnAnadir.addActionListener(e -> abrirFormularioEjercicio(null));

        btnEditar.addActionListener(e -> {
            Ejercicio seleccionado = listaEjercicios.getSelectedValue();
            if (seleccionado != null) {
                abrirFormularioEjercicio(seleccionado);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un ejercicio de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        refrescarLista();
    }

    private void refrescarLista() {
        modeloLista.clear();
        if (admin.getPoolEjercicios() != null) {
            for (Ejercicio ej : admin.getPoolEjercicios()) {
                if (ej instanceof EjercicioCardio) modeloLista.addElement(ej);
            }
            for (Ejercicio ej : admin.getPoolEjercicios()) {
                if (ej instanceof EjercicioFuerza) modeloLista.addElement(ej);
            }
        }
    }

    private String generarSiguienteId() {
        int maxId = 0;
        if (admin.getPoolEjercicios() != null) {
            for (Ejercicio ej : admin.getPoolEjercicios()) {
                try {
                    String idStr = ej.getId().replaceAll("[^0-9]", "");
                    if (!idStr.isEmpty()) {
                        int numId = Integer.parseInt(idStr);
                        if (numId > maxId) {
                            maxId = numId;
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignora IDs que no tengan formato numérico parseable
                }
            }
        }
        return String.format("%03d", maxId + 1);
    }

    private void abrirFormularioEjercicio(Ejercicio ejEditar) {
        boolean esEdicion = (ejEditar != null);
        String tipoSeleccionado = "Cardio";

        if (!esEdicion) {
            String[] opcionesTipo = {"Cardio", "Fuerza"};
            int seleccionTipo = JOptionPane.showOptionDialog(this, "Selecciona el Tipo de Ejercicio a agregar:",
                    "Tipo de Ejercicio", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcionesTipo, opcionesTipo[0]);
            if (seleccionTipo == -1) return;
            tipoSeleccionado = opcionesTipo[seleccionTipo];
        } else {
            tipoSeleccionado = (ejEditar instanceof EjercicioCardio) ? "Cardio" : "Fuerza";
        }

        JPanel pnlForm = new JPanel(new GridLayout(8, 2, 5, 5));
        
        JTextField txtNombre = new JTextField(esEdicion ? ejEditar.getNombre() : "");
        JComboBox<Intensidad> comboIntensidad = new JComboBox<>(Intensidad.values());
        if(esEdicion) comboIntensidad.setSelectedItem(ejEditar.getIntensidad());
        
        JTextField txtTiempo = new JTextField(esEdicion ? String.valueOf(ejEditar.getTiempoEstimado()) : "");
        JTextField txtSemana = new JTextField(esEdicion ? String.valueOf(ejEditar.getUltimaSemanaUsado()) : "-1");
        JTextField txtDescripcion = new JTextField(esEdicion ? ejEditar.getDescripcion() : "");

        JLabel lblEspecifico1 = new JLabel(tipoSeleccionado.equals("Cardio") ? "Distancia (km):" : "Series:");
        JTextField txtEspecifico1 = new JTextField();
        JLabel lblEspecifico2 = new JLabel("Repeticiones:");
        JTextField txtEspecifico2 = new JTextField();
        JLabel lblEspecifico3 = new JLabel("Carga/Peso (kg):");
        JTextField txtEspecifico3 = new JTextField();

        if (esEdicion) {
            if (ejEditar instanceof EjercicioCardio) {
                txtEspecifico1.setText(String.valueOf(((EjercicioCardio) ejEditar).getDistancia()));
            } else if (ejEditar instanceof EjercicioFuerza) {
                txtEspecifico1.setText(String.valueOf(((EjercicioFuerza) ejEditar).getSeries()));
                txtEspecifico2.setText(String.valueOf(((EjercicioFuerza) ejEditar).getReps()));
                txtEspecifico3.setText(String.valueOf(((EjercicioFuerza) ejEditar).getPeso()));
            }
        }

        pnlForm.add(new JLabel("Nombre:")); pnlForm.add(txtNombre);
        pnlForm.add(new JLabel("Intensidad:")); pnlForm.add(comboIntensidad);
        pnlForm.add(new JLabel("Tiempo Estimado (min):")); pnlForm.add(txtTiempo);
        pnlForm.add(new JLabel("Última Semana Usado:")); pnlForm.add(txtSemana);
        pnlForm.add(new JLabel("Descripción/Obs:")); pnlForm.add(txtDescripcion);
        pnlForm.add(lblEspecifico1); pnlForm.add(txtEspecifico1);
        
        if (tipoSeleccionado.equals("Fuerza")) {
            pnlForm.add(lblEspecifico2); pnlForm.add(txtEspecifico2);
            pnlForm.add(lblEspecifico3); pnlForm.add(txtEspecifico3);
        }

        int result = JOptionPane.showConfirmDialog(this, pnlForm, 
                esEdicion ? "Editar Ejercicio (ID: " + ejEditar.getId() + ")" : "Nuevo Ejercicio - " + tipoSeleccionado, JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String nombre = txtNombre.getText().trim();
                Intensidad intensidad = (Intensidad) comboIntensidad.getSelectedItem();
                int tiempoEstimado = Integer.parseInt(txtTiempo.getText().trim());
                int ultimaSemana = Integer.parseInt(txtSemana.getText().trim());
                String desc = txtDescripcion.getText().trim();

                if (esEdicion) {
                    ejEditar.setNombre(nombre);
                    ejEditar.setIntensidad(intensidad);
                    ejEditar.setTiempoEstimado(tiempoEstimado);
                    ejEditar.setUltimaSemanaUsado(ultimaSemana);
                    ejEditar.setDesc(desc);
                    
                    if (ejEditar instanceof EjercicioCardio) {
                        ((EjercicioCardio) ejEditar).setDistancia(Double.parseDouble(txtEspecifico1.getText().trim()));
                    } else {
                        ((EjercicioFuerza) ejEditar).setSeries(Integer.parseInt(txtEspecifico1.getText().trim()));
                        ((EjercicioFuerza) ejEditar).setReps(Integer.parseInt(txtEspecifico2.getText().trim()));
                        ((EjercicioFuerza) ejEditar).setPeso(Double.parseDouble(txtEspecifico3.getText().trim()));
                    }
                    admin.notificar("POOL_MODIFICADO");
                } else {
                    String idAuto = generarSiguienteId();
                    Ejercicio nuevoEj;
                    if (tipoSeleccionado.equals("Cardio")) {
                        double dist = Double.parseDouble(txtEspecifico1.getText().trim());
                        nuevoEj = new EjercicioCardio(idAuto, nombre, "Cardio", intensidad, tiempoEstimado, desc, dist);
                    } else {
                        int series = Integer.parseInt(txtEspecifico1.getText().trim());
                        int reps = Integer.parseInt(txtEspecifico2.getText().trim());
                        double peso = Double.parseDouble(txtEspecifico3.getText().trim());
                        nuevoEj = new EjercicioFuerza(idAuto, nombre, "Fuerza", intensidad, tiempoEstimado, desc, series, reps, peso);
                    }
                    nuevoEj.setUltimaSemanaUsado(ultimaSemana);
                    admin.agregarEjercicio(nuevoEj); 
                }
                
                admin.commitCambios(); 
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en la operación o Base de Datos: " + ex.getMessage(), "Error Operacional", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    @Override
    public void actualizar(String evento) {
        if (evento.equals("CARGA_EXITOSA") || evento.equals("POOL_MODIFICADO")) {
            refrescarLista();
        }
    }
}