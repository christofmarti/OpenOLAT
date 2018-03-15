package org.olat.user.ui.organisation;

import org.olat.basesecurity.Organisation;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.segmentedview.SegmentViewComponent;
import org.olat.core.gui.components.segmentedview.SegmentViewEvent;
import org.olat.core.gui.components.segmentedview.SegmentViewFactory;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.util.resource.OresHelper;

/**
 * 
 * Initial date: 14 mars 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class OrganisationOverviewController extends BasicController {
	
	private final Link metadataLink;
	private final Link userManagementLink;
	private final VelocityContainer mainVC;
	private final SegmentViewComponent segmentView;
	
	private EditOrganisationController metadataCtrl;
	private OrganisationUserManagementController userMgmtCtrl;
	
	private Organisation organisation;
	
	public OrganisationOverviewController(UserRequest ureq, WindowControl wControl, Organisation organisation) {
		super(ureq, wControl);
		this.organisation = organisation;
		
		mainVC = createVelocityContainer("organisation_overview");
		
		segmentView = SegmentViewFactory.createSegmentView("segments", mainVC, this);
		segmentView.setDontShowSingleSegment(true);
		metadataLink = LinkFactory.createLink("organisation.metadata", mainVC, this);
		segmentView.addSegment(metadataLink, true);
		userManagementLink = LinkFactory.createLink("organisation.user.management", mainVC, this);
		segmentView.addSegment(userManagementLink, false);

		putInitialPanel(mainVC);
		doOpenMetadadata(ureq);
	}
	

	@Override
	public void event(UserRequest ureq, Controller source, Event event) {
		if(metadataCtrl == source) {
			if(event == Event.CANCELLED_EVENT) {
				fireEvent(ureq, event);
			} else if(event == Event.DONE_EVENT) {// done -> data saved
				fireEvent(ureq, Event.CHANGED_EVENT);
			}
		}
		
		super.event(ureq, source, event);
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if(source == segmentView) {
			if(event instanceof SegmentViewEvent) {
				SegmentViewEvent sve = (SegmentViewEvent)event;
				String segmentCName = sve.getComponentName();
				Component clickedLink = mainVC.getComponent(segmentCName);
				if (clickedLink == metadataLink) {
					doOpenMetadadata(ureq);
				} else if (clickedLink == userManagementLink){
					doOpenUsermanagement(ureq);
				}
			}
		}
	}

	@Override
	protected void doDispose() {
		//
	}
	
	private void doOpenMetadadata(UserRequest ureq) {
		if(metadataCtrl == null) {
			WindowControl bwControl = addToHistory(ureq, OresHelper.createOLATResourceableType("Metadata"), null);
			metadataCtrl = new EditOrganisationController(ureq, bwControl, organisation);
			listenTo(metadataCtrl);
		}

		addToHistory(ureq, metadataCtrl);
		mainVC.put("segmentCmp", metadataCtrl.getInitialComponent());
	}
	
	private void doOpenUsermanagement(UserRequest ureq) {
		if(userMgmtCtrl == null) {
			WindowControl bwControl = addToHistory(ureq, OresHelper.createOLATResourceableType("UserMgmt"), null);
			userMgmtCtrl = new OrganisationUserManagementController(ureq, bwControl, organisation);
			listenTo(userMgmtCtrl);
		}
		
		addToHistory(ureq, userMgmtCtrl);
		mainVC.put("segmentCmp", userMgmtCtrl.getInitialComponent());
	}
}
