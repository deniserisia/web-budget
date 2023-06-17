/*
 * Copyright (C) 2016 Arthur Gregorio, AG.Software
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
package br.com.webbudget.domain.entities.journal;

import br.com.webbudget.domain.entities.PersistentEntity;
import br.com.webbudget.domain.entities.financial.Movement;
import br.com.webbudget.domain.entities.financial.PeriodMovement;
import br.com.webbudget.domain.entities.registration.CostCenter;
import br.com.webbudget.domain.entities.registration.FinancialPeriod;
import br.com.webbudget.domain.entities.registration.MovementClass;
import br.com.webbudget.domain.entities.registration.Vehicle;
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
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.webbudget.infrastructure.utils.DefaultSchemes.JOURNAL;
import static br.com.webbudget.infrastructure.utils.DefaultSchemes.JOURNAL_AUDIT;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

/**
 * The representation of a {@link Vehicle} refueling
 *
 * @author Arthur Gregorio
 *
 * @version 1.0.0
 * @since 2.3.0, 27/06/2016
 */

@Entity
@Audited
@Table(name = "refuelings", schema = JOURNAL)
@AuditTable(value = "refuelings", schema = JOURNAL_AUDIT)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = "fuels")
public class Refueling extends PersistentEntity {
    @Column(name = "code", length = 6, unique = true)
    private String code;

    @Column(name = "accounted", nullable = false)
    private boolean accounted;

    @Column(name = "accounted_by")
    private String accountedBy;

    @Column(name = "first_refueling", nullable = false)
    private boolean firstRefueling;

    @Column(name = "full_tank", nullable = false)
    private boolean fullTank;

    @NotNull(message = "{refueling.odometer}")
    @Column(name = "odometer", nullable = false)
    private Long odometer;

    @Column(name = "distance", nullable = false)
    private Long distance;

    @Column(name = "average_consumption")
    private BigDecimal averageConsumption;

    @Column(name = "liters", nullable = false)
    private BigDecimal liters;

    @Column(name = "cost", nullable = false)
    private BigDecimal cost;

    @Column(name = "cost_per_liter", nullable = false)
    private BigDecimal costPerLiter;

    @Column(name = "place", length = 90)
    private String place;

    @NotNull(message = "{refueling.event-date}")
    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @OneToOne
    @JoinColumn(name = "id_period_movement")
    private PeriodMovement periodMovement;

    @ManyToOne(optional = false)
    @NotNull(message = "{refueling.vehicle}")
    @JoinColumn(name = "id_vehicle", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(optional = false)
    @NotNull(message = "{refueling.movement-class}")
    @JoinColumn(name = "id_movement_class", nullable = false)
    private MovementClass movementClass;

    @ManyToOne(optional = false)
    @NotNull(message = "{refueling.financial-period}")
    @JoinColumn(name = "id_financial_period", nullable = false)
    private FinancialPeriod financialPeriod;

    @OneToMany(mappedBy = "refueling", orphanRemoval = true, fetch = EAGER, cascade = {PERSIST, REMOVE})
    private List<Fuel> fuels = new ArrayList<>();

    public Refueling() {
        this.code = RandomCode.alphanumeric(6);
        this.fullTank = true;
        this.accounted = false;
        this.eventDate = LocalDate.now();
        this.cost = BigDecimal.ZERO;
        this.liters = BigDecimal.ZERO;
        this.costPerLiter = BigDecimal.ZERO;
    }

    public List<Fuel> getFuels() {
        return Collections.unmodifiableList(this.fuels);
    }

    public void addFuel() {
        this.fuels.add(new Fuel(this));
        totalsFuels();
    }

    public void deleteFuel(Fuel fuel) {
        this.fuels.remove(fuel);
        totalsFuels();
    }


    private void totalsFuels() {
        calculateTotalCost();
        calculateTotalLiters();
        calculateCostPerLiter();
    }
    public String getVehicleIdentification() {
        return vehicle.getIdentification();
    }

    public CostCenter getCostCenter() {
        return vehicle.getCostCenter();
    }

    public String getMovementDescription() {
        return vehicle.getIdentification() + " - " + movementClass.getName() + ", " +
                NumberFormat.getNumberInstance().format(liters) + "lts";
    }

    public void updateVehicleOdometer() {
        vehicle.setOdometer(odometer);
    }

    public void calculateDistance(long lastOdometer) {
        distance = firstRefueling ? 0 : odometer - lastOdometer;
    }

    public boolean isFinancialMovementPresent() {
        return periodMovement != null;
    }

    private void calculateTotalCost() {
        cost = fuels.stream()
                .map(Fuel::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void calculateTotalLiters() {
        liters = fuels.stream()
                .map(Fuel::getLiters)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void calculateCostPerLiter() {
        if (!cost.equals(BigDecimal.ZERO) && !liters.equals(BigDecimal.ZERO)) {
            costPerLiter = cost.divide(liters, RoundingMode.CEILING);
        } else {
            costPerLiter = BigDecimal.ZERO;
        }
    }
}
