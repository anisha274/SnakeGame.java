import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

abstract class GameEntity {
    protected int x, y;

    public GameEntity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    abstract void draw(Graphics g, int tileSize);

    public int getX() { return x; }
    public int getY() { return y; }
}

class SnakeSegment extends GameEntity {
    public SnakeSegment(int x, int y) {
        super(x, y);
    }

    @Override
    void draw(Graphics g, int tileSize) {
        g.setColor(Color.WHITE);
        g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
    }
}

class Food extends GameEntity {
    private static final Random RANDOM = new Random();
    private final Color color;

    public Food(int x, int y) {
        super(x, y);
        this.color = new Color(RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256));
    }

    @Override
    void draw(Graphics g, int tileSize) {
        g.setColor(color);
        g.fillOval(x * tileSize, y * tileSize, tileSize, tileSize);
    }
}

class Wall extends GameEntity {
    public Wall(int x, int y) {
        super(x, y);
    }

    @Override
    void draw(Graphics g, int tileSize) {
        g.setColor(new Color(212, 175, 55)); // Golden color
        g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
    }
}

public class SnakeGame extends JFrame {
    private static final int TILE_SIZE = 20;
    private static final int GRID_WIDTH = 30;
    private static final int GRID_HEIGHT = 20;
    private static final int GAME_SPEED = 200;
    private static final int WALL_INTERVAL = 5000;
    private static final int MAX_WALL_ATTEMPTS = 50;

    private final List<SnakeSegment> snake = new ArrayList<>();
    private final List<Wall> walls = new ArrayList<>();
    private final Random random = new Random();
    private Food food;
    private char direction;
    private char nextDirection;
    private boolean running;
    private boolean paused;
    private int score;
    private int wallCollisions;
    private Timer gameTimer;
    private Timer wallTimer;
    private JLabel scoreLabel;

    public SnakeGame() {
        initializeGame();
        initializeGUI();
    }

    private void initializeGame() {
        snake.clear();
        walls.clear();
        snake.add(new SnakeSegment(GRID_WIDTH / 2, GRID_HEIGHT / 2));
        snake.add(new SnakeSegment(GRID_WIDTH / 2 - 1, GRID_HEIGHT / 2));
        snake.add(new SnakeSegment(GRID_WIDTH / 2 - 2, GRID_HEIGHT / 2));
        direction = 'R';
        nextDirection = 'R';
        running = true;
        paused = false;
        score = 0;
        wallCollisions = 0;
        spawnFood();
    }

    private void initializeGUI() {
        setTitle("Snake Game with Golden Walls");
        setSize(GRID_WIDTH * TILE_SIZE + 20, GRID_HEIGHT * TILE_SIZE + 60);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(Color.BLACK);
        add(scoreLabel, BorderLayout.NORTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        initializeTimers();
        setFocusable(true);
    }

    private void initializeTimers() {
        gameTimer = new Timer(GAME_SPEED, e -> {
            if (running && !paused) {
                updateGame();
            }
        });

        wallTimer = new Timer(WALL_INTERVAL, e -> {
            if (running && !paused && score > 3) {
                spawnWall();
            }
        });
        wallTimer.setRepeats(true);

        gameTimer.start();
        wallTimer.start();
    }

    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (direction != 'D') nextDirection = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') nextDirection = 'D';
                break;
            case KeyEvent.VK_LEFT:
                if (direction != 'R') nextDirection = 'L';
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') nextDirection = 'R';
                break;
            case KeyEvent.VK_SPACE:
                if (!running) {
                    initializeGame();
                } else {
                    paused = !paused;
                    scoreLabel.setText("Score: " + score + (paused ? " (PAUSED)" : ""));
                }
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
        }
    }

    private void updateGame() {
        direction = nextDirection;
        move();
        checkCollisions();
        checkFood();
        repaint();
    }

    private void spawnFood() {
        int x, y;
        do {
            x = random.nextInt(GRID_WIDTH);
            y = random.nextInt(GRID_HEIGHT);
        } while (isPositionOccupied(x, y));
        food = new Food(x, y);
    }

    private void spawnWall() {
        int wallSize = random.nextBoolean() ? 2 : 3;
        boolean horizontal = random.nextBoolean();
        int x, y;
        boolean validPosition;
        int attempts = 0;

        do {
            validPosition = true;
            attempts++;
            if (attempts > MAX_WALL_ATTEMPTS) return;

            if (horizontal) {
                x = random.nextInt(GRID_WIDTH - wallSize + 1);
                y = random.nextInt(GRID_HEIGHT);
                for (int i = 0; i < wallSize; i++) {
                    if (isPositionOccupied(x + i, y)) {
                        validPosition = false;
                        break;
                    }
                }
            } else {
                x = random.nextInt(GRID_WIDTH);
                y = random.nextInt(GRID_HEIGHT - wallSize + 1);
                for (int i = 0; i < wallSize; i++) {
                    if (isPositionOccupied(x, y + i)) {
                        validPosition = false;
                        break;
                    }
                }
            }
        } while (!validPosition);

        for (int i = 0; i < wallSize; i++) {
            walls.add(horizontal ? new Wall(x + i, y) : new Wall(x, y + i));
        }
    }

    private boolean isPositionOccupied(int x, int y) {
        return isSnakeAt(x, y) || isWallAt(x, y) || (food != null && food.getX() == x && food.getY() == y);
    }

    private boolean isSnakeAt(int x, int y) {
        for (SnakeSegment segment : snake) {
            if (segment.getX() == x && segment.getY() == y) {
                return true;
            }
        }
        return false;
    }

    private boolean isWallAt(int x, int y) {
        for (Wall wall : walls) {
            if (wall.getX() == x && wall.getY() == y) {
                return true;
            }
        }
        return false;
    }

    private void move() {
        SnakeSegment head = snake.get(0);
        int newX = head.getX();
        int newY = head.getY();

        switch (direction) {
            case 'U': newY--; break;
            case 'D': newY++; break;
            case 'L': newX--; break;
            case 'R': newX++; break;
        }

        snake.add(0, new SnakeSegment(newX, newY));
        snake.remove(snake.size() - 1);
    }

    private void checkCollisions() {
        SnakeSegment head = snake.get(0);
        int x = head.getX();
        int y = head.getY();

        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            handleWallCollision();
            return;
        }

        if (isWallAt(x, y)) {
            gameOver("Golden wall collision!");
            return;
        }

        for (int i = 1; i < snake.size(); i++) {
            if (x == snake.get(i).getX() && y == snake.get(i).getY()) {
                gameOver("Self collision!");
                return;
            }
        }
    }

    private void handleWallCollision() {
        wallCollisions++;
        if (wallCollisions < 3) {
            JOptionPane.showMessageDialog(this,
                    "Warning! " + (3 - wallCollisions) + " attempts left");
            resetSnakePosition();
            walls.clear();
        } else {
            gameOver("Wall collision!");
        }
    }

    private void resetSnakePosition() {
        snake.clear();
        snake.add(new SnakeSegment(GRID_WIDTH / 2, GRID_HEIGHT / 2));
        snake.add(new SnakeSegment(GRID_WIDTH / 2 - 1, GRID_HEIGHT / 2));
        snake.add(new SnakeSegment(GRID_WIDTH / 2 - 2, GRID_HEIGHT / 2));
        direction = 'R';
        nextDirection = 'R';
    }

    private void gameOver(String reason) {
        running = false;
        gameTimer.stop();
        wallTimer.stop();
        JOptionPane.showMessageDialog(this,
                "Game Over! " + reason + " Score: " + score + "\nPress SPACE to restart");
    }

    private void checkFood() {
        SnakeSegment head = snake.get(0);
        if (head.getX() == food.getX() && head.getY() == food.getY()) {
            score++;
            scoreLabel.setText("Score: " + score);
            growSnake();
            spawnFood();
        }
    }

    private void growSnake() {
        SnakeSegment last = snake.get(snake.size() - 1);
        snake.add(new SnakeSegment(last.getX(), last.getY()));
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(Color.BLACK);
            g.setColor(new Color(30, 30, 30));

            for (int i = 0; i <= GRID_WIDTH; i++) {
                g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);}
            for (int i = 0; i <= GRID_HEIGHT; i++) {
                g.drawLine(0, i * TILE_SIZE, GRID_WIDTH * TILE_SIZE, i * TILE_SIZE);}

            for (Wall wall : walls) { wall.draw(g, TILE_SIZE);}
            food.draw(g, TILE_SIZE);

            for (SnakeSegment segment : snake) { segment.draw(g, TILE_SIZE);}
            if (!snake.isEmpty()) {
                g.setColor(Color.GREEN);
                g.fillRect(snake.get(0).getX() * TILE_SIZE,
                        snake.get(0).getY() * TILE_SIZE,
                        TILE_SIZE, TILE_SIZE);}}}}