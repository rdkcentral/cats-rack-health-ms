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

import com.cats.rackHealth.service.RackCapabilitiesService;
import com.cats.rackHealth.beans.Capability;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;



@RestController
@Tag(name = "Rack Management APIs", description = "Control APIs for retrieving and updating the details of capabilities in a rack.")
@RequestMapping(value = "/capabilities")
public class RackCapabilitiesResource {

    @Autowired
    RackCapabilitiesService rackCapabilitiesService;

    @Operation(summary = "Update the Capability", description = "Update the Capability of the Rack")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @RequestMapping(value = "", method = RequestMethod.POST)
    @Timed(percentiles = {0.5, 0.75, 0.95})
    public void updateCapabilities(@Parameter(description = "Capability list to be updated in the Rack") @RequestBody(required = true) List<Capability> capabilities) throws IOException {
        rackCapabilitiesService.updateCapabilities(capabilities);
    }

    @Operation(summary = "Get Rack Capabilities", description = "Get list of capabilities in the Rack")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation Success",
                    content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Capability.class))) }),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Capability not found")
    })
    @RequestMapping(value = "", method = RequestMethod.GET)
    @Timed(percentiles = {0.5, 0.75, 0.95})
    public List<Capability> getCapabilities() throws IOException {
        return rackCapabilitiesService.getCapabilities();
    }
}
