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

import java.util.Random;
import javax.media.opengl.glu.GLU;

public class AnimGLEventListener extends AnimListener {

    public class HeightmapGenerator {
        private static final Random random = new Random();

        public static int[] generateHeightmap(int MaxX, int MaxHeight) {
            int[] heightmap = new int[MaxX + 1];

            // Initialize endpoints
            heightmap[0] = random.nextInt(MaxHeight / 4) + MaxHeight / 2; // Left endpoint
            heightmap[MaxX] = random.nextInt(MaxHeight / 4) + MaxHeight / 2; // Right endpoint

            // Perform midpoint displacement
            midpointDisplacement(heightmap, 0, MaxX, MaxHeight / 2);

            // Add mountains to the heightmap
            addMountains(heightmap, MaxHeight);

            // Apply smoothing filter to balance spikiness
            smoothHeightmap(heightmap, 5);

            return heightmap;
        }

        private static void midpointDisplacement(int[] heightmap, int left, int right, int range) {
            if (left + 1 == right || range <= 0) {
                return; // No more points to divide or range is invalid
            }

            int midpoint = (left + right) / 2;

            // Calculate midpoint height
            int averageHeight = (heightmap[left] + heightmap[right]) / 2;
            int displacement = random.nextInt(Math.max(1, range)) - range / 2; // Ensure range is positive
            heightmap[midpoint] = Math.max(0, averageHeight + displacement);

            // Reduce the range of displacement for finer details
            int newRange = range / 2;

            // Recursively apply to left and right segments
            midpointDisplacement(heightmap, left, midpoint, newRange);
            midpointDisplacement(heightmap, midpoint, right, newRange);
        }


        private static void addMountains(int[] heightmap, int MaxHeight) {
            int length = heightmap.length;

            // Add a few mountain peaks
            int numMountains = random.nextInt(3) + 2; // 2 to 4 mountains
            for (int i = 0; i < numMountains; i++) {
                int peakPosition = random.nextInt(length / 2) + length / 4; // Avoid peaks at edges
                int peakHeight = MaxHeight - random.nextInt(MaxHeight / 3);

                // Spread the mountain height to surrounding points
                for (int x = Math.max(0, peakPosition - 10); x <= Math.min(length - 1, peakPosition + 10); x++) {
                    int distance = Math.abs(x - peakPosition);
                    int heightContribution = (int) (peakHeight * (1.0 - (distance / 10.0)));
                    heightmap[x] = Math.min(MaxHeight, heightmap[x] + heightContribution);
                }
            }
        }

        private static void smoothHeightmap(int[] heightmap, int iterations) {
            int length = heightmap.length;

            for (int iter = 0; iter < iterations; iter++) {
                int[] smoothed = new int[length];

                for (int i = 1; i < length - 1; i++) {
                    // Average the current point with its neighbors
                    smoothed[i] = (heightmap[i - 1] + heightmap[i] + heightmap[i + 1]) / 3;
                }

                // Preserve endpoints
                smoothed[0] = heightmap[0];
                smoothed[length - 1] = heightmap[length - 1];

                // Copy smoothed values back to heightmap
                System.arraycopy(smoothed, 0, heightmap, 0, length);
            }
        }
    }


    String textureName = "Back.png";
    TextureReader.Texture texture;
    int textureIndex[] = new int[1];
    int[] heightmap = new int[250];
    int projectileX = 125;
    int projectileY = 250;



    public void FillHeightMap(){
        heightmap = HeightmapGenerator.generateHeightmap(250, 150);

    }
    /*
     5 means gun in array pos
     x and y coordinate for gun
     */
    public void init(GLAutoDrawable gld) {
        FillHeightMap();
        GL gl = gld.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);    //This Will Clear The Background Color To Black


        gl.glEnable(GL.GL_TEXTURE_2D);  // Enable Texture Mapping
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        //number of textures,array to hold the indeces
        gl.glGenTextures(1, textureIndex, 0);


        try {
            texture = TextureReader.readTexture(assetsFolderName + "//" + textureName , true);
            gl.glBindTexture(GL.GL_TEXTURE_2D, textureIndex[0]);

//                mipmapsFromPNG(gl, new GLU(), texture[i]);
            new GLU().gluBuild2DMipmaps(
                    GL.GL_TEXTURE_2D,
                    GL.GL_RGBA, // Internal Texel Format,
                    texture.getWidth(), texture.getHeight(),
                    GL.GL_RGBA, // External format from image,
                    GL.GL_UNSIGNED_BYTE,
                    texture.getPixels() // Imagedata
            );
        } catch( IOException e ) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void display(GLAutoDrawable gld) {

        GL gl = gld.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);       //Clear The Screen And The Depth Buffer
        gl.glLoadIdentity();
        gl.glOrtho(0, 250.0, 0, 250.0, -1, 1);
        DrawBackground(gl);

        DrawRocket(gl,projectileX,projectileY,1);

        projectileY--;

        if (projectileY <= heightmap[projectileX]) {
            System.out.println("Destroy Terrain");
            destroyTerrain(projectileX, 22);
            projectileY = 250;
        }
        drawTerrain(gl);
    }





    public void DrawRocket(GL gl, float x, float y, float scale) {
        gl.glPushMatrix(); // Save the current transformation matrix
        gl.glTranslatef(x, y, 0); // Move the rocket to the specified position
        gl.glScalef(scale, scale, 1); // Scale the rocket to the desired size

        // Draw the rocket body (rectangle)
        gl.glColor3f(0.8f, 0.1f, 0.1f); // Red color
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(-0.1f, -0.5f); // Bottom-left
        gl.glVertex2f(0.1f, -0.5f);  // Bottom-right
        gl.glVertex2f(0.1f, 0.5f);   // Top-right
        gl.glVertex2f(-0.1f, 0.5f);  // Top-left
        gl.glEnd();

        // Draw the rocket nose (triangle)
        gl.glColor3f(1.0f, 0.5f, 0.0f); // Orange color
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex2f(-0.1f, 0.5f); // Bottom-left of the nose
        gl.glVertex2f(0.1f, 0.5f);  // Bottom-right of the nose
        gl.glVertex2f(0.0f, 0.7f);  // Top of the nose
        gl.glEnd();

        // Draw the fins (triangles)
        gl.glColor3f(0.0f, 0.0f, 1.0f); // Blue color
        gl.glBegin(GL.GL_TRIANGLES);
        // Left fin
        gl.glVertex2f(-0.1f, -0.3f);
        gl.glVertex2f(-0.2f, -0.5f);
        gl.glVertex2f(-0.1f, -0.5f);

        // Right fin
        gl.glVertex2f(0.1f, -0.3f);
        gl.glVertex2f(0.2f, -0.5f);
        gl.glVertex2f(0.1f, -0.5f);
        gl.glEnd();

        // Draw exhaust flames (optional, dynamic animation possible)
        gl.glColor3f(1.0f, 0.8f, 0.0f); // Yellow flame
        gl.glBegin(GL.GL_TRIANGLES);
        gl.glVertex2f(-0.05f, -0.5f);
        gl.glVertex2f(0.05f, -0.5f);
        gl.glVertex2f(0.0f, -0.7f);
        gl.glEnd();

        gl.glPopMatrix(); // Restore the previous transformation matrix
    }


    public void drawTerrain(GL gl) {
        gl.glBegin(GL.GL_POLYGON);
        gl.glColor3d(0.5, 0.3, 0.2); // Brownish color for ground

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


    public void destroyTerrain(int cx, int radius) {
        for (int x = Math.max(0, cx - radius); x < Math.min(heightmap.length, cx + radius); x++) {
            int distance = Math.abs(x - cx);
            int destructionHeight = (int) Math.sqrt(Math.pow(radius, 2) - Math.pow(distance, 2));
            heightmap[x] = Math.max(0, heightmap[x] - destructionHeight); // Lower the height
        }
    }




    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public void DrawBackground(GL gl){
        gl.glEnable(GL.GL_BLEND);	// Turn Blending On
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureIndex[0]);

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

        gl.glDisable(GL.GL_BLEND);
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent ke) {
           }

    @Override
    public void keyReleased(KeyEvent ke) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
