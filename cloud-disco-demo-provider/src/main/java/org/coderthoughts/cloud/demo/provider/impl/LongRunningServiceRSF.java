package org.coderthoughts.cloud.demo.provider.impl;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.cxf.dosgi.dsw.ClientContext;
import org.apache.cxf.dosgi.dsw.RemoteServiceInvocationHandler;
import org.osgi.framework.ServiceReference;

public class LongRunningServiceRSF implements RemoteServiceInvocationHandler {
    ConcurrentMap<String, Object> activeClients = new ConcurrentHashMap<String, Object>();

    @Override
    public Object invoke(ClientContext client, ServiceReference reference, Method method, Object[] args) {
        return null;
    }

    /**
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
    **/
}
