package backend;

import java.io.IOException;
import java.util.Vector;

public class AdminEjercicios {
        
    private Vector<Ejercicio> poolEjercicios;
    private Vector<Ejercicio> rutinaActual;
    private Vector<Observador> observadores;
    private int semanaActualRutina;
    
    private String rutaArchivoActual;

    private LectorArchivo lector;
    
    public AdminEjercicios() {
        this.poolEjercicios = new Vector<>();
        this.rutinaActual = new Vector<>();
        this.observadores = new Vector<>();
        this.semanaActualRutina = -1;
        this.lector = new LectorArchivo();
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


    public void cargarPoolDesdeArchivo(String ruta) throws Exception {
        this.poolEjercicios.clear();
        
        this.rutaArchivoActual = ruta;
        
 
        Vector<Ejercicio> ejerciciosCargados = this.lector.cargarEjercicios(ruta);
        this.poolEjercicios.addAll(ejerciciosCargados);
       
        this.notificar("POOL_MODIFICADO");
    }
    

    public void commitCambios() throws IOException {
        if (this.rutaArchivoActual != null && !this.rutaArchivoActual.isEmpty()) {
            this.lector.guardarCambios(this.rutaArchivoActual, this.poolEjercicios);
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