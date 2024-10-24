package com.tyleropher.game;

import java.awt.*;

public class Menu {
    private Font fnt0, fnt1;

    public Rectangle playButton, helpButton, quitButton;

    public Menu() {
        fnt0 = new Font("arial", Font.BOLD, 50);
        fnt1 = new Font("arial", Font.BOLD, 30);

        playButton = new Rectangle(Game.WIDTH / 2 + 120, 200, 100, 50);
        quitButton = new Rectangle(Game.WIDTH / 2 + 120, 300, 100, 50);
    }

    public void render(Graphics g, int high_score) {
        Graphics2D g2d = (Graphics2D)g;

        g.setFont(fnt0);
        g.setColor(Color.WHITE);
        g.drawString("SPACE GAME", Game.WIDTH / 2, 100);

        g.setFont(fnt1);
        g.drawString("Play", playButton.x + 19, playButton.y + 30);
        g2d.draw(playButton);
        g.drawString("Quit", quitButton.x + 19, quitButton.y + 30);
        g2d.draw(quitButton);
        g.drawString("High Score: " + high_score, Game.WIDTH / 2 + 80, 430);
    }
}
