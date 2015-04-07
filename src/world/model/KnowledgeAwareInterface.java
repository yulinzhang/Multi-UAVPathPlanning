/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package world.model;

import java.util.ArrayList;


/**
 *
 * @author boluo
 */
public interface KnowledgeAwareInterface {

    public ArrayList<Obstacle> getObstacles();

    public ArrayList<Conflict> getConflicts();

    public ArrayList<Threat> getThreats();

    public void setObstacles(ArrayList<Obstacle> obstacles);

    public void setConflicts(ArrayList<Conflict> conflicts);

    public void setThreats(ArrayList<Threat> threats);

    public void addObstacle(Obstacle obs);
    
    public void addConflict(Conflict conflict);
    
    public void addThreat(Threat threat);
    
    public boolean containsThreat(Threat threat);
    
    public boolean containsObstacle(Obstacle obstacle);
}
