/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import world.model.Obstacle;
import world.model.Target;
import world.model.WorldKnowledge;
import world.model.shape.Point;
import world.uav.UAV;

/**
 *
 * @author Yulin_Zhang
 */
public class RegisteredMessageDispatcher implements MessageDispatcher{
    private World world;
    private Map<Integer,Rectangle> gis_rect_registered;
    
    public RegisteredMessageDispatcher(World world)
    {
        this.world=world;
        gis_rect_registered=new HashMap<Integer,Rectangle>();
    }

    public void roleBasedRegister(Target target)
    {
        
    }
    
    public void gisBasedRegister(int uav_index,Target target, Point uav_current_waypoint)
    {
        float[] target_loc=target.getCoordinates();
        float[] uav_loc=uav_current_waypoint.toFloatArray();
        float[] rect_center=new float[2];
        rect_center[0]=(target_loc[0]+uav_loc[0])/2.0f;
        rect_center[1]=(target_loc[1]+uav_loc[1])/2.0f;
        int width=(int)Math.abs(target_loc[0]-uav_loc[0]);
        int height=(int)Math.abs(target_loc[1]-uav_loc[1]);
        Rectangle rect=new Rectangle((int)rect_center[0]-width/2,(int)rect_center[0]-height/2,width,height);
        this.gis_rect_registered.put(uav_index, rect);
    }
    
    @Override
    public void dispatch() {
        //TODO:
        LinkedList<UAV> attackers=new LinkedList<UAV>();
        for(UAV attacker:attackers)
        {
            WorldKnowledge kb=attacker.getKb();
            List<Obstacle> obstacles=kb.getObstacles();
            for(Obstacle obs:obstacles)
            {
                //TODO
            }
        }
        
        MessageDispatcher.clearRecvMsgList();
    }

    @Override
    public void getNumOfMsgLatestSent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getTotalNumOfMsgSent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
