
package gov.loc.repository.bagger.ui.handlers;


import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.GetServiceDocumentFrame;

import org.springframework.richclient.command.support.AbstractActionCommandExecutor;

import uk.ac.liv.alloy.SwordClient;

public class GetServiceDocumentExecutor extends AbstractActionCommandExecutor {
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public GetServiceDocumentExecutor(BagView bagView) {
		super();
		setEnabled(true);
		this.bagView = bagView;
	}

	public void execute() {
		System.out.println(SwordClient.getServiceDocument());	
		
	}
	

}
