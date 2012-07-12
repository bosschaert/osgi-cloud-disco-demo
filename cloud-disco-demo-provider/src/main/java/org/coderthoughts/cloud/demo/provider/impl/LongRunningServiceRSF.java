package org.coderthoughts.cloud.demo.provider.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.cxf.dosgi.dsw.RemoteServiceFactory;
import org.osgi.framework.ServiceReference;

public class LongRunningServiceRSF implements RemoteServiceFactory {
    ConcurrentMap<String, Object> activeClients = new ConcurrentHashMap<String, Object>();

    @Override
    public Object getService(String clientIP, ServiceReference reference) {
        if (activeClients.putIfAbsent(clientIP, Boolean.TRUE) != null)
            throw new RuntimeException("Only 1 concurrent invocation allowed per client.");

        return new LongRunningServiceImpl();
    }

    @Override
    public void ungetService(String clientIP, ServiceReference reference, Object service) {
        activeClients.remove(clientIP);
    }
}