package backend;

import java.io.IOException;
import java.util.Vector;
import backend.Conexion;
import java.sql.*;

public class AdminEjercicios {
        
    private Vector<Ejercicio> poolEjercicios;
    private Vector<Ejercicio> rutinaActual;
    private Vector<Observador> observadores;
    private int semanaActualRutina;
    
    private String rutaArchivoActual;
    
    public AdminEjercicios() {
        this.poolEjercicios = new Vector<>();
        this.rutinaActual = new Vector<>();
        this.observadores = new Vector<>();
        this.semanaActualRutina = -1;

    }
    

    public void cargarDatosDesdeBD() {
        poolEjercicios.clear();
        String query = "SELECT * FROM ejercicios";

        try (Connection con = DriverManager.getConnection(Conexion.url, Conexion.user, Conexion.password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String nombre = rs.getString("nombre");
                String tipo = rs.getString("tipo");
                Intensidad intensidad = Intensidad.valueOf(rs.getString("intensidad"));
                int tiempo = rs.getString("tiempo_estimado") != null ? rs.getInt("tiempo_estimado") : 0;
                int ultimaSemana = rs.getInt("ultima_semana_usado");
                String desc = rs.getString("descripcion");

                Ejercicio ej;
                if (tipo.equalsIgnoreCase("Cardio")) {
                    double distancia = rs.getDouble("distancia");
                    ej = new EjercicioCardio(id, nombre, "Cardio", intensidad, tiempo, desc, distancia);
                } else {
                    int series = rs.getInt("series");
                    int reps = rs.getInt("repeticiones");
                    double peso = rs.getDouble("peso");
                    ej = new EjercicioFuerza(id, nombre, "Fuerza", intensidad, tiempo, desc, series, reps, peso);
                }
                
                ej.setUltimaSemanaUsado(ultimaSemana);
                poolEjercicios.add(ej);
            }
            notificar("CARGA_EXITOSA");

        } catch (SQLException e) {
            System.err.println("Error al cargar desde MySQL: " + e.getMessage());
        }
    }

    public void agregarEjercicioARutina(String tipo, Intensidad intensidad, int cantidad) throws IllegalStateException {
        int agregados = 0;
        
        for (Ejercicio ej : poolEjercicios) {
            if (agregados == cantidad) {
                break;
            }
            
            if (ej.getTipo().equalsIgnoreCase(tipo) && 
                ej.getIntensidad() == intensidad && 
                (this.semanaActualRutina - ej.getUltimaSemanaUsado()) >= 1) {
                
                if (!this.rutinaActual.contains(ej)) {
                    this.rutinaActual.add(ej);
                    agregados++;
                }
            }
        }

        // si la bd no cuenta con suficientes elementos, falla
        if (agregados < cantidad) {
            this.rutinaActual.clear();
            this.notificar("RUTINA_FALLIDA");
            throw new IllegalStateException(
                "No hay suficientes ejercicios disponibles para: \n" +
                "Tipo: " + tipo + " | Intensidad: " + intensidad + "\n" +
                "Solicitados: " + cantidad + " | Encontrados en BD válidos: " + agregados
            );
        }
    }


    public void commitCambios() {
        String deleteQuery = "DELETE FROM ejercicios";
        String insertQuery = "INSERT INTO ejercicios (id, nombre, tipo, intensidad, tiempo_estimado, ultima_semana_usado, descripcion, distancia, series, repeticiones, peso) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(Conexion.url, Conexion.user, Conexion.password)) {
            con.setAutoCommit(false); 


            try (Statement deleteStmt = con.createStatement()) {
                deleteStmt.executeUpdate(deleteQuery);
            }

            try (PreparedStatement pstmt = con.prepareStatement(insertQuery)) {
                for (Ejercicio ej : poolEjercicios) {
                    pstmt.setString(1, ej.getId());
                    pstmt.setString(2, ej.getNombre());
                    pstmt.setString(3, ej.getTipo());
                    pstmt.setString(4, ej.getIntensidad().name());
                    pstmt.setInt(5, ej.getTiempoEstimado());
                    pstmt.setInt(6, ej.getUltimaSemanaUsado());
                    pstmt.setString(7, ej.getDescripcion());

                    if (ej instanceof EjercicioCardio) {
                        pstmt.setDouble(8, ((EjercicioCardio) ej).getDistancia());
                        pstmt.setNull(9, Types.INTEGER);
                        pstmt.setNull(10, Types.INTEGER);
                        pstmt.setNull(11, Types.DOUBLE);
                    } else if (ej instanceof EjercicioFuerza) {
                        pstmt.setNull(8, Types.DOUBLE);
                        pstmt.setInt(9, ((EjercicioFuerza) ej).getSeries());
                        pstmt.setInt(10, ((EjercicioFuerza) ej).getReps());
                        pstmt.setDouble(11, ((EjercicioFuerza) ej).getPeso());
                    }
                    pstmt.addBatch(); 
                }
                pstmt.executeBatch();
            }

            con.commit(); 
            System.out.println("Sincronización exitosa con MySQL.");

        } catch (SQLException e) {
            System.err.println("Error al guardar en MySQL: " + e.getMessage());
        }
    }

    // GETTERS Y SETTERS

    public Vector<Ejercicio> getPoolEjercicios() {
        return this.poolEjercicios;
    }
   
    public void setSemanaActualRutina(int sem) {
        this.semanaActualRutina = sem;
    }
    
    public int getSemanaActualRutina() {
        return this.semanaActualRutina;
    }

    public Vector<Ejercicio> getRutinaActual() {
        return this.rutinaActual;
    }
    
    // OBSERVER 
    
    public void registrarObservador(Observador obs) {
        if (!observadores.contains(obs)) {
            observadores.add(obs);
        }
    }
    
    public void removerObservador(Observador obs) {
        observadores.remove(obs);
    }

    public void notificar(String evento) {
        for (Observador obs : observadores) {
            obs.actualizar(evento);
        }
    }
    
    public void prepararRevision() {
        this.notificar("PREPARAR_REVISION");
    }

    // CRUD 

    public void eliminarEjercicio(Ejercicio ej) {
        this.poolEjercicios.remove(ej);
        this.notificar("POOL_MODIFICADO"); 
    }

    public void agregarEjercicio(Ejercicio ej) {
        this.poolEjercicios.add(ej);
        this.notificar("POOL_MODIFICADO");
    }
    
    // METODOS CONTEO 
    
    public int contarTipo(String tipo) {
        int contador = 0;
        for (Ejercicio ej : poolEjercicios) {
            if (ej.getTipo().equalsIgnoreCase(tipo)) {
                contador++;
            }
        }
        return contador;
    }
    
    public int contarIntensidad(Intensidad intensidad) {
        int contador = 0;
        for (Ejercicio ej : poolEjercicios) {
            if (ej.getIntensidad() == intensidad) {
                contador++;
            }
        }
        return contador;
    }
    
    public void reiniciarRutina() {
        this.rutinaActual.clear();
        this.rutinaActual.trimToSize();
    }
    
}