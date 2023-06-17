/*
 * Copyright (C) 2015 Arthur Gregorio, AG.Software
 *
 * This class represents an authorization for a single functionality.
 */
package br.com.webbudget.domain.entities.configuration;

import br.com.webbudget.domain.entities.PersistentEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import static br.com.webbudget.infrastructure.utils.DefaultSchemes.CONFIGURATION;
import static br.com.webbudget.infrastructure.utils.DefaultSchemes.CONFIGURATION_AUDIT;

@Entity
@Audited
@ToString
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "authorizations", schema = CONFIGURATION)
@AuditTable(value = "authorizations", schema = CONFIGURATION_AUDIT)
public class Authorization extends PersistentEntity {

    @Getter
    @Setter
    @Column(name = "functionality_name", nullable = false, length = 90)
    private String functionalityName;

    @Getter
    @Setter
    @Column(name = "permission_name", nullable = false, length = 90)
    private String permissionName;

    public Authorization(String functionalityName, String permissionName) {
        this.functionalityName = functionalityName;
        this.permissionName = permissionName;
    }

    public String getFullPermission() {
        return this.functionalityName + ":" + this.permissionName;
    }

    public boolean isFunctionality(String functionality) {
        return functionality != null && this.functionalityName.equals(functionality);
    }

    public boolean isPermission(String permission) {
        return permission != null && (this.permissionName.equals(permission)
                || this.getFullPermission().equals(permission));
    }

    public <R> R getFunctionality() {

    }

    public boolean isFunctionality(Object functionality) {
    }
}
