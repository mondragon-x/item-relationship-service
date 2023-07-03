/********************************************************************************
 * Copyright (c) 2021,2022,2023
 *       2022: ZF Friedrichshafen AG
 *       2022: ISTOS GmbH
 *       2022,2023: Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *       2022,2023: BOSCH AG
 * Copyright (c) 2021,2022,2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0. *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.irs.registryclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.tractusx.irs.edc.client.EdcSubmodelFacade;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class DefaultConfigurationTest {

    private final DefaultConfiguration testee = new DefaultConfiguration();

    @Test
    void centralDigitalTwinRegistryService() {
        final var service = testee.centralDigitalTwinRegistryService(
                testee.digitalTwinRegistryClientImpl(new RestTemplate(), "descriptor/{aasIdentifier}",
                        "shell?{assetIds}"));

        assertThat(service).isNotNull();
    }

    @Test
    void decentralDigitalTwinRegistryService() {
        final EdcSubmodelFacade facadeMock = mock(EdcSubmodelFacade.class);
        final var service = testee.decentralDigitalTwinRegistryService(
                testee.discoveryFinderClient(new RestTemplate(), "finder"),
                testee.endpointDataForConnectorsService(facadeMock),
                testee.decentralDigitalTwinRegistryClient(new RestTemplate()));

        assertThat(service).isNotNull();
    }
}