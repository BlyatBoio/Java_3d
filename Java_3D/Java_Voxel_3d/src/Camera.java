
import Interaction_Helpers.KeyInputListener;
import Interaction_Helpers.MouseInputListener;
import Primatives_3D.Mesh;
import Primatives_3D.Polygon;
import Primatives_3D.Raycast;
import Primatives_3D.ShapeMaker;
import Primatives_3D.Vector3D;
import Rotation_3D.AngleHandler;
import Rotation_3D.AxisAngle;
import Rotation_3D.EulerAngle;
import Rotation_3D.Quaternion;
import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Camera {
    @SuppressWarnings("FieldMayBeFinal")

    // Position and rotational variables
    public static Vector3D position = new Vector3D(0, 0, 0);
    public static AxisAngle forward = new AxisAngle(0, 1, 0, 0);
    // Display varibles
    public static final int resolution = 3; // how many pixels are drawn per ray
    public static final int screenWidth = 1200;
    public static final int screenHeight = 800;
    public static final double aspectRatio = (double) screenWidth / screenHeight;
    public static final int FPS = 30;
    public static final int FPSScaling = 60/FPS;
    // Constants to reduce computation when casting rays
    public static final int widthRes = screenWidth/resolution;
    public static final int halfWidthRes = (screenWidth/resolution)/2;
    public static final int heightRes = screenHeight/resolution;
    public static final int halfHeightRes = (screenHeight/resolution)/2;

    // Raycast storage
    public static Raycast[] rays;
    public static Quaternion[] raycastRotations;

    // Polygon storage and organization
    public static ArrayList<Polygon> polys = new ArrayList<>();
    private static ArrayList<Polygon> culledPolys = new ArrayList<>();
    // Pre-defined variables for polygon colision checks
    private static final double epsilon = 0.0001;
    private static boolean drawLine = true;
    private static boolean hit = false;
    private static double minDist = 100000000;
    private static int[] returnColor = new int[3];
    private static polygonHitInfo noHit = new polygonHitInfo(false);
    private static final int[] defaultReturn = new int[]{0, 0, 0};
    // Window and JFrame init variables
    private static final JFrame f = new JFrame();
    private static final BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    private static final Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            cursorImg, new Point(0, 0), "blank cursor");
    private static final KeyInputListener keyboardL = new KeyInputListener();      
    private static final MouseInputListener mouseL = new MouseInputListener();
    public static PixelPainter painter = new PixelPainter(screenWidth, screenHeight);
    private static Robot mouseLocker;
    private static boolean isMouseLocked = false;
    private static boolean keyPressed = false;

    private static Mesh cube2 = ShapeMaker.getCube(7, 7, 7, 1, 1, 1);
    private static Mesh cube1 = ShapeMaker.getCube(5, 5, 5, 10, 10, 10);

    public static void rotateBy(Quaternion direction){
        keyPressed = true;
        forward = AngleHandler.asAxisAngle(AngleHandler.mult(AngleHandler.asQuaternion(forward), AngleHandler.normalize(direction)));
    }

    public static void rotateByLocal(Quaternion direction){
        keyPressed = true;
        forward = AngleHandler.asAxisAngle(AngleHandler.mult(AngleHandler.normalize(direction), AngleHandler.asQuaternion(forward)));
    }

    public static void moveBy(Vector3D vec){
        position.add(vec);
    }
    
    public static void moveBy(double x, double y, double z){
        position.x += x;
        position.y += y;
        position.z += z;
    }

    public static void moveByLocal(Vector3D vec){
        moveBy(AngleHandler.getRotated(vec, AngleHandler.asQuaternion(forward)));
    }
    
    public static void moveByLocal(double x, double y, double z){
        moveBy(AngleHandler.getRotated(new Vector3D(x, y, z), AngleHandler.asQuaternion(forward)));
    }

    public static void startRenderer(){

        polys = ShapeMaker.addToArrayList(ShapeMaker.getCube(-100, -100, -200, 200, 200, 400), polys);
        polys = ShapeMaker.addToArrayList(cube1, polys);
        polys = ShapeMaker.addToArrayList(cube2, polys);

        defineRays();

        JPanel p = new JPanel();  
        
        keyboardL.addSelf(f);
        mouseL.addSelf(f);
        try {
            mouseLocker = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    
        f.setSize(screenWidth, screenHeight);
        f.setLocation(200, 100);
        f.setVisible(true);
        f.add(p);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(painter);     
           
        Timer time = new Timer();
            TimerTask task = new TimerTask(){
                @Override
                public void run(){
                    update();
                }
            };
            time.scheduleAtFixedRate(task, 0, 1000/FPS);
    }

    public static void update(){
        keyPressed = false;
        castRays();
        if(mouseL.isMouseDown()) {
            isMouseLocked = true;
            f.setCursor(blankCursor);
        }
        if(keyboardL.keyIsDown(27)) {
            isMouseLocked = false;
            f.setCursor(Cursor.getDefaultCursor());
        }
        cube1.rotate(new AxisAngle(0.2, 0, 1, 1));
        rotateBy(AngleHandler.asQuaternion(new EulerAngle(0, mouseL.getMovedX()/(FPS*2), 0)));
        rotateByLocal(AngleHandler.asQuaternion(new EulerAngle(mouseL.getMovedY()/(FPS*2), 0, 0)));
        if(keyboardL.keyIsDown(87)) {moveByLocal(0, 0, 0.5*FPSScaling); keyPressed = true;}
        if(keyboardL.keyIsDown(83)) {moveByLocal(0, 0, -0.5*FPSScaling); keyPressed = true;}
        if(keyboardL.keyIsDown(65)) {moveByLocal(-0.005*FPSScaling, 0, 0); keyPressed = true;}
        if(keyboardL.keyIsDown(68)) {moveByLocal(0.005*FPSScaling, 0, 0); keyPressed = true;}
        if(keyboardL.keyIsDown(32)) {moveByLocal(0, 0.05*FPSScaling, 0); keyPressed = true;}
        if(keyboardL.keyIsDown(16)) {moveByLocal(0, -0.05*FPSScaling, 0); keyPressed = true;}
        if(keyPressed == true) painter.repaint();
        if(isMouseLocked){
            mouseLocker.mouseMove(f.getX() + screenWidth/2, f.getY() + screenHeight/2);
        }
    }

    private static void cullPolys(){
        culledPolys.clear();

        for(Polygon p : polys){
            culledPolys.add(p);
        }
    }

    private static double dist3D(Vector3D v1, Vector3D v2){
        return Math.sqrt((v1.x-v2.x)*(v1.x-v2.x)+(v1.y-v2.y)*(v1.y-v2.y)+(v1.z-v2.z)*(v1.z-v2.z));
    }

    private static void castRays(){
        cullPolys();
        
        Quaternion fwd = AngleHandler.asQuaternion(forward);
        for(int x = 0; x < widthRes; x++){
            for(int y = 0; y < heightRes; y++){
                int i = y+x*heightRes;
                rays[i].set(position.copy(), AngleHandler.mult(raycastRotations[i], fwd));
                painter.setPixelGroup(x*resolution, y*resolution, resolution, resolution, cast(rays[i]));
            }
        }
    }

    private static void defineRays(){
        ArrayList<Raycast> tempRays = new ArrayList<>();
        ArrayList<Quaternion> tempQuats = new ArrayList<>();
        
        Quaternion fwd = AngleHandler.asQuaternion(forward);
        for(int x = 0; x < widthRes; x++){
            for(int y = 0; y < heightRes; y++){
                Quaternion quat = new Quaternion(3, ((double)(y-(heightRes/2)) / heightRes) / aspectRatio, ((double)(x-(widthRes/2)) / widthRes), 0);
                tempQuats.add(quat);
                tempRays.add(new Raycast(position.copy(), AngleHandler.mult(quat, fwd)));
            }
        }

        rays = tempRays.toArray(Raycast[]::new);
        raycastRotations = tempQuats.toArray(Quaternion[]::new);
    }

    private static int[] cast(Raycast ray){
        minDist = 100000000;
        hit = false;
        for(Polygon p: culledPolys){
            polygonHitInfo d = isRayIntersecting(p, ray);
            if(d.didHit && d.distance != 0 && d.distance < minDist){
                minDist = d.distance;
                hit = true;
                if(d.isLine) returnColor = defaultReturn;
                //else returnColor = new int[]{(int)(d.u*255), 0, (int)(d.v*255)};
                else returnColor = p.color;
            }
        }
        if(!hit) return defaultReturn;
        return returnColor;
    }
    
    private static polygonHitInfo isRayIntersecting(Polygon p, Raycast ray){

        Vector3D e1 = p.v2.copy().sub(p.v1);
        Vector3D e2 = p.v3.copy().sub(p.v1);

        Vector3D normal = ray.vect.copy().cross(e2);
        double det = e1.dot(normal);

        double invDet = 1/det;
        Vector3D s = ray.origin.copy().sub(p.v1);
        double u = invDet * s.dot(normal);

        if ((u < 0 && Math.abs(u) > epsilon) || (u > 1 && Math.abs(u-1) > epsilon)) return noHit;

        Vector3D s_cross_e1 = s.copy().cross(e1);
        double v = invDet * ray.vect.dot(s_cross_e1);

        if ((v < 0 && Math.abs(v) > epsilon) || (u + v > 1 && Math.abs(u + v - 1) > epsilon)) return noHit;

        double t = invDet * e2.copy().dot(s_cross_e1);
        if(t > epsilon) return new polygonHitInfo(true, u, v, t, normal, (drawLine && (u < 0.02 || v < 0.02 || u > 0.98 || v > 0.98 || u+v > 0.98))); 
        return noHit;
    }
}