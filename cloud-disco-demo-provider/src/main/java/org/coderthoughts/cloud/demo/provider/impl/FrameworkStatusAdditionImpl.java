package org.coderthoughts.cloud.demo.provider.impl;

import org.apache.cxf.dosgi.dsw.ClientInfo;
import org.coderthoughts.cloud.framework.service.api.FrameworkStatusAddition;

public class FrameworkStatusAdditionImpl implements FrameworkStatusAddition {
    @Override
    public String getFrameworkVariable(String name, ClientInfo client) {
        if ("network.load".equals(name)) {
            return "42";
        }
        throw new IllegalArgumentException(name);
    }

    @Override
    public String getServiceVariable(long id, String name, ClientInfo client) {
        throw new UnsupportedOperationException();
    }
}
