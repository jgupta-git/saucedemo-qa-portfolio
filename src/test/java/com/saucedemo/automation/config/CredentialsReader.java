package com.saucedemo.automation.config;

import com.saucedemo.automation.dto.LoginCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Loads login credentials from saucedemo-credentials.properties on the
 * classpath, so no test data lives in source code or feature files. That
 * file is gitignored - see saucedemo-credentials.properties.example for
 * the template.
 */
public final class CredentialsReader {

    private static final String FILE_NAME = "saucedemo-credentials.properties";

    public static LoginCredentials loadCredentials() {
        Properties props = new Properties();
        try (InputStream in = CredentialsReader.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (in == null) {
                throw new IllegalStateException(FILE_NAME + " not found on the classpath. Copy "
                        + FILE_NAME + ".example to " + FILE_NAME + " (same folder) and fill in real values.");
            }
            props.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + FILE_NAME, e);
        }

        String username = props.getProperty("saucedemo.username");
        String password = props.getProperty("saucedemo.password");
        return new LoginCredentials(username, password);
    }

    private CredentialsReader() {
    }
}
