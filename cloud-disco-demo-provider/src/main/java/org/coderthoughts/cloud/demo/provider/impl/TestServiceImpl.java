package org.coderthoughts.cloud.demo.provider.impl;

import org.coderthoughts.cloud.demo.api.TestService;

public class TestServiceImpl implements TestService {
    @Override
    public String doit(String s) {
        System.out.println("Received: " + s);
        return new StringBuilder(s).reverse().toString();
    }
}
