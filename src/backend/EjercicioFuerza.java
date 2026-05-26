
package backend;

public class EjercicioFuerza extends Ejercicio {
    int series;
    int reps;
    double peso;
    
    public EjercicioFuerza(String id, String nombre, String tipo, Intensidad intensidad, int tiempoEstimado, String desc, int series, int reps, double peso) {
        super(id, nombre, intensidad, tiempoEstimado, desc);
        this.series = series;
        this.reps = reps;
        this.peso = peso;
        this.tipo = "Fuerza";
    }
    
    @Override
    public String getTipo() {
        return tipo;
    }
    
    public void setSeries(int series) {
        this.series = series;
    }
    
    public void setReps(int reps) {
        this.reps = reps;
    }
    
    public void setPeso(double peso) {
        this.peso = peso;
    }
    
    
    public int getSeries() { return series; }
    public int getReps() { return reps; }
    public double getPeso() { return peso; }

}
