
package com.rebobank.payment.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Scanner;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebobank.payment.config.AuthenticationRequestWrapper;
import com.rebobank.payment.constant.PaymentInitiationConstant;
import com.rebobank.payment.exception.GenericException;
import com.rebobank.payment.exception.InvalidSignatureException;
import com.rebobank.payment.exception.UnknownCertificateException;
import com.rebobank.payment.model.PaymentRejectedResponse;
import com.rebobank.payment.util.ErrorReasonCode;
import com.rebobank.payment.util.TransactionStatus;

import sun.security.provider.X509Factory;

/**
 * Customer X.509 authentication filter
 */
public class CustomX509AuthenticationFilter extends X509AuthenticationFilter
{

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CustomX509AuthenticationFilter.class);

    private final HttpSecurity http;

    public CustomX509AuthenticationFilter(HttpSecurity http)
    {
        this.http = http;
    }

    @Override
    @SuppressWarnings("resource")
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request)
    {
        String signatureInput = request
                .getHeader(PaymentInitiationConstant.REQUEST_PARAM_NAME_SIGNATURE);
        String xRequestId = request
                .getHeader(PaymentInitiationConstant.REQUEST_PARAM_NAME_X_REQUEST_ID);
        String body = "";

        byte[] requestId = xRequestId.getBytes();
        try {
            if ("POST".equalsIgnoreCase(request.getMethod())) {
                Scanner scanner = new Scanner(request.getInputStream(), "UTF-8")
                        .useDelimiter("\\A");
                body = scanner.hasNext() ? scanner.next() : "";

                LOGGER.debug("Request body -> {}", body);
            }

            byte[] payloadMsg = getDigestMessage(body);
            byte[] concatBytes = new byte[requestId.length + payloadMsg.length];
            System.arraycopy(requestId, 0, concatBytes, 0, requestId.length);
            System.arraycopy(payloadMsg, 0, concatBytes, requestId.length, payloadMsg.length);
            Base64.getEncoder().encodeToString(concatBytes);

            setAuthenticationManager(this.http.getSharedObject(AuthenticationManager.class));
            X509Certificate x509Certificate = parseCertificate(request);
            PublicKey publicKey = x509Certificate.getPublicKey();

            X500Principal principal = x509Certificate.getSubjectX500Principal();

            String commonName = null;
            final LdapName ln = new LdapName(principal.getName());
            for (Rdn rdn : ln.getRdns()) {
                if (rdn.getType().equalsIgnoreCase("CN")) {
                    LOGGER.debug("Common name -> {}", rdn.getValue());
                    commonName = (String) rdn.getValue();
                    break;
                }
            }

            // White list validation
            if (null == commonName || !commonName.startsWith("Sandbox-TPP")) {
                throw new UnknownCertificateException("Unknown Certification");
            }

            byte[] signatureBytes = Base64.getDecoder()
                    .decode(signatureInput.getBytes(StandardCharsets.UTF_8));

            Signature signature = Signature
                    .getInstance(PaymentInitiationConstant.ALGORITHM_SHA256WITHRSA);

            signature.initVerify(publicKey);

            signature.update(concatBytes);

            boolean isSignatureOK = signature.verify(signatureBytes);

            LOGGER.info("The signature is {}{}", (isSignatureOK ? "" : "NOT "), "VALID");
            if (!isSignatureOK) {
                throw new InvalidSignatureException("Invalid Signature");
            }

            return x509Certificate;

        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | IOException
                | InvalidNameException | CertificateException exception) {
            LOGGER.error("Error while parsing the signature and certificate", exception);
            throw new GenericException("Error parsing signature and certificate");
        }
    }

    private X509Certificate parseCertificate(HttpServletRequest request) throws CertificateException
    {
        String certStr = request.getHeader("Signature-Certificate");
        // before decoding we need to get rod off the prefix and suffix
        byte[] decoded = Base64.getDecoder().decode(certStr.replaceAll(X509Factory.BEGIN_CERT, "") // NOSONAR
                .replaceAll(X509Factory.END_CERT, "")); // NOSONAR

        return (X509Certificate) CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(decoded));
    }

    private X509Certificate readClientCertificate(HttpServletRequest request)
    {
        X509Certificate[] certs = (X509Certificate[]) request
                .getAttribute("javax.servlet.request.X509Certificate");

        if (certs != null && certs.length > 0) {
            LOGGER.debug("X.509 client authentication certificate: {}", certs[0]);
            return certs[0];
        } else {
            throw new UnknownCertificateException("Unknown Certificate");
        }
    }

    private byte[] getDigestMessage(String message) throws NoSuchAlgorithmException
    {
        // Creating the MessageDigest object
        MessageDigest md = MessageDigest
                .getInstance(PaymentInitiationConstant.DIGEST_ALGORITHM_SHA256);

        // Passing data to the created MessageDigest Object
        md.update(message.getBytes());

        // Compute the message digest
        return md.digest();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        try {
            ServletRequest requestNew = new AuthenticationRequestWrapper(
                    (HttpServletRequest) request);
            HttpServletResponse responseNew = (HttpServletResponse) response;
            getPreAuthenticatedCredentials((HttpServletRequest) requestNew);
            chain.doFilter(requestNew, responseNew);
        } catch (Exception exception) {
            if (exception instanceof InvalidSignatureException) {// NOSONAR
                setInvaildSignatureErrorResponse(HttpStatus.BAD_REQUEST,
                        (HttpServletResponse) response, exception);
            } else if (exception instanceof UnknownCertificateException) {// NOSONAR
                setUnknownCertificationResponse(HttpStatus.BAD_REQUEST,
                        (HttpServletResponse) response, exception);
            } else if (exception instanceof GenericException) {// NOSONAR
                setGenericErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                        (HttpServletResponse) response, exception);
            }
        }
    }

    private void setInvaildSignatureErrorResponse(HttpStatus status, HttpServletResponse response,
            Throwable ex) throws IOException
    {

        response.setStatus(status.value());
        response.setContentType(PaymentInitiationConstant.CONTENT_TYPE_APPLICATION_JSON);
        PrintWriter writer = response.getWriter();
        PaymentRejectedResponse paymentRejectedResponse = new PaymentRejectedResponse(
                TransactionStatus.Rejected, ex.getLocalizedMessage(),
                ErrorReasonCode.INVALID_SIGNATURE);
        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(paymentRejectedResponse));
        writer.close();
    }

    private void setUnknownCertificationResponse(HttpStatus status, HttpServletResponse response,
            Throwable ex) throws IOException
    {

        response.setStatus(status.value());
        response.setContentType(PaymentInitiationConstant.CONTENT_TYPE_APPLICATION_JSON);
        PrintWriter writer = response.getWriter();
        PaymentRejectedResponse paymentRejectedResponse = new PaymentRejectedResponse(
                TransactionStatus.Rejected, ex.getLocalizedMessage(),
                ErrorReasonCode.UNKNOWN_CERTIFICATE);
        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(paymentRejectedResponse));
        writer.close();

    }

    private void setGenericErrorResponse(HttpStatus status, HttpServletResponse response,
            Throwable ex) throws IOException
    {

        response.setStatus(status.value());
        response.setContentType(PaymentInitiationConstant.CONTENT_TYPE_APPLICATION_JSON);
        PrintWriter writer = response.getWriter();
        PaymentRejectedResponse paymentRejectedResponse = new PaymentRejectedResponse(
                TransactionStatus.Rejected, ex.getLocalizedMessage(),
                ErrorReasonCode.GENERAL_ERROR);
        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(paymentRejectedResponse));
        writer.close();

    }
}