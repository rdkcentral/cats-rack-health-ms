package com.cats.rackHealth.ui;

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
import com.cats.rackHealth.service.RackCapabilitiesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    RackCapabilitiesService capabilitiesService;

    @Autowired
    HealthStatusMap healthStatusMap;

    @GetMapping("/rackhealth")
    public String homePage(Model model) {
        List<Capability> capabilities = capabilitiesService.getCapabilities();
        if(capabilities != null){
            List<CapabilityBean> capabilityBeans = capabilities.stream().map(capability -> {
                HealthStatusBean healthStatusBean = healthStatusMap.get(capability.getShorthand());
                if(healthStatusBean == null){
                    healthStatusBean = new HealthStatusBean();
                    healthStatusBean.setIsHealthy(false);
                    healthStatusBean.setRemarks("No health status available for capability");
                }
                String json = "";
                ObjectMapper mapper = new ObjectMapper();
                try {
                    json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(healthStatusBean);
                } catch (JsonProcessingException e) {
                   json = e.getMessage();
                }
                return new CapabilityBean(capability.getName(),capability.getShorthand(),healthStatusBean.getIsHealthy(), healthStatusBean.getRemarks(),json);
            }).collect(Collectors.toList());
            model.addAttribute("capabilities", capabilityBeans);
        }else{
            model.addAttribute("capabilities", new ArrayList<>());
        }

        return "home";
    }
}

@Data
@AllArgsConstructor
class CapabilityBean{
    String name;
    String shorthand;
    Boolean isHealthy;
    String remarks;
    String json;
}