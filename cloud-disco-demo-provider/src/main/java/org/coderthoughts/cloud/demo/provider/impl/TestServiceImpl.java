package org.coderthoughts.cloud.demo.provider.impl;

import org.coderthoughts.cloud.demo.api.TestService;

public class TestServiceImpl implements TestService {
    private final String uuid;

    TestServiceImpl(String frameworkUUID) {
        uuid = frameworkUUID;
    }

    @Override
    public String doit(String s) {
        System.out.println("Received this message: " + s);
        return "My Framework UUID is: " + uuid;
    }
}
