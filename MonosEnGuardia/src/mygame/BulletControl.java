/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Jonat
 */
public class BulletControl extends AbstractControl {
    private Vector3f direction;
    private float speed = 10f; // Velocidad de la bala

    public BulletControl(Vector3f direction) {
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
}
