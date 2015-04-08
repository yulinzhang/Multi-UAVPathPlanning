/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import config.StaticInitConfig;
import world.Message;

/**
 *
 * @author boluo
 */
public class Threat extends Target {
    protected int target_type = 0;
    protected float threat_range=0;
    protected String threat_cap="";
    public Threat(int index, float[] coordinates, int target_type) {
        super(index, coordinates);
        this.target_type=target_type;
        this.msg_type=Message.THREAT_MSG;
    }
    
        @Override
    public String toString()
    {
        return StaticInitConfig.THREAT_NAME+this.index;
    }

    public int getTarget_type() {
        return target_type;
    }

    public void setTarget_type(int target_type) {
        this.target_type = target_type;
    }

    public float getThreat_range() {
        return threat_range;
    }

    public void setThreat_range(float threat_range) {
        this.threat_range = threat_range;
    }

    public String getThreat_cap() {
        return threat_cap;
    }

    public void setThreat_cap(String threat_cap) {
        this.threat_cap = threat_cap;
    }
    
    
}
