/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphAnalysis.api;

import com.mxgraph.view.mxGraph;
import graphAnalysis.LinkType;
import graphAnalysis.config.GraphAnalysisConfig;
import graphAnalysis.core.GraphUtil;
import graphAnalysis.core.StyleSheet;
import java.util.ArrayList;
import util.DistanceUtil;
import world.model.Threat;
import world.uav.Attacker;

/**
 *
 * @author AG BRIGHTER
 */
public class TaskNetwork 
{
    private final Threat selThreat;
    private final ArrayList<Attacker> attackers;
    GraphAnalysisConfig config;
            
    /**
     * Constructor for task network graph
     * @param selThreat The selected threat
     * @param attackers All attackers in the world
     */
    public TaskNetwork(Threat selThreat, ArrayList<Attacker> attackers) 
    {
        this.config = new GraphAnalysisConfig();
        this.selThreat = selThreat;
        this.attackers = attackers;
    }
    
    /**
     * API for getting the task network graph
     * @return Graph object containing the task network
     */
    public mxGraph getGraph()
    {
        mxGraph taskNetworkGraph = new mxGraph();
        setGraphStylesheet(taskNetworkGraph);
        taskNetLogic(taskNetworkGraph);
        return taskNetworkGraph;
    }
    
    /**
     * Configures the style sheet for a given graph
     * @param taskNetworkGraph 
     */
    private void setGraphStylesheet(mxGraph taskNetworkGraph) {
        StyleSheet.getGeneralEllipseStyle(taskNetworkGraph.getStylesheet());
        StyleSheet.getUndirectedEdgeStyle(taskNetworkGraph.getStylesheet());
    }
    
    /**
     * Gets all attackers with the selected threat
     * @return 
     */
    private ArrayList<Attacker> attackersWithSelThreat()
    {
        ArrayList<Attacker> at_selThreat = new ArrayList<>();
        for(Attacker attacker : this.attackers)
        {
            if(attacker.getKb().containsThreat(selThreat))
            {
                at_selThreat.add(attacker);
            }
        }
        return at_selThreat;
    }
    
    /**
     * Method for drawing task network graph
     * @param graph The graph object to be used
     */
    private void taskNetLogic(mxGraph graph)
    {
        ArrayList<Attacker> attackersWithSelThreat = attackersWithSelThreat();
        for(Attacker attacker : attackersWithSelThreat)
        {
            for(Attacker atkr : attackersWithSelThreat)
            {
                if(attacker != atkr)
                {
                    float d_ij = DistanceUtil.distanceBetween(attacker.getCenter_coordinates(), atkr.getCenter_coordinates());
                    float R = config.getRelThreshold();
                    
                    if(d_ij <= R)
                    {
                        GraphUtil.generateGraph(graph, attacker.toString(), String.valueOf(d_ij), atkr.toString(), LinkType.STRONG_LINK);
                    }
                    else
                    {
                        GraphUtil.generateGraph(graph, attacker.toString(), String.valueOf(d_ij), atkr.toString(), LinkType.WEAK_LINK);
                    }
                }
            }
        }
    }
}
