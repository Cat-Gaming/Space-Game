package com.tyleropher.game;

import com.tyleropher.game.classes.EntityA;
import com.tyleropher.game.classes.EntityB;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;

public class Game extends Canvas implements Runnable {

    public static final int WIDTH = 320;
    public static final int HEIGHT = WIDTH / 12 * 9;
    public static final int SCALE = 2;
    public final String TITLE = "Space Game";

    private boolean running = false;
    private Thread thread;

    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private BufferedImage spriteSheet = null;
    private BufferedImage background = null;

    private boolean is_shooting = false;
    private static boolean bgMusicStarts = false;

    private final int default_enemy_count = 5;
    private int enemy_count = default_enemy_count;
    private int enemy_killed = 0;
    private int high_score = 0;
    private int score = 0;

    private Player p;
    private Controller c;
    private Textures tex;
    private Menu menu;

    private Clip bgMusicClip;
    private Clip titleScreenMusicClip;

    public LinkedList<EntityA> ea;
    public LinkedList<EntityB> eb;

    public static final int MAX_HEALTH = 100 * 2;
    public static int HEALTH = MAX_HEALTH;

    private final Font menuFont = new Font("arial", Font.BOLD, 30);

    public enum STATE {
        MENU,
        GAME
    };

    public static STATE State = STATE.MENU;

    private void loadMusicAndPlay() {
        try {
            titleScreenMusicClip = AudioLoader.loadAudioFile("/Space_TitleScreen_Music.wav");
            bgMusicClip = AudioLoader.loadAudioFile("/Space_BG_Music.wav");
            titleScreenMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        requestFocus();
        BufferedImageLoader loader = new BufferedImageLoader();
        try {
            spriteSheet = loader.loadImage("/sprite_sheet.png");
            background = loader.loadImage("/background.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadMusicAndPlay();

        tex = new Textures(this);
        c = new Controller(tex, this);
        p = new Player(((double) WIDTH / 2) * SCALE, ((double) HEIGHT / 2) * SCALE, tex, this, c);
        menu = new Menu();

        ea = c.getEntityA();
        eb = c.getEntityB();

        addKeyListener(new KeyInput(this));
        addMouseListener(new MouseInput());

        c.createEnemy(enemy_count);
    }

    private synchronized void start() {
        if (running)
            return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private synchronized void stop() {
        if (!running)
            return;
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    @Override
    public void run() {
        init();
        long lastTime = System.nanoTime();
        final double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                tick();
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(TITLE + " - FPS: " + frames);
                frames = 0;
            }
        }
        stop();
    }

    private void tick() {
        if (bgMusicStarts) {
            invokeBGMusic();
            bgMusicStarts = false;
        }
        if (State == STATE.GAME) {
            if (HEALTH <= 0) {
                State = STATE.MENU;

                if (score > high_score) {
                    high_score = score;
                }
                score = 0;

                // NOTE: This code has many bugs.
                // I don't know how I fixed the bug, but it worked.
                c.removeAllEntities();
                ea = null;
                eb = null;
                p.setX((double) (WIDTH/2) * SCALE);
                p.setY((double) (HEIGHT/2) * SCALE);
                HEALTH = MAX_HEALTH;
                enemy_count = default_enemy_count;
                enemy_killed = 0;
                c.removeAllEntities();
                c.createEnemy(enemy_count);
                ea = c.getEntityA();
                eb = c.getEntityB();
                bgMusicClip.close();
                titleScreenMusicClip.close();
                loadMusicAndPlay();
                return;
            }
            p.tick();
            c.tick();
            if (enemy_killed >= enemy_count) {
                enemy_count += 2;
                enemy_killed = 0;
                c.createEnemy(enemy_count);
            }
        }
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        // Start drawing
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(background, 0, 0, null);

        if (State == STATE.GAME) {
            p.render(g);
            c.render(g);

            g.setColor(Color.GRAY);
            g.fillRect(5, (Game.HEIGHT * Game.SCALE) - 55, MAX_HEALTH, 50);

            if (HEALTH <= 25 * 2) {
                g.setColor(Color.RED);
            } else if (HEALTH <= 50 * 2) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.GREEN);
            }
            g.fillRect(5, (Game.HEIGHT * Game.SCALE) - 55, HEALTH, 50);

            g.setColor(Color.WHITE);
            g.drawRect(5, (Game.HEIGHT * Game.SCALE) - 55, MAX_HEALTH, 50);

            g.setFont(menuFont);
            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 50, 50);

        } else if (State == STATE.MENU) {
            menu.render(g, high_score);
        }
        // End drawing
        g.dispose();
        bs.show();
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (State == STATE.GAME) {
            if (key == KeyEvent.VK_D) {
                p.setVelX(5);
            } else if (key == KeyEvent.VK_A) {
                p.setVelX(-5);
            } else if (key == KeyEvent.VK_S) {
                p.setVelY(5);
            } else if (key == KeyEvent.VK_W) {
                p.setVelY(-5);
            } else if (key == KeyEvent.VK_SPACE && !is_shooting) {
                is_shooting = true;
                c.addEntity(new Bullet(p.getX(), p.getY(), tex, this));
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_D) {
            p.setVelX(0);
        } else if (key == KeyEvent.VK_A) {
            p.setVelX(0);
        } else if (key == KeyEvent.VK_S) {
            p.setVelY(0);
        } else if (key == KeyEvent.VK_W) {
            p.setVelY(0);
        } else if (key == KeyEvent.VK_SPACE) {
            is_shooting = false;
        }
    }

    public static void main(String[] args) {
        Game game = new Game();

        game.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        game.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

        JFrame frame = new JFrame(game.TITLE);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.start();
    }

    public void invokeBGMusic() {
        if (titleScreenMusicClip == null || bgMusicClip == null) {
            return;
        }
        titleScreenMusicClip.stop();
        bgMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static void startBgMusic() {
        bgMusicStarts = true;
    }

    public BufferedImage getSpriteSheet() {
        return spriteSheet;
    }

    public int getEnemy_count() {
        return enemy_count;
    }

    public void setEnemy_count(int enemy_count) {
        this.enemy_count = enemy_count;
    }

    public int getEnemy_killed() {
        return enemy_killed;
    }

    public void setEnemy_killed(int enemy_killed) {
        this.enemy_killed = enemy_killed;
    }

    public void addScore(int score) {
        this.score += score;
    }
}
