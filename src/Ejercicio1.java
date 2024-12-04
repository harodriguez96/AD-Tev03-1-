// Importación de paquetes necesarios para la conexión a bases de datos, manejo de excepciones y utilidades generales
import java.sql.*;
import java.util.*;
import java.io.*;

public class Ejercicio1 {

    // Constantes para la conexión a la base de datos
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/dbeventos?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "eventos";
    private static final String PASSWORD = "eventos";

    public static void main(String[] args) {
        // Bloque try-with-resources para establecer la conexión a la base de datos y cerrarla automáticamente
        try (Connection con = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD)) {

            // Mensaje de confirmación de conexión exitosa
            System.out.println("Conectado");

            // Encabezados para la tabla que se mostrará en consola
            System.out.printf("%-30s | %-10s | %-35s | %-30s%n", "Nombre del Evento", "Asistentes", "Ubicación", "Dirección");
            System.out.println("-".repeat(100)); // Línea separadora

            // Consulta SQL para obtener todos los eventos
            String sql = "SELECT * FROM eventos";

            // Bloque try-with-resources para ejecutar la consulta y manejar el resultado
            try (Statement stmt = con.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                // Iterar sobre los resultados obtenidos de la consulta
                while (rs.next()) {
                    // Extraer información del evento actual
                    String eventName = rs.getString("nombre_evento");
                    int locationId = rs.getInt("id_ubicacion");

                    // Obtener información de la ubicación del evento
                    String[] array = getLocation(con, locationId);

                    // Obtener el número de asistentes al evento
                    int eventId = rs.getInt("id_evento");
                    int asistentes = getAsistentes(con, eventId);

                    // Imprimir los datos del evento formateados en la tabla
                    System.out.printf("%-30s | %-10s | %-35s | %-30s%n", eventName, asistentes, array[0], array[1]);
                }
            }
        } catch (SQLException e) {
            // Manejo de errores de conexión y ejecución de consultas
            System.err.println(e.getMessage());
        }
    }

    // Método para obtener información de la ubicación basado en el ID
    public static String[] getLocation(Connection con, int id_ubicacion) {
        String[] localArray = new String[2]; // Arreglo para almacenar nombre y dirección
        String sql = "SELECT nombre, direccion FROM ubicaciones where id_ubicacion=?";

        // Bloque try-with-resources para preparar y ejecutar la consulta
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id_ubicacion); // Establecer el parámetro en la consulta

            try (ResultSet rs = stmt.executeQuery()) {
                // Si se encuentra un resultado, asignar los valores al arreglo
                if (rs.next()) {
                    localArray[0] = rs.getString(1); // Nombre de la ubicación
                    localArray[1] = rs.getString(2); // Dirección de la ubicación
                }
            }
            return localArray; // Devolver el arreglo con la información
        } catch (SQLException e) {
            // Manejo de errores al ejecutar la consulta
            System.err.println(e.getMessage());
        }
        return null; // Devolver null en caso de error
    }

    // Método para obtener el número de asistentes a un evento basado en el ID del evento
    public static int getAsistentes(Connection con, int id_evento) {
        String sql = "SELECT count(dni) FROM asistentes_eventos where id_evento=?";
        
        // Bloque try-with-resources para preparar y ejecutar la consulta
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id_evento); // Establecer el parámetro en la consulta

            try (ResultSet rs = stmt.executeQuery()) {
                // Si se encuentra un resultado, devolver el número de asistentes
                if (rs.next()) {
                    return rs.getInt(1); // Número de asistentes
                }
            }
        } catch (SQLException e) {
            // Manejo de errores al ejecutar la consulta
            System.err.println(e.getMessage());
        }
        return -1; // Devolver -1 en caso de error
    }
}

