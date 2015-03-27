/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.awt.Polygon;

/**
 *
 * @author boluo
 */
public class Obstacle extends EnvConstraint {

    private Polygon shape;

    public Obstacle(Polygon shape, int index) {
        super(index, null);
        this.shape=shape;
    }

    public Polygon getShape() {
        return shape;
    }

    public void setShape(Polygon shape) {
        this.shape = shape;
    }



}
