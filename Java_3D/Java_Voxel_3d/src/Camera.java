
import Interaction_Helpers.KeyInputListener;
import Interaction_Helpers.MouseInputListener;
import Primatives_3D.Polygon;
import Primatives_3D.Raycast;
import Primatives_3D.ShapeMaker;
import Primatives_3D.Vector3D;
import Rotation_3D.AngleHandler;
import Rotation_3D.Quaternion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Camera {
    public static Vector3D position = new Vector3D(0, 0, 0);
    public static Quaternion forward = new Quaternion(1, 0, 0, 0);
    public static final int resolution = 5;
    public static final int screenWidth = 800;
    public static final int screenHeight = 500;
    public static final double aspectRatio = screenWidth / screenHeight;
    public static final int FPS = 60;
    public static PixelPainter painter = new PixelPainter(screenWidth, screenHeight);
    public static ArrayList<Polygon> polys = new ArrayList<>();
    
    private static final KeyInputListener keyboardL = new KeyInputListener();      
    private static final MouseInputListener mouseL = new MouseInputListener();
    private static final int[] defaultReturn = new int[3];
    private static boolean keyPressed = false;

    public static void rotateBy(Quaternion direction){
        keyPressed = true;
        forward = AngleHandler.mult(direction, forward);
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
        moveBy(AngleHandler.getRotated(vec, forward));
    }
    
    public static void moveByLocal(double x, double y, double z){
        moveBy(AngleHandler.getRotated(new Vector3D(x, y, z), forward));
    }

    public static void startRenderer(){

        polys = ShapeMaker.addToArrayList(ShapeMaker.getCube(-10, -10, -10, 20, 20, 20), polys);
        JFrame f = new JFrame();
        JPanel p = new JPanel();  
        
        keyboardL.addSelf(f);
        mouseL.addSelf(f);
        
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
        // roughly, q1 affects y, q2 affects x, and q3 affects zx
        rotateBy(new Quaternion(50, 2*mouseL.getMovedY()/FPS, 4*mouseL.getMovedX()/FPS, 0));
        if(keyboardL.keyIsDown(87)) {moveByLocal(0, 0, 0.5); keyPressed = true;}
        if(keyboardL.keyIsDown(83)) {moveByLocal(0, 0, -0.5); keyPressed = true;}
        if(keyboardL.keyIsDown(65)) {moveByLocal(-0.005, 0, 0); keyPressed = true;}
        if(keyboardL.keyIsDown(68)) {moveByLocal(0.005, 0, 0); keyPressed = true;}
        if(keyboardL.keyIsDown(32)) {moveByLocal(0, 0.05, 0); keyPressed = true;}
        if(keyboardL.keyIsDown(16)) {moveByLocal(0, -0.05, 0); keyPressed = true;}
        if(keyPressed == true) painter.repaint();
    }

    private static void castRays(){
        painter.setPixelGroup(0, 0, screenWidth, screenHeight, 0, 0, 0);
        sortPolys();
        for(int x = 0; x < screenWidth/resolution; x++){
            for(int y = 0; y < screenHeight/resolution; y++){
                int[] color = cast(getRay(x-(screenWidth/resolution)/2, y-(screenHeight/resolution)/2));
                if(color != defaultReturn) {
                    painter.setPixelGroup(x*resolution, y*resolution, resolution, resolution, color);
                }
            }
        }
    }


    private static void sortPolys(){
        Collections.sort(polys, new Comparator<Polygon>() {
            @Override
            public int compare(Polygon p1, Polygon p2) {
                return Double.compare(distToPoly(p1), distToPoly(p1));
            }
        });
        //Collections.sort(polys, (p1, p2) -> distToPoly(p1).compareTo(distToPoly(p2)));
    }

    private static double distToPoly(Polygon p){
        Vector3D ab = p.p2.copy().sub(p.p1);
        Vector3D ac = p.p3.copy().sub(p.p1);
        Vector3D ap = position.copy().sub(p.p1);
        
        double d1 = ab.dot(ap);
        double d2 = ac.dot(ap);

        if(d1 < 0 && d2 < 0) return dist3D(position.x, position.y, position.z, p.p1.x, p.p1.y, p.p1.z);
 
        Vector3D bp = position.copy().sub(p.p2);
        double d3 = ab.dot(bp);
        double d4 = ac.dot(bp);
        if (d3 >= 0 && d4 <= d3) return dist3D(position.x, position.y, position.z, p.p2.x, p.p2.y, p.p2.z);

        Vector3D cp = position.copy().sub(p.p3);
        double d5 = ab.dot(cp);
        double d6 = ac.dot(cp);
        if (d6 >= 0 && d5 <= d6) return dist3D(position.x, position.y, position.z, p.p3.x, p.p3.y, p.p3.z);

        double vc = d1 * d4 - d3 * d2;
        if (vc <= 0 && d1 >= 0 && d3 <= 0)
        {
            double v = d1 / (d1 - d3);
            Vector3D point = p.p1.copy().add(v).add(ab);
                return dist3D(position.x, position.y, position.z, point.x, point.y, point.z);
        }
            
        double vb = d5 * d2 - d1 * d6;
        if (vb <= 0 && d2 >= 0 && d6 <= 0)
        {
            double v = d2 / (d2 - d6);
            Vector3D point = p.p1.copy().add(v).add(ac);
            return dist3D(position.x, position.y, position.z, point.x, point.y, point.z);
        }
            
        double va = d3 * d6 - d5 * d4;
        if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0)
        {
            double v = (d4 - d3) / ((d4 - d3) + (d5 - d6));
            Vector3D point = p.p2.copy().add(v).mult(p.p3.copy().sub(p.p2));
            return dist3D(position.x, position.y, position.z, point.x, point.y, point.z);
        }

        double denom = 1 / (va + vb + vc);
        double v = vb * denom;
        double w = vc * denom;
        Vector3D point = p.p1.copy().add(ab.copy().mult(v)).add(ac.mult(w));
        return dist3D(position.x, position.y, position.z, point.x, point.y, point.z);
    }

    private static double dist3D(double x1, double y1, double z1, double x2, double y2, double z2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z1-z2)*(z1-z2));
    }

    private static Raycast getRay(int x, int y){
        //return new Raycast(position.copy().add(AngleHandler.getRotated(new Vector3D(x, y, 0), forward)), forward);
        return new Raycast(position.copy(), AngleHandler.mult(forward, new Quaternion(10, ((double)y / (screenHeight/resolution)) * aspectRatio, ((double)x / (screenWidth/resolution)), 0)));
    }   

    private static int[] cast(Raycast ray){
        for(Polygon p: polys){
            if(p.isRayIntersecting(ray)) return p.color;
        }
        return new int[3];
    }
}