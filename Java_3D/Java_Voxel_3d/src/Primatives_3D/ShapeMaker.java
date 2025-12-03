package Primatives_3D;

import java.util.ArrayList;

public class ShapeMaker {
    public static Polygon[] getFace(int x, int y, int z, int x2, int y2, int z2, int x3, int y3, int z3, int x4, int y4, int z4){
        Polygon[] polys = new Polygon[2];
        polys[0] = new Polygon(new Vector3D(x, y, z), new Vector3D(x2, y2, z2), new Vector3D(x3, y3, z3));
        polys[1] = new Polygon(new Vector3D(x, y, z), new Vector3D(x3, y3, z3), new Vector3D(x4, y4, z4));
        return polys;
    }

    public static Polygon[] getFace(Vector3D v1, Vector3D v2, Vector3D v3, Vector3D v4){
        Polygon[] polys = new Polygon[2];
        polys[0] = new Polygon(v1, v2, v3);
        polys[1] = new Polygon(v1, v3, v4);
        return polys;
    }

    public static Polygon[] getCube(int x, int y, int z, int w, int h, int l){
        Polygon[] polys = new Polygon[12];
        setArrayRange(0, 1, polys, getFace(x, y, z, x + w, y, z, x + w, y + h, z, x, y + h, z)); // front
        setArrayRange(2, 3, polys, getFace(x, y, z + l, x + w, y, z + l, x + w, y + h, z + l, x, y + h, z + l)); // back
        setArrayRange(4, 5, polys, getFace(x, y, z, x, y, z + l, x, y + h, z + l, x, y + h, z)); // left
        setArrayRange(6, 7, polys, getFace(x + w, y, z, x + w, y, z + l, x + w, y + h, z + l, x + w, y + h, z)); // right
        setArrayRange(8, 9, polys, getFace(x, y, z, x + w, y, z, x + w, y, z + l, x, y, z + l)); // top
        setArrayRange(10, 11, polys, getFace(x, y + h, z, x + w, y + h, z, x + w, y + h, z + l, x, y + h, z + l)); // bottom
        return polys;
    }

    public static ArrayList<Polygon> addToArrayList(Polygon[] arr, ArrayList<Polygon> list){
        for(Polygon p : arr){
            list.add(p);
        }
        return list;
    }

    private static Polygon[] setArrayRange(int start, int end, Polygon[] setArray, Polygon[] recieveArray){
        for(int i = 0; i < (end+1) - start; i++){
            setArray[start+i] = recieveArray[i];
        }
        return setArray;
    } 
}
