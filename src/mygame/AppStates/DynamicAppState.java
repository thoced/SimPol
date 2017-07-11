/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.AppStates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.List;

/**
 *
 * @author Thonon
 */
public class DynamicAppState extends AbstractAppState {
    
    private SimpleApplication app;
    
    private BulletAppState bulletAppState;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) 
    {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication)app;
        this.bulletAppState = app.getStateManager().getState(BulletAppState.class);
             
        
        // chargement du node Dynamic
        Spatial dyn = ((Node)this.app.getRootNode()).getChild("Dynamic");
        
        this.createPhysique((Node)dyn);
        
        
        // disable pour éviter d'appeller la methode update inutilement
        this.setEnabled(false);
        
       
    }
    
     private void createPhysique(Node n)
    {
       if(n == null)
           return;
       
        PhysicsControl ctr = n.getControl(PhysicsControl.class);
                                    if(ctr != null)
                                        this.bulletAppState.getPhysicsSpace().add(ctr);
        
       List<Spatial> listSpatial = n.getChildren();
       
       if(listSpatial != null)
       {
            for(Spatial sp : listSpatial)
            {

                if(sp instanceof Node)
                    this.createPhysique((Node)sp);
            }
       }
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
       
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
    
}
