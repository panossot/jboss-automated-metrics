/*
 * Copyleft 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.metrics;

import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.metrics.automatedmetricsapi.Metric;

/**
 *
 * @author Panagiotis Sotiropoulos
 */
public class MetricsClass {
    private static AtomicInteger countAtomic;
    private static int count = 0;
    
    private static int count2 = 0;

    public MetricsClass(){
        countAtomic = new AtomicInteger(1);
    }
    
    @Metric(fieldName = {"count"}, deploymentName = "myTestDeployment")
    public synchronized void getAndSetCountIncreased() {
        count = this.countAtomic.getAndIncrement();
    }

    @Metric(fieldName = {"count2"}, deploymentName = "myTestDeployment")
    public synchronized void getAndSetCount2Increased() {
        count2 = this.count2+2;
    }
    
}
