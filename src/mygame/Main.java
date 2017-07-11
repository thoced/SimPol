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
import mygame.AppStates.TriggerCibleAppState;
import mygame.Ctrl.CibleCtrl;



/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication implements RawInputListener  {

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
        
       
        
        this.setUpJoys();
        this.setUpKeys();
        
       
       

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
        
       /* player = new CharacterControl(capsuleDebout, 0.05f);
        player.setJumpSpeed(28);
        player.setFallSpeed(30);
        player.setGravity(30);
        player.setPhysicsLocation(new Vector3f(-0,5, 1));
        player.setApplyPhysicsLocal(false);
        player.setCollisionGroup(1);*/
        
        player =  new BetterCharacterControl(1f,2f,1f);
        player.setJumpForce(new Vector3f(0,28,0));
        player.setGravity(new Vector3f(0,-100,0));
        
        playerNode = new Node();
        playerNode.setLocalTranslation(0, 4, -5);
        playerNode.addControl(player);
        bulletAppState.getPhysicsSpace().add(player);
        bulletAppState.getPhysicsSpace().add(playerNode);
       
       
        
        
        // ajout du sceneModel
        this.rootNode.attachChild(sceneModel);
      //  bulletAppState.getPhysicsSpace().add(player);
        
        // création des appState
        this.getStateManager().attach(new TriggerCibleAppState());
        this.getStateManager().attach(new DynamicAppState());
     
      // Water
      
      
     
    }

    @Override
    public void simpleUpdate(float tpf) 
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
        cam.setLocation(playerNode.getLocalTranslation().add(new Vector3f(0,2f,0)));
        // mise à jour de la direction de la caméra
        Quaternion q = cam.getRotation();
        QuatCam.fromAngles(LOOKX,LOOKY, 0f);
        q.multLocal(QuatCam);
        q.lookAt(cam.getDirection(), Vector3f.UNIT_Y);
        cam.setRotation(q);
           
       // update
        player.update(tpf);
        
       
     
        
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
    
   
    
    private void setUpKeys() 
    {
    /*inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_Q));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_Z));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addMapping("Crounch", new KeyTrigger(KeyInput.KEY_LCONTROL));
    inputManager.addMapping("Stref_right", new KeyTrigger(KeyInput.KEY_E));
    inputManager.addMapping("Stref_left", new KeyTrigger(KeyInput.KEY_A));*/
        
    inputManager.addRawInputListener(this);
   
    // clic
    //inputManager.addMapping("Clic", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    
   /* inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
    inputManager.addListener(this, "Crounch");
    inputManager.addListener(this, "Stref_right");
    inputManager.addListener(this, "Stref_left");*/
    
    // listener pour le clic
   // inputManager.addListener(this, "Clic");
    

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
       if(evt.getButton().getLogicalId() == JoystickButton.BUTTON_0)
       {
           if(evt.isPressed())
           {
            Spatial cible = ((Spatial)this.rootNode.getChild("CIBLE"));
            if(cible != null)
            {
                CibleCtrl ctrl = cible.getControl(CibleCtrl.class);
                if(ctrl != null)
                {
                    ctrl.setType(CibleCtrl.Type.POSITIVE);
                }
            }
           }
           
       }
       
       if(evt.getButton().getLogicalId() == JoystickButton.BUTTON_1)
       {
           if(evt.isPressed())
           {
            Spatial cible = ((Spatial)this.rootNode.getChild("CIBLE"));
            if(cible != null)
            {
                CibleCtrl ctrl = cible.getControl(CibleCtrl.class);
                if(ctrl != null)
                {
                    ctrl.setType(CibleCtrl.Type.NEGATIVE);
                }
            }
           }
           
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
