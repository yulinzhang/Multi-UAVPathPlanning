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
import javax.swing.tree.TreePath;
import util.StringUtil;
import world.model.shape.Point;

/**
 *
 * @author boluo
 */
public class OntologyBasedKnowledge extends KnowledgeInterface {

    private OntModel ontology_based_knowledge;
    private static String base_ns = "http://www.multiagent.com.cn/robotontology/";
    private static String rdf_ns = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(OntologyBasedKnowledge.class);
    private String prefix = "PREFIX mars:<http://www.multiagent.com.cn/robotontology/>" + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
    /**
     * internal variables
     *
     */
    private OntClass Obstacle_Class, Region_Class, Polygon_Class, LowerBoundOfRegion_Class, UpperBoundOfRegion_Class, Threat_Class;
    private OntClass Conflict_Class;
    private DatatypeProperty hasExpectedConflictTime, hasDecidedConflictTime, conflictFromRobot;
    private ObjectProperty has_region, has_polygon, has_lowerbound, has_upperbound, rdf_type;
    private DatatypeProperty hasCenter, hasSpeed, hasRange, hasThreatCapability;
    private DatatypeProperty has_points, hasMaxXCoordinate, hasMaxYCoordinate, hasMinXCoordinate, hasMinYCoordinate;

    public OntologyBasedKnowledge() {
        super();
        ontology_based_knowledge = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null);
        try {
            ontology_based_knowledge.read(new FileInputStream(StaticInitConfig.ROBOT_ONTOLOGY_TEMPLATE_FILE_PATH), null);
        } catch (FileNotFoundException ex) {
            logger.error(ex);
        }
        initClassAndProperty();
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

        //class and property for threat
        Threat_Class = ontology_based_knowledge.createClass(base_ns + "Threat");

        hasCenter = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasCenter");
        hasSpeed = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasSpeed");
        hasRange = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasRange");
        hasThreatCapability = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasThreatCapability");

        //class and property for conflict
        Conflict_Class = ontology_based_knowledge.createClass(base_ns + "Conflict");
        hasExpectedConflictTime = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasExpectedConflictTime");
        hasDecidedConflictTime = ontology_based_knowledge.createDatatypeProperty(base_ns + "hasDecidedConflictTime");
        conflictFromRobot = ontology_based_knowledge.createDatatypeProperty(base_ns + "conflictFromRobot");
    }

    public static void main(String[] args) {
        OntologyBasedKnowledge kb = new OntologyBasedKnowledge();
        Polygon polygon = new Polygon();
        polygon.addPoint(0, 1);
        polygon.addPoint(2, 4);
        polygon.addPoint(3, 6);
        Obstacle obs = new Obstacle(polygon, 0);
        float[] coord = new float[2];
        Threat threat = new Threat(0, coord, 0);
        threat.setSpeed(2);
        threat.setTarget_type(0);
        threat.setThreat_cap("aaa");
        threat.setThreat_range(2);
        kb.addThreat(threat);
        OntologyBasedKnowledge.printStatements(kb.ontology_based_knowledge);
        kb.addObstacle(obs);
        kb.addObstacle(obs);
        logger.debug("size=" + kb.getObstacles().size());
        OntologyBasedKnowledge.printStatements(kb.ontology_based_knowledge);
//        kb.getObstacles();
//        kb.getThreats();
//        boolean result = kb.containsObstacle(obs);
//        logger.debug(result);
        kb.setObstacles(null);
//         kb.setThreats(null);
        logger.debug("size=" + kb.getObstacles().size());
        OntologyBasedKnowledge.printStatements(kb.ontology_based_knowledge);
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

    public static String printOntology(Model m) {
        StringWriter writer = new StringWriter();
        m.write(writer, "RDF/XML-ABBREV", base_ns);
        String rdf_merged_result = writer.toString();
        return rdf_merged_result;
    }

    public static void printStatements(Model m) {
        int total = 0;
        for (StmtIterator i = m.listStatements(); i.hasNext();) {
            Statement stmt = i.nextStatement();
            total++;
            logger.debug(" - " + PrintUtil.print(stmt));
        }
        logger.debug(total);
    }

    public static void printStatements(Model m, Resource s, Property p, Resource o) {
        for (StmtIterator i = m.listStatements(s, p, o); i.hasNext();) {
            Statement stmt = i.nextStatement();
            logger.debug(" - " + PrintUtil.print(stmt));
        }
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
        ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
        String sparql = "SELECT ?points "
                + "{"
                + "?obstacle_ind mars:hasRegion ?region_ind ."
                + "?region_ind mars:hasPolygon ?polygon_ind ."
                + "?polygon_ind mars:hasPoints ?points"
                + "}";
        Query query = QueryFactory.create(prefix + sparql);
        QueryExecution qe = QueryExecutionFactory.create(query, ontology_based_knowledge);
        ResultSet results = qe.execSelect();
        while (results.hasNext()) {
            QuerySolution result = results.next();
            Polygon polygon = new Polygon();
            String raw_points_str = result.get("points").toString();
            int end_index = raw_points_str.indexOf("^");
            String[] points_str = raw_points_str.substring(0, end_index).split(" ");
            for (String point_str : points_str) {
                String[] coord_str = point_str.split(",");
                int[] coord = new int[2];
                coord[0] = Integer.parseInt(coord_str[0]);
                coord[1] = Integer.parseInt(coord_str[1]);
                polygon.addPoint(coord[0], coord[1]);
            }
            Obstacle obstacle = new Obstacle(polygon, 0);
            obstacles.add(obstacle);
        }
        return obstacles;
    }

    @Override
    public ArrayList<Conflict> getConflicts() {
        ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
        Map<Integer, Conflict> conflict_map = new HashMap<Integer, Conflict>();
        String sparql = "SELECT ?conflict_center ?conflict_range ?exptected_conflict_time ?decided_conflict_time ?uav_index"
                + "{"
                + "?conflict_ind rdf:type mars:Conflict ."
                + "?conflict_ind mars:hasCenter ?conflict_center ."
                + "?conflict_ind mars:hasRange ?conflict_range ."
                + "?conflict_ind mars:hasExpectedConflictTime ?exptected_conflict_time ."
                + "?conflict_ind mars:hasDecidedConflictTime ?decided_conflict_time ."
                + "?conflict_ind mars:conflictFromRobot ?uav_index"
                + "}";
        Query query = QueryFactory.create(prefix + sparql);
        QueryExecution qe = QueryExecutionFactory.create(query, ontology_based_knowledge);
        ResultSet results = qe.execSelect();
//        ResultSetFormatter.out(System.out, results, query);
        while (results.hasNext()) {
            QuerySolution result = results.next();
            String raw_center_str = StringUtil.parseLiteralStr(result.get("conflict_center").toString());
            String[] coord_str = raw_center_str.split(",");
            float[] center_coord = new float[2];
            center_coord[0] = Float.parseFloat(coord_str[0]);
            center_coord[1] = Float.parseFloat(coord_str[1]);

            String raw_range_str = StringUtil.parseLiteralStr(result.get("conflict_range").toString());
            float range = Float.parseFloat(raw_range_str);

            String raw_expected_conflict_time_str = StringUtil.parseLiteralStr(result.get("exptected_conflict_time").toString());
            int expected_conflict_time = Integer.parseInt(raw_expected_conflict_time_str);

            String raw_decided_conflict_time_str = StringUtil.parseLiteralStr(result.get("decided_conflict_time").toString());
            int decided_conflict_time = Integer.parseInt(raw_decided_conflict_time_str);

            String raw_uav_index_str = StringUtil.parseLiteralStr(result.get("uav_index").toString());
            int uav_index = Integer.parseInt(raw_uav_index_str);

            Point point = new Point(center_coord[0], center_coord[1], 0);
            point.setDecision_time_step(decided_conflict_time);
            point.setExptected_time_step(expected_conflict_time);

            Conflict conflict = conflict_map.get(uav_index);
            if (conflict == null) {
                LinkedList<Point> path = new LinkedList<Point>();
                conflict = new Conflict(uav_index, path, decided_conflict_time, range);
                conflict_map.put(uav_index, conflict);
            }
            LinkedList<Point> path = conflict.getPath_prefound();
            path.add(point);
        }
        Iterator<Conflict> conflict_iter = conflict_map.values().iterator();
        while (conflict_iter.hasNext()) {
            Conflict conflict = conflict_iter.next();
            conflict.sort();
            conflicts.add(conflict);
        }
        return conflicts;
    }

    @Override
    public ArrayList<Threat> getThreats() {
        ArrayList<Threat> threats = new ArrayList<Threat>();
        String sparql = "SELECT ?center ?speed ?range ?threatCap"
                + "{"
                + "?threat_ind mars:hasCenter ?center ."
                + "?threat_ind mars:hasSpeed ?speed ."
                + "?threat_ind mars:hasRange ?range ."
                + "?threat_ind mars:hasThreatCapability ?threatCap"
                + "}";
        Query query = QueryFactory.create(prefix + sparql);
        QueryExecution qe = QueryExecutionFactory.create(query, ontology_based_knowledge);
        ResultSet results = qe.execSelect();
        while (results.hasNext()) {
            QuerySolution result = results.next();
            String raw_center_str = StringUtil.parseLiteralStr(result.get("center").toString());
            String[] coord_str = raw_center_str.split(",");
            float[] center_coord = new float[2];
            center_coord[0] = Float.parseFloat(coord_str[0]);
            center_coord[1] = Float.parseFloat(coord_str[1]);

            String raw_speed_str = StringUtil.parseLiteralStr(result.get("speed").toString());
            float speed = Float.parseFloat(raw_speed_str);

            String raw_range_str = StringUtil.parseLiteralStr(result.get("range").toString());
            float range = Float.parseFloat(raw_range_str);

            String raw_therat_cap_str = StringUtil.parseLiteralStr(result.get("threatCap").toString());

            Threat threat = new Threat(0, center_coord, 0);
            threat.setThreat_cap(raw_therat_cap_str);
            threat.setThreat_range(range);
            threat.setSpeed(speed);
            threats.add(threat);
        }
        return threats;
    }

    private Model deleteAllObstacles() {
        Model m = ontology_based_knowledge.removeAll(null, has_region, null).removeAll(null, has_polygon, null)
                .removeAll(null, hasRange, null).removeAll(null, has_upperbound, null).removeAll(null, has_points, null)
                .removeAll(null, hasMaxXCoordinate, null).removeAll(null, hasMaxYCoordinate, null).removeAll(null, hasMinXCoordinate, null)
                .removeAll(null, hasMinYCoordinate, null)
                .removeAll(Obstacle_Class, rdf_type, null).removeAll(Region_Class, rdf_type, null)
                .removeAll(Polygon_Class, rdf_type, null).removeAll(LowerBoundOfRegion_Class, rdf_type, null).removeAll(UpperBoundOfRegion_Class, rdf_type, null)
                .removeAll(null, rdf_type, Obstacle_Class).removeAll(null, rdf_type, Region_Class).removeAll(null, rdf_type, Polygon_Class)
                .removeAll(null, rdf_type, LowerBoundOfRegion_Class).removeAll(null, rdf_type, UpperBoundOfRegion_Class);
        return m;
    }

    private Model deleteAllThreats() {
        Model m = ontology_based_knowledge.removeAll(Threat_Class, hasCenter, null).removeAll(null, hasSpeed, null)
                .removeAll(Threat_Class, hasRange, null).removeAll(null, hasThreatCapability, null)
                .removeAll(null, rdf_type, Threat_Class);
        return m;
    }

    private Model deleteAllConflicts() {
        Model m = ontology_based_knowledge.removeAll(Conflict_Class, hasCenter, null).removeAll(Conflict_Class, hasRange, null)
                .removeAll(null, hasExpectedConflictTime, null).removeAll(null, hasDecidedConflictTime, null).removeAll(null, this.conflictFromRobot, null)
                .removeAll(null, rdf_type, Conflict_Class);
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
        }
    }

    @Override
    public void setConflicts(ArrayList<Conflict> conflicts) {
        ontology_based_knowledge = (OntModel) deleteAllConflicts();
        if (conflicts != null) {
            int conflict_num = conflicts.size();
            for (int i = 0; i < conflict_num; i++) {
                this.addConflict(conflicts.get(i));
            }
        }
    }

    @Override
    public void setThreats(ArrayList<Threat> threats) {
        ontology_based_knowledge = (OntModel) deleteAllThreats();
        if (threats != null) {
            int threat_num = threats.size();
            for (int i = 0; i < threat_num; i++) {
                this.addThreat(threats.get(i));
            }
        }
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
    public void addConflict(Conflict conflict) {

        LinkedList<Point> path = conflict.getPath_prefound();
        int path_len = path.size();
        for (int i = 0; i < path_len; i++) {
            Point conflict_point = path.get(i);
            Individual conflict_individual = Conflict_Class.createIndividual();
            Literal center = ontology_based_knowledge.createTypedLiteral(conflict_point.getX() + "," + conflict_point.getY());
            Literal conflict_range = ontology_based_knowledge.createTypedLiteral(conflict.getConflict_range());
            Literal exptected_conflict_time = ontology_based_knowledge.createTypedLiteral(conflict_point.getExptected_time_step());
            Literal decided_conflict_time = ontology_based_knowledge.createTypedLiteral(conflict_point.getDecision_time_step());
            Literal conflict_from_robot = ontology_based_knowledge.createTypedLiteral(conflict.getUav_index());
            conflict_individual.addProperty(hasCenter, center);
            conflict_individual.addProperty(hasRange, conflict_range);
            conflict_individual.addProperty(hasExpectedConflictTime, exptected_conflict_time);
            conflict_individual.addProperty(hasDecidedConflictTime, decided_conflict_time);
            conflict_individual.addProperty(conflictFromRobot, conflict_from_robot);
        }
    }

    @Override
    public void addThreat(Threat threat) {
        Individual threat_individual = Threat_Class.createIndividual();

        Literal center = ontology_based_knowledge.createTypedLiteral(threat.getCoordinates()[0] + "," + threat.getCoordinates()[1]);
        Literal speed = ontology_based_knowledge.createTypedLiteral(threat.getSpeed());
        Literal threat_range = ontology_based_knowledge.createTypedLiteral(threat.getThreat_range());
        Literal threat_cap = ontology_based_knowledge.createTypedLiteral(threat.getThreat_cap());

        threat_individual.addProperty(hasCenter, center);
        threat_individual.addProperty(hasSpeed, speed);
        threat_individual.addProperty(hasRange, threat_range);
        threat_individual.addProperty(hasThreatCapability, threat_cap);
    }

    @Override
    public boolean containsThreat(Threat threat) {
        Literal center = ontology_based_knowledge.createTypedLiteral(threat.getCoordinates()[0] + "," + threat.getCoordinates()[1]);
        Selector selector = new SimpleSelector(null, hasCenter, center);
        Model result_model = ontology_based_knowledge.query(selector);
        printStatements(result_model);
        return result_model.listStatements().hasNext();
    }

    @Override
    public boolean containsObstacle(Obstacle obstacle) {
        Literal points = ontology_based_knowledge.createTypedLiteral(obstacle.getPointsStr());
        Selector selector = new SimpleSelector(null, has_points, points);
        Model result_model = ontology_based_knowledge.query(selector);
        printStatements(result_model);
        return result_model.listStatements().hasNext();
    }

    @Override
    public boolean deleteComponent(TreePath path, Object leaf_node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsConflict(Conflict conflict) {
        return false;
    }

    @Override
    public Object getRoot() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getChild(Object parent, int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getChildCount(Object parent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isLeaf(Object node) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
