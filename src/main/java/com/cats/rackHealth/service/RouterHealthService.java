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

import com.cats.rackHealth.beans.*;
import com.cats.rackHealth.h2.registry.MicroserviceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;


@Service
@Slf4j
public class RouterHealthService {
    @Autowired
    HealthStatusMap healthStatusMap;

    @Autowired
    MicroserviceRegistry microserviceRegistry;

    @Value("${cats.rack.firmware.details}")
    private String firmwareUrl;

    @Autowired
    RestTemplate restTemplate;

    public RouterLeaseStatus checkHealthFromRouter(Capability capability) {

        if(capability == null){
            throw new IllegalArgumentException("Capability cant be null ");
        }
        return checkHealthFromRouter(capability.getShorthand());
    }

    public RouterLeaseStatus checkHealthFromRouter(String capability) {

        if(capability == null || capability.isEmpty()){
            throw new IllegalArgumentException("Capability cant be null ");
        }
        String healthUrl = microserviceRegistry.getValue("RTR","healthUrl");
        WebClient client = WebClient.create();
        WebClient.RequestHeadersSpec request = null;
        RouterHealthStatus routerHealthStatus;
        try {
            WebClient.ResponseSpec response = client.method(HttpMethod.GET).uri(new URI(healthUrl)).accept(MediaType.APPLICATION_JSON).retrieve();
            routerHealthStatus =  response.bodyToMono(RouterHealthStatus.class).block();
            if(routerHealthStatus != null) {
                return routerHealthStatus.get(capability);
            }

        } catch (WebClientResponseException webClientResponseException){
            RouterLeaseStatus routerLeaseStatus = new RouterLeaseStatus();
            routerHealthStatus = new RouterHealthStatus();
            if(webClientResponseException.getRawStatusCode() == 502){
                routerLeaseStatus.setIsHealthy(false);
                routerLeaseStatus.setComment("mt-query-ms might be stopped or not installed");
                routerHealthStatus.put(capability,routerLeaseStatus);
            }else if(webClientResponseException.getRawStatusCode() == 400){
                routerLeaseStatus.setIsHealthy(false);
                routerLeaseStatus.setComment("health user credentials might be mismatched");
                routerHealthStatus.put(capability,routerLeaseStatus);
            }

            return routerHealthStatus.get(capability);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public RouterFirmwareStatus getFirmwareStatus() {

        ResponseEntity<RouterFirmwareStatus> responseEntity = restTemplate.getForEntity(firmwareUrl, RouterFirmwareStatus.class);
        return responseEntity.getBody();
    }
}
