package com.cats.rackHealth.service;

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

import com.cats.rackHealth.beans.Capability;
import com.cats.rackHealth.beans.HealthStatusMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@CacheConfig(cacheNames={"capabilities"})
public class RackCapabilitiesService {

    @Autowired
    CapabilityHealthCheck capabilityHealthCheckService;

    @Value( "${capabilities.file.path}" )
    String capabilitiesFilePath;

    @Autowired
    HealthStatusMap healthStatusMap;

    private final ObjectMapper mapper = new ObjectMapper();

    public void updateCapabilities(List<Capability> capabilities) throws IOException {
        if (capabilities != null){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(capabilitiesFilePath))) {
                writer.write(mapper.writeValueAsString(capabilities));
            } catch (IOException ex) {
                throw ex;
            }

        }
    }

    public List<Capability> getCapabilities() {
        List<Capability> capabilities = null;
        Capability rtr = new Capability();
        rtr.setShorthand("RTR");
        rtr.setName("RTR");
        try {
            capabilities = mapper.readValue(new File(capabilitiesFilePath), new TypeReference<List<Capability>>(){});
            capabilities.add(rtr);
            return capabilities;
        }catch(IOException e){

        }

        return capabilities;
    }
}