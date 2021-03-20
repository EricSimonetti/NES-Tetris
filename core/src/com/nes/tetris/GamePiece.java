package com.nes.tetris;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class GamePiece {
    private Sprite[] blocks;
    private TextureAtlas textureAtlas;
    private int type = 0;
    private final int BLOCK_SIZE = 24;

    public GamePiece(String piece, int level, TextureAtlas textureAtlas){
        this.textureAtlas = textureAtlas;
        blocks = new Sprite[4];
        newPiece(piece, level);
    }

    public void newPiece(String piece, int level){
        String p;
        if(piece.equals("J")||piece.equals("S")) {
            p = "JS";
            type = 0;
        }
        else if(piece.equals("L")||piece.equals("Z")) {
            p = "LZ";
            type = 1;
        }
        else {
            p = "TOI";
            type = 2;
        }
        for(int i = 0; i<4; i++){
            blocks[i] = this.textureAtlas.createSprite(p+"-"+(level%10));
            blocks[i].setOrigin(type, 0);
        }
    }

    public void setLevel(int level){
        for(int i = 0; i<4; i++) {
            int tempx = (int)blocks[i].getX();
            int tempy = (int)blocks[i].getY();
            int type = (int)blocks[i].getOriginX();
            String p = "";
            if(type == 0) {
                p = "JS";
            }
            else if(type == 1) {
                p = "LZ";
            }
            else {
                p = "TOI";
            }
            blocks[i] = this.textureAtlas.createSprite(p+"-"+(level%10));
            blocks[i].setPosition(tempx, tempy);
            blocks[i].setOrigin(type, 0);
        }
    }

    public Sprite[] getBlocks(){
        return blocks;
    }

    public void draw(String piece, int x, int y, int r, SpriteBatch batch){
        switch(piece){
            case "I":  drawI(x, y, r, batch);
                break;
            case "O":  drawO(x, y, r, batch);
                break;
            case "T":  drawT(x, y, r, batch);
               break;
            case "S":  drawS(x, y, r, batch);
                break;
            case "Z":  drawZ(x, y, r, batch);
                break;
            case "J":  drawJ(x, y, r, batch);
                break;
            case "L":  drawL(x, y, r, batch);
                break;
        }
    }

    private void drawI(int x, int y, int r, SpriteBatch batch){
        for(int i = 0; i<4; i++){
            if(r == 0 || r == 2) {
                blocks[i].setPosition(x + (BLOCK_SIZE * i), y);
                blocks[i].draw(batch);
            }
            else {
                int thisx = x;
                int thisy = y + (BLOCK_SIZE * i);
                if(thisy<=551) {
                    blocks[i].setPosition(thisx, thisy);
                    blocks[i].draw(batch);
                }
            }
        }
    }

    private void drawO(int x, int y, int r, SpriteBatch batch){
        int count = 0;
        for(int i = 0; i<2; i++){
            for(int j = 0; j<2; j++){
                blocks[count].setPosition(x+(BLOCK_SIZE*i), y+(BLOCK_SIZE*j));
                blocks[count].draw(batch);
                count++;
            }
        }
    }

    private void drawT(int x, int y, int r, SpriteBatch batch){
        if(r == 0){
            for(int i = 0; i<3; i++){
                blocks[i].setPosition(x+(BLOCK_SIZE*i), y+BLOCK_SIZE);
                blocks[i].draw(batch);
            }
            blocks[3].setPosition(x+BLOCK_SIZE, y);
            blocks[3].draw(batch);
        }
        if(r == 1){
            for(int i = 0; i<3; i++){
                int thisx = x+BLOCK_SIZE;
                int thisy = y+BLOCK_SIZE*i;
                if(thisy<=551) {
                    blocks[i].setPosition(thisx, thisy);
                    blocks[i].draw(batch);
                }
            }
            blocks[3].setPosition(x, y+BLOCK_SIZE);
            blocks[3].draw(batch);
        }
        if(r == 2){
            for(int i = 0; i<3; i++){
                blocks[i].setPosition(x+(BLOCK_SIZE*i), y+BLOCK_SIZE);
                blocks[i].draw(batch);
            }
            int thisx = x+BLOCK_SIZE;
            int thisy = y+BLOCK_SIZE*2;
            if(thisy<=551) {
                blocks[3].setPosition(thisx, thisy);
                blocks[3].draw(batch);
            }
        }
        if(r == 3){
            for(int i = 0; i<3; i++){
                int thisx = x+BLOCK_SIZE;
                int thisy = y+BLOCK_SIZE*i;
                if(thisy<=551) {
                    blocks[i].setPosition(thisx, thisy);
                    blocks[i].draw(batch);
                }
            }
            blocks[3].setPosition(x+BLOCK_SIZE*2, y+BLOCK_SIZE);
            blocks[3].draw(batch);
        }
    }

    private void drawS(int x, int y, int r, SpriteBatch batch){
        int count = 0;
        if(r == 0 || r == 2) {
            for (int i = 0; i < 2; i++) {
                blocks[count].setPosition(x + (BLOCK_SIZE * i), y);
                blocks[count].draw(batch);
                count++;
            }
            for (int j = 0; j < 2; j++) {
                blocks[count].setPosition(x + (BLOCK_SIZE * (j + 1)), y + BLOCK_SIZE);
                blocks[count].draw(batch);
                count++;
            }
        }
        else {
            for (int i = 0; i < 2; i++) {
                int thisx = x+BLOCK_SIZE;
                int thisy = y+ (BLOCK_SIZE * (i+1));
                if(thisy<=551) {
                    blocks[count].setPosition(thisx, thisy);
                    blocks[count].draw(batch);
                }
                count++;
            }
            for (int j = 0; j < 2; j++) {
                blocks[count].setPosition(x + BLOCK_SIZE*2, y+ (BLOCK_SIZE *j));
                blocks[count].draw(batch);
                count++;
            }
        }
    }

    private void drawZ(int x, int y, int r, SpriteBatch batch){
        int count = 0;
        if(r == 0 || r == 2) {
            for (int i = 0; i < 2; i++) {
                blocks[count].setPosition(x + (BLOCK_SIZE * i), y + BLOCK_SIZE);
                blocks[count].draw(batch);
                count++;
            }
            for (int j = 0; j < 2; j++) {
                blocks[count].setPosition(x + (BLOCK_SIZE * (j + 1)), y);
                blocks[count].draw(batch);
                count++;
            }
        }
        else {
            for (int i = 0; i < 2; i++) {
                blocks[count].setPosition(x+BLOCK_SIZE, y+ (BLOCK_SIZE * (i)));
                blocks[count].draw(batch);
                count++;
            }
            for (int j = 0; j < 2; j++) {
                int thisx = x + BLOCK_SIZE * 2;
                int thisy = y + (BLOCK_SIZE * (j + 1));
                if(thisy<=551) {
                    blocks[count].setPosition(thisx, thisy);
                    blocks[count].draw(batch);
                }
                count++;
            }
        }
    }

    private void drawJ(int x, int y, int r, SpriteBatch batch){
        if(r == 0){
            for(int i = 0; i<3; i++){
                blocks[i].setPosition(x+(BLOCK_SIZE*i), y+BLOCK_SIZE);
                blocks[i].draw(batch);
            }
            blocks[3].setPosition(x+BLOCK_SIZE*2, y);
            blocks[3].draw(batch);
        }
        if(r == 1){
            for(int i = 0; i<3; i++){
                int thisx = x+BLOCK_SIZE;
                int thisy = y+BLOCK_SIZE*i;
                if(thisy<=551) {
                    blocks[i].setPosition(thisx, thisy);
                    blocks[i].draw(batch);
                }
            }
            blocks[3].setPosition(x, y);
            blocks[3].draw(batch);
        }
        if(r == 2){
            for(int i = 0; i<3; i++){
                blocks[i].setPosition(x+(BLOCK_SIZE*i), y+BLOCK_SIZE);
                blocks[i].draw(batch);
            }
            int thisx = x;
            int thisy = y+BLOCK_SIZE*2;
            if(thisy<=551) {
                blocks[3].setPosition(thisx, thisy);
                blocks[3].draw(batch);
            }
        }
        if(r == 3){
            int thisx, thisy;
            for(int i = 0; i<3; i++){
                thisx = x+BLOCK_SIZE;
                thisy = y+BLOCK_SIZE*i;
                if(thisy<=551) {
                    blocks[i].setPosition(thisx, thisy);
                    blocks[i].draw(batch);
                }
            }
            thisx = x+BLOCK_SIZE*2;
            thisy = y+BLOCK_SIZE*2;
            if(thisy<=551) {
                blocks[3].setPosition(thisx, thisy);
                blocks[3].draw(batch);
            }
        }
    }

    private void drawL(int x, int y, int r, SpriteBatch batch){
        if(r == 0){
            for(int i = 0; i<3; i++){
                blocks[i].setPosition(x+(BLOCK_SIZE*i), y+BLOCK_SIZE);
                blocks[i].draw(batch);
            }
            blocks[3].setPosition(x, y);
            blocks[3].draw(batch);
        }
        if(r == 1){
            for(int i = 0; i<3; i++){
                int thisx = x+BLOCK_SIZE;
                int thisy = y+BLOCK_SIZE*i;
                if(thisy<=551) {
                    blocks[i].setPosition(thisx, thisy);
                    blocks[i].draw(batch);
                }
            }
            int thisx = x;
            int thisy = y+BLOCK_SIZE*2;
            if(thisy<=551) {
                blocks[3].setPosition(thisx, thisy);
                blocks[3].draw(batch);
            }
        }
        if(r == 2){
            for(int i = 0; i<3; i++){
                blocks[i].setPosition(x+(BLOCK_SIZE*i), y+BLOCK_SIZE);
                blocks[i].draw(batch);
            }
            int thisx = x+BLOCK_SIZE*2;
            int thisy = y+BLOCK_SIZE*2;
            if(thisy<=551) {
                blocks[3].setPosition(thisx, thisy);
                blocks[3].draw(batch);
            }
        }
        if(r == 3){
            int thisx, thisy;
            for(int i = 0; i<3; i++){
                thisx = x+BLOCK_SIZE;
                thisy = y+BLOCK_SIZE*i;
                if(thisy<=551) {
                    blocks[i].setPosition(thisx, thisy);
                    blocks[i].draw(batch);
                }
            }
            blocks[3].setPosition(x+BLOCK_SIZE*2, y);
            blocks[3].draw(batch);
        }
    }

    public void drop(){
        for(int i = 0; i<4; i++){
            blocks[i].translateY(-BLOCK_SIZE);
        }
    }

    public void left(){
        for(int i = 0; i<4; i++){
            blocks[i].translateX(-BLOCK_SIZE);
        }
    }

    public void right(){
        for(int i = 0; i<4; i++){
            blocks[i].translateX(-BLOCK_SIZE);
        }
    }

    public int getType(){
        return type;
    }

    public void dispose(){
        blocks = null;
        textureAtlas.dispose();
    }
}
