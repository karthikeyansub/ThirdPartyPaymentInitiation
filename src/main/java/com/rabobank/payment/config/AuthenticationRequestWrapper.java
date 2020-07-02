package com.rabobank.payment.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationRequestWrapper extends HttpServletRequestWrapper
{

    private final String payload;

    public AuthenticationRequestWrapper(HttpServletRequest request)
    {
        super(request);

        // read the original payload into the payload variable
        StringBuilder stringBuilder = new StringBuilder();
        // read the payload into the StringBuilder
        try (InputStream inputStream = request.getInputStream()) {

            if (inputStream != null) {
                try (BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream))) {
                    char[] charBuffer = new char[128];
                    int bytesRead = -1;
                    while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                        stringBuilder.append(charBuffer, 0, bytesRead);
                    }
                }
            } else {
                // make an empty string since there is no payload
                stringBuilder.append("");
            }
        } catch (IOException exception) {
            log.error("Exception while read the request body");
        }

        payload = stringBuilder.toString();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                payload.getBytes());
        return new ServletInputStream()
        {
            @Override
            public boolean isFinished()
            {
                return false;
            }

            @Override
            public boolean isReady()
            {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener)
            {
                // ignore
            }

            public int read() throws IOException
            {
                return byteArrayInputStream.read();
            }
        };
    }
}
