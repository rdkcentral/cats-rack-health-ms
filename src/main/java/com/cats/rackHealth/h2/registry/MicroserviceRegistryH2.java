package com.cats.rackHealth.h2.registry;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MicroserviceRegistryH2 implements MicroserviceRegistry {

    @Autowired
    MicroserviceRegistryRepository microserviceRegistryRepository;

    @Override
    public String getValue(Capability capability, String key) {
        if(capability != null ) {
           return getValue(capability.getShorthand(), key);
        }

        return null;

    }

    @Override
    public String getValue(String capability, String key) {
        if(capability != null ) {
            MSRegistry msRegistryData = microserviceRegistryRepository.findByCapability(capability);
            if(msRegistryData != null){
                switch (key){
                    case "healthUrl":
                        return msRegistryData.getHealthUrl();
                }
            }
        }

        return null;

    }
}
