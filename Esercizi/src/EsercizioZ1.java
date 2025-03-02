import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    public static void main(String[] args) {
        Connection conn = connessioneDatabase();
        if (conn != null) {
            Scanner scanner = new Scanner(System.in);
            try {
                mostraMenuPrincipale(scanner, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connessione al database fallita.");
        }
    }

    // #region METODI PER IL CONTROLLO DEGLI INPUT
    // Metodo per controllare l'input intero
    public static Integer controlloInputInteri(Scanner scanner) {
        Integer valore;
        do {
            while (!scanner.hasNextInt()) {
                System.out.print("Devi inserire un numero intero. Riprova ");
                scanner.next();
            }
            valore = scanner.nextInt();
            if (valore < 0) {
                System.out.print("Il numero non può essere negativo. Riprova: ");
            }
        } while (valore < 0);
        return valore;
    }

    // Metodo per controllare l'input double, con possibilità di inserire null
    public static Double controlloInputDouble(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return null;
            }

            try {
                Double valore = Double.parseDouble(input);
                if (valore < 0) {
                    System.out.print("Il numero non può essere negativo. Riprova: ");
                } else {
                    return valore;
                }
            } catch (NumberFormatException e) {
                System.out.print("Devi inserire un numero decimale valido. Riprova: ");
            }
        }
    }

    // Metodo per controllare che l'input stringa non sia vuoto
    public static String controlloInputStringhe(Scanner scanner) {
        String valore;
        do {
            valore = scanner.nextLine().trim();
            if (valore.isEmpty()) {
                System.out.print("Input non valido. Inserisci un testo: ");
            }
        } while (valore.isEmpty());
        return valore;
    }

    // Metodo per controllare che l'input sulle date
    public static LocalDate controlloInputData(Scanner scanner) {
        LocalDate data = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        do {
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return null; // Se l'utente preme Enter, restituisce null
            }

            try {
                data = LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato data non valido. Inserisci la data nel formato YYYY-MM-DD.");
            }
        } while (data == null);

        return data;
    }

    // #endregion

    // #region METODI PER LA GESTIONE DELLE MODIFICHE E VISUALIZZAZIONE SUL DB

    // #region CONTROLLO SULLE SPECIE

    public static int selezionaSpecie(Connection conn, Scanner scanner) {
        int idSpecie = -1;
        boolean idValido = false;

        while (!idValido) {
            // Stampa tutte le specie disponibili
            System.out.println("\nSpecie disponibili:");
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT ID, name FROM specie");
                    ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    System.out.println(rs.getInt("ID") + " - " + rs.getString("name"));
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return -1; // Errore nel recuperare le specie
            }

            // Chiede l'ID all'utente
            System.out.print("Inserisci l'ID della specie: ");
            if (scanner.hasNextInt()) {
                idSpecie = scanner.nextInt();
                scanner.nextLine(); // Consuma il newline

                // Verifica se l'ID esiste nel database
                if (verificaIdSpecie(conn, idSpecie)) {
                    idValido = true;
                } else {
                    System.out.println("ID non valido. Riprova.");
                }
            } else {
                System.out.println("Devi inserire un numero intero. Riprova.");
                scanner.next(); // Consuma l'input errato
            }
        }

        return idSpecie;
    }

    public static boolean verificaIdSpecie(Connection conn, int idSpecie) {
        String query = "SELECT COUNT(*) FROM specie WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idSpecie);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // #endregion

    // #region CONTROLLO SUI NIDI

    public static int selezionaNido(Connection conn, Scanner scanner) {
        int idNido = -1;
        boolean idValido = false;

        while (!idValido) {
            // Stampa tutti i nidi disponibili
            System.out.println("\nNidi disponibili:");
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT ID, name FROM nidi");
                    ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    System.out.println(rs.getInt("ID") + " - " + rs.getString("name"));
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return -1; // Errore nel recuperare i nidi
            }

            // Chiede l'ID all'utente
            System.out.print("Inserisci l'ID del nido: ");
            if (scanner.hasNextInt()) {
                idNido = scanner.nextInt();
                scanner.nextLine(); // Consuma il newline

                // Verifica se l'ID esiste nel database
                if (verificaIdNido(conn, idNido)) {
                    idValido = true;
                } else {
                    System.out.println("ID non valido. Riprova.");
                }
            } else {
                System.out.println("Devi inserire un numero intero. Riprova.");
                scanner.next(); // Consuma l'input errato
            }
        }

        return idNido;
    }

    public static boolean verificaIdNido(Connection conn, int idNido) {
        String query = "SELECT COUNT(*) FROM nidi WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idNido);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // #endregion

    // #region CONTROLLO SUI PICCIONI

    public static int selezionaPiccione(Connection conn, Scanner scanner) {
        int idPiccione = -1;
        boolean idValido = false;

        while (!idValido) {
            // Stampa tutti i piccioni disponibili
            System.out.println("\nPiccioni disponibili:");
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT ID, Name FROM piccioni");
                    ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    System.out.println(rs.getInt("ID") + " - " + rs.getString("Name"));
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return -1; // Errore nel recuperare i piccioni
            }

            // Chiede l'ID all'utente
            System.out.print("Inserisci l'ID del piccione da aggiornare: ");
            if (scanner.hasNextInt()) {
                idPiccione = scanner.nextInt();
                scanner.nextLine(); // Consuma il newline

                // Verifica se l'ID esiste nel database
                if (verificaIdPiccione(conn, idPiccione)) {
                    idValido = true;
                } else {
                    System.out.println("ID non valido. Riprova.");
                }
            } else {
                System.out.println("Devi inserire un numero intero. Riprova.");
                scanner.next(); // Consuma l'input errato
            }
        }

        return idPiccione;
    }

    public static boolean verificaIdPiccione(Connection conn, int idPiccione) {
        String query = "SELECT COUNT(*) FROM piccioni WHERE ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idPiccione);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // #endregion

    // Metodo per aggiungere un piccione
    public static void aggiungiPiccione(Connection conn, Scanner scanner) {
        try {
            System.out.println("\n=== Aggiungi un nuovo Piccione ===");
            // Chiede i dati all'utente
            System.out.print("Inserisci l'ID della specie: ");
            int idSpecie = selezionaSpecie(conn, scanner);

            System.out.print("Inserisci l'ID del nido: ");
            int idNest = selezionaNido(conn, scanner);

            System.out.print("Inserisci il nome del piccione: ");
            String name = controlloInputStringhe(scanner);

            System.out.print("Inserisci la data di nascita (YYYY-MM-DD) oppure premi Enter per lasciare null: ");
            LocalDate birthDate = controlloInputData(scanner);

            System.out.print("Inserisci il peso oppure premi Enter per lasciare null: ");
            Double weight = controlloInputDouble(scanner); // Il metodo già gestisce null

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

    // Metodo per aggiornare un piccione
    public static void aggiornaPiccione(Connection conn, Scanner scanner) throws SQLException {
        String query = "UPDATE piccioni SET idnest = ?, name = ?, birthdate = ?, weight = ? WHERE id = ?";

        System.out.print("Inserisci l'id del piccione da aggiornare: ");
        int id = selezionaPiccione(conn, scanner);
        scanner.nextLine();
        System.out.println("ID  " + id);

        System.out.print("Inserisci il nuovo id del nido: ");
        int idNest = selezionaNido(conn, scanner);

        System.out.print("Inserisci il nuovo nome del piccione: ");
        String name = scanner.nextLine();

        System.out.println(
                "Inserisci la nuova data di nascita del piccione: (YYYY-MM-DD) oppure premi Enter per lasciare null: ");
        LocalDate birthDate = controlloInputData(scanner);

        System.out.println("Inserisci il nuovo peso del piccione: ");
        Double weight = controlloInputDouble(scanner); // Il metodo già gestisce null
        scanner.nextLine();

        try {
            PreparedStatement pstmt = createPreparedStatement(conn, query);

            pstmt.setInt(1, idNest);
            pstmt.setString(2, name);

            if (birthDate != null) {
                pstmt.setDate(3, java.sql.Date.valueOf(birthDate));
            } else {
                pstmt.setNull(3, java.sql.Types.DATE);
            }

            if (weight != null) {
                pstmt.setDouble(4, weight);
            } else {
                pstmt.setNull(4, java.sql.Types.DOUBLE);
            }
            pstmt.setInt(5, id);

            // Eseguiamo l'aggiornamento
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Aggiornamento riuscito!");
            } else {
                System.out.println("Aggiornamento fallito!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodo per cancellare un piccione
    private static void cancellaPiccione(Connection connection, Scanner scanner) throws SQLException {
        // Chiede all'utente quale piccione eliminare
        System.out.println("Inserisci id piccione da eliminare");
        int idPiccione = selezionaPiccione(connection, scanner);

        // Query per cancellare il piccione con l'ID selezionato
        String query = "DELETE FROM piccioni WHERE ID = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idPiccione);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Piccione eliminato con successo!");
            } else {
                System.out.println("Errore: Nessun piccione trovato con ID " + idPiccione);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Mostra la lista aggiornata dei piccioni
        mostraPiccioni(connection);
    }

    // Metodo per stampare tutti i piccioni rimasti
    private static void mostraPiccioni(Connection connection) throws SQLException {
        String query = "SELECT * FROM piccioni";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();

            // Stampa intestazioni delle colonne
            System.out.println(String.format("%-10s %-15s %-10s %-20s %-20s %-10s",
                    metaData.getColumnName(1),
                    metaData.getColumnName(2),
                    metaData.getColumnName(3),
                    metaData.getColumnName(4),
                    metaData.getColumnName(5),
                    metaData.getColumnName(6)));

            System.out.println("========================================================");

            // Stampa ogni riga della tabella
            while (rs.next()) {
                System.out.println(String.format("%-10s %-15s %-10s %-20s %-20s %-10s",
                        rs.getString("ID"),
                        rs.getString("IDSpecie"),
                        rs.getString("IDNest"),
                        rs.getString("Name"),
                        rs.getString("BirthDate"),
                        rs.getString("Weight")));
            }
        }
    }

    // Metodo per mostrare tutte le specie
    private static void mostraSpecie(Connection connection) throws SQLException {
        String query = "SELECT * FROM specie";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();

            // Stampa intestazioni delle colonne
            System.out.println(String.format("%-10s %-25s %-45s %-45s",
                    metaData.getColumnName(1),
                    metaData.getColumnName(2),
                    metaData.getColumnName(3),
                    metaData.getColumnName(4)));

            System.out.println("==========================================================");

            // Stampa ogni riga della tabella
            while (rs.next()) {
                System.out.println(String.format("%-10s %-25s %-45s %-45s",
                        rs.getString("ID"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("lore")));
            }
        }
    }

    // Metodo per mostrare tutti i nidi
    private static void mostraNidi(Connection connection) throws SQLException {
        String query = "SELECT * FROM nidi";

        try (PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();

            // Stampa intestazioni delle colonne
            System.out.println(String.format("%-10s %-20s %-10s %-10s",
                    metaData.getColumnName(1),
                    metaData.getColumnName(2),
                    metaData.getColumnName(3),
                    metaData.getColumnName(4)));

            System.out.println("==========================================================");

            // Stampa ogni riga della tabella
            while (rs.next()) {
                System.out.println(String.format("%-10s %-20s %-10s %-10s",
                        rs.getString("ID"),
                        rs.getString("name"),
                        rs.getString("dimension"),
                        rs.getString("material")));
            }
        }
    }

    // Creare un Trigger per la tabella Piccioni
    public static void creaTrigger(Connection conn) throws SQLException {
        // Controllo se i trigger esistono già
        if (!esisteTrigger(conn, "after_update_pigeon")) {
            String triggerUpdate = "CREATE TRIGGER after_update_pigeon AFTER UPDATE ON piccioni " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "    IF NEW.Name != OLD.Name AND EXISTS (SELECT 1 FROM piccioni WHERE Name = NEW.Name) THEN " +
                    "        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Errore: Il nome deve essere univoco!'; " +
                    "    END IF; " +
                    "    INSERT INTO piccioni_log (ID, idnest, Name, birthdate, weight, creationDate, motivo) " +
                    "    VALUES (OLD.ID, OLD.idnest, OLD.Name, OLD.birthdate, OLD.weight, NOW(), 'Aggiornamento di un piccione'); "
                    +
                    "    INSERT INTO piccioni_log (ID, idnest, Name, birthdate, weight, creationDate, motivo) " +
                    "    VALUES (NEW.ID, NEW.idnest, NEW.Name, NEW.birthdate, NEW.weight, NOW(), 'Aggiornamento di un piccione'); "
                    +
                    "END;";

            PreparedStatement pstmt = createPreparedStatement(conn, triggerUpdate);
            pstmt.executeUpdate();
            System.out.println("Trigger di aggiornamento creato con successo.");
        }

        if (!esisteTrigger(conn, "before_pigeon_delete")) {
            String triggerDelete = "CREATE TRIGGER before_pigeon_delete BEFORE DELETE ON piccioni " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "    INSERT INTO piccioni_log (ID, idnest, Name, birthdate, weight, creationDate, motivo) " +
                    "    VALUES (OLD.ID, OLD.idnest, OLD.Name, OLD.birthdate, OLD.weight, NOW(), 'Cancellazione di un piccione'); "
                    +
                    "END;";

            PreparedStatement pstmt = createPreparedStatement(conn, triggerDelete);
            pstmt.executeUpdate();
            System.out.println("Trigger di cancellazione creato con successo.");
        }

        if (!esisteTrigger(conn, "before_pigeon_insert")) {
            String triggerInsert = "CREATE TRIGGER before_pigeon_insert BEFORE INSERT ON piccioni " +
                    "FOR EACH ROW " +
                    "BEGIN " +
                    "    IF EXISTS (SELECT 1 FROM piccioni WHERE Name = NEW.Name) THEN " +
                    "        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Errore: Il nome deve essere univoco!'; " +
                    "    END IF; " +
                    "END;";

            PreparedStatement pstmt = createPreparedStatement(conn, triggerInsert);
            pstmt.executeUpdate();
            System.out.println("Trigger di inserimento creato con successo.");
        }
    }

    // Metodo per verificare se un trigger esiste già nel database
    public static boolean esisteTrigger(Connection conn, String triggerName) throws SQLException {
        String query = "SELECT COUNT(*) FROM information_schema.TRIGGERS WHERE TRIGGER_NAME = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, triggerName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Restituisce true se il trigger esiste
                }
            }
        }
        return false; // Se non esiste
    }

    // #endregion

    // #region METODI DEI MENU
    // Metodo per il menu principale
    public static void mostraMenuPrincipale(Scanner scanner, Connection conn) throws SQLException {
        creaTrigger(conn);
        int scelta;
        boolean exitMainMenu = false;
        while (!exitMainMenu) {
            System.out.println("\n==== BENVENUTO NEL NOSTRO NEGOZIO DI PICCIONI ====");
            System.out.println("1. Aggiungi Piccioni");
            System.out.println("2. Cancella uin Piccione");
            System.out.println("3. Modifica un piccione");
            System.out.println("4. Mostra specie");
            System.out.println("5. Mostra nidi");
            System.out.println("6. Mostra piccioni");
            System.out.println("7. esci");
            System.out.print("Scegli un'opzione (1-4): ");
            scelta = controlloInputInteri(scanner);
            scanner.nextLine();

            switch (scelta) {
                case 1:
                    aggiungiPiccione(conn, scanner);
                    break;
                case 2:
                    cancellaPiccione(conn, scanner);
                    break;
                case 3:
                    aggiornaPiccione(conn, scanner);
                    break;
                case 4:
                    mostraSpecie(conn);
                    break;
                case 5:
                    mostraNidi(conn);
                    break;
                case 6:
                    mostraPiccioni(conn);
                    break;
                case 7:
                    System.out.println("CIAOOOO CRA CRA");
                    exitMainMenu = true;
                    break;
                default:
                    System.out.println("Opzione non valida! Riprova.");
            }
        }

    }
    // #endregion
}
