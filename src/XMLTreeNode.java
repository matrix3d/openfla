import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: lizhi http://matrix3d.github.io/
 */
public class XMLTreeNode {
	public Node node;
	public XMLTreeNode(Node node){
		this.node=node;
	}
	public String toString(){
		return node.getNodeName();
	}
}
