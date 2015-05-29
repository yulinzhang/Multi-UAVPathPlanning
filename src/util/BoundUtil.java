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

import algorithm.RRT.RRTNode;

/** This is a tool class, which provide many tool functions.
 *
 * @author Yulin_Zhang
 */
public class BoundUtil {
    
    /** check whether the rrt node is within the bound of the world (rectangle).
     * 
     * @param node
     * @param bound_width
     * @param bound_height
     * @return true if within given bound
     */
    public static boolean withinBound(RRTNode node, int bound_width, int bound_height)
    {
        float[] coordinate=node.getCoordinate();
        return withinBound(coordinate[0],coordinate[1],bound_width,bound_height);
    }
    
    /** check whether the given point is within a relaxed bound(100 relaxed).
     * 
     * @param coord_x
     * @param coord_y
     * @param bound_width
     * @param bound_height
     * @return 
     */
    public static boolean withinRelaxedBound(float coord_x,float coord_y,int bound_width, int bound_height)
    {
        if(coord_x>bound_width-100||coord_x<100||coord_y>bound_height-100||coord_y<100)
        {
            return false;
        }
        return true;
    }
    
    /** check  whether the given point is within a bound.
     * 
     * @param coord_x
     * @param coord_y
     * @param bound_width
     * @param bound_height
     * @return 
     */
    public static boolean withinBound(float coord_x,float coord_y,int bound_width, int bound_height)
    {
        if(coord_x>bound_width||coord_x<0||coord_y>bound_height||coord_y<0)
        {
            return false;
        }
        return true;
    }
}
