package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        initScene();
    }

    private void initScene() {
        AudioNode music = new AudioNode(assetManager, "Sounds/musica.wav", true);
        music.setPositional(false);
        music.setLooping(true);
        music.setVolume(0.7f);
        rootNode.attachChild(music);
        music.play();
        
        // Creamos el suelo del escenario
        Box ground = new Box(10, 0.1f, 10);
        Geometry groundGeom = new Geometry("Ground", ground);
        
        // Cargamos la textura de pasto
        Texture grassTexture = assetManager.loadTexture("Textures/Terrain/grass3.jpg");
        Material groundMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        groundMat.setTexture("ColorMap", grassTexture); // Asignamos la textura al material
        groundGeom.setMaterial(groundMat);
        
        rootNode.attachChild(groundGeom);

        // Creamos una torre (que luego será la Banana Dorada)
        Spatial model = assetManager.loadModel("Models/monkey.j3o" );
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture Tex = assetManager.loadTexture("Textures/texture.png");
        mat.setTexture("ColorMap", Tex);
        model.setMaterial(mat);
        model.scale(5f);
        model.rotate(0,135,0);
        model.setLocalTranslation(0, -.25f, 0); // Posicionamos la torre
        rootNode.attachChild(model);
        //Flowers
        Spatial flower1 = assetManager.loadModel("Models/flor3.j3o" );
        Material flowermat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowertxt = assetManager.loadTexture("Textures/Flowers/flor3.png");
        flowermat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowermat.setTexture("ColorMap", flowertxt);
        flower1.setMaterial(flowermat);
        flower1.scale(1);
        model.setLocalTranslation(-6, -.25f, 0);
        rootNode.attachChild(flower1);
        
        
        // Agregamos la luz al escenario
        initLight();
    }

    private void initLight() {
        // Configuramos la luz ambiental
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(0.3f)); // Color y intensidad de la luz ambiental
        rootNode.addLight(ambientLight);
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Aquí irá la lógica de actualización del juego (movimiento, colisiones, etc.)
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Aquí se podría agregar código para renderizar elementos adicionales
    }
}
