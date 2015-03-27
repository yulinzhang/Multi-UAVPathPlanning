/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.awt.Image;

/**
 *
 * @author boluo
 */
public class Threat extends EnvConstraint{
    private Circle shape;
    public Threat(Circle shape, int index) {
        super(index,null);
        this.shape=shape;
    }

    public Circle getShape() {
        return shape;
    }

    public void setShape(Circle shape) {
        this.shape = shape;
    }

}
