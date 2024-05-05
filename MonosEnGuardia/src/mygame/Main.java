package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import java.util.Iterator;

public class Main extends SimpleApplication {

    private Node enemyNode;
    private Node targetNode;
    private float spawnTimer = 0f;
    private float spawnInterval = 3f;

    private int vidaTorre = 10; // Vida inicial de la torre
    private Spatial model;

    private int deadEnemies = 0;
    private Node bananaNode;
    private BitmapText deathCountText;
    private BitmapText vidaText;

    private int deathCount = 0;
    private AudioNode shootingSound;
    private AudioNode damageSound;
    private Picture crosshair;
    private Node bulletNode;
    private BulletAppState bulletAppState;
    private AudioNode deadSound; // Declaración del AudioNode

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Monos en Guardia");
        settings.setSettingsDialogImage("Interface/INICIO.png");
        settings.setFullscreen(true);
        settings.setFrameRate(60); // Establece el framerate deseado
        settings.setVSync(true); // Activa el VSync para evitar el tearing
        settings.setSamples(4); // Activa el anti-aliasing (si es compatible con tu hardware)
        settings.setUseInput(true); // Habilita la entrada de usuario

        Main app = new Main();
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        setDisplayStatView(false);

        initScene();
        enemyNode = new Node("enemyNode");
        rootNode.attachChild(enemyNode);

        targetNode = new Node("targetNode");
        targetNode.setLocalTranslation(0, -.3f, -5);
        rootNode.attachChild(targetNode);

        bananaNode = new Node("bananaNode");
        rootNode.attachChild(bananaNode);

        shootingSound = new AudioNode(assetManager, "Sounds/banana.wav", false);
        shootingSound.setPositional(false);
        shootingSound.setLooping(false);
        shootingSound.setVolume(0.25f);
        rootNode.attachChild(shootingSound);

        deadSound = new AudioNode(assetManager, "Sounds/dead.wav", false);
        deadSound.setPositional(false);
        deadSound.setLooping(false);
        deadSound.setVolume(1f);
        deadSound.setPitch(.5f);
        rootNode.attachChild(deadSound);
        
        damageSound = new AudioNode(assetManager, "Sounds/damage.wav", false);
        damageSound.setPositional(false);
        damageSound.setLooping(false);
        damageSound.setVolume(.75f);
        damageSound.setPitch(1.25f);
        rootNode.attachChild(damageSound);


        crosshair = new Picture("Crosshair");
        crosshair.setImage(assetManager, "Textures/crosshair.png", true);
        float screenWidth = settings.getWidth();
        float screenHeight = settings.getHeight();
        float crosshairWidth = screenWidth * 0.05f;
        float crosshairHeight = screenHeight * 0.05f;
        crosshair.setWidth(crosshairWidth);
        crosshair.setHeight(crosshairHeight);
        float crosshairPosX = (screenWidth - crosshairWidth) * 0.5f;
        float crosshairPosY = (screenHeight - crosshairHeight) * 0.5f;
        crosshair.setPosition(crosshairPosX, crosshairPosY);
        guiNode.attachChild(crosshair);

        inputManager.addMapping("Disparar", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Disparar");

        bulletNode = new Node("bulletNode");
        rootNode.attachChild(bulletNode);

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // Inicializar el contador de muertes
        deathCountText = new BitmapText(guiFont, false);
        deathCountText.setSize(guiFont.getCharSet().getRenderedSize());
        deathCountText.setColor(ColorRGBA.Yellow); // Color del texto
        deathCountText.setText("Enemigos: 0");
        deathCountText.setLocalTranslation(10, settings.getHeight() - 10, 0); // Posición del texto en la pantalla
        guiNode.attachChild(deathCountText);

        vidaText = new BitmapText(guiFont, false);
        vidaText.setSize(guiFont.getCharSet().getRenderedSize());
        vidaText.setColor(ColorRGBA.Green); // Color del texto
        vidaText.setText("Vida de la Torre: " + vidaTorre); // Texto inicial
        vidaText.setLocalTranslation(settings.getWidth()-180, settings.getHeight() - 10, 0); // Posición del texto en la pantalla
        guiNode.attachChild(vidaText);
    }

    private void initScene() {
        AudioNode music = new AudioNode(assetManager, "Sounds/musica.wav", true);
        music.setPositional(false);
        music.setLooping(true);
        music.setVolume(0.7f);
        rootNode.attachChild(music);
        music.play();

        cam.setLocation(new Vector3f(-3.5f, 4f, 9f)); // Establece la posición de la cámara
        cam.lookAt(new Vector3f(2, 0, 40), Vector3f.UNIT_Y);

        // Creamos el suelo del escenario
        Spatial ground = assetManager.loadModel("Models/escenario.j3o");
        Spatial ground2 = assetManager.loadModel("Models/escenario.j3o");

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
        model = assetManager.loadModel("Models/monkey.j3o");
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture Tex = assetManager.loadTexture("Textures/texture.png");
        mat.setTexture("ColorMap", Tex);
        model.setMaterial(mat);
        model.scale(7f);
        model.rotate(0, 3.2f, 0);
        model.setLocalTranslation(0, -.3f, -5); // Posicionamos la torre
        rootNode.attachChild(model);

        //torre enemiga
        Spatial torredark = assetManager.loadModel("Models/tower.j3o");
        Material darkm = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture darkt = assetManager.loadTexture("Textures/torredark.png");
        darkm.setTexture("ColorMap", darkt);
        torredark.setMaterial(darkm);
        torredark.scale(3.5f);
        torredark.rotate(0, 0, 0);
        torredark.setLocalTranslation(0, .5f, 25); // Posicionamos la torre
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
            "Textures/Flowers/amapola.png",};

        // Crear y agregar las flores
        for (int i = 0; i < flowerPositions.length; i++) {
            Spatial flower = assetManager.loadModel("Models/flor.j3o");
            Material flowerMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Texture flowerTxt = assetManager.loadTexture(flowerTextures[i]);
            flowerMat.setTexture("ColorMap", flowerTxt);
            flower.setMaterial(flowerMat);
            flower.scale(3);
            flower.rotate(0, 4.5f, 0);
            flower.setLocalTranslation(flowerPositions[i]);
            rootNode.attachChild(flower);
        }

        // Caminito
        Spatial caminito = assetManager.loadModel("Models/Caminito.j3o");
        Material caminitomat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture caminitotxt = assetManager.loadTexture("Textures/Terrain/caminito.png");
        caminitomat.setTexture("ColorMap", caminitotxt);
        caminito.setMaterial(caminitomat);
        caminito.scale(3.5f);
        caminito.rotate(0, 7.9f, 0);
        caminito.setLocalTranslation(.2f, -.8f, 3.6f);
        rootNode.attachChild(caminito);

        // Caminito 2
        Spatial caminito2 = assetManager.loadModel("Models/Caminito.j3o");
        Material caminitomat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture caminitotxt2 = assetManager.loadTexture("Textures/Terrain/caminito2.png");
        caminitomat2.setTexture("ColorMap", caminitotxt2);
        caminito2.setMaterial(caminitomat2);
        caminito2.scale(3.5f);
        caminito2.rotate(0, 1.55f, 0);
        caminito2.setLocalTranslation(.2f, -.8f, 16.65f);
        rootNode.attachChild(caminito2);

        //torresitas
        // Array de posiciones de las torresitas
        Vector3f[] torresPositions = {
            new Vector3f(6, 0.5f, 20),
            new Vector3f(3.5f, 0.5f, 25),
            new Vector3f(-3.5f, 0.5f, 25),
            new Vector3f(-6, 0.5f, 20),};

        // Crear y agregar las torresitas
        for (int i = 0; i < torresPositions.length; i++) {
            Spatial torresita = assetManager.loadModel("Models/tower.j3o");
            Material torresitaM = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Texture torresitaT = assetManager.loadTexture("Textures/torredark.png");
            torresitaM.setTexture("ColorMap", torresitaT);
            torresita.setMaterial(torresitaM);
            torresita.scale(1.5f);
            torresita.setLocalTranslation(torresPositions[i]);
            rootNode.attachChild(torresita);
        }

        //arbol
        Spatial arbol = assetManager.loadModel("Models/arbol.j3o");
        Material arbolm = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture arbolt = assetManager.loadTexture("Textures/arbol.png");
        arbolm.setTexture("ColorMap", arbolt);
        arbol.setMaterial(arbolm);
        arbol.scale(2.5f);
        arbol.rotate(0, 2f, 0);
        arbol.setLocalTranslation(-3.5f, .1f, 9f);
        rootNode.attachChild(arbol);

        //arbol2
        Spatial arbol2 = assetManager.loadModel("Models/arbol.j3o");
        arbol2.setMaterial(arbolm);
        arbol2.scale(2.5f);
        arbol2.rotate(0, 2f, 0);
        arbol2.setLocalTranslation(5f, .1f, 4f);
        rootNode.attachChild(arbol2);

        //arbol3
        Spatial arbol3 = assetManager.loadModel("Models/arbol.j3o");
        arbol3.setMaterial(arbolm);
        arbol3.scale(2.5f);
        arbol3.rotate(0, 2f, 0);
        arbol3.setLocalTranslation(-3f, .1f, 2f);
        rootNode.attachChild(arbol3);

    }

    @Override
    public void simpleUpdate(float tpf) {
        spawnTimer += tpf;
        if (spawnTimer >= spawnInterval) {
            spawnEnemy();
            spawnTimer = 0f;
        }

        // Llama al método handleCollisions() para verificar las colisiones
        handleCollisions();

        for (Spatial enemy : enemyNode.getChildren()) {
            Vector3f enemyPos = enemy.getWorldTranslation();
            Vector3f targetPos = targetNode.getWorldTranslation();
            Vector3f direction = targetPos.subtract(enemyPos).normalizeLocal();
            float distance = enemyPos.distance(targetPos);

            if (distance > 1f) {
                Vector3f interpolatedPos = enemyPos.add(direction.mult(tpf * 2f));
                enemy.setLocalTranslation(interpolatedPos);
            } else {
                enemy.move(direction.mult(tpf * 2f));
            }
        }
    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Disparar") && !keyPressed) {
                disparar();
            }
        }
    };

    private void handleCollisions() {
        // Detección de colisión entre balas y enemigos
        for (Iterator<Spatial> bulletIterator = bulletNode.getChildren().iterator(); bulletIterator.hasNext();) {
            Spatial bullet = bulletIterator.next();
            for (Iterator<Spatial> enemyIterator = enemyNode.getChildren().iterator(); enemyIterator.hasNext();) {
                Spatial enemy = enemyIterator.next();
                if (bullet.getWorldBound().intersects(enemy.getWorldBound())) {
                    enemy.removeFromParent();
                    bullet.removeFromParent();
                    deadEnemies++;
                    deadSound.playInstance(); // Reproducir el sonido de muerte

                    // Incrementar el contador de muertes y actualizar el texto
                    deathCount++;
                    deathCountText.setText("Enemigos: " + deathCount);
                }
            }
        }

        // Detección de colisión entre enemigos y la torre
        for (Iterator<Spatial> enemyIterator = enemyNode.getChildren().iterator(); enemyIterator.hasNext();) {
            Spatial enemy = enemyIterator.next();
            if (enemy.getWorldTranslation().z < -5) { // Comprobamos si el enemigo ha llegado a la coordenada z de la torre
                enemy.removeFromParent(); // Eliminamos el enemigo
                vidaTorre--; // Reducimos la vida de la torre
                damageSound.playInstance(); // Reproducir el sonido de muerte
                vidaText.setText("Vida de la Torre: " + vidaTorre); // Actualizamos el texto de la vida de la torre en pantalla
                switch (vidaTorre) {
                    case 7 -> vidaText.setColor(ColorRGBA.Yellow);
                    case 5 -> vidaText.setColor(ColorRGBA.Orange);
                    case 3 -> vidaText.setColor(ColorRGBA.Red);
                    default -> {
                    }
                }
                if (vidaTorre <= 0) {
                    gameOver(); // Llamamos al método de Game Over si la vida de la torre llega a 0
                }
            }
        }

        // Ajuste del intervalo de aparición de enemigos en función del número de enemigos muertos
        if (deadEnemies > 10) {
            spawnInterval = 2.5f;
        }
        if (deadEnemies > 20) {
            spawnInterval = 2f;
        }
        if (deadEnemies > 30) {
            spawnInterval = 1.5f;
        }
        if (deadEnemies > 40) {
            spawnInterval = 1.25f;
        }
        if (deadEnemies > 50) {
            spawnInterval = 1f;
        }
        if (deadEnemies > 60) {
            spawnInterval = .75f;
        }
        if (deadEnemies > 90) {
            spawnInterval = .5f;
        }
        if (deadEnemies > 150) {
            spawnInterval = .25f;
        }
    }

    private void gameOver() {
        // Lógica para mostrar el mensaje de Game Over y detener el juego
        System.out.println("Game Over");
        // Por ejemplo, puedes mostrar un mensaje en la consola y cerrar la aplicación
        stop(); // Detiene la aplicación
    }

    private void disparar() {
        Spatial bullet = assetManager.loadModel("Models/banana.j3o");
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture bullett = assetManager.loadTexture("Textures/banana.png");
        mat.setTexture("ColorMap", bullett);
        bullet.setMaterial(mat);
        Vector3f startPosition = cam.getLocation();
        bullet.setLocalTranslation(startPosition);
        Vector3f direction = cam.getDirection();
        bulletNode.attachChild(bullet);
        bulletAppState.getPhysicsSpace().add(bullet);
        bullet.addControl(new BulletControl(direction));
        shootingSound.playInstance();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Additional rendering code can be added here
    }

    private void spawnEnemy() {
        Spatial enemy = assetManager.loadModel("Models/cojeno.j3o");
        Material enemym = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture enemyt = assetManager.loadTexture("Textures/cojeno.png");
        enemym.setTexture("ColorMap", enemyt);
        enemy.setMaterial(enemym);
        enemy.rotate(0, 3, 0);
        Vector3f spawnPosition = new Vector3f(FastMath.nextRandomFloat() * 10 - 5, 0.5f, FastMath.nextRandomFloat() * 10 + 15);
        enemy.setLocalTranslation(spawnPosition);
        enemyNode.attachChild(enemy);
        bulletAppState.getPhysicsSpace().add(enemy);
    }
}
