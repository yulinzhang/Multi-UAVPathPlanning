/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import world.model.Target;

/**
 *
 * @author boluo
 */
public class DummyMessageDispatcher extends MessageDispatcher{

    public DummyMessageDispatcher(World world) {
        super(world);
    }

    @Override
    public void register(Integer uav_index, float[] current_loc, Target target) {
    }

    @Override
    public void decideAndSumitMsgToSend() {
    }
    
}
