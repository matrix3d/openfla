import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lizhi http://matrix3d.github.io/
 */
public class Sprite{
	public Float scaleX=1f;
	public Float scaleY=1f;
	public Float rotation=0f;
	public Float x=0f;
	public Float y=0f;
	public List<Sprite> children=new ArrayList<Sprite>();
	public AffineTransform transform=new AffineTransform();
	public Sprite parent;
	public Sprite add(Sprite s){
		children.add(s);
		s.parent=this;
		return s;
	}

	public void doTransorm(Graphics2D g){
		//transform=new AffineTransform();
		//transform.scale(scaleX,scaleY);
		//transform.rotate(rotation);
		//transform.translate(x,y);
		if(parent!=null){
			transform.concatenate(parent.transform);
		}
		g.setTransform(transform);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	}
	public void render(Graphics2D g,World world){
		doTransorm(g);
		renderChild(g,world);
	}

	public void renderChild(Graphics2D g,World world){
		for(Sprite s:children){
			s.render(g,world);
		}
	}
}
