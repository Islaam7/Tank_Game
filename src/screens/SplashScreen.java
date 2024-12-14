package screens;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
    public SplashScreen() {
        JLabel splashLabel = new JLabel(new ImageIcon(getClass().getResource("/Assets/tank_images/tankSplash3.png")));

        getContentPane().add(splashLabel, BorderLayout.CENTER);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    public void showSplashScreen() {
        setVisible(true);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setVisible(false);
    }
}
