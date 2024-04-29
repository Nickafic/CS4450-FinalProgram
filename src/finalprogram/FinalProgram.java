package finalprogram;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.Sys;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class FinalProgram {
    private CameraController camera;
    private Chunk chunk;
    private boolean first = true;
    
    private FloatBuffer lightPos;
    private FloatBuffer whitelt;
    private FloatBuffer ambientlt;
    private FloatBuffer diffusedlt;
    
    public void start() {
        try {
            createWindow();
            initGL();
            camera = new CameraController(0f, 0f, 0f);
            chunk = new Chunk(0, 0, 0);
            
            
            float dx = 0.0f;
            float dy = 0.0f;
            float mouseSensitivity = 0.09f;
            float movementSpeed = 20.0f;
            long lastTime = Sys.getTime();
            
            while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                long time = Sys.getTime();
                float dt = (time - lastTime) / 1000.0f;
                lastTime = time;
                
                camera.yaw(dx * mouseSensitivity);
                camera.pitch(dy * mouseSensitivity);
                dx = Mouse.getDX();
                dy = Mouse.getDY();           

                if (Keyboard.isKeyDown(Keyboard.KEY_W)){
                    camera.forward(movementSpeed * dt);
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_S)){
                    camera.backward(movementSpeed * dt);
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_A)){
                    camera.left(movementSpeed * dt);
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_D)){
                    camera.right(movementSpeed * dt);
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                    camera.up(movementSpeed * dt);
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                    camera.down(movementSpeed * dt);
                }

                render();
                Display.update();
                Display.sync(60);
            }

            Display.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640, 480));
        Display.setTitle("Minecraft Style Game");
        Display.setLocation((Display.getDisplayMode().getWidth() - 640) / 2, (Display.getDisplayMode().getHeight() - 480) / 2);
        Display.create();
        Display.setResizable(true);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(70f, 640f / 480f, 0.3f, 1000f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glViewport(0, 0, Display.getWidth(), Display.getHeight());
    }

    private void initGL() {
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_BLEND);
        Mouse.setGrabbed(true);
        
        initLights();
        glLight(GL_LIGHT0, GL_POSITION, lightPos); 
        glLight(GL_LIGHT0, GL_SPECULAR, whitelt);
        glLight(GL_LIGHT0, GL_DIFFUSE, whitelt);
        glLight(GL_LIGHT0, GL_AMBIENT, whitelt);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
    }
    
    private void render() {
        if (first) {
            first = false;
            chunk = new Chunk(0, 0, 0);
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glLoadIdentity();
        camera.lookThrough();
        chunk.render();
    }
    
        private void initLights() {
        lightPos = BufferUtils.createFloatBuffer(4);
        lightPos.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();

        whitelt = BufferUtils.createFloatBuffer(4);
        whitelt.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();

        ambientlt = BufferUtils.createFloatBuffer(4);
        ambientlt.put(0.2f).put(0.2f).put(0.2f).put(1.0f).flip();

        diffusedlt = BufferUtils.createFloatBuffer(4);
        diffusedlt.put(0.8f).put(0.8f).put(0.8f).put(1.0f).flip();
    }
    
    public static void main(String[] args) {
        FinalProgram fin = new FinalProgram();
        fin.start();
    }
}
