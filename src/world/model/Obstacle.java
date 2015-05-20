/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import config.StaticInitConfig;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import world.Message;

/**
 *
 * @author boluo
 */
public class Obstacle extends Message implements Serializable {

    private int index;
    private Polygon shape;
    private Rectangle mbr;

    public Obstacle(Polygon shape, int index) {
        this.shape = shape;
        this.index = index;
        if (shape != null) {
            this.mbr = shape.getBounds();
        }
        this.msg_type = Message.OBSTACLE_MSG;
    }

    public Polygon getShape() {
        return shape;
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

    @Override
    public String toString() {
        return StaticInitConfig.OBSTACLE_NAME + this.index;
    }

    public String getPointsStr() {
        int[] xpoints = shape.xpoints;
        int[] ypoints = shape.ypoints;
        int point_num = shape.npoints;
        String result = "";
        for (int i = 0; i < point_num; i++) {
            result += xpoints[i] + "," + ypoints[i] + " ";
        }
        return result.trim();
    }

    @Override
    public int getMsgSize() {
        return 1;
    }

    public Object deepClone() {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(this);
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            return (oi.readObject());
        } catch (IOException ex) {
            Logger.getLogger(Target.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Target.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Obstacle) {
            Obstacle obs = (Obstacle) obj;
            return this.mbr.equals(obs.mbr);
        }else{
            return false;
        }
    }
}
