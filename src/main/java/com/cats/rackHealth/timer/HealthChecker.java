package com.cats.rackHealth.timer;

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

import com.cats.rackHealth.beans.HealthStatusMap;
import com.cats.rackHealth.service.CapabilityHealthCheck;
import com.cats.rackHealth.service.RackCapabilitiesService;
import com.cats.rackHealth.beans.Capability;
import com.cats.rackHealth.h2.registry.MicroserviceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HealthChecker {

    @Autowired
    RackCapabilitiesService capabilitiesService;
    @Autowired
    MicroserviceRegistry microserviceRegistry;
    @Autowired
    CapabilityHealthCheck capabilityHealthCheckService;
    @Autowired
    HealthStatusMap healthStatusMap;

    @Scheduled(fixedRate = 1000*60*5)
    public void checkHealth(){
        List<Capability> capabilities = capabilitiesService.getCapabilities();
        if(capabilities != null){
            Set<String> removedCapabilities = healthStatusMap.keySet().stream().filter(capability ->
                !capabilities.stream().map(capability1 -> capability1.getShorthand())
                        .filter(capShortHand -> capShortHand.equals(capability))
                        .findAny().isPresent()
            ).collect(Collectors.toSet());
            log.info("removedCapabilities = " + removedCapabilities);
            removedCapabilities.stream().forEach(removedCapability -> healthStatusMap.remove(removedCapability));
            capabilities.parallelStream().forEach(capability -> {
                log.debug("capability = " + capability);
                String healthUrl = microserviceRegistry.getValue(capability,"healthUrl");
                log.info("healthUrl = " + healthUrl);
                capabilityHealthCheckService.checkHealth(capability, healthUrl);
                log.info("Done Check = " + capability);
            });
        }
    }

}