package com.nes.tetris;

public interface GameLoop {
    void setLevelUpdating(boolean levelUpdating);

    void setLevel(int level);

    void update();

    void draw();

    void setMusic();

    void dispose();
}
