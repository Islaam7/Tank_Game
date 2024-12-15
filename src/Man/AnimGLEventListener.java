package Man;

import Texture.TextureReader;
import java.awt.event.*;
import java.io.IOException;
import javax.media.opengl.*;

import java.util.Random;
import javax.media.opengl.glu.GLU;
import Man.Game.*;


public class AnimGLEventListener extends AnimListener {


    String textureNames[] = {"Clouds.jpg","Tank3.png","TankCanon.png"};
    TextureReader.Texture texture[] = new TextureReader.Texture[textureNames.length];
    int textures[] = new int[textureNames.length];
    int projectileX = 125;
    int projectileY = 250;
    int Difficulty = 0;
    boolean inGame = true;
    boolean Paused = false;
    int MAX_TERRAIN_WIDTH = 500;
    int MAX_TERRAIN_HEIGHT= 500;
    Game game;
    Player[] players;
    Player currentplayer;


    public void init(GLAutoDrawable glAutoDrawable) {

        GL gl = glAutoDrawable.getGL();
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, 500.0, 0, 500.0, -1, 1);

        game = new Game(gl,0, 0);
        players = game.players;
        currentplayer = players[game.turn];


        //number of textures,array to hold the indeces
        gl.glEnable(GL.GL_TEXTURE_2D);  // Enable Texture Mapping
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glGenTextures(textureNames.length, textures, 0);

        for (int i = 0; i < textureNames.length; i++) {
            try {
                texture[i] = TextureReader.readTexture(assetsFolderName + "//" + textureNames[i], true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);

//                mipmapsFromPNG(gl, new GLU(), texture[i]);
                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA, // Internal Texel Format,
                        texture[i].getWidth(), texture[i].getHeight(),
                        GL.GL_RGBA, // External format from image,
                        GL.GL_UNSIGNED_BYTE,
                        texture[i].getPixels() // Imagedata
                );
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

    public void display(GLAutoDrawable gld) {

        GL gl = gld.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);       //Clear The Screen And The Depth Buffer

        if (inGame) {

            DrawBackground(gl);
            drawTerrain(gl);

            players[0].drawTank(gl,textures,players[0].posX,40f,game.heightmap,players[0].angle);
            players[1].drawTank(gl,textures,players[1].posX,40f,game.heightmap,players[1].angle);

        }

    }


    public void drawTerrain(GL gl) {
        int[] heightmap = game.heightmap;
        gl.glBegin(GL.GL_POLYGON);
        gl.glColor3d(0.5, 0.25, 0);

        // Define the bottom edge of the polygon (ground level)
        for (int x = 0; x < heightmap.length; x++) {
            gl.glVertex2f(x, 0); // Ground level

        }

        // Define the top edge of the polygon (terrain heights)
        for (int x = heightmap.length - 1; x >= 0; x--) {
            gl.glVertex2f(x, heightmap[x]); // Terrain height
        }

        gl.glEnd();
    }



    public void DeleteGame(){
        game.CleanUp();
        game = null;
        inGame = false;
    }



    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public void DrawBackground(GL gl) {
        gl.glEnable(GL.GL_BLEND); // Turn Blending On
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);

        // Reset the color to white
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        gl.glBegin(GL.GL_POLYGON);
        // Front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f); // Adjust coordinates to fit your viewport
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(MAX_TERRAIN_WIDTH, 0.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(MAX_TERRAIN_WIDTH, MAX_TERRAIN_HEIGHT, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(0.0f, MAX_TERRAIN_HEIGHT, 0.0f);
        gl.glEnd();

        gl.glDisable(GL.GL_BLEND);
    }



    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();
        if (!currentplayer.isAi()) {
            if (key == KeyEvent.VK_LEFT) {
                currentplayer.setAngle(currentplayer.angle + 1);
                System.out.println(currentplayer.angle);

            } else if (key == KeyEvent.VK_RIGHT) {
                currentplayer.setAngle(currentplayer.angle - 1);
                System.out.println(currentplayer.angle);
            } else if (key == KeyEvent.VK_SPACE) {
                {
                    game.processMove(currentplayer.angle, currentplayer.power);
                    currentplayer = players[game.turn];
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

}
