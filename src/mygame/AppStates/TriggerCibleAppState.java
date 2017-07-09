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
public class TriggerCibleAppState extends AbstractAppState {
    
    private SimpleApplication app;
    
    private Camera cam;
    
    // liste des TriggerCible
    private List<TriggerCible>  listTrigger = new ArrayList<TriggerCible>();
      
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
                   listTrigger.add(new TriggerCible(sp));
                  
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
        for(TriggerCible trigger : listTrigger)
        {
            
            if(trigger.isIsOn())
            {
                // le trigger est déja déclenché, on regarde si la caméra est hors du trigger pour lancer l'évenement out
                if(!trigger.getSpatial().getWorldBound().intersects(this.cam.getLocation()))
                {
                    // la caméra est sortie, on positionne le trigger à off
                    trigger.setIsOn(false);
                    // on active l'évenemnt cible out
                    String nameCible = trigger.getNameCible();
                    String actionOut = trigger.getTypeActionOut();
                    if(nameCible != null)
                    {
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
                                     switch(actionOut)
                                       {
                                           case "POSITIVE": ctrl.setType(CibleCtrl.Type.POSITIVE);break;
                                           
                                           case "NEGATIVE": ctrl.setType(CibleCtrl.Type.NEGATIVE);break;
                                           
                                           case "NULL":
                                           default    : ctrl.setType(CibleCtrl.Type.NULL);break;
                                       }
                                }
                            }


                        }
                    }
                    
                }
            }
            else
            {
                        // si le trigger n'est pas déja enclenché et que la camera est dans le trigger
                   if(trigger.getSpatial().getWorldBound().intersects(this.cam.getLocation()))
                   {
                       // le trigger est déclenché
                       trigger.setIsOn(true);
                       // on récupère les values
                       String nameCible = trigger.getNameCible();
                       String actionIn = trigger.getTypeActionIn();

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
                                   if(actionIn != null)
                                   {
                                                                            
                                       switch(actionIn)
                                       {
                                           case "POSITIVE": ctrl.setType(CibleCtrl.Type.POSITIVE);break;
                                           
                                           case "NEGATIVE": ctrl.setType(CibleCtrl.Type.NEGATIVE);break;
                                           
                                           case "NULL":
                                           default    : ctrl.setType(CibleCtrl.Type.NULL);break;
                                       }
                                   }
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
