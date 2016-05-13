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

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;

/**
 * 
 * Initial date: 19.04.2016<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class InteractionResultComponent extends AssessmentObjectComponent  {
	
	private static final InteractionResultComponentRenderer VELOCITY_RENDERER = new InteractionResultComponentRenderer();
	
	
	private final InteractionResultFormItem qtiItem;
	
	private final Interaction interaction;
	private TestSessionController testSessionController;
	private ResolvedAssessmentTest resolvedAssessmentTest;
	private final ResolvedAssessmentItem resolvedAssessmentItem;
	
	private ItemSessionState itemSessionState;
	private boolean showSolution;
	
	public InteractionResultComponent(String name, Interaction interaction,
			ResolvedAssessmentItem resolvedAssessmentItem, InteractionResultFormItem qtiItem) {
		super(name);
		this.qtiItem = qtiItem;
		this.interaction = interaction;
		this.resolvedAssessmentItem = resolvedAssessmentItem;
	}

	public boolean isShowSolution() {
		return showSolution;
	}

	public void setShowSolution(boolean showSolution) {
		this.showSolution = showSolution;
	}

	public ResolvedAssessmentTest getResolvedAssessmentTest() {
		return resolvedAssessmentTest;
	}

	public void setResolvedAssessmentTest(ResolvedAssessmentTest resolvedAssessmentTest) {
		this.resolvedAssessmentTest = resolvedAssessmentTest;
	}

	public TestSessionController getTestSessionController() {
		return testSessionController;
	}

	public void setTestSessionController(TestSessionController testSessionController) {
		this.testSessionController = testSessionController;
	}

	@Override
	public String getResponseUniqueIdentifier(ItemSessionState state, Interaction i) {
		return getDispatchID() + interaction.getResponseIdentifier().toString();
	}

	@Override
	public Interaction getInteractionOfResponseUniqueIdentifier(String responseUniqueId) {
		return null;
	}

	public ItemSessionState getItemSessionState() {
		return itemSessionState;
	}

	public void setItemSessionState(ItemSessionState itemSessionState) {
		this.itemSessionState = itemSessionState;
	}

	public Interaction getInteraction() {
		return interaction;
	}
	
	public ResolvedAssessmentItem getResolvedAssessmentItem() {
		return resolvedAssessmentItem;
	}

	@Override
	public InteractionResultFormItem getQtiItem() {
		return qtiItem;
	}
	
	@Override
	public String relativePathTo(ResolvedAssessmentItem resolvedAssessmentItem) {
		URI itemUri = resolvedAssessmentItem.getItemLookup().getSystemId();
		File itemFile = new File(itemUri);
		URI testUri = resolvedAssessmentTest.getTestLookup().getSystemId();
		File testFile = new File(testUri);
		Path relativePath = testFile.toPath().getParent().relativize(itemFile.toPath().getParent());
		String relativePathString = relativePath.toString();
		if(relativePathString.isEmpty()) {
			return relativePathString;
		} else if(relativePathString.endsWith("/")) {
			return relativePathString;
		}
		return relativePathString.concat("/");
	}

	@Override
	public InteractionResultComponentRenderer getHTMLRendererSingleton() {
		return VELOCITY_RENDERER;
	}
}