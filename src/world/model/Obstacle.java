/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import config.StaticInitConfig;
import java.awt.Polygon;
import java.awt.Rectangle;
import world.Message;

/**
 *
 * @author boluo
 */
public class Obstacle extends Message {

    private int index;
    private Polygon shape;
    private Rectangle mbr;

    public Obstacle(Polygon shape, int index) {
        this.shape = shape;
        this.index = index;
        if (shape != null) {
            this.mbr = shape.getBounds();
        }
        this.msg_type=Message.OBSTACLE_MSG;
    }

    public Polygon getShape() {
        return shape;
    }

    public void setShape(Polygon shape) {
        this.shape = shape;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Rectangle getMbr() {
        return mbr;
    }

    public void setMbr(Rectangle mbr) {
        this.mbr = mbr;
    }

    @Override
    public String toString() {
        return StaticInitConfig.OBSTACLE_NAME + this.index;
    }
    
    public String getPointsStr()
    {
        int[] xpoints=shape.xpoints;
        int[] ypoints=shape.ypoints;
        int point_num=shape.npoints;
        String result="";
        for(int i=0;i<point_num;i++)
        {
            result+=xpoints[i]+","+ypoints[i]+" ";
        }
        return result.trim();
    }

    @Override
    public int getMsgSize() {
        return 1;
    }

}
