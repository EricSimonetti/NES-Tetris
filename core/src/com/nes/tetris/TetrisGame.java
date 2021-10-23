package com.nes.tetris;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;
import java.util.Random;

public class TetrisGame {
    private final int BLOCK_SIZE = 24;
    private final Sprite[][] grid;
    private final Random random;
    private String next, current;
    private int currentPosX, currentPosY;
    private int[] xPos, yPos;
    private int currentR = 0;

    TetrisGame(long randomSeed) {
        random = new Random(randomSeed);
        this.grid = new Sprite[22][10];
        this.next = "";
        this.current = "";
        this.currentPosX = 5;
        this.currentPosY = 19;
        this.xPos = new int[4];
        this.yPos = new int[4];
    }

    public String next() {
        this.current = this.next;

        String next = "";
        int nextInt = random.nextInt(8); // I, O, T, S, Z, J, L, and dummy.
        next = getPieceLetter(nextInt);
        if (next.equals("") || next.equals(current)) //if the next piece landed on the dummy or is the same as the last peice
            next = reroll();                        //reroll
        this.next = next;


        this.currentPosX = 5;
        this.currentPosY = 19;
        this.currentR = 0;
        this.xPos = new int[4];
        this.yPos = new int[4];
        initCurrent();
        return this.next;
    }

    //int generateNextPseudorandomNumber(int value) { //original sudorandom algorithm from the nes game itself
    //    return ((((value >> 9) & 1) ^ ((value >> 1) & 1)) << 15) | (value >> 1);
    //}

    private void initCurrent() {
        switch (current) {
            case "I":
                initI();
                break;
            case "O":
                initO();
                break;
            case "T":
                initT();
                break;
            case "S":
                initS();
                break;
            case "Z":
                initZ();
                break;
            case "J":
                initJ();
                break;
            case "L":
                initL();
                break;
        }
    }

    private String reroll() {
        int nextInt = random.nextInt(7); // I, O, T, S, Z, J, and L.
        return getPieceLetter(nextInt);
    }

    public String getPieceLetter(int i) {
        switch (i) {
            case 0:
                return "T";

            case 1:
                return "J";

            case 2:
                return "Z";

            case 3:
                return "O";

            case 4:
                return "S";

            case 5:
                return "L";

            case 6:
                return "I";
        }
        return "";
    }

    public int getLetterPiece(String piece) {
        switch (piece) {
            case "T":
                return 0;

            case "J":
                return 1;

            case "Z":
                return 2;

            case "O":
                return 3;

            case "S":
                return 4;

            case "L":
                return 5;

            case "I":
                return 6;
        }
        return -1;
    }

    public int getCurrentR() {
        return currentR;
    }

    public int getCurrentPosX() {
        return currentPosX;
    }

    public int getCurrentPosY() {
        return currentPosY;
    }

    public int drop() {
        initCurrent();
        for (int i = 0; i < 4; i++) {
            if (yPos[i] - 1 < 0 || (grid[yPos[i] - 1][xPos[i]]) != null) {
                return yPos[i];
            }
        }
        currentPosY--;
        initCurrent();
        return -1;
    }

    public boolean testEnd() {
        boolean end = true;
        for (int i = 0; i < 4; i++) {
            if (yPos[i] >= 0 && xPos[i] >= 0 && xPos[i] <= 9) {
                end = end && grid[yPos[i]][xPos[i]] == null;
            }
        }
        return !end;
    }

    public ArrayList<Integer> addCurr(Sprite[] sprites, int type) {
        ArrayList<Integer> linesBroken = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            grid[yPos[i]][xPos[i]] = sprites[i];
            grid[yPos[i]][xPos[i]].setOrigin(type, 2);
            boolean broken = true;
            for (int j = 0; j < 10; j++) {
                broken = broken && grid[yPos[i]][j] != null;
            }
            if (broken)
                linesBroken.add(yPos[i]);
        }
        return linesBroken;
    }

    public Sprite[][] getSprites() {
        return grid;
    }

    public void initI() {
        for (int i = 0; i < 4; i++) {
            if (currentR == 0 || currentR == 2) {
                xPos[i] = (currentPosX - 2) + i;
                yPos[i] = currentPosY;
            } else {
                xPos[i] = currentPosX;
                yPos[i] = (currentPosY - 1) + i;
            }
        }
    }

    public void initO() {
        int count = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                xPos[count] = (currentPosX - 1) + i;
                yPos[count] = (currentPosY - 1) + j;
                count++;
            }
        }
    }

    public void initT() {
        if (currentR == 0) {
            for (int i = 0; i < 3; i++) {
                xPos[i] = (currentPosX - 1) + i;
                yPos[i] = currentPosY;
            }
            xPos[3] = currentPosX;
            yPos[3] = currentPosY - 1;
        }
        if (currentR == 1) {
            for (int i = 0; i < 3; i++) {
                xPos[i] = currentPosX;
                yPos[i] = (currentPosY - 1) + i;
            }
            xPos[3] = currentPosX - 1;
            yPos[3] = currentPosY;
        }
        if (currentR == 2) {
            for (int i = 0; i < 3; i++) {
                xPos[i] = (currentPosX - 1) + i;
                yPos[i] = currentPosY;
            }
            xPos[3] = currentPosX;
            yPos[3] = currentPosY + 1;
        }
        if (currentR == 3) {
            for (int i = 0; i < 3; i++) {
                xPos[i] = currentPosX;
                yPos[i] = (currentPosY - 1) + i;
            }
            xPos[3] = currentPosX + 1;
            yPos[3] = currentPosY;
        }
    }

    public void initS() {
        int count = 0;
        if (currentR == 0 || currentR == 2) {
            for (int i = 0; i < 2; i++) {
                xPos[count] = (currentPosX - 1) + i;
                yPos[count] = currentPosY - 1;
                count++;
            }
            for (int j = 0; j < 2; j++) {
                xPos[count] = (currentPosX) + j;
                yPos[count] = currentPosY;
                count++;
            }
        } else {
            for (int i = 0; i < 2; i++) {
                xPos[count] = currentPosX;
                yPos[count] = (currentPosY) + i;
                count++;
            }
            for (int j = 0; j < 2; j++) {
                xPos[count] = currentPosX + 1;
                yPos[count] = (currentPosY - 1) + j;
                count++;
            }
        }
    }

    public void initZ() {
        int count = 0;
        if (currentR == 0 || currentR == 2) {
            for (int i = 0; i < 2; i++) {
                xPos[count] = (currentPosX - 1) + i;
                yPos[count] = currentPosY;
                count++;
            }
            for (int j = 0; j < 2; j++) {
                xPos[count] = (currentPosX) + j;
                yPos[count] = currentPosY - 1;
                count++;
            }
        } else {
            for (int i = 0; i < 2; i++) {
                xPos[count] = currentPosX;
                yPos[count] = (currentPosY - 1) + i;
                count++;
            }
            for (int j = 0; j < 2; j++) {
                xPos[count] = currentPosX + 1;
                yPos[count] = (currentPosY) + j;
                count++;
            }
        }
    }

    public void initJ() {
        if (currentR == 0) {
            for (int i = 0; i < 3; i++) {
                xPos[i] = (currentPosX - 1) + i;
                yPos[i] = currentPosY;
            }
            xPos[3] = currentPosX + 1;
            yPos[3] = currentPosY - 1;
        }
        if (currentR == 1) {
            for (int i = 0; i < 3; i++) {
                xPos[i] = currentPosX;
                yPos[i] = (currentPosY - 1) + i;
            }
            xPos[3] = currentPosX - 1;
            yPos[3] = currentPosY - 1;
        }
        if (currentR == 2) {
            for (int i = 0; i < 3; i++) {
                xPos[i] = (currentPosX - 1) + i;
                yPos[i] = currentPosY;
            }
            xPos[3] = currentPosX - 1;
            yPos[3] = currentPosY + 1;
        }
        if (currentR == 3) {
            for (int i = 0; i < 3; i++) {
                xPos[i] = currentPosX;
                yPos[i] = (currentPosY - 1) + i;
            }
            xPos[3] = currentPosX + 1;
            yPos[3] = currentPosY + 1;
        }
    }

    public void initL() {
        if (currentR == 0) {
            for (int i = 0; i < 3; i++) {
                xPos[i] = (currentPosX - 1) + i;
                yPos[i] = currentPosY;
            }
            xPos[3] = currentPosX - 1;
            yPos[3] = currentPosY - 1;
        }
        if (currentR == 1) {
            for (int i = 0; i < 3; i++) {
                xPos[i] = currentPosX;
                yPos[i] = (currentPosY - 1) + i;
            }
            xPos[3] = currentPosX - 1;
            yPos[3] = currentPosY + 1;
        }
        if (currentR == 2) {
            for (int i = 0; i < 3; i++) {
                xPos[i] = (currentPosX - 1) + i;
                yPos[i] = currentPosY;
            }
            xPos[3] = currentPosX + 1;
            yPos[3] = currentPosY + 1;
        }
        if (currentR == 3) {
            for (int i = 0; i < 3; i++) {
                xPos[i] = currentPosX;
                yPos[i] = (currentPosY - 1) + i;
            }
            xPos[3] = currentPosX + 1;
            yPos[3] = currentPosY - 1;
        }
    }

    public boolean right() {
        boolean right = checkRight();
        if (right) {
            currentPosX++;
            initCurrent();
        }
        return right;
    }

    public boolean checkRight() {
        initCurrent();
        for (int i = 0; i < 4; i++) {
            if (xPos[i] + 1 > 9 || (grid[yPos[i]][xPos[i] + 1]) != null) {
                return false;
            }
        }
        return true;
    }

    public boolean left() {
        boolean left = checkLeft();
        if (left) {
            currentPosX--;
            initCurrent();
        }
        return left;
    }

    public boolean checkLeft() {
        initCurrent();
        for (int i = 0; i < 4; i++) {
            if (xPos[i] - 1 < 0 || (grid[yPos[i]][xPos[i] - 1]) != null) {
                return false;
            }
        }
        return true;
    }

    public void rotateCW() {
        int oldR = currentR;
        if (currentR == 3)
            currentR = 0;
        else
            currentR++;
        if (!checkRotation()) {
            currentR = oldR;
        }
    }

    public void rotateCCW() {
        int oldR = currentR;
        if (currentR == 0)
            currentR = 3;
        else
            currentR--;
        if (!checkRotation()) {
            currentR = oldR;
        }
    }

    private boolean checkRotation() {
        initCurrent();
        for (int i = 0; i < 4; i++) {
            if (xPos[i] < 0 || xPos[i] > 9 || yPos[i] < 0 || (grid[yPos[i]][xPos[i]]) != null) {
                return false;
            }
        }
        return true;
    }

    public void removeSprite(int x, int y) {
        grid[y][x] = null;
    }

    public void moveLines(ArrayList<Integer> lines) {
        int smallest = 30;
        for (int j : lines) {
            if (j < smallest)
                smallest = j;
        }
        moveLines(smallest);
    }

    private void moveLines(int start) {
        for (int i = start; i < grid.length - 1; i++) {
            int nextline = i + 1;
            boolean empty = true;
            while (empty) {
                for (int j = 0; j < grid[nextline].length; j++) {
                    if (grid[nextline][j] != null) {
                        empty = false;
                    }
                }
                if (empty) {
                    nextline++;
                    if (nextline == 22) {
                        empty = false;
                    }
                }
            }
            if (nextline == 22) {
                i = grid.length - 1;
            } else {
                grid[i] = grid[nextline];
                for (int j = 0; j < grid[i].length; j++) {
                    if (grid[i][j] != null) {
                        grid[i][j].translateY(-BLOCK_SIZE * (nextline - i));
                    }
                }
                grid[nextline] = new Sprite[10];
            }
        }
    }

    public boolean testFast() {
        boolean empty = true;
        for (int i = 14; i < grid.length; i++) {
            for (int j = 0; j < grid[14].length; j++) {
                if (grid[14][j] != null) {
                    empty = false;
                }
            }
        }
        return !empty;
    }
}