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

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import org.jboss.metrics.automatedmetrics.utils.DoubleValue;

/**
 *
 * @author Panagiotis Sotiropoulos
 */
@Path("/rest/metric/data/{id}")
public interface PostDataRhq {

    @PUT
    @Path("/raw/{timeStamp}")
    @Consumes("application/json")
    void postDataRhq(DoubleValue data, @PathParam("id") int id, @PathParam("timeStamp") long timestamp, @HeaderParam(HttpHeaders.ACCEPT) String accept);
}
