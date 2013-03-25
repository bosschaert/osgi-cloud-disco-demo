package org.coderthoughts.cloud.demo.provider.impl;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.cxf.dosgi.dsw.ClientInfo;
import org.apache.cxf.dosgi.dsw.RemoteServiceFactory;
import org.osgi.framework.ServiceReference;

public class LongRunningServiceRSF implements RemoteServiceFactory {
    ConcurrentMap<String, Object> activeClients = new ConcurrentHashMap<String, Object>();

    @Override
    public Object getService(ClientInfo clientIP, ServiceReference reference, Method method, Object[] args) {
        if (activeClients.putIfAbsent(clientIP.getHostIPAddress(), Boolean.TRUE) != null)
            throw new RuntimeException("Only 1 concurrent invocation allowed per client.");

        return new LongRunningServiceImpl();
    }

    @Override
    public void ungetService(ClientInfo clientIP, ServiceReference reference, Object service, Method method, Object[] args, Object rv) {
        activeClients.remove(clientIP);
    }
}
