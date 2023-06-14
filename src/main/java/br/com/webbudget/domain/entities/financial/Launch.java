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
import br.com.webbudget.domain.entities.registration.FinancialPeriod;
import br.com.webbudget.infrastructure.utils.RandomCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;

import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL;
import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL_AUDIT;
@Entity
@Audited
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "launches", schema = FINANCIAL)
@AuditTable(value = "launches", schema = FINANCIAL_AUDIT)
public class Launch extends PersistentEntity {

    @Column(name = "code", nullable = false, length = 6, unique = true)
    private String code;

    @Column(name = "quote_number", length = 6)
    private Integer quoteNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_financial_period", nullable = false)
    private FinancialPeriod financialPeriod;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_fixed_movement", nullable = false)
    private FixedMovement fixedMovement;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_period_movement", nullable = false)
    private PeriodMovement periodMovement;

    public Launch() {
        this.code = RandomCode.alphanumeric(6);
    }

    public boolean isLastQuote() {
        return this.fixedMovement.getTotalQuotes() == this.quoteNumber;
    }

    // Extrair a lógica para um novo método
    public boolean isLastQuoteOfFixedMovement() {
        return fixedMovement.getTotalQuotes().equals(quoteNumber);
    }
}