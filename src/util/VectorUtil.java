/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author boluo
 */
public class VectorUtil {

    /**
     *
     * @param coordinate_x
     * @param coordinate_y
     * @return the result from 0 to 2PI
     */
    public static float getAngleOfVectorRelativeToXCoordinate(float coordinate_x, float coordinate_y) {
        float length_from_origin = (float) Math.sqrt(Math.pow(coordinate_x, 2) + Math.pow(coordinate_y, 2));
        float angle = (float) Math.acos(coordinate_x / length_from_origin);
        if (coordinate_y < 0) {
            angle = (float) (2 * Math.PI - angle);
        }
        return angle;
    }
    

    public static float getAngleOfTwoVector(float[] coordinate_vector_1, float[] coordinate_vector_2) {
        float vector_1_times_vector_2 = coordinate_vector_1[0] * coordinate_vector_2[0] + coordinate_vector_1[1] * coordinate_vector_2[1];
        float length_of_vector_1 = (float) Math.sqrt(Math.pow(coordinate_vector_1[0], 2) + Math.pow(coordinate_vector_1[1], 2));
        float length_of_vector_2 = (float) Math.sqrt(Math.pow(coordinate_vector_2[0], 2) + Math.pow(coordinate_vector_2[1], 2));
        float angle = (float) Math.acos(vector_1_times_vector_2 / (length_of_vector_1 * length_of_vector_2));
        return angle;
    }

    /**
     *
     * @param angle
     * @return the result from 0 to 2PI
     */
    public static float getNormalAngle(float angle) {
        angle += Math.PI * 4;
        while (angle > Math.PI * 2) {
            angle -= Math.PI * 2;
        }
        return angle;
    }

    /**
     * 
     * @param angle1
     * @param angle2
     * @return  the result from 0 to PI
     */
    public static float getBetweenAngle(float angle1, float angle2) {
        float delta_angle = VectorUtil.getNormalAngle(angle1 - angle2);
        if (delta_angle > Math.PI) {
            delta_angle = (float) Math.PI * 2 - delta_angle;
        }
        return delta_angle;
    }

    public static void main(String[] args) {
        System.out.println(Math.toDegrees(VectorUtil.getAngleOfVectorRelativeToXCoordinate(0, -2)));
                System.out.println(Math.toDegrees(VectorUtil.getBetweenAngle((float)Math.PI*45/180, (float)(255.0/180.0*Math.PI))));
    }
}
