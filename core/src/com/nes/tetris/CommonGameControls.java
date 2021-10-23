package com.nes.tetris;

import com.badlogic.gdx.audio.Music;

public interface CommonGameControls {

    boolean getEnd(int playerNumber);

    void setEnd(boolean end, int playerNumber);

    int getMusicSelect();

    void setMenuA(boolean menuA);

    Music getMusic();

    void setMusic(Music music);

    int getFrameCounter();

    void reset();

    void setPaused(boolean paused);

}
