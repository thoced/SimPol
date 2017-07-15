package mygame;

import com.jme3.ai.navmesh.NavMesh;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.input.JoyInput;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.input.JoystickButton;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.JoyAxisTrigger;
import com.jme3.input.controls.JoyButtonTrigger;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.water.SimpleWaterProcessor;
import java.util.List;

import edu.ufl.digitalworlds.j4k.*;
import mygame.AppStates.DynamicAppState;
import mygame.AppStates.MovementAppState;
import mygame.AppStates.TriggerCibleAppState;
import mygame.Ctrl.CibleCtrl;



/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication  {

  private BulletAppState bulletAppState;
  private RigidBodyControl landscape;
  private RigidBodyControl landscape2;
  //private CharacterControl player;
  private BetterCharacterControl player;
  private Node                   playerNode;
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
  
  // NavMesh
  private NavMesh navMesh;
  private AgentCtrl agent;
  
  // Node
  private Spatial sceneModel;
   
    
    public static void main(String[] args) {
        Main app = new Main();
        
        //AppSettings settings = new AppSettings(true);
        //settings.setUseJoysticks(true);
        //app.setSettings(settings);
        app.start();    
        
      
    }

    @Override
    public void simpleInitApp() {
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        
        getFlyByCamera().setEnabled(false);
        this.getFlyByCamera().setMoveSpeed(6f);
        this.getFlyByCamera().setRotationSpeed(0.8f);
       // 

      /*  Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);*/
      
              /** Load a model. Uses model and texture from jme3-test-data library! */ 
        sceneModel = (Node)assetManager.loadModel("Scenes/scene03.j3o");
        //sceneModel.setLocalScale(4f);
        
        // creation de la physique
        bulletAppState = new BulletAppState();
        this.stateManager.attach(bulletAppState);
       
        try
        {
        // creation de la physique de la scene
            CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape((Spatial)((Node)sceneModel).getChild("OutdoorNode"));
                if(sceneShape != null)
                {
                    landscape = new RigidBodyControl(sceneShape, 0);
                    sceneModel.addControl(landscape);
                    bulletAppState.getPhysicsSpace().add(landscape);
                 }
        }catch(IllegalArgumentException iae)
        {
            
        }
        
        try
        {
        // creation de la physique de la scene
            CollisionShape sceneShape2 =
                CollisionShapeFactory.createMeshShape((Spatial)((Node)sceneModel).getChild("IndoorNode"));
                if(sceneShape2 != null)
                {
                    landscape2 = new RigidBodyControl(sceneShape2, 0);
                    sceneModel.addControl(landscape2);
                    bulletAppState.getPhysicsSpace().add(landscape2);
                 }
        }catch(IllegalArgumentException iae)
        {
            
        }
       
        // physique du personnage
        capsuleDebout = new CapsuleCollisionShape(1f, 1.8f, 1);
        capsuleAccroupi = new CapsuleCollisionShape(1.5f,3f,1);
             
        // ajout du sceneModel
        this.rootNode.attachChild(sceneModel);
      //  bulletAppState.getPhysicsSpace().add(player);
        
        // création des appState
        this.getStateManager().attach(new MovementAppState());
        this.getStateManager().attach(new TriggerCibleAppState());
        this.getStateManager().attach(new DynamicAppState());
     
      // Water
      
      
     
    }

    @Override
    public void simpleUpdate(float tpf) 
    {
      
         
       
     
        
    }
   


    
    /* @Override
    public void simpleUpdate(float tpf) 
    {
        camDir.set(cam.getDirection()).multLocal(0.2f);
        camLeft.set(cam.getLeft()).multLocal(POVXLeft);
        walkDirection.set(0, 0, 0);
        camAxe.set(0,0,0);
        
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        if(sLeft)
        {
             camAxe.addLocal(camLeft.mult(6f));
        }
        if(sRight)
        {
            camAxe.addLocal(camLeft.mult(6f).negate());
        }
        
         // modif axes
        
        
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation().add(camAxe));
        player.update(tpf);
        
       
     
        
    }*/

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    private void setNavMesh(Node scene)
    {
        // récupération du mesh
        Spatial geo = scene.getChild("chemin");
        if(geo != null)
        {
            // chargement du navmesh
            Mesh mesh = ((Geometry)geo).getMesh();
            navMesh  = new NavMesh(mesh);
            
            NavMeshPathfinder f = new NavMeshPathfinder(navMesh);
            
            
        }
        else
        {
            System.out.println("Erreur de chargement du NavMesh");
        }
    }
    
   
    
    

   /* @Override
    public void onAction(String binding, boolean value, float tpf)
    {
          System.out.println(binding);
        
        if(binding.equals("JOYLEFT"))
        {
            System.out.println("LEFFFF ACTION");
        }
        
        if (binding.equals("Left")) 
        {
      if (value) { left = true; } else { left = false; }
    } else if (binding.equals("Right")) {
      if (value) { right = true; } else { right = false; }
    } else if (binding.equals("Up")) {
      if (value) { up = true; } else { up = false; }
    } else if (binding.equals("Down")) {
      if (value) { down = true; } else { down = false; }
    } else if (binding.equals("Jump") && value) {
      player.jump();
      
    }
    else if(binding.equals("Crounch") && !isCrounch)
    {
       //player.setPhysicsLocation(player.getPhysicsLocation().subtract(new Vector3f(0,10,0)));
      // player.getCollisionShape().setScale(new Vector3f(1,0.5f,1));
        
       player.setCollisionShape(capsuleAccroupi);
     
       isCrounch = !isCrounch;
    }
    else 
        if(binding.equals("Stref_left"))
    {
         if (value)
             sLeft = true;
         else
             sLeft = false;
    }
    else
        if(binding.equals("Stref_right"))
        {
            if(value)
                sRight = true;
            else
                sRight = false;
        }
        
    // clic
    if(binding.equals("Clic"))
    {
         CollisionResults results = new CollisionResults();
         
          Ray ray = new Ray(cam.getLocation(), cam.getDirection());
          
          sceneModel.collideWith(ray, results);
          
          if(results.size() > 0)
          {
              System.out.println("collision !!");
              agent.goTo(results.getClosestCollision().getContactPoint());
          }
          else
              System.out.println("No collision");
         
    }
        
        
        
    }*/

  

    private void setUpJoys() 
    {
        Joystick[] joysticks = inputManager.getJoysticks();
        if (joysticks == null)
            throw new IllegalStateException("Cannot find any joysticks!");
        
            for(Joystick j : joysticks)
            {
                System.out.println(j);
            }
    }

    
}
