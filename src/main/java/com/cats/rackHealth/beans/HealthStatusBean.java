package com.cats.rackHealth.beans;

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

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class HealthStatusBean {

    @Schema($schema = "Any applicable version w.r.t microservice. Ex: Microservice Version")
    Map<String,String> version;
    @JsonAlias({"isHealthy", "is_healthy"})
    @Schema($schema = "High level health summary for the microservice.")
    Boolean isHealthy;
    @Schema($schema = "Any human understandable remarks.")
    String remarks;
    @Schema($schema = "Health of any router leases if any")
    @JsonAlias({"leaseHealthStatus", "lease_health_status"})
    RouterLeaseStatus leaseHealthStatus;
    @Schema($schema = "Firmware health report of any router if any")
    RouterFirmwareStatus firmwareHealthStatus;
    @JsonAlias({"hwDevicesHealthStatus", "hw_devices_health_status"})
    @Schema($schema = "Health status for any dependent hardware devices like IrBlasters, Power Devices etc. Can be null or empty if there are no dependencies.")
    List<HealthReport> hwDevicesHealthStatus;
    @Schema($schema = "Health of any dependent softwares like redrathub, ffmpeg, CandelaTech GUI etc. Can be null or empty if there are no dependencies.")
    List<HealthReport> dependenciesHealthStatus;
    @Schema($schema = "Any License information and expiry information")
    List<License> licenses;
    @Schema($schema = "Any other information that needs to be captured as key value.")
    Map<String,String> metadata;
}

@Data
class License{
    @Schema($schema = "License name")
    String name;
    @Schema($schema = "Human understandable remark about license")
    String remarks;
    @Schema($schema = "Expiry Date of license")
    Date expiryDate;
    @Schema($schema = "Any max property enforced by license like node count etc")
    Integer maxPropertyCount;
    @Schema($schema = "Any other information that needs to be captured as key value.")
    Map<String,String> metadata;
}
