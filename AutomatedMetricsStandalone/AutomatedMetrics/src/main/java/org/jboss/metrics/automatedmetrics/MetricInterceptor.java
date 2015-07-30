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

package org.jboss.metrics.automatedmetrics;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.jboss.metrics.automatedmetricsapi.DBStore;
import org.jboss.metrics.automatedmetricsapi.Metric;
import org.jboss.metrics.jbossautomatedmetricslibrary.DeploymentMetricProperties;
import org.jboss.metrics.jbossautomatedmetricslibrary.MetricsCache;
import org.jboss.metrics.jbossautomatedmetricslibrary.MetricsCacheCollection;

/**
 *
 * @author Panagiotis Sotiropoulos
 */
@Metric
@Interceptor
public class MetricInterceptor {

    private final Map<String, Field> metricFields = new HashMap();
    private Field field;

    @AroundInvoke
    public Object metricsInterceptor(InvocationContext ctx) throws Exception {

        Object result = ctx.proceed();

        Method method = ctx.getMethod();

        try {
            Metric metricAnnotation = method.getAnnotation(Metric.class);
            if (metricAnnotation != null) {
                int fieldNameSize = metricAnnotation.fieldName().length;
                String group = metricAnnotation.groupName();

                for (int i = 0; i < fieldNameSize; i++) {

                    accessField(metricAnnotation, method, i);
                    String cacheStore = DeploymentMetricProperties.getDeploymentMetricProperties().getDeploymentMetricProperty(group).getCacheStore();
                    String rhqMonitoring = DeploymentMetricProperties.getDeploymentMetricProperties().getDeploymentMetricProperty(group).getRhqMonitoring();

                    if (cacheStore != null && Boolean.parseBoolean(cacheStore)) {
                        MetricsCache metricsCacheInstance;
                            metricsCacheInstance = MetricsCacheCollection.getMetricsCacheCollection().getMetricsCacheInstance(group);
                            if (metricsCacheInstance == null) {
                                metricsCacheInstance = new MetricsCache();
                                MetricsCacheCollection.getMetricsCacheCollection().addMetricsCacheInstance(group, metricsCacheInstance);
                            }
                        Store.CacheStore(ctx.getTarget(), field, metricsCacheInstance);
                    }
                    if (rhqMonitoring != null && Boolean.parseBoolean(rhqMonitoring)) {
                        MonitoringRhq mrhqInstance;
                            mrhqInstance = MonitoringRhqCollection.getRhqCollection().getMonitoringRhqInstance(group);
                            if (mrhqInstance == null) {
                                mrhqInstance = new MonitoringRhq(group);
                                MonitoringRhqCollection.getRhqCollection().addMonitoringRhqInstance(group, mrhqInstance);
                            }

                        mrhqInstance.rhqMonitoring(ctx.getTarget(), field, group);
                    }
                }
            }

            DBStore dbStoreAnnotation = method.getAnnotation(DBStore.class);
            if (dbStoreAnnotation != null) {  
                String group = dbStoreAnnotation.groupName();
                String statementName = dbStoreAnnotation.statementName();
                String query = ParseDbQuery.parse(dbStoreAnnotation.queryUpdateDB(),metricFields,ctx.getTarget(),group);
                Statement stmt = DeploymentMetricProperties.getDeploymentMetricProperties().getDeploymentMetricProperty(group).getDatabaseStatement().get(statementName);
                stmt.executeUpdate(query);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }

    private void accessField(Metric metricAnnotation, Method method, int fieldNum) throws Exception {
        if (metricFields.containsKey(metricAnnotation.fieldName()[fieldNum])) {
            field = metricFields.get(metricAnnotation.fieldName()[fieldNum]);
        } else {
            field = method.getDeclaringClass().getDeclaredField(metricAnnotation.fieldName()[fieldNum]);
            field.setAccessible(true);
            metricFields.put(metricAnnotation.fieldName()[fieldNum], field);
        }
    }
}
