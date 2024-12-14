package Man;

import Texture.TextureReader;
import java.awt.event.*;
import java.io.IOException;
import javax.media.opengl.*;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.glu.GLU;

public class AnimGLEventListener4 extends AnimListener {

    List<Point> points = new ArrayList<>();

    String[] textureNames = {"Man1.png", "Man2.png", "Man3.png", "Man4.png", "11.png", "18.png", "Cloud.jpeg"};
    TextureReader.Texture[] texture = new TextureReader.Texture[textureNames.length];
    int[] textures = new int[textureNames.length];

    public void init(GLAutoDrawable gld) {

        GL gl = gld.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);    // This will clear the background color to white
        gl.glOrtho(-400, 400, -300, 300, -1, 1);
        gl.glEnable(GL.GL_TEXTURE_2D);  // Enable texture mapping
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glGenTextures(textureNames.length, textures, 0);

        // Load textures
        for (int i = 0; i < textureNames.length; i++) {
            try {
                texture[i] = TextureReader.readTexture(assetsFolderName + "//" + textureNames[i], true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);

                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA, // Internal Texel Format
                        texture[i].getWidth(), texture[i].getHeight(),
                        GL.GL_RGBA, // External format from image
                        GL.GL_UNSIGNED_BYTE,
                        texture[i].getPixels() // Image data
                );
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        {
            points.add(new Point(-1.0, -1.0));
            points.add(new Point(-1.0, -0.45));
            points.add(new Point(-0.975, -0.475));
            points.add(new Point(-0.95, -0.45));
            points.add(new Point(-0.925, -0.425));
            points.add(new Point(-0.9, -0.5));
            points.add(new Point(-0.875, -0.475));
            points.add(new Point(-0.85, -0.425));
            points.add(new Point(-0.825, -0.375));
            points.add(new Point(-0.82, -0.22));
            points.add(new Point(-0.8, -0.15));
            points.add(new Point(-0.775, -0.225));
            points.add(new Point(-0.75, -0.25));
            points.add(new Point(-0.725, -0.275));
            points.add(new Point(-0.7, -0.3));
            points.add(new Point(-0.675, -0.275));
            points.add(new Point(-0.65, -0.225));
            points.add(new Point(-0.625, -0.175));
            points.add(new Point(-0.6, -0.15));
            points.add(new Point(-0.575, -0.125));
            points.add(new Point(-0.55, -0.1));
            points.add(new Point(-0.525, -0.075));
            points.add(new Point(-0.5, -0.05));
            points.add(new Point(-0.475, -0.025));
            points.add(new Point(-0.45, 0));
            points.add(new Point(-0.425, 0.025));
            points.add(new Point(-0.4, -0.18));
            points.add(new Point(-0.375, -0.125));
            points.add(new Point(-0.35, -0.1));
            points.add(new Point(-0.325, -0.075));
            points.add(new Point(-0.3, -0.13));
            points.add(new Point(-0.275, -0.075));
            points.add(new Point(-0.25, 0));
            points.add(new Point(-0.225, 0.025));
            points.add(new Point(-0.2, 0.4));
            points.add(new Point(-0.175, 0.325));
            points.add(new Point(-0.15, 0.6));
            points.add(new Point(-0.125, 0.175));
            points.add(new Point(-0.1, 0.65));
            points.add(new Point(-0.05, 0.6));
            points.add(new Point(0.0, 0.5));
            points.add(new Point(0.05, 0.525));
            points.add(new Point(0.1, 0.6));
            points.add(new Point(0.15, 0.6));
            points.add(new Point(0.2, 0.62));
            points.add(new Point(0.25, 0.635));
            points.add(new Point(0.3, 0.65));
            points.add(new Point(0.35, 0.675));
            points.add(new Point(0.4, 0.6));
            points.add(new Point(0.45, 0.525));
            points.add(new Point(0.5, 0.3));
            points.add(new Point(0.55, 0.225));
            points.add(new Point(0.6, 0.12));
            points.add(new Point(0.65, 0.075));
            points.add(new Point(0.7, 0.0));
            points.add(new Point(0.75, -0.01));
            points.add(new Point(0.8, -0.02));
            points.add(new Point(0.85, -0.075));
            points.add(new Point(0.9, -0.15));
            points.add(new Point(0.95, -0.2));
            points.add(new Point(1.0, -0.25));
            points.add(new Point(1, -1));
        }
    }
    @Override
    public void display(GLAutoDrawable gld) {
        GL gl = gld.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);// Clear the screen and the depth buffer
        gl.glLoadIdentity();

        gl.glColor3f(1, 1, 1);
        DrawBackground(gl);

        gl.glColor3f(0.5451f, 0.2706f, 0.0745f);
        gl.glDisable(GL.GL_TEXTURE_2D);


        gl.glBegin(GL.GL_POLYGON);
        for (Point p : points) {
            gl.glVertex2d(p.x, p.y);
        }
        gl.glEnd();

        gl.glEnable(GL.GL_TEXTURE_2D);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {}

    @Override
    public void displayChanged(GLAutoDrawable glAutoDrawable, boolean b, boolean b1) {}

    public void DrawBackground(GL gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[textures.length - 1]); // Turn Blending On
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
    @Override
    public void keyPressed(final KeyEvent event) {
    }
    @Override
    public void keyReleased(final KeyEvent event) {
    }
    @Override
    public void keyTyped(final KeyEvent event) {
        // don't care
    }
}
