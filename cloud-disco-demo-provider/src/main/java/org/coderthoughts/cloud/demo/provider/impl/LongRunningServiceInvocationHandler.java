package org.coderthoughts.cloud.demo.provider.impl;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.cxf.dosgi.dsw.ClientContext;
import org.apache.cxf.dosgi.dsw.RemoteServiceInvocationHandler;
import org.coderthoughts.cloud.demo.api.LongRunningService;
import org.osgi.framework.ServiceReference;

public class LongRunningServiceInvocationHandler implements RemoteServiceInvocationHandler<LongRunningService> {
    ConcurrentMap<String, Object> activeClients = new ConcurrentHashMap<String, Object>();

    @Override
    public Object invoke(ClientContext client, ServiceReference reference, Method method, Object[] args) throws Exception {
        if (activeClients.putIfAbsent(client.getHostIPAddress(), Boolean.TRUE) != null)
            throw new RuntimeException("Only 1 concurrent invocation allowed per client.");

        try {
            return method.invoke(new LongRunningServiceImpl(), args);
        } finally {
            activeClients.remove(client.getHostIPAddress());
        }
    }
}
