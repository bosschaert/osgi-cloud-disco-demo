package org.coderthoughts.cloud.demo.provider.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.dosgi.dsw.ClientInfo;
import org.coderthoughts.cloud.framework.service.api.FrameworkStatusAddition;

public class FrameworkStatusAdditionImpl implements FrameworkStatusAddition {
    final Map<String, Object> props; //  = new HashMap<String, Object>();
    {
        props = new HashMap<String, Object>();
        props.put("org.coderthoughts.my-application.role", "data-store");
    }

    @Override
    public String[] getAdditionalPropertyKeys() {
        return props.keySet().toArray(new String [] {});
    }

    @Override
    public Object getAdditionalProperty(String key) {
        return props.get(key);
    }

    @Override
    public String[] getFrameworkVariableNames() {
        return null;
    }

    @Override
    public String getFrameworkVariable(String name, ClientInfo client) {
        return null;
    }

    @Override
    public String[] getServiceVariableNames() {
        return null;
    }

    @Override
    public String getServiceVariable(long serviceID, String name, ClientInfo client) {
        return null;
    }
}
