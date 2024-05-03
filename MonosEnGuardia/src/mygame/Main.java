package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;

public class Main extends SimpleApplication {


    private Node enemyNode; // Nodo para contener a los enemigos
    private Node targetNode; // Nodo de la banana dorada
    private float spawnTimer = 0f;
    private float spawnInterval = 2f; // Intervalo de tiempo entre la generación de enemigos


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
        // Inicializar nodos
        enemyNode = new Node("enemyNode");
        rootNode.attachChild(enemyNode);

        // Crear torre de la banana dorada y establecerla como objetivo
        targetNode = new Node("targetNode");
        targetNode.setLocalTranslation(0, -.3f, -5); // Posicionamiento de la torre de la banana dorada
        rootNode.attachChild(targetNode);
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
        Spatial ground2 = assetManager.loadModel("Models/escenario.j3o" );
        
        // Cargamos la textura de pasto
        Texture grassTexture = assetManager.loadTexture("Textures/Terrain/pasto.png");
        Material groundMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        groundMat.setTexture("ColorMap", grassTexture); // Asignamos la textura al material
        ground.setMaterial(groundMat);
        ground.scale(6.5f);
        ground.setLocalTranslation(0, -2.5f, 0);
        rootNode.attachChild(ground);
        
         // Cargamos la textura de pasto
        Texture grassTexture2 = assetManager.loadTexture("Textures/Terrain/pastodark.png");
        Material groundMat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        groundMat2.setTexture("ColorMap", grassTexture2); // Asignamos la textura al material
        ground2.setMaterial(groundMat2);
        ground2.scale(6.5f);
        ground2.setLocalTranslation(0, -2.5f, 20.25f);
        rootNode.attachChild(ground2);
        
        
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
        
        //torre enemiga
        Spatial torredark = assetManager.loadModel("Models/tower.j3o" );
        Material darkm = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture darkt = assetManager.loadTexture("Textures/torredark.png");
        darkm.setTexture("ColorMap", darkt);
        torredark.setMaterial(darkm);
        torredark.scale(3.5f);
        torredark.rotate(0,0,0);
        torredark.setLocalTranslation(0,.5f, 25); // Posicionamos la torre
        rootNode.attachChild(torredark);
       
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);
        
        //Flowers
        // Array de posiciones de las flores
        Vector3f[] flowerPositions = {
            new Vector3f(-6, 0, 6),
            new Vector3f(6, 0, -6),
            new Vector3f(6, 0, 0),
            new Vector3f(-6, 0, -6),
            new Vector3f(-4, 0, 4),
            new Vector3f(9, 0, -3),
            new Vector3f(6, 0, 3),
            new Vector3f(9, 0, 3),
            new Vector3f(-9, 0, 7),
            new Vector3f(-8, 0, 2),
            new Vector3f(8, 0, 7)
        };

        // Array de texturas de las flores
        String[] flowerTextures = {
            "Textures/Flowers/flor1.png",
            "Textures/Flowers/flor2.png",
            "Textures/Flowers/flor3.png",
            "Textures/Flowers/flor2.png",
            "Textures/Flowers/flor3.png",
            "Textures/Flowers/flor1.png",
            "Textures/Flowers/flor1.png",
            "Textures/Flowers/flor3.png",
            "Textures/Flowers/flor1.png",
            "Textures/Flowers/flor2.png",
            "Textures/Flowers/flor3.png"
        };

        // Crear y agregar las flores
        for (int i = 0; i < flowerPositions.length; i++) {
            Spatial flower = assetManager.loadModel("Models/flor3.j3o");
            Material flowerMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Texture flowerTxt = assetManager.loadTexture(flowerTextures[i]);
            flowerMat.setTexture("ColorMap", flowerTxt);
            flowerMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            flower.setMaterial(flowerMat);
            flower.scale(1);
            flower.setLocalTranslation(flowerPositions[i]);
            rootNode.attachChild(flower);
        }
       
        // Caminito
        Spatial caminito = assetManager.loadModel("Models/Caminito.j3o");
        Material caminitomat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture caminitotxt = assetManager.loadTexture("Textures/Terrain/caminito.png");
        caminitomat.setTexture("ColorMap",caminitotxt);
        caminito.setMaterial(caminitomat);
        caminito.scale(3.5f);
        caminito.rotate(0,7.9f,0);
        caminito.setLocalTranslation(.2f, -.8f, 3.6f);
        rootNode.attachChild(caminito);
        
        // Caminito 2
        Spatial caminito2 = assetManager.loadModel("Models/Caminito.j3o");
        Material caminitomat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture caminitotxt2 = assetManager.loadTexture("Textures/Terrain/caminito2.png");
        caminitomat2.setTexture("ColorMap",caminitotxt2);
        caminito2.setMaterial(caminitomat2);
        caminito2.scale(3.5f);
        caminito2.rotate(0,1.55f,0);
        caminito2.setLocalTranslation(.2f, -.8f, 16.65f);
        rootNode.attachChild(caminito2);
        

        
    }
    @Override
    public void simpleUpdate(float tpf) {
         // Generar enemigos
        spawnTimer += tpf;
        if (spawnTimer >= spawnInterval) {
            spawnEnemy();
            spawnTimer = 0f;
        }

        // Mover enemigos hacia la torre de la banana dorada de manera menos lineal
        for (Spatial enemy : enemyNode.getChildren()) {
            Vector3f enemyPos = enemy.getWorldTranslation();
            Vector3f targetPos = targetNode.getWorldTranslation();
            Vector3f direction = targetPos.subtract(enemyPos).normalizeLocal();
            float distance = enemyPos.distance(targetPos);

            // Si el enemigo está lejos, interpolamos su posición para suavizar el movimiento
            if (distance > 1f) {
                Vector3f interpolatedPos = enemyPos.add(direction.mult(tpf * 2f));
                enemy.setLocalTranslation(interpolatedPos);
            } else {
                // Si el enemigo está cerca, lo movemos directamente hacia la torre
                enemy.move(direction.mult(tpf * 2f));
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Aquí se podría agregar código para renderizar elementos adicionales
    }
     private void spawnEnemy() {
        // Cargar modelo del enemigo (por ejemplo, un mono)
        Spatial enemy = assetManager.loadModel("Models/cojeno.j3o");
        Material enemym = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture enemyt = assetManager.loadTexture("Textures/cojeno.png");
        enemym.setTexture("ColorMap",enemyt);
        enemy.setMaterial(enemym);
        enemy.rotate(0,3,0);
        // Posicionamiento aleatorio en la torre oscura
        Vector3f spawnPosition = new Vector3f(FastMath.nextRandomFloat() * 10 - 5, 0.5f, FastMath.nextRandomFloat() * 10 + 15);
        enemy.setLocalTranslation(spawnPosition);

        // Agregar el enemigo al nodo de enemigos
        enemyNode.attachChild(enemy);
    }

}
