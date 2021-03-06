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
package org.olat.modules.quality.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.olat.core.commons.persistence.DB;
import org.olat.core.commons.persistence.SortKey;
import org.olat.core.id.Identity;
import org.olat.core.id.User;
import org.olat.core.id.UserConstants;
import org.olat.modules.forms.EvaluationFormSurvey;
import org.olat.modules.quality.QualityDataCollection;
import org.olat.modules.quality.QualityExecutorParticipation;
import org.olat.modules.quality.QualityParticipation;
import org.olat.modules.quality.ui.ExecutorParticipationDataModel.ExecutorParticipationCols;
import org.olat.modules.quality.ui.ParticipationDataModel.ParticipationCols;
import org.olat.test.JunitTestHelper;
import org.olat.test.OlatTestCase;
import org.olat.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 13.06.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
@Service
public class QualityParticipationDAOTest extends OlatTestCase {

	@Autowired
	private DB dbInstance;
	@Autowired
	private QualityTestHelper qualityTestHelper;
	
	@Autowired
	private QualityParticipationDAO sut;

	@Before
	public void cleanUp() {
		qualityTestHelper.deleteAll();
	}
	
	@Test
	public void shouldGetParticipationCount() {
		QualityDataCollection dataCollection = qualityTestHelper.createDataCollection();
		EvaluationFormSurvey survey = qualityTestHelper.createSurvey(dataCollection);
		int numberOfParticipations = 3;
		for (int i = 0; i < numberOfParticipations; i++) {
			qualityTestHelper.createParticipation(survey);
		}
		dbInstance.commit();
		
		int count = sut.getParticipationCount(dataCollection);
		
		assertThat(count).isEqualTo(numberOfParticipations);
	}
	
	@Test
	public void shouldLoadParticipations() {
		QualityDataCollection dataCollection = qualityTestHelper.createDataCollection();
		EvaluationFormSurvey survey = qualityTestHelper.createSurvey(dataCollection);
		int numberOfParticipations = 3;
		for (int i = 0; i < numberOfParticipations; i++) {
			Identity identity = JunitTestHelper.createAndPersistIdentityAsRndUser("quality-");
			qualityTestHelper.createParticipation(survey, identity);
		}
		dbInstance.commit();
		
		List<QualityParticipation> participations = sut.loadParticipations(dataCollection, 0, -1);
		
		assertThat(participations).hasSize(numberOfParticipations);
		QualityParticipation participation = participations.get(0);
		assertThat(participation.getParticipationRef()).isNotNull();
		assertThat(participation.getFirstname()).isNotNull();
		assertThat(participation.getLastname()).isNotNull();
		assertThat(participation.getEmail()).isNotNull();
	}
	
	@Test
	public void shouldLoadParticipationsPaged() {
		QualityDataCollection dataCollection = qualityTestHelper.createDataCollection();
		EvaluationFormSurvey survey = qualityTestHelper.createSurvey(dataCollection);
		int numberOfParticipations = 3;
		for (int i = 0; i < numberOfParticipations; i++) {
			Identity identity = JunitTestHelper.createAndPersistIdentityAsRndUser("quality-");
			qualityTestHelper.createParticipation(survey, identity);
		}
		dbInstance.commit();
		
		List<QualityParticipation> participations = sut.loadParticipations(dataCollection, 1, 1);
		
		assertThat(participations).hasSize(1);
	}
	
	@Test
	public void shouldLoadParticipationsOrdered() {
		QualityDataCollection dataCollection = qualityTestHelper.createDataCollection();
		EvaluationFormSurvey survey = qualityTestHelper.createSurvey(dataCollection);
		Identity identityZ = JunitTestHelper.createAndPersistIdentityAsRndUser("quality-");
		User userZ = identityZ.getUser();
		userZ.setProperty(UserConstants.LASTNAME, "Z");
		UserManager.getInstance().updateUser(userZ);
		qualityTestHelper.createParticipation(survey, identityZ);
		Identity identityA = JunitTestHelper.createAndPersistIdentityAsRndUser("quality-");
		User userA = identityA.getUser();
		userA.setProperty(UserConstants.LASTNAME, "A");
		UserManager.getInstance().updateUser(userA);
		qualityTestHelper.createParticipation(survey, identityA);
		dbInstance.commit();
		
		SortKey sortKey = new SortKey(ParticipationCols.lastname.name(), true);
		List<QualityParticipation> participations = sut.loadParticipations(dataCollection, 0, -1, sortKey);
		
		assertThat(participations.get(0).getLastname()).isEqualTo("A");
		assertThat(participations.get(1).getLastname()).isEqualTo("Z");
	}
	
	@Test
	public void shouldLoadParticipationsOrderedByAllColumns() {
		QualityDataCollection dataCollection = qualityTestHelper.createDataCollection();
		EvaluationFormSurvey survey = qualityTestHelper.createSurvey(dataCollection);
		Identity identity = JunitTestHelper.createAndPersistIdentityAsRndUser("quality-");
		qualityTestHelper.createParticipation(survey, identity);
		dbInstance.commit();
		
		List<ParticipationCols> excludedCols = Arrays.asList();
		for (ParticipationCols col: ParticipationCols.values()) {
			if (!excludedCols.contains(col)) {
				SortKey sortKey = new SortKey(col.name(), true);
				sut.loadParticipations(dataCollection, 0, -1, sortKey);
			}
		}
		
		// Only check that no Exception is thrown to be sure that hql syntax is ok.
	}
	
	@Test
	public void shouldGetExecutorParticipationCount() {
		QualityDataCollection dataCollection1 = qualityTestHelper.createDataCollection();
		QualityDataCollection dataCollection2 = qualityTestHelper.createDataCollection();
		Identity identity = JunitTestHelper.createAndPersistIdentityAsRndUser("quality-");
		Identity otherIdentity = JunitTestHelper.createAndPersistIdentityAsRndUser("quality-");
		qualityTestHelper.addParticipations(dataCollection1, Arrays.asList(identity, otherIdentity));
		qualityTestHelper.addParticipations(dataCollection2, Arrays.asList(identity));
		EvaluationFormSurvey otherSurvey = qualityTestHelper.createRandomSurvey();
		qualityTestHelper.createParticipation(otherSurvey, identity);
		dbInstance.commit();
		
		int count = sut.getExecutorParticipationCount(identity);
		
		assertThat(count).isEqualTo(2);
	}
	
	@Test
	public void shouldLoadExecutorParticipations() {
		QualityDataCollection dataCollection1 = qualityTestHelper.createDataCollection();
		QualityDataCollection dataCollection2 = qualityTestHelper.createDataCollection();
		Identity identity = JunitTestHelper.createAndPersistIdentityAsRndUser("quality-");
		Identity otherIdentity = JunitTestHelper.createAndPersistIdentityAsRndUser("quality-");
		qualityTestHelper.addParticipations(dataCollection1, Arrays.asList(identity, otherIdentity));
		qualityTestHelper.addParticipations(dataCollection2, Arrays.asList(identity));
		EvaluationFormSurvey otherSurvey = qualityTestHelper.createRandomSurvey();
		qualityTestHelper.createParticipation(otherSurvey, identity);
		dbInstance.commit();
		
		List<QualityExecutorParticipation> participations = sut.loadExecutorParticipations(identity, 0, -1);
		
		assertThat(participations).hasSize(2);
		QualityExecutorParticipation participation = participations.get(0);
		assertThat(participation.getParticipationRef()).isNotNull();
		assertThat(participation.getParticipationStatus()).isNotNull();
		assertThat(participation.getStart()).isNotNull();
		assertThat(participation.getDeadline()).isNotNull();
		assertThat(participation.getTitle()).isNotNull();
	}
	
	@Test
	public void shouldLoadExecutorParticipationsPaged() {
		QualityDataCollection dataCollection1 = qualityTestHelper.createDataCollection("Z");
		QualityDataCollection dataCollection2 = qualityTestHelper.createDataCollection("A");
		Identity identity = JunitTestHelper.createAndPersistIdentityAsRndUser("quality-");
		qualityTestHelper.addParticipations(dataCollection1, Arrays.asList(identity));
		qualityTestHelper.addParticipations(dataCollection2, Arrays.asList(identity));
		dbInstance.commit();
		
		List<QualityExecutorParticipation> participations = sut.loadExecutorParticipations(identity, 1, 1);
		
		assertThat(participations).hasSize(1);
	}
	
	@Test
	public void shouldLoadExecutorParticipationsOrdered() {
		QualityDataCollection dataCollection1 = qualityTestHelper.createDataCollection("Z");
		QualityDataCollection dataCollection2 = qualityTestHelper.createDataCollection("A");
		Identity identity = JunitTestHelper.createAndPersistIdentityAsRndUser("quality-");
		qualityTestHelper.addParticipations(dataCollection1, Arrays.asList(identity));
		qualityTestHelper.addParticipations(dataCollection2, Arrays.asList(identity));
		dbInstance.commit();
		
		SortKey sortKey = new SortKey(ExecutorParticipationCols.title.name(), true);
		List<QualityExecutorParticipation> participations = sut.loadExecutorParticipations(identity, 0, -1, sortKey);
		
		assertThat(participations.get(0).getTitle()).isEqualTo("A");
		assertThat(participations.get(1).getTitle()).isEqualTo("Z");
	}
	
	@Test
	public void shouldLoadExecutorParticipationsOrderedByAllColumns() {
		QualityDataCollection dataCollection = qualityTestHelper.createDataCollection();
		Identity identity = JunitTestHelper.createAndPersistIdentityAsRndUser("quality-");
		qualityTestHelper.addParticipations(dataCollection, Arrays.asList(identity));
		dbInstance.commit();
		
		List<ExecutorParticipationCols> excludedCols = Arrays.asList(ExecutorParticipationCols.execute);
		for (ExecutorParticipationCols col: ExecutorParticipationCols.values()) {
			if (!excludedCols.contains(col)) {
				SortKey sortKey = new SortKey(col.name(), true);
				sut.loadExecutorParticipations(identity, 0, -1, sortKey);
			}
		}
		
		// Only check that no Exception is thrown to be sure that hql syntax is ok.
	}
}
