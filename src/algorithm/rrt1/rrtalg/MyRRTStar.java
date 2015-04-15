package algorithm.rrt1.rrtalg;



import algorithm.rrt1.domain.Domain;
import java.util.ArrayList;
import util.DistanceUtil;
import world.model.Obstacle;
import world.model.shape.Point;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author boluo
 */
public class MyRRTStar extends RRTStar<Point,Edge>{
    
    
    public MyRRTStar(Domain<Point, Edge> domain, Point initialState, double initialRadius, double minRadius, double maxRadius) {
        super(domain, initialState, initialRadius, minRadius, maxRadius);
    }
}
