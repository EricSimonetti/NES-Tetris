package com.nes.tetris;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;

public class NesTetris extends ApplicationAdapter implements CommonGameControls {
    private final double FRAME_PERIOD = (1 / 60.0988) * 1000;
    private final List<GameLoop> gameLoops = new ArrayList<>();
    private final Map<Integer, Boolean> ends = new HashMap<>();
    private final int width;
    int frameCounter = 0;
    private boolean paused;
    private boolean legal;
    private long legalTimer;
    private boolean title;
    private boolean menu;
    private int blink;
    private boolean typeSelectA;
    private int musicSelect;
    private Music music;
    private Sound menuShift;
    private Sound beep;
    private Sound beepAlt;
    private Sound pauseBeep;
    private boolean menuA;
    private boolean topLevel;
    private int levelSelect;
    private TextureAtlas textureAtlas;
    private SpriteBatch batch;
    private Texture img;

    public NesTetris(int width) {
        this.width = width;
    }


    public static Color valueOf(String hex) {
        hex = hex.charAt(0) == '#' ? hex.substring(1) : hex;
        int r = Integer.valueOf(hex.substring(0, 2), 16);
        int g = Integer.valueOf(hex.substring(2, 4), 16);
        int b = Integer.valueOf(hex.substring(4, 6), 16);
        int a = hex.length() != 8 ? 255 : Integer.valueOf(hex.substring(6, 8), 16);
        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    @Override
    public void create() {
        init();
    }

    private void init() {
        textureAtlas = new TextureAtlas("sprites.txt");
        batch = new SpriteBatch();
        img = new Texture("A.png");

        paused = false;
        legal = true;
        legalTimer = System.currentTimeMillis();
        title = false;
        menu = false;
        blink = 1;
        typeSelectA = true;
        menuA = false;
        topLevel = true;
        levelSelect = 0;

        music = Gdx.audio.newMusic(Gdx.files.internal("1-music.ogg"));
        music.setLooping(true);

        musicSelect = 0;

        menuShift = Gdx.audio.newSound(Gdx.files.internal("sfx/menu.mp3"));
        beep = Gdx.audio.newSound(Gdx.files.internal("sfx/menuSelect.mp3"));
        beepAlt = Gdx.audio.newSound(Gdx.files.internal("sfx/menuSelect-alt.mp3"));
        pauseBeep = Gdx.audio.newSound(Gdx.files.internal("sfx/pause.mp3"));


        long timeInMillis = Calendar.getInstance().getTimeInMillis();

        gameLoops.add(new TetrisGameLoop(true, 1, width, timeInMillis, textureAtlas, this));
        gameLoops.add(new TetrisGameLoop(false, 2, width, timeInMillis, textureAtlas, this));

        gameLoops.forEach(gameLoop -> {
            ends.put(gameLoop.getPlayerNumber(), false);
        });
    }

    @Override
    public boolean getEnd(int playerNumber) {
        return ends.get(playerNumber);
    }

    @Override
    public void setEnd(boolean end, int playerNumber) {
        ends.put(playerNumber, end);
    }

    @Override
    public int getMusicSelect() {
        return musicSelect;
    }

    @Override
    public void setMenuA(boolean menuA) {
        this.menuA = menuA;
    }

    @Override
    public Music getMusic() {
        return music;
    }

    @Override
    public void setMusic(Music music) {
        this.music = music;
    }

    @Override
    public void dispose() {
        textureAtlas.dispose();
        batch.dispose();
        img.dispose();
        music.dispose();
        menuShift.dispose();
        beep.dispose();
        beepAlt.dispose();
        pauseBeep.dispose();
        gameLoops.forEach(GameLoop::dispose);
    }

    @Override
    public int getFrameCounter() {
        return frameCounter;
    }

    @Override
    public void render() {
        long beginTime = System.currentTimeMillis();
        if (frameCounter == 3)
            frameCounter = 0;
        else
            frameCounter++;
        if (legal) {
            legalUpdate();
            legalRender();
        } else if (title) {
            titleUpdate();
            titleRender();
        } else if (menu) {
            menuUpdate();
            menuRender();
        } else if (menuA) {
            if (frameCounter == 0) {
                blink--;
            }
            menuAUpdate();
            if (blink == 0) {
                blink = 2;
                menuARenderAlt();
            } else
                menuARender();
        } else if (paused) {
            pauseUpdate();
            pauseRender();
        } else {
            gameLoops.stream()
                    .filter(gameLoop -> FALSE.equals(ends.get(gameLoop.getPlayerNumber())))
                    .forEach(GameLoop::update);
            gameLoops.forEach(GameLoop::draw);
        }
        long timeDiff = System.currentTimeMillis() - beginTime;
        long sleepTime = (long) (FRAME_PERIOD - timeDiff);
        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
            }
        }
        //System.out.println(gravity);
        //System.out.println((double)Gdx.graphics.getFramesPerSecond());
    }

    private void legalUpdate() {
        long timeDiff = System.currentTimeMillis() - legalTimer;
        if (timeDiff >= 4250) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                legal = false;
                title = true;
            }
        }
        if (timeDiff >= 8500) {
            legal = false;
            title = true;
        }
    }

    private void legalRender() {
        batch.begin();
        img.dispose();
        Texture img = new Texture("law.png");
        batch.draw(img, 0, 0);
        batch.end();
    }

    private void titleUpdate() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            beep.play();
            title = false;
            menu = true;
        }
    }

    private void titleRender() {
        batch.begin();
        img.dispose();
        Texture img = new Texture("Title.png");
        batch.draw(img, 0, 0);
        batch.end();
    }

    private void menuUpdate() {
        if (!music.isPlaying()) {
            music.play();
        }
        if (musicSelect == 3) {
            if (music != null) {
                music.stop();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            menuShift.play();
            if (typeSelectA) {
                typeSelectA = false;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            menuShift.play();
            if (!typeSelectA) {
                typeSelectA = true;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            menuShift.play();
            if (musicSelect != 3) {
                musicSelect++;
                gameLoops.forEach(GameLoop::setMusic);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            menuShift.play();
            if (musicSelect != 0) {
                musicSelect--;
                gameLoops.forEach(GameLoop::setMusic);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && typeSelectA) {
            beepAlt.play();
            music.stop();
            menu = false;
            menuA = true;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            beepAlt.play();
            menu = false;
            title = true;
        }

        //*/

    }

    private void menuRender() {
        batch.begin();
        img.dispose();
        Texture img = new Texture("menu.png");
        batch.draw(img, 0, 0);

        Sprite ms = textureAtlas.createSprite("musicSelector");
        Sprite ts = textureAtlas.createSprite("typeSelector");

        if (frameCounter == 0) {
            blink--;
        }

        if (typeSelectA && blink == 0) {
            ts.setPosition(186, 482);
            ts.draw(batch);
        } else if (!typeSelectA && blink == 0) {
            ts.setPosition(474, 482);
            ts.draw(batch);
        }

        if (musicSelect == 0 && blink == 0) {
            blink = 1;
            ms.setPosition(307, 237);
            ms.draw(batch);
        } else if (musicSelect == 1 && blink == 0) {
            blink = 1;
            ms.setPosition(307, 189);
            ms.draw(batch);
        } else if (musicSelect == 2 && blink == 0) {
            blink = 1;
            ms.setPosition(307, 141);
            ms.draw(batch);
        } else if (blink == 0) {
            blink = 1;
            ms.setPosition(307, 93);
            ms.draw(batch);
        }
        batch.end();
    }

    private void menuAUpdate() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            beep.play();
            menuA = false;
            menu = true;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            menuShift.play();
            topLevel = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            menuShift.play();
            topLevel = true;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            menuShift.play();
            if (levelSelect != 0) {
                levelSelect--;
            } else if (!topLevel) {
                topLevel = true;
                levelSelect = 4;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            menuShift.play();
            if (levelSelect != 4) {
                levelSelect++;
            } else if (topLevel) {
                topLevel = false;
                levelSelect = 0;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            beep.play();
            menuA = false;
            gameLoops.forEach(gameLoop -> {
                gameLoop.setLevel(topLevel ? levelSelect : (levelSelect + 5));
                gameLoop.setLevelUpdating(true);
            });
            img.dispose();
            img = new Texture("A.png");
            if (musicSelect != 3) {
                music.play();
            }
        }
    }

    private void menuARender() {
        batch.begin();
        img.dispose();
        Texture img = new Texture("menuA.png");
        TextureData textureData = img.getTextureData();
        textureData.prepare();
        Pixmap pixmap = img.getTextureData().consumePixmap();

        int startx = 156 + levelSelect * 48, starty = 227 + (!topLevel ? 48 : 0);
        int endx = startx + 48, endy = starty + 48;

        for (int y = 0; y < pixmap.getHeight(); y++) {
            for (int x = 0; x < pixmap.getWidth(); x++) {
                if (y >= starty && y <= endy && x >= startx && x <= endx) {
                    if (pixmap.getPixel(x, y) == 255) {
                        pixmap.setColor(valueOf("FC9838"));
                        pixmap.fillRectangle(x, y, 1, 1);
                    }

                }
            }
        }

        img = new Texture(pixmap);
        textureData.disposePixmap();
        pixmap.dispose();
        batch.draw(img, 0, 0);
        batch.end();
    }

    private void menuARenderAlt() {
        batch.begin();
        img.dispose();
        Texture img = new Texture("menuA.png");
        batch.draw(img, 0, 0);
        batch.end();
    }

    private void pauseUpdate() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            paused = false;
            music.play();
        }
    }

    private void pauseRender() {
        batch.begin();
        img.dispose();
        Texture img = new Texture("pause.png");
        batch.draw(img, 0, 0);
        batch.end();
    }

    @Override
    public void reset() {
        int store1 = levelSelect;
        boolean store2 = topLevel;
        dispose();
        init();
        levelSelect = store1;
        topLevel = store2;
        legal = false;
        menuA = true;
        gameLoops.forEach(GameLoop::setMusic);
        batch.begin();
    }

    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
