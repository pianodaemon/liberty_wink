package com.immortalcrab.meh;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.annotation.PostConstruct;

import org.apache.wink.common.internal.runtime.RuntimeDelegateImpl;

@Singleton
@Startup
@Lock(LockType.READ)
@Path("/platform")
public class PlatformResource {

    @PostConstruct
    public void init()
    {
        RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
    }

    @Path("/platformCertificationDN")
    @GET
    public String getPlatformCertificationDN() {
        return "DummyDN";
    }
}
