/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphAnalysis.core;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxStylesheet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author AG BRIGHTER
 */
public class StyleSheet 
{

    public StyleSheet() {
    }
    
    /**
     * Style for drawing nodes as ellipse
     * @param stylesheet
     * @return 
     */
    public static mxStylesheet getGeneralEllipseStyle(mxStylesheet stylesheet)
    {
        Map<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE);
        style.put(mxConstants.STYLE_FILLCOLOR, mxUtils.parseColor("#000000"));
        stylesheet.putCellStyle("ELLIPSE", style);
        return stylesheet;
    }
    
    /**
     * Style for drawing undirected edges
     * @param stylesheet
     * @return 
     */
    public static mxStylesheet getUndirectedEdgeStyle(mxStylesheet stylesheet)
    {
        Map<String, Object> style = new HashMap<>();
        style.put(mxConstants.STYLE_ENDARROW, mxConstants.NONE);
        stylesheet.putCellStyle("UNDIRECTED", style);
        return stylesheet;
    }
}
