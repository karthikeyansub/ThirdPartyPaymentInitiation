package com.rebobank.payment.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.rebobank.payment.util.ErrorReasonCode;
import com.rebobank.payment.util.TransactionStatus;

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
public class PaymentRejectedResponse implements Serializable
{
    private static final long serialVersionUID = 8002177112886324277L;

    @NotNull
    private TransactionStatus status;

    @NotNull
    private String reason;

    @NotNull
    private ErrorReasonCode reasonCode;
}
