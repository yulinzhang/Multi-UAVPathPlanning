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
import config.FilePathConfig;
import java.awt.Polygon;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import util.ObtacleUtil;
import util.StringUtil;
import world.model.shape.Point;

/**
 *
 * @author Yulin_Zhang
 */
public class OntologyBasedKnowledge extends KnowledgeInterface {

    public OntModel ontology_based_knowledge;
    public static String base_ns = "http://www.multiagent.com.cn/robotontology/";
    public static String rdf_ns = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(OntologyBasedKnowledge.class);
    public String prefix = "PREFIX mars:<http://www.multiagent.com.cn/robotontology/>" + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";

    /**
     * internal variables
     *
     *
     */
    public OntClass Obstacle_Class, Region_Class, Polygon_Class, LowerBoundOfRegion_Class, UpperBoundOfRegion_Class, Threat_Class;
    public OntClass Conflict_Class;
    public DatatypeProperty hasExpectedConflictTime, hasDecidedConflictTime, conflictFromRobot;
    public ObjectProperty has_region, has_polygon, has_lowerbound, has_upperbound, rdf_type;
    public DatatypeProperty hasConflictCenter, hasConflictRange;
    public DatatypeProperty hasThreatCenter, hasThreatSpeed, hasThreatRange, hasThreatCapability, hasThreatIndex, threatEnabled;
    public DatatypeProperty has_points, hasMaxXCoordinate, hasMaxYCoordinate, hasMinXCoordinate, hasMinYCoordinate, hasObstacleIndex;

    public RDFNode null_node = null;
    /**
     * cache to speed the information retrieve process.
     *
     */
    public boolean obstacle_updated = false, threat_updated = false, conflict_updated = false;
    public ArrayList<Obstacle> obstacles_cache = new ArrayList<Obstacle>();
    public ArrayList<Threat> threats_cache = new ArrayList<Threat>();
    public ArrayList<Conflict> conflicts_cache = new ArrayList<Conflict>();

    public OntologyBasedKnowledge() {
        super();
        ontology_based_knowledge = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, null);
        try {
//            ontology_based_knowledge.read(new FileInputStream(FilePathConfig.ROBOT_ONTOLOGY_TEMPLATE_FILE_PATH), null);
            ontology_based_knowledge.read(OntologyBasedKnowledge.class.getResourceAsStream(FilePathConfig.ROBOT_ONTOLOGY_TEMPLATE_FILE_PATH), null);
        } catch (Exception ex) {
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
        OntologyBasedKnowledge kb = new OntologyBasedKnowledge();
        Polygon polygon = new Polygon();
        polygon.addPoint(0, 1);
        polygon.addPoint(2, 4);
        polygon.addPoint(3, 6);
        Obstacle obs = new Obstacle(polygon, 0);
        float[] coord = new float[2];
        coord[0] = 2;
        coord[1] = 3;
        Threat threat = new Threat(0, coord, 0, 3);
        threat.setSpeed(2);
        threat.setTarget_type(0);
        threat.setThreat_cap("aaa");
        threat.setThreat_range(2);
        threat.setEnabled(false);
        kb.addThreat(threat);

        float[] coord1 = new float[2];
        coord1[0] = 1;
        coord1[0] = 2;
        Threat threat1 = new Threat(1, coord1, 2, 4);
        threat1.setEnabled(false);
        kb.addThreat(threat1);
        kb.removeThreat(threat1);
        ArrayList<Threat> threats = kb.getThreats();
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

    @Override
    public ArrayList<Conflict> getConflicts() {
        if (!this.conflict_updated) {
            return this.conflicts_cache;
        }
        ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
        Map<Integer, Conflict> conflict_map = new HashMap<Integer, Conflict>();
        String sparql = "SELECT ?conflict_center ?conflict_range ?exptected_conflict_time ?decided_conflict_time ?uav_index"
                + "{"
                + "?conflict_ind rdf:type mars:Conflict ."
                + "?conflict_ind mars:hasConflictCenter ?conflict_center ."
                + "?conflict_ind mars:hasConflictRange ?conflict_range ."
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
        this.conflicts_cache = conflicts;
        this.conflict_updated = false;
        return conflicts;
    }

    @Override
    public ArrayList<Threat> getThreats() {
        if (!this.threat_updated) {
            return this.threats_cache;
        }
        ArrayList<Threat> threats = new ArrayList<Threat>();
        String sparql = "SELECT ?center ?speed ?range ?threatCap ?index ?threat_enabled"
                + "{"
                + "?threat_ind mars:hasThreatCenter ?center ."
                + "?threat_ind mars:hasThreatSpeed ?speed ."
                + "?threat_ind mars:hasThreatRange ?range ."
                + "?threat_ind mars:hasThreatIndex ?index ."
                + "?threat_ind mars:threatEnabled ?threat_enabled ."
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

            String raw_threat_index = StringUtil.parseLiteralStr(result.get("index").toString());
            Integer threat_index = Integer.parseInt(raw_threat_index);

            String raw_threat_enabled_index = StringUtil.parseLiteralStr(result.get("threat_enabled").toString());
            boolean threat_enabled = false;
            if (raw_threat_enabled_index.equals("true")) {
                threat_enabled = true;
            } else {
                threat_enabled = false;
            }

            Threat threat = new Threat(0, center_coord, 0, speed);
            threat.setThreat_cap(raw_therat_cap_str);
            threat.setThreat_range(range);
            threat.setIndex(threat_index);
            threat.setEnabled(threat_enabled);
            threats.add(threat);
        }
        this.threats_cache = threats;
        this.threat_updated = false;
        return threats;
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

    public Model deleteAllThreats() {
        Model m = ontology_based_knowledge.removeAll(null, hasThreatCenter, null).removeAll(null, hasThreatSpeed, null)
                .removeAll(null, hasThreatRange, null).removeAll(null, hasThreatCapability, null).removeAll(null, hasThreatIndex, null).removeAll(null, threatEnabled, null)
                .removeAll(null, rdf_type, Threat_Class).removeAll(Threat_Class, rdf_type, null);
        this.threat_num = 0;
        this.threat_updated = true;
        return m;
    }

    public Model deleteAllConflicts() {
        Model m = ontology_based_knowledge.removeAll(null, hasConflictCenter, null).removeAll(null, hasConflictRange, null)
                .removeAll(null, hasExpectedConflictTime, null).removeAll(null, hasDecidedConflictTime, null).removeAll(null, this.conflictFromRobot, null)
                .removeAll(null, rdf_type, Conflict_Class).removeAll(Conflict_Class, rdf_type, null);
        this.conflict_num = 0;
        this.conflict_updated = true;
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
    public void setConflicts(ArrayList<Conflict> conflicts) {
        ontology_based_knowledge = (OntModel) deleteAllConflicts();
        if (conflicts != null) {
            int conflict_num = conflicts.size();
            for (int i = 0; i < conflict_num; i++) {
                this.addConflict(conflicts.get(i));
            }
            this.conflict_num = conflict_num;
        }
        this.conflict_updated = true;
    }

    @Override
    public void setThreats(ArrayList<Threat> threats) {
        ontology_based_knowledge = (OntModel) deleteAllThreats();
        if (threats != null) {
            int threat_num = threats.size();
            for (int i = 0; i < threat_num; i++) {
                this.addThreat(threats.get(i));
            }
            this.threat_num = threat_num;
        }
        this.threat_updated = true;
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
    public void addConflict(Conflict conflict) {
        ArrayList<Conflict> conflicts = this.getConflicts();
        if (conflict_num == 0) {
            addConflictWithoutCheck(conflict);
            return;
        }
        for (int i = 0; i < conflict_num; i++) {
            Conflict temp_conflict = conflicts.get(i);
            if (temp_conflict.getUav_index() == conflict.getUav_index()) {
                this.removeConflict(temp_conflict);
                addConflictWithoutCheck(conflict);
                return;
            }
        }
        addConflictWithoutCheck(conflict);
        conflict_num++;
    }

    private void addConflictWithoutCheck(Conflict conflict) {

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
            conflict_individual.addProperty(hasConflictCenter, center);
            conflict_individual.addProperty(hasConflictRange, conflict_range);
            conflict_individual.addProperty(hasExpectedConflictTime, exptected_conflict_time);
            conflict_individual.addProperty(hasDecidedConflictTime, decided_conflict_time);
            conflict_individual.addProperty(conflictFromRobot, conflict_from_robot);
        }
        this.conflict_num++;
        this.conflict_updated = true;
    }

    @Override
    public void addThreat(Threat threat) {
        Individual threat_individual = Threat_Class.createIndividual();

        Literal center = ontology_based_knowledge.createTypedLiteral(threat.getCoordinates()[0] + "," + threat.getCoordinates()[1]);
        Literal speed = ontology_based_knowledge.createTypedLiteral(threat.getSpeed());
        Literal threat_range = ontology_based_knowledge.createTypedLiteral(threat.getThreat_range());
        Literal threat_cap = ontology_based_knowledge.createTypedLiteral(threat.getThreat_cap());
        Literal threat_index = ontology_based_knowledge.createTypedLiteral(threat.getIndex());
        Literal threat_enabled = ontology_based_knowledge.createTypedLiteral(threat.isEnabled());

        threat_individual.addProperty(hasThreatCenter, center);
        threat_individual.addProperty(hasThreatSpeed, speed);
        threat_individual.addProperty(hasThreatRange, threat_range);
        threat_individual.addProperty(hasThreatCapability, threat_cap);
        threat_individual.addProperty(hasThreatIndex, threat_index);
        threat_individual.addProperty(threatEnabled, threat_enabled);
        this.threat_num++;
        this.threat_updated = true;
    }

    @Override
    public boolean containsThreat(Threat threat) {
        Literal center = ontology_based_knowledge.createTypedLiteral(threat.getCoordinates()[0] + "," + threat.getCoordinates()[1]);
        Selector selector = new SimpleSelector(null, hasThreatCenter, center);
        Model result_model = ontology_based_knowledge.query(selector);
//        printStatements(result_model);
        return result_model.listStatements().hasNext();
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
    public boolean containsConflict(Conflict conflict) {
        return false;
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

//    @Override
//    public boolean removeThreat(Threat threat) {
//        Literal center = ontology_based_knowledge.createTypedLiteral(threat.getCoordinates()[0] + "," + threat.getCoordinates()[1]);
//        StmtIterator smt_list_to_find_threat_individual = ontology_based_knowledge.listStatements(null, hasThreatCenter, center);
//        if (smt_list_to_find_threat_individual.hasNext()) {
//            Resource threat_individual = smt_list_to_find_threat_individual.next().getSubject();
//            ontology_based_knowledge = (OntModel) ontology_based_knowledge.removeAll(threat_individual, hasThreatCenter, null).removeAll(threat_individual, hasThreatSpeed, null).removeAll(threat_individual, hasThreatRange, null).removeAll(threat_individual, hasThreatCapability, null).removeAll(threat_individual, hasThreatIndex, null).removeAll(threat_individual, threatEnabled, null).removeAll(null, null, threat_individual);
//            this.threat_num--;
//            this.threat_updated = true;
//            return true;
//        } else {
//            return false;
//        }
//    }
    @Override
    public boolean removeThreat(Threat threat) {
        Literal index = ontology_based_knowledge.createTypedLiteral(threat.getIndex());
        StmtIterator smt_list_to_find_threat_individual = ontology_based_knowledge.listStatements(null, hasThreatIndex, index);
        if (smt_list_to_find_threat_individual.hasNext()) {
            Resource threat_individual = smt_list_to_find_threat_individual.next().getSubject();
            ontology_based_knowledge = (OntModel) ontology_based_knowledge.removeAll(threat_individual, hasThreatCenter, null).removeAll(threat_individual, hasThreatSpeed, null).removeAll(threat_individual, hasThreatRange, null).removeAll(threat_individual, hasThreatCapability, null).removeAll(threat_individual, hasThreatIndex, null).removeAll(threat_individual, threatEnabled, null).removeAll(null, null, threat_individual);
            this.threat_num--;
            this.threat_updated = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeConflict(Conflict conflict) {
        return false;
    }
}
