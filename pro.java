
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AirplaneFX extends Application {

    private static final String PROJECT_DIR = "C://javaproject";
    private static final String HOSTESS_FILE = PROJECT_DIR + "//hostesses.txt";
    private static final String PLANE_FILE = PROJECT_DIR + "//planes.txt";
    private static final String ADMIN_PASSWORD = "ET-123Alemayehu";
    private static final String HOSTESS_PASSWORD = "ET-HostessSchedule";

    private Stage primaryStage;
    private Scene mainScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        ensureDirectoryExists(PROJECT_DIR);
        setupMainMenu();
        primaryStage.setTitle("Airplane Management System");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void ensureDirectoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() && !directory.mkdirs()) {
            showAlert(Alert.AlertType.ERROR, "Directory Creation Failed", 
                     "Failed to create project directory!");
        }
    }

    private void setupMainMenu() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Button adminBtn = new Button("Admin Menu");
        adminBtn.setPrefWidth(200);
        adminBtn.setOnAction(e -> showPasswordDialog(true));

        Button hostessBtn = new Button("Hostess Menu");
        hostessBtn.setPrefWidth(200);
        hostessBtn.setOnAction(e -> showPasswordDialog(false));

        Button exitBtn = new Button("Exit");
        exitBtn.setPrefWidth(200);
        exitBtn.setOnAction(e -> Platform.exit());

        layout.getChildren().addAll(adminBtn, hostessBtn, exitBtn);
        mainScene = new Scene(layout, 400, 300);
    }

    private void showPasswordDialog(boolean isAdmin) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Authentication");
        dialog.setHeaderText("Enter " + (isAdmin ? "Admin" : "Hostess") + " Password:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(password -> {
            if ((isAdmin && password.equals(ADMIN_PASSWORD)) {
                showAdminMenu();
            } else if (!isAdmin && password.equals(HOSTESS_PASSWORD)) {
                showHostessMenu();
            } else {
                showAlert(Alert.AlertType.ERROR, "Access Denied", "Incorrect password!");
            }
        });
    }

    private void showAdminMenu() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        String[] options = {
            "Add Hostess", "Delete Hostess", "Add Plane",
            "Delete Plane", "View All Hostesses", "View All Planes", "Back"
        };

        for (String option : options) {
            Button btn = new Button(option);
            btn.setPrefWidth(200);
            btn.setOnAction(e -> handleAdminChoice(option));
            layout.getChildren().add(btn);
        }

        Button backBtn = new Button("Back to Main Menu");
        backBtn.setOnAction(e -> primaryStage.setScene(mainScene));
        layout.getChildren().add(backBtn);

        ScrollPane scrollPane = new ScrollPane(layout);
        primaryStage.setScene(new Scene(scrollPane, 400, 400));
    }

Redi, [5/19/2025 9:45 AM]
private void handleAdminChoice(String choice) {
        switch (choice) {
            case "Add Hostess":
                HostessManagement.addHostess(primaryStage);
                break;
            case "Delete Hostess":
                HostessManagement.deleteHostess(primaryStage);
                break;
            case "Add Plane":
                addPlane();
                break;
            case "Delete Plane":
                deletePlane();
                break;
            case "View All Hostesses":
                HostessManagement.viewAllHostesses(primaryStage);
                break;
            case "View All Planes":
                viewAllPlanes();
                break;
        }
    }

    private void showHostessMenu() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Button viewProfileBtn = new Button("View Profile");
        viewProfileBtn.setOnAction(e -> HostessManagement.viewProfile(primaryStage));

        Button viewScheduleBtn = new Button("View Schedule");
        viewScheduleBtn.setOnAction(e -> HostessManagement.viewSchedule(primaryStage));

        Button backBtn = new Button("Back to Main Menu");
        backBtn.setOnAction(e -> primaryStage.setScene(mainScene));

        layout.getChildren().addAll(viewProfileBtn, viewScheduleBtn, backBtn);
        primaryStage.setScene(new Scene(layout, 300, 200));
    }

    // Plane Management Methods
    private void addPlane() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField depTimeField = new TextField();
        TextField depCityField = new TextField();
        TextField destCityField = new TextField();
        TextField finalTimeField = new TextField();

        grid.addRow(0, new Label("Plane ID:"), idField);
        grid.addRow(1, new Label("Name:"), nameField);
        grid.addRow(2, new Label("Departure Time:"), depTimeField);
        grid.addRow(3, new Label("Departure City:"), depCityField);
        grid.addRow(4, new Label("Destination City:"), destCityField);
        grid.addRow(5, new Label("Final Time:"), finalTimeField);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Plane");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Plane plane = new Plane(
                    idField.getText(),
                    nameField.getText(),
                    Float.parseFloat(depTimeField.getText()),
                    depCityField.getText(),
                    destCityField.getText(),
                    Float.parseFloat(finalTimeField.getText())
                );
                savePlane(plane);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Plane added successfully!");
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number format!");
            }
        }
    }

    private void deletePlane() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Delete Plane");
        dialog.setHeaderText("Enter Plane ID to delete:");
        
        dialog.showAndWait().ifPresent(planeId -> {
            List<Plane> planes = loadPlanes();
            if (planes.removeIf(plane -> plane.planeid.equals(planeId))) {
                savePlanes(planes);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Plane deleted successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Plane not found!");
            }
        });
    }


private void viewAllPlanes() {
        List<Plane> planes = loadPlanes();
        ListView<String> listView = new ListView<>();
        for (Plane plane : planes) {
            listView.getItems().add(plane.toString());
        }

        Stage stage = new Stage();
        stage.setScene(new Scene(listView, 400, 300));
        stage.setTitle("All Planes");
        stage.show();
    }

    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void savePlane(Plane plane) {
        List<Plane> planes = loadPlanes();
        planes.add(plane);
        savePlanes(planes);
    }

    private static void savePlanes(List<Plane> planes) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PLANE_FILE))) {
            planes.forEach(p -> writer.println(p.planeid + "," + p.name + "," + p.departureTime + "," 
                + p.departureCity + "," + p.destinationCity + "," + p.finalTime));
        } catch (IOException e) {
            new AirplaneFX().showAlert(Alert.AlertType.ERROR, "File Error", "Error saving planes!");
        }
    }

    private static List<Plane> loadPlanes() {
        List<Plane> planes = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(PLANE_FILE))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length == 6) {
                    planes.add(new Plane(
                        parts[0], parts[1], Float.parseFloat(parts[2]),
                        parts[3], parts[4], Float.parseFloat(parts[5])
                    ));
                }
            }
        } catch (FileNotFoundException e) {
            // File not created yet
        }
        return planes;
    }

    static class HostessManagement {
        static void addHostess(Stage owner) {
           
        }

        static void deleteHostess(Stage owner) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Delete Hostess");
            dialog.setHeaderText("Enter Hostess ID:");
            dialog.showAndWait().ifPresent(id -> {
                try {
                    List<Hostess> hostesses = loadHostesses();
                    if (hostesses.removeIf(h -> h.id == Integer.parseInt(id))) {
                        saveHostesses(hostesses);
                        new AirplaneFX().showAlert(Alert.AlertType.INFORMATION, "Success", "Hostess deleted!");
                    } else {
                        new AirplaneFX().showAlert(Alert.AlertType.ERROR, "Error", "Hostess not found!");
                    }
                } catch (NumberFormatException e) {
                    new AirplaneFX().showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID format!");
                }
            });
        }

        static void viewAllHostesses(Stage owner) {
            List<Hostess> hostesses = loadHostesses();
            ListView<String> listView = new ListView<>();
            hostesses.forEach(h -> listView.getItems().add(h.toString()));

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.setScene(new Scene(listView, 500, 400));
            stage.setTitle("All Hostesses");
            stage.show();
        }


static void viewProfile(Stage owner) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("View Profile");
            dialog.setHeaderText("Enter Hostess ID:");
            dialog.showAndWait().ifPresent(id -> {
                try {
                    loadHostesses().stream()
                        .filter(h -> h.id == Integer.parseInt(id))
                        .findFirst()
                        .ifPresentOrElse(
                            h -> showHostessDialog(h),
                            () -> new AirplaneFX().showAlert(Alert.AlertType.ERROR, "Not Found", "Hostess not found!")
                        );
                } catch (NumberFormatException e) {
                    new AirplaneFX().showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID format!");
                }
            });
        }

        static void viewSchedule(Stage owner) {
            new AirplaneFX().showAlert(Alert.AlertType.INFORMATION, "Schedule", "Schedule not implemented yet");
        }

        private static void showHostessDialog(Hostess hostess) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hostess Profile");
            alert.setHeaderText("Hostess Details");
            alert.setContentText(hostess.toString());
            alert.showAndWait();
        }

   
        private static List<Hostess> loadHostesses() {
            List<Hostess> hostesses = new ArrayList<>();
            try (Scanner scanner = new Scanner(new File(HOSTESS_FILE))) {
                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(",");
                    if (parts.length == 8) {
                        hostesses.add(new Hostess(
                            Integer.parseInt(parts[0]), parts[1], parts[2],
                            Integer.parseInt(parts[3]), Float.parseFloat(parts[4]),
                            parts[5], Float.parseFloat(parts[6]), parts[7]
                        ));
                    }
                }
            } catch (FileNotFoundException e) {
               
            }
            return hostesses;
        }

        private static void saveHostesses(List<Hostess> hostesses) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(HOSTESS_FILE))) {
                hostesses.forEach(h -> writer.println(
                    h.id + "," + h.name + "," + h.sex + "," + h.age + "," +
                    h.height + "," + h.status + "," + h.GPA + "," + h.level
                ));
            } catch (IOException e) {
                new AirplaneFX().showAlert(Alert.AlertType.ERROR, "File Error", "Error saving hostesses!");
            }
        }
    }

    static class Hostess {
       
    }

    static class Plane {
      
    }
}




    private void initializeDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Create hostesses table
            String createHostessesTable = "CREATE TABLE IF NOT EXISTS hostesses (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "sex TEXT NOT NULL, " +
                    "age INTEGER NOT NULL, " +
                    "height REAL NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "gpa REAL NOT NULL, " +
                    "level TEXT NOT NULL)";
            
            // Create planes table
            String createPlanesTable = "CREATE TABLE IF NOT EXISTS planes (" +
                    "planeid TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "departureTime REAL NOT NULL, " +
                    "departureCity TEXT NOT NULL, " +
                    "destinationCity TEXT NOT NULL, " +
                    "finalTime REAL NOT NULL)";

            stmt.execute(createHostessesTable);
            stmt.execute(createPlanesTable);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void setupUI() {
        setTitle("Airplane Management System");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        adminButton = new JButton("Admin Menu");
        hostessButton = new JButton("Hostess Menu");
        exitButton = new JButton("Exit");

        adminButton.addActionListener(this::adminMenu);
        hostessButton.addActionListener(this::hostessMenu);
        exitButton.addActionListener(e -> System.exit(0));

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(adminButton);
        panel.add(hostessButton);
        panel.add(exitButton);

        add(panel);
        setVisible(true);
    }

    private void adminMenu(ActionEvent e) {
        String password = JOptionPane.showInputDialog(this, "Welcome Sir, Enter Your Password:");
        if (!ADMIN_PASSWORD.equals(password)) {
            JOptionPane.showMessageDialog(this, "Incorrect password. Access denied.");
            return;
        }

        String[] options = {"Add Hostess", "Delete Hostess", "Add Plane", "Delete Plane", 
                           "View All Hostesses", "View All Planes", "Back to Main Menu"};
        
        while (true) {
            int choice = JOptionPane.showOptionDialog(this, "Admin Menu", "Admin Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0: HostessManagement.addHostess(this); break;
                case 1: HostessManagement.deleteHostess(this); break;
                case 2: addPlane(); break;
                case 3: deletePlane(); break;
                case 4: HostessManagement.viewAllHostesses(this); break;
                case 5: viewAllPlanes(); break;
                case 6: return;
                default: break;
            }
        }
    }

Redi, [5/19/2025 10:02 AM]
private void hostessMenu(ActionEvent e) {
        String password = JOptionPane.showInputDialog(this, "Welcome, Enter Your Password:");
        if (!HOSTESS_PASSWORD.equals(password)) {
            JOptionPane.showMessageDialog(this, "Incorrect password. Access denied.");
            return;
        }

        String[] options = {"View Profile", "View Schedule", "Back to Main Menu"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(this, "Hostess Menu", "Hostess Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0: HostessManagement.viewProfile(this); break;
                case 1: HostessManagement.viewSchedule(this); break;
                case 2: return;
                default: break;
            }
        }
    }

    // Plane Management Methods
    private void addPlane() {
        String planeId = JOptionPane.showInputDialog(this, "Enter Plane ID:");
        String name = JOptionPane.showInputDialog(this, "Enter Plane Name:");
        float departureTime = Float.parseFloat(JOptionPane.showInputDialog(this, "Enter Departure Time:"));
        String departureCity = JOptionPane.showInputDialog(this, "Enter Departure City:");
        String destinationCity = JOptionPane.showInputDialog(this, "Enter Destination City:");
        float finalTime = Float.parseFloat(JOptionPane.showInputDialog(this, "Enter Final Time:"));

        String sql = "INSERT INTO planes(planeid, name, departureTime, departureCity, destinationCity, finalTime) " +
                     "VALUES(?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, planeId);
            pstmt.setString(2, name);
            pstmt.setFloat(3, departureTime);
            pstmt.setString(4, departureCity);
            pstmt.setString(5, destinationCity);
            pstmt.setFloat(6, finalTime);
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Plane added successfully.");
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void deletePlane() {
        String planeId = JOptionPane.showInputDialog(this, "Enter Plane ID to delete:");
        
        String sql = "DELETE FROM planes WHERE planeid = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, planeId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Plane deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Plane not found.");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void viewAllPlanes() {
        String sql = "SELECT * FROM planes";
        StringBuilder sb = new StringBuilder();

Redi, [5/19/2025 10:02 AM]
try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                sb.append("ID: ").append(rs.getString("planeid"))
                  .append(", Name: ").append(rs.getString("name"))
                  .append(", Departure: ").append(rs.getFloat("departureTime"))
                  .append(", From: ").append(rs.getString("departureCity"))
                  .append(", To: ").append(rs.getString("destinationCity"))
                  .append(", Arrival: ").append(rs.getFloat("finalTime"))
                  .append("\n");
            }
            
            JOptionPane.showMessageDialog(this, sb.toString());
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    static class HostessManagement {
        public static void addHostess(JFrame parent) {
            int id = Integer.parseInt(JOptionPane.showInputDialog(parent, "Enter Hostess ID:"));
            String name = JOptionPane.showInputDialog(parent, "Enter Hostess Name:");
            String sex = JOptionPane.showInputDialog(parent, "Enter Hostess Sex (Male/Female):");
            int age = Integer.parseInt(JOptionPane.showInputDialog(parent, "Enter Hostess Age:"));
            float height = Float.parseFloat(JOptionPane.showInputDialog(parent, "Enter Hostess Height (in meters):"));
            String status = JOptionPane.showInputDialog(parent, "Enter Hostess Status:");
            float gpa = Float.parseFloat(JOptionPane.showInputDialog(parent, "Enter Hostess GPA:"));
            String level = JOptionPane.showInputDialog(parent, "Enter Hostess Level:");

            // Validation checks
            if ((sex.equalsIgnoreCase("Female") && height < 1.50f) 
                (sex.equalsIgnoreCase("Male") && height < 1.60f)) {
                JOptionPane.showMessageDialog(parent, "Height requirement not met!");
                return;
            }
            if (age <= 19  gpa <= 3.0f) {
                JOptionPane.showMessageDialog(parent, "Age or GPA requirement not met!");
                return;
            }

            String sql = "INSERT INTO hostesses(id, name, sex, age, height, status, gpa, level) " +
                         "VALUES(?,?,?,?,?,?,?,?)";

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, sex);
                pstmt.setInt(4, age);
                pstmt.setFloat(5, height);
                pstmt.setString(6, status);
                pstmt.setFloat(7, gpa);
                pstmt.setString(8, level);
                
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(parent, "Hostess added successfully.");
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(parent, "Database error: " + ex.getMessage());
            }
        }

        public static void deleteHostess(JFrame parent) {
            int id = Integer.parseInt(JOptionPane.showInputDialog(parent, "Enter Hostess ID to delete:"));
            
            String sql = "DELETE FROM hostesses WHERE id = ?";

Redi, [5/19/2025 10:02 AM]
try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, id);
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(parent, "Hostess deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(parent, "Hostess not found.");
                }
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(parent, "Database error: " + ex.getMessage());
            }
        }

        public static void viewAllHostesses(JFrame parent) {
            String sql = "SELECT * FROM hostesses";
            StringBuilder sb = new StringBuilder();

            try (Connection conn = DriverManager.getConnection(DB_URL);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getInt("id"))
                      .append(", Name: ").append(rs.getString("name"))
                      .append(", Sex: ").append(rs.getString("sex"))
                      .append(", Age: ").append(rs.getInt("age"))
                      .append(", Height: ").append(rs.getFloat("height"))
                      .append(", Status: ").append(rs.getString("status"))
                      .append(", GPA: ").append(rs.getFloat("gpa"))
                      .append(", Level: ").append(rs.getString("level"))
                      .append("\n");
                }
                
                JOptionPane.showMessageDialog(parent, sb.toString());
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(parent, "Database error: " + ex.getMessage());
            }
        }

        public static void viewProfile(JFrame parent) {
            int id = Integer.parseInt(JOptionPane.showInputDialog(parent, "Enter your Hostess ID:"));
            
            String sql = "SELECT * FROM hostesses WHERE id = ?";
            
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    String profile = "ID: " + rs.getInt("id") + "\n" +
                                    "Name: " + rs.getString("name") + "\n" +
                                    "Sex: " + rs.getString("sex") + "\n" +
                                    "Age: " + rs.getInt("age") + "\n" +
                                    "Height: " + rs.getFloat("height") + "\n" +
                                    "Status: " + rs.getString("status") + "\n" +
                                    "GPA: " + rs.getFloat("gpa") + "\n" +
                                    "Level: " + rs.getString("level");
                    JOptionPane.showMessageDialog(parent, profile);
                } else {
                    JOptionPane.showMessageDialog(parent, "Hostess not found.");
                }
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(parent, "Database error: " + ex.getMessage());
            }
        }

        public static void viewSchedule(JFrame parent) {
            JOptionPane.showMessageDialog(parent, "Schedule viewing not implemented yet");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AirplaneDB());
    }
}

Redi, [5/19/2025 5:04 PM]
case 5: // Base 10 Logarithm
                    System.out.print("Enter positive number: ");
                    num = scanner.nextDouble();
                    if (num > 0) {
                        System.out.println("log(" + num + ") = " + Math.log10(num));
                    } else {
                        System.out.println("Invalid input! Number must be positive.");
                    }
                    break;
