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
package org.jboss.metrics.jbossautomatedmetricsproperties;

import java.awt.Color;
import java.sql.Statement;
import java.util.HashMap;
import javax.swing.JFrame;
import org.math.plot.Plot2DPanel;

/**
 *
 * @author Panagiotis Sotiropoulos
 */
public class MetricProperties {
 
    private String rhqMonitoring = "false";
    private String cacheStore = "false";
    private int cacheMaxSize = Integer.MAX_VALUE;
    private String databaseStore = "false";
    private String metricPlot = "false";
    private HashMap<String,Statement> databaseStatement;
    private HashMap<String,String> updateDbQueries;
    private HashMap<String,Plot2DPanel> plots;
    private HashMap<String,JFrame> frames;
    private HashMap<String,Color> colors;
    private int plotRefreshRate = 1;
    private int rhqMonitoringRefreshRate = 1;
    private String rhqServerUrl = "localhost";
    private String rhqServerPort = "7080";
    private String rhqServerUsername = "rhqadmin";
    private String rhqServerPassword = "rhqadmin";
    private HashMap<String,String> rhqScheduleIds;

    public MetricProperties() {
        rhqScheduleIds = new HashMap<>();
        databaseStatement = new HashMap<>();
        updateDbQueries = new HashMap<>();
        plots = new HashMap<>();
        colors = new HashMap<>();
        frames = new HashMap<>();
    }
    
    public synchronized String getRhqMonitoring() {
        return rhqMonitoring;
    }

    public synchronized void setRhqMonitoring(String rhqMonitoring) {
        this.rhqMonitoring = rhqMonitoring;
    }

    public synchronized String getCacheStore() {
        return cacheStore;
    }

    public synchronized void setCacheStore(String cacheStore) {
        this.cacheStore = cacheStore;
    }

    public synchronized HashMap<String, String> getRhqScheduleIds() {
        return rhqScheduleIds;
    }

    public synchronized void setRhqScheduleIds(HashMap<String, String> rhqScheduleIds) {
        this.rhqScheduleIds = rhqScheduleIds;
    }
    
    public synchronized String getRhqScheduleId(String name) {
        return(this.rhqScheduleIds.get(name));
    }

    public synchronized String getRhqServerUrl() {
        return rhqServerUrl;
    }

    public synchronized void setRhqServerUrl(String rhqServerUrl) {
        this.rhqServerUrl = rhqServerUrl;
    }

    public synchronized String getRhqServerPort() {
        return rhqServerPort;
    }

    public synchronized void setRhqServerPort(String rhqServerPort) {
        this.rhqServerPort = rhqServerPort;
    }

    public synchronized String getRhqServerUsername() {
        return rhqServerUsername;
    }

    public synchronized void setRhqServerUsername(String rhqServerUsername) {
        this.rhqServerUsername = rhqServerUsername;
    }

    public synchronized String getRhqServerPassword() {
        return rhqServerPassword;
    }

    public synchronized void setRhqServerPassword(String rhqServerPassword) {
        this.rhqServerPassword = rhqServerPassword;
    }
    
    public synchronized void addRhqScheduleId(String name, String id) {
        this.rhqScheduleIds.put(name, id);
    }
    
    public synchronized void removeRhqScheduleId(String name) {
        this.rhqScheduleIds.remove(name);
    }

    public synchronized String getDatabaseStore() {
        return databaseStore;
    }

    public synchronized void setDatabaseStore(String databaseStore) {
        this.databaseStore = databaseStore;
    }

    public synchronized HashMap<String, Statement> getDatabaseStatement() {
        return databaseStatement;
    }

    public synchronized void setDatabaseStatement(HashMap<String, Statement> databaseStatement) {
        this.databaseStatement = databaseStatement;
    }

    public synchronized HashMap<String, String> getUpdateDbQueries() {
        return updateDbQueries;
    }

    public synchronized void setUpdateDbQueries(HashMap<String, String> updateDbQueries) {
        this.updateDbQueries = updateDbQueries;
    }

    public synchronized String getMetricPlot() {
        return metricPlot;
    }

    public synchronized void setMetricPlot(String metricPlot) {
        this.metricPlot = metricPlot;
    }

    public synchronized HashMap<String, Plot2DPanel> getPlots() {
        return plots;
    }

    public synchronized void setPlots(HashMap<String, Plot2DPanel> plots) {
        this.plots = plots;
    }

    public synchronized void addPlot(String name, Plot2DPanel plot) {
        this.plots.put(name, plot);
    }
    
    public synchronized void removePlot(String name) {
        this.plots.remove(name);
    }

    public synchronized int getPlotRefreshRate() {
        return plotRefreshRate;
    }

    public synchronized void setPlotRefreshRate(int plotRefreshRate) {
        this.plotRefreshRate = plotRefreshRate;
    }

    public synchronized HashMap<String, Color> getColors() {
        return colors;
    }

    public synchronized void setColors(HashMap<String, Color> colors) {
        this.colors = colors;
    }
    
    public synchronized void addColor(String name, Color color) {
        this.colors.put(name, color);
    }
    
    public synchronized void removeColor(String name) {
        this.colors.remove(name);
    }
    
    public synchronized HashMap<String, JFrame> getFrames() {
        return frames;
    }

    public synchronized void setFrames(HashMap<String, JFrame> frames) {
        this.frames = frames;
    }

    public synchronized void addFrame(String name, JFrame frame) {
        this.frames.put(name, frame);
    }
    
    public synchronized void removeFrame(String name) {
        this.frames.remove(name);
    }

    public synchronized int getRhqMonitoringRefreshRate() {
        return rhqMonitoringRefreshRate;
    }

    public synchronized void setRhqMonitoringRefreshRate(int rhqMonitoringRefreshRate) {
        this.rhqMonitoringRefreshRate = rhqMonitoringRefreshRate;
    }

    public int getCacheMaxSize() {
        return cacheMaxSize;
    }

    public void setCacheMaxSize(int cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }

}
