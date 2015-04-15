package algorithm.rrt1.rrtalg;



import algorithm.rrt1.domain.Domain;
import world.model.shape.DubinsCurve;
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
public class MyRRTStarDubin extends RRTStar<Point,DubinsCurve>{
    
    
    public MyRRTStarDubin(Domain<Point, DubinsCurve> domain, Point initialState, double initialRadius, double minRadius, double maxRadius) {
        super(domain, initialState, initialRadius, minRadius, maxRadius);
    }
}
