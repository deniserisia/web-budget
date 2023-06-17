package br.com.webbudget.domain.entities.registration;

import br.com.webbudget.domain.entities.PersistentEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static br.com.webbudget.infrastructure.utils.DefaultSchemes.REGISTRATION;
import static br.com.webbudget.infrastructure.utils.DefaultSchemes.REGISTRATION_AUDIT;

/**
 * Represents a movement class in the application.
 * Stores information about the class name, budget, active status, type, cost center, and total movements.
 * Handles operations related to the class.
 *
 * This class is used to define various types of movements, such as revenue or expenses.
 * Each movement class is associated with a cost center.
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 1.0.0, 04/03/2014
 */
@Entity
@Audited
@ToString(callSuper = true, exclude = "totalMovements")
@Table(name = "movement_classes", schema = REGISTRATION)
@EqualsAndHashCode(callSuper = true, exclude = "totalMovements")
@AuditTable(value = "movement_classes", schema = REGISTRATION_AUDIT)
public class MovementClass extends PersistentEntity {

    @Getter
    @Setter
    @NotBlank(message = "{movement-class.name}")
    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Getter
    @Setter
    @Column(name = "budget")
    @NotNull(message = "{movement-class.budget}")
    private BigDecimal budget;

    @Getter
    @Setter
    @Column(name = "active", nullable = false)
    private boolean active;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{movement-class.movement-class-type}")
    @Column(name = "movement_class_type", nullable = false, length = 45)
    private MovementClassType movementClassType;

    @Getter
    @Setter
    @ManyToOne
    @NotNull(message = "{movement-class.cost-center}")
    @JoinColumn(name = "id_cost_center", nullable = false)
    private CostCenter costCenter;

    @Getter
    @Setter
    @Transient
    private BigDecimal totalMovements;

    /**
     * Default constructor.
     * Sets the initial values for active, budget, and totalMovements.
     */
    public MovementClass() {
        this.active = true;
        this.budget = BigDecimal.ZERO;
        this.totalMovements = BigDecimal.ZERO;
    }

    /**
     * Checks if this is a revenue class.
     *
     * @return true if it is a revenue class, false otherwise
     */
    public boolean isRevenue() {
        return this.movementClassType == MovementClassType.REVENUE;
    }

    /**
     * Checks if this is an expense class.
     *
     * @return true if it is an expense class, false otherwise
     */
    public boolean isExpense() {
        return this.movementClassType == MovementClassType.EXPENSE;
    }

    /**
     * Checks if the budget of this class is exceeded.
     *
     * @return true if the budget is exceeded, false otherwise
     */
    public boolean isOverBudget() {
        return this.totalMovements.compareTo(this.budget) >= 0;
    }

    /**
     * Calculates the budget completion percentage for this class.
     *
     * @return the budget completion percentage
     */
    public int budgetCompletionPercentage() {
        BigDecimal percentage = BigDecimal.ZERO;

        if (this.isOverBudget()) {
            return 100;
        } else if (this.budget.equals(BigDecimal.ZERO)) {
            percentage = this.totalMovements.multiply(new BigDecimal(100))
                    .divide(this.budget, 2, RoundingMode.HALF_UP);
        }
        return percentage.intValue() > 100 ? 100 : percentage.intValue();
    }
}
