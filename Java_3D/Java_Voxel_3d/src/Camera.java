
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
    public static Vector3D position = new Vector3D(0, 0, 0);
    public static AxisAngle forward = new AxisAngle(0, 1, 0, 0);
    public static final int resolution = 5;
    public static final int screenWidth = 800;
    public static final int screenHeight = 800;
    public static final double aspectRatio = screenWidth / screenHeight;
    public static final int FPS = 20;
    public static final int FPSScaling = 60/FPS;
    public static PixelPainter painter = new PixelPainter(screenWidth, screenHeight);
    public static ArrayList<Polygon> polys = new ArrayList<>();
    private static final double epsilon = 0.0001;

    private static final JFrame f = new JFrame();
    private static final BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    private static final Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
            cursorImg, new Point(0, 0), "blank cursor");
    private static final KeyInputListener keyboardL = new KeyInputListener();      
    private static final MouseInputListener mouseL = new MouseInputListener();
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

        polys = ShapeMaker.addToArrayList(ShapeMaker.getCube(-10, -10, -10, 20, 20, 20), polys);
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
        // Not this this is very wrong -> roughly, q1 affects y, q2 affects x, and q3 affects zx
        //rotateBy(new Quaternion(50, 2*mouseL.getMovedY()/FPS, 4*mouseL.getMovedX()/FPS, 0));
        if(mouseL.isMouseDown()) {
            isMouseLocked = true;
            f.setCursor(blankCursor);
        }
        if(keyboardL.keyIsDown(27)) {
            isMouseLocked = false;
            f.setCursor(Cursor.DEFAULT_CURSOR);
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

    private static void castRays(){
        painter.setPixelGroup(0, 0, screenWidth, screenHeight, 0, 0, 0);
        //sortPolys();
        for(int x = 0; x < screenWidth/resolution; x++){
            for(int y = 0; y < screenHeight/resolution; y++){
                int[] color = cast(getRay(x-(screenWidth/resolution)/2, y-(screenHeight/resolution)/2));
                if(color != defaultReturn) {
                    painter.setPixelGroup(x*resolution, y*resolution, resolution, resolution, color);
                }
            }
        }
    }

    private static Raycast getRay(int x, int y){
        //return new Raycast(position.copy().add(AngleHandler.getRotated(new Vector3D(x, y, 0), forward)), forward);
        return new Raycast(position.copy(), AngleHandler.mult(new Quaternion(10, ((double)y / (screenHeight/resolution)) / aspectRatio, ((double)x / (screenWidth/resolution)), 0), AngleHandler.asQuaternion(forward)));
    }   

    private static int[] cast(Raycast ray){
        int[] returnColor = new int[3];
        double minDist = 1000000000;
        boolean hit = false;
        for(Polygon p: polys){
            double d = isRayIntersecting(p, ray);
            if(d != 0 && d < minDist){
                minDist = d;
                hit = true;
                returnColor = p.color;
            }
        }
        if(!hit) return new int[3];
        return returnColor;
    }
    
    private static double isRayIntersecting(Polygon p, Raycast ray){
        Vector3D e1 = p.p2.copy().sub(p.p1);
        Vector3D e2 = p.p3.copy().sub(p.p1);
        Vector3D rayDir = AngleHandler.getRotated(new Vector3D(0, 0, 1), ray.direction);
        Vector3D normal = rayDir.copy().cross(e2);
        double det = e1.dot(normal);

        double invDet = 1/det;
        Vector3D s = ray.origin.copy().sub(p.p1);
        double u = invDet * s.dot(normal);

        if ((u < 0 && Math.abs(u) > epsilon) || (u > 1 && Math.abs(u-1) > epsilon)) return 0;

        Vector3D s_cross_e1 = s.copy().cross(e1);
        double v = invDet * rayDir.dot(s_cross_e1);

        if ((v < 0 && Math.abs(v) > epsilon) || (u + v > 1 && Math.abs(u + v - 1) > epsilon)) return 0;

        double t = invDet * e2.copy().dot(s_cross_e1);
        if(t > epsilon) return t;
        else return 0;
    }
}