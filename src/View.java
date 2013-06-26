
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: lizhi http://matrix3d.github.io/
 */
public class View extends JFrame {
	private JTree tree;
	public View() {
		super("openfla");
		JButton btn=new JButton("west");
		add(btn, BorderLayout.WEST);
		btn=new JButton("north");
		add(btn,BorderLayout.NORTH);
		btn=new JButton("south");
		add(btn,BorderLayout.SOUTH);
		btn=new JButton("east");
		add(btn,BorderLayout.EAST);

		JSplitPane splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		tree = new JTree();
		JScrollPane jp=new JScrollPane(tree);
		splitPane.add(jp,JSplitPane.LEFT);
		splitPane.add(new JButton(),JSplitPane.RIGHT);

		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE,new DropTargetAdapter() {
			@Override
			public void drop(DropTargetDropEvent dtde) {
				try{
					if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
						dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
						java.util.List<File> list = (java.util.List<File>)(dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor));
						dofile(list);
						dtde.dropComplete(true);
					}else{
						dtde.rejectDrop();
					}
				}catch (Exception e){

				}
			}
		});
	}

	private void openXFLFile(File file){
		try{
			File dom = new File(file.getParent()+File.separator+"DOMDocument.xml");
			if(dom.exists()){
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(dom);
				Container parent = tree.getParent();
				parent.remove(tree);
				tree=new JTree(doXml(doc));
				parent.add(tree);
			}

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private DefaultMutableTreeNode doXml(Node node){
		NodeList list = node.getChildNodes();
		DefaultMutableTreeNode tnode=new DefaultMutableTreeNode(new XMLTreeNode(node));
		for(int i=0;i<list.getLength();i++){
			Node cnode = list.item(i);
			if(cnode.getNodeType()==Node.TEXT_NODE){
				continue;
			}
			tnode.add(doXml(cnode));
		}
		return tnode;
	}

	private void dofile(java.util.List<File> list){
		String temp="";
		for (File file:list){
			temp+=file.getAbsolutePath()+";\n";
			if(file.getName().endsWith(".xfl")){
				//JOptionPane.showMessageDialog(null,file.getName());
				openXFLFile(file);
				return;
			}
		}
		JOptionPane.showMessageDialog(null,temp);
	}
}
