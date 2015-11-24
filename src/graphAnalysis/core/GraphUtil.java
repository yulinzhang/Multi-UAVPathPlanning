/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphAnalysis.core;

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;
import graphAnalysis.LinkType;
import java.util.ArrayList;

/**
 *
 * @author AG BRIGHTER
 */
public class GraphUtil 
{
    private static final ArrayList<Integer> gentdCornts = new ArrayList<>();

    public GraphUtil() {
    }
    
    /**
     * generates a graph on a given graph's model
     * @param graph The graph
     * @param subject vertex
     * @param predicate edge
     * @param object  vertex
     * @param linkType The kind of relationship between the nodes e.g. weak or strong
     */
    public static void generateGraph(mxGraph graph, String subject, String predicate, String object, LinkType linkType)
    {
        System.out.println("graph-> -"+subject+"- -"+predicate+"- -"+object+"-");
        Object v1, v2;

        //gets the current structure of the graph
        mxGraphModel grphMdl = (mxGraphModel) graph.getModel();
        Object parent = graph.getDefaultParent();
        
        graph.getModel().beginUpdate();
        try
        {
            String node1 = subject;
            String node2 = object;

            Object exist_node1 = grphMdl.getCell(node1);
            Object exist_node2 = grphMdl.getCell(node2);

            //checks if the current subject exists in the graph
            if(exist_node1!=null)
            {
                v1 = exist_node1;
            }
            else
            {
                v1 = graph.insertVertex(parent, node1, node1, 
                                genRndCrdnt(5, 500), genRndCrdnt(5, 800) , 80, 30, "ELLIPSE");
            }

            //checks if the current object exists in the graph
            if(exist_node2!=null)
            {
                v2 = exist_node2;
            }
            else
            {

                /*
                 * paramerters are parent, id, value, x, y, width, height, style
                 * */
                v2 = graph.insertVertex(parent, node2, node2, 
                                genRndCrdnt(5, 1000), genRndCrdnt(5, 700) , 80, 30, "ELLIPSE");
            }

            Object [] prd = graph.getEdgesBetween(v1, v2);
            if(prd.length == 0)
            {
                graph.insertEdge(parent, null, predicate, v1, v2, "UNDIRECTED");
            }
            else
            {
                for (Object prd1 : prd) {
                    if (!((mxICell) prd1).getValue().toString().equalsIgnoreCase(predicate)) {
                        graph.insertEdge(parent, null, predicate, v1, v2, "UNDIRECTED");
                    }
                }
            }
        }
        finally
        {
                graph.getModel().endUpdate();
        }

    }
    
    /**
     * Generates a random coordinate for a node
     * @param startRange
     * @param endRange
     * @return 
     */
    private static int genRndCrdnt(int startRange, int endRange)
    {
        int rndNum=0;
        do
        {
                rndNum = ( (int)(startRange + (Math.random()*endRange) ) );
        } while(gentdCornts.contains(rndNum)); 
        gentdCornts.add(rndNum);//registers the generated coordinate
        return rndNum;
    }
}
