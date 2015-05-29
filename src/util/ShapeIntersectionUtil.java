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

import java.awt.Rectangle;
import world.model.shape.Circle;

/** This is a tool class and it provides tool function to check whether two shape intersected with each other.
 *
 * @author Yulin_Zhang
 */
public class ShapeIntersectionUtil {
    
    /** check whether the given line intersected with given rectangle.
     * 
     * @param rect
     * @param line_starting_point
     * @param line_ending_point
     * @return 
     */
    public static boolean isIntersected(Rectangle rect, float[] line_starting_point, float[] line_ending_point)
    {
        return rect.intersectsLine(line_starting_point[0], line_starting_point[1], line_ending_point[0], line_ending_point[1]);
    }
    
    
    /** check whether the given line intersects with given circle.
     * 
     * @param circle
     * @param line_starting_point
     * @param line_ending_point
     * @return 
     */
    public static boolean isIntersected(Circle circle,float[] line_starting_point, float[] line_ending_point)
    {
        return circle.getCollision_rect().intersects(line_starting_point[0], line_starting_point[1], line_ending_point[0], line_ending_point[1]);
    }
}
