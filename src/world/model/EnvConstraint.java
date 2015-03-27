/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import java.awt.Image;

/**
 *
 * @author boluo
 */
public class EnvConstraint {

//    protected float[] coordinates;
    protected int index;
    protected Image img;
    protected int img_width;
    protected int img_height;

    public EnvConstraint(int index,Image img) {
//        this.coordinates = coordinates;
        this.index=index;
        this.img=img;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }
    
}
