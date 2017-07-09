/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.AppStates;

import com.jme3.scene.Spatial;

/**
 *
 * @author Thonon
 */
public class TriggerCible
{
    private Spatial spatial;
    
    private boolean isOn = false;
    
    private String value;
    
    public String getNameCible()
    {
        if(spatial != null)
            return spatial.getUserData("name_cible");
        
        else
            return null;
    }
    
    public String getTypeActionIn()
    {
        if(spatial != null)
            return spatial.getUserData("action_in");
        
        else
            return null;
    }
    public String getTypeActionOut()
    {
        if(spatial != null)
            return spatial.getUserData("action_out");
        
        else
            return null;
    }

    public TriggerCible(Spatial spatial) {
        this.spatial = spatial;
    }

    public Spatial getSpatial() {
        return spatial;
    }

    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
    }

    public boolean isIsOn() {
        return isOn;
    }

    public void setIsOn(boolean isOn) {
        this.isOn = isOn;
    }
    
    
    
    
}
