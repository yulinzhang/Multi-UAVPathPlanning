/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author Yulin_Zhang
 */
public class ObtacleUtil {

    /**Analyse KML file to extract ends of obstacle 
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

    public static ArrayList<Obstacle> readObstacleFromResourceKML(String resource_kml_file_path) {
        return readObstacleFromKMLInputStream(ObtacleUtil.class.getResourceAsStream(resource_kml_file_path));
    }

    public static void main(String[] args) {
        readObstacleFromResourceKML("/Users/Yulin_Zhang/Desktop/simple_obstacle_v2.kml");
    }
}
