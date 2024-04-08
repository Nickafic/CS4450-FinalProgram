/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package finalprogram;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;


public class FinalProgram {

    
    private float cameraX = 0.0f;
    private float cameraY = 0.0f;
    private float cameraZ = 0.0f;
    private float cameraRotationY = 0.0f;
    private float cameraRotationX = 0.0f;

    public void start() {
        try {
            Display.setDisplayMode(new DisplayMode(640, 480));
            Display.create();
            Display.setTitle("Minecraft Style Game");
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        initGL();
        Mouse.setGrabbed(true); 

        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            render();
            input();
            Display.update();
            Display.sync(60); // Cap at 60fps
        }

        Display.destroy();
    }

    private void initGL() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(70f, 640f / 480f, 0.3f, 1000f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();
        GL11.glRotatef(cameraRotationX, 1, 0, 0);
        GL11.glRotatef(cameraRotationY, 0, 1, 0);
        GL11.glTranslatef(-cameraX, -cameraY, -cameraZ);

        // Start drawing the cube
        GL11.glBegin(GL11.GL_QUADS);

        // Front face
        GL11.glColor3f(1, 0, 0); // Red
        GL11.glVertex3f(-1, -1, 1);
        GL11.glVertex3f(-1, 1, 1);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(1, -1, 1);

        // Back face
        GL11.glColor3f(0, 1, 0); // Green
        GL11.glVertex3f(-1, -1, -1);
        GL11.glVertex3f(-1, 1, -1);
        GL11.glVertex3f(1, 1, -1);
        GL11.glVertex3f(1, -1, -1);

        // Top face
        GL11.glColor3f(0, 0, 1); // Blue
        GL11.glVertex3f(-1, 1, -1);
        GL11.glVertex3f(-1, 1, 1);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(1, 1, -1);

        // Bottom face
        GL11.glColor3f(1, 1, 0); // Yellow
        GL11.glVertex3f(-1, -1, -1);
        GL11.glVertex3f(-1, -1, 1);
        GL11.glVertex3f(1, -1, 1);
        GL11.glVertex3f(1, -1, -1);

        // Right face
        GL11.glColor3f(1, 0, 1); // Magenta
        GL11.glVertex3f(1, -1, -1);
        GL11.glVertex3f(1, -1, 1);
        GL11.glVertex3f(1, 1, 1);
        GL11.glVertex3f(1, 1, -1);

        // Left face
        GL11.glColor3f(0, 1, 1); // Cyan
        GL11.glVertex3f(-1, -1, -1);
        GL11.glVertex3f(-1, -1, 1);
        GL11.glVertex3f(-1, 1, 1);
        GL11.glVertex3f(-1, 1, -1);

        GL11.glEnd();
    }

    private void input() {
        float moveSpeed = 0.1f;
        float rotationRad = (float)Math.toRadians(cameraRotationY);

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            cameraX += moveSpeed * Math.sin(rotationRad); // Move forward
            cameraZ -= moveSpeed * Math.cos(rotationRad);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            cameraX -= moveSpeed * Math.sin(rotationRad); // Move backward
            cameraZ += moveSpeed * Math.cos(rotationRad);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            cameraX -= moveSpeed * Math.cos(rotationRad);
            cameraZ -= moveSpeed * Math.sin(rotationRad);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            cameraX += moveSpeed * Math.cos(rotationRad);
            cameraZ += moveSpeed * Math.sin(rotationRad);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            cameraY += moveSpeed;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            cameraY -= moveSpeed;
        }

        // Camera rotation based on mouse movement
        cameraRotationY += 0.3f * Mouse.getDX();
        cameraRotationX -= 0.3f * Mouse.getDY();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FinalProgram game = new FinalProgram();
        game.start();

 
    }
    
}
