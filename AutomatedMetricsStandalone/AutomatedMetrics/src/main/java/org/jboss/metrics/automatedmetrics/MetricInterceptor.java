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

import com.rits.cloning.Cloner;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.jboss.metrics.automatedmetricsapi.DBStore;
import org.jboss.metrics.automatedmetricsapi.Metric;
import org.jboss.metrics.jbossautomatedmetricslibrary2.CodeParams;
import org.jboss.metrics.jbossautomatedmetricslibrary2.CodeParamsCollection;
import org.jboss.metrics.jbossautomatedmetricslibrary.DeploymentMetricProperties;
import org.jboss.metrics.jbossautomatedmetricslibrary.MetricsCache;
import org.jboss.metrics.jbossautomatedmetricslibrary.MetricsCacheCollection;
import org.jboss.metrics.jbossautomatedmetricsproperties.MetricProperties;

/**
 *
 * @author Panagiotis Sotiropoulos
 */
@Metric
@Interceptor
public class MetricInterceptor {

    private Map<String, Field> metricFields = new HashMap();
    private final static Object rhqLock = new Object();
    private final static Object dbLock = new Object();
    private final static Object cacheLock = new Object();

    @AroundInvoke
    public Object metricsInterceptor(InvocationContext ctx) throws Exception {
        Object result = ctx.proceed();
        Method method = ctx.getMethod();
        final Object target = ctx.getTarget();
            
        try {
            final Metric metricAnnotation = method.getAnnotation(Metric.class);
            MetricsCache metricsCacheInstance = null;
            HashMap<String, Object> metricValuesInternal = new HashMap();
            if (metricAnnotation != null) {
                int fieldNameSize = metricAnnotation.fieldName().length;
                final int dataSize = metricAnnotation.data().length;
                final String group = metricAnnotation.groupName();

                final MetricProperties properties = DeploymentMetricProperties.getDeploymentMetricProperties().getDeploymentMetricProperty(group);
                String cacheStore = properties.getCacheStore();
                String rhqMonitoring = properties.getRhqMonitoring();
                String metricPlot = properties.getMetricPlot();
                final int refreshRate = properties.getPlotRefreshRate();

                for (int i = 0; i < fieldNameSize; i++) {

                    final Field field = accessField(metricAnnotation, method, i);
                    final Object fieldValue = field.get(target);
                    final String fieldName = field.getName();
                    metricValuesInternal.put(metricAnnotation.fieldName()[i], field.get(target));
                    
                    
                    if (cacheStore != null && Boolean.parseBoolean(cacheStore)) {
                        synchronized(cacheLock) {
                            metricsCacheInstance = MetricsCacheCollection.getMetricsCacheCollection().getMetricsCacheInstance(group);
                            if (metricsCacheInstance == null) {
                                metricsCacheInstance = new MetricsCache();
                                MetricsCacheCollection.getMetricsCacheCollection().addMetricsCacheInstance(group, metricsCacheInstance);
                            }
                            Store.CacheStore(target, fieldName, fieldValue, metricsCacheInstance, properties);
                        }
                    }
                    
                    if (rhqMonitoring != null && Boolean.parseBoolean(rhqMonitoring)) {
                        new Thread() {
                            public void run() {
                                MonitoringRhq mrhqInstance;
                                synchronized(rhqLock) {
                                    mrhqInstance = MonitoringRhqCollection.getRhqCollection().getMonitoringRhqInstance(group);
                                    if (mrhqInstance == null) {
                                        mrhqInstance = new MonitoringRhq(group);
                                        MonitoringRhqCollection.getRhqCollection().addMonitoringRhqInstance(group, mrhqInstance);
                                    }
                                }

                                try {
                                    mrhqInstance.rhqMonitoring(target, fieldName, group);
                                } catch (IllegalArgumentException | IllegalAccessException ex) {
                                   ex.printStackTrace();
                                }
                                
                            }
                        }.start();
                    }
                    
                }
                
                if (metricPlot != null && Boolean.parseBoolean(metricPlot)) {
                    new Thread() {
                        public void run() {
                            if (dataSize != 0) {
                                for (int i = 0; i < dataSize; i++) {
                                    try {
                                        Field field = getData(metricAnnotation, i);
                                        String fieldName = field.getName();
                                        Object fieldValue = field.get(target);
                                        MetricPlot.plot(metricAnnotation, fieldName, target, properties, group, refreshRate, i);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }.start();
                }
            }

            final DBStore dbStoreAnnotation = method.getAnnotation(DBStore.class);
            if (dbStoreAnnotation != null) {  
                final String group = dbStoreAnnotation.groupName();
                MetricProperties properties = DeploymentMetricProperties.getDeploymentMetricProperties().getDeploymentMetricProperty(group);
                String dataBaseStorage = properties.getDatabaseStore();
                final MetricsCache mCI = metricsCacheInstance;
                if (dataBaseStorage != null && Boolean.parseBoolean(dataBaseStorage)) {
                    final Map<String, Object> metricValuesCloned = (Map<String, Object>)metricValuesInternal.clone();
                    Field metricUser = null;
                    CodeParams cp = null;
                    String mUser = "default";
                    try {
                        metricUser = method.getDeclaringClass().getDeclaredField("metricUser");
                        if (metricUser != null) {
                            metricUser.setAccessible(true);
                            mUser = metricUser.get(target).toString();
                            if (CodeParamsCollection.getCodeParamsCollection().existsCodeParamsInstance(metricUser.get(target).toString()))
                                cp = CodeParamsCollection.getCodeParamsCollection().getCodeParamsInstance(metricUser.get(target).toString());
                        }
                    }catch(Exception e) {
                        // Probably the metric user is not defined. Go on with the execution of the database storage.
                    }
                    
                    final String user = mUser;
                    Cloner cloner = new Cloner();
                    final CodeParams cParams = cloner.deepClone(cp);
                    new Thread() {
                        public void run() {
                            DBStoreInstance dBStoreInstance;
                            synchronized(dbLock) {
                                dBStoreInstance = DBStoreCollection.getDBStoreCollection().getDbStoreInstance(group);
                                if (dBStoreInstance == null) {
                                    dBStoreInstance = new DBStoreInstance();
                                    DBStoreCollection.getDBStoreCollection().addDbStoreInstance(group, dBStoreInstance);
                                }
                            }

                            try {
                                dBStoreInstance.dbStore(dbStoreAnnotation, target, metricValuesCloned, group, cParams, user);
                            } catch (IllegalArgumentException | IllegalAccessException | SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }

    private synchronized Field accessField(Metric metricAnnotation, Method method, int fieldNum) throws Exception {
        Field field;
        if (metricFields.containsKey(metricAnnotation.fieldName()[fieldNum])) {
            field = metricFields.get(metricAnnotation.fieldName()[fieldNum]);
        } else {
            field = method.getDeclaringClass().getDeclaredField(metricAnnotation.fieldName()[fieldNum]);
            field.setAccessible(true);
            metricFields.put(metricAnnotation.fieldName()[fieldNum], field);
        }
        
        return field;
    }
    
    private Field getData(Metric metricAnnotation, int fieldNum) throws Exception {
        Field field;
        if (metricFields.containsKey(metricAnnotation.data()[fieldNum])) {
            field = metricFields.get(metricAnnotation.data()[fieldNum]);
        }else {
            field = null;
        }
        return field;
    }
}
