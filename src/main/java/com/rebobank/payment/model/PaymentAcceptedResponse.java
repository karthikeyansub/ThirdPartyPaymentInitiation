package com.rebobank.payment.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.rebobank.payment.util.TransactionStatus;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//TODO: need to check whether to use JSON null check or javax validation check
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PaymentAcceptedResponse implements Serializable
{
    private static final long serialVersionUID = -2517992590197862432L;

    @NotNull
    private String paymentId;

    @NotNull
    private TransactionStatus status;

}
