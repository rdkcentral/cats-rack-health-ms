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

import java.util.Map;

@Data
public class HealthReport{
    @Schema($schema ="The entity being reported. For ex: IrnetboxProIII or Redrathub")
    String entity;
    @JsonAlias({"deviceId", "device_id"})
    @Schema($schema ="Device ID (if applicable, mostly for hardware devices).")
    String deviceId;
    @JsonAlias({"isHealthy", "is_healthy"})
    @Schema($schema ="Is this device/dependency healthy.")
    Boolean isHealthy;
    @Schema($schema ="Any human understandable remarks.")
    String remarks;
    @Schema($schema ="IP/hostname of the dependency.")
    String host;
    @Schema($schema ="Any applicable version w.r.t device. Ex: Redrathub Version.")
    Map<String,String> version;
    @Schema($schema ="Any other information that needs to be captured as key value.")
    Map<String,String> metadata;
}
