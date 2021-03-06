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
* Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
* University of Zurich, Switzerland.
* <hr>
* <a href="http://www.openolat.org">
* OpenOLAT - Online Learning and Training</a><br>
* This file has been modified by the OpenOLAT community. Changes are licensed
* under the Apache 2.0 license as the original file.  
* <p>
*/ 

package org.olat.core.id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.olat.basesecurity.OrganisationRoles;

/**
*  Description:<br>
* @author Felix Jost
*/
public class Roles implements Serializable {
	private static final long serialVersionUID = 4726449291059674346L;
	private final boolean isSystemAdmin;
	private final boolean isOLATAdmin;
	private final boolean isUserManager;
	private final boolean isGroupManager;
	private final boolean isAuthor;
	private final boolean isCoach;
	private final boolean isGuestOnly;
	private final boolean isLearnResourceManager;
	private final boolean isQPoolManager;
	private final boolean isCurriculumManager;
	private final boolean isInvitee;
	
	private List<RolesByOrganisation> rolesByOrganisations;

	/**
	 * @param isOLATAdmin
	 * @param isUserManager
	 * @param isGroupManager
	 * @param isAuthor
	 * @param isGuestOnly
	 * @param isUniCourseManager
	 */
	public Roles(boolean isOLATAdmin, boolean isUserManager, boolean isGroupManager, boolean isAuthor, boolean isGuestOnly,
			boolean isLearnResourceManager, boolean isInvitee) {
		this(false, isOLATAdmin, isGroupManager, isUserManager, isAuthor, isGuestOnly, isLearnResourceManager, false,  false, false, isInvitee);
	}
	
	public Roles(boolean isSystemAdmin, boolean isOLATAdmin, boolean isUserManager, boolean isGroupManager, boolean isAuthor, boolean isGuestOnly,
			boolean isLearnResourceManager, boolean isQPoolManager, boolean isCurriculumManager, boolean isCoach, boolean isInvitee) {
		this.isSystemAdmin = isSystemAdmin;
		this.isOLATAdmin = isOLATAdmin;
		this.isGroupManager = isGroupManager;
		this.isUserManager = isUserManager;
		this.isAuthor = isAuthor;
		this.isGuestOnly = isGuestOnly;
		this.isLearnResourceManager = isLearnResourceManager;
		this.isQPoolManager = isQPoolManager;
		this.isCurriculumManager = isCurriculumManager;
		this.isInvitee = isInvitee;
		this.isCoach = isCoach;
	}
	
	/**
	 * The roles of a standard user without special permissions.
	 * 
	 * @return The roles object
	 */
	public static final Roles userRoles() {
		return new Roles(false, false, false, false, false, false, false, false, false, false, false);
	}
	
	public static final Roles authorRoles() {
		return new Roles(false, false, false, true, false, false, false);
	}
	
	public static final Roles roles(OrganisationRoles... organisationRoles) {
		boolean systemAdmin = false;
		boolean olatAdmin = false;
		boolean groupManager = false;
		boolean userManager = false;
		boolean author = false;
		boolean guestOnly = false;
		boolean learnResourceManager = false;
		boolean poolAdmin = false;
		boolean curriculumManager = false;
		boolean invitee = false;
		boolean coach = false;

		if(organisationRoles != null && organisationRoles.length > 0) {
			for(OrganisationRoles organisationRole:organisationRoles) {
				if(organisationRole != null) {
					switch(organisationRole) {
						case sysadmin: systemAdmin = true; break;
						case administrator: olatAdmin = true; break;
						case usermanager: userManager = true; break;
						case learnresourcemanager: learnResourceManager = true; break;
						case groupmanager: groupManager = true; break;
						case poolmanager: poolAdmin = true; break;
						case curriculummanager: curriculumManager = true; break;
						case author: author = true; break;
						case coach: coach = true; break;
						case user: break;
						case invitee: invitee = true; break;
						case guest: guestOnly = true; break;
					}
				}
			}
		}
		
		return new Roles(systemAdmin, olatAdmin, userManager, groupManager, author, guestOnly,
				learnResourceManager, poolAdmin, curriculumManager, coach, invitee);
	}
	
	public void setRolesByOrganisation(List<RolesByOrganisation> rolesByOrganisations) {
		this.rolesByOrganisations = new ArrayList<>(rolesByOrganisations);
	}
	
	public RolesByOrganisation getRoles(OrganisationRef organisation) {
		RolesByOrganisation setOfRoles = null;
		if(rolesByOrganisations != null) {
			for(int i=rolesByOrganisations.size(); i--> 0; ) {
				if(rolesByOrganisations.get(i).matchOrganisation(organisation)) {
					setOfRoles = rolesByOrganisations.get(i);
				}
			}
		}
		return setOfRoles;
	}
	
	/**
	 * All the organizations 
	 * 
	 * @return
	 */
	public List<OrganisationRef> getOrganisations() {
		Set<OrganisationRef> organisations = new HashSet<>();
		if(rolesByOrganisations != null) {
			for(int i=rolesByOrganisations.size(); i--> 0; ) {
				organisations.add(rolesByOrganisations.get(i).getOrganisation());
			}
		}
		return new ArrayList<>(organisations);
	}
	
	public List<OrganisationRef> getOrganisationsWithRole(OrganisationRoles role) {
		List<OrganisationRef> organisations = new ArrayList<>();
		if(rolesByOrganisations != null) {
			for(int i=rolesByOrganisations.size(); i--> 0; ) {
				if(rolesByOrganisations.get(i).hasRole(role)) {
					organisations.add(rolesByOrganisations.get(i).getOrganisation());
				}
			}
		}
		return organisations;
	}
	
	public List<OrganisationRef> getOrganisationsWithRoles(OrganisationRoles... roles) {
		List<OrganisationRef> organisations = new ArrayList<>();
		if(rolesByOrganisations != null) {
			for(int i=rolesByOrganisations.size(); i--> 0; ) {
				if(rolesByOrganisations.get(i).hasSomeRoles(roles)) {
					organisations.add(rolesByOrganisations.get(i).getOrganisation());
				}
			}
		}
		return organisations;
	}
	
	public boolean hasRole(OrganisationRef organisation, OrganisationRoles role) {
		boolean foundRole = false;
		if(rolesByOrganisations != null) {
			for(int i=rolesByOrganisations.size(); i--> 0; ) {
				if(rolesByOrganisations.get(i).matchOrganisation(organisation) && rolesByOrganisations.get(i).hasRole(role)) {
					foundRole = true;
				}
			}
		}
		return foundRole;
	}
	
	public boolean hasSomeRoles(OrganisationRef organisation, OrganisationRoles... roles) {
		boolean foundRole = false;
		if(rolesByOrganisations != null) {
			for(int i=rolesByOrganisations.size(); i--> 0; ) {
				if(rolesByOrganisations.get(i).matchOrganisation(organisation) && rolesByOrganisations.get(i).hasSomeRoles(roles)) {
					foundRole = true;
				}
			}
		}
		return foundRole;
	}
	
	public boolean hasRoleInParentLine(Organisation organisation, OrganisationRoles role) {
		boolean foundRole = false;
		if(rolesByOrganisations != null) {
			for(int i=rolesByOrganisations.size(); i--> 0; ) {
				if(rolesByOrganisations.get(i).matchOrganisationOrItsParents(organisation) && rolesByOrganisations.get(i).hasRole(role)) {
					foundRole = true;
				}
			}
		}
		return foundRole;
	}
	
	public boolean hasRole(List<? extends OrganisationRef> organisations, OrganisationRoles role) {
		if(rolesByOrganisations != null) {
			for(OrganisationRef organisation: organisations) {
				for(int i=rolesByOrganisations.size(); i--> 0; ) {
					if(rolesByOrganisations.get(i).matchOrganisation(organisation) && rolesByOrganisations.get(i).hasRole(role)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isSystemAdmin() {
		return isSystemAdmin;
	}

	/**
	 * @return boolean
	 */
	public boolean isOLATAdmin() {
		return isOLATAdmin;
	}

	/**
	 * @return boolean
	 */
	public boolean isAuthor() {
		return isAuthor;
	}
	
	public boolean isCoach() {
		return isCoach;
	}
	
	/**
	 * @return boolean
	 */
	public boolean isGuestOnly() {
		return isGuestOnly;
	}
	
	/**
	 * @return boolean
	 */
	public boolean isGroupManager() {
		return isGroupManager;
	}

	/**
	 * @return boolean true if the user has the role "user manager" in some organizations.
	 */
	public boolean isUserManager() {
		return isUserManager;
	}
	
	/**
	 * @return boolean True if the user has the role "learn resource manager" in some organizations.
	 */
	public boolean isLearnResourceManager() {
		return isLearnResourceManager;
	}
	
	/**
	 * @return boolean
	 */
	public boolean isQPoolManager() {
		return isQPoolManager;
	}
	
	public boolean isCurriculumManager() {
		return isCurriculumManager;
	}
	
	/**
	 * @return boolean
	 */
	public boolean isInvitee() {
		return isInvitee;
	}

	@Override
	public String toString() {
		return "admin:"+isOLATAdmin+", usermanager:"+isUserManager+", groupmanager:"+isGroupManager+", author:"+isAuthor+", guestonly:"+isGuestOnly+", isInstitutionalResourceManager:"+isLearnResourceManager+", isInvitee:"+isInvitee+", "+super.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isAuthor ? 1231 : 1237);
		result = prime * result + (isGroupManager ? 1231 : 1237);
		result = prime * result + (isGuestOnly ? 1231 : 1237);
		result = prime * result + (isLearnResourceManager ? 1231 : 1237);
		result = prime * result + (isInvitee ? 1231 : 1237);
		result = prime * result + (isOLATAdmin ? 1231 : 1237);
		result = prime * result + (isUserManager ? 1231 : 1237);
		result = prime * result + (isQPoolManager ? 1231 : 1237);
		result = prime * result + (isCurriculumManager ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		Roles other = (Roles) obj;
		return isOLATAdmin == other.isOLATAdmin
				&& isUserManager == other.isUserManager
				&& isGroupManager == other.isGroupManager
				&& isAuthor == other.isAuthor
				&& isGuestOnly == other.isGuestOnly
				&& isLearnResourceManager == other.isLearnResourceManager
				&& isQPoolManager == other.isQPoolManager
				&& isCurriculumManager == other.isCurriculumManager
				&& isInvitee == other.isInvitee;
	}
}
