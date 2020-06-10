/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.openobject.blog.tutorial.meecrowave;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import javax.enterprise.context.Dependent;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;

@Dependent
@ApplicationPath("tutorial")
@OpenAPIDefinition(
        info = @Info(title = "Customer API",
                contact = @Contact(name = "Francois Papon", email = "fpapon@github.com"),
                version = "1.0.0")
)
public class MeecrowaveApplication extends Application {

    @Produces
    @Liveness
    HealthCheck checkMemory() {
        MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
        return () -> HealthCheckResponse.named("heap-memory").state(mxbean.getHeapMemoryUsage().getUsed() < 0.9).build();
    }

    @Produces
    @Readiness
    HealthCheck checkCpu() {
        ThreadMXBean mxbean = ManagementFactory.getThreadMXBean();
        return () -> HealthCheckResponse.named("cpu-usage").state(mxbean.getCurrentThreadCpuTime() < 0.9).build();
    }

}
