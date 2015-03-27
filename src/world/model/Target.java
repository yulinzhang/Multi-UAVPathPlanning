/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import world.model.EnvConstraint;
import java.awt.Image;

/**
 *
 * @author boluo
 */
public class Target extends EnvConstraint{
    private float[] coordinates;
    public Target(int index,float[] coordinates) {
        super( index, null);
        this.coordinates=coordinates;
    }

    public float[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(float[] coordinates) {
        this.coordinates = coordinates;
    }

    
}
