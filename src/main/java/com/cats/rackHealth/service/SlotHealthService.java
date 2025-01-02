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
import com.cats.rackHealth.beans.HealthReport;
import com.cats.rackHealth.beans.HealthStatusBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SlotHealthService {

    @Autowired
    BuildProperties buildProperties;


    @Autowired
    RackCapabilitiesService rackCapabilitiesService;


    @Value("${cats.rack.video.url}")
    private String url;

    @Value("${cats.rack.scat.health.url}")
    private String scatUrl;

    public HealthStatusBean slotPowerHealth(long slotNo) {
        RestTemplate restTemplate = new RestTemplate();
        HealthReport healthReport = new HealthReport();
        HealthStatusBean healthStatusBean = new HealthStatusBean();
        Map<String, String> version = new HashMap<>();
        String remark = "N/A";

        healthReport.setEntity("PWR");
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url + "minion/rest/rack/" + slotNo + "/power/status", String.class);
            healthStatusBean.setIsHealthy(responseEntity.getStatusCode().is2xxSuccessful());
            remark = responseEntity.getBody();
        }
        catch (Exception e) {
            healthStatusBean.setIsHealthy(false);
            remark = e.getMessage();
        }
        ResponseEntity<String> versionResponse = restTemplate.getForEntity( url + "minion/rest/rack/" + slotNo + "/power/version", String.class );
        if( versionResponse.getStatusCode().is2xxSuccessful() ) {
            version.put("Power-MS Version", versionResponse.getBody());
        }
        else {
            version.put("Power-MS Version", "Unknown");
        }
        healthReport.setVersion(version);
        healthReport.setHost(url);
        if (!healthStatusBean.getIsHealthy()) {
            healthReport.setRemarks(remark);
            healthStatusBean.setRemarks(remark);
        }
        return healthStatusBean;
    }

    public HealthStatusBean slotIRHealth( long slotNo ) {
        RestTemplate restTemplate = new RestTemplate();
        HealthReport healthReport = new HealthReport();
        HealthStatusBean healthStatusBean = new HealthStatusBean();
        String remark = "N/A";
        healthReport.setEntity("IRR");
        try {
            MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
            headers.add("Accept", "text/plain");
            HttpEntity httpEntity = new HttpEntity(headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url + "minion/rest/rack/" + slotNo + "/ir/pressKey?keySet=PC_REMOTE&command=VOLUP", httpEntity, String.class);
            healthStatusBean.setIsHealthy(responseEntity.getStatusCode().is2xxSuccessful());
            remark = responseEntity.getBody();
        }
        catch (Exception e) {
            healthStatusBean.setIsHealthy(false);
            remark = e.getMessage();
        }
        healthReport.setHost(url);
        if (!healthStatusBean.getIsHealthy()) {
            healthStatusBean.setRemarks(remark);
        }
        return healthStatusBean;
    }

   public HealthStatusBean slotVideoHealth( long slotNo ) {
        HealthStatusBean healthStatusBean = new HealthStatusBean();
        RestTemplate restTemplate = new RestTemplate();
        try {
            String slotHealthUrl = url + "rackhealth/video/health/" + slotNo + "/status";
            log.debug("url is {}",slotHealthUrl);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(slotHealthUrl, String.class);
            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                String screenStatus = responseEntity.getBody();
                if (screenStatus.length() > 1) {
                    String[] words = screenStatus.split(" ");
                    String lastWord = words[words.length - 1];

                    if (lastWord.equals("Normal")) {
                        healthStatusBean.setIsHealthy(true);
                    } else {
                        healthStatusBean.setIsHealthy(false);
                        healthStatusBean.setRemarks("Observed " + lastWord + " Screen");
                    }
                }
                else {
                    healthStatusBean.setIsHealthy(false);
                    healthStatusBean.setRemarks("Could not fetch screenshot from device");
                }
            }else {
                log.debug("Failed to fetch the slot health details. The response status code from the URL is {}", responseEntity.getStatusCode());
                healthStatusBean.setIsHealthy(false);
                healthStatusBean.setRemarks("Could not fetch screenshot from device");
            }
        }
        catch (Exception e) {
            log.error("Error occurred {}", e.getMessage());
            healthStatusBean.setIsHealthy(false);
            healthStatusBean.setRemarks("Could not fetch screenshot from device");
        }
        return healthStatusBean;
    }

    public Map< String,String > getVersion() {
        Map<String , String> version = new HashMap<>();
        version.put("MS_VERSION",buildProperties.getVersion());
        return version;
    }

    public boolean isHealthy(List<HealthReport> reports) {
        for (HealthReport report : reports) {
            if(!report.getIsHealthy()) {
                return false;
            }
        }
        return true;
    }

    public HealthStatusBean getSlotCapabilityHealth(long slotNo, String capability) {
        HealthStatusBean capHealthBean= new HealthStatusBean();
        switch (capability) {
            case "IRR" : return slotIRHealth(slotNo);
            case "PWR" : return slotPowerHealth(slotNo);
            case "VID" : return slotVideoHealth(slotNo);
            case "TCE" : return slotTraceHealth(slotNo);
            default    : capHealthBean.setRemarks("N/A");
                         return capHealthBean;
        }
    }

    public HealthStatusBean slotTraceHealth(long slotNo) {
        HealthStatusBean healthStatusBean = new HealthStatusBean();
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<HealthStatusBean> responseEntity = restTemplate.getForEntity(scatUrl, HealthStatusBean.class);
            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                healthStatusBean = responseEntity.getBody();
            }else{
                healthStatusBean.setIsHealthy(false);
                healthStatusBean.setRemarks(responseEntity.getStatusCode().toString());
            }
        }
        catch (Exception e) {
            healthStatusBean.setIsHealthy(false);
            healthStatusBean.setRemarks(e.getMessage());
        }
        return healthStatusBean;
    }

    public Map< String, HealthStatusBean > getSlotHealth(long slotNo, List<String> capabilities) {
        List<Capability> availableCaps = rackCapabilitiesService.getCapabilities();
        if(availableCaps == null) {
            availableCaps = new ArrayList<>();
        }
        log.info("The slot no is {}",slotNo);
        List<String> available = new ArrayList<>();
        availableCaps.forEach(availableCap -> {
            available.add(availableCap.getShorthand());
        });
        if(capabilities == null) {
            capabilities = available;
        }
        int test = available.size();
        log.info("the test is {}",test);
        if(!available.containsAll(capabilities)) {
            capabilities.removeAll(available);
            throw new IllegalArgumentException( capabilities.toString() + " Not Applicable for this Rack");
        }
        Map<String, HealthStatusBean> slotHealthMap = new HashMap<>();

        capabilities.forEach(capability -> {
            slotHealthMap.put(capability, getSlotCapabilityHealth(slotNo, capability));
        });

        return slotHealthMap;
    }
}
