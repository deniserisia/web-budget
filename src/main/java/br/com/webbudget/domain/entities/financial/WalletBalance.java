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
import br.com.webbudget.domain.entities.registration.Wallet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL;
import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL_AUDIT;

@Entity
@Audited
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "wallet_balances", schema = FINANCIAL)
@AuditTable(value = "wallet_balances", schema = FINANCIAL_AUDIT)
public class WalletBalance extends PersistentEntity {

    @Getter
    @Setter
    @Column(name = "actual_balance", nullable = false)
    private BigDecimal actualBalance;
    @Getter
    @Setter
    @Column(name = "old_balance", nullable = false)
    private BigDecimal oldBalance;
    @Getter
    @Setter
    @Column(name = "transaction_value", nullable = false)
    @NotNull(message = "{wallet-balance.transaction-value}")
    private BigDecimal transactionValue;
    @Getter
    @Setter
    @Column(name = "movement_code")
    private String movementCode;
    @Getter
    @Setter
    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;
    @Getter
    @Setter
    @Column(name = "movement_date_time", nullable = false)
    private LocalDateTime movementDateTime;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "balance_type", nullable = false)
    private BalanceType balanceType;
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", nullable = false)
    private ReasonType reasonType;
    
    @Getter
    @Setter
    @ManyToOne
    @NotNull(message = "{wallet-balance.null-wallet}")
    @JoinColumn(name = "id_wallet", nullable = false)
    private Wallet wallet;

    public void processBalances() {
        BigDecimal oldBalance = wallet.getActualBalance();
        BigDecimal newBalance = calculateNewBalance(oldBalance);
        updateBalances(newBalance);
    }

    private BigDecimal calculateNewBalance(BigDecimal oldBalance) {
        return oldBalance.add(transactionValue);
    }

    private void updateBalances(BigDecimal newBalance) {
        oldBalance = wallet.getActualBalance();
        actualBalance = newBalance;
        wallet.setActualBalance(newBalance);
    }
    public LocalDate getMovementDate() {
        return this.movementDateTime.toLocalDate();
    }
    public boolean isOldBalanceNegative() {
        return this.oldBalance.signum() < 0;
    }

    public boolean isActualBalanceNegative() {
        return this.actualBalance.signum() < 0;
    }

    public boolean isTransactionValueNegative() {
        return this.transactionValue.signum() < 0;
    }

    public boolean isDebit() {
        return this.balanceType == BalanceType.DEBIT;
    }

    public boolean isCredit() {
        return this.balanceType == BalanceType.CREDIT;
    }

    public boolean isRevenue() {
        return this.reasonType == ReasonType.REVENUE;
    }

    public boolean isExpense() {
        return this.reasonType == ReasonType.EXPENSE;
    }

    public boolean isDebitCard() {
        return this.reasonType == ReasonType.DEBIT_CARD;
    }
    public boolean isTransference() {
        return this.reasonType == ReasonType.TRANSFERENCE;
    }
    public boolean isAdjustment() {
        return this.reasonType == ReasonType.ADJUSTMENT;
    }
    public boolean isReturn() {
        return this.reasonType == ReasonType.RETURN;
    }



}