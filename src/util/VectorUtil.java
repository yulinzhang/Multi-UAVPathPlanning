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
package util;

/** This is a tool class and provides tool function to deal with vectors.
 *
 * @author Yulin_Zhang
 */
public class VectorUtil {

    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(VectorUtil.class);

    /** This function calculate the angle between given vector and x coordinate.
     *
     * @param coordinate_x
     * @param coordinate_y
     * @return the result from 0 to 2PI
     */
    public static double getAngleOfVectorRelativeToXCoordinate(double coordinate_x, double coordinate_y) {
        float length_from_origin = (float) Math.sqrt(Math.pow(coordinate_x, 2) + Math.pow(coordinate_y, 2));
        double angle = Math.acos(coordinate_x / length_from_origin);
        if (coordinate_y < 0) {
            angle = (2 * Math.PI - angle);
        }
        return angle;
    }

    /** This function calculate the angle between two vectors.
     * 
     * @param coordinate_vector_1
     * @param coordinate_vector_2
     * @return 
     */
    public static double getAngleOfTwoVector(float[] coordinate_vector_1, float[] coordinate_vector_2) {
        float vector_1_times_vector_2 = coordinate_vector_1[0] * coordinate_vector_2[0] + coordinate_vector_1[1] * coordinate_vector_2[1];
        float length_of_vector_1 = (float) Math.sqrt(Math.pow(coordinate_vector_1[0], 2) + Math.pow(coordinate_vector_1[1], 2));
        float length_of_vector_2 = (float) Math.sqrt(Math.pow(coordinate_vector_2[0], 2) + Math.pow(coordinate_vector_2[1], 2));
        float angle = (float) Math.acos(vector_1_times_vector_2 / (length_of_vector_1 * length_of_vector_2));
        return angle;
    }

    /** This function translate any angle to normal angle, which is in [0, 2PI].
     *
     * @param angle
     * @return the result from 0 to 2PI
     */
    public static double getNormalAngle(double angle) {
        angle += Math.PI * 4;
        while (angle > Math.PI * 2) {
            angle -= Math.PI * 2;
        }
        return angle;
    }

    /** This function calculate the included angle between two angles.
     *
     * @param angle1
     * @param angle2
     * @return the result from 0 to PI
     */
    public static double getBetweenAngle(double angle1, double angle2) {
        double delta_angle = VectorUtil.getNormalAngle(angle1 - angle2);
        if (delta_angle > Math.PI) {
            delta_angle = (float) Math.PI * 2 - delta_angle;
        }
        return delta_angle;
    }
}
