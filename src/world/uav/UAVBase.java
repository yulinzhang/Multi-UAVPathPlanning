/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.uav;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.ImageUtil;

/**
 *
 * @author boluo
 */
public class UAVBase {
    private float[] coordinate;
    private int base_width;
    private int base_height;
    private BufferedImage image;

    public UAVBase(float[] coordinate, int base_width, int base_height) {
        try {
            this.coordinate = coordinate;
            this.base_width = base_width;
            this.base_height = base_height;
            image=ImageUtil.retrieveImage("/resources/radar2.jpg");
        } catch (IOException ex) {
            Logger.getLogger(UAVBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public float[] getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(float[] coordinate) {
        this.coordinate = coordinate;
    }

    public int getBase_width() {
        return base_width;
    }

    public void setBase_width(int base_width) {
        this.base_width = base_width;
    }

    public int getBase_height() {
        return base_height;
    }

    public void setBase_height(int base_height) {
        this.base_height = base_height;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
    
    
}
