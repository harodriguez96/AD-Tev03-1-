// Importación de paquetes necesarios para trabajar con SQL, entrada de datos y utilidades generales
import java.util.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Ejercicio2 {

    // Constantes para la conexión a la base de datos
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/dbeventos?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "eventos";
    private static final String PASSWORD = "eventos";

    public static void main(String[] args) {
        // Bloque try-with-resources para conectar con la base de datos y asegurarse de que se cierre correctamente
        try (Connection con = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD)) {

            // Crear un objeto Scanner para capturar entrada del usuario
            Scanner teclado = new Scanner(System.in);

            // Pedir al usuario que introduzca el nombre de una ubicación
            System.out.println("Introduce el nombre de la ubicacion:");
            String ubicacion = teclado.nextLine();

            // Llamar al método para obtener la capacidad actual de la ubicación
            int capacidad = getCapacidad(con, ubicacion);

            // Mostrar la capacidad actual al usuario
            System.out.println("La capacidad actual de la ubicacion " + ubicacion + " es: " + capacidad);

            // Pedir al usuario que introduzca la nueva capacidad máxima
            System.out.println("Introduce la nueva capacidad maxima:");
            int capacidadMAX = teclado.nextInt();

            // Llamar al método para actualizar la capacidad en la base de datos
            setCapacidad(con, capacidadMAX, ubicacion);

        } catch (SQLException e) {
            // Manejo de errores relacionados con SQL
            System.err.println(e.getMessage());
        }
    }

    // Método para obtener la capacidad actual de una ubicación específica
    public static int getCapacidad(Connection con, String ubi) {
        // Consulta SQL para obtener la capacidad de una ubicación según su nombre
        String sql = "SELECT capacidad FROM ubicaciones where nombre=?";

        // Bloque try-with-resources para preparar y ejecutar la consulta
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, ubi); // Establecer el parámetro para la consulta
            try (ResultSet rs = stmt.executeQuery()) {
                // Recorrer los resultados de la consulta (debería ser solo uno)
                while (rs.next()) {
                    int capacidad = rs.getInt(1); // Obtener la capacidad
                    return capacidad; // Devolver la capacidad encontrada
                }
            }
        } catch (SQLException e) {
            // Manejo de errores de SQL
            System.err.println(e.getMessage());
        }
        return -1; // Devolver -1 en caso de error o si no se encuentra la ubicación
    }

    // Método para actualizar la capacidad máxima de una ubicación específica
    private static void setCapacidad(Connection con, int capacidad, String ubi) {
        // Consulta SQL para actualizar la capacidad de una ubicación según su nombre
        String sql = "UPDATE ubicaciones SET capacidad = ? where nombre=?";

        // Bloque try-with-resources para preparar y ejecutar la consulta
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, capacidad); // Establecer el nuevo valor de capacidad
            stmt.setString(2, ubi);   // Especificar el nombre de la ubicación

            // Ejecutar la consulta de actualización
            int resultado = stmt.executeUpdate();

            // Comprobar si la actualización fue exitosa
            if (resultado == 1) {
                System.out.println("Capacidad actualizada correctamente.");
                getCapacidad(con, ubi); // Opcional: Llamar al método para verificar la capacidad actualizada
            } else {
                System.out.println("No se ha podido actualizar correctamente la capacidad.");
            }
        } catch (SQLException e) {
            // Manejo de errores de SQL
            System.err.println(e.getMessage());
        }
    }
}


