package com.tyleropher.game;

import com.tyleropher.game.classes.EntityA;
import com.tyleropher.game.classes.EntityB;
import com.tyleropher.game.libs.Animation;

import java.awt.*;
import java.util.Random;

public class Enemy extends GameObject implements EntityB {
    Random r = new Random();

    private final double baseSpeed = 1.05;
    private double speed = r.nextDouble(3) + baseSpeed;

    private Textures tex;
    private Controller c;
    private Game game;

    Animation anim;

    public Enemy(double x, double y, Textures tex, Controller c, Game game) {
        super(x, y);
        this.tex = tex;
        this.c = c;
        this.game = game;

        anim = new Animation(20, tex.enemy[0], tex.enemy[1], tex.enemy[2]);
    }

    public void tick() {
        y += speed;

        if (y > Game.HEIGHT * Game.SCALE) {
            speed = r.nextDouble(3) + baseSpeed;
            x = r.nextInt((Game.WIDTH * Game.SCALE) - 32);
            y = -10;
        }

        for (int i = 0; i < game.ea.size(); i++) {
            EntityA tempEnt = game.ea.get(i);
            if (Physics.Collision(this, tempEnt)) {
                c.removeEntity(this);
                game.setEnemy_killed(game.getEnemy_killed() + 1);
                game.addScore(1);
            }
        }

        anim.runAnimation();
    }

    public void render(Graphics g) {
        anim.drawAnimation(g, x, y, 0);
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, 32, 32);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
