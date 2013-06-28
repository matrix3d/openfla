import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: lizhi http://matrix3d.github.io/
 */
public class XMLTreeNode {
	public Node node;
	public String symbolName;
	public XMLTreeNode(Node node){
		this.node=node;
	}
	public String toString(){
		String nodeName=node.getNodeName();
		if(nodeName=="DOMBitmapItem"){
			return node.getAttributes().getNamedItem("name").getNodeValue();
		}else if(nodeName=="Include"){
			return node.getAttributes().getNamedItem("href").getNodeValue();
		}
		return nodeName;
	}
}
