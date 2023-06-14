package br.com.webbudget.domain.entities.financial;

import br.com.webbudget.domain.entities.PersistentEntity;
import br.com.webbudget.domain.entities.registration.Card;
import br.com.webbudget.domain.entities.registration.Wallet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL;
import static br.com.webbudget.infrastructure.utils.DefaultSchemes.FINANCIAL_AUDIT;

@Entity
@Audited
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "payments", schema = FINANCIAL)
@AuditTable(value = "payments", schema = FINANCIAL_AUDIT)
//@NoArgsConstructor
@AllArgsConstructor
public class Payment extends PersistentEntity {

    @NotNull(message = "{payment.paid-on}")
    @Column(name = "paid_on", nullable = false)
    private LocalDate paidOn;

    @Column(name = "discount")
    private BigDecimal discount;

    @Column(name = "paid_value", nullable = false)
    private BigDecimal paidValue;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "{payment.payment-method}")
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "id_card")
    private Card card;

    @ManyToOne
    @JoinColumn(name = "id_wallet")
    private Wallet wallet;

    public Payment() {
        this.paidOn = LocalDate.now();
        this.discount = BigDecimal.ZERO;
        this.paymentMethod = PaymentMethod.CASH;
    }

    public boolean isPaidWithCash() {
        return this.paymentMethod == PaymentMethod.CASH;
    }

    public boolean isPaidWithCreditCard() {
        return this.paymentMethod == PaymentMethod.CREDIT_CARD;
    }

    public boolean isPaidWithDebitCard() {
        return this.paymentMethod == PaymentMethod.DEBIT_CARD;
    }

    public Wallet getDebitCardWallet() {
        return this.card.getWallet();
    }
}
