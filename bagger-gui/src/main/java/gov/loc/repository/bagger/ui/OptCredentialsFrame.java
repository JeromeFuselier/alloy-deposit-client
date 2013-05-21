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
import javax.swing.JPasswordField;
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

public class OptCredentialsFrame extends JFrame implements ActionListener {
	private static final Log log = LogFactory.getLog(OptCredentialsFrame.class);
	private static final long serialVersionUID = 1L;
	BagView bagView;
	
	JPanel panel;
	
	JPasswordField pwdField;
	JLabel pwdLabel;
	JTextField usernameField;
	JLabel usernameLabel;
	JTextField oboField;
	JLabel oboLabel;

	private String messages;
    

	public OptCredentialsFrame(BagView bagView, String title) {
        super(title);
		this.bagView = bagView;
		if (bagView != null) {
	        getContentPane().removeAll();
	        panel = createComponents();
		} else {
			panel = new JPanel();
		}
		
        getContentPane().add(panel, BorderLayout.CENTER);
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
		titlePane.setTitle("Options");
		titlePane.setMessage( new DefaultMessage("Modify credentials"));
		titlePaneContainer.add(titlePane.getControl());
		titlePaneContainer.add(new JSeparator(), BorderLayout.SOUTH);
		pageControl.add(titlePaneContainer, BorderLayout.NORTH);
		JPanel contentPane = new JPanel();


		usernameLabel = new JLabel("Username");
		usernameField = new JTextField(SwordClient.username);
		usernameField.setHorizontalAlignment(JTextField.RIGHT);
		
		pwdLabel = new JLabel("Password");
		pwdField = new JPasswordField(SwordClient.password);
		pwdField.setHorizontalAlignment(JTextField.RIGHT);
		
		oboLabel = new JLabel("On Behalf Of");
		oboField = new JTextField(SwordClient.obo);
		oboField.setHorizontalAlignment(JTextField.RIGHT);
		
		
    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();
        JPanel panel = new JPanel(layout);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        int row = 0;
        
        JLabel spacerLabel = new JLabel();
        
        buildConstraints(glbc, 0, row, 1, 1, 5, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST); 
        layout.setConstraints(usernameLabel, glbc);
        panel.add(usernameLabel);
        buildConstraints(glbc, 1, row, 1, 1, 40, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER); 
        layout.setConstraints(spacerLabel, glbc);
        panel.add(spacerLabel);
        buildConstraints(glbc, 2, row, 1, 1, 400, 100, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST); 
        layout.setConstraints(usernameField, glbc);
        panel.add(usernameField);
        
        row++;        
        buildConstraints(glbc, 0, row, 1, 1, 5, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST); 
        layout.setConstraints(pwdLabel, glbc);
        panel.add(pwdLabel);
        buildConstraints(glbc, 1, row, 1, 1, 40, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER); 
        layout.setConstraints(spacerLabel, glbc);
        panel.add(spacerLabel);
        buildConstraints(glbc, 2, row, 1, 1, 400, 10, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST); 
        layout.setConstraints(pwdField, glbc);
        panel.add(pwdField);
        
        row++;        
        buildConstraints(glbc, 0, row, 1, 1, 5, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST); 
        layout.setConstraints(oboLabel, glbc);
        panel.add(oboLabel);
        buildConstraints(glbc, 1, row, 1, 1, 40, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER); 
        layout.setConstraints(spacerLabel, glbc);
        panel.add(spacerLabel);
        buildConstraints(glbc, 2, row, 1, 1, 400, 10, GridBagConstraints.HORIZONTAL, GridBagConstraints.EAST); 
        layout.setConstraints(oboField, glbc);
        panel.add(oboField);
        
                
    	
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
			SwordClient.obo = oboField.getText();
			SwordClient.username = usernameField.getText();
			SwordClient.password = pwdField.getText();

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