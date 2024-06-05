package mygame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class ExplosiveBulletControl extends AbstractControl {
    private Vector3f direction;
    private float speed = 10f; // Velocidad de la bala
    private float explosionRadius = 3f; // Radio de la explosión

    public ExplosiveBulletControl(Vector3f direction) {
        this.direction = direction;
    }

    @Override
    protected void controlUpdate(float tpf) {
        spatial.move(direction.mult(tpf * speed));
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Este método no se utiliza en este ejemplo
    }

    @Override
public void setSpatial(com.jme3.scene.Spatial spatial) {
    super.setSpatial(spatial);
    if (spatial != null) {
        // Obtener el control de tipo RigidBodyControl
        RigidBodyControl rigidBodyControl = spatial.getControl(com.jme3.bullet.control.RigidBodyControl.class);
        // Verificar si el control no es null
        if (rigidBodyControl != null) {
            // Establecer el grupo de colisión
            rigidBodyControl.setCollisionGroup(1);
            // Establecer los grupos de colisión con los que puede interactuar
            rigidBodyControl.setCollideWithGroups(2);
        } else {
            // Manejar el caso donde no se encuentra ningún control RigidBodyControl
            System.out.println("No se encontró ningún control RigidBodyControl en el Spatial.");
        }
    }
}

    @Override
    public void collideWith(com.jme3.scene.Spatial other) {
        if (other.getName().equals("enemyNode")) {
            // Obtener la posición de la bala y del enemigo
            Vector3f bulletPosition = spatial.getWorldTranslation();
            Vector3f enemyPosition = other.getWorldTranslation();

            // Calcular la distancia entre la bala y el enemigo
            float distance = bulletPosition.distance(enemyPosition);

            // Si el enemigo está dentro del radio de la explosión
            if (distance <= explosionRadius) {
                // Eliminar el enemigo
                other.removeFromParent();
            }
        }
    }
}
