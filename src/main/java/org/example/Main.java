package org.example;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.opengl.*;

import com.jogamp.opengl.util.FPSAnimator;

public class Main {
    public static void main(String[] args) {

        GLProfile.initSingleton();
        GLProfile glp = GLProfile.get(GLProfile.GL2);

        GLCapabilities caps = new GLCapabilities(glp);
        caps.setDoubleBuffered(true);
        caps.setHardwareAccelerated(true);

        // NEWT window instead of AWT Frame + GLCanvas
        GLWindow window = GLWindow.create(caps);
        window.setTitle("Color Switch Clone");
        window.setSize(400, 800);
        window.setVisible(true);

        Game game = new Game(window);
        window.addGLEventListener(game);

        FPSAnimator animator = new FPSAnimator(window, 60);
        animator.start();

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(com.jogamp.newt.event.WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
