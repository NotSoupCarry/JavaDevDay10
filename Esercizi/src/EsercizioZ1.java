import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class EsercizioZ1 {

    // #region CONSTANTI
    // Costanti dati di connessione
    private static final String DB_URL = "jdbc:mysql://localhost:3306/piccionaia";
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

    // Metodo per la creazione degli statement scrollable e updatable
    public static PreparedStatement createPreparedStatement(Connection conn, String query) {
        try {
            PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            return pstmt;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //#region METODI PER LA GESTIONE DELLE MODIFICHE E VISUALIZZAZIONE SUL DB  
    public static void aggiungiPiccione(Connection conn, Scanner scanner) {
        try {
            System.out.println("\n=== Aggiungi un nuovo Piccione ===");
            // Chiede i dati all'utente
            System.out.print("Inserisci l'ID della specie: ");
            int idSpecie = scanner.nextInt();
            scanner.nextLine(); 
    
            System.out.print("Inserisci l'ID del nido: ");
            int idNest = scanner.nextInt();
            scanner.nextLine();
    
            System.out.print("Inserisci il nome del piccione: ");
            String name = scanner.nextLine();
    
            System.out.print("Inserisci la data di nascita (YYYY-MM-DD) oppure premi Enter per lasciare null: ");
            String birthDate = scanner.nextLine();
            if (birthDate.isEmpty()) birthDate = null;
    
            System.out.print("Inserisci il peso oppure premi Enter per lasciare null: ");
            String pesoInput = scanner.nextLine();
            Double weight = pesoInput.isEmpty() ? null : Double.parseDouble(pesoInput);
    
            // Query SQL per l'inserimento
            String sql = "INSERT INTO piccioni (IDSpecie, IDNest, Name, BirthDate, Weight) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, idSpecie);
                pstmt.setInt(2, idNest);
                pstmt.setString(3, name);
    
                if (birthDate != null) {
                    pstmt.setDate(4, java.sql.Date.valueOf(birthDate));
                } else {
                    pstmt.setNull(4, java.sql.Types.DATE);
                }
    
                if (weight != null) {
                    pstmt.setDouble(5, weight);
                } else {
                    pstmt.setNull(5, java.sql.Types.DOUBLE);
                }
    
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Piccione aggiunto con successo!");
                } else {
                    System.out.println("Errore nell'aggiunta del piccione.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    //#endregion
    
    // #region METODI DEI MENU
    // Metodo per il menu principale
    public static void mostraMenuPrincipale(Scanner scanner, Connection conn) throws SQLException {
        int scelta;
        boolean exitMainMenu = false;
        while (!exitMainMenu) {
            System.out.println("\n==== BENVENUTO NEL NOSTRO NEGOZIO DI PICCIONI ====");
            System.out.println("1. Aggiungi Piccioni");
            System.out.println("2. Cancella uin Piccione");
            System.out.println("3. Modifica un piccione");
            System.out.println("4. Sotto menù dei Trigger");
            System.out.print("Scegli un'opzione (1-4): ");
            scelta = controlloInputInteri(scanner);
            scanner.nextLine();

            switch (scelta) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    mostraSottoMenuTriggers(conn, scanner);
                    break;
                case 5:
                    System.out.println("CIAOOOO");
                    exitMainMenu = true;
                    break;
                default:
                    System.out.println("Opzione non valida! Riprova.");
            }
        }

    }
    // #endregion
}
