package org.coderthoughts.cloud.demo.provider.impl;

import org.apache.cxf.dosgi.dsw.ClientContext;
import org.coderthoughts.cloud.framework.service.api.FrameworkNodeAddition;

public class FrameworkNodeAdditionImpl implements FrameworkNodeAddition {
    @Override
    public String getFrameworkVariable(String name, ClientContext client) {
        if ("network.load".equals(name)) {
            return "42";
        }
        throw new IllegalArgumentException(name);
    }
}
