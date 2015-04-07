/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm.RRT;

/**
 *
 * @author boluo
 */
public class RRTNode {

    private float[] coordinate;
    private float path_lenght_from_root;
    private float distance_from_goal;
    private float current_angle=-1;
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

    public void setCurrent_angle(float current_angle) {
        this.current_angle = current_angle;
    }

    public float getCurrent_angle() {
        return current_angle;
    }

    public int getExpected_time_step() {
        return expected_time_step;
    }

    public void setExpected_time_step(int expected_time_step) {
        this.expected_time_step = expected_time_step;
    }
    
    
}
