/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import world.Message;

/**
 *
 * @author boluo
 */
public class Target extends Message{
    protected float[] coordinates;
    protected int index;
    public Target(int index,float[] coordinates) {
        this.index=index;
        this.coordinates=coordinates;
    }

    public float[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(float[] coordinates) {
        this.coordinates = coordinates;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
}