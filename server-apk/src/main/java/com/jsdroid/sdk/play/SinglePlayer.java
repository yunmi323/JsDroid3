package com.jsdroid.sdk.play;

import android.media.MediaPlayer;

import java.io.IOException;

public class SinglePlayer {
    static MediaPlayer mediaPlayer;

    public static int play(String file) {
        stop();
        int time = 0;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(file);
            mediaPlayer.prepare();
            time = mediaPlayer.getDuration();
            mediaPlayer.start();
        } catch (Throwable e) {
        }
        return time;
    }

    public static synchronized void stop() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
        } catch (Throwable e) {
        }
    }
}
