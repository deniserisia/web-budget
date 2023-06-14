/*
 * Copyright (C) 2014 Arthur Gregorio, AG.Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.webbudget.domain.entities.financial;

import br.com.webbudget.domain.entities.PersistentEntity;
import br.com.webbudget.domain.entities.registration.Contact;
import br.com.webbudget.domain.exceptions.BusinessLogicException;
import br.com.webbudget.infrastructure.utils.RandomCode;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL;
import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL_AUDIT;
import static javax.persistence.CascadeType.REMOVE;

@Entity
@Audited
@Table(name = "movements", schema = FINANCIAL)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@AuditTable(value = "movements", schema = FINANCIAL_AUDIT)
@Data
@ToString(callSuper = true, exclude = {"apportionments", "deletedApportionments"})
@EqualsAndHashCode(callSuper = true, exclude = {"apportionments", "deletedApportionments"})
@NamedEntityGraph(name = "Movement.full", attributeNodes = @NamedAttributeNode(value = "apportionments"))
@DiscriminatorColumn(name = "discriminator_value", length = 15, discriminatorType = DiscriminatorType.STRING)
public class Movement extends PersistentEntity {

    @Column(name = "code", nullable = false, length = 6, unique = true)
    private String code;

    @NotBlank(message = "{movement.identification}")
    @Column(name = "identification", nullable = false, length = 90)
    private String identification;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "{movement.value}")
    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name = "id_contact")
    private Contact contact;

    @OneToMany(mappedBy = "movement", cascade = CascadeType.REMOVE)
    private Set<Apportionment> apportionments;

    @Transient
    private Set<Apportionment> deletedApportionments;

    public Movement() {
        this.code = RandomCode.alphanumeric(6);
        this.apportionments = new HashSet<>();
        this.deletedApportionments = new HashSet<>();
    }

    public String getContactName() {
        return this.contact != null ? this.contact.getName() : "";
    }

    public boolean isExpense() {
        return this.apportionments.stream()
                .findFirst()
                .map(Apportionment::isExpense)
                .orElse(false);
    }

    public boolean isRevenue() {
        return this.apportionments.stream()
                .findFirst()
                .map(Apportionment::isRevenue)
                .orElse(false);
    }

    public void add(Apportionment apportionment) {
        this.apportionments.add(apportionment);
    }

    public void addAll(Set<Apportionment> apportionments) {
        this.apportionments.addAll(apportionments);
    }

    public void remove(Apportionment apportionment) {
        if (apportionment.isSaved()) {
            this.deletedApportionments.add(apportionment);
        }
        this.apportionments.remove(apportionment);
    }

    public BigDecimal calculateRemainingTotal() {
        BigDecimal remaining = this.value.subtract(calculateApportionmentsTotal());

        if (remaining.signum() <= 0) {
            throw new BusinessLogicException("error.period-movement.no-value-to-divide");
        }

        return remaining;
    }

    private BigDecimal calculateApportionmentsTotal() {
        return this.apportionments.stream()
                .map(Apportionment::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Set<Apportionment> copyApportionments() {
        return this.apportionments.stream()
                .map(Apportionment::copyOf)
                .collect(Collectors.toSet());
    }
}
