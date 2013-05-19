package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.util.ApplicationContextUtil;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.progress.BusyIndicator;

public class BagTreeTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(BagTreeTransferHandler.class);
	private static DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String", null);
    private static boolean debugImport = false;
	DataFlavor nodesFlavor;
	DataFlavor[] flavors = new DataFlavor[1];
    private boolean isPayload;
	private DefaultMutableTreeNode[] nodesToRemove;
	BagView bagView;

    public BagTreeTransferHandler(boolean isPayload, BagView bagView) {
    	super();
    	this.isPayload = isPayload;
		this.bagView = bagView;
		try {
			String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" +	javax.swing.tree.DefaultMutableTreeNode[].class.getName() +	"\"";
			nodesFlavor = new DataFlavor(mimeType);
			flavors[0] = nodesFlavor;
		} catch(ClassNotFoundException e) {
			//log.error("ClassNotFound: " + e.getMessage());
			e.printStackTrace();
		}
    }

    private void display(String s) {
    	String msg = "BagTreeTransferHandler." + s;
    	//log.info(msg);
    }

    public int getSourceActions(JComponent c) {
    	return TransferHandler.COPY;
    }

    public boolean canImport(JComponent comp, DataFlavor transferFlavors[]) {
    	for (int i = 0; i < transferFlavors.length; i++) {
    		Class representationclass = transferFlavors[i].getRepresentationClass();
    		// URL from Explorer or Firefox, KDE
    		if ((representationclass != null) && URL.class.isAssignableFrom(representationclass)) {
    			if (debugImport) {
    				display("canImport accepted " + transferFlavors[i]);
    			}
    			return true;
    		}
    		// Drop from Windows Explorer
    		if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])) {
    			if (debugImport) {
    				display("canImport accepted " + transferFlavors[i]);
    			}
    			return true;
    		}
    		// Drop from GNOME
    		if (DataFlavor.stringFlavor.equals(transferFlavors[i])) {
    			if (debugImport) {
    				display("canImport accepted " + transferFlavors[i]);
    			}
    			return true;
    		}
    		if (uriListFlavor.equals(transferFlavors[i])) {
    			if (debugImport) {
    				display("canImport accepted " + transferFlavors[i]);
    			}
    			return true;
    		}
    		if (debugImport) {
    			log.error("canImport " + i + " unknown import " + transferFlavors[i]);
    		}
    	}
    	if (debugImport) {
    		log.error("canImport rejected");
    	}
    	return false;
    }

	protected Transferable createTransferable(JComponent c) {
		JTree tree = (JTree)c;
		TreePath[] paths = tree.getSelectionPaths();
		if(paths != null) {
			// Make up a node array of copies for transfer and
			// another for/of the nodes that will be removed in
			// exportDone after a successful drop.
			List<DefaultMutableTreeNode> copies = new ArrayList<DefaultMutableTreeNode>();
			List<DefaultMutableTreeNode> toRemove =	new ArrayList<DefaultMutableTreeNode>();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[0].getLastPathComponent();
			DefaultMutableTreeNode copy = copy(node);
			copies.add(copy);
			toRemove.add(node);
			for(int i = 1; i < paths.length; i++) {
				DefaultMutableTreeNode next = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
				// Do not allow higher level nodes to be added to list.
				if(next.getLevel() < node.getLevel()) {
					break;
				} else if(next.getLevel() > node.getLevel()) {  // child node
					copy.add(copy(next));
					// node already contains child
				} else {                                        // sibling
					copies.add(copy(next));
					toRemove.add(next);
				}
			}
			DefaultMutableTreeNode[] nodes = copies.toArray(new DefaultMutableTreeNode[copies.size()]);
			nodesToRemove =	toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
			return new NodesTransferable(nodes);
		}
		return null;
	}

	/** Defensive copy used in createTransferable. */
	private DefaultMutableTreeNode copy(TreeNode node) {
		return new DefaultMutableTreeNode(node);
	}

	protected void exportDone(JComponent source, Transferable data, int action) {
//		if((action & MOVE) == MOVE) {
			JTree tree = (JTree)source;
			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			// Remove nodes saved in nodesToRemove in createTransferable.
			for(int i = 0; i < nodesToRemove.length; i++) {
				display("exportDonevnodesToRemove: " + nodesToRemove[i] );
				model.removeNodeFromParent(nodesToRemove[i]);
			}
			if (this.isPayload) {
				//bagView.removeDataHandler.removeData();
			} else {
				//bagView.removeTagFileHandler.removeTagFile();
			}
//		}
	}

    public static String getCanonicalFileURL(File file) {
    	String path = file.getAbsoluteFile().getPath();
    	if (File.separatorChar != '/') {
    		path = path.replace(File.separatorChar, '/');
    	}
    	// Not network path
    	if (!path.startsWith("//")) {
    		if (path.startsWith("/")) {
    			path = "//" + path;
    		} else {
    			path = "///" + path;
    		}
    	}
    	return "file:" + path;
    }

	public class NodesTransferable implements Transferable {
		DefaultMutableTreeNode[] nodes;

		public NodesTransferable(DefaultMutableTreeNode[] nodes) {
			this.nodes = nodes;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if(!isDataFlavorSupported(flavor))
				throw new UnsupportedFlavorException(flavor);
			return nodes;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return nodesFlavor.equals(flavor);
		}
	}
	

    public void addBagData(File file, boolean lastFileFlag) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
        try {
        	bagView.getBag().addFileToPayload(file);
        	boolean alreadyExists = bagView.bagPayloadTree.addNodes(file, false);
        	if (alreadyExists) {
        		bagView.showWarningErrorDialog("Warning - file already exists", "File: " + file.getName() + "\n" + "already exists in bag.");
        	}
        } catch (Exception e) {
        	//log.error("BagView.addBagData: " + e);
        	bagView.showWarningErrorDialog("Error - file not added", "Error adding bag file: " + file + "\ndue to:\n" + e.getMessage());
        }
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }
    
	public boolean importData(TransferSupport support) {    
        if (!canImport(support)) {
            return false;
        }

        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();

        TreePath path = dl.getPath();
        int childIndex = dl.getChildIndex();

        String data;
        try
        {
            data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            
            File file = new File(new URI(data));
            String message = ApplicationContextUtil.getMessage("bag.message.filesadded");

            addBagData(file, true);
        	ApplicationContextUtil.addConsoleMessage(message + " " + file.getAbsolutePath());
        	bagView.bagPayloadTreePanel.refresh(bagView.bagPayloadTree);
			bagView.updateAddData();
			
			
        }
        catch (UnsupportedFlavorException e)
        {
            return false;                   
        }
        catch (IOException e)
        {
            return false;                   
        } catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return true;
        
	}
}
