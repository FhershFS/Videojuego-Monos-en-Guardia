package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

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
        // Creamos el suelo del escenario
        Box ground = new Box(10, 0.1f, 10);
        Geometry groundGeom = new Geometry("Ground", ground);
        Material groundMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        groundMat.setColor("Color", ColorRGBA.Green);
        groundGeom.setMaterial(groundMat);
        rootNode.attachChild(groundGeom);

        // Creamos una torre (que luego será la Banana Dorada)
        Box tower = new Box(1, 2, 1);
        Geometry towerGeom = new Geometry("Tower", tower);
        Material towerMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        towerMat.setColor("Color", ColorRGBA.Yellow);
        towerGeom.setMaterial(towerMat);
        towerGeom.setLocalTranslation(0, 1, 0); // Posicionamos la torre
        rootNode.attachChild(towerGeom);

        // Aquí puedes agregar más geometrías para representar otros elementos del escenario
        // como los conejos, monos, enemigos, etc.

        // Agregamos la luz al escenario
        initLight();
    }

    private void initLight() {
        // Configuramos la luz ambiental
        rootNode.addLight(new AmbientLight());
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
