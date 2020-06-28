package com.rebobank.payment.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.rebobank.payment.constant.PaymentInitiationConstant;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PaymentInitiationRequest implements Serializable
{
    private static final long serialVersionUID = 2416661968436935895L;

    @NotNull(message = "Debtor IBAN is null")
    @Pattern(regexp = PaymentInitiationConstant.IBAN_PATTERN, message = "Incorrect Debtor IBAN")
    private String debtorIBAN;

    @NotNull(message = "Creditor IBAN is null")
    @Pattern(regexp = PaymentInitiationConstant.IBAN_PATTERN, message = "Incorrect Creditor IBAN")
    private String creditorIBAN;

    @NotNull(message = "Amount is null")
    @Pattern(regexp = PaymentInitiationConstant.AMOUNT_PATTERN, message = "Invalid Amount")
    private String amount;

    @Pattern(regexp = PaymentInitiationConstant.CURRENCY_PATTERN, message = "Invalid Currency")
    private String currency;

    @NotNull(message = "EndToEndId is null")
    private String endToEndId;

}
