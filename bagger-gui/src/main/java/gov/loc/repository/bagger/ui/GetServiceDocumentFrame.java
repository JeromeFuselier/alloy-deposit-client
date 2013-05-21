/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.util.ApplicationContextUtil;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.writer.Writer;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;
import gov.loc.repository.bagit.writer.impl.ZipWriter;
import gov.loc.repository.bagger.ui.Progress;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.dialog.TitlePane;
import org.springframework.richclient.util.GuiStandardUtils;

import uk.ac.liv.alloy.SwordClient;

public class GetServiceDocumentFrame extends JFrame implements ActionListener {
	private static final Log log = LogFactory.getLog(GetServiceDocumentFrame.class);
	private static final long serialVersionUID = 1L;
	BagView bagView;
	File bagFile;
	String bagFileName = "";
	JPanel savePanel;
	JPanel serializeGroupPanel;
	JTextField bagNameField;
	JLabel urlLabel;
	JTextField urlField;
	JButton browseButton;
	JButton okButton;
	JButton cancelButton;
	JRadioButton noneButton;
	JRadioButton zipButton;
	JRadioButton tarButton;
	JRadioButton tarGzButton;
	JRadioButton tarBz2Button;

	JCheckBox holeyCheckbox;
	JCheckBox isTagCheckbox;
	JCheckBox isPayloadCheckbox;
    JComboBox collectionList;
	private String messages;
    
    ArrayList<String> documents = null;

	public GetServiceDocumentFrame(BagView bagView, String title) {
        super(title);
		documents = SwordClient.getServiceDocument();
		this.bagView = bagView;
		if (bagView != null) {
	        getContentPane().removeAll();
	        savePanel = createComponents();
		} else {
			savePanel = new JPanel();
		}
		
        getContentPane().add(savePanel, BorderLayout.CENTER);
        //setPreferredSize(preferredDimension);
        this.setBounds(300,200, 600, 200);
        pack();
    }
	
	protected JComponent createButtonBar() {
		CommandGroup dialogCommandGroup = CommandGroup.createCommandGroup(null, getCommandGroupMembers());
		JComponent buttonBar = dialogCommandGroup.createButtonBar();
		GuiStandardUtils.attachDialogBorder(buttonBar);
		return buttonBar;
	}
	
	
	protected Object[] getCommandGroupMembers() {
		return new AbstractCommand[] { finishCommand, cancelCommand };
	}
	
	
    /**
	 * Initialize the standard commands needed on a Dialog: Ok/Cancel.
	 */
	private void initStandardCommands() {
		finishCommand = new ActionCommand(getFinishCommandId()) {
			public void doExecuteCommand() {				
				new OkHandler().actionPerformed(null);
			}
		};

		cancelCommand = new ActionCommand(getCancelCommandId()) {
			public void doExecuteCommand() {
				new CancelHandler().actionPerformed(null);
			}
		};
	}
	
	
	
	protected String getFinishCommandId() {
		return DEFAULT_FINISH_COMMAND_ID;
	}
	
	protected String getCancelCommandId() {
		return DEFAULT_CANCEL_COMMAND_ID;
	}
	
	protected static final String DEFAULT_FINISH_COMMAND_ID = "okCommand";

	protected static final String DEFAULT_CANCEL_COMMAND_ID = "cancelCommand";
	
	private ActionCommand finishCommand;

	private ActionCommand cancelCommand;
	
	

    private JPanel createComponents() {
        
        TitlePane titlePane = new TitlePane();
        initStandardCommands();
        JPanel pageControl = new JPanel(new BorderLayout());
		JPanel titlePaneContainer = new JPanel(new BorderLayout());
		titlePane.setTitle(bagView.getPropertyMessage("SendSIPFrame.title"));
		titlePane.setMessage( new DefaultMessage(bagView.getPropertyMessage("SendSIPFrame.description")));
		titlePaneContainer.add(titlePane.getControl());
		titlePaneContainer.add(new JSeparator(), BorderLayout.SOUTH);
		pageControl.add(titlePaneContainer, BorderLayout.NORTH);
		JPanel contentPane = new JPanel();


		JLabel collectionListLabel = new JLabel("Collection ID");
		
		collectionList = new JComboBox(documents.toArray());
		collectionList.setName("Collection ID");
		//collectionList.setSelectedItem(Version.V0_96.versionString);
		collectionList.setToolTipText("Colllection ID on the repository");
		
    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();
        JPanel panel = new JPanel(layout);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        int row = 0;
        
        JLabel spacerLabel = new JLabel();
        
        buildConstraints(glbc, 0, row, 1, 1, 5, 50, GridBagConstraints.NONE, GridBagConstraints.WEST); 
        layout.setConstraints(collectionListLabel, glbc);
        panel.add(collectionListLabel);
        buildConstraints(glbc, 1, row, 1, 1, 40, 50, GridBagConstraints.NONE, GridBagConstraints.WEST); 
        layout.setConstraints(spacerLabel, glbc);
        panel.add(spacerLabel);
        buildConstraints(glbc, 2, row, 1, 1, 40, 50, GridBagConstraints.NONE, GridBagConstraints.WEST); 
        layout.setConstraints(collectionList, glbc);
        panel.add(collectionList);
        
    	
    	GuiStandardUtils.attachDialogBorder(contentPane);
		pageControl.add(panel);
		JComponent buttonBar = createButtonBar();
		pageControl.add(buttonBar,BorderLayout.SOUTH);
	
		this.pack();
		return pageControl;
    	
 
    }
    

    public void actionPerformed(ActionEvent e) {
    	invalidate();
    	repaint();
    }



    private class OkHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			String coll_id = (String)collectionList.getSelectedItem();
			boolean temp_file = false;
			File tmpRootPath = null;
			
			// Create a temporary file
			if (bagView.getBagRootPath() == null) {
				temp_file = true;
				try {
					tmpRootPath = File.createTempFile("temp-file-name", ".zip");
	
					bagView.setBagRootPath(tmpRootPath);
		        	
		        	DefaultBag bag = bagView.getBag();
		            bag.setRootDir(tmpRootPath);
		                		
		    		Writer bagWriter = null;      
					BagFactory bagFactory = new BagFactory();
					bagWriter = new ZipWriter(bagFactory);
					bag.write(bagWriter);

				} catch (IOException e1) {
					e1.printStackTrace();
				} 
				
	            
	            
			} 

			String result = SwordClient.sendBag(coll_id, bagView.getBagRootPath());				
			ApplicationContextUtil.addConsoleMessage(result);
			
			if (temp_file) {
				if (tmpRootPath != null)
					tmpRootPath.delete();
				bagView.setBagRootPath(null);
			}
			

			setVisible(false);
			
        }
    }

    private class CancelHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			setVisible(false);
        }
    }

    
    private void buildConstraints(GridBagConstraints gbc,int x, int y, int w, int h, int wx, int wy, int fill, int anchor) {
    	gbc.gridx = x; // start cell in a row
    	gbc.gridy = y; // start cell in a column
    	gbc.gridwidth = w; // how many column does the control occupy in the row
    	gbc.gridheight = h; // how many column does the control occupy in the column
    	gbc.weightx = wx; // relative horizontal size
    	gbc.weighty = wy; // relative vertical size
    	gbc.fill = fill; // the way how the control fills cells
    	gbc.anchor = anchor; // alignment
    }
    
    private String getMessage(String property) {
    	return bagView.getPropertyMessage(property);
    }


}