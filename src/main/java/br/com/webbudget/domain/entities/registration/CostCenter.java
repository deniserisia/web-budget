/*
 * Copyright (C) 2015 Arthur Gregorio, AG.Software
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
package br.com.webbudget.domain.entities.registration;

import br.com.webbudget.application.components.Color;
import br.com.webbudget.domain.entities.PersistentEntity;
import br.com.webbudget.infrastructure.jpa.ColorConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * The representation of a cost center in the application
 *
 * @author Arthur Gregorio
 *
 * @version 1.1.0
 * @since 1.0.0, 28/03/2014
 */
@Entity
@Audited
@Table(name = "cost_centers")
@ToString(callSuper = true, of = "name")
@AuditTable(value = "audit_cost_centers")
@EqualsAndHashCode(callSuper = true, of = "name")
public class CostCenter extends PersistentEntity {

    @Getter
    @Setter
    @NotEmpty(message = "{cost-center.name}")
    @Column(name = "name", nullable = false, length = 90)
    private String name;
    @Getter
    @Setter
    @Convert(converter = ColorConverter.class)
    @Column(name = "color", nullable = false, length = 20)
    private Color color;
    @Getter
    @Setter
    @NotNull(message = "{cost-center.expenses-budget}")
    @Column(name = "expenses_budget", nullable = false)
    private BigDecimal expensesBudget;
    @Getter
    @Setter
    @NotNull(message = "{cost-center.revenues-budget}")
    @Column(name = "revenues_budget", nullable = false)
    private BigDecimal revenuesBudget;
    @Getter
    @Setter
    @Column(name = "blocked")
    private boolean blocked;
    @Getter
    @Setter
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "id_parent")
    private CostCenter parent;

    @Setter
    @Getter
    @Transient
    private BigDecimal percentage;
    @Setter
    @Getter
    @Transient
    private BigDecimal totalMovements;

    /**
     * Default constructor
     */
    public CostCenter() {
        this.color = Color.randomize();
        this.percentage = BigDecimal.ZERO;
        this.totalMovements = BigDecimal.ZERO;
        this.revenuesBudget = BigDecimal.ZERO;
        this.expensesBudget = BigDecimal.ZERO;
    }
    
    /**
     * Get the parent cost center name
     *
     * @return the name of the parent cost center
     */
    public String getParentName() {
        return this.parent != null ? this.parent.getName() : null;
    }
    
    /**
     * Method used to check if this cost center control the budget by the type of a {@link MovementClass}
     *
     * @param classType the {@link MovementClassType} to determine which budget we should consume
     * @return <code>true</code> for a cost center that control budget or <code>false</code> otherwise
     */
    public boolean controlBudget(MovementClassType classType) {
        if (classType == MovementClassType.IN) {
            return this.revenuesBudget.compareTo(BigDecimal.ZERO) > 0;
        } else {
            return this.expensesBudget.compareTo(BigDecimal.ZERO) > 0;
        }
    }
}
