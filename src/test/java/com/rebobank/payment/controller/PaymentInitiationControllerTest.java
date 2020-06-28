package com.rebobank.payment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebobank.payment.model.PaymentInitiationRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentInitiationControllerTest
{

    // @Mock
    // private PaymentInitiationService mockPaymentInitiationService;

    @Autowired(required = true)
    private MockMvc mockMvc;

    @Autowired(required = true)
    private ObjectMapper objectMapper;

    private String signatureCertificate;

    private String signature;

    private String xRequestId;

    @BeforeEach
    public void setUp()
    {
        signatureCertificate = "MIIDwjCCAqoCCQDxVbCjIKynQjANBgkqhkiG9w0BAQsFADCBojELMAkGA1UEBhMC\r\n"
                + "TkwxEDAOBgNVBAgMB1V0cmVjaHQxEDAOBgNVBAcMB1V0cmVjaHQxETAPBgNVBAoM\r\n"
                + "CFJhYm9iYW5rMRMwEQYDVQQLDApBc3Nlc3NtZW50MSIwIAYDVQQDDBlTYW5kYm94\r\n"
                + "LVRQUDpleGNlbGxlbnQgVFBQMSMwIQYJKoZIhvcNAQkBFhRuby1yZXBseUByYWJv\r\n"
                + "YmFuay5ubDAeFw0yMDAxMzAxMzIyNDlaFw0yMTAxMjkxMzIyNDlaMIGiMQswCQYD\r\n"
                + "VQQGEwJOTDEQMA4GA1UECAwHVXRyZWNodDEQMA4GA1UEBwwHVXRyZWNodDERMA8G\r\n"
                + "A1UECgwIUmFib2JhbmsxEzARBgNVBAsMCkFzc2Vzc21lbnQxIjAgBgNVBAMMGVNh\r\n"
                + "bmRib3gtVFBQOmV4Y2VsbGVudCBUUFAxIzAhBgkqhkiG9w0BCQEWFG5vLXJlcGx5\r\n"
                + "QHJhYm9iYW5rLm5sMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAryLy\r\n"
                + "ouTQr1dvMT4qvek0eZsh8g0DQQLlOgBzZwx7iInxYEAgMNxCKXiZCbmWHBYqh6lp\r\n"
                + "Ph+BBmrnBQzB+qrSNIyd4bFhfUlQ+htK08yyL9g4nyLt0LeKuxoaVWpInrB5FRzo\r\n"
                + "EY5PPpcEXSObgr+pM71AvyJtQLxZbqTao4S7TRKecUm32Wwg+FWY/StSKlox3QmE\r\n"
                + "axEGU7aPkaQfQs4hrtuUePwKrbkQ2hQdMpvI5oXRWzTqafvEQvND+IyLvZRqf0TS\r\n"
                + "vIwsgtJd2tch2kqPoUwng3AmUFleJbMjFNzrWM7TH9LkKPItYtSuMTzeSe9o0SmX\r\n"
                + "ZFgcEBh5DnETZqIVuQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQASFOkJiKQuL7fS\r\n"
                + "ErH6y5Uwj9WmmQLFnit85tjbo20jsqseTuZqLdpwBObiHxnBz7o3M73PJAXdoXkw\r\n"
                + "iMVykZrlUSEy7+FsNZ4iFppoFapHDbfBgM2WMV7VS6NK17e0zXcTGySSRzXsxw0y\r\n"
                + "EQGaOU8RJ3Rry0HWo9M/JmYFrdBPP/3sWAt/+O4th5Jyk8RajN3fHFCAoUz4rXxh\r\n"
                + "UZkf/9u3Q038rRBvqaA+6c0uW58XqF/QyUxuTD4er9veCniUhwIX4XBsDNxIW/rw\r\n"
                + "BRAxOUkG4V+XqrBb75lCyea1o/9HIaq1iIKI4Day0piMOgwPEg1wF383yd0x8hRW\r\n"
                + "4zxyHcER";

        signature = "AlFr/WbYiekHmbB6XdEO/7ghKd0n6q/bapENAYsL86KoYHqa4eP34xfH9icpQRmTpH0qOkt1vfUP\r\n"
                + "Wnaqu+vHBWx/gJXiuVlhayxLZD2w41q8ITkoj4oRLn2U1q8cLbjUtjzFWX9TgiQw1iY0ezpFqyDL\r\n"
                + "PU7+ZzO01JI+yspn2gtto0XUm5KuxUPK24+xHD6R1UZSCSJKXY1QsKQfJ+gjzEjrtGvmASx1SUrp\r\n"
                + "myzVmf4qLwFB1ViRZmDZFtHIuuUVBBb835dCs2W+d7a+icGOCtGQbFcHvW0FODibnY5qq8v5w/P9\r\n"
                + "i9PSarDaGgYb+1pMSnF3p8FsHAjk3Wccg2a1GQ==";

        xRequestId = "29318e25-cebd-498c-888a-f77672f66449";
    }

    // @Test
    public void initiatePayment() throws Exception
    {
        final PaymentInitiationRequest request = new PaymentInitiationRequest("91ABNA0417164",
                "NL91ABNA0417164302", "0", "EUR", "U1000");

        mockMvc.perform(MockMvcRequestBuilders.post("/initiate-payment")
                .content(objectMapper.writeValueAsString(request))
                .header("Signature-Certificate", signatureCertificate)
                .header("Signature", signature).header("X-Request-Id", xRequestId)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // subject.initiatePayment(request);

    }
}
