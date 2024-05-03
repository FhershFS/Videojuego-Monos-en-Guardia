package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;

public class Main extends SimpleApplication {

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Monos en Guardia");
        settings.setSettingsDialogImage("Interface/INICIO.png");
        settings.setFullscreen(true);
        Main app = new Main();
        app.setSettings(settings);
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
        
        cam.setLocation(new Vector3f(0, 2, 15)); // Establece la posición de la cámara
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        // Creamos el suelo del escenario
        Spatial ground = assetManager.loadModel("Models/escenario.j3o" );
        // Cargamos la textura de pasto
        Texture grassTexture = assetManager.loadTexture("Textures/Terrain/pasto.png");
        Material groundMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        groundMat.setTexture("ColorMap", grassTexture); // Asignamos la textura al material
        ground.setMaterial(groundMat);
        ground.scale(6.5f);
        ground.setLocalTranslation(0, -2.5f, 0);
        rootNode.attachChild(ground);

        // Creamos una torre (que luego será la Banana Dorada)
        Spatial model = assetManager.loadModel("Models/monkey.j3o" );
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture Tex = assetManager.loadTexture("Textures/texture.png");
        mat.setTexture("ColorMap", Tex);
        model.setMaterial(mat);
        model.scale(7f);
        model.rotate(0,3.2f,0);
        model.setLocalTranslation(0, -.3f, -5); // Posicionamos la torre
        rootNode.attachChild(model);
       
        //Flowers
        Spatial flower1 = assetManager.loadModel("Models/flor3.j3o" );
        Material flowermat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowertxt = assetManager.loadTexture("Textures/Flowers/flor3.png");
        flowermat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowermat.setTexture("ColorMap", flowertxt);
        flowermat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flower1.setMaterial(flowermat);
        flower1.scale(1);
        flower1.setLocalTranslation(-6, 0, 0);
        rootNode.attachChild(flower1);
        
        //Flowers 
        
        Spatial flower2 = assetManager.loadModel("Models/flor3.j3o" );
        Material flowermat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowertxt2 = assetManager.loadTexture("Textures/Flowers/flor1.png");
        flowermat2.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowermat2.setTexture("ColorMap", flowertxt2);
        flower2.setMaterial(flowermat2);
        flower2.scale(1);
        flower2.setLocalTranslation(-6, 0, 6);
        rootNode.attachChild(flower2);
        
        
        Spatial flower3 = assetManager.loadModel("Models/flor3.j3o" );
        Material flowermat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowertxt3 = assetManager.loadTexture("Textures/Flowers/flor2.png");
        flowermat3.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowermat3.setTexture("ColorMap", flowertxt3);
        flower3.setMaterial(flowermat3);
        flower3.scale(1);
        flower3.setLocalTranslation(6, 0, -6);
        rootNode.attachChild(flower3);
        
        Spatial flower4 = assetManager.loadModel("Models/flor3.j3o" );
        Material flowermat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowertxt4 = assetManager.loadTexture("Textures/Flowers/flor3.png");
        flowermat4.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowermat4.setTexture("ColorMap", flowertxt4);
        flower4.setMaterial(flowermat3);
        flower4.scale(1);
        flower4.setLocalTranslation(6, 0, 0);
        rootNode.attachChild(flower4);
        
        
        // Quinta flor (flor2)
        Spatial flower5 = assetManager.loadModel("Models/flor3.j3o");
        Material flowerMat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowerTxt5 = assetManager.loadTexture("Textures/Flowers/flor2.png");
        flowerMat5.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowerMat5.setTexture("ColorMap", flowerTxt5);
        flower5.setMaterial(flowerMat5);
        flower5.scale(1);
        flower5.setLocalTranslation(-6, 0, -6);
        rootNode.attachChild(flower5);

        // Sexta flor (flor3)
        Spatial flower6 = assetManager.loadModel("Models/flor3.j3o");
        Material flowerMat6 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowerTxt6 = assetManager.loadTexture("Textures/Flowers/flor3.png");
        flowerMat6.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowerMat6.setTexture("ColorMap", flowerTxt6);
        flower6.setMaterial(flowerMat6);
        flower6.scale(1);
        flower6.setLocalTranslation(-4, 0, 4);
        rootNode.attachChild(flower6);

        // Séptima flor (flor1)
        Spatial flower7 = assetManager.loadModel("Models/flor3.j3o");
        Material flowerMat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowerTxt7 = assetManager.loadTexture("Textures/Flowers/flor1.png");
        flowerMat7.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowerMat7.setTexture("ColorMap", flowerTxt7);
        flower7.setMaterial(flowerMat7);
        flower7.scale(1);
        flower7.setLocalTranslation(9, 0, -3);
        rootNode.attachChild(flower7);

        // Octava flor (flor2)
        Spatial flower8 = assetManager.loadModel("Models/flor3.j3o");
        Material flowerMat8 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowerTxt8 = assetManager.loadTexture("Textures/Flowers/flor1.png");
        flowerMat8.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowerMat8.setTexture("ColorMap", flowerTxt8);
        flower8.setMaterial(flowerMat8);
        flower8.scale(1);
        flower8.setLocalTranslation(6, 0, 3);
        rootNode.attachChild(flower8);

        // Novena flor (flor3)
        Spatial flower9 = assetManager.loadModel("Models/flor3.j3o");
        Material flowerMat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowerTxt9 = assetManager.loadTexture("Textures/Flowers/flor3.png");
        flowerMat9.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowerMat9.setTexture("ColorMap", flowerTxt9);
        flower9.setMaterial(flowerMat9);
        flower9.scale(1);
        flower9.setLocalTranslation(9, 0, 3);
        rootNode.attachChild(flower9);

       // Décima flor (flor1)
        Spatial flower10 = assetManager.loadModel("Models/flor3.j3o");
        Material flowerMat10 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowerTxt10 = assetManager.loadTexture("Textures/Flowers/flor1.png");
        flowerMat10.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowerMat10.setTexture("ColorMap", flowerTxt10);
        flower10.setMaterial(flowerMat10);
        flower10.scale(1);
        flower10.setLocalTranslation(-9, 0, 7);
        rootNode.attachChild(flower10);

        // Undécima flor (flor2)
        Spatial flower11 = assetManager.loadModel("Models/flor3.j3o");
        Material flowerMat11 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowerTxt11 = assetManager.loadTexture("Textures/Flowers/flor2.png");
        flowerMat11.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowerMat11.setTexture("ColorMap", flowerTxt11);
        flower11.setMaterial(flowerMat11);
        flower11.scale(1);
        flower11.setLocalTranslation(-8, 0, 2);
        rootNode.attachChild(flower11);
        
        // Duodécima flor (flor3)
        Spatial flower12 = assetManager.loadModel("Models/flor3.j3o");
        Material flowerMat12 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture flowerTxt12 = assetManager.loadTexture("Textures/Flowers/flor3.png");
        flowerMat12.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        flowerMat12.setTexture("ColorMap", flowerTxt12);
        flower12.setMaterial(flowerMat12);
        flower12.scale(1);
        flower12.setLocalTranslation(8, 0, 7);
        rootNode.attachChild(flower12);
        
        // Agregamos la luz al escenario
        initLight();
        
        // Caminito
        Spatial caminito = assetManager.loadModel("Models/Caminito.j3o");
        Material caminitomat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture caminitotxt = assetManager.loadTexture("Textures/Terrain/caminito.png");
        caminitomat.setTexture("ColorMap",caminitotxt);
        caminito.setMaterial(caminitomat);
        caminito.scale(3.4f);
        caminito.rotate(0,7.9f,0);
        caminito.setLocalTranslation(.2f, -.8f, 3.6f);
        rootNode.attachChild(caminito);
        
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
