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
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.water.WaterFilter;

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
  private Vector3f offsetStanding = new Vector3f(0,2.3f,0);
  private Vector3f offsetState = offsetStanding.clone();
  private float    speedStanding = 12f;
  
  // Shift (pencher la tête)
  private final Vector3f offsetStateHead = new Vector3f();
  private Vector3f offsetLeftHead;
  private Vector3f offsetRightHead;
  private int     posHead = 0; // -1 : left  +1 : right   0 = centre
  private  float   angleLean = 0f; // lean
  private float angleLeanMaxRight = 0.55f;
  private float angleLeanMaxLeft = -0.55f;
  private float speedLeanHead = 4f;
  
  
  // betterplayer
  private BetterCharacterControl player;
  private Node                   playerNode;
  
  private Camera cam;
  private BulletAppState bulletAppState;
    
  WaterFilter water;
  
    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        cam = app.getCamera();
        bulletAppState = stateManager.getState(BulletAppState.class);
        
        // physique player
        player =  new BetterCharacterControl(1f,2f,1f);
        player.setJumpForce(new Vector3f(0,28,0));
        player.setGravity(new Vector3f(0,-1,0));
        
        
        playerNode = new Node();
        playerNode.setLocalTranslation(27.16068f, 8, -31.609413f);
        playerNode.addControl(player);
        bulletAppState.getPhysicsSpace().add(player);
       // bulletAppState.getPhysicsSpace().add(playerNode);
        
        
        
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
        camLeft.y =0;
        
        // mise à jour du vecgteur walkDirection
        walkDirection.addLocal(camLeft);
        walkDirection.addLocal(camDir);
               // modif axes
        
        // update du walkDirection
        player.setWalkDirection(walkDirection);
        player.setViewDirection(cam.getDirection());
        
        // mise à jour de la positino de la camera
        //cam.setLocation(player.getPhysicsLocation().add(new Vector3f(0,0.7f,0)));
        if(standing)
            offsetState.interpolateLocal(offsetStanding, tpf * speedStanding);
        else
            offsetState.interpolateLocal(offsetSquat, tpf * speedStanding);
        
        // Systeme de tête penchée
        switch(posHead)
        {
            case -1 : offsetStateHead.interpolateLocal(offsetLeftHead, tpf * speedLeanHead);
                      angleLean = FastMath.interpolateLinear(tpf * speedLeanHead, angleLean, angleLeanMaxLeft);
                      break;
            
            case 1: offsetStateHead.interpolateLocal(offsetRightHead, tpf * speedLeanHead);
                    angleLean = FastMath.interpolateLinear(tpf * speedLeanHead, angleLean, angleLeanMaxRight);
                    break;
            
            case 0: offsetStateHead.interpolateLocal(Vector3f.ZERO, tpf * speedLeanHead);
                    angleLean = FastMath.interpolateLinear(tpf * speedLeanHead, angleLean, 0f);
                    break;
        }
        
        // mise à jour de la location de la caméra
         cam.setLocation(playerNode.getLocalTranslation().add(offsetState).add(offsetStateHead));
        // cam.setRotation(playerNode.getWorldTransform().getRotation());
      
        
        // Quaternion de rotation de la caméra sur base des mouvements du gamepad
        Quaternion currentRot = cam.getRotation();
        QuatCam.loadIdentity();
        QuatCam.fromAngles(LOOKX,LOOKY, 0);
        

        // multiplication des quaternions
        currentRot.multLocal(QuatCam);
        Vector3f[] axis = new Vector3f[3];
        
        // creation du leanVecteur
        Quaternion quatLean = new Quaternion();
        quatLean.fromAngleAxis(angleLean,cam.getDirection());
        Vector3f leanVec = quatLean.mult(Vector3f.UNIT_Y);
        
       /* currentRot.toAxes(axis);
        Vector3f XX = leanVec.cross(axis[2]);
        Vector3f YY = axis[2].cross(XX);
        currentRot.fromAxes(XX,YY,axis[2]);*/
        
       currentRot.lookAt(cam.getDirection(), leanVec);
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
       
       // Pencher la tête
       if(evt.getButton().getLogicalId() == JoystickButton.BUTTON_7)
       {
           if(evt.isPressed())
           {
               offsetRightHead = cam.getLeft().negate();
               posHead = 1;
              
           }
           else
               posHead = 0;
         
       }
       
       if(evt.getButton().getLogicalId() == JoystickButton.BUTTON_6)
       {
           if(evt.isPressed())
           {
               offsetLeftHead = cam.getLeft();
               posHead = -1;
              
           }
           else
               posHead = 0;
         
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
