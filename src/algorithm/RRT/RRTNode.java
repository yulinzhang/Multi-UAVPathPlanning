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
package algorithm.RRT;

/**
 *
 * @author Yulin_Zhang
 */
public class RRTNode {

    private float[] coordinate;
    private float path_lenght_from_root;
    private float distance_from_goal;
    private double current_angle=-1;
    private int expected_time_step=-1;

    public RRTNode() {
        coordinate = new float[]{-1f, -1f};
        path_lenght_from_root = -1f;
        distance_from_goal = Float.POSITIVE_INFINITY;
    }

    public RRTNode(float x_coordinate, float y_coordinate) {
        coordinate = new float[]{x_coordinate, y_coordinate};
        path_lenght_from_root = -1;
        distance_from_goal = Float.POSITIVE_INFINITY;
    }

    public RRTNode(float x_coordinate, float y_coordinate, float path_lenght_from_root, float distance_from_goal) {
        coordinate = new float[]{x_coordinate, y_coordinate};
        this.path_lenght_from_root = path_lenght_from_root;
        this.distance_from_goal = distance_from_goal;
    }

    public void setCoordinate(float x_coordinate, float y_coordinate) {
        coordinate[0] = x_coordinate;
        coordinate[1] = y_coordinate;
    }

    public float[] getCoordinate() {
        return coordinate;
    }


    public float getPath_lenght_from_root() {
        return path_lenght_from_root;
    }

    public void setPath_lenght_from_root(float path_lenght_from_root) {
        this.path_lenght_from_root = path_lenght_from_root;
    }

    public float getDistance_from_goal() {
        return distance_from_goal;
    }

    public void setDistance_from_goal(float distance_from_goal) {
        this.distance_from_goal = distance_from_goal;
    }

    public String toString() {
        return "N = (" + coordinate[0] + "," + coordinate[1] + ")";
    }

    public void setCurrent_angle(double current_angle) {
        this.current_angle = current_angle;
    }

    public double getCurrent_angle() {
        return current_angle;
    }

    public int getExpected_time_step() {
        return expected_time_step;
    }

    public void setExpected_time_step(int expected_time_step) {
        this.expected_time_step = expected_time_step;
    }
    
    
}
