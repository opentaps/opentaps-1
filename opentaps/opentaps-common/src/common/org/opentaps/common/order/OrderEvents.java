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

/* This file has been modified by Open Source Strategies, Inc. */

package org.opentaps.common.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.shoppingcart.CartItemModifyException;
import org.ofbiz.order.shoppingcart.ItemNotFoundException;
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.ofbiz.order.shoppingcart.shipping.ShippingEvents;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.product.config.ProductConfigWrapper;
import org.ofbiz.product.store.ProductStoreSurveyWrapper;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.base.entities.ContactMech;
import org.opentaps.base.entities.OrderContactMech;
import org.opentaps.base.entities.OrderHeaderNoteView;
import org.opentaps.base.entities.OrderTerm;
import org.opentaps.base.entities.PostalAddress;
import org.opentaps.base.entities.SupplierProduct;
import org.opentaps.base.entities.TelecomNumber;
import org.opentaps.common.order.shoppingcart.OpentapsShippingEstimateWrapper;
import org.opentaps.common.order.shoppingcart.OpentapsShoppingCart;
import org.opentaps.common.order.shoppingcart.OpentapsShoppingCartHelper;
import org.opentaps.common.party.PartyContactHelper;
import org.opentaps.common.party.PartyNotFoundException;
import org.opentaps.common.party.PartyReader;
import org.opentaps.common.util.UtilAccountingTags;
import org.opentaps.common.util.UtilCommon;
import org.opentaps.common.util.UtilConfig;
import org.opentaps.common.util.UtilDate;
import org.opentaps.common.util.UtilMessage;
import org.opentaps.domain.DomainsDirectory;
import org.opentaps.domain.DomainsLoader;
import org.opentaps.domain.billing.payment.Payment;
import org.opentaps.domain.order.Order;
import org.opentaps.domain.order.OrderAdjustment;
import org.opentaps.domain.order.OrderItemShipGroup;
import org.opentaps.domain.order.OrderRepositoryInterface;
import org.opentaps.domain.organization.AccountingTagConfigurationForOrganizationAndUsage;
import org.opentaps.domain.party.Party;
import org.opentaps.domain.party.PartyRepositoryInterface;
import org.opentaps.domain.purchasing.PurchasingRepositoryInterface;
import org.opentaps.foundation.entity.EntityNotFoundException;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.InfrastructureException;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.RepositoryException;

/**
 * Common order events such as destroying the cart, etc.
 *
 */
public final class OrderEvents {

    private OrderEvents() { }

    private static final String MODULE = OrderEvents.class.getName();
    private static final String PURCHASING_LABEL = "PurchasingUiLabels";

    /**
     * Custom cart destroy method to avoid null pointer exception with ShoppingCartEvent.destroyCart().
     * The reason is because ShoppingCartEvent.destroyCart() uses clearCart(), which
     * is bad logic:  The clearCart() method calls getCartObject(), which will create a cart if
     * none is found, thus leading to a situation where an empty cart is created and then destroyed
     * when there is no cart to begin with. Normally this would be fine except that creating a cart
     * requires that productStoreId be in the session.  If it isn't, then a null pointer exception occurs.
     * It's probably a good idea to fix the cart system to handle this issue.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @param applicationName a <code>String</code> value
     * @return a <code>String</code> value
     */
    public static String destroyCart(HttpServletRequest request, HttpServletResponse response, String applicationName) {
        HttpSession session = request.getSession(true);

        // set the product store in session to avoid null pointer crash
        String productStoreId = (String) session.getAttribute("productStoreId");
        if (productStoreId == null) {
            productStoreId = UtilConfig.getPropertyValue(applicationName, applicationName + ".order.productStoreId");
            if (productStoreId != null) {
                session.setAttribute("productStoreId", productStoreId);
            } else {
                Debug.logWarning("No productStoreId found in " + applicationName + ".properties -- this could cause problems", MODULE);
                return "success";
            }
        }

        // erase the tracking code
        session.removeAttribute("trackingCodeId");

        // call the legacy method
        return ShoppingCartEvents.destroyCart(request, response);
    }

    /**
     * Custom add order item method.  This is a simplification of ShoppingCartEvents.addToCart() which validates
     * fields specific to the order form presented in crmsfa.  It also detects whether a configurable or virtual
     * product with variants is submitted, then redirects user to a page where the configuration and variants
     * can be selected.  If the entered product does not match anything, this will redirect to a lookup page.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return a <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String addOrderItem(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession(true);
        TimeZone timeZone = UtilCommon.getTimeZone(request);
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        OpentapsShoppingCart cart = getCart(request);
        if (cart == null) {
            Debug.logWarning("Cart is not defined, unable to add order items.", MODULE);
            UtilMessage.addInternalError(request);
            return "error";
        }
        Locale locale = cart.getLocale();

        String productId = UtilCommon.getParameter(request, "productId");
        if (productId == null) {
            productId = UtilCommon.getParameter(request, "add_product_id"); // some legacy forms use this
            if (productId == null) {
                UtilMessage.addRequiredFieldError(request, "productId");
                return "error";
            }
        }

        // find Product match using comprehensive search
        GenericValue product = null;
        try {
            Map results = dispatcher.runSync("getProductByComprehensiveSearch", UtilMisc.toMap("productId", productId));
            if (ServiceUtil.isError(results) || ServiceUtil.isFailure(results)) {
                return UtilMessage.createAndLogEventError(request, results, locale, MODULE);
            }
            product = (GenericValue) results.get("product");
            productId = (String) results.get("productId");
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        // if it's a variant product, send to virtualProduct request
        if (product != null && "Y".equals(product.get("isVirtual"))) {
            request.setAttribute("product_id", productId);
            return "virtualProduct";
        }
        
        // still no product, send to lookup request
        if (product == null) {
            return "findMatchingProducts";
        }

        // if the product is configurable or virtual, send user to appropriate page
        if ("AGGREGATED".equals(product.get("productTypeId"))) {
            return "configureProduct";
        } else if ("Y".equals(product.get("isVirtual"))) {
            return "chooseVariantProduct";
        }

        // validate quantity
        double quantity = 0;
        String quantityString = UtilCommon.getParameter(request, "quantity");
        if (quantityString == null) {
            UtilMessage.addRequiredFieldError(request, "quantity");
            return "error";
        }
        try {
            quantity = Double.parseDouble(quantityString);
        } catch (NumberFormatException e) {
            UtilMessage.addFieldError(request, "quantity", "OpentapsFieldError_BadDoubleFormat");
            return "error";
        }

        // validate the desired delivery date
        Timestamp shipBeforeDate = null;
        String shipBeforeDateString = UtilCommon.getParameter(request, "shipBeforeDate");
        if (shipBeforeDateString != null) {
            try {
                shipBeforeDate = UtilDateTime.getDayEnd(UtilDateTime.stringToTimeStamp(shipBeforeDateString, UtilDateTime.getDateFormat(locale), timeZone, locale), timeZone, locale);
            } catch (ParseException e) {
                UtilMessage.addFieldError(request, "shipBeforeDate", UtilMessage.expandLabel("OpentapsFieldError_BadDateFormat", locale, UtilMisc.toMap("format", UtilDateTime.getDateFormat(locale))));
                return "error";
            }
        }

        // build the attributes map
        Map attributes = FastMap.newInstance();

        // validate the accounting tags
        Map tags = new HashMap<String, String>();
        UtilAccountingTags.addTagParameters(request, tags);
        try {
            DomainsLoader domainLoader = new DomainsLoader(new Infrastructure(dispatcher), new User(userLogin));
            DomainsDirectory dd = domainLoader.loadDomainsDirectory();
            OrderRepositoryInterface orderRepository = dd.getOrderDomain().getOrderRepository();
            List<AccountingTagConfigurationForOrganizationAndUsage> missings = orderRepository.validateTagParameters(cart, tags, UtilAccountingTags.TAG_PARAM_PREFIX, productId);
            if (!missings.isEmpty()) {
                for (AccountingTagConfigurationForOrganizationAndUsage missingTag : missings) {
                    UtilMessage.addError(request, "OpentapsError_ServiceErrorRequiredTagNotFound", UtilMisc.toMap("tagName", missingTag.getDescription()));
                    UtilMessage.addRequiredFieldError(request, missingTag.getPrefixedName(UtilAccountingTags.TAG_PARAM_PREFIX));
                }
                return "error";
            }
            // get the validated accounting tags and set them as cart item attributes
            UtilAccountingTags.addTagParameters(tags, attributes);
        } catch (GeneralException e) {
            Debug.logError(e, MODULE);
            return "error";
        }


        String comments = request.getParameter("comments");

        attributes.put("itemComment", comments);

        String isPromo = UtilCommon.getParameter(request, "isPromo");
        boolean isPromoItem = "Y".equals(isPromo);
        if (isPromoItem) {
            attributes.put("customPromo", "Y");
        }

        // handle surveys
        List<GenericValue> surveys = ProductStoreWorker.getProductSurveys(cart.getDelegator(), cart.getProductStoreId(), productId, "CART_ADD");
        if (surveys != null && surveys.size() > 0) {

            // check if a survey was submitted
            String surveyResponseId = (String) request.getAttribute("surveyResponseId");
            if (UtilValidate.isNotEmpty(surveyResponseId)) {
                // if it is then we pass it to the addOrIncreaseItem method as an attribute
                attributes.put("surveyResponses", UtilMisc.toList(surveyResponseId));
            } else {
                // set up a surveyAction and surveyWrapper, then redirect to survey
                ProductStoreSurveyWrapper wrapper = new ProductStoreSurveyWrapper(surveys.get(0), cart.getOrderPartyId(), UtilHttp.getParameterMap(request));
                request.setAttribute("surveyWrapper", wrapper);
                request.setAttribute("surveyAction", "addOrderItemSurvey");
                return "survey";
            }
        }

        // determine order item type based on ProductOrderItemType
        String itemType = null;
        try {
            itemType = UtilOrder.getOrderItemTypeId(product.getString("productTypeId"), cart.getOrderType(), cart.getDelegator());
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        // pass all the customized fields from the request parameters into the item attributes
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        ModelEntity model = delegator.getModelEntity("OrderItem");
        for (Object o : request.getParameterMap().keySet()) {
            if (!(o instanceof String)) {
                continue;
            }
            String n = (String) o;
            if (UtilCommon.isCustomEntityField(n)) {
                String v = request.getParameter(n);
                // validate
                try {
                    model.convertFieldValue(n, v, delegator);
                } catch (IllegalArgumentException e) {
                    UtilMessage.addFieldError(request, n, e.getMessage());
                    return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
                }
                attributes.put(n, v);
            }
        }

        // add the item to the cart using the simplest method
        try {
            String productCatalogId = CatalogWorker.getCurrentCatalogId(request);
            addItemToOrder(cart, productId, null, quantity, null, null, null, shipBeforeDate, null, null, attributes, productCatalogId, null, itemType, null, null, request);
        } catch (GeneralException e) {
            Debug.logError(e, "Failed to add product [" + productId + "] to cart: " + e.getMessage(), MODULE);
            UtilMessage.addError(request, "OpentapsError_CannotAddItem", UtilMisc.toMap("message", e.getMessage()));
            return "error";
        }

        return "success";
    }

    /** Add an item to the order of shopping cart, or if already there, increase the quantity.
     * @param cart a <code>ShoppingCart</code> value
     * @param productId a <code>String</code> value
     * @param selectedAmountDbl a <code>Double</code> value
     * @param quantity a <code>double</code> value
     * @param reservStart a <code>Timestamp</code> value
     * @param reservLengthDbl a <code>Double</code> value
     * @param reservPersonsDbl a <code>Double</code> value
     * @param shipBeforeDate a <code>Timestamp</code> value
     * @param shipAfterDate a <code>Timestamp</code> value
     * @param features a <code>Map</code> value
     * @param attributes a <code>Map</code> value
     * @param prodCatalogId a <code>String</code> value
     * @param configWrapper a <code>ProductConfigWrapper</code> value
     * @param itemType a <code>String</code> value
     * @param itemGroupNumber a <code>String</code> value
     * @param parentProductId a <code>String</code> value
     * @param request a <code>HttpServletRequest</code> value
     * @return the index of the item in the cart
     * @throws CartItemModifyException if an exception occur
     * @throws ItemNotFoundException if an exception occur
     * @throws RepositoryException if an exception occur
     * @throws InfrastructureException if an exception occur
     * @throws GenericServiceException if an exception occur
     * @throws GenericEntityException if an exception occur
     */
    public static int addItemToOrder(ShoppingCart cart, String productId, Double selectedAmountDbl, double quantity, Timestamp reservStart, Double reservLengthDbl, Double reservPersonsDbl,
            Timestamp shipBeforeDate, Timestamp shipAfterDate, Map features, Map attributes, String prodCatalogId,
            ProductConfigWrapper configWrapper, String itemType, String itemGroupNumber, String parentProductId, HttpServletRequest request) throws CartItemModifyException, ItemNotFoundException, RepositoryException, InfrastructureException, GenericServiceException, GenericEntityException {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        if (cart.getOrderType().equals("PURCHASE_ORDER")) {
            // check if exist the SupplierProudct, if not then add new SupplierProduct
            DomainsLoader domainLoader = new DomainsLoader(request);
            PurchasingRepositoryInterface purchasingRepository = domainLoader.loadDomainsDirectory().getPurchasingDomain().getPurchasingRepository();
            SupplierProduct supplierProduct = purchasingRepository.getSupplierProduct(cart.getPartyId(), productId, new BigDecimal(quantity), cart.getCurrency());
            if (supplierProduct == null) {
                // create a supplier product
                GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
                supplierProduct = new SupplierProduct();
                supplierProduct.setProductId(productId);
                supplierProduct.setSupplierProductId(productId);
                supplierProduct.setPartyId(cart.getPartyId());
                supplierProduct.setMinimumOrderQuantity(BigDecimal.ZERO);
                supplierProduct.setLastPrice(BigDecimal.ZERO);
                supplierProduct.setCurrencyUomId(cart.getCurrency());
                supplierProduct.setAvailableFromDate(UtilDateTime.nowTimestamp());
                supplierProduct.setComments(UtilProperties.getMessage(PURCHASING_LABEL, "PurchOrderCreateSupplierProductByUserLogin", UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId")), locale));
                //use purchasingRepository to create new SupplierProduct
                purchasingRepository.createSupplierProduct(supplierProduct);
                Debug.logInfo("created for purchase order entry by " + userLogin.getString("userLoginId") + ", productId is [" + productId + "], partyId is [" + cart.getPartyId() + "]", MODULE);
            }
        }
        //using old ofbiz code to add order item
        BigDecimal selectedAmount = null;
        if (selectedAmountDbl != null) {
            selectedAmount = BigDecimal.valueOf(selectedAmountDbl);
        }
        BigDecimal quantityBd = BigDecimal.valueOf(quantity);
        BigDecimal reservLength = null;
        if (reservLengthDbl != null) {
            reservLength = BigDecimal.valueOf(reservLengthDbl);
        }
        BigDecimal reservPersons = null;
        if (reservPersonsDbl != null) {
            reservPersons = BigDecimal.valueOf(reservPersonsDbl);
        }


        int index = cart.addOrIncreaseItem(productId, selectedAmount, quantityBd, reservStart, reservLength, reservPersons,
                shipBeforeDate, shipAfterDate, features, attributes, prodCatalogId,
                configWrapper, itemType, itemGroupNumber, parentProductId, dispatcher);
        return index;
    }


    /**
     * Custom append item to order method.
     * This is a wrapper for the edit order screen that checks for a survey if necessary.
     * Then the controller should be set to call the opentaps.appendOrderItem service on success.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return a <code>String</code> value
     */
    @SuppressWarnings("unchecked")
    public static String appendItemToOrder(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);

        String orderId = UtilCommon.getParameter(request, "orderId");
        if (orderId == null) {
            UtilMessage.addRequiredFieldError(request, "orderId");
            return "error";
        }

        OrderReadHelper orh = new OrderReadHelper(delegator, orderId);

        String productId = UtilCommon.getParameter(request, "productId");
        if (productId == null) {
            UtilMessage.addRequiredFieldError(request, "productId");
            return "error";
        }

        // find Product match using comprehensive search
        try {
            Map results = dispatcher.runSync("getProductByComprehensiveSearch", UtilMisc.toMap("productId", productId));
            if (ServiceUtil.isError(results) || ServiceUtil.isFailure(results)) {
                return UtilMessage.createAndLogEventError(request, results, locale, MODULE);
            }
            productId = (String) results.get("productId");
        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        // handle surveys
        List<GenericValue> surveys = ProductStoreWorker.getProductSurveys(delegator, orh.getProductStoreId(), productId, "CART_ADD");
        if (surveys != null && surveys.size() > 0) {

            // check if a survey was submitted
            String surveyResponseId = (String) request.getAttribute("surveyResponseId");
            if (UtilValidate.isEmpty(surveyResponseId)) {
                // set up a surveyAction and surveyWrapper, then redirect to survey
                ProductStoreSurveyWrapper wrapper = new ProductStoreSurveyWrapper(surveys.get(0), orh.getPlacingParty().getString("partyId"), UtilHttp.getParameterMap(request));
                request.setAttribute("surveyWrapper", wrapper);
                request.setAttribute("surveyAction", "appendItemToOrderSurvey");
                return "survey";
            }
        }

        return "success";
    }

    /**
     * Gets the <code>OpentapsShoppingCart</code> shopping cart from the session, or null if it did not exist.
     * @param request a <code>HttpServletRequest</code> value
     * @return a <code>ShoppingCart</code> value
     */
    public static OpentapsShoppingCart getCart(HttpServletRequest request) {
        HttpSession session = request.getSession(true);

        // if one already exists, return it
        ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
        if (cart == null) {
            return null;
        }
        if (cart instanceof OpentapsShoppingCart) {
            return (OpentapsShoppingCart) cart;
        } else {
            OpentapsShoppingCart opentapsCart = new OpentapsShoppingCart(cart);
            session.setAttribute("shoppingCart", opentapsCart);
            return opentapsCart;
        }
    }

    /**
     * Gets the <code>OpentapsShoppingCart</code> shopping cart from the session, or create it.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @return a <code>ShoppingCart</code> value
     */
    public static ShoppingCart getOrInitializeCart(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        // if one already exists, return it
        ShoppingCart cart = getCart(request);
        if (cart != null) {
            if (cart instanceof OpentapsShoppingCart) {
                return cart;
            } else {
                OpentapsShoppingCart opentapsCart = new OpentapsShoppingCart(cart);
                Debug.logWarning("Converting ShoppingCart -> OpentapsShoppingCart, reset name from [" + opentapsCart.getOrderName() + "] to [" + cart.getOrderName() + "]", MODULE);
                opentapsCart.setOrderName(cart.getOrderName());
                return opentapsCart;
            }
        }

        // get or initialize the product store, first from session, then from request
        // I think (but am not sure) that once an item has been added, the productStoreId should be in session, so this prevents someone
        // from over-writing it with a URL parameter
        String productStoreId = (String) session.getAttribute("productStoreId");
        if (productStoreId == null) {
            productStoreId = request.getParameter("productStoreId");
        }

        GenericValue productStore = null;
        try {
            productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
        } catch (GenericEntityException e) {
            Debug.logError(e, MODULE);
            return null;
        }

        if (productStore == null) {
            throw new IllegalArgumentException("Product Store with ID [" + productStoreId + "] not found.");
        }
        session.setAttribute("productStoreId", productStoreId);

        // initialize a new cart
        String webSiteId = null;
        String billFromVendorPartyId = productStore.getString("payToPartyId");
        String currencyUomId = productStore.getString("defaultCurrencyUomId");
        String billToCustomerPartyId = request.getParameter("partyId");

        OpentapsShoppingCart opentapsCart = new OpentapsShoppingCart(delegator, productStoreId, webSiteId, UtilHttp.getLocale(request), currencyUomId, billToCustomerPartyId, billFromVendorPartyId);
        opentapsCart.setOrderPartyId(billToCustomerPartyId);  // this is the actual partyId used by cart system
        opentapsCart.setOrderType("SALES_ORDER");
        session.setAttribute("shoppingCart", opentapsCart);

        // set sales channel first from request parameter, then from product store
        if (UtilValidate.isNotEmpty(request.getParameter("salesChannelEnumId"))) {
            opentapsCart.setChannelType(request.getParameter("salesChannelEnumId"));
        } else if ((productStore != null) && (UtilValidate.isNotEmpty(productStore.getString("defaultSalesChannelEnumId")))) {
            opentapsCart.setChannelType(productStore.getString("defaultSalesChannelEnumId"));
        }

        // erase any pre-existing tracking code
        session.removeAttribute("trackingCodeId");

        return opentapsCart;
    }

    /**
     * Updates a ship group.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return a <code>String</code> value
     * @exception GenericEntityException if an error occurs
     */
    public static String updateShipGroup(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        return updateShipGroup(request, response, null);
    }

    /**
     * Updates a ship group.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @param shipWrapper an <code>OpentapsShippingEstimateWrapper</code> value
     * @return a <code>String</code> value
     * @exception GenericEntityException if an error occurs
     */
    public static String updateShipGroup(HttpServletRequest request, HttpServletResponse response, OpentapsShippingEstimateWrapper shipWrapper) throws GenericEntityException {
        String shipGroupSeqIdStr = request.getParameter("shipGroupSeqId");
        int shipGroupSeqId = Integer.parseInt(shipGroupSeqIdStr);
        String contactMechId = request.getParameter("contactMechId");
        String carrierPartyId = request.getParameter("carrierPartyId");
        String shipmentMethodTypeId = request.getParameter("shipmentMethodTypeId");
        Boolean maySplit = new Boolean("Y".equals(request.getParameter("maySplit")));
        Boolean isGift = new Boolean("Y".equals(request.getParameter("isGift")));
        String shippingInstructions = request.getParameter("shippingInstructions");
        String giftMessage = request.getParameter("giftMessage");
        String shipBeforeDate = request.getParameter("shipBeforeDate");
        String thirdPartyAccountNumber = request.getParameter("thirdPartyAccountNumber");
        String thirdPartyPostalCode = request.getParameter("thirdPartyPostalCode");
        String thirdPartyCountryCode = request.getParameter("thirdPartyCountryCode");
        Boolean isCOD = new Boolean("Y".equals(request.getParameter("isCOD")));
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        ShoppingCart cart = getOrInitializeCart(request);
        return updateShipGroup(dispatcher, delegator, cart, shipGroupSeqId, contactMechId, carrierPartyId, shipmentMethodTypeId, maySplit, isGift, shippingInstructions, giftMessage, shipBeforeDate, thirdPartyAccountNumber, thirdPartyPostalCode, thirdPartyCountryCode, isCOD, shipWrapper, UtilCommon.getTimeZone(request), UtilHttp.getLocale(request));
    }

    /**
     * Describe <code>updateShipGroup</code> method here.
     *
     * @param dispatcher a <code>LocalDispatcher</code> value
     * @param delegator a <code>Delegator</code> value
     * @param cart the <code>ShoppingCart</code>
     * @param shipGroupSeqId the ID of the ship group to update
     * @param contactMechId the shipping address to set
     * @param carrierPartyId the shipping carrier to set
     * @param shipmentMethodTypeId the shipment method type to set
     * @param maySplit the may split flag for the ship group, defaults to false
     * @param isGift the is gift flag for the ship group, defaults to false
     * @param shippingInstructions the shipping instructions to set for the ship group
     * @param giftMessage the gift message if is gift
     * @param shipBeforeDateString the date to ship before
     * @param thirdPartyAccountNumber the account number for third party shipping
     * @param thirdPartyPostalCode the postal code for third party shipping
     * @param thirdPartyCountryCode the country code for third party shipping
     * @param isCOD the is cash on delivery flag for the ship group
     * @param shipWrapper an <code>OpentapsShippingEstimateWrapper</code> value
     * @param timeZone a <code>TimeZone</code> value
     * @param locale a <code>Locale</code> value
     * @return a <code>String</code> value
     * @exception GenericEntityException if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static String updateShipGroup(LocalDispatcher dispatcher, Delegator delegator, ShoppingCart cart, int shipGroupSeqId, String contactMechId, String carrierPartyId, String shipmentMethodTypeId,
            Boolean maySplit, Boolean isGift, String shippingInstructions, String giftMessage, String shipBeforeDateString,
            String thirdPartyAccountNumber, String thirdPartyPostalCode, String thirdPartyCountryCode, Boolean isCOD, OpentapsShippingEstimateWrapper shipWrapper, TimeZone timeZone, Locale locale) throws GenericEntityException {

        if (UtilValidate.isEmpty(cart)) {
            Debug.logWarning("updateShipGroup: empty cart, nothing to do", MODULE);
            return "success";
        }

        List cartShipGroups = cart.getShipGroups();
        if (UtilValidate.isEmpty(cartShipGroups)) {
            Debug.logWarning("updateShipGroup: no shipping group, nothig to do", MODULE);
            return "success";
        }

        ShoppingCart.CartShipInfo shipGroup = (ShoppingCart.CartShipInfo) cartShipGroups.get(shipGroupSeqId);
        if (UtilValidate.isEmpty(shipGroup)) {
            Debug.logWarning("updateShipGroup: empty shipGroup, nothing to do", MODULE);
            return "success";
        }

        GenericValue productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", cart.getProductStoreId()));
        boolean noShipOnDropShipGroups = "Y".equals(productStore.get("noShipOnDropShipGroups"));

        cart.setItemShipGroupEstimate(BigDecimal.ZERO, shipGroupSeqId);
        Debug.logInfo("updateShipGroup: Setting shipping method [" + shipmentMethodTypeId + "] for shipping group [" + shipGroupSeqId + "]", MODULE);
        cart.setShipmentMethodTypeId(shipGroupSeqId, shipmentMethodTypeId);
        if (noShipOnDropShipGroups && UtilValidate.isNotEmpty(cart.getSupplierPartyId(shipGroupSeqId))) {
            cart.setCarrierPartyId(shipGroupSeqId, "_NA_");
            cart.setShipmentMethodTypeId(shipGroupSeqId, "NO_SHIPPING");
        } else if ("NO_SHIPPING".equals(shipmentMethodTypeId)) {
            if (!cart.shippingApplies()) {
                GenericValue orderParty = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", cart.getPartyId()));
                if (UtilValidate.isNotEmpty(orderParty)) {
                    // use first found order party's shipping address, otherwise billing address
                    GenericValue shippingAddress = EntityUtil.getFirst(new FastList<GenericValue>(ContactHelper.getContactMech(orderParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false)));
                    if (shippingAddress == null) {
                        shippingAddress = EntityUtil.getFirst(new FastList<GenericValue>(ContactHelper.getContactMech(orderParty, "BILLING_LOCATION", "POSTAL_ADDRESS", false)));
                        if (shippingAddress == null && productStore.get("inventoryFacilityId") != null) {
                            List<GenericValue> facilityMechPurps = delegator.findByAndCache("FacilityContactMechPurpose", UtilMisc.toMap("facilityId", productStore.get("inventoryFacilityId"), "contactMechPurposeTypeId", "SHIP_ORIG_LOCATION"));
                            facilityMechPurps = EntityUtil.filterByDate(facilityMechPurps);
                            shippingAddress = EntityUtil.getFirst(facilityMechPurps);
                        }
                    }

                    if (shippingAddress != null) {
                        contactMechId = shippingAddress.getString("contactMechId");
                    }
                }
            }
            cart.setCarrierPartyId(shipGroupSeqId, "_NA_");
        } else {
            cart.setCarrierPartyId(shipGroupSeqId, carrierPartyId);
        }
        cart.setShippingContactMechId(shipGroupSeqId, contactMechId);

        cart.setMaySplit(shipGroupSeqId, UtilValidate.isNotEmpty(maySplit) ? maySplit : Boolean.FALSE);
        cart.setIsGift(shipGroupSeqId, UtilValidate.isNotEmpty(isGift) ? isGift : Boolean.FALSE);
        if (UtilValidate.isEmpty(shipGroup.carrierRoleTypeId)) {
            shipGroup.carrierRoleTypeId = "CARRIER";
        }

        boolean isCodChanged = false;
        if (cart instanceof OpentapsShoppingCart) {
            OpentapsShoppingCart opentapsCart = (OpentapsShoppingCart) cart;

            // set or unset the third party billing account information
            // only if the account is supplied will postal code and country code be set
            // if the third party billing account is null then all will be cleared
            if (UtilValidate.isNotEmpty(thirdPartyAccountNumber)) {
                opentapsCart.setThirdPartyAccountNumber(shipGroupSeqId, thirdPartyAccountNumber);
                if (UtilValidate.isNotEmpty(thirdPartyPostalCode)) {
                    opentapsCart.setThirdPartyPostalCode(shipGroupSeqId, thirdPartyPostalCode);
                }
                if (UtilValidate.isNotEmpty(thirdPartyCountryCode)) {
                    opentapsCart.setThirdPartyCountryCode(shipGroupSeqId, thirdPartyCountryCode);
                }
            } else {
                opentapsCart.setThirdPartyAccountNumber(shipGroupSeqId, null);
                opentapsCart.setThirdPartyPostalCode(shipGroupSeqId, null);
                opentapsCart.setThirdPartyCountryCode(shipGroupSeqId, null);

            }
            boolean newIsCod = UtilValidate.isNotEmpty(isCOD) && isCOD.booleanValue();
            if (opentapsCart.getCOD(shipGroupSeqId) != newIsCod) {
                isCodChanged = true;
                Debug.logInfo("Is COD changed, will force shipping estimate update", MODULE);
                opentapsCart.setCOD(shipGroupSeqId, newIsCod);
            }
        }

        cart.setShippingInstructions(shipGroupSeqId, UtilValidate.isNotEmpty(shippingInstructions) ? shippingInstructions : null);
        cart.setGiftMessage(shipGroupSeqId, UtilValidate.isNotEmpty(giftMessage) ? giftMessage : null);

        Timestamp newShipBeforeDate = null;
        if (UtilValidate.isNotEmpty(shipBeforeDateString)) {
            newShipBeforeDate = UtilDate.toTimestamp(shipBeforeDateString, timeZone, locale);

            if (UtilValidate.isEmpty(newShipBeforeDate)) {
                Debug.logError("Invalid shipBeforeDate [" + shipBeforeDateString + "] in OrderEvents.updateShipGroup() - ignoring", MODULE);
            } else {
                newShipBeforeDate = UtilDateTime.getDayEnd(newShipBeforeDate);
            }
        }

        if (UtilValidate.isEmpty(newShipBeforeDate)) {

            // If not overridden, set the shipGroup.shipBeforeDate to the earliest shipBeforeDate of the shipGroup's items
            newShipBeforeDate = getShipGroupShipBeforeDateByItem(cart, shipGroupSeqId);
        }

        cart.setShipBeforeDate(shipGroupSeqId, newShipBeforeDate);

        updateShipGroupShippingEstimate(dispatcher, delegator, cart, shipGroupSeqId, shipWrapper, isCodChanged);

        return "success";
    }

    /**
     * Updates the shipping estimate for the given ship group in the cart.
     *
     * @param dispatcher a <code>LocalDispatcher</code> value
     * @param delegator a <code>Delegator</code> value
     * @param cart a <code>ShoppingCart</code> value
     * @param shipGroupSeqId an <code>int</code> value
     * @param shipWrapper an <code>OpentapsShippingEstimateWrapper</code> value
     * @exception GenericEntityException if an error occurs
     */
    public static void updateShipGroupShippingEstimate(LocalDispatcher dispatcher, Delegator delegator, ShoppingCart cart, int shipGroupSeqId, OpentapsShippingEstimateWrapper shipWrapper) throws GenericEntityException {
        updateShipGroupShippingEstimate(dispatcher, delegator, cart, shipGroupSeqId, shipWrapper, false);
    }

    /**
     * Updates the shipping estimate for the given ship group in the cart.
     *
     * @param dispatcher a <code>LocalDispatcher</code> value
     * @param delegator a <code>Delegator</code> value
     * @param cart a <code>ShoppingCart</code> value
     * @param shipGroupSeqId an <code>int</code> value
     * @param shipWrapper an <code>OpentapsShippingEstimateWrapper</code> value
     * @param force set to <code>true</code> to force the estimate re-calculation, else only updates it if the shipping address changed
     * @exception GenericEntityException if an error occurs
     */
    public static void updateShipGroupShippingEstimate(LocalDispatcher dispatcher, Delegator delegator, ShoppingCart cart, int shipGroupSeqId, OpentapsShippingEstimateWrapper shipWrapper, boolean force) throws GenericEntityException {
        if (shipWrapper == null && cart instanceof OpentapsShoppingCart) {
            shipWrapper = ((OpentapsShoppingCart) cart).getShipEstimateWrapper(shipGroupSeqId);
        }
        if (shipWrapper == null) {
            shipWrapper = new OpentapsShippingEstimateWrapper(dispatcher, cart, shipGroupSeqId);
        }

        if (UtilValidate.isNotEmpty(shipWrapper)) {

            // Update the address of the shipping estimate wrapper, if necessary - this will trigger an update of shipping estimates
            if (force || UtilValidate.isEmpty(shipWrapper.getShippingAddress()) || !shipWrapper.getShippingAddress().getString("contactMechId").equals(cart.getShippingContactMechId(shipGroupSeqId))) {
                GenericValue shippingAddress = null;
                if (UtilValidate.isNotEmpty(cart.getShippingContactMechId(shipGroupSeqId))) {
                    shippingAddress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", cart.getShippingContactMechId(shipGroupSeqId)));
                }

                shipWrapper.setShippingAddress(shippingAddress);
            }

            // Update the shipGroup shipping estimate
            BigDecimal shipGroupEstimate = shipWrapper.getShippingEstimate(cart.getShipmentMethodTypeId(shipGroupSeqId), cart.getCarrierPartyId(shipGroupSeqId));
            if (UtilValidate.isNotEmpty(shipGroupEstimate)) {
                cart.setItemShipGroupEstimate(shipGroupEstimate, shipGroupSeqId);
            }

            if (cart instanceof OpentapsShoppingCart) {
                ((OpentapsShoppingCart) cart).setShipEstimateWrapper(shipGroupSeqId, shipWrapper);
            }
        }
    }

    /**
     * Updates the shipping estimate for all the ship groups in the cart.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return a <code>String</code> value
     * @exception GenericEntityException if an error occurs
     */
    public static String updateCartShippingEstimates(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        ShoppingCart cart = getOrInitializeCart(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        Debug.logInfo("updateCartShippingEstimates", MODULE);
        int shipGroups = cart.getShipGroupSize();
        for (int i = 0; i < shipGroups; i++) {
            updateShipGroupShippingEstimate(dispatcher, delegator, cart, i, null);
        }

        return "success";
    }

    /**
     * Gets the ship before date for the given ship group.
     *
     * @param cart a <code>ShoppingCart</code> value
     * @param shipGroupSeqId an <code>int</code> value
     * @return a <code>Timestamp</code> value
     */
    @SuppressWarnings("unchecked")
    public static Timestamp getShipGroupShipBeforeDateByItem(ShoppingCart cart, int shipGroupSeqId) {
        Timestamp shipBeforeDate = null;

        Map shipItems = cart.getShipGroupItems(shipGroupSeqId);
        if (UtilValidate.isEmpty(shipItems)) {
            return shipBeforeDate;
        }

        Iterator siit = shipItems.keySet().iterator();
        while (siit.hasNext()) {
            ShoppingCartItem item = (ShoppingCartItem) siit.next();
            Timestamp itemShipBeforeDate = item.getShipBeforeDate();
            if (UtilValidate.isEmpty(shipBeforeDate) || (UtilValidate.isNotEmpty(itemShipBeforeDate) && itemShipBeforeDate.before(shipBeforeDate))) {
                shipBeforeDate = item.getShipBeforeDate();
            }
        }

        return shipBeforeDate;
    }

    /**
     * Gets the list of estimates for the given ship group of the cart.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param shipGroupSeqId an <code>int</code> value
     * @return a <code>List</code> value
     */
    public static List getCartShipEstimates(HttpServletRequest request, int shipGroupSeqId) {
        List shipEstimates = new ArrayList();
        ShoppingCart cart = getOrInitializeCart(request);
        if (UtilValidate.isEmpty(cart)) {
            return shipEstimates;
        }
        if (UtilValidate.isEmpty(cart.getShipGroups())) {
            return shipEstimates;
        }
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        OpentapsShippingEstimateWrapper shipWrapper = null;
        if (cart instanceof OpentapsShoppingCart) {
            shipWrapper = ((OpentapsShoppingCart) cart).getShipEstimateWrapper(shipGroupSeqId);
        }
        if (shipWrapper == null) {
            shipWrapper = new OpentapsShippingEstimateWrapper(dispatcher, cart, shipGroupSeqId);
        }
        shipEstimates = getCartShipEstimates(request, cart, shipWrapper);
        return shipEstimates;
    }

    /**
     * Gets the list of estimates for each shipment method of the cart.
     *
     * @param request a <code>HttpServletRequest</code> value
     * @param cart a <code>ShoppingCart</code> value
     * @param shipWrapper an <code>OpentapsShippingEstimateWrapper</code> value
     * @return a <code>List</code> value
     */
    @SuppressWarnings("unchecked")
    public static List getCartShipEstimates(HttpServletRequest request, ShoppingCart cart, OpentapsShippingEstimateWrapper shipWrapper) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        List shipEstimates = new ArrayList();

        if (UtilValidate.isEmpty(cart)) {
            return shipEstimates;
        }
        if (UtilValidate.isEmpty(cart.getShipGroups())) {
            return shipEstimates;
        }

        List carrierShipmentMethods = shipWrapper.getShippingMethods();
        Iterator csmit = carrierShipmentMethods.iterator();
        while (csmit.hasNext()) {
            GenericValue carrierShipmentMethod = (GenericValue) csmit.next();
            String carrierPartyId = carrierShipmentMethod.getString("partyId");
            String shipmentMethodTypeId = carrierShipmentMethod.getString("shipmentMethodTypeId");

            // skip no shipment method since this is accounted for in UI
            if ("NO_SHIPPING".equals(shipmentMethodTypeId)) {
                continue;
            }

            GenericValue method = null;
            try {
                method = carrierShipmentMethod.getRelatedOne("ShipmentMethodType");
            } catch (GenericEntityException e) {
                continue;
            }
            if (method == null) {
                continue;
            }

            // If there's no estimate and the carrier party is someone meaningful, don't add it
            // to the map - that way the UI can maintain a list of available shipping methods.
            if (!"_NA_".equals(carrierPartyId) && !shipWrapper.getAllEstimates().containsKey(carrierShipmentMethod)) {
                continue;
            }

            Map shipEstimateMap = UtilMisc.toMap("carrierPartyId", carrierPartyId, "shipmentMethodTypeId", shipmentMethodTypeId, "description", carrierShipmentMethod.getString("description"));

            String carrierPartyName = PartyHelper.getPartyName(delegator, carrierPartyId, false);
            shipEstimateMap.put("carrierName", carrierPartyName);
            shipEstimateMap.put("userDescription", carrierShipmentMethod.get("userDescription"));

            BigDecimal shipEstimate = shipWrapper.getShippingEstimate(shipmentMethodTypeId, carrierPartyId);
            if (!"_NA_".equals(carrierPartyId) && shipEstimate != null && shipEstimate.doubleValue() <= 0.0) {
                shipEstimate = null; // if a carrier estimate is 0, then avoid entering the shipEstimate so the UI can print Calculated Offline
            }
            if (shipEstimate != null) {
                String shipEstimateString = UtilFormatOut.formatCurrency(shipEstimate.doubleValue(), cart.getCurrency(), UtilHttp.getLocale(request));
                shipEstimateMap.put("shipEstimate", shipEstimateString);
                shipEstimateMap.put("shipEstimateDouble", shipEstimate);
            }

            shipEstimates.add(shipEstimateMap);
        }
        return shipEstimates;
    }

    /**
     * Performs validation on the current Shipping Method Type and Shipping Address.
     *  Normally this runs before switching to the Review Order page, and on error it displays
     *  the crmsfaQuickCheckout page.
     * Validates that the shipping method is in ProductStoreShipmentMeth and that the
     *  shipping address is not empty.
     * Note that the shipping options can still be changed later on the viewOrder page, this
     *  won't be validated by this method then.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return a <code>String</code> value
     * @exception GenericEntityException if an error occurs
     */
    public static String validateOrderShippingOptions(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
        Delegator delegator = (Delegator) request.getAttribute("delegator");

        ShoppingCart cart = getOrInitializeCart(request);
        int shipGroups = cart.getShipGroupSize();
        String productStoreId = cart.getProductStoreId();
        String shipmentMethodTypeId;
        GenericValue shippingAddress;
        List<GenericValue> productStoreShipmentMeths = null;
        for (int i = 0; i < shipGroups; i++) {
            shipmentMethodTypeId = cart.getShipmentMethodTypeId(i);
            Debug.logInfo("shipGroup[" + i + "] has shipment method type = " + shipmentMethodTypeId, MODULE);
            if (UtilValidate.isEmpty(shipmentMethodTypeId)) {
                Debug.logError("No shipment method defined.", MODULE);
                UtilMessage.addError(request, "OpentapsError_ShippingMethodOrAddressMissing");
                return "error";
            } else {
                try {
                    productStoreShipmentMeths = delegator.findByAnd("ProductStoreShipmentMeth", UtilMisc.toMap("productStoreId", productStoreId, "shipmentMethodTypeId", shipmentMethodTypeId));
                } catch (GenericEntityException e) {
                    Debug.logError(e.toString(), MODULE);
                    productStoreShipmentMeths = null;
                }
            }

            // check if there is was a shipping method and it was valid for the cart product store
            if (UtilValidate.isEmpty(productStoreShipmentMeths)) {
                Debug.logError("The shipment method type [" + shipmentMethodTypeId + "] is not valid for the product store [" + productStoreId + "].", MODULE);
                UtilMessage.addError(request, "OpentapsError_ShippingMethodInvalid", UtilMisc.toMap("shipmentMethodTypeId", shipmentMethodTypeId, "productStoreId", productStoreId));
                return "error";
            }

            // now validate the shipping address
            // allows _NA_ for address unknown too
            if (!"_NA_".equals(cart.getShippingContactMechId())) {
                shippingAddress = cart.getShippingAddress(i);
                Debug.logInfo("shipGroup[" + i + "] shipping address = " + shippingAddress, MODULE);
                if (UtilValidate.isEmpty(shippingAddress)) {
                    Debug.logError("No shipping address defined.", MODULE);
                    UtilMessage.addError(request, "OpentapsError_ShippingMethodOrAddressMissing");
                    return "error";
                }
            }
        }

        return "success";
    }

    /**
     * Wrapper to support only update a postal address that is associated to an order and not to a party.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return a <code>String</code> value
     * @exception GenericEntityException if an error occurs
     * @exception GenericServiceException if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static String updatePostalAddress(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, GenericServiceException {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        HttpSession session = request.getSession();

        // validation
        for (String required : Arrays.asList("address1", "city", "postalCode")) {
            if (UtilValidate.isEmpty(UtilCommon.getParameter(request, required))) {
                UtilMessage.addError(request, "CrmError_MissingAddressFields");
                return "error";
            }
        }

        // check if we should only associate to the order (and not associate to the party)
        Boolean orderOnly = "Y".equals(UtilCommon.getParameter(request, "onlyForOrder"));
        // check if an orderId is given (shipGroupSeqId and oldContactMechId are also given to call updateOrderItemShipGroup)
        String orderId = UtilCommon.getParameter(request, "orderId");
        // shipGroupSeqId may be null if we are creating a new ship group
        String shipGroupSeqId = UtilCommon.getParameter(request, "shipGroupSeqId");
        Boolean applyToCart = false;

        String serviceName;

        if (!orderOnly) {
            // as a safety, only change the cart if the donePage is crmsfaQuickCheckout, so we are sure to be in the checkout process
            // TODO: Make a checkout postal address create/update screen that doesn't reuse the other postal address screens, so we don't need to check against this parameter
            applyToCart = "crmsfaQuickCheckout".equals(UtilCommon.getParameter(request, "donePage"));
            serviceName = "updatePartyPostalAddress";
        } else {
            if (UtilValidate.isNotEmpty(orderId)) {
                Debug.logInfo("Only updating the Contact Mech for the current order [" + orderId + "]", MODULE);
            } else {
                applyToCart = true;
                Debug.logInfo("Only updating the Contact Mech for the cart", MODULE);
            }
            serviceName = "updatePostalAddress";
        }

        // prepare the parameters for the service
        Map result;
        ModelService modelService = dispatcher.getDispatchContext().getModelService(serviceName);
        Map input = modelService.makeValid(UtilHttp.getParameterMap(request), "IN");
        input.put("userLogin", session.getAttribute("userLogin"));

        // call the service
        try {
            result = dispatcher.runSync(serviceName, input);
            if (ServiceUtil.isError(result) || ServiceUtil.isFailure(result)) {
                Debug.logError(serviceName + " error", MODULE);
                // needed to bounce back the error messages to the UI
                ServiceUtil.getMessages(request, result, null);
                return "error";
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, MODULE);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        String contactMechId = (String) result.get("contactMechId");

        // apply to order if the id was given
        if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(shipGroupSeqId)) {
            Debug.logInfo("Applying contactMech [" + contactMechId + "] to the order [" + orderId + "]", MODULE);
            // prepare the parameters for the service updateOrderItemShipGroup
            modelService = dispatcher.getDispatchContext().getModelService("updateOrderContactMech");
            input = modelService.makeValid(UtilHttp.getParameterMap(request), "IN");
            input.put("userLogin", session.getAttribute("userLogin"));
            input.put("contactMechId", contactMechId);

            // call the service updateOrderItemShipGroup
            try {
                result = dispatcher.runSync("updateOrderContactMech", input);
                if (ServiceUtil.isError(result) || ServiceUtil.isFailure(result)) {
                    Debug.logError("updateOrderContactMech error", MODULE);
                    // needed to bounce back the error messages to the UI
                    ServiceUtil.getMessages(request, result, null);
                    return "error";
                }
            } catch (GenericServiceException e) {
                Debug.logError(e, MODULE);
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
        } else if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isEmpty(shipGroupSeqId)) {
            // put the new contactMechId in the session as we did not assign it
            session.setAttribute("newContactMechId", contactMechId);
        } else if (applyToCart) {
            // check if it is a SHIPPING_LOCATION
            Boolean isShippingLocation = false;
            if (!orderOnly) {
                String partyId = UtilCommon.getParameter(request, "partyId");
                // find "SHIPPING_LOCATION" PartyContactMechPurpose with valid date
                List purposes = null;
                try {
                    purposes = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", "SHIPPING_LOCATION")), true);
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, MODULE);
                }
                if (UtilValidate.isNotEmpty(purposes)) {
                    isShippingLocation = true;
                }
            }
            // apply to cart if it is a Shipping address or if it is an order only contact mech (because then the purpose is not set)
            if (orderOnly || isShippingLocation) {
                Debug.logInfo("Applying contactMech [" + contactMechId + "] to the cart", MODULE);
                ShoppingCart cart = getOrInitializeCart(request);
                cart.setShippingContactMechId(contactMechId);
                updateCartShippingEstimates(request, response);
            }
        }

        // bounce back the success message to the UI
        ServiceUtil.getMessages(request, result, null);
        return "success";
    }

    /**
     * Wrapper for createPostalAddressAndPurpose calling the ofbiz service createPartyPostalAddress
     * and updating the cart shipping address.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return a <code>String</code> value
     * @exception GenericEntityException if an error occurs
     * @exception GenericServiceException if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static String createPartyPostalAddress(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException, GenericServiceException {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();

        // validation
        for (String required : Arrays.asList("address1", "city", "postalCode")) {
            if (UtilValidate.isEmpty(UtilCommon.getParameter(request, required))) {
                UtilMessage.addError(request, "CrmError_MissingAddressFields");
                return "error";
            }
        }

        // check if an orderId is given (shipGroupSeqId and oldContactMechId are also given to call updateOrderItemShipGroup)
        String orderId = UtilCommon.getParameter(request, "orderId");
        // shipGroupSeqId may be null if we are creating a new ship group
        String shipGroupSeqId = UtilCommon.getParameter(request, "shipGroupSeqId");
        // if so, check if we should only associate to the order (and not associate to the party)
        Boolean orderOnly = "Y".equals(UtilCommon.getParameter(request, "onlyForOrder"));
        Boolean applyToCart = false;

        String serviceName;

        if (!orderOnly) {
            // as a safety, only change the cart if the donePage is crmsfaQuickCheckout, so we are sure to be in the checkout process
            // TODO: Make a checkout postal address create/update screen that doesn't reuse the other postal address screens, so we don't need to check against this parameter
            applyToCart = "crmsfaQuickCheckout".equals(UtilCommon.getParameter(request, "donePage"));
            serviceName = "createPartyPostalAddress";
        } else {
            if (UtilValidate.isNotEmpty(orderId)) {
                Debug.logInfo("Only creating the Contact Mech for the current order [" + orderId + "]", MODULE);
            } else {
                applyToCart = true;
                Debug.logInfo("Only creating the Contact Mech for the cart", MODULE);
            }
            serviceName = "createPostalAddress";
        }

        // prepare the parameters for the service
        Map result;
        ModelService modelService = dispatcher.getDispatchContext().getModelService(serviceName);
        Map input = modelService.makeValid(UtilHttp.getParameterMap(request), "IN");
        input.put("userLogin", session.getAttribute("userLogin"));

        // call the service
        try {
            result = dispatcher.runSync(serviceName, input);
            if (ServiceUtil.isError(result) || ServiceUtil.isFailure(result)) {
                Debug.logError(serviceName + " error", MODULE);
                // needed to bounce back the error messages to the UI
                ServiceUtil.getMessages(request, result, null);
                return "error";
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, MODULE);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            return "error";
        }
        String contactMechId = (String) result.get("contactMechId");

        // apply to order if the order id and shipGroupSeqId were given
        if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isNotEmpty(shipGroupSeqId)) {
            Debug.logInfo("Applying contactMech [" + contactMechId + "] to the order [" + orderId + "]", MODULE);
            // prepare the parameters for the service updateOrderItemShipGroup
            modelService = dispatcher.getDispatchContext().getModelService("updateOrderItemShipGroup");
            input = modelService.makeValid(UtilHttp.getParameterMap(request), "IN");
            input.put("userLogin", session.getAttribute("userLogin"));
            input.put("contactMechId", contactMechId);

            // call the service updateOrderItemShipGroup
            try {
                result = dispatcher.runSync("updateOrderItemShipGroup", input);
                if (ServiceUtil.isError(result) || ServiceUtil.isFailure(result)) {
                    Debug.logError("updateOrderItemShipGroup error", MODULE);
                    // needed to bounce back the error messages to the UI
                    ServiceUtil.getMessages(request, result, null);
                    return "error";
                }
            } catch (GenericServiceException e) {
                Debug.logError(e, MODULE);
                request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
                return "error";
            }
        } else if (UtilValidate.isNotEmpty(orderId) && UtilValidate.isEmpty(shipGroupSeqId)) {
            // put the new contactMechId in the session as we did not assign it
            session.setAttribute("newContactMechId", contactMechId);
        } else if (applyToCart) {
            // apply to cart if it is a Shipping address
            if ("SHIPPING_LOCATION".equals(request.getParameter("contactMechPurposeTypeId"))) {
                Debug.logInfo("Applying contactMech [" + contactMechId + "] to the order [" + orderId + "]", MODULE);
                ShoppingCart cart = getOrInitializeCart(request);
                cart.setShippingContactMechId(contactMechId);
                updateCartShippingEstimates(request, response);
            }
        } else if ("SHIPPING_LOCATION".equals(request.getParameter("contactMechPurposeTypeId"))) {
            Debug.logInfo("Done page was not crmsfaQuickCheckout, did not applied the new shipping address to the current cart.", MODULE);
        }

        // bounce back the "address created successfully" message to the UI
        ServiceUtil.getMessages(request, result, null);
        return "success";
    }

    /**
     * Updates the items in the shopping cart.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response string
     */
    public static String modifyCart(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        Locale locale = UtilHttp.getLocale(request);
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Security security = (Security) request.getAttribute("security");
        OpentapsShoppingCartHelper cartHelper = new OpentapsShoppingCartHelper(dispatcher, cart);
        String controlDirective;
        Map result;
        // not used yet: Locale locale = UtilHttp.getLocale(request);

        Map paramMap = UtilHttp.getParameterMap(request);

        String removeSelectedFlag = request.getParameter("removeSelected");
        String[] selectedItems = request.getParameterValues("selectedItem");
        boolean removeSelected = ("true".equals(removeSelectedFlag) && selectedItems != null && selectedItems.length > 0);
        result = cartHelper.modifyCart(security, userLogin, paramMap, removeSelected, selectedItems, locale);
        controlDirective = processResult(result, request);

        // Determine where to send the browser
        if (controlDirective.equals(ERROR)) {
            return "error";
        } else {
            return "success";
        }
    }

    private static final String NO_ERROR = "noerror";
    private static final String NON_CRITICAL_ERROR = "noncritical";
    private static final String ERROR = "error";

    /**
     * This should be called to translate the error messages of the
     * <code>ShoppingCartHelper</code> to an appropriately formatted
     * <code>String</code> in the request object and indicate whether
     * the result was an error or not and whether the errors were
     * critical or not.
     *
     * @param result  the result returned from the <code>ShoppingCartHelper</code>
     * @param request the servlet request instance to set the error messages in
     * @return one of NON_CRITICAL_ERROR, ERROR or NO_ERROR.
     */
    private static String processResult(Map result, HttpServletRequest request) {
        // Check for errors
        StringBuffer errMsg = new StringBuffer();
        if (result.containsKey(ModelService.ERROR_MESSAGE_LIST)) {
            List errorMsgs = (List) result.get(ModelService.ERROR_MESSAGE_LIST);
            Iterator iterator = errorMsgs.iterator();
            errMsg.append("<ul>");
            while (iterator.hasNext()) {
                errMsg.append("<li>");
                errMsg.append(iterator.next());
                errMsg.append("</li>");
            }
            errMsg.append("</ul>");
        } else if (result.containsKey(ModelService.ERROR_MESSAGE)) {
            errMsg.append(result.get(ModelService.ERROR_MESSAGE));
            request.setAttribute("_ERROR_MESSAGE_", errMsg.toString());
        }

        // See whether there was an error
        if (errMsg.length() > 0) {
            request.setAttribute("_ERROR_MESSAGE_", errMsg.toString());
            if (result.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
                return NON_CRITICAL_ERROR;
            } else {
                return ERROR;
            }
        } else {
            return NO_ERROR;
        }
    }

    /**
     * Prepare jasper parameters for running order report.
     * @param dl a <code>DomainsLoader</code> value
     * @param delegator a <code>Delegator</code> value
     * @param dispatcher a <code>LocalDispatcher</code> value
     * @param userLogin a <code>GenericValue</code> value
     * @param locale a <code>Locale</code> value
     * @param orderId a <code>String</code> value
     * @param organizationPartyId a <code>String</code> value
     * @return the event response <code>String</code>
     * @throws GenericServiceException if an error occurs
     * @throws GenericEntityException if an error occurs
     * @throws PartyNotFoundException if an error occurs
     * @throws RepositoryException if an error occurs
     * @throws EntityNotFoundException if an error occurs
     */
    @SuppressWarnings("unchecked")
    public static Map prepareOrderReportParameters(DomainsLoader dl, Delegator delegator, LocalDispatcher dispatcher, GenericValue userLogin, Locale locale, String orderId, String organizationPartyId) throws GenericServiceException, GenericEntityException, PartyNotFoundException, RepositoryException, EntityNotFoundException {
        Map<String, Object> parameters = FastMap.newInstance();

        //  placeholder for report parameters
        Map<String, Object> jrParameters = FastMap.newInstance();
        // prepare company information
        Map<String, Object> organizationInfo = UtilCommon.getOrganizationHeaderInfo(organizationPartyId, delegator);
        jrParameters.putAll(organizationInfo);
        PartyReader pr = new PartyReader(organizationPartyId, delegator);
        jrParameters.put("website", pr.getWebsite());
        jrParameters.put("primaryPhone", PartyContactHelper.getTelecomNumberByPurpose(organizationPartyId, "PRIMARY_PHONE", true, delegator));
        jrParameters.put("primaryFax", PartyContactHelper.getTelecomNumberByPurpose(organizationPartyId, "FAX_NUMBER", true, delegator));
        DomainsDirectory domains = dl.loadDomainsDirectory();
        OrderRepositoryInterface orderRepository = domains.getOrderDomain().getOrderRepository();
        PartyRepositoryInterface partyRepository = domains.getPartyDomain().getPartyRepository();
        // load order object and put it to report parameters
        Order order = orderRepository.getOrderById(orderId);
        jrParameters.put("order", order);

        List<Map<String, Object>> reportData = new FastList<Map<String, Object>>();
        // retrieve order item info and pass it to JR, add as report data
        for (org.opentaps.domain.order.OrderItem orderItem : order.getOrderItems()) {
            BigDecimal orderSubTotal = orderItem.getSubTotal();
            if (orderItem.isCancelled()) {
                if (order.isPurchaseOrder()) {
                    continue;
                }
                orderSubTotal = BigDecimal.ZERO;
            }
            Map<String, Object> reportLine = new FastMap<String, Object>();
            if (orderItem.getProductId() != null) {
                reportLine.put("productId", orderItem.getProductId());
            }
            if (orderItem.getOrderItemTypeId() != null) {
                reportLine.put("orderItemTypeId", orderItem.getOrderItemTypeId());
            }
            reportLine.put("itemDescription", orderItem.getItemDescription());
            reportLine.put("orderQuantity", orderItem.getOrderedQuantity());
            reportLine.put("orderUnitPrice", orderItem.getUnitPrice());
            reportLine.put("orderSubTotal", orderSubTotal);
            reportLine.put("adjustmentsAmount", orderItem.getOtherAdjustmentsAmount());
            // put it into report data collection
            reportData.add(reportLine);
        }

        // retrieve supplier postal address for purchasing order and pass it to JR
        if (order.isPurchaseOrder() && order.getBillFromVendor() != null) {
            PostalAddress supplierAddress = partyRepository.getSupplierPostalAddress(order.getBillFromVendor());
            if (supplierAddress != null) {
                // if there is no toName on the address, then the name of the supplier would be used
                if (UtilValidate.isEmpty(supplierAddress.getToName())) {
                    supplierAddress.setToName(order.getBillFromVendor().getName());
                }
                jrParameters.put("supplierAddress", supplierAddress);
            }
        }

        // get the client phone number
        Party client = order.getPlacingCustomer();
        if (client != null) {
            Map<String, Object> contactMechLine = FastMap.newInstance();
            TelecomNumber clientPhone = client.getPrimaryPhone();
            if (clientPhone != null) {
                List<Map<String, Object>> clientPhoneNumbers = FastList.newInstance();
                contactMechLine.putAll(clientPhone.toMap());
                clientPhoneNumbers.add(contactMechLine);
                jrParameters.put("clientPhoneNumberList", new JRMapCollectionDataSource(clientPhoneNumbers));
            }
        }

        // retrieve contact mech info and pass it to JR
        List<Map<String, Object>> orderContactMechList = FastList.newInstance();
        Map<String, Object> shippingPhoneLine = FastMap.newInstance();
        for (OrderContactMech orderContactMech : order.getOrderContactMeches()) {
            // ignore if the address is set to _NA_
            if ("_NA_".equals(orderContactMech.getContactMechId())) {
                continue;
            }

            Map<String, Object> contactMechLine = FastMap.newInstance();
            ContactMech contactMech = orderContactMech.getContactMech();
            if (contactMech.getContactMechTypeId().equals("POSTAL_ADDRESS") && contactMech.getPostalAddress() != null) {
                contactMechLine.put("contactMechTypeId", contactMech.getContactMechTypeId());
                String contactMechPurposeType = orderContactMech.getContactMechPurposeType().getDescription();
                contactMechLine.putAll(contactMech.getPostalAddress().toMap());
                if (orderContactMech.getContactMechPurposeType() != null) {
                    contactMechLine.put("contactMechPurposeType", contactMechPurposeType);
                }
                orderContactMechList.add(contactMechLine);
            }
            if (order.isPurchaseOrder() && "PHONE_SHIPPING".equalsIgnoreCase(orderContactMech.getContactMechPurposeTypeId())) {
                shippingPhoneLine = FastMap.newInstance();
                String contactMechPurposeType = orderContactMech.getContactMechPurposeType().getDescription();
                shippingPhoneLine.putAll(contactMech.getTelecomNumber().toMap());
                if (orderContactMech.getContactMechPurposeType() != null) {
                    shippingPhoneLine.put("contactMechPurposeType", contactMechPurposeType);
                }
            }
        }
        if (UtilValidate.isNotEmpty(shippingPhoneLine) && UtilValidate.isNotEmpty(orderContactMechList)) {
            for (Map<String, Object> orderContactMech : orderContactMechList) {
                String contactMechTypeId = (String) orderContactMech.get("contactMechTypeId");
                if ("POSTAL_ADDRESS".equalsIgnoreCase(contactMechTypeId)) {
                    orderContactMech.put("countryCode", shippingPhoneLine.get("countryCode"));
                    orderContactMech.put("areaCode", shippingPhoneLine.get("areaCode"));
                    orderContactMech.put("contactNumber", shippingPhoneLine.get("contactNumber"));
                }
            }
        }
        if (UtilValidate.isNotEmpty(orderContactMechList)) {
            // if orderContactMechList not empty, then pass it to JR
            jrParameters.put("orderContactMechList", new JRMapCollectionDataSource(orderContactMechList));
        }
        // retrieve shipping group info and pass it to JR
        List<Map<String, Object>> shipGroupList = FastList.newInstance();
        for (OrderItemShipGroup shipGroup : order.getShipGroups()) {
            Map<String, Object> shipGroupLine = FastMap.newInstance();

            String carrierName = org.opentaps.common.party.PartyHelper.getPartyName(shipGroup.getCarrierParty());
            if (UtilValidate.isNotEmpty(carrierName)) {
                shipGroupLine.put("carrierName", carrierName);
            }
            if (shipGroup.getShipmentMethodType() != null) {
                shipGroupLine.put("shipmentMethodType", shipGroup.getShipmentMethodType().getDescription());
            }
            if (UtilValidate.isNotEmpty(shipGroup.getShippingInstructions())) {
                shipGroupLine.put("shippingInstructions", shipGroup.getShippingInstructions());
            }
            shipGroupList.add(shipGroupLine);
        }
        if (UtilValidate.isNotEmpty(shipGroupList)) {
            // if shipGroupList not empty, then pass it to JR
            jrParameters.put("shipGroupList", new JRMapCollectionDataSource(shipGroupList));
        }
        // retrieve order terms info and pass it to JR
        List<Map<String, Object>> orderTermList = FastList.newInstance();
        for (OrderTerm orderTerm : order.getOrderTerms()) {
            Map<String, Object> orderTermLine = FastMap.newInstance();
            orderTermLine.putAll(orderTerm.toMap());
            if (orderTerm.getTermType() != null) {
                orderTermLine.put("termType", orderTerm.getTermType().getDescription());
            }
            orderTermList.add(orderTermLine);
        }
        if (UtilValidate.isNotEmpty(orderTermList)) {
            // if orderTermList not empty, then pass it to JR
            jrParameters.put("orderTermList", new JRMapCollectionDataSource(orderTermList));
        }
        // retrieve shipping adjustment info and pass it to JR
        List<Map<String, Object>> shipAdjustmentList = FastList.newInstance();
        for (OrderAdjustment adj : order.getShippingAdjustments()) {
            BigDecimal adjustmentAmount = adj.calculateAdjustment(order);
            if (adjustmentAmount.doubleValue() != 0d) {
                Map<String, Object> adjLine = FastMap.newInstance();
                adjLine.put("description", adj.getDescription());
                adjLine.put("adjustmentAmount", adjustmentAmount);
                if (adj.getOrderAdjustmentType() != null) {
                    adjLine.put("adjustmentType", adj.getOrderAdjustmentType().getDescription());
                }
                shipAdjustmentList.add(adjLine);
            }
        }
        if (UtilValidate.isNotEmpty(shipAdjustmentList)) {
            // if shipAdjustmentList not empty, then pass it to JR
            jrParameters.put("shipAdjustmentList", new JRMapCollectionDataSource(shipAdjustmentList));
        }
        // retrieve other adjustments such as promotions information and pass it to JR
        List<Map<String, Object>> otherAdjustmentList = FastList.newInstance();
        for (OrderAdjustment adj : order.getNonShippingAdjustments()) {
            BigDecimal adjustmentAmount = adj.calculateAdjustment(order);
            if (adjustmentAmount.doubleValue() != 0d) {
                Map<String, Object> adjLine = FastMap.newInstance();
                adjLine.put("description", adj.getDescription());
                adjLine.put("adjustmentAmount", adjustmentAmount);
                if (adj.getOrderAdjustmentType() != null) {
                    adjLine.put("adjustmentType", adj.getOrderAdjustmentType().getDescription());
                }
                otherAdjustmentList.add(adjLine);
            }
        }
        if (UtilValidate.isNotEmpty(otherAdjustmentList)) {
            // if otherAdjustmentList not empty, then pass it to JR
            jrParameters.put("otherAdjustmentList", new JRMapCollectionDataSource(otherAdjustmentList));
        }
        // retrieve order notes information and pass it to JR
        List<Map<String, Object>> notesList = FastList.newInstance();
        for (OrderHeaderNoteView note : order.getNotes()) {
            Map<String, Object> noteLine = FastMap.newInstance();
            //if have note content
            if (note.getInternalNote() != null && !note.getInternalNote().equals("Y")) {
                noteLine.putAll(note.toMap());

                Map notePartyNameResult = dispatcher.runSync("getPartyNameForDate", UtilMisc.toMap("partyId", note.getNoteParty(), "compareDate", note.getNoteDateTime(), "lastNameFirst", "Y", "userLogin", userLogin));
                if (ServiceUtil.isError(notePartyNameResult) || ServiceUtil.isFailure(notePartyNameResult)) {
                    throw new GenericServiceException(ServiceUtil.getErrorMessage(notePartyNameResult));
                }
                String fullName = (String) notePartyNameResult.get("fullName");
                noteLine.put("fullName", fullName);
                notesList.add(noteLine);
            }
        }
        if (UtilValidate.isNotEmpty(notesList)) {
            // if notesList not empty, then pass it to JR
            jrParameters.put("notesList", new JRMapCollectionDataSource(notesList));
        }
        // retrieve order payment information and pass it to JR
        List<Map<String, Object>> paymentsList = FastList.newInstance();
        for (Payment payment : order.getPayments()) {
            Map<String, Object> paymentLine = FastMap.newInstance();
            paymentLine.put("amountApplied", payment.getAmount());
            paymentLine.put("effectiveDate", payment.getEffectiveDate());
            paymentLine.put("paymentRefNum", payment.getPaymentRefNum());
            if (payment.getPaymentMethod() != null && payment.getPaymentMethod().isCreditCard() && payment.getCreditCard() != null) {
                //payment method is credit card, just display last four number
                String maskNums = "";
                for (int i = 0; i < payment.getCreditCard().getCardNumber().length() - 4; i++) {
                    maskNums += "*";
                }
                maskNums += payment.getCreditCard().getCardNumber().substring(payment.getCreditCard().getCardNumber().length() - 4);
                String creditCardInfo = payment.getCreditCard().getCardType() + " " + maskNums + " " + payment.getCreditCard().getExpireDate();
                paymentLine.put("method", creditCardInfo);
            } else {
                paymentLine.put("method", payment.getPaymentMethodType().getDescription());
            }
            paymentsList.add(paymentLine);
        }
        if (UtilValidate.isNotEmpty(paymentsList)) {
            // if paymentsList not empty, then pass it to JR
            jrParameters.put("paymentsList", new JRMapCollectionDataSource(paymentsList));
        }
        JRMapCollectionDataSource jrDataSource = new JRMapCollectionDataSource(reportData);
        parameters.put("jrDataSource", jrDataSource);
        parameters.put("jrParameters", jrParameters);
        return parameters;

    }

    /**
     * Prepare data and parameters for running order report.
     * @param request a <code>HttpServletRequest</code> value
     * @param response a <code>HttpServletResponse</code> value
     * @return the event response <code>String</code>
     */
    public static String prepareOrderReport(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);

        String orderId = UtilCommon.getParameter(request, "orderId");
        String organizationPartyId = (String) request.getSession().getAttribute("organizationPartyId");
        if (UtilValidate.isEmpty(organizationPartyId)) {
            //check if include organizationPartyId, else throw a event error
            organizationPartyId = UtilCommon.getParameter(request, "organizationPartyId");
            if (UtilValidate.isEmpty(organizationPartyId)) {
                organizationPartyId = UtilConfig.getPropertyValue("opentaps", "organizationPartyId");
                if (UtilValidate.isEmpty(organizationPartyId)) {
                    UtilMessage.createAndLogEventError(request, "OpentapsError_CannotPrintOrderOrganizationPartyId", UtilMisc.toMap("orderId", orderId), locale, MODULE);
                }
            }
        }
        try {
            // get parameter for jasper
            DomainsLoader dl = new DomainsLoader(request);
            Map jasperParameters = prepareOrderReportParameters(dl, delegator, dispatcher, userLogin, locale, orderId, organizationPartyId);
            request.setAttribute("jrParameters", jasperParameters.get("jrParameters"));
            request.setAttribute("jrDataSource", jasperParameters.get("jrDataSource"));

        } catch (GenericEntityException e) {
            UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (GenericServiceException e) {
            UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (PartyNotFoundException e) {
            UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (EntityNotFoundException e) {
            UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (RepositoryException e) {
            UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        } catch (InfrastructureException e) {
            UtilMessage.createAndLogEventError(request, e, locale, MODULE);
        }

        return "success";
    }

    /**
     * From Ofbiz <code>ShippingEvents</code>, but also account for the COD surcharge.
     *
     * @param dispatcher a <code>LocalDispatcher</code> value
     * @param delegator a <code>Delegator</code> value
     * @param orh an <code>OrderReadHelper</code> value
     * @param shipGroupSeqId a <code>String</code> value
     * @return a <code>Map</code> value
     */
    @SuppressWarnings("unchecked")
    public static Map getShipEstimate(LocalDispatcher dispatcher, Delegator delegator, OrderReadHelper orh, String shipGroupSeqId) {
        // check for shippable items
        if (!orh.shippingApplies()) {
            Map responseResult = ServiceUtil.returnSuccess();
            responseResult.put("shippingTotal", BigDecimal.ZERO);
            return responseResult;
        }

        GenericValue shipGroup = orh.getOrderItemShipGroup(shipGroupSeqId);
        String shipmentMethodTypeId = shipGroup.getString("shipmentMethodTypeId");
        String carrierRoleTypeId = shipGroup.getString("carrierRoleTypeId");
        String carrierPartyId = shipGroup.getString("carrierPartyId");
        String supplierPartyId = shipGroup.getString("supplierPartyId");

        GenericValue shipAddr = orh.getShippingAddress(shipGroupSeqId);
        if (shipAddr == null) {
            return UtilMisc.toMap("shippingTotal", BigDecimal.ZERO);
        }

        String contactMechId = shipAddr.getString("contactMechId");

        // check if need to add the COD surcharge
        boolean isCod = false;
        try {
            List<GenericValue> codPaymentPrefs = delegator.findByAnd("OrderPaymentPreference",
                    UtilMisc.toList(EntityCondition.makeCondition("orderId", orh.getOrderId()),
                            EntityCondition.makeCondition("paymentMethodTypeId", "EXT_COD"),
                            EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PAYMENT_CANCELLED")));

            isCod = UtilValidate.isNotEmpty(codPaymentPrefs);
        } catch (GeneralException e) {
            return ServiceUtil.returnError("A problem occurred while getting the order payment preferences.");
        }

        Debug.logInfo("getShipEstimate: order [" + orh.getOrderId() + "] isCod = " + isCod, MODULE);

        return getShipGroupEstimate(dispatcher, delegator, orh.getOrderTypeId(), shipmentMethodTypeId, carrierPartyId, carrierRoleTypeId,
                contactMechId, orh.getProductStoreId(), supplierPartyId, orh.getShippableItemInfo(shipGroupSeqId), orh.getShippableWeight(shipGroupSeqId),
                orh.getShippableQuantity(shipGroupSeqId), orh.getShippableTotal(shipGroupSeqId), isCod);
    }

    /**
     * From Ofbiz <code>ShippingEvents</code>, but also account for the COD surcharge.
     *
     * @param dispatcher a <code>LocalDispatcher</code> value
     * @param delegator a <code>Delegator</code> value
     * @param cart a <code>ShoppingCart</code> value
     * @param groupNo an <code>int</code> value
     * @return a <code>Map</code> value
     */
    @SuppressWarnings("unchecked")
    public static Map getShipGroupEstimate(LocalDispatcher dispatcher, Delegator delegator, OpentapsShoppingCart cart, int groupNo) {
        // check for shippable items
        if (!cart.shippingApplies()) {
            Map responseResult = ServiceUtil.returnSuccess();
            responseResult.put("shippingTotal", BigDecimal.ZERO);
            return responseResult;
        }

        String shipmentMethodTypeId = cart.getShipmentMethodTypeId(groupNo);
        String carrierPartyId = cart.getCarrierPartyId(groupNo);

        return getShipGroupEstimate(dispatcher, delegator, cart.getOrderType(), shipmentMethodTypeId, carrierPartyId, null,
                cart.getShippingContactMechId(groupNo), cart.getProductStoreId(), cart.getSupplierPartyId(groupNo), cart.getShippableItemInfo(groupNo),
                cart.getShippableWeight(groupNo), cart.getShippableQuantity(groupNo), cart.getShippableTotal(groupNo), cart.getCOD(groupNo));
    }

    /**
     * From Ofbiz <code>ShippingEvents</code>, but also account for the COD surcharge.
     *
     * @param dispatcher a <code>LocalDispatcher</code> value
     * @param delegator a <code>Delegator</code> value
     * @param orderTypeId a <code>String</code> value
     * @param shipmentMethodTypeId a <code>String</code> value
     * @param carrierPartyId a <code>String</code> value
     * @param carrierRoleTypeId a <code>String</code> value
     * @param shippingContactMechId a <code>String</code> value
     * @param productStoreId a <code>String</code> value
     * @param supplierPartyId a <code>String</code> value
     * @param itemInfo a <code>List</code> value
     * @param shippableWeight a <code>double</code> value
     * @param shippableQuantity a <code>double</code> value
     * @param shippableTotal a <code>double</code> value
     * @param isCod flag indicating cod surcharges should be added
     * @return a <code>Map</code> value
     */
    @SuppressWarnings("unchecked")
    public static Map getShipGroupEstimate(LocalDispatcher dispatcher, Delegator delegator, String orderTypeId,
            String shipmentMethodTypeId, String carrierPartyId, String carrierRoleTypeId, String shippingContactMechId,
            String productStoreId, String supplierPartyId, List itemInfo, BigDecimal shippableWeight, BigDecimal shippableQuantity,
            BigDecimal shippableTotal, boolean isCod) {

        String standardMessage = "A problem occurred calculating shipping. Fees will be calculated offline.";
        List errorMessageList = new ArrayList();

        if (shipmentMethodTypeId == null || carrierPartyId == null) {
            if ("SALES_ORDER".equals(orderTypeId)) {
                errorMessageList.add("Please Select Your Shipping Method.");
                return ServiceUtil.returnError(errorMessageList);
            } else {
                return ServiceUtil.returnSuccess();
            }
        }

        if (carrierRoleTypeId == null) {
            carrierRoleTypeId = "CARRIER";
        }

        if (shippingContactMechId == null) {
            errorMessageList.add("Please Select Your Shipping Address.");
            return ServiceUtil.returnError(errorMessageList);
        }

        // if as supplier is associated, then we have a drop shipment and should use the origin shipment address of it
        String shippingOriginContactMechId = null;
        if (supplierPartyId != null) {
            try {
                GenericValue originAddress = ShippingEvents.getShippingOriginContactMech(delegator, supplierPartyId);
                if (originAddress == null) {
                    return ServiceUtil.returnError("Cannot find the origin shipping address (SHIP_ORIG_LOCATION) for the supplier with ID [" + supplierPartyId + "].  Will not be able to calculate drop shipment estimate.");
                }
                shippingOriginContactMechId = originAddress.getString("contactMechId");
            } catch (GeneralException e) {
                return ServiceUtil.returnError(standardMessage);
            }
        }

        // no shippable items; we won't change any shipping at all
        if (shippableQuantity.signum() == 0) {
            Map result = ServiceUtil.returnSuccess();
            result.put("shippingTotal", BigDecimal.ZERO);
            return result;
        }

        // check for an external service call
        GenericValue storeShipMethod = ProductStoreWorker.getProductStoreShipmentMethod(delegator, productStoreId, shipmentMethodTypeId, carrierPartyId, carrierRoleTypeId);

        if (storeShipMethod == null) {
            errorMessageList.add("No applicable shipment method found.");
            return ServiceUtil.returnError(errorMessageList);
        }

        // the initial amount before manual estimates
        BigDecimal shippingTotal = BigDecimal.ZERO;

        // prepare the service invocation fields
        Map serviceFields = new HashMap();
        serviceFields.put("initialEstimateAmt", shippingTotal);
        serviceFields.put("shippableTotal", shippableTotal);
        serviceFields.put("shippableQuantity", shippableQuantity);
        serviceFields.put("shippableWeight", shippableWeight);
        serviceFields.put("shippableItemInfo", itemInfo);
        serviceFields.put("productStoreId", productStoreId);
        serviceFields.put("carrierRoleTypeId", "CARRIER");
        serviceFields.put("carrierPartyId", carrierPartyId);
        serviceFields.put("shipmentMethodTypeId", shipmentMethodTypeId);
        serviceFields.put("shippingContactMechId", shippingContactMechId);
        serviceFields.put("shippingOriginContactMechId", shippingOriginContactMechId);

        // call the external shipping service
        try {
            BigDecimal externalAmt = ShippingEvents.getExternalShipEstimate(dispatcher, storeShipMethod, serviceFields);
            if (externalAmt != null) {
                shippingTotal = shippingTotal.add(externalAmt);
            }
        } catch (GeneralException e) {
            return ServiceUtil.returnError(standardMessage);
        }

        // update the initial amount
        serviceFields.put("initialEstimateAmt", shippingTotal);

        // call the generic estimate service
        try {
            BigDecimal genericAmt = ShippingEvents.getGenericShipEstimate(dispatcher, storeShipMethod, serviceFields);
            if (genericAmt != null) {
                shippingTotal = shippingTotal.add(genericAmt);
            }
        } catch (GeneralException e) {
            // return failure instead of error so the item can be added to order still
            return ServiceUtil.returnFailure(standardMessage);
        }

        // add COD surcharges
        if (isCod) {
            BigDecimal codSurcharge = storeShipMethod.getBigDecimal("codSurcharge");
            if (UtilValidate.isNotEmpty(codSurcharge)) {
                shippingTotal = shippingTotal.add(codSurcharge);
            }
        }

        // return the totals
        Map responseResult = ServiceUtil.returnSuccess();
        responseResult.put("shippingTotal", shippingTotal);
        return responseResult;
    }
}
