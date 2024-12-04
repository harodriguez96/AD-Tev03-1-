// Importación de librerías necesarias para manejar SQL, expresiones regulares, entrada y utilidades generales
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Ejercicio3 {

    // Constantes para la conexión a la base de datos
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/dbeventos?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "eventos";
    private static final String PASSWORD = "eventos";

    // Variable global para almacenar el nombre del asistente
    private static String nombre;

    public static void main(String[] args) {
        // Bloque try-with-resources para manejar la conexión a la base de datos automáticamente
        try (Connection con = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD)) {

            Scanner teclado = new Scanner(System.in); // Crear objeto Scanner para capturar entrada del usuario

            // Pedir al usuario que introduzca el DNI del asistente
            System.out.println("Introduce el DNI del asistente:");
            String dni = teclado.next();

            // Validar el formato del DNI
            if (validateDNI(dni)) {
                // Obtener datos del asistente y mostrar eventos disponibles
                selectAsistente(con, dni);
                listaEventos(con);

                // Pedir al usuario que seleccione un evento
                System.out.println("Introduce el numero del evento al que quieres asistir: ");
                int id_evento = teclado.nextInt();

                // Registrar al asistente en el evento seleccionado
                registrarAsistente(con, id_evento, dni);
            } else {
                // Mostrar mensaje de error si el DNI no es válido
                System.out.println("El DNI no tiene el formato correcto.");
            }
        } catch (SQLException e) {
            // Manejo de errores de conexión y ejecución de consultas
            System.err.println(e.getMessage());
        }
    }

    // Método para validar el formato del DNI usando expresiones regulares
    private static boolean validateDNI(String dni) {
        // Definir un patrón: 8 dígitos seguidos de una letra mayúscula
        Pattern pattern = Pattern.compile("^[0-9]{8}[A-Z]$");
        Matcher matcher = pattern.matcher(dni);

        // Retornar true si coincide con el patrón, false de lo contrario
        return matcher.matches();
    }

    // Método para obtener el nombre del asistente a partir de su DNI
    private static String selectAsistente(Connection con, String dni) {
        String sql = "SELECT nombre FROM asistentes where dni=?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, dni); // Establecer el parámetro de la consulta

            try (ResultSet rs = stmt.executeQuery()) {
                // Si hay resultados, mostrar el nombre del asistente
                if (rs.next()) {
                    nombre = rs.getString(1);
                    System.out.println("Estas haciendo la reserva para: " + nombre);
                    return nombre;
                }
            }
        } catch (SQLException e) {
            // Manejo de errores al ejecutar la consulta
            System.err.println(e.getMessage());
        }
        return null; // Retornar null si no se encuentra el asistente
    }

    // Método para listar todos los eventos disponibles con sus detalles
    public static void listaEventos(Connection con) {
        String sql = "SELECT id_evento, nombre_evento FROM eventos";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Iterar sobre los resultados y mostrar información del evento
            while (rs.next()) {
                int id_evento = rs.getInt("id_evento");
                String nameEvento = rs.getString("nombre_evento");
                String nameLocation = getLocationName(con, id_evento);
                System.out.printf("%s. %-10s - Espacios disponibles: %s\n", 
                    id_evento, nameEvento, espaciosDisponibles(con, nameLocation, id_evento));
            }
        } catch (SQLException e) {
            // Manejo de errores al ejecutar la consulta
            System.err.println(e.getMessage());
        }
    }

    // Método para obtener el nombre de la ubicación de un evento
    private static String getLocationName(Connection con, int id_evento) {
        String sql = "SELECT id_ubicacion FROM eventos where id_evento = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id_evento); // Establecer el parámetro de la consulta

            try (ResultSet rs = stmt.executeQuery()) {
                // Obtener el ID de la ubicación y llamar a un método externo para obtener su nombre
                if (rs.next()) {
                    int id_ubicacion = rs.getInt(1);
                    String[] array = Ejercicio1.getLocation(con, id_ubicacion);
                    return array[0];
                }
            }
        } catch (SQLException e) {
            // Manejo de errores al ejecutar la consulta
            System.err.println(e.getMessage());
        }
        return null; // Retornar null si no se encuentra la ubicación
    }

    // Método para calcular los espacios disponibles en un evento
    private static int espaciosDisponibles(Connection con, String ubi, int id_evento) {
        // Llama a métodos de Ejercicio1 y Ejercicio2 para calcular capacidad restante
        return Ejercicio2.getCapacidad(con, ubi) - Ejercicio1.getAsistentes(con, id_evento);
    }

    // Método para registrar a un asistente en un evento
    private static void registrarAsistente(Connection con, int id_evento, String dni) {
        String ubicacion = getLocationName(con, id_evento);

        // Verificar si hay espacios disponibles
        if (espaciosDisponibles(con, ubicacion, id_evento) > 0) {
            String sql = "INSERT INTO asistentes_eventos values (?,?)";

            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, dni); // Establecer DNI del asistente
                stmt.setInt(2, id_evento); // Establecer ID del evento

                int resultado = stmt.executeUpdate(); // Ejecutar la consulta

                // Verificar si la inserción fue exitosa
                if (resultado == 1) {
                    System.out.println(nombre + " ha sido registrado para el evento seleccionado.");
                } else {
                    System.out.println("No se ha podido registrar en el evento.");
                }
            } catch (SQLException e) {
                // Manejo de errores al ejecutar la consulta
                System.err.println(e.getMessage());
            }
        } else {
            // Mensaje si no hay espacios disponibles
            System.out.println("No hay espacio en el evento para registrarse.");
        }
    }
}

