/* 
 * Copyright (c) Yulin Zhang
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package world.model.shape;

import world.Message;

/**
 *
 * @author Yulin_Zhang
 */
public class Point{
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
