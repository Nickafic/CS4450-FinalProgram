package finalprogram;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class CameraController {
    
    private Vector3f position;
    private Vector3f lightpos;
    private float yaw;
    private float pitch;
    private long lastTime;
    private Chunk chunk;
    
    public CameraController(float x, float y, float z){
        chunk = new Chunk(0,0,0);
        
        position = new Vector3f(x,y,z);
        lightpos = new Vector3f(x,y,z);
        
        lightpos.x = chunk.CHUNK_SIZE *chunk.CUBE_LENGTH + chunk.CHUNK_SIZE;
        lightpos.y = (chunk.CHUNK_SIZE)/5;
        lightpos.z = chunk.CHUNK_SIZE *chunk.CUBE_LENGTH + chunk.CHUNK_SIZE;
        
        position.y -= 50;
        position.x -= 40;
        position.z -= 40;
    }
    
    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
    
    public void yaw(float amount){
        yaw += amount;
    }
    
    public void pitch(float amount){
        pitch -= amount;
    }

    public void forward(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    public void backward(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }   

    public void left(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw-90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw-90));
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    public void right(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw+90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw+90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    public void up(float distance){
        position.y -= distance;
    }

    public void down(float distance){
        position.y += distance;
    }
    
    public void lookThrough(){
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(position.x, position.y, position.z);
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lightpos.x).put(lightpos.y).put(lightpos.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

}