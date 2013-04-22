package org.coderthoughts.cloud.demo.provider.impl;

import org.apache.cxf.dosgi.dsw.ClientInfo;
import org.coderthoughts.cloud.framework.service.api.FrameworkNodeAddition;

public class FrameworkStatusAdditionImpl implements FrameworkNodeAddition {
    @Override
    public String getFrameworkVariable(String name, ClientInfo client) {
        if ("network.load".equals(name)) {
            return "42";
        }
        throw new IllegalArgumentException(name);
    }
}
