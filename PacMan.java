import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.Timer;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    private final int ROW_COUNT = 21;
    private final int COL_COUNT = 19;
    private final int TILE_SIZE = 32;
    private final int BOARD_WIDTH = COL_COUNT * TILE_SIZE;
    private final int BOARD_HEIGHT = ROW_COUNT * TILE_SIZE;
    private final int PACMAN_BASE_SPEED = TILE_SIZE / 4;
    private int pacmanSpeed = PACMAN_BASE_SPEED;
    private final int GHOST_SPEED = TILE_SIZE / 4;

    private Image wallImage;
    private Image blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private HashSet<Block> walls;
    private HashSet<Block> foods;
    private HashSet<Block> powerPellets;
    private HashSet<Block> ghosts;
    private HashSet<Block> speedBoosts;
    private HashSet<Block> invisibilityOrbs;
    private Block pacman;

    private Timer gameLoop;
    private Random random = new Random();
    private int score = 0;
    private int highScore = 0;
    private int lives = 3;
    private int level = 1;
    private boolean gameOver = false;
    private boolean powerMode = false;
    private int powerTimer = 0;
    private int ghostsEatenCombo = 0;
    private char requestedDirection = 'R';
    private long modeSwitchTime = 0;
    private boolean scatterMode = true;

    private boolean speedBoostActive = false;
    private int speedBoostTimer = 0;
    private boolean invisibleActive = false;
    private int invisibleTimer = 0;

    private ArrayList<Particle> particles = new ArrayList<>();
    private float bgHue = 0f;
    private int screenShake = 0;

    private HashMap<Block, Boolean> ghostEdible = new HashMap<>();
    private HashMap<Block, Integer> ghostEatenTimer = new HashMap<>();

    private final char[] DIRECTIONS = {'U', 'D', 'L', 'R'};

    // USERNAME (added for login)
    private String username;

    // Tile map (same as before)
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X   B    O    I   X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X   B   O   I    X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    class Particle {
        int x, y, life;
        Color color;
        Particle(int x, int y, Color c) {
            this.x = x; this.y = y; this.life = 15; this.color = c;
        }
        void update() { life--; }
        void draw(Graphics g) {
            if (life > 0) {
                g.setColor(color);
                g.fillRect(x, y, 3, 3);
            }
        }
    }

    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'U';
        int velX = 0, velY = 0;

        Block(Image img, int x, int y, int w, int h) {
            this.image = img;
            this.x = x; this.y = y; this.width = w; this.height = h;
            this.startX = x; this.startY = y;
        }

        void setDirection(char dir) {
            int spd = (dir == requestedDirection && speedBoostActive) ? (int)(pacmanSpeed * 1.5f) : pacmanSpeed;
            if (dir == 'U') { velX = 0; velY = -spd; }
            else if (dir == 'D') { velX = 0; velY = spd; }
            else if (dir == 'L') { velX = -spd; velY = 0; }
            else if (dir == 'R') { velX = spd; velY = 0; }
            direction = dir;
        }

        void reset() {
            x = startX; y = startY; velX = 0; velY = 0; direction = 'U';
        }
    }

    // Constructor now takes username
    public PacMan(String username) {
        this.username = username;
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        loadImages();
        loadHighScore();    // loads per-user high score
        loadMap();
        initGhostModes();
        startGameLoop();
    }

    private void loadImages() {
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
    }

    // Per-user high score file
    private void loadHighScore() {
        String filename = "highscore_" + username + ".txt";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            highScore = Integer.parseInt(br.readLine());
        } catch (Exception e) { highScore = 0; }
    }

    private void saveHighScore() {
        String filename = "highscore_" + username + ".txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write(String.valueOf(highScore));
        } catch (Exception e) {}
    }

    private void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        powerPellets = new HashSet<>();
        ghosts = new HashSet<>();
        speedBoosts = new HashSet<>();
        invisibilityOrbs = new HashSet<>();

        for (int r = 0; r < ROW_COUNT; r++) {
            String row = tileMap[r];
            if (row.length() != COL_COUNT) continue;
            for (int c = 0; c < COL_COUNT; c++) {
                char ch = row.charAt(c);
                int x = c * TILE_SIZE;
                int y = r * TILE_SIZE;

                if (ch == 'X') {
                    walls.add(new Block(wallImage, x, y, TILE_SIZE, TILE_SIZE));
                } else if (ch == 'b') {
                    Block g = new Block(blueGhostImage, x, y, TILE_SIZE, TILE_SIZE);
                    ghosts.add(g);
                } else if (ch == 'o') {
                    Block g = new Block(orangeGhostImage, x, y, TILE_SIZE, TILE_SIZE);
                    ghosts.add(g);
                } else if (ch == 'p') {
                    Block g = new Block(pinkGhostImage, x, y, TILE_SIZE, TILE_SIZE);
                    ghosts.add(g);
                } else if (ch == 'r') {
                    Block g = new Block(redGhostImage, x, y, TILE_SIZE, TILE_SIZE);
                    ghosts.add(g);
                } else if (ch == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, TILE_SIZE, TILE_SIZE);
                    pacman.setDirection('R');
                } else if (ch == ' ') {
                    foods.add(new Block(null, x + 14, y + 14, 4, 4));
                } else if (ch == 'O') {
                    powerPellets.add(new Block(null, x + 8, y + 8, 16, 16));
                } else if (ch == 'B') {
                    speedBoosts.add(new Block(null, x, y, TILE_SIZE, TILE_SIZE));
                } else if (ch == 'I') {
                    invisibilityOrbs.add(new Block(null, x, y, TILE_SIZE, TILE_SIZE));
                }
            }
        }
    }

    private void initGhostModes() {
        for (Block g : ghosts) {
            ghostEdible.put(g, false);
            ghostEatenTimer.put(g, 0);
            char randDir = DIRECTIONS[random.nextInt(4)];
            g.setDirection(randDir);
        }
        modeSwitchTime = System.currentTimeMillis();
        scatterMode = true;
    }

    private void startGameLoop() {
        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    private void updateMode() {
        long now = System.currentTimeMillis();
        long elapsed = now - modeSwitchTime;
        if (scatterMode && elapsed > 7000) {
            scatterMode = false;
            modeSwitchTime = now;
        } else if (!scatterMode && elapsed > 20000) {
            scatterMode = true;
            modeSwitchTime = now;
        }
    }

    private void movePacman() {
        char tryDir = requestedDirection;
        Block test = new Block(null, pacman.x, pacman.y, pacman.width, pacman.height);
        int spd = (speedBoostActive ? (int)(pacmanSpeed * 1.5f) : pacmanSpeed);
        if (tryDir == 'U') test.y -= spd;
        else if (tryDir == 'D') test.y += spd;
        else if (tryDir == 'L') test.x -= spd;
        else if (tryDir == 'R') test.x += spd;

        boolean blocked = false;
        for (Block w : walls) if (collision(test, w)) { blocked = true; break; }
        if (!blocked) pacman.setDirection(tryDir);

        pacman.x += pacman.velX;
        pacman.y += pacman.velY;

        for (Block w : walls) {
            if (collision(pacman, w)) {
                pacman.x -= pacman.velX;
                pacman.y -= pacman.velY;
                break;
            }
        }

        if (pacman.direction == 'U') pacman.image = pacmanUpImage;
        else if (pacman.direction == 'D') pacman.image = pacmanDownImage;
        else if (pacman.direction == 'L') pacman.image = pacmanLeftImage;
        else pacman.image = pacmanRightImage;

        if (pacman.x < 0) pacman.x = BOARD_WIDTH - TILE_SIZE;
        if (pacman.x + TILE_SIZE > BOARD_WIDTH) pacman.x = 0;
    }

    private void moveGhosts() {
        updateMode();
        for (Block ghost : ghosts) {
            if (ghostEatenTimer.get(ghost) > 0) {
                ghostEatenTimer.put(ghost, ghostEatenTimer.get(ghost) - 1);
                if (ghostEatenTimer.get(ghost) == 0) {
                    ghost.reset();
                    ghostEdible.put(ghost, false);
                    ghost.setDirection(DIRECTIONS[random.nextInt(4)]);
                }
                continue;
            }

            int targetX = 0, targetY = 0;
            if (powerMode && ghostEdible.get(ghost)) {
                targetX = random.nextInt(BOARD_WIDTH);
                targetY = random.nextInt(BOARD_HEIGHT);
            } else if (invisibleActive && !powerMode) {
                targetX = random.nextInt(BOARD_WIDTH);
                targetY = random.nextInt(BOARD_HEIGHT);
            } else if (scatterMode) {
                if (ghost.image == redGhostImage) { targetX = 0; targetY = 0; }
                else if (ghost.image == pinkGhostImage) { targetX = BOARD_WIDTH - TILE_SIZE; targetY = 0; }
                else if (ghost.image == blueGhostImage) { targetX = 0; targetY = BOARD_HEIGHT - TILE_SIZE; }
                else { targetX = BOARD_WIDTH - TILE_SIZE; targetY = BOARD_HEIGHT - TILE_SIZE; }
            } else {
                if (ghost.image == redGhostImage) {
                    targetX = pacman.x; targetY = pacman.y;
                } else if (ghost.image == pinkGhostImage) {
                    int aheadX = pacman.x, aheadY = pacman.y;
                    if (pacman.direction == 'U') aheadY -= 4 * TILE_SIZE;
                    else if (pacman.direction == 'D') aheadY += 4 * TILE_SIZE;
                    else if (pacman.direction == 'L') aheadX -= 4 * TILE_SIZE;
                    else aheadX += 4 * TILE_SIZE;
                    targetX = aheadX; targetY = aheadY;
                } else if (ghost.image == blueGhostImage) {
                    targetX = pacman.x + (pacman.direction == 'L' ? -2 : pacman.direction == 'R' ? 2 : 0) * TILE_SIZE;
                    targetY = pacman.y + (pacman.direction == 'U' ? -2 : pacman.direction == 'D' ? 2 : 0) * TILE_SIZE;
                } else {
                    double dist = Math.hypot(ghost.x - pacman.x, ghost.y - pacman.y);
                    if (dist > 8 * TILE_SIZE) { targetX = pacman.x; targetY = pacman.y; }
                    else { targetX = 0; targetY = BOARD_HEIGHT - TILE_SIZE; }
                }
            }

            char bestDir = ghost.direction;
            int bestDist = Integer.MAX_VALUE;
            for (char dir : DIRECTIONS) {
                int newX = ghost.x, newY = ghost.y;
                if (dir == 'U') newY -= GHOST_SPEED;
                else if (dir == 'D') newY += GHOST_SPEED;
                else if (dir == 'L') newX -= GHOST_SPEED;
                else newX += GHOST_SPEED;
                Block test = new Block(null, newX, newY, TILE_SIZE, TILE_SIZE);
                boolean collides = false;
                for (Block w : walls) if (collision(test, w)) { collides = true; break; }
                if (!collides) {
                    int dist = Math.abs(newX - targetX) + Math.abs(newY - targetY);
                    if (dist < bestDist) { bestDist = dist; bestDir = dir; }
                }
            }
            ghost.setDirection(bestDir);
            ghost.x += ghost.velX;
            ghost.y += ghost.velY;
            for (Block w : walls) if (collision(ghost, w)) {
                ghost.x -= ghost.velX;
                ghost.y -= ghost.velY;
                break;
            }
        }
    }

    private void checkCollisions() {
        Block eatenFood = null;
        for (Block f : foods) if (collision(pacman, f)) { eatenFood = f; score += 10; addParticles(f.x + 2, f.y + 2, Color.WHITE); Toolkit.getDefaultToolkit().beep(); }
        foods.remove(eatenFood);

        Block eatenPower = null;
        for (Block p : powerPellets) if (collision(pacman, p)) { eatenPower = p; score += 50; activatePowerMode(); addParticles(p.x + 8, p.y + 8, Color.YELLOW); Toolkit.getDefaultToolkit().beep(); }
        powerPellets.remove(eatenPower);

        Block eatenSpeed = null;
        for (Block b : speedBoosts) if (collision(pacman, b)) { eatenSpeed = b; activateSpeedBoost(); addParticles(b.x + 16, b.y + 16, Color.CYAN); Toolkit.getDefaultToolkit().beep(); }
        speedBoosts.remove(eatenSpeed);

        Block eatenInvis = null;
        for (Block i : invisibilityOrbs) if (collision(pacman, i)) { eatenInvis = i; activateInvisibility(); addParticles(i.x + 16, i.y + 16, new Color(255, 0, 255)); Toolkit.getDefaultToolkit().beep(); }
        invisibilityOrbs.remove(eatenInvis);

        for (Block ghost : ghosts) {
            if (ghostEatenTimer.get(ghost) > 0) continue;
            if (collision(pacman, ghost)) {
                if (powerMode && ghostEdible.get(ghost)) {
                    int points = 200 * (1 << ghostsEatenCombo);
                    score += points;
                    ghostsEatenCombo++;
                    ghostEdible.put(ghost, false);
                    ghostEatenTimer.put(ghost, 120);
                    ghost.x = -100; ghost.y = -100;
                    addParticles(ghost.startX + 16, ghost.startY + 16, Color.ORANGE);
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    lives--;
                    screenShake = 10;
                    if (lives <= 0) {
                        gameOver = true;
                        if (score > highScore) { highScore = score; saveHighScore(); }
                        gameLoop.stop();
                    } else { resetPositions(); }
                }
            }
        }

        if (foods.isEmpty() && powerPellets.isEmpty() && speedBoosts.isEmpty() && invisibilityOrbs.isEmpty()) {
            level++;
            score += 1000 * level;
            loadMap();
            resetPositions();
            for (Block g : ghosts) {
                g.setDirection(DIRECTIONS[random.nextInt(4)]);
                ghostEdible.put(g, false);
                ghostEatenTimer.put(g, 0);
            }
        }
    }

    private void activatePowerMode() {
        powerMode = true;
        powerTimer = 180;
        ghostsEatenCombo = 0;
        for (Block g : ghosts) ghostEdible.put(g, true);
    }

    private void activateSpeedBoost() { speedBoostActive = true; speedBoostTimer = 300; }
    private void activateInvisibility() { invisibleActive = true; invisibleTimer = 250; }

    private void updatePowerUps() {
        if (powerMode) { powerTimer--; if (powerTimer <= 0) { powerMode = false; for (Block g : ghosts) ghostEdible.put(g, false); } }
        if (speedBoostActive) { speedBoostTimer--; if (speedBoostTimer <= 0) speedBoostActive = false; }
        if (invisibleActive) { invisibleTimer--; if (invisibleTimer <= 0) invisibleActive = false; }
    }

    private void addParticles(int x, int y, Color c) {
        for (int i = 0; i < 6; i++) particles.add(new Particle(x + random.nextInt(10) - 5, y + random.nextInt(10) - 5, c));
    }

    private void updateParticles() { particles.removeIf(p -> { p.update(); return p.life <= 0; }); }

    private void resetPositions() {
        pacman.reset(); pacman.setDirection('R'); requestedDirection = 'R';
        for (Block g : ghosts) {
            g.reset(); ghostEdible.put(g, false); ghostEatenTimer.put(g, 0);
            g.setDirection(DIRECTIONS[random.nextInt(4)]);
        }
        powerMode = false; speedBoostActive = false; invisibleActive = false; powerTimer = 0;
    }

    private boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        bgHue += 0.003f; if (bgHue > 1) bgHue -= 1;
        Color startColor = Color.getHSBColor(bgHue, 0.4f, 0.15f);
        Color endColor = Color.getHSBColor((bgHue + 0.5f) % 1, 0.6f, 0.1f);
        GradientPaint grad = new GradientPaint(0, 0, startColor, 0, BOARD_HEIGHT, endColor);
        g2d.setPaint(grad);
        g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g2d.setColor(new Color(255, 255, 255, 40));
        for (int i = 0; i <= ROW_COUNT; i++) { g2d.drawLine(0, i * TILE_SIZE, BOARD_WIDTH, i * TILE_SIZE); g2d.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, BOARD_HEIGHT); }

        int shakeX = (screenShake > 0) ? random.nextInt(5) - 2 : 0;
        int shakeY = (screenShake > 0) ? random.nextInt(5) - 2 : 0;
        if (screenShake > 0) screenShake--;
        g2d.translate(shakeX, shakeY);

        for (Block w : walls) g2d.drawImage(w.image, w.x, w.y, w.width, w.height, null);
        g2d.setColor(Color.WHITE);
        for (Block f : foods) g2d.fillRect(f.x, f.y, f.width, f.height);
        for (Block p : powerPellets) { g2d.setColor(Color.YELLOW); g2d.fillOval(p.x, p.y, p.width, p.height); g2d.setColor(Color.ORANGE); g2d.fillOval(p.x + 4, p.y + 4, p.width - 8, p.height - 8); }
        for (Block b : speedBoosts) { g2d.setColor(Color.CYAN); g2d.fillRect(b.x + 4, b.y + 4, b.width - 8, b.height - 8); g2d.setColor(Color.BLUE); g2d.setFont(new Font("Arial", Font.BOLD, 20)); g2d.drawString("S", b.x + 12, b.y + 24); }
        for (Block i : invisibilityOrbs) { g2d.setColor(new Color(255, 0, 255)); g2d.fillRect(i.x + 4, i.y + 4, i.width - 8, i.height - 8); g2d.setColor(Color.WHITE); g2d.drawString("?", i.x + 12, i.y + 24); }

        for (Block gh : ghosts) {
            if (ghostEatenTimer.get(gh) == 0) {
                if (powerMode && ghostEdible.get(gh)) {
                    if ((System.currentTimeMillis() / 100) % 2 == 0) {
                        g2d.setColor(Color.BLUE); g2d.fillRect(gh.x, gh.y, gh.width, gh.height);
                        g2d.setColor(Color.WHITE); g2d.fillOval(gh.x + 8, gh.y + 8, 6, 6); g2d.fillOval(gh.x + 18, gh.y + 8, 6, 6);
                    } else {
                        g2d.setColor(Color.WHITE); g2d.fillRect(gh.x, gh.y, gh.width, gh.height);
                        g2d.setColor(Color.BLUE); g2d.fillOval(gh.x + 8, gh.y + 8, 6, 6); g2d.fillOval(gh.x + 18, gh.y + 8, 6, 6);
                    }
                } else { g2d.drawImage(gh.image, gh.x, gh.y, gh.width, gh.height, null); }
            }
        }

        if (!invisibleActive || (System.currentTimeMillis() / 200) % 2 == 0) g2d.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        else { g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f)); g2d.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null); g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); }

        for (Particle p : particles) p.draw(g2d);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 18));
        g2d.setColor(Color.WHITE);
        String scoreStr = "SCORE: " + score;
        String highStr = "BEST: " + highScore;
        String livesStr = "LIVES: " + lives;
        String levelStr = "LVL: " + level;
        String userStr = "USER: " + username;
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(scoreStr, 8, 22);
        g2d.drawString(highStr, BOARD_WIDTH - fm.stringWidth(highStr) - 8, 22);
        g2d.drawString(livesStr, 8, BOARD_HEIGHT - 8);
        g2d.drawString(levelStr, BOARD_WIDTH - fm.stringWidth(levelStr) - 8, BOARD_HEIGHT - 8);
        g2d.drawString(userStr, 8, 45);

        int yOff = 65;
        if (powerMode) { g2d.setColor(Color.RED); g2d.drawString("GHOST EATER", 10, yOff); yOff += 20; }
        if (speedBoostActive) { g2d.setColor(Color.CYAN); g2d.drawString("SPEED BOOST", 10, yOff); yOff += 20; }
        if (invisibleActive) { g2d.setColor(new Color(255, 0, 255)); g2d.drawString("INVISIBLE", 10, yOff); }

        if (gameOver) {
            g2d.setColor(new Color(0, 0, 0, 200)); g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 32)); g2d.setColor(Color.RED);
            String msg = "GAME OVER"; int msgW = fm.stringWidth(msg);
            g2d.drawString(msg, (BOARD_WIDTH - msgW)/2, BOARD_HEIGHT/2 - 30);
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 18));
            String restart = "Press any key to restart"; int resW = fm.stringWidth(restart);
            g2d.drawString(restart, (BOARD_WIDTH - resW)/2, BOARD_HEIGHT/2 + 20);
        }

        g2d.translate(-shakeX, -shakeY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            movePacman(); moveGhosts(); checkCollisions(); updatePowerUps(); updateParticles();
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            gameOver = false; lives = 3; score = 0; level = 1;
            loadMap(); resetPositions(); initGhostModes(); gameLoop.start(); repaint();
            return;
        }
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_UP) requestedDirection = 'U';
        else if (code == KeyEvent.VK_DOWN) requestedDirection = 'D';
        else if (code == KeyEvent.VK_LEFT) requestedDirection = 'L';
        else if (code == KeyEvent.VK_RIGHT) requestedDirection = 'R';
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}