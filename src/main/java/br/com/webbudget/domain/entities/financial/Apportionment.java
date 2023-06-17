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
package br.com.webbudget.domain.entities.financial;

import br.com.webbudget.domain.entities.PersistentEntity;
import br.com.webbudget.domain.entities.registration.CostCenter;
import br.com.webbudget.domain.entities.registration.MovementClass;
import br.com.webbudget.infrastructure.utils.RandomCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL;
import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL_AUDIT;


@Entity
@Audited
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "apportionments", schema = FINANCIAL)
@AuditTable(value = "apportionments", schema = FINANCIAL_AUDIT)
public class Apportionment extends PersistentEntity {

    @Getter
    @Column(name = "code", nullable = false, length = 6, unique = true)
    private String code;
    @Getter
    @Setter
    @NotNull(message = "{apportionment.value}")
    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "id_movement", nullable = false)
    private Movement movement;
    @Getter
    @Setter
    @ManyToOne
    @NotNull(message = "{apportionment.cost-center}")
    @JoinColumn(name = "id_cost_center", nullable = false)
    private CostCenter costCenter;
    @Getter
    @Setter
    @ManyToOne
    @NotNull(message = "{apportionment.movement-class}")
    @JoinColumn(name = "id_movement_class", nullable = false)
    private MovementClass movementClass;

    public Apportionment() {
        this.code = RandomCode.alphanumeric(6);
    }

    public Apportionment(BigDecimal value) {
        this();
        this.value = value;
    }
    public Apportionment(BigDecimal value, MovementClass movementClass) {
        this();
        this.value = value;
        this.movementClass = movementClass;
        this.costCenter = movementClass.getCostCenter();
    }

    public boolean isRevenue() {
        return this.movementClass.isRevenue();
    }

    public boolean isExpense() {
        return this.movementClass.isExpense();
    }

    public String getMovementClassName() {
        return this.movementClass != null ? this.movementClass.getName() : "";
    }
    public String getCostCenterName() {
        return this.costCenter != null ? this.costCenter.getName() : "";
    }

    public boolean isCostCenterAndMovementClassEquals(Apportionment apportionment) {
        return apportionment.getCostCenter().equals(this.costCenter)
                && apportionment.getMovementClass().equals(this.movementClass);
    }
    public static Apportionment copyOf(Apportionment toCopy){
        return new Apportionment(toCopy.getValue(), toCopy.getMovementClass());
    }
}