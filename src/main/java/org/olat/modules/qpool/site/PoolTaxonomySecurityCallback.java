/**
 * OLAT - Online Learning and Training<br>
 * http://www.olat.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Copyright (c) frentix GmbH<br>
 * http://www.frentix.com<br>
 * <p>
 */
package org.olat.modules.qpool.site;

import java.util.Date;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.control.navigation.SiteSecurityCallback;
import org.olat.core.id.Roles;
import org.olat.core.util.StringHelper;
import org.olat.core.util.UserSession;
import org.olat.modules.qpool.QuestionPoolModule;
import org.olat.modules.taxonomy.TaxonomyCompetenceTypes;
import org.olat.modules.taxonomy.TaxonomyRef;
import org.olat.modules.taxonomy.TaxonomyService;
import org.olat.modules.taxonomy.model.TaxonomyRefImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Guest and invittee are discarded. Administrator and pool administrators
 * are allowed and the user with a competence manage or teach (dependent
 * on question pool settings).
 * 
 * Initial date: 5 janv. 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
@Service("poolTaxonomySiteSecurityCallback")
public class PoolTaxonomySecurityCallback implements SiteSecurityCallback {
	
	@Autowired
	private TaxonomyService taxonomyService;
	@Autowired
	private QuestionPoolModule questionPoolModule;
	
	@Override
	public boolean isAllowedToLaunchSite(UserRequest ureq) {
		if (!questionPoolModule.isEnabled() || !StringHelper.isLong(questionPoolModule.getTaxonomyQPoolKey())
				|| ureq == null || ureq.getIdentity() == null ) {
			return false;
		}
		
		UserSession usess = ureq.getUserSession();
		if (usess == null) {
			return false;
		}

		Roles roles = usess.getRoles();
		if (roles == null || roles.isInvitee() || roles.isGuestOnly()) {
			return false;
		}
		if (roles.isOLATAdmin() || roles.isQPoolManager()) {
			return true;
		}
		
		TaxonomyCompetenceTypes[] types = new TaxonomyCompetenceTypes[] { TaxonomyCompetenceTypes.manage, TaxonomyCompetenceTypes.teach };
		TaxonomyRef taxonomy = new TaxonomyRefImpl(Long.valueOf(questionPoolModule.getTaxonomyQPoolKey()));
		return taxonomyService.hasTaxonomyCompetences(taxonomy, ureq.getIdentity(), new Date(), types);
	}
}
