package com.nes.tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.ArrayList;
import java.util.Arrays;

public class TetrisGameLoop implements GameLoop {

    private static final int[] HEIGHT_HELPER = {384, 339, 288, 240, 192, 147, 105};
    private final int START_X = 288, START_Y = 72;
    private final int BLOCK_SIZE = 24;
    private final int[] gravities = {48, 43, 38, 33, 28, 23, 18, 13, 8, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1};
    private final TextureAtlas textureAtlas;
    private final SpriteBatch batch;
    private final CommonGameControls gameControls;
    int leftBlock = 4, rightBlock = 5;
    private final boolean screenClearer;
    private final int playerNumber;
    private Texture img;
    private ArrayList<Integer> linesBroken;
    private int ARE;
    private int dropcount;
    private int gravity;
    private TetrisGame board;
    private GamePiece gamePiece;
    private Sound lock;
    private int DAS;
    private Sound shift;
    private int ABTimer;
    private int firstlevel;
    private boolean downUnheld;
    private int leveled;
    private int level;
    private int score;
    private int lines;
    private int dropPoints;
    private boolean showNext;
    private Sound rotate;
    private boolean broken;
    private Sound tetris;
    private Sound clear;
    private boolean levelUpdating;
    private GamePiece gpp;
    private boolean fast;
    private String currentPiece = "";
    private String nextPiece;
    private Sprite[] stats;
    private int[] statNums;
    private final int width;
    private int endTimer;
    private int endCurtain;
    private Music endLock;

    public TetrisGameLoop(boolean screenClearer,
                          int playerNumber,
                          int width,
                          TextureAtlas textureAtlas,
                          CommonGameControls commonGameControls) {
        this.screenClearer = screenClearer;
        this.playerNumber = playerNumber;
        this.width = width;

        this.textureAtlas = textureAtlas;
        this.gameControls = commonGameControls;

        batch = new SpriteBatch();
        init();
    }

    private void init() {
        img = new Texture("A.png");
        endTimer = 120;
        broken = false;
        endCurtain = 19;
        dropPoints = 0;
        ARE = 96;
        DAS = 16;
        ABTimer = 0;
        level = 0;
        leveled = 0;
        levelUpdating = false;
        firstlevel = Math.min((level * 10 + 10), Math.max(100, (level * 10 - 50)));
        gravity = gravities[level];
        dropcount = gravity;
        downUnheld = true;
        showNext = true;
        score = 0;
        board = new TetrisGame(level);
        statNums = new int[7];
        currentPiece = board.next();
        statNums[board.getLetterPiece(currentPiece)]++;
        nextPiece = board.next();
        statNums[board.getLetterPiece(nextPiece)]++;
        linesBroken = new ArrayList<>();
        lines = 0;
        stats = new Sprite[7];
        for (int i = 0; i < 7; i++) {
            String peice = board.getPieceLetter(i);
            stats[i] = textureAtlas.createSprite(peice + "-" + level % 10 + "p");
            int x = 78, y = HEIGHT_HELPER[i];
            if (i == 3) x = 87;
            else if (i == 6) x = 72;

            stats[i].setPosition(x + getCurrentPlayerZeroX(), y);
        }

        gpp = new GamePiece(nextPiece, level % 10, textureAtlas);
        gamePiece = new GamePiece(currentPiece, level % 10, textureAtlas);

        endLock = Gdx.audio.newMusic(Gdx.files.internal("sfx/end.mp3"));
        lock = Gdx.audio.newSound(Gdx.files.internal("sfx/lock.mp3"));
        rotate = Gdx.audio.newSound(Gdx.files.internal("sfx/rotate.mp3"));
        shift = Gdx.audio.newSound(Gdx.files.internal("sfx/shift.mp3"));
        tetris = Gdx.audio.newSound(Gdx.files.internal("sfx/tetris.mp3"));
        clear = Gdx.audio.newSound(Gdx.files.internal("sfx/clear.mp3"));

    }

    @Override
    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    public void setLevelUpdating(boolean levelUpdating) {
        this.levelUpdating = levelUpdating;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void update() {
        img.dispose();
        img = new Texture("A.png");
        if (linesBroken.isEmpty()) {
            if (ARE == 0) {
                dropcount--;
                if (dropcount == 0) {
                    dropcount = gravity;
                    int lock = board.drop();
                    if (lock < 0) {
                        gamePiece.drop();
                    } else {
                        this.lock.play();
                        lock += 2;//
                        ARE = (lock / 4) * 2 + 10;
                        newPiece();
                        if (!gameControls.getEnd(getPlayerNumber())) {
                            gameControls.setEnd(board.testEnd(), getPlayerNumber());
                        }
                    }
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                    if (!Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                        if (board.right()) {
                            shift.play();
                            gamePiece.right();
                            DAS = 16;
                        }
                    }
                    normalGrav();
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                    if (!Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                        if (board.left()) {
                            shift.play();
                            gamePiece.left();
                            DAS = 16;
                        }
                    }
                    normalGrav();
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    if (!Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                        if (DAS == 0) {
                            if (board.right()) {
                                DAS = 6;
                                shift.play();
                                gamePiece.right();
                            } else {
                                DAS = 0;
                            }
                        } else
                            DAS--;
                    }
                    normalGrav();
                } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    if (!Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                        if (DAS == 0) {
                            if (board.left()) {
                                DAS = 6;
                                shift.play();
                                gamePiece.left();
                            } else {
                                DAS = 0;
                            }
                        } else
                            DAS--;
                    }
                    normalGrav();
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                    downUnheld = true;
                    dropcount = 3;
                } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    if (downUnheld) {
                        if (level < 29) {
                            gravity = 2;
                        } else gravity = 1;
                        dropPoints++;
                        if (dropPoints == 16)
                            dropPoints = 10;
                    }
                }
                if (!Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    normalGrav();
                    dropPoints = 0;
                }


                if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {
                    showNext = !showNext;
                }

                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    gameControls.getMusic().pause();
                    gameControls.setPaused(true);
                }


                if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                    if (ABTimer == 0) {
                        rotate.play();
                        board.rotateCW();
                        draw();
                    }
                    ABTimer++;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
                    if (ABTimer == 0) {
                        ABTimer += 2;
                        rotate.play();
                        board.rotateCCW();
                        draw();
                    }
                }
                if (ABTimer != 0) {
                    ABTimer--;
                }
            } else {
                ARE--;
                if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                    ARE = 0;
                    dropcount = 3;
                }
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    gameControls.getMusic().pause();
                    gameControls.setPaused(true);
                }
            }
        } else {
            if (!broken) {
                broken = true;
                if (linesBroken.size() == 4) {
                    tetris.play();
                } else {
                    clear.play();
                }
            }
            ArrayList<Integer> store = new ArrayList<>();
            for (Integer i : linesBroken) {
                store.add(i);
                if (gameControls.getFrameCounter() == 0 && leftBlock >= 0) {
                    board.removeSprite(leftBlock, i);
                    board.removeSprite(rightBlock, i);
                    if (linesBroken.size() == 4) {
                        img.dispose();
                        img = new Texture("flash.png");
                    }
                }
            }
            if (gameControls.getFrameCounter() == 2 && linesBroken.size() == 4) {
                img.dispose();
                img = new Texture("A.png");
            }
            if (leftBlock == -1 && gameControls.getFrameCounter() == 0) {
                img.dispose();
                img = new Texture("A.png");
                linesBroken.clear();
            }
            if (gameControls.getFrameCounter() == 0) {
                leftBlock--;
                rightBlock++;
            }
            if (linesBroken.isEmpty()) {
                broken = false;
                leftBlock = 4;
                rightBlock = 5;
                lines += store.size();
                if ((lines > firstlevel && leveled == 0) || (lines > firstlevel + 10 * leveled && leveled > 0)) {
                    level++;
                    leveled++;
                    gravity = gravities[level];
                    levelUpdating = true;
                    gamePiece.setLevel(level);
                    gpp.setLevel(level);
                }
                incScore(store.size());
                board.moveLines(store);
            }
        }
        boolean fast = board.testFast();
        if (fast) {
            if (!this.fast) {
                setFastMusic();
                if (gameControls.getMusicSelect() != 3) {
                    gameControls.getMusic().play();
                }
            }
        } else {
            if (this.fast) {
                setMusic();
                if (gameControls.getMusicSelect() != 3) {
                    gameControls.getMusic().play();
                }
            }
        }
    }

    private void newPiece() {
        linesBroken.addAll(board.addCurr(gamePiece.getBlocks(), gamePiece.getType()));
        score += dropPoints;
        dropPoints = 0;

        gamePiece.newPiece(nextPiece, level % 10);
        currentPiece = nextPiece;
        nextPiece = board.next();
        statNums[board.getLetterPiece(nextPiece)]++;
        downUnheld = false;
        gravity = gravities[level];
        gpp.newPiece(nextPiece, level % 10);
    }

    private void normalGrav() {
        if (level >= 29) {
            gravity = 1;
        } else {
            gravity = gravities[level];
        }
    }

    private int getCurrentPlayerZeroX() {
        return width * (playerNumber - 1);
    }

    @Override
    public void draw() {
        if (screenClearer) {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

        batch.begin();
        batch.draw(img, getCurrentPlayerZeroX(), 0);
        if (levelUpdating) {
            gpp.setLevel(level);
            gamePiece.setLevel(level);
            setLevel(level, board.getSprites(), batch);
            for (int i = 0; i < 7; i++) {
                String peice = board.getPieceLetter(i);
                stats[i] = textureAtlas.createSprite(peice + "-" + (level % 10) + "p");
                int x = 78, y = HEIGHT_HELPER[i];
                if (i == 3) x = 87;
                else if (i == 6) x = 72;

                stats[i].setPosition(x + getCurrentPlayerZeroX(), y);
            }
        }

        for (Sprite[] bs : board.getSprites()) {
            for (Sprite b : bs)
                if (b != null)
                    b.draw(batch);
        }
        for (int i = 0; i < 7; i++) {
            stats[i].draw(batch);
        }
        if (showNext) {
            drawNext(batch);
        }

        if (linesBroken.isEmpty()) {
            if (currentPiece.equals("I")) {
                gridI(board.getCurrentPosX(), board.getCurrentPosY(), board.getCurrentR(), gamePiece, batch);
            } else {
                gridTZSJLO(currentPiece, board.getCurrentPosX(), board.getCurrentPosY(), board.getCurrentR(), gamePiece, batch);
            }
        }

        drawPoints(batch);
        drawLevel(batch);
        drawStats(batch);
        drawLines(batch);

        if (gameControls.getEnd(getPlayerNumber())) {
            endRender(batch);
        }

        batch.end();
    }

    private void incScore(int numLines) {
        int baseScore = 0;
        if (numLines == 1) {
            baseScore = 40;
        } else if (numLines == 2) {
            baseScore = 100;
        } else if (numLines == 3) {
            baseScore = 300;
        } else if (numLines == 4) {
            baseScore = 1200;
        }
        this.score += baseScore * (level + 1);
    }

    private void setFastMusic() {
        fast = true;
        gameControls.getMusic().stop();
        gameControls.getMusic().dispose();
        if (gameControls.getMusicSelect() == 0) {
            gameControls.setMusic(Gdx.audio.newMusic(Gdx.files.internal("1-music-fast.ogg")));
            gameControls.getMusic().setLooping(true);
        } else if (gameControls.getMusicSelect() == 1) {
            gameControls.setMusic(Gdx.audio.newMusic(Gdx.files.internal("2-music-fast.ogg")));
            gameControls.getMusic().setLooping(true);
        } else if (gameControls.getMusicSelect() == 2) {
            gameControls.setMusic(Gdx.audio.newMusic(Gdx.files.internal("3-music-fast.ogg")));
            gameControls.getMusic().setLooping(true);
        }
    }

    @Override
    public void setMusic() {
        fast = false;
        gameControls.getMusic().stop();
        gameControls.getMusic().dispose();
        if (gameControls.getMusicSelect() == 0) {
            gameControls.setMusic(Gdx.audio.newMusic(Gdx.files.internal("1-music.ogg")));
            gameControls.getMusic().setLooping(true);
        } else if (gameControls.getMusicSelect() == 1) {
            gameControls.setMusic(Gdx.audio.newMusic(Gdx.files.internal("2-music.ogg")));
            gameControls.getMusic().setLooping(true);
        } else if (gameControls.getMusicSelect() == 2) {
            gameControls.setMusic(Gdx.audio.newMusic(Gdx.files.internal("3-music.ogg")));
            gameControls.getMusic().setLooping(true);
        }
    }

    public void setLevel(int level, Sprite[][] grid, SpriteBatch batch) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] != null) {
                    int tempX = (int) grid[i][j].getX();
                    int tempY = (int) grid[i][j].getY();
                    int type = (int) grid[i][j].getOriginX();
                    String p = "";
                    if (type == 0) {
                        p = "JS";
                    } else if (type == 1) {
                        p = "LZ";
                    } else {
                        p = "TOI";
                    }
                    grid[i][j] = textureAtlas.createSprite(p + "-" + (level % 10));
                    grid[i][j].setPosition(tempX, tempY);
                    grid[i][j].setOrigin(type, 0);
                }
            }
        }
    }

    private void gridI(int x, int y, int r, GamePiece gamePiece, SpriteBatch batch) {
        int thisX, thisY;
        if (r == 0 || r == 2) {
            thisX = (x * BLOCK_SIZE + START_X + getCurrentPlayerZeroX()) - (2 * BLOCK_SIZE);
            thisY = (y * BLOCK_SIZE + START_Y);
        } else {
            thisX = (x * BLOCK_SIZE + START_X + getCurrentPlayerZeroX());
            thisY = (y * BLOCK_SIZE + START_Y) - BLOCK_SIZE;
        }
        gamePiece.draw("I", thisX, thisY, r, batch);
    }

    private void gridTZSJLO(String piece, int x, int y, int r, GamePiece gpp, SpriteBatch batch) {
        int thisX = (x * BLOCK_SIZE + START_X + getCurrentPlayerZeroX()) - BLOCK_SIZE;
        int thisY = (y * BLOCK_SIZE + START_Y) - BLOCK_SIZE;
        gpp.draw(piece, thisX, thisY, r, batch);
    }

    private void drawPoints(SpriteBatch batch) {
        if (score > 999999) score = 999999;
        String points = score + "";
        int zeros = 6 - points.length();
        for (int i = 0; i < zeros; i++) {
            points = "0" + points;
        }
        String[] numbers = points.split("");
        String[] topNumbers = {"0", "1", "0", "0", "0", "0"};
        int[] widthHelper = {576, 600, 624, 648, 672, 696};
        widthHelper = Arrays.stream(widthHelper)
                .map(operand -> operand + getCurrentPlayerZeroX())
                .toArray();
        Sprite[] scoreboard = new Sprite[6];
        Sprite[] top = new Sprite[6];
        for (int i = 0; i < 6; i++) {
            top[i] = textureAtlas.createSprite(topNumbers[i]);
            top[i].setPosition(widthHelper[i], 555);
            scoreboard[i] = textureAtlas.createSprite(numbers[i]);
            scoreboard[i].setPosition(widthHelper[i], 483);
            top[i].draw(batch);
            scoreboard[i].draw(batch);
        }
    }

    private void drawLevel(SpriteBatch batch) {
        if (level > 99) level = 99;
        String lvl = level + "";
        int zeros = 2 - lvl.length();
        for (int i = 0; i < zeros; i++) {
            lvl = "0" + lvl;
        }
        String[] levelStrArr = lvl.split("");
        Sprite[] levelarr = new Sprite[2];
        int[] widthHelper = {624, 648};
        widthHelper = Arrays.stream(widthHelper)
                .map(operand -> operand + getCurrentPlayerZeroX())
                .toArray();
        for (int i = 0; i < 2; i++) {
            levelarr[i] = textureAtlas.createSprite(levelStrArr[i]);
            levelarr[i].setPosition(widthHelper[i], 171);
            levelarr[i].draw(batch);
        }
    }

    private void drawStats(SpriteBatch batch) {
        String[][] allStats = new String[7][3];
        int[] tempStatNums = new int[7];
        int counter = 0;
        for (int i = 6; i >= 0; i--) {
            tempStatNums[counter] = statNums[i];
            counter++;
        }


        for (int i = 0; i < 7; i++) {
            if (tempStatNums[i] > 999) tempStatNums[i] = 999;
            String stat = tempStatNums[i] + "";
            int zeros = 3 - stat.length();
            for (int j = 0; j < zeros; j++) {
                stat = "0" + stat;
            }
            String[] numbers = stat.split("");
            allStats[i] = numbers;
        }
        int hgap = 48;
        int wgap = 24;
        Sprite[][] statSprites = new Sprite[7][3];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 3; j++) {
                statSprites[i][j] = textureAtlas.createSprite(allStats[i][j]);
                statSprites[i][j].setPosition(144 + getCurrentPlayerZeroX() + wgap * j, 99 + hgap * i);
                statSprites[i][j].draw(batch);
            }
        }
    }

    private void drawLines(SpriteBatch batch) {
        if (lines > 999) lines = 999;
        String ln = lines + "";
        int zeros = 3 - ln.length();
        for (int i = 0; i < zeros; i++) {
            ln = "0" + ln;
        }
        String[] lineStrArr = ln.split("");
        Sprite[] lineArr = new Sprite[3];
        int[] widthHelper = {456, 480, 504};
        widthHelper = Arrays.stream(widthHelper)
                .map(operand -> operand + getCurrentPlayerZeroX())
                .toArray();
        for (int i = 0; i < 3; i++) {
            lineArr[i] = textureAtlas.createSprite(lineStrArr[i]);
            lineArr[i].setPosition(widthHelper[i], 603);
            lineArr[i].draw(batch);
        }
    }

    private void drawNext(SpriteBatch batch) {
        int height, width;
        if (nextPiece.equals("I")) {
            height = 24;
            width = 24 * 4;
        } else if (nextPiece.equals("O")) {
            height = 24 * 2;
            width = 24 * 2;
        } else {
            height = 24 * 2;
            width = 24 * 3;
        }
        int x = ((103 - width) / 2) + 573, y = ((99 - height) / 2) + 261;
        gpp.draw(nextPiece, x + getCurrentPlayerZeroX(), y, 0, batch);
    }

    private void endRender(SpriteBatch batch) {
        if (endCurtain == 19 && endTimer == 120) {
            gameControls.getMusic().stop();
            endLock.play();
            endTimer--;
        } else if (endTimer > 0) {
            endTimer--;
        }
        if (endTimer == 0) {
            Texture[] curtain = new Texture[20 - endCurtain];
            for (int i = 19; i >= endCurtain; i--) {
                curtain[19 - i] = new Texture("C-" + (level % 10) + ".png");
                batch.draw(curtain[19 - i], START_X + getCurrentPlayerZeroX(), START_Y + 24 * i);
            }
            if (endCurtain > 0 && gameControls.getFrameCounter() == 0)
                endCurtain--;
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && endCurtain == 0) {
                for (int i = 19; i >= endCurtain; i--) {
                    curtain[19 - i].dispose();
                }
                gameControls.reset();
                gameControls.setMenuA(true);
            }
        }
    }

    @Override
    public void dispose() {
        gpp.dispose();
        gamePiece.dispose();
        textureAtlas.dispose();
        batch.dispose();
        img.dispose();
        endLock.dispose();
        lock.dispose();
        rotate.dispose();
        shift.dispose();
        tetris.dispose();
        clear.dispose();
    }

}
