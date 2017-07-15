/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.AppStates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.JoystickAxis;
import com.jme3.input.JoystickButton;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import mygame.Ctrl.CibleCtrl;

/**
 *
 * @author Thonon
 */
public class MovementAppState extends AbstractAppState implements RawInputListener{
    
  private Vector3f walkDirection = new Vector3f();
  private boolean left = false, right = false, up = false, down = false,sLeft = false,sRight = false;
  private boolean isCrounch = false;
  private Vector3f camDir = new Vector3f();
  private Vector3f camLeft = new Vector3f();
  private Vector3f camAxe = new Vector3f();
  
  float POVX = 0.0f;
  float POVY = 0.0f;
  
  float LOOKY = 0.0f;
  float LOOKX = 0.0f;
  float LOOKZ = 0.0f; // pour pencher la tête
  
  private Quaternion QuatCam = new Quaternion();
  private Matrix3f MatCam = new Matrix3f(); 
   
  private Vector3f lookAt = new Vector3f();
  
  // capsule shae
  CapsuleCollisionShape capsuleDebout;
  CapsuleCollisionShape capsuleAccroupi;
  // Standing
  private boolean standing = true;
  private Vector3f offsetSquat = new Vector3f(0,1,0);
  private Vector3f offsetStanding = new Vector3f(0,2,0);
  private Vector3f offsetState = offsetStanding.clone();
  private float    speedStanding = 12f;
  
  // betterplayer
  private BetterCharacterControl player;
  private Node                   playerNode;
  
  private Camera cam;
  private BulletAppState bulletAppState;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        cam = app.getCamera();
        bulletAppState = stateManager.getState(BulletAppState.class);
        
        // physique player
        player =  new BetterCharacterControl(1f,2f,1f);
        player.setJumpForce(new Vector3f(0,28,0));
        player.setGravity(new Vector3f(0,-100,0));
        
        playerNode = new Node();
        playerNode.setLocalTranslation(0, 4, -0);
        playerNode.addControl(player);
        bulletAppState.getPhysicsSpace().add(player);
        bulletAppState.getPhysicsSpace().add(playerNode);
        
        // input
        app.getInputManager().addRawInputListener(this);
        
    }
    
    @Override
    public void update(float tpf) 
    {
        // calcul des axes de la caméra
        camDir.set(cam.getDirection()).multLocal(POVY*4);
        camLeft.set(cam.getLeft()).multLocal(POVX*4);
        walkDirection.set(0, 0, 0);
        camAxe.set(0,0,0);
        
        camDir.y = 0;
        // mise à jour du vecgteur walkDirection
        walkDirection.addLocal(camLeft);
        walkDirection.addLocal(camDir);
         // modif axes
        
        // update du walkDirection
        player.setWalkDirection(walkDirection);
        
        // mise à jour de la positino de la camera
        //cam.setLocation(player.getPhysicsLocation().add(new Vector3f(0,0.7f,0)));
        if(standing)
            offsetState.interpolateLocal(offsetStanding, tpf * speedStanding);
        else
            offsetState.interpolateLocal(offsetSquat, tpf * speedStanding);
        
        // mise à jour de la location de la caméra
         cam.setLocation(playerNode.getLocalTranslation().add(offsetState));
        
        // mise à jour de la direction de la caméra
        Quaternion q = cam.getRotation();
        QuatCam.fromAngles(LOOKX,LOOKY, 0f);
        q.multLocal(QuatCam);
        q.lookAt(cam.getDirection(), Vector3f.UNIT_Y);
        cam.setRotation(q);
           
       // update
        player.update(tpf);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

   @Override
    public void beginInput() {
        
    }

    @Override
    public void endInput() {
      
    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) 
    {
       JoystickAxis axis = evt.getAxis();
       if(axis.getLogicalId() == JoystickAxis.X_AXIS)       
            POVX = (-evt.getValue()) ;
       
       if(axis.getLogicalId() == JoystickAxis.Y_AXIS)
           POVY = (-evt.getValue()) ;
            
       if(axis.getLogicalId() == JoystickAxis.Z_AXIS)
           LOOKY = -evt.getValue() / 8f;
       
       if(axis.getLogicalId() == JoystickAxis.Z_ROTATION)
           LOOKX = evt.getValue() / 8f;
      
       
    }

    
    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) 
    {
       if(evt.getButton().getLogicalId() == JoystickButton.BUTTON_5)
       {
           if(evt.isPressed())
           {
               // se mettre à croupi
               standing = false;
           }
           else
              standing = true;
           
       }
      
        
     System.out.println(evt.getButton().getLogicalId());
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
       
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
       
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
       
    }

    @Override
    public void onTouchEvent(TouchEvent evt) {
       
    }
    
}
