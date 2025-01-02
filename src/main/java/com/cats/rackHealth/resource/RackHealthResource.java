package com.cats.rackHealth.resource;

/*
 * Copyright 2021 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import com.cats.rackHealth.beans.HealthStatusBean;
import com.cats.rackHealth.beans.HealthStatusMap;
import com.cats.rackHealth.service.RackCapabilitiesService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "Rack Health Management APIs", description = "Control APIs to retrieve the overall health of the Rack and health based on the capabilities within the rack")
public class RackHealthResource {

    @Autowired
    RackCapabilitiesService rackCapabilitiesService;

    @Autowired
    HealthStatusMap healthStatusMap;


    @Operation(summary = "Retrieve Rack Health Status", description = "Retrieve comprehensive health status details of the Rack.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = HealthStatusBean.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @RequestMapping(value = "", method = RequestMethod.GET)
    @Timed(percentiles = {0.5, 0.75, 0.95})
    public HealthStatusMap getAllHealth() {
        if(rackCapabilitiesService.getCapabilities() == null || rackCapabilitiesService.getCapabilities().isEmpty()){
            throw new IllegalStateException("Capabilities not configured for rack");
        }
        return healthStatusMap;
    }

    @Operation(summary = "Retrieve Health Status of a Capability", description = "Retrieve the health status for a specific capability within the rack.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = HealthStatusBean.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Capability not found")
    })
    @RequestMapping(value = "/{capability}", method = RequestMethod.GET)
    @Timed(percentiles = {0.5, 0.75, 0.95})
    public HealthStatusBean getHealth(@Parameter(description = "Capability to get details for.") @PathVariable("capability") String capabilityShortHand) {
        return healthStatusMap.get(capabilityShortHand);
    }
}
