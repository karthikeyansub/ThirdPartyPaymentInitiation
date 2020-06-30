
package com.rebobank.payment.filter;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import com.rebobank.payment.constant.PaymentInitiationConstant;
import com.rebobank.payment.exception.InvalidSignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;

/**
 * Customer X.509 authentication filter
 */
public class CustomX509AuthenticationFilter extends X509AuthenticationFilter
{

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomX509AuthenticationFilter.class);

    private final HttpSecurity http;

    public CustomX509AuthenticationFilter(HttpSecurity http)
    {
        this.http = http;
    }

    @Override
    @SuppressWarnings("resource")
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request)
    {
        String signatureInput = request.getHeader(PaymentInitiationConstant.REQUEST_PARAM_NAME_SIGNATURE);
        String xRequestId = request.getHeader(PaymentInitiationConstant.REQUEST_PARAM_NAME_X_REQUEST_ID);
        String body = "";

        byte[] requestId = xRequestId.getBytes();
        try
        {
            if ("POST".equalsIgnoreCase(request.getMethod()))
            {
                Scanner scanner = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
                body = scanner.hasNext() ? scanner.next() : "";
            }

            byte[] payloadMsg = getDigestMessage(body);
            byte[] concatBytes = new byte[requestId.length + payloadMsg.length];
            System.arraycopy(requestId, 0, concatBytes, 0, requestId.length);
            System.arraycopy(payloadMsg, 0, concatBytes, requestId.length, payloadMsg.length);
            Base64.getEncoder().encodeToString(concatBytes);

            setAuthenticationManager(this.http.getSharedObject(AuthenticationManager.class));
            X509Certificate x509Certificate = extractClientCertificate(request);
            PublicKey publicKey = x509Certificate.getPublicKey();

            byte[] signatureBytes = Base64.getDecoder().decode(signatureInput.getBytes("utf-8"));

            Signature signature = Signature.getInstance(PaymentInitiationConstant.ALGORITHM_SHA256WITHRSA);

            signature.initVerify(publicKey);

            signature.update(concatBytes);

            boolean isSignatureOK = signature.verify(signatureBytes);

            LOGGER.info("The signature is " + (isSignatureOK ? "" : "NOT ") + "VALID");
            if(!isSignatureOK)
            {
                throw new InvalidSignatureException("Invalid Signature");
            }
            
            return x509Certificate;

        }
        catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | IOException exception)
        {
            LOGGER.error("INVALID_SIGNATURE", exception);
            throw new InvalidSignatureException("Invalid Signature");
        }
    }
    
    private X509Certificate extractClientCertificate(HttpServletRequest request)
    {
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

        if (certs != null && certs.length > 0)
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("X.509 client authentication certificate:" + certs[0]);
            }
        }
        return certs[0];
    }

    private byte[] getDigestMessage(String message) throws NoSuchAlgorithmException
    {
        // Creating the MessageDigest object
        MessageDigest md = MessageDigest.getInstance(PaymentInitiationConstant.DIGEST_ALGORITHM_SHA256);

        // Passing data to the created MessageDigest Object
        md.update(message.getBytes());

        // Compute the message digest
        return md.digest();
    }
}