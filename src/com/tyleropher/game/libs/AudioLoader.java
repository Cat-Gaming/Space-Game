package com.tyleropher.game.libs;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class AudioLoader {
    public static Clip loadAudioFile(String path) throws UnsupportedAudioFileException, IOException {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    AudioLoader.class.getResourceAsStream(path));
            clip.open(inputStream);
            return clip;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
