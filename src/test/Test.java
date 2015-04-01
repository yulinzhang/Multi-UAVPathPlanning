/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;

/**
 *
 * @author boluo
 */
public class Test {
    private static Logger logger=Logger.getLogger(Test.class);  
    public static void main(String[] args) throws IOException {
        logger.debug("begin to generate");
//        grayImage();
    }

    public static void grayImage() throws IOException {
        File file = new File("C:\\Users\\boluo\\Desktop\\map\\militarymap.jpg");
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
            }
        }

        File newFile = new File("C:\\Users\\boluo\\Desktop\\map\\militarymap-dealed1.jpg");
        ImageIO.write(grayImage, "jpg", newFile);
    }
}
