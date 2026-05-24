import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        // Show login dialog
        JFrame tempFrame = new JFrame();
        tempFrame.setUndecorated(true);
        tempFrame.setVisible(true);
        LoginDialog login = new LoginDialog(tempFrame);
        login.setVisible(true);
        tempFrame.dispose();

        if (!login.isLoggedIn()) {
            System.exit(0); // user closed dialog without login
        }

        String username = login.getUsername();

        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac-Man - Welcome " + username);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan pacmanGame = new PacMan(username);
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);
    }
}