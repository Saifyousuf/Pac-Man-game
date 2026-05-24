import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean loggedIn = false;
    private String loggedUser = null;
    private HashMap<String, String> users = new HashMap<>();
    private static final String USER_FILE = "users.txt";

    public LoginDialog(JFrame parent) {
        super(parent, "Login to Pac-Man", true);
        setSize(300, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        loadUsers();

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Sign Up");
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(loginBtn);
        btnPanel.add(signupBtn);

        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> attemptLogin());
        signupBtn.addActionListener(e -> attemptSignup());
        getRootPane().setDefaultButton(loginBtn);
    }

    private void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (FileNotFoundException e) {
            // No users file yet – create default user
            users.put("player", "pacman");
            saveUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (var entry : users.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void attemptLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill both fields");
            return;
        }
        if (users.containsKey(user) && users.get(user).equals(pass)) {
            loggedIn = true;
            loggedUser = user;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password");
        }
    }

    private void attemptSignup() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill both fields");
            return;
        }
        if (users.containsKey(user)) {
            JOptionPane.showMessageDialog(this, "Username already exists");
            return;
        }
        users.put(user, pass);
        saveUsers();
        JOptionPane.showMessageDialog(this, "Signup successful! Please login.");
        usernameField.setText("");
        passwordField.setText("");
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUsername() {
        return loggedUser;
    }
}