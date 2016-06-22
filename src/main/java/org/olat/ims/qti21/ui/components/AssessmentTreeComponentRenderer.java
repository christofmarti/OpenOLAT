/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.ims.qti21.ui.components;

import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.components.form.flexible.impl.FormJSHelper;
import org.olat.core.gui.components.form.flexible.impl.NameValuePair;
import org.olat.core.gui.render.RenderResult;
import org.olat.core.gui.render.Renderer;
import org.olat.core.gui.render.StringOutput;
import org.olat.core.gui.render.URLBuilder;
import org.olat.core.gui.translator.Translator;
import org.olat.core.util.StringHelper;
import org.olat.ims.qti21.AssessmentTestSession;
import org.olat.ims.qti21.model.audit.CandidateEvent;
import org.olat.ims.qti21.model.audit.CandidateTestEventType;
import org.olat.ims.qti21.ui.CandidateSessionContext;
import org.olat.ims.qti21.ui.QTIWorksAssessmentTestEvent.Event;

import uk.ac.ed.ph.jqtiplus.node.content.variable.PrintedVariable;
import uk.ac.ed.ph.jqtiplus.node.test.NavigationMode;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingContext;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPartSessionState;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNode;
import uk.ac.ed.ph.jqtiplus.state.TestPlanNodeKey;
import uk.ac.ed.ph.jqtiplus.state.TestSessionState;

/**
 * 
 * Initial date: 01.03.2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class AssessmentTreeComponentRenderer extends AssessmentObjectComponentRenderer {

	@Override
	public void render(Renderer renderer, StringOutput sb, Component source, URLBuilder ubu,
			Translator translator, RenderResult renderResult, String[] args) {
		
		AssessmentTreeComponent component = (AssessmentTreeComponent)source;
		TestSessionController testSessionController = component.getTestSessionController();
		if(!testSessionController.getTestSessionState().isEnded()) {
			CandidateSessionContext candidateSessionContext = component.getCandidateSessionContext();
			final AssessmentTestSession candidateSession = candidateSessionContext.getCandidateSession();
	        if (!candidateSession.isExploded() && !candidateSessionContext.isTerminated()) {

	    		CandidateEvent candidateEvent = candidateSessionContext.getLastEvent();
	    		CandidateTestEventType testEventType = candidateEvent.getTestEventType();
	    		
	    		RenderingRequest options;
	        	if (testEventType == CandidateTestEventType.REVIEW_ITEM) {
	            	options = RenderingRequest.getItemReview();
	            }  else if (testEventType == CandidateTestEventType.SOLUTION_ITEM) {
	            	options = RenderingRequest.getItemSolution();
	            } else {
	            	options = RenderingRequest.getItem(testSessionController);
	            }
	        	AssessmentRenderer renderHints = new AssessmentRenderer(renderer);
    			renderTestEvent(testSessionController, renderHints, sb, component, ubu, translator, options);

			}
		}
	}
	
	private void renderTestEvent(TestSessionController testSessionController, AssessmentRenderer renderer, StringOutput target,
			AssessmentTreeComponent component, URLBuilder ubu, Translator translator, RenderingRequest options) {

		CandidateSessionContext candidateSessionContext = component.getCandidateSessionContext();
		CandidateEvent candidateEvent = candidateSessionContext.getLastEvent();
		CandidateTestEventType testEventType = candidateEvent.getTestEventType();

        final TestSessionState testSessionState = testSessionController.getTestSessionState();
        if(!candidateSessionContext.isTerminated() && !testSessionState.isExited()) {
        	if (testEventType == CandidateTestEventType.REVIEW_ITEM) {
        		renderer.setReviewMode(true);
        	} else if (testEventType == CandidateTestEventType.SOLUTION_ITEM) {
                renderer.setSolutionMode(true);
        	}
            renderNavigation(renderer, target, component, ubu, translator, options);
        }
    }
	
	private void renderNavigation(AssessmentRenderer renderer, StringOutput sb,
			AssessmentTreeComponent component, URLBuilder ubu, Translator translator, RenderingRequest options) {
		sb.append("<div id='o_qti_menu' class='qtiworks o_assessmenttest o_testpartnavigation'>");
		//part, sections and item refs
		TestPlanNode currentTestPartNode = component.getCurrentTestPartNode();
		if(currentTestPartNode != null) {
			sb.append("<ul class='o_testpartnavigation list-unstyled'>");
			currentTestPartNode.getChildren().forEach((node)
				-> renderNavigation(renderer, sb, component, node, ubu, translator, options));
			sb.append("</ul>");
		}
		sb.append("</div>");
	}
	
	private void renderNavigation(AssessmentRenderer renderer, StringOutput sb, AssessmentTreeComponent component, TestPlanNode node,
			URLBuilder ubu, Translator translator, RenderingRequest options) {
		switch(node.getTestNodeType()) {
			case ASSESSMENT_SECTION:
				renderNavigationAssessmentSection(renderer, sb, component, node, ubu, translator, options);
				break;
			case ASSESSMENT_ITEM_REF:
				renderNavigationAssessmentItem(sb, component, node, translator, options);
				break;
			default: break;
		}
	}
	
	private void renderNavigationAssessmentSection(AssessmentRenderer renderer, StringOutput sb, AssessmentTreeComponent component, TestPlanNode sectionNode,
			URLBuilder ubu, Translator translator, RenderingRequest options) {
		sb.append("<li class='o_assessmentsection o_qti_menu_item'>")
		  .append("<header><h4>").append(sectionNode.getSectionPartTitle()).append("</h4>");
		//renderAssessmentSectionRubrickBlock(renderer, sb, component, sectionNode, ubu, translator);

		sb.append("</header><ul class='o_testpartnavigation_inner list-unstyled'>");
		sectionNode.getChildren().forEach((child)
				-> renderNavigation(renderer, sb, component, child, ubu, translator, options));
		sb.append("</ul></li>");
	}
	
	private void renderNavigationAssessmentItem(StringOutput sb, AssessmentTreeComponent component, TestPlanNode itemNode,
			Translator translator, RenderingRequest options) {
		sb.append("<li class='o_assessmentitem'>");
		renderAssessmentItemLink(sb, component, itemNode);
		renderAssessmentItemAttempts(sb, component, itemNode, translator);
		renderItemStatus(sb, component, itemNode, translator, options);
		renderAssessmentItemMark(sb, component, itemNode);
		sb.append("</li>");
	}
	
	/**
	 * 
	 * @param sb
	 * @param component
	 * @param itemNode
	 * @return The event used or null
	 */
	private Event renderAssessmentItemLink(StringOutput sb, AssessmentTreeComponent component, TestPlanNode itemNode) {
		String key = itemNode.getKey().toString();
		Form form = component.getQtiItem().getRootForm();
		String dispatchId = component.getQtiItem().getFormDispatchId();
		TestSessionController testSessionController = component.getTestSessionController();
		
		TestSessionState testSessionState = testSessionController.getTestSessionState();
		TestPart testPart = testSessionController.getCurrentTestPart();
		TestPlanNodeKey currentTestPartKey = testSessionState.getCurrentTestPartKey();
		TestPartSessionState currentTestPartSessionState = testSessionState.getTestPartSessionStates().get(currentTestPartKey);

		Event event;
		if(testPart == null || testPart.getNavigationMode() == NavigationMode.NONLINEAR) {
			if(testSessionState.isEnded() || currentTestPartSessionState.isEnded()) {
				if(itemNode.getEffectiveItemSessionControl().isAllowReview() || itemNode.getEffectiveItemSessionControl().isShowFeedback()) {
					event = Event.reviewItem;
				} else {
					event = null;
				}
			} else {
				event = Event.selectItem;
			}
		} else {
			event = null;
		}
	
		if(event == null) {
			sb.append("<span class='o_assessmentitem_nav_disabled'>");
		} else {
			sb.append("<a href='javascript:;' onclick=\"")
			  .append(FormJSHelper.getXHRFnCallFor(form, dispatchId, 1, true, true,
					new NameValuePair("cid", event.name()), new NameValuePair("item", key)))
			  .append(";\" class=''>");
		}
		sb.append("<span class='questionTitle'>").append(itemNode.getSectionPartTitle()).append("</span>");

		if(event == null) {
			sb.append("</span>");
		} else {
			sb.append("</a>");
		}
		return event;
	}
	
	private void renderItemStatus(StringOutput sb, AssessmentTreeComponent component, TestPlanNode itemNode,
			Translator translator, RenderingRequest options) {
		ItemProcessingContext itemProcessingContext = component.getItemSessionState(itemNode);
		ItemSessionState itemSessionState = itemProcessingContext.getItemSessionState();
		renderItemStatus(sb, itemSessionState, options, translator);
	}
	
	private void renderAssessmentItemMark(StringOutput sb, AssessmentTreeComponent component, TestPlanNode itemNode) {	
		String key = itemNode.getKey().toString();
		Form form = component.getQtiItem().getRootForm();
		String dispatchId = component.getQtiItem().getFormDispatchId();
		boolean mark = component.getCandidateSessionContext().isMarked(key);
		
		sb.append("<a href='javascript:;' onclick=\"")
		  .append(FormJSHelper.getXHRFnCallFor(form, dispatchId, 1, true, true,
				new NameValuePair("cid", Event.mark.name()), new NameValuePair("item", key)))
		  .append("; o_toggleMark(this); return false;\" class='o_assessmentitem_marks'><i class='o_icon ")
		  .append("o_icon_bookmark", "o_icon_bookmark_add", mark)
		  .append("'>&nbsp;</i></a>");
	}
	
	private void renderAssessmentItemAttempts(StringOutput sb, AssessmentTreeComponent component, TestPlanNode itemNode,
			Translator translator) {
		ItemProcessingContext itemProcessingContext = component.getItemSessionState(itemNode);
		ItemSessionState itemSessionState = itemProcessingContext.getItemSessionState();
		
		//attempts
		int numOfAttempts = itemSessionState.getNumAttempts();
		int maxAttempts = 0;
		if(itemProcessingContext instanceof ItemSessionController) {
			ItemSessionController itemSessionController = (ItemSessionController)itemProcessingContext;
			maxAttempts = itemSessionController.getItemSessionControllerSettings().getMaxAttempts();
		}		
		sb.append("<span class='o_assessmentitem_attempts'");
		if(maxAttempts > 0) {
			String title = translator.translate("attemptsleft", new String[] { Integer.toString((maxAttempts - numOfAttempts)) });
			sb.append(" title=\"").append(StringHelper.escapeHtml(title)).append("\">")
			  .append("<i class='o_icon o_icon_attempt_limit'> </i> ");
		} else {
			sb.append(">");
		}
		sb.append(numOfAttempts).append("</span>");
		
		
		
	}
	
	@Override
	protected void renderPrintedVariable(AssessmentRenderer renderer, StringOutput sb,
			AssessmentObjectComponent component, ResolvedAssessmentItem resolvedAssessmentItem, ItemSessionState itemSessionState,
			PrintedVariable printedVar) {
		//
	}
}