package com.tyleropher.game;

import com.tyleropher.game.classes.EntityA;
import com.tyleropher.game.libs.Animation;

import java.awt.*;

public class Bullet extends GameObject implements EntityA {
    private Textures tex;
    private Game game;

    Animation anim;

    public Bullet(double x, double y, Textures tex, Game game) {
        super(x, y);
        this.tex = tex;
        this.game = game;

        anim = new Animation(5, tex.missile[0], tex.missile[1], tex.missile[2]);
    }

    public void tick() {
        y -= 10;

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
