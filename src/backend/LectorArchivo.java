package backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Vector;

public class LectorArchivo {


    public static Vector<Ejercicio> cargarEjercicios(String ruta) throws FileNotFoundException, IOException, IllegalArgumentException {
        Vector<Ejercicio> listaCargada = new Vector<>();
        
     
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            int numeroLinea = 0;
            
            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                // ignora líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }
                
                // separa por punto y coma
                String[] datos = linea.split(";");
                
                //formato mínimo al menos 8 campos para Cardio y 10 para Fuerza
                if (datos.length < 8) {
                    throw new IllegalArgumentException("Línea " + numeroLinea + ": Información incompleta. Faltan atributos esenciales.");
                }
                
                String id = datos[0].trim();
                String nombre = datos[1].trim();
                String tipo = datos[2].trim();
                String intStr = datos[3].trim().toUpperCase();
                
   
                Intensidad intensidad;
                try {
                    intensidad = Intensidad.valueOf(intStr.replace(" ", "_"));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Línea " + numeroLinea + ": Nivel de intensidad inválido ('" + intStr + "').");
                }
                
                int tiempoEstimado = Integer.parseInt(datos[4].trim());
                String descripcion = datos[5].trim();
                
                int ultimaSemana = Integer.parseInt(datos[6].trim());

                if (tipo.equalsIgnoreCase("CARDIO")) {
                    String distanciaStr = datos[7].trim().replace(",", ".");
                    double distancia = Double.parseDouble(distanciaStr);
                   
                    
                    EjercicioCardio cardio = new EjercicioCardio(id, nombre, tipo, intensidad, tiempoEstimado, descripcion, distancia);
                    cardio.setUltimaSemanaUsado(ultimaSemana);
                    listaCargada.add(cardio);
                    
                } else if (tipo.equalsIgnoreCase("FUERZA")) {
                    if (datos.length < 10) {
                        throw new IllegalArgumentException("Línea " + numeroLinea + ": Ejercicio de Fuerza con datos insuficientes (requiere series, reps y peso).");
                    }
                    int series = Integer.parseInt(datos[7].trim());
                    int reps = Integer.parseInt(datos[8].trim());
                    String pesoStr = datos[9].trim().replace(",", ".");
                    double peso = Double.parseDouble(pesoStr);

                    
                    EjercicioFuerza fuerza = new EjercicioFuerza(id, nombre, tipo, intensidad, tiempoEstimado, descripcion, series, reps, peso);
                    fuerza.setUltimaSemanaUsado(ultimaSemana);
                    listaCargada.add(fuerza);
                    
                } else {
                    // formato o tipo incorrecto
                    throw new IllegalArgumentException("Línea " + numeroLinea + ": Tipo de ejercicio desconocido ('" + tipo + "').");
                }
            }
        }
       
        return listaCargada;
    }


    public void guardarCambios(String ruta, Vector<Ejercicio> poolEjercicios) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            
            for (Ejercicio ej : poolEjercicios) {
                String linea = "";
                
                //introduce arg en sus respectivas posiciones y tipo de dato
                if (ej instanceof EjercicioCardio) {
                    EjercicioCardio c = (EjercicioCardio) ej;
                    linea = String.format("%s;%s;%s;%s;%d;%s;%d;%.1f", 
                            c.getId(), c.getNombre(), c.getTipo(), c.getIntensidad(), 
                            c.getTiempoEstimado(), c.getDescripcion(), c.getUltimaSemanaUsado(), c.getDistancia());
                } else if (ej instanceof EjercicioFuerza) {
                    EjercicioFuerza f = (EjercicioFuerza) ej;
                    linea = String.format("%s;%s;%s;%s;%d;%s;%d;%d;%d;%.1f", 
                            f.getId(), f.getNombre(), f.getTipo(), f.getIntensidad(), 
                            f.getTiempoEstimado(), f.getDescripcion(), f.getUltimaSemanaUsado(),  f.getSeries(), f.getReps(), f.getPeso());
                }

                if (!linea.isEmpty()) {
                    bw.write(linea);
                    bw.newLine();
                }
            }
            System.out.println("Sincronización del archivo .txt finalizada con éxito.");
            
        } catch (IOException e) {
            System.err.println("Error al intentar escribir en el archivo: " + e.getMessage());
        }
    }
}