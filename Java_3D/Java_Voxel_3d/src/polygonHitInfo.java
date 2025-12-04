import Primatives_3D.Vector3D;

public class polygonHitInfo{
    public boolean didHit = false;
    public double u = 0;
    public double v = 0;
    public double distance = 0;
    public Vector3D normal = new Vector3D(0, 0, 0);
    public boolean isLine = false;
    public polygonHitInfo(boolean didHit, double u, double v, double distance, Vector3D normal, boolean isLine){
        this.didHit = didHit;
        this.u = u;
        this.v = v;
        this.distance = distance;
        this.normal = normal;
        this.isLine = isLine;
    }

    public polygonHitInfo(boolean didHit){
        this.didHit = didHit;
    }
}
