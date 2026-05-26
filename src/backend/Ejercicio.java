
package backend;


public abstract class Ejercicio {
    protected String id;
    protected String nombre;
    protected String tipo;
    protected Intensidad intensidad;
    protected int tiempoEstimado;
    protected String desc;
    protected int ultimaSemanaUsado;
    
    public Ejercicio(String id, String nombre, Intensidad intensidad, int tiempoEstimado, String desc) {
        this.id = id;
        this.nombre = nombre;
        this.intensidad = intensidad;
        this.tiempoEstimado = tiempoEstimado;
        this.desc = desc;
        this.ultimaSemanaUsado = 0;
    }
    
    public void setCodigo(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setIntensidad(Intensidad intensidad) {
        this.intensidad = intensidad;
    }

    public void setTiempoEstimado(int tiempoEstimado) {
        this.tiempoEstimado = tiempoEstimado;
    }

    public void setDesc(String descripcion) {
        this.desc = descripcion;
    }

    public void setUltimaSemanaUsado(int ultimaSemanaUsado) {
        this.ultimaSemanaUsado = ultimaSemanaUsado;
    }


    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public Intensidad getIntensidad() { return intensidad; }
    public int getTiempoEstimado() { return tiempoEstimado; }
    public String getDescripcion() { return desc; }
    public int getUltimaSemanaUsado() { return ultimaSemanaUsado; }

}