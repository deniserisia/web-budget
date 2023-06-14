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
import br.com.webbudget.domain.entities.registration.FinancialPeriod;
import br.com.webbudget.infrastructure.utils.RandomCode;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL;
import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL_AUDIT;

@Data
@Entity
@Audited
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "closings", schema = FINANCIAL)
@AuditTable(value = "closings", schema = FINANCIAL_AUDIT)
public class Closing extends PersistentEntity {

    @Column(name = "revenues", nullable = false)
    private BigDecimal revenues;

    @Column(name = "expenses", nullable = false)
    private BigDecimal expenses;

    @Column(name = "credit_card_expenses", nullable = false)
    private BigDecimal creditCardExpenses;

    @Column(name = "debit_card_expenses", nullable = false)
    private BigDecimal debitCardExpenses;

    @Column(name = "cash_expenses", nullable = false)
    private BigDecimal cashExpenses;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "accumulated", nullable = false)
    private BigDecimal accumulated;

    @Column(name = "closing_date", nullable = false)
    private LocalDate closingDate;

    @OneToOne
    @JoinColumn(name = "id_financial_period")
    private FinancialPeriod financialPeriod;

    public Closing() {
        this.balance = BigDecimal.ZERO;
        this.expenses = BigDecimal.ZERO;
        this.revenues = BigDecimal.ZERO;
        this.accumulated = BigDecimal.ZERO;
        this.creditCardExpenses = BigDecimal.ZERO;
        this.debitCardExpenses = BigDecimal.ZERO;
        this.closingDate = LocalDate.now();
    }
}