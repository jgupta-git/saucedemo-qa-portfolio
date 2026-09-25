package com.saucedemo.automation.config;

import com.saucedemo.automation.dto.LoginCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Loads login credentials from saucedemo-credentials.properties on the
 * classpath, so no test data lives in source code or feature files. The
 * file is checked into the repo since SauceDemo's test credentials are
 * publicly documented on its own login page - nothing here is secret.
 */
public final class CredentialsReader {

    private static final String FILE_NAME = "saucedemo-credentials.properties";

    public static LoginCredentials loadCredentials() {
        Properties props = new Properties();
        try (InputStream in = CredentialsReader.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (in == null) {
                throw new IllegalStateException(FILE_NAME + " not found on the classpath. It should be "
                        + "checked into src/test/resources/ - check the build actually pulled it in.");
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
