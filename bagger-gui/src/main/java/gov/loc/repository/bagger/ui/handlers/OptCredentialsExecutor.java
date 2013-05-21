
package gov.loc.repository.bagger.ui.handlers;


import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.GetServiceDocumentFrame;
import gov.loc.repository.bagger.ui.OptCredentialsFrame;
import gov.loc.repository.bagger.ui.OptUrlFrame;

import org.springframework.richclient.command.support.AbstractActionCommandExecutor;


public class OptCredentialsExecutor extends AbstractActionCommandExecutor {
   	private static final long serialVersionUID = 1L;
	private OptCredentialsFrame frame;
	BagView bagView;
	DefaultBag bag;

	public OptCredentialsExecutor(BagView bagView) {
		super();
		setEnabled(true);
		this.bagView = bagView;
	}

	public void execute() {
		bag = bagView.getBag();
		
		frame = new OptCredentialsFrame(bagView, "Credentials");
		frame.setVisible(true);
		
	}
	

}
