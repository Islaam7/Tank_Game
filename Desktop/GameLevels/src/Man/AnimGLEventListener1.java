package Man;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Texture.TextureReader;
import java.awt.event.*;
import java.io.IOException;
import javax.media.opengl.*;

import java.util.BitSet;
import java.util.Random;
import javax.media.opengl.glu.GLU;

public class AnimGLEventListener1 extends AnimListener {

    int indexOfMax=50;
    double maxHight=0;
    double minHight=0;
    double [] MountainPoints = new double[100];

    String[] textureNames = {"Man1.png", "Man2.png", "Man3.png", "Man4.png", "11.png", "18.png", "Cloud.jpeg"};
    TextureReader.Texture[] texture = new TextureReader.Texture[textureNames.length];
    int[] textures = new int[textureNames.length];
    /*
     5 means gun in array pos
     x and y coordinate for gun
     */
    public void init(GLAutoDrawable gld) {

        GL gl = gld.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);    //This Will Clear The Background Color To Black

        gl.glEnable(GL.GL_TEXTURE_2D);  // Enable Texture Mapping
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glGenTextures(textureNames.length, textures, 0);

        for(int i = 0; i < textureNames.length; i++){
            try {
                texture[i] = TextureReader.readTexture(assetsFolderName + "//" + textureNames[i] , true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);

                //mipmapsFromPNG(gl, new GLU(), texture[i]);
                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA, // Internal Texel Format,
                        texture[i].getWidth(), texture[i].getHeight(),
                        GL.GL_RGBA, // External format from image,
                        GL.GL_UNSIGNED_BYTE,
                        texture[i].getPixels() // Image data
                );
            } catch( IOException e ) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        {
            for (int i = 0; i < MountainPoints.length; i++) {
                double x;

                Random random = new Random();

                if (i < 50) {
                    x = (30 / (1 + Math.pow(Math.E, -(i * 0.2 - 5))) + 10);
                } else {
                    x = (-30 / (1 + Math.pow(Math.E, -(i * 0.2 - 15)))) + 50;
                }
                MountainPoints[i] = x;

                for (int j = 1; j < MountainPoints.length - 1; j++) {
                    double y = random.nextGaussian() * 4 + MountainPoints[j];

                    if (i % 5 == 0) {
                        MountainPoints[j] = y;
                    } else {
                        MountainPoints[j] = (MountainPoints[j - 1] + MountainPoints[j + 1]) / 2;
                    }
                }
                if (MountainPoints[i] >= maxHight) {
                    maxHight = MountainPoints[i];
                    indexOfMax = i + 1;
                }
                if (MountainPoints[i] <= minHight) {
                    minHight = MountainPoints[i];
                }
            }
        }
    }
    public void display(GLAutoDrawable gld) {
        GL gl = gld.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glEnable(GL.GL_BLEND);//Clear The Screen And The Depth Buffer
        gl.glLoadIdentity();
        gl.glColor3f(1, 1f, 1f);
        DrawBackground(gl);

        DrawMountain(gl);

    }
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
    public void DrawMountain(GL gl){
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glColor3f(0.5451f, 0.2706f, 0.0745f);
        gl.glBegin(GL.GL_POLYGON);
        gl.glVertex2d(-1,-1);
        for (int i = 0; i < MountainPoints.length ; i++) {
            gl.glVertex2d( (i*2.0 /(MountainPoints.length-1))-1, (MountainPoints[i]*2/(maxHight-minHight))-1);
        }
        gl.glVertex2d(1,-1);

        gl.glEnd();
        gl.glEnable(GL.GL_TEXTURE_2D);
    }
    public void DrawBackground(GL gl){
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[textures.length-1]);	// Turn Blending On

        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);
        // Front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }
    /*
     * KeyListener
     */
    public BitSet keyBits = new BitSet(256);
    @Override
    public void keyPressed(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.set(keyCode);
    }
    @Override
    public void keyReleased(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.clear(keyCode);
    }
    @Override
    public void keyTyped(final KeyEvent event) {
        // don't care
    }
    public boolean isKeyPressed(final int keyCode) {
        return keyBits.get(keyCode);
    }
}