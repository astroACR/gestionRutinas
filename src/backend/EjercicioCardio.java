
package backend;


public class EjercicioCardio extends Ejercicio {
    
    double distancia;
    
    public EjercicioCardio(String id, String nombre, String tipo, Intensidad intensidad, int tiempoEstimado, String desc, double distancia){
        super(id, nombre, intensidad, tiempoEstimado, desc);
        this.distancia = distancia;
        this.tipo = "Cardio";
    }
    
    @Override
    public String getTipo() {
        return tipo;
    }
    
    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
    
    public double getDistancia() { return distancia; }
}
