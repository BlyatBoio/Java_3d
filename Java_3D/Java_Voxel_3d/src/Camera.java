
import Interaction_Helpers.KeyInputListener;
import Interaction_Helpers.MouseInputListener;
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
    public static final int resolution = 5; // how many pixels are drawn per ray
    public static final int screenWidth = 1200;
    public static final int screenHeight = 600;
    public static final double aspectRatio = screenWidth / screenHeight;
    public static final int FPS = 60;
    public static final int FPSScaling = 60/FPS;

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

    private static final int[] defaultReturn = new int[3];
    private static boolean keyPressed = false;

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
        polys = ShapeMaker.addToArrayList(ShapeMaker.getCube(0, -10, 50, 10, 10, 10), polys);
        JPanel p = new JPanel();  
        
        keyboardL.addSelf(f);
        mouseL.addSelf(f);
        try {
            mouseLocker = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    
        f.setSize(screenWidth, screenHeight);
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
            mouseLocker.mouseMove(screenWidth/2, screenHeight/2);
        }
    }

    private static void cullPolys(){
        culledPolys.clear();
        for(Polygon p : polys){  
            culledPolys.add(p);
        }
    }

    private static double dist3D(double x, double y, double z, double x2, double y2, double z2){
        return Math.sqrt((x2-x)*(x2-x)+(y2-y)*(y2-y)+(z2-z)*(z2-z));
    }

    private static void castRays(){
        painter.setPixelGroup(0, 0, screenWidth, screenHeight, 0, 0, 0);
        cullPolys();
        
        Quaternion fwd = AngleHandler.asQuaternion(forward);
        for(int x = 0; x < screenWidth/resolution; x++){
            for(int y = 0; y < screenHeight/resolution; y++){
                int[] color = cast(getRay(x-(screenWidth/resolution)/2, y-(screenHeight/resolution)/2, fwd));
                if(color != defaultReturn) painter.setPixelGroup(x*resolution, y*resolution, resolution, resolution, color);
            }
        }
    }

    private static Raycast getRay(int x, int y, Quaternion fwd){
        // Q0 affects FOV
        return new Raycast(position.copy(), AngleHandler.mult(new Quaternion(3, ((double)y / (screenHeight/resolution)) / aspectRatio, ((double)x / (screenWidth/resolution)), 0), fwd));
    }   

    private static int[] cast(Raycast ray){
        minDist = 100000000;
        hit = false;
        for(Polygon p: culledPolys){
            polygonHitInfo d = isRayIntersecting(p, ray);
            if(d.didHit && d.distance != 0 && d.distance < minDist){
                minDist = d.distance;
                hit = true;
                if(d.isLine) returnColor = new int[]{0, 0, 0};
                else returnColor = new int[]{(int)(d.u*255), 0, (int)(d.v*255)};
            }
        }
        if(!hit) return new int[3];
        return returnColor;
    }
    
    private static polygonHitInfo isRayIntersecting(Polygon p, Raycast ray){
        Vector3D normal = ray.vect.copy().cross(p.e2);
        double det = p.e1.dot(normal);

        double invDet = 1/det;
        Vector3D s = ray.origin.copy().sub(p.p1);
        double u = invDet * s.dot(normal);

        if ((u < 0 && Math.abs(u) > epsilon) || (u > 1 && Math.abs(u-1) > epsilon)) return noHit;

        Vector3D s_cross_e1 = s.copy().cross(p.e1);
        double v = invDet * ray.vect.dot(s_cross_e1);

        if ((v < 0 && Math.abs(v) > epsilon) || (u + v > 1 && Math.abs(u + v - 1) > epsilon)) return noHit;

        double t = invDet * p.e2.copy().dot(s_cross_e1);
        if(t > epsilon) return new polygonHitInfo(true, u, v, t, normal, (drawLine && (u < 0.02 || v < 0.02 || u > 0.98 || v > 0.98 || u+v > 0.98))); 
        return noHit;
    }
}