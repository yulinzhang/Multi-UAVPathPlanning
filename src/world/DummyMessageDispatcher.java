/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import world.model.KnowledgeAwareInterface;
import world.model.Target;

/** This is a implementation of no information sharing. It has a dummy implementation and does nothing.
 *
 * @author boluo
 */
public class DummyMessageDispatcher extends MessageDispatcher{

    public DummyMessageDispatcher(KnowledgeAwareInterface intelligent_unit) {
        super(intelligent_unit);
    }

    @Override
    public void register(Integer uav_index, float[] current_loc, Target target) {
    }

    @Override
    public void decideAndSumitMsgToSend() {
    }
    
}
