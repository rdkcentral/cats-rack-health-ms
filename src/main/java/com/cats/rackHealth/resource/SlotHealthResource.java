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
import com.cats.rackHealth.service.SlotHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Slot Health Management APIs", description = "APIs to retrieve the health status of a specific slot within the rack.")
@RequestMapping(value = "/slot/{slotNo}")
public class SlotHealthResource {

    @Autowired
    SlotHealthService slotHealthService;

    @Operation(summary = "Get health for a Slot", description = "Get health details for a given slot on the rack.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "operation successful",useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Slot not found")
    })
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Map<String , HealthStatusBean> getSlotHealth(@Parameter(description = "Slot to get details for.") @PathVariable("slotNo") long slotNo, @Parameter(description = "Request Body for the list of capability.") @RequestBody(required = false) List<String> capabilities) {
        return slotHealthService.getSlotHealth(slotNo, capabilities);
    }
}
