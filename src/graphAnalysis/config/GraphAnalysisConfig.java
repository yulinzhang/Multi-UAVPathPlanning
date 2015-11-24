/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphAnalysis.config;

/**
 *
 * @author AG BRIGHTER
 */
public class GraphAnalysisConfig 
{
    private float relThreshold;

    public GraphAnalysisConfig() {
        this.relThreshold = 5; //default value
    }

    public float getRelThreshold() {
        return relThreshold;
    }

    public void setRelThreshold(float relThreshold) {
        this.relThreshold = relThreshold;
    }
    
    
}
