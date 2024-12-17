package Man;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package project;

import Texture.TextureReader;
import java.awt.event.*;
import java.io.IOException;
import javax.media.opengl.*;

import java.util.BitSet;
import java.util.Random;
import javax.media.opengl.glu.GLU;

public class AnimGLEventListener1 extends AnimListener {

    double yBullet=0,xBullet=0;  boolean bom = false ,isBom = false,don =false; boolean push =false; int  k =4;
    int indexOfMax=50; double maxHight=0;double minHight=0;float slope;
    double[] calme= new double[110];
    int animationIndex = 0; boolean tank1 =true;boolean tank2 =false;
    int monsterIndex = 4;
    int maxWidth = 109;
    int maxHeight = 110;
    int xTank = 80, yTank = maxHeight/2; int xTank2 = 10;
    int x1 = maxWidth/3, y1 = maxHeight-10;
    double angle1=45,angle2=45;
    String textureNames[] = {"tank.png","m1.png","md3.png","spinner.gif","1.png","2.png","4.png","4.png","5.png","6.png","7.png","8.png","9.png","10.png","11.png","12.png","13.png","14.png","15.png","16.png","arrow.png","bullet.png","game_background.jpg"};
    TextureReader.Texture texture[] = new TextureReader.Texture[textureNames.length];
    int textures[] = new int[textureNames.length];
    final float g =9.8f;
    final double PI =Math.PI;
    double t=0;
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

//                mipmapsFromPNG(gl, new GLU(), texture[i]);
                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA, // Internal Texel Format,
                        texture[i].getWidth(), texture[i].getHeight(),
                        GL.GL_RGBA, // External format from image,
                        GL.GL_UNSIGNED_BYTE,
                        texture[i].getPixels() // Imagedata
                );
            } catch( IOException e ) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
        {


            for (int i = 0; i < calme.length; i++) {
                float sd = 4f;
                float yy = 0;
                Random random = new Random();

                if (i < 50) {
                    yy = (float) (30 / (1 + Math.pow(Math.E, -(i * .2 - 5)))) + 10;
                } else {
                    yy = (float) (-30 / (1 + Math.pow(Math.E, -(i * .2 - 15)))) + 50;
                }

                calme[i] = yy;

                for (int j = 1; j < calme.length - 1; j++) {
                    float ff = (float) (random.nextGaussian() * sd + calme[j]);

                    if (i % 5 == 0) {
                        calme[j] = ff;
                    } else {
                        calme[j] = (calme[j - 1] + calme[j + 1]) / 2;
                    }
                }
                if (calme[i] >= maxHight) {
                    maxHight = calme[i];
                    indexOfMax = i + 1;
                }
                if (calme[i] <= minHight) {
                    minHight = calme[i];

                }
            }



        }
    }

    public void display(GLAutoDrawable gld) {
        y1-=.05;
        GL gl = gld.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glEnable(GL.GL_BLEND);//Clear The Screen And The Depth Buffer
        gl.glLoadIdentity();
        gl.glColor3f(1, 1f, 1f);
        DrawBackground(gl);
        handleKeyPress();
        DrawLives(gl);
        DrawTopBar(gl);
        animationIndex = animationIndex % 4;

//        DrawGraph(gl);
        gl.glColor3f(1, 1f, 1f);


        TanKr(gl, (float) (-angle1),xTank);
        TanKl(gl, (float) (-angle2),xTank2);
        if(tank2){ DrawSprite(gl,xTank2,(float) (((calme[xTank2+8]-minHight)/(maxHight-minHight)+.05+.02*Math.sin(t))*maxHeight),textureNames.length-3,.5f,1);}
        if(tank1){ DrawSprite(gl,xTank,(float) (((calme[xTank+8]-minHight)/(maxHight-minHight)+.05+.02*Math.sin(t))*maxHeight),textureNames.length-3,.5f,1);}
        if(isKeyPressed(KeyEvent.VK_SPACE)||isBom){
            if(tank1){
                isBom =true;
                t+=.05;
                Bullet((int)-angle1+180,40,xTank,(float) (((calme[xTank+8]-minHight)/(maxHight-minHight)-0.03)*maxHeight));

                DrawSprite(gl,(float) xBullet,(float)yBullet,textureNames.length-2,.5f,0);
            } else if (tank2){
                isBom =true;
                t+=.05;
                Bullet((int)angle2,40,xTank2,(float) (((calme[xTank2+8]-minHight)/(maxHight-minHight)-0.03)*maxHeight));

                DrawSprite(gl,(float) xBullet,(float)yBullet,textureNames.length-2,.5f,0);
            }
        }
        double distance = sqrdDistance((int) xBullet,(int)(((calme[(int)xBullet+5]-minHight)/(maxHight-minHight)-0.08)*maxHeight),(int) xBullet, (int) yBullet);
        System.out.println(distance+"fdj;klad;");

        if(distance<=10){
            calme[(int)xBullet+2]-=(maxHight-minHight)*.02;
            calme[(int)xBullet+8]-=(maxHight-minHight)*.02;
            calme[(int)xBullet+5]-=(maxHight-minHight)*.1;
            calme[(int)xBullet+6]-=(maxHight-minHight)*.05;
            calme[(int)xBullet+7]-=(maxHight-minHight)*.04;
            calme[(int)xBullet+4]-=(maxHight-minHight)*.05;
            calme[(int)xBullet+3]-=(maxHight-minHight)*.04;
            isBom=false;
            push =true;

        }
        if(push){
            if(k>19) {push =false; k=4; don=true;}
            DrawSprite(gl,(float) xBullet,(float)yBullet,k,.5f,0);
            k++;
        }
        if(don){
            tank2 =!tank2;
            tank1=!tank1;
            don = false;
            t=0;
        }
        gl.glColor3f(1, 1f, 1f);
        DrawSprite(gl, x1, y1, monsterIndex, 1,0);
//        point point2 =new point(0,0);
        System.out.println(x1+"  "+y1);
//        System.out.println((point2.x+1)*50+"  "+(point2.y+1)*50);

        double dist = sqrdDistance((int) xBullet,(int)(float) (((calme[(int)(xBullet+7)]-minHight)/(maxHight-minHight)-.03)*maxHeight), (int) xBullet, (int) yBullet);
        double radii = Math.pow(0.5*0.1*maxHeight+0.5*0.1*maxHeight,2);
        boolean isCollided = dist<=40;
        System.out.println(isCollided + ", "+ dist + ", "+ radii);
        if(y1<0){
            x1=(int)(Math.random()*maxWidth);
            y1=maxHeight;
            monsterIndex = (int)(Math.random()*2)+4;
        }if(isCollided){
            x1=(int)(Math.random()*maxWidth);
            y1=maxHeight;
            monsterIndex = (int)(Math.random()*2)+4;

//            calme.get((int)((x1/100.0)*61)+1).y-=.05;
//            calme.get((int)((x1/100.0)*61)-1).y-=.05;
        }


        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glColor3f(1, 1, 0);
        gl.glBegin(GL.GL_POLYGON);
        gl.glVertex2d(-1,-1);
        for (int i = 0; i <calme.length ; i++) {

            gl.glVertex2d( (i*2.0 /(calme.length))-1, ((calme[i]-minHight)*2.0/(maxHight-minHight))-1);
        }
        gl.glVertex2d(1,-1);

        gl.glEnd();
        gl.glEnable(GL.GL_TEXTURE_2D);
        System.out.println();
        // Example: Drawing angle at the bottom left corner
        int angle = 45; // Replace this with your calculated angle
        String angleString = "Angle: " + angle + "°";

//// Render "Angle:"
//        DrawSprite(gl, 5, 5, 14, 0.3f,0); // A
//        DrawSprite(gl, 8, 5, 21, 0.3f,0); // n
//        DrawSprite(gl, 11, 5, 17, 0.3f,0); // g
//        DrawSprite(gl, 14, 5, 14, 0.3f,0); // l
//        DrawSprite(gl, 17, 5, 14, 0.3f,0); // e
//        DrawSprite(gl, 20, 5, 26, 0.3f,0); // :

// Render each digit of the angle
        String angleDigits = Integer.toString(angle);
        for (int i = 0; i < angleDigits.length(); i++) {
            int digit = Character.getNumericValue(angleDigits.charAt(i));
            DrawSprite(gl, 23 + (i * 3), 5, digit + 14, 0.3f,0); // Draw each digit
        }

// Render "°" (Assume it's stored as texture index 24)
        DrawSprite(gl, 26 + (angleDigits.length() * 3), 5, 22, 0.3f,0);
    }

    public double sqrdDistance(int x, int y, int x1, int y1){
        return Math.pow(x-x1,2)+Math.pow(y-y1,2);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public void DrawSprite(GL gl,float x, float y, int index, float scale,float rotation){

        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);	// Turn Blending On

        gl.glPushMatrix();
        gl.glTranslated( x/(maxWidth/2.0) - 0.9, y/(maxHeight/2.0) - 0.9, 0);
        gl.glScaled(0.1*scale, 0.1*scale, 1);
        gl.glRotated(-rotation, 0, 0, 1);
        //System.out.println(x +" " + y);
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

    public void handleKeyPress() {

        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            if (xTank > 0) {
                xTank--;
            }
            animationIndex++;
        }
        if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            if (xTank < maxWidth-10) {
                xTank++;
            }
            animationIndex++;
        }
        if (isKeyPressed(KeyEvent.VK_UP)) {
            if(tank1)
            {angle1 = Math.min(angle1 + .5, 90);}
            if(tank2)
            {angle2 = Math.min(angle2 + .5, 90);}
        } else if (isKeyPressed( KeyEvent.VK_DOWN)) {
            if(tank1) angle1 = Math.max(angle1 - .5, -90);
            if(tank2)angle2 = Math.max(angle2 - .5, -90);

        }
        if(isKeyPressed(KeyEvent.VK_SPACE));
    }

    public BitSet keyBits = new BitSet(256);

    @Override
    public void keyPressed(final KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_PLUS) {
            power = Math.min(power + 1, 100); // Increase power up to a max of 100
        }
        if (e.getKeyCode() == KeyEvent.VK_MINUS) {
            power = Math.max(power - 1, 10); // Decrease power, minimum 10
        }
        int keyCode = e.getKeyCode();
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
    public void TanKl(GL gl,float rotation,int xTank){
        float YTank =0;float leftY=0, rightY=0;
        System.out.println(xTank+"tank");
        if(xTank<50) {YTank =(float) (((calme[xTank+7]-minHight)/(maxHight-minHight)-.03)*maxHeight);
            System.out.println( ((calme[xTank+10]-minHight)/(maxHight-minHight))*maxHeight+"yfuofyuofy");
            if (xTank + 7 < calme.length) {
                leftY = (float) (((calme[xTank + 7] * 2 / (maxHight - minHight)) - 0.05) * maxHeight / 2);
            } else {
                leftY = 0; // Default value if out of bounds
            }

            if (xTank + 9 < calme.length) {
                rightY = (float) (((calme[xTank + 9] * 2 / (maxHight - minHight)) - 0.05) * maxHeight / 2);
            } else {
                rightY = 0; // Default value if out of bounds
            }}
        else {YTank =(float) (((calme[xTank+6]-minHight)/(maxHight-minHight)-0.03)*maxHeight);
            if ( xTank < calme.length - 6) {
                leftY = (float) (((calme[xTank + 5] * 2 / (maxHight - minHight)) - 0.05) * maxHeight / 2);
                rightY = (float) (((calme[xTank + 7] * 2 / (maxHight - minHight)) - 0.05) * maxHeight / 2);}}
        slope = (rightY - leftY) / 2;

        DrawSprite(gl, xTank, YTank, 0, 1f,-(float) Math.toDegrees(Math.atan(slope)));

        DrawSprite(gl, (float) (xTank+.5), YTank+3, 1, 1f,-(float) Math.toDegrees(Math.atan(slope))+rotation);


    }
    public void TanKr(GL gl,float rotation,int xTank){
        float YTank =0;float leftY=0, rightY=0;
        System.out.println(xTank+"tank");
        if(xTank<50) {YTank =(float) (((calme[xTank+7]-minHight)/(maxHight-minHight)-.03)*maxHeight);
            System.out.println( ((calme[xTank+10]-minHight)/(maxHight-minHight))*maxHeight+"yfuofyuofy");
            if ( xTank < calme.length - 6) {
                leftY = (float) (((calme[xTank + 7] * 2 / (maxHight - minHight)) - 0.05) * maxHeight / 2);
                rightY = (float) (((calme[xTank + 9] * 2 / (maxHight - minHight)) - 0.05) * maxHeight / 2);}}
        else {YTank =(float) (((calme[xTank+6]-minHight)/(maxHight-minHight)-0.03)*maxHeight);
            if ( xTank < calme.length - 6) {
                leftY = (float) (((calme[xTank + 5] * 2 / (maxHight - minHight)) - 0.05) * maxHeight / 2);
                rightY = (float) (((calme[xTank + 7] * 2 / (maxHight - minHight)) - 0.05) * maxHeight / 2);}}
        slope = (rightY - leftY) / 2;

        DrawSprite(gl, xTank, YTank, 0, 1f,-(float) Math.toDegrees(Math.atan(slope)));

        DrawSprite(gl, (float) (xTank-.5), YTank+2, 1, -1f,-(float) Math.toDegrees(Math.atan(slope))+rotation);


    }
    public void Bullet(int angle,double V0,double X0,double Y0){
        double angl =(angle+ Math.toDegrees(Math.atan(slope)))*PI/180.0;

        double velX =  V0  * Math.cos(angl);
        double velY =  V0 * Math.sin(angl);
        xBullet = velX * t + X0;

        if (xBullet < 5 || xBullet > maxWidth - 10 || (int)xBullet + 2 >= calme.length) {
            t = 0;
            isBom = false;
            tank2 = !tank2;
            tank1 = !tank1;
        }

        yBullet=velY*t-.5*g*Math.pow(t,2)+Y0;
    }

    int livesTank1 = 3; // Lives for Tank 1
    int livesTank2 = 3; // Lives for Tank 2
    int power = 40; // Default bullet power

    public void DrawTopBar(GL gl) {
        gl.glColor3f(0.2f, 0.0f, 0.2f); // Dark grey color

        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(-1.0f, 1.0f);  // Top-left
        gl.glVertex2f(1.0f, 1.0f);   // Top-right
        gl.glVertex2f(1.0f, 0.9f);   // Bottom-right
        gl.glVertex2f(-1.0f, 0.9f);  // Bottom-left
        gl.glEnd();

        // Draw text for angle and power
        gl.glColor3f(1, 1, 1); // White text
        DrawText(gl, "Angle: " + (tank1 ? angle1 : angle2) + "°", -0.9f, 0.95f);
        DrawText(gl, "Power: " + power, -0.4f, 0.95f);

        // Draw Back Button
        gl.glColor3f(1, 0, 0); // Red color
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex2f(-1.0f, 0.9f);
        gl.glVertex2f(-0.85f, 0.9f);
        gl.glVertex2f(-0.85f, 0.95f);
        gl.glVertex2f(-1.0f, 0.95f);
        gl.glEnd();
        DrawText(gl, "Back", -0.98f, 0.93f);
    }


    public void DrawLives(GL gl) {
        gl.glColor3f(1, 0, 0); // Red for lives display
        DrawText(gl, "Tank 1 Lives: " + livesTank1, -0.9f, 0.9f);
        DrawText(gl, "Tank 2 Lives: " + livesTank2, 0.5f, 0.9f);
    }

    public void DrawText(GL gl, String text, float x, float y) {
        // Draw text using a simple method (needs appropriate font or image rendering library)
        // Placeholder: Text drawing implementation depends on the library in use
        // For now, you can load pre-rendered text sprites to replace this function
        System.out.println("Text: " + text + " at (" + x + ", " + y + ")");
    }

    public void CheckBackButtonPress(int mouseX, int mouseY) {
        // Check if mouse click occurs in the 'Back' button area (bottom left corner)
        if (mouseX >= 10 && mouseX <= 60 && mouseY >= 10 && mouseY <= 40) {
            goToHomeScreen();
        }
    }

    public void goToHomeScreen() {
        System.out.println("Returning to Home Screen...");
        // Placeholder logic: Return to home screen or reset the game
    }
    @Override
    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        CheckBackButtonPress(mouseX, mouseY);
    }
}