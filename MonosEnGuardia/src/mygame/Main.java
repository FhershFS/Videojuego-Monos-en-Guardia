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
import com.jme3.ui.Picture;

public class Main extends SimpleApplication {


    private Node enemyNode; // Nodo para contener a los enemigos
    private Node targetNode; // Nodo de la banana dorada
    private float spawnTimer = 0f;
    private float spawnInterval = 3f; // Intervalo de tiempo entre la generación de enemigos
    private Node bananaNode;
    private AudioNode shootingSound;
    private Picture crosshair;
    private boolean isShooting = false;
    private Node bulletNode;

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
        
        // Inicializar el nodo para los plátanos
        bananaNode = new Node("bananaNode");
        rootNode.attachChild(bananaNode);
        
          // Cargar el sonido de disparo
        shootingSound = new AudioNode(assetManager, "Sounds/banana.wav", false);
        shootingSound.setPositional(false); // Establecer como no posicional
        shootingSound.setLooping(false); // Reproducir una sola vez
        shootingSound.setVolume(0.5f); // Ajustar el volumen según sea necesario
        rootNode.attachChild(shootingSound);
        
        // Cargar la textura de la mirilla
        crosshair = new Picture("Crosshair");
        crosshair.setImage(assetManager, "Textures/crosshair2.png", true);
        
        // Obtener el tamaño de la pantalla
        float screenWidth = settings.getWidth();
        float screenHeight = settings.getHeight();
        
        // Establecer el tamaño de la mirilla
        float crosshairWidth = screenWidth * 0.05f;
        float crosshairHeight = screenHeight * 0.05f;
        
        crosshair.setWidth(crosshairWidth);
        crosshair.setHeight(crosshairHeight);
        
        // Centrar la mirilla en la pantalla
        float crosshairPosX = (screenWidth - crosshairWidth) * 0.5f;
        float crosshairPosY = (screenHeight - crosshairHeight) * 0.5f;
        crosshair.setPosition(crosshairPosX, crosshairPosY);
        
        guiNode.attachChild(crosshair);
        
        
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
            new Vector3f(6.5f, 0, -6),
            new Vector3f(6, 0, 0),
            new Vector3f(-6, 0, -6),
            new Vector3f(-4, 0, 4),
            new Vector3f(9, 0, -3),
            new Vector3f(6, 0, 3),
            new Vector3f(9.5f, 0, 3),
            new Vector3f(-9, 0, 7),
            new Vector3f(-8, 0, 2),
            new Vector3f(8, 0, 7),
            new Vector3f(5.5f, 0, 9)
        };

        // Array de texturas de las flores
        String[] flowerTextures = {
            "Textures/Flowers/amapola.png",
            "Textures/Flowers/flor morada.png",
            "Textures/Flowers/girasol.png",
            "Textures/Flowers/flor morada.png",
            "Textures/Flowers/girasol.png",
            "Textures/Flowers/amapola.png",
            "Textures/Flowers/amapola.png",
            "Textures/Flowers/girasol.png",
            "Textures/Flowers/amapola.png",
            "Textures/Flowers/flor morada.png",
            "Textures/Flowers/girasol.png",
            "Textures/Flowers/amapola.png",
        };

        // Crear y agregar las flores
        for (int i = 0; i < flowerPositions.length; i++) {
            Spatial flower = assetManager.loadModel("Models/flor.j3o");
            Material flowerMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Texture flowerTxt = assetManager.loadTexture(flowerTextures[i]);
            flowerMat.setTexture("ColorMap", flowerTxt);
            flower.setMaterial(flowerMat);
            flower.scale(3);
            flower.rotate(0,4.5f,0);
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
        
        //torresitas
        // Array de posiciones de las torresitas
        Vector3f[] torresPositions = {
            new Vector3f(6, 0.5f, 20),
            new Vector3f(3.5f, 0.5f, 25),
            new Vector3f(-3.5f,0.5f, 25),
            new Vector3f(-6, 0.5f, 20),
        };

        // Crear y agregar las torresitas
        for (int i = 0; i < torresPositions.length; i++) {
            Spatial torresita = assetManager.loadModel("Models/tower.j3o");
            Material torresitaM = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Texture torresitaT = assetManager.loadTexture("Textures/torredark.png");
            torresitaM.setTexture("ColorMap", torresitaT);
            torresita .setMaterial(torresitaM);
            torresita .scale(1.5f);
            torresita .setLocalTranslation(torresPositions[i]);
            rootNode.attachChild(torresita );
        }
        
        //arbol
        Spatial arbol = assetManager.loadModel("Models/arbol.j3o");
        Material arbolm = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture arbolt = assetManager.loadTexture("Textures/arbol.png");
        arbolm.setTexture("ColorMap",arbolt);
        arbol.setMaterial(arbolm);
        arbol.scale(2.5f);
        arbol.rotate(0,2f,0);
        arbol.setLocalTranslation(-3.5f, .1f, 9f);
        rootNode.attachChild(arbol);
        
        //arbol2
        Spatial arbol2 = assetManager.loadModel("Models/arbol.j3o");
        arbol2.setMaterial(arbolm);
        arbol2.scale(2.5f);
        arbol2.rotate(0,2f,0);
        arbol2.setLocalTranslation(5f,.1f, 4f);
        rootNode.attachChild(arbol2);
        
        //arbol3
        Spatial arbol3 = assetManager.loadModel("Models/arbol.j3o");
        arbol3.setMaterial(arbolm);
        arbol3.scale(2.5f);
        arbol3.rotate(0,2f,0);
        arbol3.setLocalTranslation(-3f, .1f, 2f);
        rootNode.attachChild(arbol3);
        
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
