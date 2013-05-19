
package gov.loc.repository.bagger.ui.handlers;


import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.GetServiceDocumentFrame;

import org.springframework.richclient.command.support.AbstractActionCommandExecutor;


public class SendSIPExecutor extends AbstractActionCommandExecutor {
   	private static final long serialVersionUID = 1L;
	private GetServiceDocumentFrame sdFrame;
	BagView bagView;
	DefaultBag bag;

	public SendSIPExecutor(BagView bagView) {
		super();
		setEnabled(true);
		this.bagView = bagView;
	}

	public void execute() {
		bag = bagView.getBag();
		
		sdFrame = new GetServiceDocumentFrame(bagView, bagView.getPropertyMessage("bag.frame.sd"));
		sdFrame.setVisible(true);
		
	}
	

}
