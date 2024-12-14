package Man;

import com.sun.opengl.util.*;
import java.awt.*;
import javax.media.opengl.*;
import javax.swing.*;
import screens.SplashScreen;

public class Anim extends JFrame {

    public static void main(String[] args) {
        SplashScreen splash = new SplashScreen();
        splash.showSplashScreen();

        SwingUtilities.invokeLater(() -> new Anim());
    }

    public Anim() {
        GLCanvas glcanvas;
        Animator animator;

        AnimListener listener = new AnimGLEventListener4();
        glcanvas = new GLCanvas();
        glcanvas.addGLEventListener(listener);
        glcanvas.addKeyListener(listener);
        getContentPane().add(glcanvas, BorderLayout.CENTER);
        animator = new FPSAnimator(15);
        animator.add(glcanvas);
        animator.start();

        setTitle("Anim Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setFocusable(true);
        glcanvas.requestFocus();
    }
}
