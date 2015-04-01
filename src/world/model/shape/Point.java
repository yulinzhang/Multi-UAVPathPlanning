/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model.shape;

/**
 *
 * @author boluo
 */
public class Point {
    protected double x;
    protected double y;
    protected double z=0;
    private static final Point zero = new Point(0, 0,0);

    public static Point zero() {
        return zero;
    }

    public Point() {
        super();
    }

    public Point(double x, double y, double z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public double[] toDoubleArray() {
        return new double[]{x, y};
    }

    public float[] toFloatArray(){
        return new float[]{(float)x,(float)y};
    }
    
    public double getYaw() {
        return z;
    }

    public void setYaw(double z) {
        this.z = z;
    }
       

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

}
