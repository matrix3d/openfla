
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lizhi http://matrix3d.github.io/
 */
public class View extends JFrame {
	private JTree tree;
	private World sprite;
	private File currentFile;
	private Map<String,Node> symbols;
	private Map<String,XMLImage> bitmaps;
	public View() {
		super("openfla");
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("menu");
		JMenuItem item = new JMenuItem("item");
		setJMenuBar(menuBar);
		menuBar.add(menu);
		menu.add(item);

		UIManager.LookAndFeelInfo[] lookAndFeels= UIManager.getInstalledLookAndFeels();
		menu=new JMenu("lookAndFeels");
		menuBar.add(menu);
		for (int i=0;i<lookAndFeels.length;i++){
			item = new JMenuItem(lookAndFeels[i].getClassName());
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JMenuItem item1 = (JMenuItem) e.getSource();
					try {
						UIManager.setLookAndFeel(item1.getText());
						SwingUtilities.updateComponentTreeUI(getContentPane());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
			menu.add(item);
		}

		JToolBar toolBar = new JToolBar();
		toolBar.setEnabled(false);
		JButton btn=new JButton("btn");

		toolBar.add(btn);
		add(toolBar,BorderLayout.PAGE_START);

		JSplitPane splitPane = new JSplitPane();
		add(splitPane, BorderLayout.CENTER);
		tree = new JTree();
		JScrollPane jp=new JScrollPane(tree);
		splitPane.add(jp,JSplitPane.LEFT);
		sprite=new World();
		jp=new JScrollPane(sprite);
		splitPane.add(jp,JSplitPane.RIGHT);

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

	private String getHrefUrl(Node node){
		String url=node.getAttributes().getNamedItem("href").getNodeValue();
		url=url.replace("/",File.separator);
		url=currentFile.getParent()+File.separator+"LIBRARY"+File.separator+url;
		return url;
	}

	private BufferedImage getImage(String name){
		XMLImage xi=bitmaps.get(name);
		if(xi==null){
			return  null;
		}
		if(xi.image!=null){
			return xi.image;
		}
		try {
			File file=new File(getHrefUrl(xi.node));
			xi.image=ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return xi.image;
	}

	private void show(XMLTreeNode treeNode){
		Node node =treeNode.node;
		if(node!=null&&node.getNodeName()=="DOMBitmapItem"){
				sprite.root.children.clear();
				Bitmap bmp=new Bitmap();
				bmp.image=getImage(node.getAttributes().getNamedItem("name").getNodeValue());

				sprite.root.add(bmp);
				sprite.repaint();
		}else if(node!=null&&node.getNodeName()=="Include"){
			sprite.root.children.clear();
			sprite.root.add(getMC(treeNode.symbolName));
			sprite.repaint();
		}
	}

	private Sprite getMC(String name){
		Sprite sprite1 = new Sprite();
		Node node = symbols.get(name);
		if(node!=null){
			Document doc=(Document)node;
			NodeList layers= doc.getElementsByTagName("DOMLayer");
			for (int i=0;i<layers.getLength();i++){
				Node layer =layers.item(i);
				NodeList frames = layer.getChildNodes();
				for(int j=0;j<frames.getLength();j++){
					Node frame = frames.item(j);
					if(frame.getNodeName()=="frames"){
						NodeList DOMFrames=frame.getChildNodes();
						for(int k=0;k<DOMFrames.getLength();k++){
							Node DOMFrame=DOMFrames.item(k);
							if(DOMFrame.getNodeName()=="DOMFrame"){
								NodeList elements=DOMFrame.getChildNodes();
								for(int a=0;a<elements.getLength();a++){
									Node element=elements.item(a);
									if(element.getNodeName()=="elements"){
										NodeList domInstances=element.getChildNodes();
										for(int b=0;b<domInstances.getLength();b++){
											Node instance=domInstances.item(b);
											if(instance.getNodeName()=="DOMBitmapInstance"){
												Bitmap bitmap=new Bitmap();
												bitmap.image=getImage(instance.getAttributes().getNamedItem("libraryItemName").getNodeValue());
												sprite1.add(bitmap);
											}else if(instance.getNodeName()=="DOMSymbolInstance"){
												sprite1.add(getMC(instance.getAttributes().getNamedItem("libraryItemName").getNodeValue()));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return  sprite1;
	}

	private Document getDoc(File file){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			return doc;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private void openXFLFile(File file){

			File dom = new File(file.getParent()+File.separator+"DOMDocument.xml");
			currentFile=file;
			if(dom.exists()){
				Document doc=getDoc(dom);
				symbols=new HashMap<String, Node>();
				bitmaps =new HashMap<String, XMLImage>();
				Container parent = tree.getParent();
				parent.remove(tree);
				tree=new JTree(doXml(doc));
				tree.addTreeSelectionListener(new TreeSelectionListener() {
					@Override
					public void valueChanged(TreeSelectionEvent e) {
						DefaultMutableTreeNode treeNode =(DefaultMutableTreeNode)(e.getPath().getLastPathComponent());
						XMLTreeNode xmlTreeNode=(XMLTreeNode)treeNode.getUserObject();
						show(xmlTreeNode);
					}
				});
				parent.add(tree);
			}
	}

	private DefaultMutableTreeNode doXml(Node node){
		NodeList list = node.getChildNodes();
		XMLTreeNode xt=new XMLTreeNode(node);
		DefaultMutableTreeNode tnode=new DefaultMutableTreeNode(xt);
		if(node.getNodeName()=="Include"){
			File file =new File(getHrefUrl(node));
			Document doc=getDoc(file);
			XPathFactory factoryXpah = XPathFactory.newInstance();
			XPath xpath = factoryXpah.newXPath();
			try{
				XPathExpression snameXP = xpath.compile("DOMSymbolItem/@name");
				String sname=snameXP.evaluate(doc);
				xt.symbolName=sname;
				symbols.put(sname,doc);
			}catch (Exception e){

			}
		}else if(node.getNodeName()=="DOMBitmapItem"){
			XMLImage xmlImage=new XMLImage();
			xmlImage.node=node;
			bitmaps.put(node.getAttributes().getNamedItem("name").getNodeValue(),xmlImage);
		}
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
