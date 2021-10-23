package com.nes.tetris.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.nes.tetris.NesTetris;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 672;
        int width = 768;
        config.width = width * 2;
        new LwjglApplication(new NesTetris(width), config);
    }
}
