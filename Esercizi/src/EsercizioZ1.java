import java.sql.Connection;
import java.sql.DriverManager;

public class EsercizioZ1 {

    // #region CONSTANTI
    // Costanti dati di connessione
    private static final String DB_URL = "jdbc:mysql://localhost:3306/TeamPiccione";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";
    // #endregion

    // Metodo per la connessione
    public static Connection connessioneDatabase() {
        Connection conn = null;

        try {
            // Carica il driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Crea la connessione
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            // Il metodo getCatalog() ritorna il nome dello schema a cui si è collegati
            String databaseName = conn.getCatalog();
            System.out.println("Connessione riuscita al database: " + databaseName);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
    }
}
