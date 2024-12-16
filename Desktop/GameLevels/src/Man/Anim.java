package Man;

import javax.swing.*;
import screens.HomeScreen;
import screens.SplashScreen;

public class Anim extends JFrame {

    public static void main(String[] args) {
        SplashScreen splash = new SplashScreen();
        splash.showSplashScreen();

        // Schedule to display the Home Page after the SplashScreen
        SwingUtilities.invokeLater(() -> {
            Anim anim = new Anim();
            anim.showHomePage(); // Show the HomePage
        });
    }

    public Anim() {
        setTitle("Anim Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void showHomePage() {
        // Set the content pane to the HomePage
        setContentPane(new HomeScreen(this));
        revalidate();
        repaint();
    }
}
