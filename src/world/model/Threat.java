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
public class Threat extends Target {
    protected int target_type = 0;
    public Threat(int index, float[] coordinates, int target_type) {
        super(index, coordinates);
        this.target_type=target_type;
    }
}
