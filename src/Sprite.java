import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lizhi http://matrix3d.github.io/
 */
public class Sprite{
	public Float x=0f;
	public Float y=0f;
	public List<Sprite> children=new ArrayList<Sprite>();
	public Sprite add(Sprite s){
		children.add(s);
		return s;
	}
	public void render(Graphics2D g,World world){
		for(Sprite s:children){
			s.render(g,world);
		}
	}
}
