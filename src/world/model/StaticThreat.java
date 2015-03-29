/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import config.StaticInitConfig;
import world.model.EnvConstraint;
import java.awt.Image;

/**
 *
 * @author boluo
 */
public class StaticThreat extends Target{
    public StaticThreat(int index,float[] coordinates) {
        super( index, null,StaticInitConfig.STATIC_THREAT_TYPE);
        this.coordinates=coordinates;
    }
   
}
