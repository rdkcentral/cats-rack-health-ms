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
import com.cats.rackHealth.beans.HealthStatusBean;
import com.cats.rackHealth.beans.HealthStatusMap;
import com.cats.rackHealth.beans.RouterLeaseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class CapabilityHealthCheck {
    @Autowired
    HealthStatusMap healthStatusMap;

    @Autowired
    RouterHealthService routerHealthService;

    public void checkHealth(Capability capability, String healthUrl) {
        if(capability == null || healthUrl == null){
            HealthStatusBean healthStatusBean = new HealthStatusBean();
            healthStatusBean.setIsHealthy(null);
            healthStatusBean.setRemarks("HealthURL is not configured properly "+"capability = [" + capability + "], healthUrl = [" + healthUrl + "]");
            healthStatusMap.put(capability.getShorthand(),healthStatusBean);
            return;
        }

        WebClient client = WebClient.create();
        WebClient.RequestBodySpec request = null;
        try {
            request = client.method(HttpMethod.GET).uri(new URI(healthUrl));
            request = request.accept(MediaType.APPLICATION_JSON);
            request = request.contentType(MediaType.APPLICATION_JSON);
            HealthStatusBean healthStatusBean = request.exchange().block().bodyToMono(HealthStatusBean.class).block(); //get status and update asynchronously
            log.debug("healthStatusBean " + healthStatusBean);
            if(capability.getShorthand().equals("RTR")){ // RTR is the only capability that has firmware status
                Map<String, String> version = new HashMap<>();
                version.put("ROUTER_VERSION", routerHealthService.getFirmwareStatus().getMetaData().getCurrentFirmware());
                healthStatusBean.setVersion(version);
                healthStatusBean.setFirmwareHealthStatus(routerHealthService.getFirmwareStatus());
                healthStatusBean.setIsHealthy(routerHealthService.getFirmwareStatus().getIsHealthy());
            }
            if(healthStatusBean.getLeaseHealthStatus() == null) {
                RouterLeaseStatus routerHealthStatus = routerHealthService.checkHealthFromRouter(capability);
                if (routerHealthStatus != null) {
                    healthStatusBean.setLeaseHealthStatus(routerHealthStatus);
                } else {
                    RouterLeaseStatus leaseStatus = new RouterLeaseStatus();
                    leaseStatus.setIsHealthy(false);
                    leaseStatus.setComment("Could not find router lease status");
                    healthStatusBean.setLeaseHealthStatus(leaseStatus);
                }
            }

            healthStatusMap.put(capability.getShorthand(),healthStatusBean);

        } catch (Exception e) {
            if(capability.equals("TCE") && !healthUrl.equals("http://192.168.100.11/rackhealth/trace/health")){
                checkHealth(capability, "http://192.168.100.11/rackhealth/trace/health");
                return;
            }
            HealthStatusBean healthStatusBean = new HealthStatusBean();
            healthStatusBean.setIsHealthy(false);
            healthStatusBean.setRemarks("Exception occurred when finding health "+e.getMessage());
            healthStatusMap.put(capability.getShorthand(),healthStatusBean);

        }
    }

    public HealthStatusBean getHealthStatus(Capability capability){
        if(capability != null) {
            return healthStatusMap.get(capability.getShorthand());
        }
        return null;
    }
}
