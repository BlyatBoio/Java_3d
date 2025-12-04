import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class PixelPainter extends Component{
    private BufferedImage image;
    private WritableRaster raster;
    private int[] colors;

    public PixelPainter(int screenWidth, int screenHeight){
        image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
        raster = image.getRaster();
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(image, 0, 0, null);        
    }
    public void setPixel(int x, int y, int R, int G, int B){
        raster.setPixel(x, y, new int[]{R, G, B});
    }
    public void setPixel(int x, int y, int[] color){
        raster.setPixel(x, y, color);
    }
    public void setPixelGroup(int x, int y, int w, int h, int R, int G, int B){
        colors = new int[w*h*3];
        for(int i = 0; i < w*h*3; i+=3){
            colors[i] = R;
            colors[i+1] = G;
            colors[i+2] = B;
        }
        raster.setPixels(x, y, w, h, colors);
    }
    public void setPixelGroup(int x, int y, int w, int h, int[] color){
        colors = new int[w*h*3];
        for(int i = 0; i < w*h*3; i+=3){
            colors[i] = color[0];
            colors[i+1] = color[1];
            colors[i+2] = color[2];
        }
        raster.setPixels(x, y, w, h, colors);
    }
}