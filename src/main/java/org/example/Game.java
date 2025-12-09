package org.example;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game implements GLEventListener {
    private static final float GRAVITY = -0.015f;
    private static final float JUMP_VELOCITY = 0.3f;
    private static final float BASE_RING_SPACING = 20.0f;

    private PlayerBall playerBall;
    private List<Ring> rings;
    private List<ColorChanger> colorChangers;
    private BackgroundStars backgroundStars;

    private int score;
    private boolean isGameOver;
    private boolean gameOverNotified = false;
    private boolean hasPressedSpace = false;
    private final Random random = new Random();

    private GameStateListener gameStateListener;

    private float worldMinX = -10f;
    private float worldMaxX = 10f;
    private float worldMinY = -20f;
    private float worldMaxY = 20f;
    private float cameraOffsetY = 0f;

    public Game(Object window) {
        // window parameter is now ignored (for compatibility with GLJPanel)
    }

    public void setGameStateListener(GameStateListener listener) {
        this.gameStateListener = listener;
    }

    public void handleKeyPress(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_SPACE:
                if (isGameOver) resetGame();
                else {
                    hasPressedSpace = true;
                    playerBall.jump(JUMP_VELOCITY);
                }
                break;
            case KeyEvent.VK_ESCAPE:
                if (gameStateListener != null) {
                    gameStateListener.onGameExit();
                }
                break;
        }
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void resetGamePublic() {
        resetGame();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.02f, 0.03f, 0.08f, 1.0f);

        backgroundStars = new BackgroundStars(worldMinX, worldMaxX, worldMinY, worldMaxY);
        resetGame();
    }

    private void resetGame() {
        playerBall = new PlayerBall(-8, 0.5f);
        rings = new ArrayList<>();
        colorChangers = new ArrayList<>();
        score = 0;
        isGameOver = false;
        gameOverNotified = false;
        hasPressedSpace = false;
        cameraOffsetY = 0f;
        spawnInitialRings();
    }

    private void spawnInitialRings() {
        spawnRing(5.0f);
        for (int i = 1; i < 3; i++) {
            float lastRingY = rings.get(rings.size() - 1).getY();
            float spacing = BASE_RING_SPACING + random.nextFloat() * 4.0f;
            spawnRing(lastRingY + spacing);
        }
    }

    private void spawnRing(float y) {
        float outerRadius = 3.5f + random.nextFloat() * 3.0f;
        float thickness = 0.8f + random.nextFloat() * 0.7f;
        float innerRadius = outerRadius - thickness;
        float baseSpeed = 1.5f;
        float rotationSpeed = baseSpeed * (4.5f / outerRadius) * (random.nextBoolean() ? 1 : -1);

        rings.add(new Ring(y, innerRadius, outerRadius, rotationSpeed));

        if (random.nextFloat() > 0.6f) {
            float colorChangerY = y + (BASE_RING_SPACING / 2);
            colorChangers.add(new ColorChanger(0, colorChangerY, 0.35f));
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {}

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();

        update();
        render(gl);

        // Notify when game is over (only once)
        if (isGameOver && !gameOverNotified && gameStateListener != null) {
            gameOverNotified = true;
            gameStateListener.onGameExit();
        }
    }

    private void setupOrthographicProjection(GL2 gl, int width, int height) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-10, 10, -10 * (float) height / width, 10 * (float) height / width, -1, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    private void update() {
        if (isGameOver) return;

        playerBall.applyGravity(GRAVITY);
        playerBall.update();

        if (backgroundStars != null) backgroundStars.update(0);

        if (playerBall.getY() > 0) {
            float dy = playerBall.getY();
            playerBall.setY(0);
            cameraOffsetY += dy;
            rings.forEach(r -> r.setY(r.getY() - dy));
            colorChangers.forEach(c -> c.setY(c.getY() - dy));
        }

        if (hasPressedSpace) {
            checkCollisions();
        }
        rings.forEach(Ring::update);

        rings.removeIf(r -> r.getY() < -20);
        colorChangers.removeIf(c -> c.getY() < -20);

        if (!rings.isEmpty() && rings.get(rings.size() - 1).getY() < 15) {
            float lastRingY = rings.get(rings.size() - 1).getY();
            float spacing = BASE_RING_SPACING + random.nextFloat() * 4.0f;
            spawnRing(lastRingY + spacing);
        }
    }

    private void checkCollisions() {
        // Ring collision - direct ring hit check like the template
        for (Ring ring : rings) {
            if (playerBall.isCollidingWithRing(ring)) {
                int segment = ring.getSegmentAtAngle(playerBall.getX(), playerBall.getY());
                if (segment != playerBall.getColorIndex()) {
                    isGameOver = true;
                    return;
                } else if (!ring.isPassed()) {
                    score++;
                    ring.setPassed(true);
                    System.out.println("Score: " + score);
                }
            }
        }

        // Color changer collision
        colorChangers.forEach(changer -> {
            if (changer.isColliding(playerBall)) {
                playerBall.changeColor();
                changer.setY(-20);
            }
        });

        // Out of bounds
        if (playerBall.getY() < -12) isGameOver = true;
    }

    private void render(GL2 gl) {
        if (backgroundStars != null) backgroundStars.draw(gl);
        playerBall.draw(gl);
        rings.forEach(r -> r.draw(gl));
        colorChangers.forEach(c -> c.draw(gl));
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        setupOrthographicProjection(gl, width, height);

        worldMaxY = 10 * (float) height / width;
        worldMinY = -worldMaxY;
        if (backgroundStars != null) backgroundStars.updateBounds(worldMinX, worldMaxX, worldMinY, worldMaxY);
    }
}
 