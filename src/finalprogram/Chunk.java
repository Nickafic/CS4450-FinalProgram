package finalprogram;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {

    static final int CHUNK_SIZE = 40;
    static final int CUBE_LENGTH = 2;

    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private Texture texture;
    private int StartX, StartY, StartZ;
    private Random r;

    private Block ltBlock;  //light block
    private int lightVertVBO;
    private int lightColVBO;
    private int lightTexVBO;

    void render(){
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE* 24);
        glPopMatrix();

        glPushMatrix();
        glTranslatef(0, 32, 0); // moving light source Block at expected location
        glBindBuffer(GL_ARRAY_BUFFER, lightVertVBO);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER,lightVertVBO);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, lightTexVBO);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        glDrawArrays(GL_QUADS, 0, 24);
        glPopMatrix();
    }

    public void rebuildMesh(float startX, float startY, float startZ) {

        float persistence = 0.05f;
        int seed = (int)System.currentTimeMillis();
        SimplexNoise noise = new SimplexNoise(CHUNK_SIZE, persistence, seed);

        VBOColorHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        lightColVBO = glGenBuffers();
        lightTexVBO = glGenBuffers();
        lightVertVBO = glGenBuffers();

        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE *CHUNK_SIZE)* 6 * 12);
        FloatBuffer LightPosData = BufferUtils.createFloatBuffer(6 * 12);
        FloatBuffer LightColData = BufferUtils.createFloatBuffer(6 * 12);
        FloatBuffer LightTexData = BufferUtils.createFloatBuffer(6 * 12);

        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                for (float y = 0; y < CHUNK_SIZE; y += 1) {
                    float height = (float) (startY + (100 * noise.getNoise(x, 0, z)) * CUBE_LENGTH);

                    if (y <= height) {
                        Blocks[(int)(x)][(int)(y)][(int) (z)] = new Block(Block.BlockType.BlockType_GRASS);
                        VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), (float) (y * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)), (float) (startZ + z * CUBE_LENGTH)));
                        VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                        VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int) (x)][(int) (y)][(int) (z)], y, height));
                    }
                }
            }
        }
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                for (float y = 0; y < 2; y += 1) {
                    if (Blocks[(int)x][(int)y][(int)z] == null) {
                        Blocks[(int)(x)][(int)(y)][(int) (z)] = new Block(Block.BlockType.BlockType_SAND);
                        VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), (float) (y * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)), (float) (startZ + z * CUBE_LENGTH)));
                        VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                        if(y == 0)
                            VertexTextureData.put(texHelper(0,0,5));
                        else
                            VertexTextureData.put(texHelper(0,0,4));
                        Blocks[(int)x][(int)y][(int)z].SetActive(true);
                    }
                }
            }
        }
        for (float x = 0; x < CHUNK_SIZE; x += 1) {
            for (float z = 0; z < CHUNK_SIZE; z += 1) {
                for (float y = 0; y < 3; y += 1) {
                    if (Blocks[(int)x][(int)y][(int)z] == null) {
                        Blocks[(int)(x)][(int)(y)][(int) (z)] = new Block(Block.BlockType.BlockType_WATER);
                        VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), (float) (y * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)), (float) (startZ + z * CUBE_LENGTH)));
                        VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                        VertexTextureData.put(texHelper(0,0,2));
                        Blocks[(int)x][(int)y][(int)z].SetActive(true);
                    }
                }
            }
        }
        // Light source block
        LightPosData.put(createCube(-2f, 0f, -2f));
        LightColData.put(createCubeVertexCol(getCubeColor(ltBlock)));
        LightTexData.put(createTexCube(0.0f, 0.0f, ltBlock, 0.0f, 1.0f));
        
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        LightColData.flip();
        LightPosData.flip();
        LightTexData.flip();

        glBindBuffer(GL_ARRAY_BUFFER,VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexPositionData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER,VertexColorData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0); 
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER,lightVertVBO);
        glBufferData(GL_ARRAY_BUFFER,LightPosData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER,lightColVBO);
        glBufferData(GL_ARRAY_BUFFER,LightColData,GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0); 
        glBindBuffer(GL_ARRAY_BUFFER, lightTexVBO);
        glBufferData(GL_ARRAY_BUFFER, LightTexData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }


    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }

    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
        // TOP QUAD
        x + offset, y + offset, z,
        x - offset, y + offset, z,
        x - offset, y + offset, z - CUBE_LENGTH,
        x + offset, y + offset, z - CUBE_LENGTH,
        // BOTTOM QUAD
        x + offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x - offset, y - offset, z,
        x + offset, y - offset, z,
        // FRONT QUAD
        x + offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        // BACK QUAD
        x + offset, y - offset, z,
        x - offset, y - offset, z,
        x - offset, y + offset, z,
        x + offset, y + offset, z,
        // LEFT QUAD
        x - offset, y + offset, z - CUBE_LENGTH,
        x - offset, y + offset, z,
        x - offset, y - offset, z,
        x - offset, y - offset, z - CUBE_LENGTH,
        // RIGHT QUAD
        x + offset, y + offset, z,
        x + offset, y + offset, z - CUBE_LENGTH,
        x + offset, y - offset, z - CUBE_LENGTH,
        x + offset, y - offset, z };
    }

    private float[] getCubeColor(Block block) {
        return new float[] { 1, 1, 1 };
    }

    public Chunk(int startX, int startY, int startZ) {

        try {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        } catch (Exception e) {
            System.out.print("ER-ROAR!");
            e.printStackTrace();
        }
        
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];

        ltBlock = new Block(Block.BlockType.BlockType_LIGHT);
        lightColVBO = glGenBuffers();
        lightVertVBO = glGenBuffers();
        lightTexVBO = glGenBuffers();
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }


    public static float[] createTexCube(float x, float y, Block block, float currY, float height){
        block.SetActive(true);
        float level = currY/height;
        if(currY == 2) {
            return texHelper(x,y,1);
        }
        if (block.GetID() == -1) {
            return texHelper(x,y,-1);
        }
        else if(level <= 0){
            return texHelper(x,y,5);
        }
        else if(level <= 0.5){
            return texHelper(x,y,4);
        }
        else if(level <= 0.8){
            return texHelper(x,y,3);
        }
        else if(level <= 1){
            return texHelper(x,y,0);
        }
        else{
            return texHelper(x,y,0);
        }
    }
    public static float[] texHelper(float x, float y, int id){
        float offset = (1024f/16)/1024f;
        return switch (id) {
            case 0 -> new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*3, y + offset*9,
                // TOP!
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                // LEFT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1};
            case 1 -> new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset * 2, y + offset * 1,
                x + offset * 3, y + offset * 1,
                x + offset * 3, y + offset * 2,
                x + offset * 2, y + offset * 2,
                // TOP!
                x + offset * 2, y + offset * 1,
                x + offset * 3, y + offset * 1,
                x + offset * 3, y + offset * 2,
                x + offset * 2, y + offset * 2,
                // FRONT QUAD
                x + offset * 2, y + offset * 1,
                x + offset * 3, y + offset * 1,
                x + offset * 3, y + offset * 2,
                x + offset * 2, y + offset * 2,
                // BACK QUAD
                x + offset * 2, y + offset * 1,
                x + offset * 3, y + offset * 1,
                x + offset * 3, y + offset * 2,
                x + offset * 2, y + offset * 2,
                // LEFT QUAD
                x + offset * 2, y + offset * 1,
                x + offset * 3, y + offset * 1,
                x + offset * 3, y + offset * 2,
                x + offset * 2, y + offset * 2,
                // RIGHT QUAD
                x + offset * 2, y + offset * 1,
                x + offset * 3, y + offset * 1,
                x + offset * 3, y + offset * 2,
                x + offset * 2, y + offset * 2};
            case 2 -> new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset * 15, y + offset * 12,
                x + offset * 16, y + offset * 12,
                x + offset * 15, y + offset * 13,
                x + offset * 16, y + offset * 13,
                // TOP!
                x + offset * 15, y + offset * 12,
                x + offset * 16, y + offset * 12,
                x + offset * 15, y + offset * 13,
                x + offset * 16, y + offset * 13,
                // FRONT QUAD
                x + offset * 15, y + offset * 12,
                x + offset * 16, y + offset * 12,
                x + offset * 16, y + offset * 13,
                x + offset * 15, y + offset * 13,
                // BACK QUAD
                x + offset * 15, y + offset * 12,
                x + offset * 16, y + offset * 12,
                x + offset * 16, y + offset * 13,
                x + offset * 15, y + offset * 13,
                // LEFT QUAD
                x + offset * 15, y + offset * 12,
                x + offset * 16, y + offset * 12,
                x + offset * 16, y + offset * 13,
                x + offset * 15, y + offset * 13,
                // RIGHT QUAD
                x + offset * 15, y + offset * 12,
                x + offset * 16, y + offset * 12,
                x + offset * 16, y + offset * 13,
                x + offset * 15, y + offset * 13};
            case 3 -> new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset * 1, y + offset * 10,
                x + offset * 2, y + offset * 10,
                x + offset * 2, y + offset * 11,
                x + offset * 1, y + offset * 11,
                // TOP!
                x + offset * 3, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 0,
                x + offset * 3, y + offset * 0,
                // FRONT QUAD
                x + offset * 3, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 0,
                x + offset * 3, y + offset * 0,
                // BACK QUAD
                x + offset * 3, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 0,
                x + offset * 3, y + offset * 0,
                // LEFT QUAD
                x + offset * 3, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 0,
                x + offset * 3, y + offset * 0,
                // RIGHT QUAD
                x + offset * 3, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 0,
                x + offset * 3, y + offset * 0};
            case 4 -> new float[] {
                // BOTTOM QUAD(DOWN=+Y)
                x + offset * 1, y + offset * 0,
                x + offset * 2, y + offset * 0,
                x + offset * 2, y + offset * 1,
                x + offset * 1, y + offset * 1,
                // TOP!
                x + offset * 1, y + offset * 0,
                x + offset * 2, y + offset * 0,
                x + offset * 2, y + offset * 1,
                x + offset * 1, y + offset * 1,
                // FRONT QUAD
                x + offset * 1, y + offset * 0,
                x + offset * 2, y + offset * 0,
                x + offset * 2, y + offset * 1,
                x + offset * 1, y + offset * 1,
                // BACK QUAD
                x + offset * 1, y + offset * 0,
                x + offset * 2, y + offset * 0,
                x + offset * 2, y + offset * 1,
                x + offset * 1, y + offset * 1,
                // LEFT QUAD
                x + offset * 1, y + offset * 0,
                x + offset * 2, y + offset * 0,
                x + offset * 2, y + offset * 1,
                x + offset * 1, y + offset * 1,
                // RIGHT QUAD
                x + offset * 1, y + offset * 0,
                x + offset * 2, y + offset * 0,
                x + offset * 2, y + offset * 1,
                x + offset * 1, y + offset * 1};
            case -1 -> new float[] {    //////////////// ltBlock source block
                // BOTTOM QUAD(DOWN=+Y)
                x + offset * 2, y + offset * 4,
                x + offset * 3, y + offset * 4,
                x + offset * 3, y + offset * 5,
                x + offset * 2, y + offset * 5,
                // TOP!
                x + offset * 2, y + offset * 4,
                x + offset * 3, y + offset * 4,
                x + offset * 3, y + offset * 5,
                x + offset * 2, y + offset * 5,
                // FRONT QUAD
                x + offset * 2, y + offset * 4,
                x + offset * 3, y + offset * 4,
                x + offset * 3, y + offset * 5,
                x + offset * 2, y + offset * 5,
                // BACK QUAD
                x + offset * 2, y + offset * 4,
                x + offset * 3, y + offset * 4,
                x + offset * 3, y + offset * 5,
                x + offset * 2, y + offset * 5,
                // LEFT QUAD
                x + offset * 2, y + offset * 4,
                x + offset * 3, y + offset * 4,
                x + offset * 3, y + offset * 5,
                x + offset * 2, y + offset * 5,
                // RIGHT QUAD
                x + offset * 2, y + offset * 4,
                x + offset * 3, y + offset * 4,
                x + offset * 3, y + offset * 5,
                x + offset * 2, y + offset * 5};
            default -> new float[]{
                // BOTTOM QUAD(DOWN=+Y)
                x + offset * 1, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 2,
                x + offset * 1, y + offset * 2,
                // TOP!
                x + offset * 1, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 2,
                x + offset * 1, y + offset * 2,
                // FRONT QUAD
                x + offset * 1, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 2,
                x + offset * 1, y + offset * 2,
                // BACK QUAD
                x + offset * 1, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 2,
                x + offset * 1, y + offset * 2,
                // LEFT QUAD
                x + offset * 1, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 2,
                x + offset * 1, y + offset * 2,
                // RIGHT QUAD
                x + offset * 1, y + offset * 1,
                x + offset * 2, y + offset * 1,
                x + offset * 2, y + offset * 2,
                x + offset * 1, y + offset * 2};
        };
    }
}
