
package com.rebobank.payment.constant;

public interface PaymentInitiationConstant
{

    public static final String IBAN_PATTERN = "[A-Z]{2}[0-9]{2}[a-zA-Z0-9]{1,30}";

    public static final String AMOUNT_PATTERN = "-?[0-9]+(\\.[0-9]{1,3})?";

    public static final String CURRENCY_PATTERN = "[A-Z]{3}";

    public static final String REQUEST_PARAM_NAME_X_REQUEST_ID = "X-Request-Id";

    public static final String REQUEST_PARAM_NAME_SIGNATURE = "Signature";

    public static final String REQUEST_PARAM_NAME_SIGNATURE_CERTIFICATE = "Signatur-Certificate";

    public static final String ALGORITHM_SHA256WITHRSA = "SHA256WithRSA";

    public static final String DIGEST_ALGORITHM_SHA256 = "SHA-256";

}
