# Sistema de Gestión de Rutinas de Entrenamiento

Este proyecto es una herramienta robusta desarrollada bajo el paradigma de Programación Orientada a Objetos (POO) en Java y entorno gráfico Swing. Permite administrar una base de datos de ejercicios (altas, bajas y modificaciones) y generar rutinas mixtas personalizadas controlando el sobreentrenamiento mediante reglas de disponibilidad temporal.

---

## 1. Cómo Funciona el Código (Arquitectura)

El software está diseñado bajo una arquitectura desacoplada en capas para separar la lógica de negocio de la interfaz gráfica, utilizando el patrón de diseño Observer:

* Capa de Backend (backend):
    * Ejercicio (Clase Abstracta): Superclase que encapsula los atributos comunes (id, nombre, tipo, intensidad, tiempoEstimado, descripcion y ultimaSemanaUsado).
    * EjercicioCardio y EjercicioFuerza (Subclases): Heredan de Ejercicio e implementan atributos polimórficos propios (distancia para cardio; series, repeticiones y peso para fuerza).
    * AdminEjercicios (Controlador Central): Funciona como el "Sujeto" del patrón Observer. Centraliza el pool de ejercicios, gestiona los filtros de generación y ejecuta el guardado atómico en el archivo.
* Capa de Frontend (frontend):
    * Controlador: Gestiona la navegación secuencial entre las distintas ventanas del sistema.
    * PantallaEditarCliente (CRUD): Permite gestionar los ejercicios. El campo ID se automatiza de forma correlativa buscando el mayor identificador numérico en el pool para evitar colisiones.
    * PantallaGeneracion: Captura los parámetros del usuario (semana, tipo e intensidad). Setea la semana activa en el backend de manera secuencial antes de correr los filtros para garantizar la consistencia.
    * PantallaResumen: Muestra la rutina calculada en un JTable polimórfico no editable. Al presionar "Confirmar y Guardar", impacta los cambios en disco y gatilla eventos de limpieza gráfica mediante strings de control exactos (PREPARAR_REVISION).

### Regla de Disponibilidad Temporal (Algoritmo)
Para que un ejercicio pueda ser seleccionado en una rutina, debe cumplir la siguiente condición matemática:
Semana Actual - Última Semana Usado >= 1

Si el resultado es 0 (ya se usó en la misma semana), el sistema arroja una excepción  del tipo IllegalStateException y despliega un diálogo de advertencia en la UI sin romper el flujo del programa.

---

## 2. Formato del Archivo de Carga (ejercicios.txt)

La persistencia de datos utiliza un archivo de texto plano llamado obligatoriamente ejercicios.txt ubicado en la raíz del proyecto. El motor de persistencia realiza una lectura posicional e indexada por líneas. 

Cada línea del archivo representa un único ejercicio y sus campos deben estar separados estrictamente por el carácter de tubería (;).

### Estructura de Campos General:
ID;Nombre;Tipo;Intensidad;TiempoEstimado;UltimaSemanaUsado;Descripcion;AtributosEspecíficos...

### Especificación por Tipo de Ejercicio:

1. Ejercicios de Cardio:
   Deben contener 8 campos en total. El último campo corresponde a la distancia en kilómetros (número decimal con punto).
   * Formato: ID;Nombre;Cardio;INTENSIDAD;TiempoMin;UltimaSemana;Descripcion;DistanciaKm
   * Ejemplo: 001;Trote en Cinta;Cardio;MEDIA;30;-1;Zona aeróbica mantener ritmo;5.5

2. Ejercicios de Fuerza:
   Deben contener 10 campos en total. Los últimos tres campos corresponden estrictamente a las series (entero), repeticiones (entero) y carga/peso en kg (decimal con punto).
   * Formato: ID;Nombre;Fuerza;INTENSIDAD;TiempoMin;UltimaSemana;Descripcion;Series;Repeticiones;PesoKg
   * Ejemplo: 002;Press de Banca;Fuerza;ALTA;45;-1;Foco en la fase excéntrica;4;8;82.5

### Notas Importantes para la Carga Manual:
* Estado Inicial (-1): Si el ejercicio es nuevo o nunca ha sido integrado en una rutina, el campo UltimaSemanaUsado debe registrarse siempre como -1. Esto fuerza que matemáticamente esté disponible en cualquier semana de consulta inicial (Semana - (-1) = Semana + 1 >= 2).
* Intensidad: Los valores del cuarto campo deben coincidir exactamente con los literales definidos en el Enum de tu backend (por ejemplo: BAJA, MEDIA, ALTA).
* Separadores: Asegúrate de no dejar espacios en blanco inmediatamente antes o después del carácter ; al editar manualmente el archivo, para evitar errores de parseo numérico (NumberFormatException).