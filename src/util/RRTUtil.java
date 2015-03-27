/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import algorithm.RRT.RRTNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author boluo
 */
public class RRTUtil {


    /** problem
     * 
     * @param input
     * @return 
     */
    @Deprecated
    public static HashMap<RRTNode, Float> sortHashMap(HashMap<RRTNode, Float> input) {

        Map<RRTNode, Float> tempMap = new HashMap<RRTNode, Float>();

        for (RRTNode wsState : input.keySet()) {
            tempMap.put(wsState, input.get(wsState));
        }

        List<RRTNode> mapKeys = new ArrayList<RRTNode>(tempMap.keySet());
        List<Float> mapValues = new ArrayList<Float>(tempMap.values());
        HashMap<RRTNode, Float> sortedMap = new LinkedHashMap<RRTNode, Float>();
        TreeSet<Float> sortedSet = new TreeSet<Float>(mapValues);
        Object[] sortedArray = sortedSet.toArray();

        int size = sortedArray.length;
        for (int i = 0; i < size; i++) {
            sortedMap.put(mapKeys.get(mapValues.indexOf(sortedArray[i])),
                    (Float) sortedArray[i]);
        }
        return sortedMap;
    }
    
    
    public static void main(String[] args) {
        HashMap<RRTNode, Float> input = new HashMap<RRTNode, Float>();
        RRTNode rrt_node_1 = new RRTNode(0, 3);
        RRTNode rrt_node_2 = new RRTNode(0, 2);
        RRTNode rrt_node_3 = new RRTNode(0, 1);
        input.put(rrt_node_3, 1.0f);
        input.put(rrt_node_2, 3.0f);
        input.put(rrt_node_1, 2.0f);
        input=RRTUtil.sortHashMap(input);
        for (RRTNode n : input.keySet()) {
            System.out.println(n.getCoordinate()[1]);
        }
    }
}
