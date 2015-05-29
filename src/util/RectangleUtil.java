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

/** This is a tool class and provide tool function to deal with rectangle.
 *
 * @author Yulin_Zhang
 */
public class RectangleUtil {
    
    /** This function generate a rectangle according to its two diagonal points.
     * 
     * @param point1
     * @param point2
     * @return 
     */
    public static Rectangle findMBRRect(float[] point1,float[] point2)
    {
        float[] rect_center=new float[2]; 
        rect_center[0] = (point1[0] + point2[0]) / 2.0f;
        rect_center[1] = (point1[1] + point2[1]) / 2.0f;
        int width = (int) Math.abs(point2[0] - point1[0]);
        int height = (int) Math.abs(point2[1] - point1[1]);
        Rectangle rect = new Rectangle((int) rect_center[0] - width / 2, (int) rect_center[0] - height / 2, width, height);
        return rect;
    }
}
