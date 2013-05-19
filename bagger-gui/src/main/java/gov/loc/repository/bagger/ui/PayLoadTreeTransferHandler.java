package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.ui.handlers.AddDataHandler;
import gov.loc.repository.bagger.ui.handlers.BagTreeTransferHandler;
import gov.loc.repository.bagger.ui.handlers.BagTreeTransferHandler.NodesTransferable;
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
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.progress.BusyIndicator;

public class PayLoadTreeTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(BagTreeTransferHandler.class);
	private static DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String", null);
    private static boolean debugImport = true;
	DataFlavor nodesFlavor;
	DataFlavor[] flavors = new DataFlavor[1];
    private boolean isPayload;
	private DefaultMutableTreeNode[] nodesToRemove;
	BagView bagView;

    public PayLoadTreeTransferHandler(boolean isPayload, BagView bagView) {
    	super();
    	this.isPayload = isPayload;
		this.bagView = bagView;
		try {
			String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" +	javax.swing.tree.DefaultMutableTreeNode[].class.getName() +	"\"";
			nodesFlavor = new DataFlavor(mimeType);
			flavors[0] = nodesFlavor;
		} catch(ClassNotFoundException e) {
			log.error("ClassNotFound: " + e.getMessage());
		}
    }

    private void display(String s) {
    	String msg = "BagTreeTransferHandler." + s;
    	log.info(msg);
    }
    
    public boolean canImport(JComponent comp, DataFlavor transferFlavors[]) {
    	for (int i = 0; i < transferFlavors.length; i++) {
    		Class representationclass = transferFlavors[i].getRepresentationClass();
    		
    		// URL from Explorer or Firefox, KDE
    		if ((representationclass != null) && URL.class.isAssignableFrom(representationclass)) {
    			return true;
    		}
    		// Drop from Windows Explorer
    		if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])) {
    			return true;
    		}
    		// Drop from GNOME
    		if (DataFlavor.stringFlavor.equals(transferFlavors[i])) {
    			return true;
    		}
    		if (uriListFlavor.equals(transferFlavors[i])) {
    			return true;
    		}
    	}
    	return false;
    }

	

    public void addBagData(File file, boolean lastFileFlag) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
        try {
        	bagView.getBag().addFileToPayload(file);
        	boolean alreadyExists = bagView.bagPayloadTree.addNodes(file, false);
        	if (alreadyExists) {
        		bagView.showWarningErrorDialog("Warning - file already exists", "File: " + file.getName() + "\n" + "already exists in bag.");
        	}
			bagView.updateAddData();
        } catch (Exception e) {
        	log.error("BagView.addBagData: " + e);
        	bagView.showWarningErrorDialog("Error - file not added", "Error adding bag file: " + file + "\ndue to:\n" + e.getMessage());
        }
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }
    
	public boolean importData(JComponent comp, Transferable t) { 
		if (!t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			return false;
		}

		List data;
		try {
			data = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
			Iterator i = data.iterator();
			while (i.hasNext()) {
				File file = (File)i.next();
				addBagData(file, true);
			}

		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		return true;
        
	}

}
