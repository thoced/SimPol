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
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import mygame.Ctrl.CibleCtrl;

/**
 *
 * @author Thonon
 * 
 * 
 */
public class TriggerState extends AbstractAppState {
    
    private SimpleApplication app;
    
    private Camera cam;
    
    private List<Trigger>  listTrigger = new ArrayList<Trigger>();
    
    
    public void getTrigger(Node n)
    {

       List<Spatial> listSpatial = n.getChildren();
       
       if(listSpatial != null)
       {
            for(Spatial sp : listSpatial)
            {
               //  si il s'agit d'un trigger
                if(sp.getName().equals("TRIGGER"))
                {
                    // ajout dans la liste des triggers
                   listTrigger.add(new Trigger(sp));
                  
                }
                
                if(sp instanceof Node)
                    this.getTrigger((Node)sp);
            }
       }
    }
    
    public Spatial getCible(Node n,String value)
    {
       Spatial cible = null;
       List<Spatial> listSpatial = n.getChildren();
       
       if(listSpatial != null)
       {
            for(Spatial sp : listSpatial)
            {
               //  si il s'agit du nom de la cible
                if(sp.getName().equals(value))
                   return sp;
                              
                if(sp instanceof Node)
                {
                    cible =  this.getCible((Node)sp,value);
                    if(cible != null)
                        return cible;
                }
  
            }
       }
       
      return cible;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
      
       this.app = (SimpleApplication) app;
       this.cam = app.getCamera();
       // récupération des node portant les nom de "trigger"
       this.getTrigger(this.app.getRootNode());
  
        
    }
    
    @Override
    public void update(float tpf)
    {
        // on test si les trigger sont franchit par la caméra
        for(Trigger trigger : listTrigger)
        {
            // si le trigger n'est pas déja enclenché et que la camera est dans le trigger
            if(!trigger.isIsOn() && trigger.getSpatial().getWorldBound().intersects(this.cam.getLocation()))
            {
                // le trigger est déclenché
                trigger.setIsOn(true);
                // on récupère les values
                String nameCible = trigger.getNameCible();
                String typeAction = trigger.getTypeAction();
                
                if(nameCible != null)
                {
                    // la nameCible indique le nom de la cible
                    // on récupère les Spatial comprenant le nom en question
                    Spatial cible= this.getCible(this.app.getRootNode(), nameCible);
                    if(cible != null)
                    {
                        // on récupère le CibleCtrl
                        CibleCtrl ctrl = cible.getControl(CibleCtrl.class);
                        if(ctrl != null)
                        {
                            if(typeAction != null)
                            {
                                if(typeAction.equals("POSITIVE"))
                                 ctrl.setType(CibleCtrl.Type.POSITIVE);
                                if(typeAction.equals("NEGATIVE"))
                                 ctrl.setType(CibleCtrl.Type.NEGATIVE);
                            }
                        }
                    }
                   
                    
                }
               
            }
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
    
   
    
}
