package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import java.util.Iterator;
import com.jme3.scene.Node;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends SimpleApplication {

    private Node enemyNode;
    private Node targetNode;
    private float spawnTimer = 0f;
    private float spawnInterval = 1.0f;
    private float jumpPhase = 0f;

    private int vidaTorre = 10; // Vida inicial de la torre
    private Spatial model;

    int deadEnemies = 0;
    private Node bananaNode;
    private BitmapText deathCountText;
    private BitmapText vidaText;

    int deathCount = 0;
    private AudioNode shootingSound;
    private AudioNode damageSound;
    private AudioNode explosionSound;
    private AudioNode gameSound;
    private AudioNode loadSound;
    private Picture crosshair;
    private Node bulletNode;
    private BulletAppState bulletAppState;
    private AudioNode deadSound; // Declaración del AudioNode

    private float pauseTimer = 0f;
    private boolean isGameOver = false;
    private float explosiveCooldown = 20f; // Cooldown de 20 segundos
    private float cooldownTime = 0f; // Tiempo restante del cooldown
    private BitmapText cooldownText; // Texto para mostrar el cooldown
    private float explosiveTimer = 0f;
    private boolean canShootExplosive = true;

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Monos en Guardia");
        settings.setSettingsDialogImage("Interface/INICIO_X.jpeg");
        settings.setFullscreen(true);
        settings.setResolution(1024, 768);
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

        cam.setLocation(new Vector3f(-3.5f, 4f, 9f));
        cam.lookAt(new Vector3f(2, 0, 40), Vector3f.UNIT_Y);

        flyCam.setMoveSpeed(0); // Deshabilitar el movimiento con las teclas
        flyCam.setZoomSpeed(0); // Deshabilitar el zoom con las teclas

        initScene();
        enemyNode = new Node("enemyNode");
        rootNode.attachChild(enemyNode);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

        // Cambiar el color de fondo de la escena
        viewPort.setBackgroundColor(ColorRGBA.fromRGBA255(1, 0, 5, 255));

        viewPort.addProcessor(fpp);

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

        explosionSound = new AudioNode(assetManager, "Sounds/explosion.wav", false);
        explosionSound.setPositional(false);
        explosionSound.setLooping(false);
        explosionSound.setPitch(0.5f);
        rootNode.attachChild(explosionSound);

        deadSound = new AudioNode(assetManager, "Sounds/dead.wav", false);
        deadSound.setPositional(false);
        deadSound.setLooping(false);
        deadSound.setVolume(1f);
        deadSound.setPitch(.5f);
        rootNode.attachChild(deadSound);
        
        loadSound = new AudioNode(assetManager, "Sounds/load.wav", false);
        loadSound.setPositional(false);
        loadSound.setLooping(false);
        loadSound.setVolume(2f);
        rootNode.attachChild(loadSound);

        damageSound = new AudioNode(assetManager, "Sounds/damage.wav", false);
        damageSound.setPositional(false);
        damageSound.setLooping(false);
        damageSound.setVolume(.75f);
        damageSound.setPitch(.75f);
        rootNode.attachChild(damageSound);
        
        gameSound = new AudioNode(assetManager, "Sounds/gameover.wav", false);
        gameSound.setPositional(false);
        gameSound.setLooping(false);
        gameSound.setVolume(1.5f);
        rootNode.attachChild(gameSound);

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


        inputManager.addMapping("Disparar", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "Disparar");

        inputManager.addMapping("Camara1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("Camara2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("Camara3", new KeyTrigger(KeyInput.KEY_3));

        inputManager.addListener(actionListener, "Camara1", "Camara2", "Camara3");

        bulletNode = new Node("bulletNode");
        rootNode.attachChild(bulletNode);

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // Inicializar el contador de muertes
        deathCountText = new BitmapText(guiFont, false);
        deathCountText.setSize(guiFont.getCharSet().getRenderedSize());
        deathCountText.setColor(ColorRGBA.Yellow); // Color del texto
        deathCountText.setText("Enemigos derrotados: 0");
        deathCountText.setLocalTranslation(10, settings.getHeight() - 10, 0); // Posición del texto en la pantalla
        guiNode.attachChild(deathCountText);

        vidaText = new BitmapText(guiFont, false);
        vidaText.setSize(guiFont.getCharSet().getRenderedSize());
        vidaText.setColor(ColorRGBA.Green); // Color del texto
        vidaText.setText("Vida de la Torre: " + vidaTorre); // Texto inicial
        vidaText.setLocalTranslation(settings.getWidth() - 180, settings.getHeight() - 10, 0); // Posición del texto en la pantalla
        guiNode.attachChild(vidaText);

        BitmapText instructionsText = new BitmapText(guiFont, false);
        instructionsText.setSize(guiFont.getCharSet().getRenderedSize());
        instructionsText.setColor(ColorRGBA.White); // Color del texto
        instructionsText.setText("""
                                 Controles:
                                 Disparar: Click Izquierdo
                                 Bomba: Click Derecho
                                 Cambiar c\u00e1mara:
                                    - Camara 1: Tecla 1
                                    - Camara 2: Tecla 2
                                    - Camara 3: Tecla 3"""); // Texto de las instrucciones
        instructionsText.setLocalTranslation(10, 150, 0); // Posición del texto en la pantalla
        guiNode.attachChild(instructionsText);

        inputManager.addMapping("DispararExplosivo", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, "DispararExplosivo");
        
        // Inicializar el texto del cooldown
        cooldownText = new BitmapText(guiFont, false);
        cooldownText.setSize(guiFont.getCharSet().getRenderedSize());
        cooldownText.setColor(ColorRGBA.Red); // Color del texto
        cooldownText.setText(""); // Inicialmente vacío
        cooldownText.setSize(30);
        cooldownText.setLocalTranslation(settings.getWidth() / 2.5f, settings.getHeight() / 2f, 0); // Posición del texto en la pantalla
        guiNode.attachChild(cooldownText);
    }

    private void initScene() {

        AudioNode music = new AudioNode(assetManager, "Sounds/musica.wav", true);
        music.setPositional(false);
        music.setLooping(true);
        music.setVolume(0.5f);
        rootNode.attachChild(music);
        music.play();
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
        if (isGameOver) {
            pauseTimer += tpf;
            if (pauseTimer >= 3f) { // Espera 3 segundos antes de detener el juego
                stop();
            }
            return; // Salir del método simpleUpdate para detener la actualización del juego
        }

        // Llama al método handleCollisions() para verificar las colisiones
        handleCollisions();

        jumpPhase += tpf; // Incrementar la fase del salto

        for (Spatial enemy : enemyNode.getChildren()) {
            Vector3f enemyPos = enemy.getWorldTranslation();
            Vector3f targetPos = targetNode.getWorldTranslation();
            Vector3f direction = targetPos.subtract(enemyPos).normalizeLocal();
            float distance = enemyPos.distance(targetPos);

            if (distance > 1f) {
                Vector3f interpolatedPos = enemyPos.add(direction.mult(tpf * 2f));

                // Aplicar el movimiento de salto usando una onda sinusoidal
                float jumpHeight = 0.025f; // Altura del salto
                float jumpSpeed = 10f; // Velocidad del salto
                float verticalOffset = FastMath.sin(jumpPhase * jumpSpeed) * jumpHeight;

                interpolatedPos.y += verticalOffset; // Aplicar la componente vertical del salto
                enemy.setLocalTranslation(interpolatedPos);
            } else {
                enemy.move(direction.mult(tpf * 2f));
            }
        }
          if (!canShootExplosive) {
        cooldownTime -= tpf; // Reducir el tiempo restante del cooldown
        if (cooldownTime <= 0) {
            cooldownTime = 0; // Asegurar que el tiempo no sea negativo
            canShootExplosive = true; // Restablecer la capacidad de disparar explosivos
        }
    } else {
        // Si puede disparar explosivos, restablecer el tiempo de cooldown
        cooldownTime = explosiveCooldown;
    }

    // Actualizar el texto del cooldown en pantalla
    cooldownText.setText("" + String.format("%.1f", cooldownTime));
    }

    private final ActionListener actionListener = new ActionListener() {
    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
        enqueue(() -> {
            if (name.equals("Disparar") && !keyPressed) {
                disparar();
            } else if (name.equals("DispararExplosivo") && !keyPressed) {
                dispararExplosivo();
            }
              if (name.equals("DispararExplosivo") && !keyPressed && canShootExplosive) {
                dispararExplosivo();
                canShootExplosive = false; // Desactiva la capacidad de disparar explosivos hasta que termine el cooldown
            }
            if (keyPressed) {
                switch (name) {
                    case "Camara1":
                        cam.setLocation(new Vector3f(-3.5f, 4f, 9f));
                        cam.lookAt(new Vector3f(2, 0, 40), Vector3f.UNIT_Y);
                        break;
                    case "Camara2":
                        cam.setLocation(new Vector3f(5f, 4f, 4f));
                        cam.lookAt(new Vector3f(2, 0, 40), Vector3f.UNIT_Y);
                        break;
                    case "Camara3":
                        cam.setLocation(new Vector3f(-3f, 4f, 2f));
                        cam.lookAt(new Vector3f(2, 0, 40), Vector3f.UNIT_Y);
                        break;
                    default:
                        break;
                }
            }
        });
    }
};


    private void handleCollisions() {
        // Detección de colisión entre balas y enemigos
         for (Iterator<Spatial> bulletIterator = bulletNode.getChildren().iterator(); bulletIterator.hasNext();) {
        Spatial bullet = bulletIterator.next();
        for (Iterator<Spatial> enemyIterator = enemyNode.getChildren().iterator(); enemyIterator.hasNext();) {
            Spatial enemy = enemyIterator.next();
            if (bullet.getWorldBound().intersects(enemy.getWorldBound())) {
                if (bullet.getName().equals("explosion")) {
                    // Crea una explosión en la posición del enemigo
                    crearExplosion(enemy.getWorldTranslation());
                    // Elimina el enemigo
                    enemy.removeFromParent();
                    deadEnemies++;
                    deadSound.playInstance();
                    deathCount++;
                    deathCountText.setText("Enemigos derrotados: " + deathCount);
                } else {
                    enemy.removeFromParent();
                    bullet.removeFromParent();
                    deadEnemies++;
                    deadSound.playInstance();
                    deathCount++;
                    deathCountText.setText("Enemigos derrotados: " + deathCount);
                }
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
                    case 7 ->
                        vidaText.setColor(ColorRGBA.Yellow);
                    case 5 ->
                        vidaText.setColor(ColorRGBA.Orange);
                    case 3 ->
                        vidaText.setColor(ColorRGBA.Red);
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
            spawnInterval = 1.75f;
        }
        if (deadEnemies > 40) {
            spawnInterval = 1.5f;
        }
        if (deadEnemies > 50) {
            spawnInterval = 1.25f;
            explosiveCooldown = 15f;            
        }
        if (deadEnemies > 60) {
            spawnInterval = 1f;
        }
        if (deadEnemies > 80) {
            spawnInterval = .75f;
        }
        if (deadEnemies > 120) {
            spawnInterval = .5f;
            explosiveCooldown = 12f; 
        }
        if (deadEnemies > 150) {
            spawnInterval = .25f;
        }
        if (deadEnemies > 200) {
            spawnInterval = .15f;
            explosiveCooldown = 10f; 
        }
        if (deadEnemies > 250) {
            spawnInterval = .10f;
            explosiveCooldown = 5f; 
        }
    }

    private void gameOver() {
        // Muestra el mensaje de Game Over
        BitmapText gameOverText = new BitmapText(guiFont, false);
        gameOverText.setSize(guiFont.getCharSet().getRenderedSize() * 4);
        gameOverText.setColor(ColorRGBA.Red); // Color del texto
        gameOverText.setText("¡Game Over!"); // Texto a mostrar
        gameOverText.setLocalTranslation(settings.getWidth() / 2f - gameOverText.getLineWidth() / 2f, settings.getHeight() / 2f, 0); // Posición del texto en la pantalla
        guiNode.attachChild(gameOverText);

        // Iniciar el temporizador de pausa
        pauseTimer = 0f;
        isGameOver = true;
        
        gameSound.playInstance();
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
        shootingSound.setPitch(1f);
        shootingSound.playInstance();
    }

    private void dispararExplosivo() {
        if (!canShootExplosive) {
            loadSound.playInstance();
            return;
        }

        // Crear la esfera explosiva
        shootingSound.setPitch(.75f);
        Sphere bullet = new Sphere(10, 10, .25f);
        Geometry bulletGeometry = new Geometry("explosion", bullet);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Orange);
        bulletGeometry.setMaterial(mat);
        Vector3f startPosition = cam.getLocation();
        bulletGeometry.setLocalTranslation(startPosition);
        Vector3f direction = cam.getDirection();
        bulletNode.attachChild(bulletGeometry);
        bulletAppState.getPhysicsSpace().add(bulletGeometry);
        bulletGeometry.addControl(new BulletControl(direction));
 
        shootingSound.playInstance();    
    }
    
      private void crearExplosion(Vector3f posicion) {
        Sphere explosionSphere = new Sphere(30, 30, 3f); // Ajusta el radio de la esfera de explosión según tus necesidades
        Geometry explosionGeometry = new Geometry("explosion", explosionSphere);
        Material explosionMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        explosionMaterial.setColor("Color", ColorRGBA.Yellow);
        explosionGeometry.setMaterial(explosionMaterial);
        explosionGeometry.setLocalTranslation(posicion);
        rootNode.attachChild(explosionGeometry);
        explosionSound.playInstance(); // Reproduce el sonido de la explosión

        // Elimina todos los enemigos dentro del área de la explosión
        for (Iterator<Spatial> enemyIterator = enemyNode.getChildren().iterator(); enemyIterator.hasNext();) {
            Spatial enemy = enemyIterator.next();
            if (enemy.getWorldTranslation().distance(posicion) < 4f) { // Ajusta el radio del área de explosión según tus necesidades
                enemy.removeFromParent();
                deadEnemies++;
                deathCount++;
                deathCountText.setText("Enemigos derrotados: " + deathCount);
            }
        }

        // Programa la eliminación de la esfera de explosión después de un cierto tiempo
        Timer timer = new Timer();
        timer.schedule(new ExplosionTimerTask(explosionGeometry), 50); // Ajusta el tiempo de vida útil de la esfera de explosión (en milisegundos)
    }

    // Clase interna para la tarea de temporizador de la explosión
    class ExplosionTimerTask extends TimerTask {
        private final Geometry explosionGeometry;

        public ExplosionTimerTask(Geometry explosionGeometry) {
            this.explosionGeometry = explosionGeometry;
        }

        @Override
        public void run() {
            // Elimina la esfera de explosión
            explosionGeometry.removeFromParent();
        }
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
