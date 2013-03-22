package org.coderthoughts.cloud.demo.provider.impl;

import org.apache.cxf.dosgi.dsw.ClientInfo;
import org.coderthoughts.cloud.framework.service.api.FrameworkStatusAddition;

public class FrameworkStatusAdditionImpl implements FrameworkStatusAddition {
    @Override
    public String getFrameworkVariable(String name, ClientInfo client) {
        return null;
    }
}
