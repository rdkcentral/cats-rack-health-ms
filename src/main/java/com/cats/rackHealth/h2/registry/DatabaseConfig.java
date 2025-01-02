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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseConfig {

    @Value("${cats.rack.base.path}")
    private String  rackHealthBaseUrl;

    @Bean
    public CommandLineRunner initData(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("DROP TABLE IF EXISTS MSRegistry");

            jdbcTemplate.execute("CREATE TABLE MSRegistry (" +
                    "id INT AUTO_INCREMENT  PRIMARY KEY," +
                    "capability VARCHAR(250) NOT NULL," +
                    "health_url VARCHAR(250) NOT NULL)");

            jdbcTemplate.update("INSERT INTO MSRegistry (capability, health_url) VALUES (?, ?)",
                    "IRR",   rackHealthBaseUrl + "/ir/health");
            jdbcTemplate.update("INSERT INTO MSRegistry (capability, health_url) VALUES (?, ?)",
                    "PWR",   rackHealthBaseUrl + "/power/rest/health");
            jdbcTemplate.update("INSERT INTO MSRegistry (capability, health_url) VALUES (?, ?)",
                    "WQA",  rackHealthBaseUrl + "/atten/rest/health");
            jdbcTemplate.update("INSERT INTO MSRegistry (capability, health_url) VALUES (?, ?)",
                    "TCE",   rackHealthBaseUrl + ":9080/scat/api/health");
            jdbcTemplate.update("INSERT INTO MSRegistry (capability, health_url) VALUES (?, ?)",
                    "RTR",  rackHealthBaseUrl + "/mtquery/api/v2/router/capability");
            jdbcTemplate.update("INSERT INTO MSRegistry (capability, health_url) VALUES (?, ?)",
                    "VID",   rackHealthBaseUrl + "/vid/health");
            jdbcTemplate.update("INSERT INTO MSRegistry (capability, health_url) VALUES (?, ?)",
                    "RLY",  rackHealthBaseUrl + "/relay/health");
        };
    }
}