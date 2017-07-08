/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.Ctrl;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author Thonon
 */
public class CibleCtrl extends AbstractControl {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.

    
    private boolean init = true;
    
    public enum Type {POSITIVE,NEGATIVE,NULL};
    
    private Type type = Type.NULL;
    
    private boolean armed = false;
    
    private float speed = 1f;

    public Type getType() {
        return type;
    }

    public void setType(Type type)
    {
         if(type == Type.NULL || this.type == Type.NULL)
             posEndArmed = this.getSpatial().getWorldTranslation().add(vectorArmed);
        
        this.type = type;

    }

    
    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    private Vector3f vectorArmed = new Vector3f(0f,0f,2f);
    
    private Vector3f posEndArmed = new Vector3f(0f,0f,0f);
    
    private Vector3f posStartArmed;
    
    private Vector3f currentPos;

    public Vector3f getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(Vector3f currentPos) {
        this.currentPos = currentPos;
    }
    
    

    public Vector3f getPosEndArmed() {
        return posEndArmed;
    }

    public void setPosEndArmed(Vector3f posEndArmed) {
        this.posEndArmed = posEndArmed;
    }

    public Vector3f getPosStartArmed() {
        return posStartArmed;
    }

    public void setPosStartArmed(Vector3f posStartArmed) {
        this.posStartArmed = posStartArmed;
    }

   
    
    public Vector3f getVectorArmed() {
        return vectorArmed;
    }

    public void setVectorArmed(Vector3f vectorArmed) {
        this.vectorArmed = vectorArmed;
      
    }

    public boolean isArmed() {
        return armed;
    }

    public void setArmed(boolean armed) {
        this.armed = armed;
        
      
        
    }
      
    
    
    @Override
    protected void controlUpdate(float tpf) 
    {
        // init
        if(init)
        {
            posStartArmed = this.getSpatial().getWorldTranslation().clone();
            init = false;
        }
        
            
       if(type == Type.POSITIVE)
       {
          // déplacement de la cible vers la position posEndArmed
          // reception de la position actuel
           Vector3f currentPos = this.getSpatial().getWorldTranslation();
          // calcul de la différence entre la position current et la posEndArmed
          Vector3f diff = posEndArmed.subtract(currentPos);
          float dist = diff.length();
          if(dist > 0.05f)
          {
              // on continue, on normalize le vecteur de différence
              diff.normalizeLocal();
              // on ajoute le vecteur de différence multiplié au tpf et à une valeur speed
              this.getSpatial().setLocalTranslation(currentPos.add(diff.mult(tpf * speed)));
            
          }
           
       }
       if(type == Type.NEGATIVE)
       {
           // retour en position de départ
            Vector3f currentPos = this.getSpatial().getWorldTranslation();
            Vector3f diff = posStartArmed.subtract(currentPos);
            float dist = diff.length();
             if(dist > 0.05f)
             {
                  diff.normalizeLocal();
                  this.getSpatial().setLocalTranslation(currentPos.add(diff.mult(tpf * speed)));
             }
            
       }
       
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        CibleCtrl control = new CibleCtrl();
        control.setVectorArmed(this.getVectorArmed());
        control.setArmed(this.isArmed());
        control.setSpeed(this.getSpeed());
        control.setType(this.getType());
        //TODO: copy parameters to new Control
        return control;
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        this.setArmed(in.readBoolean("armed", false));
        this.setSpeed(in.readFloat("speed", 1f));
        Vector3f temp = new Vector3f();
        this.setVectorArmed((Vector3f)in.readSavable("VECTOR", new Vector3f(0,0,0f)));
        this.setType(in.readEnum("type", Type.class, Type.NULL));
       // this.setVectorArmed(temp);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        out.write(armed, "armed", false);
        out.write(speed, "speed", 1f);
        out.write(vectorArmed, "VECTOR", new Vector3f());
        out.write(type, "type", Type.NULL);
        
        
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
    
}
