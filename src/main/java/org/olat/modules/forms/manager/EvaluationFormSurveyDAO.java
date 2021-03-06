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
package org.olat.modules.forms.manager;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.olat.core.commons.persistence.DB;
import org.olat.core.id.OLATResourceable;
import org.olat.core.util.StringHelper;
import org.olat.modules.forms.EvaluationFormSurvey;
import org.olat.modules.forms.model.jpa.EvaluationFormSurveyImpl;
import org.olat.repository.RepositoryEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 29.04.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
@Service
class EvaluationFormSurveyDAO {
	
	@Autowired
	private DB dbInstance;

	EvaluationFormSurvey createSurvey(OLATResourceable ores, String subIdent, RepositoryEntry formEntry) {
		EvaluationFormSurveyImpl survey = new EvaluationFormSurveyImpl();
		survey.setCreationDate(new Date());
		survey.setLastModified(survey.getCreationDate());
		survey.setResName(ores.getResourceableTypeName());
		survey.setResId(ores.getResourceableId());
		survey.setResSubident(subIdent);
		survey.setFormEntry(formEntry);
		dbInstance.getCurrentEntityManager().persist(survey);
		return survey;
	}

	EvaluationFormSurvey updateForm(EvaluationFormSurvey survey, RepositoryEntry formEntry) {
		if (survey instanceof EvaluationFormSurveyImpl) {
			EvaluationFormSurveyImpl surveyImpl = (EvaluationFormSurveyImpl) survey;
			surveyImpl.setFormEntry(formEntry);
			return update(surveyImpl);
		}
		return survey;
	}

	private EvaluationFormSurvey update(EvaluationFormSurveyImpl surveyImpl) {
		surveyImpl.setLastModified(new Date());
		return dbInstance.getCurrentEntityManager().merge(surveyImpl);
	}

	EvaluationFormSurvey loadByResourceable(OLATResourceable ores, String subIdent) {
		if (ores == null || ores.getResourceableTypeName() == null || ores.getResourceableId() == null)
			return null;
		
		StringBuilder sb = new StringBuilder();
		sb.append("select survey from evaluationformsurvey as survey");
		sb.append(" where survey.resName=:resName");
		sb.append("   and survey.resId=:resId");
		if (StringHelper.containsNonWhitespace(subIdent)) {
			sb.append(" and survey.resSubident=:resSubident");
		}
		
		TypedQuery<EvaluationFormSurvey> query = dbInstance.getCurrentEntityManager()
				.createQuery(sb.toString(), EvaluationFormSurvey.class)
				.setParameter("resName", ores.getResourceableTypeName())
				.setParameter("resId", ores.getResourceableId());
		if (StringHelper.containsNonWhitespace(subIdent)) {
			query.setParameter("resSubident", subIdent);
		}
		List<EvaluationFormSurvey> surveys = query.getResultList();
		return surveys.isEmpty() ? null : surveys.get(0);
	}

	void delete(EvaluationFormSurvey survey) {
		if (survey == null) return;	

		String query = "delete from evaluationformsurvey as survey where survey.key=:surveyKey";
		
		dbInstance.getCurrentEntityManager().createQuery(query)
			.setParameter("surveyKey", survey.getKey())
			.executeUpdate();
	}

}
