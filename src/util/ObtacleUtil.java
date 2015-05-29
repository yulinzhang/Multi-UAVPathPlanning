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

import java.awt.Polygon;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import world.model.Obstacle;

/** This is a tool class and provide tool functions to deal with obstacle, which is written by kml 2.0.
 *
 * @author Yulin_Zhang
 */
public class ObtacleUtil {

    /** extract the obstacles from kml input stream. Since the obstacle is represented by a polygon, it extract all the points of the polygon and generate the obstacle object.
     * 
     * @param kml_input_stream
     * @return 
     */
    private static ArrayList<Obstacle> readObstacleFromKMLInputStream(InputStream kml_input_stream) {
        ArrayList<Obstacle> obstacle_list = new ArrayList<Obstacle>();
        int obstacle_index = 0;
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(kml_input_stream);
            Element root = document.getRootElement();
            Element folder = root.element("Document").element("Folder");
            Iterator<Element> placemark_iter = folder.elementIterator();
            while (placemark_iter.hasNext()) {
                Element placemark_element = (Element) placemark_iter.next();
                Element coordinate_element = placemark_element.element("Polygon").element("outerBoundaryIs").element("LinearRing").element("coordinates");
                String coordinate_text = coordinate_element.getText();
                Polygon obstacle_polygon_shape = new Polygon();
                String[] coordinates = coordinate_text.split("\n");
                for (String coordinate : coordinates) {
                    String[] coordinate_x_y = coordinate.split(",");
                    obstacle_polygon_shape.addPoint(StringUtil.StringToFloat(coordinate_x_y[0]).intValue(), 600 - Math.abs(StringUtil.StringToFloat(coordinate_x_y[1]).intValue()));
                }
                Obstacle obstacle = new Obstacle(obstacle_polygon_shape, obstacle_index);
                obstacle_list.add(obstacle);
                obstacle_index++;
            }
        } catch (DocumentException ex) {
            Logger.getLogger(ObtacleUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obstacle_list;
    }

    /** extract the obstacles from external kml file.
     * 
     * @param external_kml_file_path
     * @return 
     */
    public static ArrayList<Obstacle> readObstacleFromExternalKML(String external_kml_file_path) {
        File kml_file = new File(external_kml_file_path);
        try {
            FileInputStream kml_input_stream = new FileInputStream(kml_file);
            return readObstacleFromKMLInputStream(kml_input_stream);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ObtacleUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /** extract the obstacles from the resource file in the jar.
     * 
     * @param resource_kml_file_path
     * @return 
     */
    public static ArrayList<Obstacle> readObstacleFromResourceKML(String resource_kml_file_path) {
        return readObstacleFromKMLInputStream(ObtacleUtil.class.getResourceAsStream(resource_kml_file_path));
    }
}
