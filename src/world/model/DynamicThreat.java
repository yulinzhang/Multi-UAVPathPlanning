/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import config.StaticInitConfig;

/**
 *
 * @author boluo
 */
public class DynamicThreat extends Target{
    private float[] coordinates;
    public DynamicThreat(int index,float[] coordinates) {
        super( index, null,StaticInitConfig.DYNAMIC_THREAT_TYPE);
        this.coordinates=coordinates;
    }

    public float[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(float[] coordinates) {
        this.coordinates = coordinates;
    }

    
}
