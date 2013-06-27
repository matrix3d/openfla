
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
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: lizhi http://matrix3d.github.io/
 */
public class View extends JFrame {
	private JTree tree;
	private World sprite;
	private File currentFile;
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

	private void show(Node node){
		if(node!=null&&node.getNodeName()=="DOMBitmapItem"){
			String url=node.getAttributes().getNamedItem("sourceExternalFilepath").getNodeValue();
			url=url.substring(2);
			url=url.replace("/",File.separator);
			url=currentFile.getParent()+File.separator+url;
			File file=new File(url);
			if(file.exists()){
				sprite.root.children.clear();
				Bitmap bmp=new Bitmap();
				try {
					bmp.image=ImageIO.read(file);
				} catch (IOException e) {
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
				sprite.root.add(bmp);
				sprite.repaint();
			}
		}

	}

	private void openXFLFile(File file){
		try{
			File dom = new File(file.getParent()+File.separator+"DOMDocument.xml");
			currentFile=file;
			if(dom.exists()){
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(dom);
				Container parent = tree.getParent();
				parent.remove(tree);
				tree=new JTree(doXml(doc));
				tree.addTreeSelectionListener(new TreeSelectionListener() {
					@Override
					public void valueChanged(TreeSelectionEvent e) {
						DefaultMutableTreeNode treeNode =(DefaultMutableTreeNode)(e.getPath().getLastPathComponent());
						XMLTreeNode xmlTreeNode=(XMLTreeNode)treeNode.getUserObject();
						show(xmlTreeNode.node);
					}
				});
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
