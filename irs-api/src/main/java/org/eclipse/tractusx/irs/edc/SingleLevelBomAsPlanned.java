/********************************************************************************
 * Copyright (c) 2021,2022
 *       2022: Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *       2022: ZF Friedrichshafen AG
 *       2022: ISTOS GmbH
 * Copyright (c) 2021,2022 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.irs.edc;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.tractusx.irs.component.GlobalAssetIdentification;
import org.eclipse.tractusx.irs.component.LinkedItem;
import org.eclipse.tractusx.irs.component.MeasurementUnit;
import org.eclipse.tractusx.irs.component.Relationship;
import org.eclipse.tractusx.irs.component.enums.AspectType;
import org.eclipse.tractusx.irs.component.enums.BomLifecycle;

/**
 * SingleLevelBomAsPlanned
 */
@Data
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
class SingleLevelBomAsPlanned extends RelationshipSubmodel {

    private String catenaXId;
    private Set<ChildData> childParts;

    @Override
    public List<Relationship> asRelationships() {
        return Optional.ofNullable(this.childParts).stream().flatMap(Collection::stream)
                       .map(childData -> childData.toRelationship(this.catenaXId))
                       .toList();
    }

    /**
     * ChildData
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    /* package */ static class ChildData {

        private ZonedDateTime createdOn;
        private Quantity quantity;
        private ZonedDateTime lastModifiedOn;
        private String childCatenaXId;

        public Relationship toRelationship(final String catenaXId) {
            final LinkedItem.LinkedItemBuilder linkedItem = LinkedItem.builder()
                                                                      .childCatenaXId(GlobalAssetIdentification.of(this.childCatenaXId))
                                                                      .lifecycleContext(BomLifecycle.AS_PLANNED)
                                                                      .assembledOn(this.createdOn)
                                                                      .lastModifiedOn(this.lastModifiedOn);

            if (thereIsQuantity()) {
                final String datatypeURI = thereIsMeasurementUnit() ? this.quantity.getMeasurementUnit().getDatatypeURI() : null;
                final String lexicalValue = thereIsMeasurementUnit() ? this.quantity.getMeasurementUnit().getLexicalValue() : null;

                linkedItem.quantity(org.eclipse.tractusx.irs.component.Quantity.builder()
                                                                               .quantityNumber(this.quantity.getQuantityNumber())
                                                                               .measurementUnit(MeasurementUnit.builder()
                                                                                                               .datatypeURI(datatypeURI)
                                                                                                               .lexicalValue(lexicalValue)
                                                                                                               .build())
                                                                               .build());
            }

            return Relationship.builder()
                               .catenaXId(GlobalAssetIdentification.of(catenaXId))
                               .linkedItem(linkedItem.build())
                               .aspectType(AspectType.SINGLE_LEVEL_BOM_AS_PLANNED.toString())
                               .build();
        }

        private boolean thereIsMeasurementUnit() {
            return this.quantity != null && this.quantity.getMeasurementUnit() != null;
        }

        private boolean thereIsQuantity() {
            return this.quantity != null;
        }

        /**
         * Quantity
         */
        @Data
        @Jacksonized
        /* package */ static class Quantity {

            private Double quantityNumber;
            private MeasurementUnit measurementUnit;

            /**
             * MeasurementUnit
             */
            @Data
            @Jacksonized
            /* package */ static class MeasurementUnit {
                private String lexicalValue;
                private String datatypeURI;
            }
        }
    }


}
