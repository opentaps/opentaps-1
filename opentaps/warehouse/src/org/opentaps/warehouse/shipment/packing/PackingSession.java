/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */

/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/

/* This file may contain code which has been modified from that included with the Apache-licensed OFBiz product application */
/* This file has been modified by Open Source Strategies, Inc. */

package org.opentaps.warehouse.shipment.packing;

import java.math.BigDecimal;
import java.util.*;

import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.shipment.packing.PackingEvent;
import org.ofbiz.shipment.packing.PackingSessionLine;
import org.opentaps.domain.DomainsLoader;
import org.opentaps.base.entities.CarrierShipmentBoxType;
import org.opentaps.domain.order.Order;
import org.opentaps.domain.order.OrderItem;
import org.opentaps.domain.order.OrderRepositoryInterface;
import org.opentaps.domain.shipping.ShippingRepositoryInterface;
import org.opentaps.foundation.entity.EntityNotFoundException;
import org.opentaps.foundation.exception.FoundationException;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.RepositoryException;

/**
 * Class to extend OFBiz PackingSession in order to provide additional Warehouse application-specific support.
 *
 * @author     <a href="mailto:cliberty@opensourcestrategies.com">Chris Liberty</a>
 * @version    $Rev$
 */
public class PackingSession extends org.ofbiz.shipment.packing.PackingSession implements java.io.Serializable {

    private static final String MODULE = PackingSession.class.getName();

    protected Map<String, String> packageTrackingCodes;
    protected Map<String, String> packageBoxTypeIds;
    protected String additionalShippingChargeDescription;

    /**
     * Creates a new <code>PackingSession</code> instance.
     *
     * @param dispatcher a <code>LocalDispatcher</code> value
     * @param userLogin a <code>GenericValue</code> value
     * @param facilityId a <code>String</code> value
     * @param binId a <code>String</code> value
     * @param orderId a <code>String</code> value
     * @param shipGrp a <code>String</code> value
     */
    public PackingSession(LocalDispatcher dispatcher, GenericValue userLogin, String facilityId, String binId, String orderId, String shipGrp) {
        super(dispatcher, userLogin, facilityId, binId, orderId, shipGrp);
        this.packageTrackingCodes = new HashMap<String, String>();
        this.packageBoxTypeIds = new HashMap<String, String>();
    }

    /**
     * Creates a new <code>PackingSession</code> instance.
     *
     * @param dispatcher a <code>LocalDispatcher</code> value
     * @param userLogin a <code>GenericValue</code> value
     * @param facilityId a <code>String</code> value
     */
    public PackingSession(LocalDispatcher dispatcher, GenericValue userLogin, String facilityId) {
        this(dispatcher, userLogin, facilityId, null, null, null);
    }

    /**
     * Creates a new <code>PackingSession</code> instance.
     *
     * @param dispatcher a <code>LocalDispatcher</code> value
     * @param userLogin a <code>GenericValue</code> value
     */
    public PackingSession(LocalDispatcher dispatcher, GenericValue userLogin) {
        this(dispatcher, userLogin, null, null, null, null);
    }

    // Override the line item creation to add our custom OpentapsPackingSessionLine.
    protected void createPackLineItem(int checkCode, GenericValue res, String orderId, String orderItemSeqId, String shipGroupSeqId, String productId, BigDecimal quantity, BigDecimal weight, int packageSeqId) throws GeneralException {
        // process the result; add new item if necessary
        switch (checkCode) {
            case 0:
                // not enough reserved
                throw new GeneralException("Not enough inventory reservation available; cannot pack the item [" + orderItemSeqId + "], " + quantity + " x product [" + productId + "]");
            case 1:
                // we're all good to go; quantity already updated
                break;
            case 2:
                // need to create a new item
                String invItemId = res.getString("inventoryItemId");
                packLines.add(new OpentapsPackingSessionLine(orderId, orderItemSeqId, shipGroupSeqId, productId, invItemId, quantity, weight, packageSeqId));
                break;
            default:
                throw new GeneralException("Unrecognized checkCode [" + checkCode + "]");
        }

        // Add the line weight to the package weight
        if (weight.signum() > 0) {
            this.addToPackageWeight(packageSeqId, weight);
        }

        // update the package sequence
        if (packageSeqId > packageSeq) {
            this.packageSeq = packageSeqId;
        }
    }

    /**
     * Gets the tracking code for the given package.
     * @param packageSeqId a package ID
     * @return the tracking code for the given package, or <code>null</code> if not found
     */
    public String getPackageTrackingCode(String packageSeqId) {
        if (this.packageTrackingCodes == null) {
            return null;
        }
        if (!this.packageTrackingCodes.containsKey(packageSeqId)) {
            return null;
        }
        return this.packageTrackingCodes.get(packageSeqId);
    }

    /**
     * Sets the tracking code for the given package.
     * @param packageSeqId a package ID
     * @param packageTrackingCode the tracking code to set
     */
    public void setPackageTrackingCode(String packageSeqId, String packageTrackingCode) {
        if (UtilValidate.isEmpty(packageTrackingCode)) {
            packageTrackingCodes.remove(new Integer(packageSeqId));
        } else {
            packageTrackingCodes.put(packageSeqId, packageTrackingCode);
        }
    }

    /**
     * Gets the box type ID for the given package.
     * @param packageSeqId a package ID
     * @return the box type ID for the given package, <code>null</code> if not found
     */
    public String getPackageBoxTypeId(String packageSeqId) {
        if (this.packageBoxTypeIds != null) {
            if (this.packageBoxTypeIds.containsKey(packageSeqId)) {
                return this.packageBoxTypeIds.get(packageSeqId);
            }
        }
        return null;
    }

    /**
     * Gets the box type ID for the given package, if not set gets the default box type ID.
     * @param packageSeqId a package ID
     * @return the box type ID for the given package, or the default if not found
     */
    public String getPackageBoxTypeOrDefaultId(String packageSeqId) {
        String boxTypeId = getPackageBoxTypeId(packageSeqId);

        // if no box is set, try the default one
        if (UtilValidate.isEmpty(boxTypeId)) {
            try {
                CarrierShipmentBoxType defaultBoxType = getDefaultShipmentBoxType(Integer.parseInt(packageSeqId));
                if (defaultBoxType != null) {
                    boxTypeId = defaultBoxType.getShipmentBoxTypeId();
                }
            } catch (Exception e) {
                Debug.logError("Could not get the default box for package [" + packageSeqId + "] : " + e, MODULE);
            }
        }

        return boxTypeId;
    }

    /**
     * Sets the box type ID for the given package.
     * @param packageSeqId a package ID
     * @param packageBoxTypeId the box type ID to set
     */
    public void setPackageBoxTypeId(String packageSeqId, String packageBoxTypeId) {
        if (UtilValidate.isEmpty(packageBoxTypeId)) {
            packageBoxTypeIds.remove(new Integer(packageSeqId));
        } else {
            packageBoxTypeIds.put(packageSeqId, packageBoxTypeId);
        }
    }

    @Override public void clear() {
        super.clear();
        if (this.packageTrackingCodes != null) {
            this.packageTrackingCodes.clear();
        }
        if (this.packageBoxTypeIds != null) {
            this.packageBoxTypeIds.clear();
        }
        this.additionalShippingChargeDescription = null;
    }

    @Override public String complete(boolean force) throws GeneralException {
        String shipmentId = super.complete(force);
        updateShipmentPackageRouteSegments();
        updateShipmentPackages();
        return shipmentId;
    }

    @SuppressWarnings("unchecked")
    @Override public BigDecimal getCurrentReservedQuantity(String orderId, String orderItemSeqId, String shipGroupSeqId, String productId) {
        BigDecimal reserved = BigDecimal.ZERO;
        try {
            List<GenericValue> reservations = getDelegator().findByAnd("OrderItemShipGrpInvResAndItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "shipGroupSeqId", shipGroupSeqId, "facilityId", facilityId, "productId", productId));
            for (GenericValue res : reservations) {
                BigDecimal not = res.getBigDecimal("quantityNotAvailable");
                BigDecimal qty = res.getBigDecimal("quantity");
                if (not == null) {
                    not = BigDecimal.ZERO;
                }
                if (qty == null) {
                    qty = BigDecimal.ZERO;
                }
                reserved = reserved.add(qty).subtract(not);
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, MODULE);
        }

        return reserved;
    }

    @Override protected void createShipment() throws GeneralException {
        super.createShipment();
        GenericValue shipment = getDelegator().findByPrimaryKey("Shipment", UtilMisc.toMap("shipmentId", this.getShipmentId()));
        if (UtilValidate.isNotEmpty(shipment) && UtilValidate.isNotEmpty(this.getAdditionalShippingChargeDescription())) {
            shipment.set("addtlShippingChargeDesc", this.getAdditionalShippingChargeDescription());
            shipment.store();
        }
    }

    @SuppressWarnings("unchecked")
    protected void updateShipmentPackageRouteSegments() throws GeneralException {
        List<GenericValue> shipmentPackageRouteSegments = getDelegator().findByAnd("ShipmentPackageRouteSeg", UtilMisc.toMap("shipmentId", this.getShipmentId()));
        if (UtilValidate.isNotEmpty(shipmentPackageRouteSegments)) {
            Iterator<GenericValue> sprit = shipmentPackageRouteSegments.iterator();
            while (sprit.hasNext()) {
                GenericValue shipmentPackageRouteSegment = sprit.next();
                if (UtilValidate.isEmpty(shipmentPackageRouteSegment.get("shipmentPackageSeqId"))) {
                    continue;
                }

                // Make the string into an Integer to remove leading zeros
                Integer shipmentPackageSeqId = Integer.valueOf(shipmentPackageRouteSegment.getString("shipmentPackageSeqId"));
                String trackingCode = getPackageTrackingCode(shipmentPackageSeqId.toString());
                shipmentPackageRouteSegment.set("trackingCode", trackingCode);
            }
            getDelegator().storeAll(shipmentPackageRouteSegments);
        }
    }

    @SuppressWarnings("unchecked")
    protected void updateShipmentPackages() throws GeneralException {
        List<GenericValue> shipmentPackages = getDelegator().findByAnd("ShipmentPackage", UtilMisc.toMap("shipmentId", this.getShipmentId()));
        if (UtilValidate.isNotEmpty(shipmentPackages)) {
            Iterator<GenericValue> spit = shipmentPackages.iterator();
            while (spit.hasNext()) {
                GenericValue shipmentPackage = spit.next();

                // Make the string into an Integer to remove leading zeros
                Integer shipmentPackageSeqId = Integer.valueOf(shipmentPackage.getString("shipmentPackageSeqId"));
                String boxTypeId = getPackageBoxTypeId(shipmentPackageSeqId.toString());
                shipmentPackage.set("shipmentBoxTypeId", boxTypeId);
            }
            getDelegator().storeAll(shipmentPackages);
        }
    }

    @SuppressWarnings("unchecked")
    public List<PackingSessionLine> getLinesByPackage(int packageSeqId) {
        List<PackingSessionLine> lines = new ArrayList<PackingSessionLine>();
        Iterator<PackingSessionLine> lit = this.getLines().iterator();
        while (lit.hasNext()) {
            PackingSessionLine line = lit.next();
            if (line.getPackageSeq() == packageSeqId) {
                lines.add(line);
            }
        }
        return lines;
    }

    @SuppressWarnings("unchecked")
    public List getShippableItemInfo(int packageSeqId) {
        List shippableItemInfo = new ArrayList();
        List<PackingSessionLine> lines;
        BigDecimal packageWeight = null;
        if (packageSeqId == -1) {
            lines = getLines();
            packageWeight = getTotalWeight();
        } else {
            lines = getLinesByPackage(packageSeqId);
            packageWeight = getPackageWeight(packageSeqId);
        }
        if (UtilValidate.isEmpty(packageWeight)) {
            packageWeight = BigDecimal.ZERO;
        }
        Iterator<PackingSessionLine> lit = lines.iterator();
        while (lit.hasNext()) {
            PackingSessionLine line = lit.next();
            Map shipInfo = new HashMap();
            shipInfo.put("productId", line.getProductId());
            shipInfo.put("weight", new Double(packageWeight.doubleValue() / lines.size()));
            shipInfo.put("quantity", new Double(line.getQuantity().doubleValue()));
            shipInfo.put("piecesIncluded", new Long(1));
            shippableItemInfo.add(shipInfo);
        }
        return shippableItemInfo;
    }

    @SuppressWarnings("unchecked")
    @Override public void addOrIncreaseLine(String orderId, String orderItemSeqId, String shipGroupSeqId, String productId, BigDecimal quantity, int packageSeqId, BigDecimal weight, boolean update) throws GeneralException {
        // reset the session if we just completed
        if (status == 0) {
            throw new GeneralException("Packing session has been completed; be sure to CLEAR before packing a new order! [000]");
        }

        // do nothing if we are trying to add a quantity of 0
        if (!update && quantity.signum() == 0) {
            return;
        }

        // find the actual product ID
        productId = ProductWorker.findProductId(this.getDelegator(), productId);

        // set the default null values - primary is the assumed first item
        if (orderId == null) {
            orderId = primaryOrderId;
        }
        if (shipGroupSeqId == null) {
            shipGroupSeqId = primaryShipGrp;
        }
        if (orderItemSeqId == null && productId != null) {
            orderItemSeqId = this.findOrderItemSeqId(productId, orderId, shipGroupSeqId, quantity);
        }

        // get the reservations for the item
        Map invLookup = FastMap.newInstance();
        invLookup.put("orderId", orderId);
        invLookup.put("orderItemSeqId", orderItemSeqId);
        invLookup.put("shipGroupSeqId", shipGroupSeqId);
        if (UtilValidate.isNotEmpty(facilityId)) {
            invLookup.put("facilityId", facilityId);
        }

        List<GenericValue> reservations = this.getDelegator().findByAnd("OrderItemShipGrpInvResAndItem", invLookup, UtilMisc.toList("quantity DESC"));

        // no reservations we cannot add this item
        if (UtilValidate.isEmpty(reservations)) {
            throw new GeneralException("No inventory reservations available; cannot pack this item! [101]");
        }

        // find the inventoryItemId to use
        if (reservations.size() == 1) {
            GenericValue res = EntityUtil.getFirst(reservations);
            int checkCode = this.checkLineForAdd(res, orderId, orderItemSeqId, shipGroupSeqId, productId, quantity, packageSeqId, update);
            this.createPackLineItem(checkCode, res, orderId, orderItemSeqId, shipGroupSeqId, productId, quantity, weight, packageSeqId);
        } else {
            // more than one reservation found
            Map<GenericValue, BigDecimal> toCreateMap = FastMap.newInstance();
            Iterator<GenericValue> i = reservations.iterator();
            BigDecimal qtyRemain = quantity;

            // we need to prioritize the reservations that have quantity available
            while (i.hasNext() && qtyRemain.signum() > 0) {
                GenericValue res = i.next();
                BigDecimal resQty = res.getBigDecimal("quantity");
                BigDecimal resQtyNa = res.getBigDecimal("quantityNotAvailable");
                if (resQtyNa == null) {
                    resQtyNa = BigDecimal.ZERO;
                }

                if (resQtyNa.signum() > 0) {
                    Debug.logInfo("Skipping reservations with quantityNotAvailable on the first pass.", MODULE);
                    continue;
                }

                BigDecimal resPackedQty = this.getPackedQuantity(orderId, orderItemSeqId, shipGroupSeqId, productId, res.getString("inventoryItemId"), -1);
                if (resPackedQty.compareTo(resQty) >= 0) {
                    continue;
                } else if (!update) {
                    resQty = resQty.subtract(resPackedQty);
                }

                BigDecimal thisQty = (resQty.compareTo(qtyRemain) > 0) ? qtyRemain : resQty;

                int thisCheck = this.checkLineForAdd(res, orderId, orderItemSeqId, shipGroupSeqId, productId, thisQty, packageSeqId, update);
                switch (thisCheck) {
                    case 2:
                        Debug.log("Packing check returned '2' - new pack line will be created!", MODULE);
                        toCreateMap.put(res, thisQty);
                        qtyRemain = qtyRemain.subtract(thisQty);
                        break;
                    case 1:
                        Debug.log("Packing check returned '1' - existing pack line has been updated!", MODULE);
                        qtyRemain = qtyRemain.subtract(thisQty);
                        break;
                    case 0:
                        Debug.log("Packing check returned '0' - doing nothing.", MODULE);
                        break;
                    default:
                        throw new GeneralException("Unrecognized checkCode [" + thisCheck + "]");
                }
            }
            // second pass considering reservations with quantity not available
            i = reservations.iterator();
            while (i.hasNext() && qtyRemain.signum() > 0) {
                GenericValue res = i.next();
                BigDecimal resQty = res.getBigDecimal("quantity");
                BigDecimal resQtyNa = res.getBigDecimal("quantityNotAvailable");
                if (resQtyNa == null) {
                    resQtyNa = BigDecimal.ZERO;
                }

                if (resQtyNa.signum() == 0) {
                    Debug.logInfo("Skipping reservations without quantityNotAvailable on the second pass.", MODULE);
                    continue;
                }

                BigDecimal resPackedQty = this.getPackedQuantity(orderId, orderItemSeqId, shipGroupSeqId, productId, res.getString("inventoryItemId"), -1);
                if (resPackedQty.compareTo(resQty) >= 0) {
                    continue;
                } else if (!update) {
                    resQty = resQty.subtract(resPackedQty);
                }

                BigDecimal thisQty = (resQty.compareTo(qtyRemain) > 0) ? qtyRemain : resQty;

                int thisCheck = this.checkLineForAdd(res, orderId, orderItemSeqId, shipGroupSeqId, productId, thisQty, packageSeqId, update);
                switch (thisCheck) {
                    case 2:
                        Debug.log("Packing check returned '2' - new pack line will be created!", MODULE);
                        toCreateMap.put(res, thisQty);
                        qtyRemain = qtyRemain.subtract(thisQty);
                        break;
                    case 1:
                        Debug.log("Packing check returned '1' - existing pack line has been updated!", MODULE);
                        qtyRemain = qtyRemain.subtract(thisQty);
                        break;
                    case 0:
                        Debug.log("Packing check returned '0' - doing nothing.", MODULE);
                        break;
                    default:
                        throw new GeneralException("Unrecognized checkCode [" + thisCheck + "]");
                }
            }

            if (qtyRemain.signum() == 0) {
                Iterator x = toCreateMap.keySet().iterator();
                while (x.hasNext()) {
                    GenericValue res = (GenericValue) x.next();
                    BigDecimal qty = toCreateMap.get(res);
                    this.createPackLineItem(2, res, orderId, orderItemSeqId, shipGroupSeqId, productId, qty, weight, packageSeqId);
                }
            } else {
                throw new GeneralException("Not enough inventory reservation available; cannot pack the item! [103]");
            }
        }

        // run the add events
        this.runEvents(PackingEvent.EVENT_CODE_ADD);
    }

    @SuppressWarnings("unchecked")
    public BigDecimal getShipmentCostEstimate(String shippingContactMechId, String shipmentMethodTypeId, String carrierPartyId, String carrierRoleTypeId, String productStoreId) {
        BigDecimal shipmentCostEstimate = BigDecimal.ZERO;
        List packageSeqIds = getPackageSeqIds();
        Iterator psiit = packageSeqIds.iterator();
        while (psiit.hasNext()) {
            int packageSeqId = ((Integer) psiit.next()).intValue();
            BigDecimal packageEstimate = getShipmentCostEstimate(shippingContactMechId, shipmentMethodTypeId, carrierPartyId, carrierRoleTypeId, productStoreId, packageSeqId);
            if (UtilValidate.isNotEmpty(packageEstimate)) {
                shipmentCostEstimate = shipmentCostEstimate.add(packageEstimate);
            }
        }
        return shipmentCostEstimate;
    }

    @SuppressWarnings("unchecked")
    public BigDecimal getShipmentCostEstimate(String shippingContactMechId, String shipmentMethodTypeId, String carrierPartyId, String carrierRoleTypeId,
                                          String productStoreId, int packageSeqId) {
        BigDecimal packageWeight = getPackageWeight(packageSeqId);
        if (UtilValidate.isEmpty(packageWeight)) {
            packageWeight = BigDecimal.ZERO;
        }
        Map shipEstimate = ShippingEvents.getShipGroupEstimate(this.getDispatcher(), this.getDelegator(), null, shipmentMethodTypeId, carrierPartyId,
                                                               carrierRoleTypeId, shippingContactMechId, productStoreId, getShippableItemInfo(packageSeqId),
                                                               packageWeight, getPackedQuantity(packageSeqId), BigDecimal.ZERO, /* party */ null, /* productStoreShipMethId */ null);
        return (BigDecimal) shipEstimate.get("shippingTotal");
    }

    @SuppressWarnings("unchecked")
    public BigDecimal getShipmentCostEstimate(GenericValue orderItemShipGroup, String productStoreId, BigDecimal shippableTotal, BigDecimal shippableWeight, BigDecimal shippableQuantity) {
        String shippingContactMechId = orderItemShipGroup.getString("contactMechId");
        String shipmentMethodTypeId = orderItemShipGroup.getString("shipmentMethodTypeId");
        String carrierPartyId = orderItemShipGroup.getString("carrierPartyId");
        String carrierRoleTypeId = orderItemShipGroup.getString("carrierRoleTypeId");
        List shippableItemInfo = getShippableItemInfo(-1);

        if (UtilValidate.isEmpty(shippableWeight)) {
            shippableWeight = getTotalWeight();
        }
        if (UtilValidate.isEmpty(shippableQuantity)) {
            shippableQuantity = getPackedQuantity(-1);
        }
        if (UtilValidate.isEmpty(shippableTotal)) {
            shippableTotal = BigDecimal.ZERO;
        }
        Map shipEstimate = ShippingEvents.getShipGroupEstimate(this.getDispatcher(), this.getDelegator(), null, shipmentMethodTypeId, carrierPartyId,
                                                               carrierRoleTypeId, shippingContactMechId, productStoreId, shippableItemInfo,
                                                               shippableWeight, shippableQuantity, BigDecimal.ZERO, /* party */ null, /* productStoreShipMethId */ null);
        return (BigDecimal) shipEstimate.get("shippingTotal");
    }

    @SuppressWarnings("unchecked")
    public BigDecimal getRawPackageValue(Delegator delegator, int packageSeqId) throws GenericEntityException {
        List<PackingSessionLine> lines = getLinesByPackage(packageSeqId);
        BigDecimal value = OpentapsPackingSessionLine.ZERO;
        for (Iterator<PackingSessionLine> iter = lines.iterator(); iter.hasNext();) {
            OpentapsPackingSessionLine line = (OpentapsPackingSessionLine) iter.next();
            value = value.add(line.getRawValue(delegator));
        }
        return value;
    }

    public String getAdditionalShippingChargeDescription() {
        return additionalShippingChargeDescription;
    }

    public void setAdditionalShippingChargeDescription(String additionalShippingChargeDescription) {
        this.additionalShippingChargeDescription = additionalShippingChargeDescription;
    }

    @SuppressWarnings("unchecked")
    public Map<String, BigDecimal> getProductQuantities() {
        Map<String, BigDecimal> productIds = new HashMap<String, BigDecimal>();
        Iterator<PackingSessionLine> lit = getLines().iterator();
        while (lit.hasNext()) {
            PackingSessionLine line = lit.next();
            BigDecimal quantity = productIds.containsKey(line.getProductId()) ? productIds.get(line.getProductId()) : BigDecimal.ZERO;
            productIds.put(line.getProductId(), quantity.add(line.getQuantity()));
        }
        return productIds;
    }

    public CarrierShipmentBoxType getDefaultShipmentBoxType(int packageSeqId) throws InfrastructureException, RepositoryException, EntityNotFoundException {
        List<PackingSessionLine> lines = getLinesByPackage(packageSeqId);
        List<OrderItem> items = new ArrayList<OrderItem>();
        OrderRepositoryInterface orderRepository = getOrderRepository();
        // find the list of order items to this package
        for (PackingSessionLine line : lines) {
            String orderId = line.getOrderId();
            String orderItemSeqId = line.getOrderItemSeqId();
            try {
                Order order = orderRepository.getOrderById(orderId);
                items.add(orderRepository.getOrderItem(order, orderItemSeqId));
            } catch (FoundationException e) {
                Debug.logError("Could not find order item [" + orderId + " / " + orderItemSeqId + "]", MODULE);
            }

        }
        ShippingRepositoryInterface shippingRepository = getShippingRepository();
        // find the default box for this package
        return shippingRepository.getDefaultBoxType(items);
    }

    private OrderRepositoryInterface getOrderRepository() throws InfrastructureException, RepositoryException {
        DomainsLoader domainsLoader = new DomainsLoader(new Infrastructure(getDispatcher()), new User(userLogin));
        return domainsLoader.loadDomainsDirectory().getOrderDomain().getOrderRepository();
    }

    private ShippingRepositoryInterface getShippingRepository() throws InfrastructureException, RepositoryException {
        DomainsLoader domainsLoader = new DomainsLoader(new Infrastructure(getDispatcher()), new User(userLogin));
        return domainsLoader.loadDomainsDirectory().getShippingDomain().getShippingRepository();
    }

}
