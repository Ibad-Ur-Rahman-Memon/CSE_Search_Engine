import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

// Student class (updated)
class Student {
    String id, name, email, batch;

    public Student(String id, String name, String email, String batch) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.batch = batch;
    }

    public boolean matches(String query) {
        return name.equalsIgnoreCase(query) || id.equalsIgnoreCase(query);
    }

    @Override
    public String toString() {
        return "<html><b>ID:</b> " + id +
               "<br><b>Name:</b> " + name +
               "<br><b>Email:</b> " + email +
               "<br><b>Batch:</b> " + batch + "</html>";
    }
}

// Main GUI class
public class CSESearchEngine {
    private JFrame frame;
    private JTextField searchBar;
    private ArrayList<Student> students;

    public CSESearchEngine() {
        frame = new JFrame("CSE.Search Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        students = new ArrayList<>();
        loadStudentsFromDatabase();
        createGUI();
    }

    private void loadStudentsFromDatabase() {
        String dbPath = "F:/3rd Semester Project_java/CSE.SearchEngine/Students_CSE.db";
        String url = "jdbc:sqlite:" + dbPath;

        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT Id, name, email, batch FROM students";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String id = rs.getString("Id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String batch = rs.getString("batch");
                students.add(new Student(id, name, email, batch));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to load data from database:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createGUI() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(80, 20, 20, 20));

        JLabel logoLabel = new JLabel(new ImageIcon("F:/3rd Semester Project_java/CSE_Logo.png"));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(logoLabel);

        JLabel title = new JLabel("CSE");
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(new Color(66, 133, 244));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel(".Search Engine");
        subTitle.setFont(new Font("Arial", Font.PLAIN, 30));
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subTitle);
        centerPanel.add(titlePanel);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setOpaque(false);

        searchBar = new JTextField("Search your name or ID here", 30);
        searchBar.setFont(new Font("Arial", Font.ITALIC, 16));
        searchBar.setForeground(Color.GRAY);
        searchBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        searchBar.setPreferredSize(new Dimension(400, 40));

        JPanel roundedSearchBar = new JPanel(new BorderLayout());
        roundedSearchBar.setPreferredSize(new Dimension(450, 50));
        roundedSearchBar.setBackground(Color.WHITE);
        roundedSearchBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        roundedSearchBar.add(searchBar, BorderLayout.CENTER);
        searchPanel.add(roundedSearchBar);

        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 18));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.setFocusPainted(false);
        searchPanel.add(searchButton);

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(searchPanel);
        frame.add(centerPanel, BorderLayout.CENTER);

        searchButton.addActionListener(e -> search());
        searchBar.addActionListener(e -> search());

        frame.setVisible(true);
    }

    private void search() {
        String query = searchBar.getText().trim();
        if (query.isEmpty() || query.equals("Search your name or ID here")) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid search query.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Student result = null;
        for (Student student : students) {
            if (student.matches(query)) {
                result = student;
                break;
            }
        }

        if (result != null) {
            showResult(result);
        } else {
            JOptionPane.showMessageDialog(frame, "No results found for '" + query + "'.", "No Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showResult(Student student) {
        JFrame resultFrame = new JFrame("Search Result");
        resultFrame.setSize(400, 250);
        resultFrame.setLayout(new BorderLayout());

        JLabel resultLabel = new JLabel(student.toString(), JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        resultFrame.add(resultLabel, BorderLayout.CENTER);
        resultFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CSESearchEngine::new);
    }
}
