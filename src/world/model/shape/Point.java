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
    
    protected int decision_time_step;
    protected int exptected_time_step;
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

    public int getDecision_time_step() {
        return decision_time_step;
    }

    public void setDecision_time_step(int decision_time_step) {
        this.decision_time_step = decision_time_step;
    }

    public int getExptected_time_step() {
        return exptected_time_step;
    }

    public void setExptected_time_step(int exptected_time_step) {
        this.exptected_time_step = exptected_time_step;
    }

}
