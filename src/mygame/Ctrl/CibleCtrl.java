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

    
    private boolean init = false;
    
    private boolean oneShot = false; // si le controleur ne fonctionne qu'une fois
    
    private boolean onceActived = false; // si l'activation a déja eu lieu une fois
    
    public enum Type {POSITIVE,NEGATIVE,NULL};
    
    private Type type = Type.NULL;
    
    private float speed = 1f;

    public Type getType() {
        return type;
    }

    public void setType(Type type)
    {
        
        this.type = type;
        
        //si le type est null, on reset le postart et posend
        if(this.type == Type.NULL)
        {
            posStart = this.getSpatial().getLocalTranslation().clone();
            currentPos = posStart.clone();
        }

    }
    
 
    @Override
    public void setSpatial(Spatial spatial) 
    {
        super.setSpatial(spatial); 
        
        if(spatial != null)
        {
         posStart = this.getSpatial().getLocalTranslation().clone();
         posEnd = this.getSpatial().getLocalTranslation().clone();
         currentPos = posStart.clone();
        }
       
    }
    
    public String getNameSpatial()
    {
        return this.getSpatial().getName();
    }
    
    public void setNameSpatial(String name)
    {
        this.getSpatial().setName(name);
    }

    public Vector3f getPosStart() {
        return posStart;
    }

    public void setPosStart(Vector3f posStart) {
        this.posStart = posStart;
    }

    public Vector3f getPosEnd() {
        return posEnd;
    }

    public void setPosEnd(Vector3f posEnd) {
        this.posEnd = posEnd;
    }
    
    
    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    private Vector3f vectorArmed = new Vector3f(0f,0f,2f);
    
    private Vector3f posStart = new Vector3f();
    
    private Vector3f posEnd = new Vector3f();
    
    private Vector3f currentPos = new Vector3f();

    public Vector3f getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(Vector3f currentPos) {
        this.currentPos = currentPos;
    }
    
    
    public Vector3f getVectorArmed() {
        return vectorArmed;
    }

    public void setVectorArmed(Vector3f vectorArmed) {
        this.vectorArmed = vectorArmed;
      
    }

    public boolean isOneShot() {
        return oneShot;
    }

    public void setOneShot(boolean oneShot) {
        this.oneShot = oneShot;
    }
 
        
    @Override
    protected void controlUpdate(float tpf) 
    {
        /*if(!init)
        {
        posStart = this.getSpatial().getLocalTranslation().clone();
        currentPos = posStart.clone();
        posEnd = posStart.add(vectorArmed);
        init = true;
        }*/
        
       
        if(onceActived && this.isOneShot())
            return;
        
            
       if(type == Type.POSITIVE)
       {
         currentPos.interpolateLocal(posEnd, tpf * speed);
         this.getSpatial().setLocalTranslation(currentPos);

       }
       if(type == Type.NEGATIVE)
       {
         currentPos.interpolateLocal(posStart, tpf * speed);
         this.getSpatial().setLocalTranslation(currentPos);       
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
        control.setPosStart(this.getPosStart());
        control.setPosEnd(this.getPosEnd());
        control.setCurrentPos(this.getCurrentPos());
        control.setSpeed(this.getSpeed());
        control.setType(this.getType());
        control.setOneShot(this.isOneShot());
        //TODO: copy parameters to new Control
        return control;
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        this.setSpeed(in.readFloat("speed", 1f));
        Vector3f temp = new Vector3f();
        this.setVectorArmed((Vector3f)in.readSavable("VECTOR", new Vector3f(0,0,0f)));
        this.setPosStart((Vector3f)in.readSavable("POSSTART", this.getSpatial().getWorldTranslation()));
        this.setPosEnd((Vector3f)in.readSavable("POSEND", this.getSpatial().getWorldTranslation()));
        this.setCurrentPos((Vector3f)in.readSavable("POSCURRENT", new Vector3f(0,0,0)));
        this.setType(in.readEnum("type", Type.class, Type.NULL));
        this.setOneShot(in.readBoolean("oneshot", false));
      

        
       // this.setVectorArmed(temp);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        out.write(speed, "speed", 1f);
        out.write(vectorArmed, "VECTOR", new Vector3f());
        out.write(posStart, "POSSTART", new Vector3f());
        out.write(posEnd, "POSEND", new Vector3f());
        out.write(currentPos, "POSCURRENT", new Vector3f());
        
        out.write(type, "type", Type.NULL);
        out.write(this.oneShot, "oneshot", false);
        
        
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
    
}
