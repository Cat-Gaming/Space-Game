package com.tyleropher.game.classes;

import java.awt.*;

public interface EntityB {
    void tick();
    void render(Graphics g);
    public Rectangle getBounds();

    double getX();
    double getY();
}
