
package gov.loc.repository.bagger.ui.handlers;


import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.GetServiceDocumentFrame;
import gov.loc.repository.bagger.ui.OptUrlFrame;

import org.springframework.richclient.command.support.AbstractActionCommandExecutor;


public class OptUrlExecutor extends AbstractActionCommandExecutor {
   	private static final long serialVersionUID = 1L;
	private OptUrlFrame frame;
	BagView bagView;
	DefaultBag bag;

	public OptUrlExecutor(BagView bagView) {
		super();
		setEnabled(true);
		this.bagView = bagView;
	}

	public void execute() {
		bag = bagView.getBag();
		
		frame = new OptUrlFrame(bagView, bagView.getPropertyMessage("bag.frame.url"));
		frame.setVisible(true);
		
	}
	

}
