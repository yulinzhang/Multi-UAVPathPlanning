/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.PrintUtil;
import config.StaticInitConfig;
import java.awt.Polygon;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import util.StringUtil;
import world.model.shape.Point;

/**
 *
 * @author boluo
 */
public class OntologyBasedKnowledge_ObstacleSpecified extends OntologyBasedKnowledge {
  
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(OntologyBasedKnowledge_ObstacleSpecified.class);

    

    public OntologyBasedKnowledge_ObstacleSpecified() {
       super();
    }

    public void initClassAndProperty() {
        rdf_type = ontology_based_knowledge.createObjectProperty(rdf_ns + "type");
        //class and property for obstacle
        Obstacle_Class = ontology_based_knowledge.createClass(base_ns + "Obstacle");
        Region_Class = ontology_based_knowledge.createClass(base_ns + "Region");
        Polygon_Class = ontology_based_knowledge.createClass(base_ns + "Polygon");
        LowerBoundOfRegion_Class = ontology_based_knowledge.createClass(base_ns + "LowerBoundOfRegion");
        UpperBoundOfRegion_Class = ontology_based_knowledge.createClass(base_ns + "UpperBoundOfRegion");

        has_region = ontology_based_knowledge.createObjectProperty(base_ns + "hasRegion");
        has_polygon = ontology_based_knowledge.createObjectProperty(base_ns + "hasPolygon");
        has_lowerbound = ontology_based_knowledge.createObjectProperty(base_ns + "hasLowerBound");
        has_upperbound = ontology_based_knowledge.createObjectProperty(base_ns + "hasUpperBound");

        has_points = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasPoints");
        hasMaxXCoordinate = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasMaxXCoordinate");
        hasMaxYCoordinate = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasMaxYCoordinate");
        hasMinXCoordinate = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasMinXCoordinate");
        hasMinYCoordinate = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasMinYCoordinate");
        hasObstacleIndex = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasObstacleIndex");

        //class and property for threat
        Threat_Class = ontology_based_knowledge.createClass(base_ns + "Threat");

        hasThreatCenter = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasThreatCenter");
        hasThreatSpeed = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasThreatSpeed");
        hasThreatRange = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasThreatRange");
        hasThreatCapability = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasThreatCapability");
        hasThreatIndex = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasThreatIndex");
        threatEnabled = ontology_based_knowledge.createDatatypeProperty(base_ns + "threatEnabled");

        //class and property for conflict
        Conflict_Class = ontology_based_knowledge.createClass(base_ns + "Conflict");
        hasConflictCenter = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasConflictCenter");
        hasConflictRange = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasConflictRange");
        hasExpectedConflictTime = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasExpectedConflictTime");
        hasDecidedConflictTime = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasDecidedConflictTime");
        conflictFromRobot = ontology_based_knowledge.createDatatypeProperty(base_ns + "conflictFromRobot");
    }

    public static void main(String[] args) {
        OntologyBasedKnowledge_ObstacleSpecified kb = new OntologyBasedKnowledge_ObstacleSpecified();
        Polygon polygon = new Polygon();
        polygon.addPoint(0, 1);
        polygon.addPoint(2, 4);
        polygon.addPoint(3, 6);
        Obstacle obs = new Obstacle(polygon, 0);
        float[] coord = new float[2];
        coord[0]=2;
        coord[1]=3;
        Threat threat = new Threat(0, coord, 0, 3);
        threat.setSpeed(2);
        threat.setTarget_type(0);
        threat.setThreat_cap("aaa");
        threat.setThreat_range(2);
        threat.setEnabled(false);
        kb.addThreat(threat);
        
        float[] coord1=new float[2];
        coord1[0]=1;
        coord1[0]=2;
        Threat threat1=new Threat(1,coord1,2,4);
        threat1.setEnabled(false);
        kb.addThreat(threat1);
        kb.removeThreat(threat1);
        ArrayList<Threat> threats=kb.getThreats();
        logger.debug("size=" + kb.getThreats().size());
//        OntologyBasedKnowledge.printStatements(kb.ontology_based_knowledge);
        kb.addObstacle(obs);
//        kb.addObstacle(obs);
        kb.removeObstacle(obs);
        kb.addObstacle(obs);
        logger.debug("size=" + kb.getObstacles().size());
//        OntologyBasedKnowledge.printStatements(kb.ontology_based_knowledge);
//        kb.getObstacles();
//        kb.getThreats();
//        boolean result = kb.containsObstacle(obs);
//        logger.debug(result);
        kb.setObstacles(null);
//         kb.setThreats(null);
        logger.debug("size=" + kb.getObstacles().size());
//        OntologyBasedKnowledge.printStatements(kb.ontology_based_knowledge);
//        logger.debug(OntologyBasedKnowledge.printOntology(kb.ontology_based_knowledge));
//        OntologyBasedKnowledge.printStatements(kb.ontology_based_knowledge);
        LinkedList<Point> path = new LinkedList<Point>();
        Point point1 = new Point(1, 1, 0);
        point1.setDecision_time_step(0);
        point1.setExptected_time_step(0);
        Point point2 = new Point(1, 2, 0);
        point2.setDecision_time_step(0);
        point2.setExptected_time_step(1);
        path.add(point1);
        path.add(point2);
        Conflict conflict = new Conflict(1, path, 0, 2);
        kb.addConflict(conflict);
        ArrayList<Conflict> conflicts = kb.getConflicts();
        kb.setConflicts(conflicts);
        conflicts = kb.getConflicts();
        for (Conflict conflict1 : conflicts) {
            LinkedList<Point> path1 = conflict1.getPath_prefound();
            for (Point temp_point : path1) {
                logger.debug(temp_point.getExptected_time_step() + "\t" + temp_point.toString());
            }
        }
        logger.debug(kb.getConflicts().size());
//        logger.debug(OntologyBasedKnowledge.printOntology(kb.ontology_based_knowledge));
    }

   
    public void init() {
        OntClass Obstacle_Class = ontology_based_knowledge.createClass(base_ns + "Obstacle");
        OntClass Region_Class = ontology_based_knowledge.createClass(base_ns + "Region");
        OntClass Polygon_Class = ontology_based_knowledge.createClass(base_ns + "Polygon");
        OntClass LowerBoundOfRegion_Class = ontology_based_knowledge.createClass(base_ns + "LowerBoundOfRegion");
        OntClass UpperBoundOfRegion_Class = ontology_based_knowledge.createClass(base_ns + "UpperBoundOfRegion");

        ObjectProperty has_region = ontology_based_knowledge.createObjectProperty(base_ns + "hasRegion");
        ObjectProperty has_polygon = ontology_based_knowledge.createObjectProperty(base_ns + "hasPolygon");
        ObjectProperty has_lowerbound = ontology_based_knowledge.createObjectProperty(base_ns + "hasLowerBound");
        ObjectProperty has_upperbound = ontology_based_knowledge.createObjectProperty(base_ns + "hasUpperBound");

        DatatypeProperty has_points = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasPoints");
        DatatypeProperty hasMaxXCoordinate = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasMaxXCoordinate");
        DatatypeProperty hasMaxYCoordinate = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasMaxYCoordinate");
        DatatypeProperty hasMinXCoordinate = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasMinXCoordinate");
        DatatypeProperty hasMinYCoordinate = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasMinYCoordinate");

        Individual obs_individual = Obstacle_Class.createIndividual();
        Individual region_individual = Region_Class.createIndividual();
        Individual polygon_individual = Polygon_Class.createIndividual();
        Individual lowerbound_individual = LowerBoundOfRegion_Class.createIndividual();
        Individual upperbound_individual = UpperBoundOfRegion_Class.createIndividual();

        Literal points = ontology_based_knowledge.createTypedLiteral("(1,2),(3,4),(5,6),(1,2)", XSDDatatype.XSDstring);
        Literal max_x_coordinate = ontology_based_knowledge.createTypedLiteral(3.0f);
        Literal max_y_coordinate = ontology_based_knowledge.createTypedLiteral(4.0f);
        Literal min_x_coordinate = ontology_based_knowledge.createTypedLiteral(5.0f);
        Literal min_y_coordinate = ontology_based_knowledge.createTypedLiteral(6.0f);

        obs_individual.addProperty(has_region, region_individual);
        region_individual.addProperty(has_polygon, polygon_individual);
        polygon_individual.addProperty(has_points, points);
        region_individual.addProperty(has_lowerbound, lowerbound_individual);
        region_individual.addProperty(has_upperbound, upperbound_individual);
        lowerbound_individual.addProperty(hasMinXCoordinate, min_x_coordinate);
        lowerbound_individual.addProperty(hasMinYCoordinate, min_y_coordinate);
        upperbound_individual.addProperty(hasMaxXCoordinate, max_x_coordinate);
        upperbound_individual.addProperty(hasMaxYCoordinate, max_y_coordinate);
    }

    @Override
    public ArrayList<Obstacle> getObstacles() {
        if (!this.obstacle_updated) {
            return this.obstacles_cache;
        }
        ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
        String sparql = "SELECT ?points ?index"
                + "{"
                + "?obstacle_ind mars:hasRegion ?region_ind ."
                + "?region_ind mars:hasPolygon ?polygon_ind ."
                + "?region_ind mars:hasObstacleIndex ?index ."
                + "?polygon_ind mars:hasPoints ?points"
                + "}";
        Query query = QueryFactory.create(prefix + sparql);
        QueryExecution qe = QueryExecutionFactory.create(query, ontology_based_knowledge);
        ResultSet results = qe.execSelect();
        while (results.hasNext()) {
            QuerySolution result = results.next();
            Polygon polygon = new Polygon();
            String raw_points_str = StringUtil.parseLiteralStr(result.get("points").toString());
            String[] points_str = raw_points_str.split(" ");
            for (String point_str : points_str) {
                String[] coord_str = point_str.split(",");
                int[] coord = new int[2];
                coord[0] = Integer.parseInt(coord_str[0]);
                coord[1] = Integer.parseInt(coord_str[1]);
                polygon.addPoint(coord[0], coord[1]);
            }

            String raw_obstacle_index = StringUtil.parseLiteralStr(result.get("index").toString());
            Integer obstacle_index = Integer.parseInt(raw_obstacle_index);
            Obstacle obstacle = new Obstacle(polygon, 0);
            obstacle.setIndex(obstacle_index);
            obstacles.add(obstacle);
        }
        this.obstacles_cache = obstacles;
        this.obstacle_updated = false;
        return obstacles;
    }


    private Model deleteAllObstacles() {
        Model m = ontology_based_knowledge.removeAll(null, has_region, null).removeAll(null, has_polygon, null)
                .removeAll(null, has_points, null).removeAll(null, has_lowerbound, null).removeAll(null, has_upperbound, null)
                .removeAll(null, hasMaxXCoordinate, null).removeAll(null, hasMaxYCoordinate, null).removeAll(null, hasMinXCoordinate, null)
                .removeAll(null, hasMinYCoordinate, null).removeAll(null, hasObstacleIndex, null)
                .removeAll(Obstacle_Class, rdf_type, null).removeAll(Region_Class, rdf_type, null)
                .removeAll(Polygon_Class, rdf_type, null).removeAll(LowerBoundOfRegion_Class, rdf_type, null).removeAll(UpperBoundOfRegion_Class, rdf_type, null)
                .removeAll(null, rdf_type, Obstacle_Class).removeAll(null, rdf_type, Region_Class).removeAll(null, rdf_type, Polygon_Class)
                .removeAll(null, rdf_type, LowerBoundOfRegion_Class).removeAll(null, rdf_type, UpperBoundOfRegion_Class);
        this.obstacle_num = 0;
        this.obstacle_updated = true;
        return m;
    }

    @Override
    public void setObstacles(ArrayList<Obstacle> obstacles) {
        ontology_based_knowledge = (OntModel) deleteAllObstacles();
        if (obstacles != null) {
            int obstacle_num = obstacles.size();
            for (int i = 0; i < obstacle_num; i++) {
                this.addObstacle(obstacles.get(i));
            }
            this.obstacle_num = obstacle_num;
        }
        this.obstacle_updated = true;
    }

    @Override
    public void addObstacle(Obstacle obs) {
        Individual obs_individual = Obstacle_Class.createIndividual();
        Individual region_individual = Region_Class.createIndividual();
        Individual polygon_individual = Polygon_Class.createIndividual();
        Individual lowerbound_individual = LowerBoundOfRegion_Class.createIndividual();
        Individual upperbound_individual = UpperBoundOfRegion_Class.createIndividual();

        Literal points = ontology_based_knowledge.createTypedLiteral(obs.getPointsStr());
//        logger.debug("------------------"+points);
        Literal max_x_coordinate = ontology_based_knowledge.createTypedLiteral(obs.getMbr().getMaxX());
        Literal max_y_coordinate = ontology_based_knowledge.createTypedLiteral(obs.getMbr().getMaxY());
        Literal min_x_coordinate = ontology_based_knowledge.createTypedLiteral(obs.getMbr().getMinX());
        Literal min_y_coordinate = ontology_based_knowledge.createTypedLiteral(obs.getMbr().getMinY());
        Literal obstacle_index = ontology_based_knowledge.createTypedLiteral(obs.getIndex());

        obs_individual.addProperty(has_region, region_individual);
        region_individual.addProperty(has_polygon, polygon_individual);
        region_individual.addProperty(hasObstacleIndex, obstacle_index);
        polygon_individual.addProperty(has_points, points);
        region_individual.addProperty(has_lowerbound, lowerbound_individual);
        region_individual.addProperty(has_upperbound, upperbound_individual);
        lowerbound_individual.addProperty(hasMinXCoordinate, min_x_coordinate);
        lowerbound_individual.addProperty(hasMinYCoordinate, min_y_coordinate);
        upperbound_individual.addProperty(hasMaxXCoordinate, max_x_coordinate);
        upperbound_individual.addProperty(hasMaxYCoordinate, max_y_coordinate);
        this.obstacle_num++;
        this.obstacle_updated = true;
    }

    @Override
    public boolean containsObstacle(Obstacle obstacle) {
        Literal points = ontology_based_knowledge.createTypedLiteral(obstacle.getPointsStr());
        Selector selector = new SimpleSelector(null, has_points, points);
        Model result_model = ontology_based_knowledge.query(selector);
//        printStatements(result_model);
        return result_model.listStatements().hasNext();
    }

    @Override
    public boolean removeObstacle(Obstacle obstacle) {
        Literal points = ontology_based_knowledge.createTypedLiteral(obstacle.getPointsStr());
        StmtIterator smt_list_to_find_polygon = ontology_based_knowledge.listStatements(null, has_points, points);
        if (smt_list_to_find_polygon.hasNext()) {
            Resource polygon_individual = smt_list_to_find_polygon.nextStatement().getSubject();
            StmtIterator smt_list_to_find_region = ontology_based_knowledge.listStatements(null, has_polygon, polygon_individual);
            if (smt_list_to_find_region.hasNext()) {
                Resource region_individual = smt_list_to_find_region.nextStatement().getSubject();

                RDFNode null_node = null;

                StmtIterator smt_list_to_find_lower_bound = ontology_based_knowledge.listStatements(region_individual, has_lowerbound, null_node);
                if (smt_list_to_find_lower_bound.hasNext()) {
                    Resource lower_bound_individual = smt_list_to_find_lower_bound.nextStatement().getSubject();
                    ontology_based_knowledge = (OntModel) ontology_based_knowledge.removeAll(lower_bound_individual, hasMinXCoordinate, null).removeAll(lower_bound_individual, hasMinYCoordinate, null);
                }

                StmtIterator smt_list_to_find_upper_bound = ontology_based_knowledge.listStatements(region_individual, has_upperbound, null_node);
                if (smt_list_to_find_upper_bound.hasNext()) {
                    Resource upper_bound_individual = smt_list_to_find_upper_bound.nextStatement().getSubject();
                    ontology_based_knowledge = (OntModel) ontology_based_knowledge.removeAll(upper_bound_individual, hasMaxXCoordinate, null).removeAll(upper_bound_individual, hasMaxYCoordinate, null);
                }

                StmtIterator smt_list_to_find_obstacle = ontology_based_knowledge.listStatements(null, has_region, region_individual);
                if (smt_list_to_find_obstacle.hasNext()) {
                    Resource obstacle_individual = smt_list_to_find_obstacle.nextStatement().getSubject();
                    ontology_based_knowledge = (OntModel) ontology_based_knowledge.removeAll(null, rdf_type, obstacle_individual).removeAll(obstacle_individual, has_region, null);
                }
                ontology_based_knowledge = (OntModel) ontology_based_knowledge.removeAll(region_individual, has_polygon, null).removeAll(region_individual, this.has_lowerbound, null).removeAll(region_individual, this.has_upperbound, null);
            }
            ontology_based_knowledge = (OntModel) ontology_based_knowledge.removeAll(polygon_individual, null, null);
            this.obstacle_num--;
            this.obstacle_updated = true;
            return true;
        } else {
            return false;
        }
    }
   
}
