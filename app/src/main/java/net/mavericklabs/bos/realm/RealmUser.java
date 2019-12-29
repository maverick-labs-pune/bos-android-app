/*
 * Copyright (c) 2019. Maverick Labs
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as,
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package net.mavericklabs.bos.realm;

import net.mavericklabs.bos.model.LoginResponse;
import net.mavericklabs.bos.model.User;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmUser extends RealmObject {
    @PrimaryKey
    private String key;
    private String ngo;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String language;
    private String role;
    private Boolean isActive;
    private RealmList<RealmResource> resources;

    public RealmUser() {

    }
    public RealmUser(User user, RealmList<RealmResource> realmResources) {
        this.key = user.getKey();
        this.ngo = user.getNgo();
        this.isActive = user.getActive();
        this.firstName = user.getFirstName();
        this.middleName = user.getMiddleName();
        this.lastName = user.getLastName();
        this.role = user.getRole();
        this.resources = realmResources;
        this.language = user.getLanguage();
    }

    public RealmUser(LoginResponse loginResponse) {
        this.key = loginResponse.getUserKey();
        this.ngo = loginResponse.getNgoKey();
        this.isActive = true;
        this.firstName = loginResponse.getFirstName();
        this.lastName = loginResponse.getLastName();
        this.role = loginResponse.getRole();
        this.language = loginResponse.getLanguage();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNgo() {
        return ngo;
    }

    public void setNgo(String ngo) {
        this.ngo = ngo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getFullName() {
        if (middleName == null || middleName.length() == 0 ){
            return firstName + ' ' + lastName;
        }else{
            return firstName + ' ' + middleName + ' ' + lastName;
        }
    }

    public RealmList<RealmResource> getResources() {
        return resources;
    }

    public void setResources(RealmList<RealmResource> resources) {
        this.resources = resources;
    }
}
