import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.CallableStatement;

public class Ejercicio4 {

	private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/dbeventos?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "eventos";
    private static final String PASSWORD = "eventos";

    public static void main(String[] args) {
		
		try (Connection con = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD)){
			
			Scanner teclado = new Scanner(System.in);
			Ejercicio3.listaEventos(con);
			System.out.println("Introduce el ID del evento para consultar la cantidad de asistentes:");
			int id_evento=teclado.nextInt();
			
			String sql="SELECT obtener_numero_asistentes(?) AS num_asistentes";
			try (PreparedStatement stmt=con.prepareStatement(sql)) {
	    		stmt.setInt(1,id_evento);
	    		try(ResultSet rs = stmt.executeQuery()){
		    		while(rs.next()) {
						int asistentes = rs.getInt(1);
						System.out.println("El numero de asistentes en el evento seleccionado es: " +asistentes);
					}
		    	}
			}  catch (SQLException e) {
	            System.err.println(e.getMessage());
	        }
			
			
				
		} catch (SQLException e) {
            System.err.println(e.getMessage());
        }
	}
}
