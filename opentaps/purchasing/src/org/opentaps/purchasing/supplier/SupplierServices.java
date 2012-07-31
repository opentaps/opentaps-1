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
package org.opentaps.purchasing.supplier;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.opentaps.base.entities.PartyGroup;
import org.opentaps.common.util.UtilCommon;
import org.opentaps.common.util.UtilMessage;
import org.opentaps.domain.DomainsLoader;
import org.opentaps.domain.party.PartyDomainInterface;
import org.opentaps.domain.party.PartyRepositoryInterface;
import org.opentaps.foundation.infrastructure.Infrastructure;
import org.opentaps.foundation.infrastructure.User;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.purchasing.security.PurchasingSecurity;

public class SupplierServices {

    public static final String module = SupplierServices.class.getName();

    public static Map updateSupplier(DispatchContext dctx, Map context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = UtilCommon.getLocale(context);
        PurchasingSecurity purchasingSecurity = new PurchasingSecurity(security, userLogin);
        String organizationPartyId = (String)dctx.getAttribute("organizationPartyId");

        String supplierPartyId = (String) context.get("partyId");

        // make sure userLogin has PRCH_SPLR_UPDATE permission for this supplier
        if (!purchasingSecurity.hasPartyRelationSecurity("PRCH_SPLR", "_UPDATE", organizationPartyId)) {
            return UtilMessage.createAndLogServiceError("OpentapsError_PermissionDenied", locale, module);
        }
        try {
            // update the PartyGroup
            ModelService updatePartyGroup = dctx.getModelService("updatePartyGroup");
            Map input = updatePartyGroup.makeValid(context, "IN");
            input.put("partyId", supplierPartyId);
            input.put("userLogin", userLogin);
            Map serviceResults = dispatcher.runSync("updatePartyGroup", input);
            if (ServiceUtil.isError(serviceResults)) {
                return serviceResults;
            }

            // update PartySupplementalData
            GenericValue partyData = delegator.findByPrimaryKey("PartySupplementalData", UtilMisc.toMap("partyId", supplierPartyId));
            if (partyData == null) {
                // create a new one
                partyData = delegator.makeValue("PartySupplementalData", UtilMisc.toMap("partyId", supplierPartyId));
                partyData.create();
            }
            partyData.setNonPKFields(context);
            partyData.store();

        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "PurchError_UpdateSupplierFail", locale, module);
        } catch (GenericEntityException e) {
            return UtilMessage.createAndLogServiceError(e, "PurchError_UpdateSupplierFail", locale, module);
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map createSupplier(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String supplierPartyId = (String) context.get("partyId");

        Locale locale = UtilCommon.getLocale(context);
        String groupName = (String) context.get("groupName");
        // the field that flag if force complete to create contact even existing same name already
        String forceComplete = context.get("forceComplete") == null ? "N" : (String) context.get("forceComplete");
        Map result = ServiceUtil.returnSuccess();
        try {

            // sanitize the party Id if it was given
            if (UtilValidate.isNotEmpty(supplierPartyId)) {
                supplierPartyId = supplierPartyId.replaceAll(" ", "");
                supplierPartyId = supplierPartyId.replaceAll("[^a-zA-Z0-9_]", "_");
                supplierPartyId = supplierPartyId.replaceAll("__+", "_");
                context.put("partyId", supplierPartyId);
            }

            // verify account name is use already
            if (!"Y".equals(forceComplete)) {
                DomainsLoader domainLoader = new DomainsLoader(new Infrastructure(dispatcher), new User(userLogin));
                PartyDomainInterface partyDomain = domainLoader.loadDomainsDirectory().getPartyDomain();
                PartyRepositoryInterface repo = partyDomain.getPartyRepository();
                Set<PartyGroup> duplicateSuppliersWithName = repo.getPartyGroupByGroupNameAndRoleType(groupName, "SUPPLIER");
                // if existing the account which have same account name, then return the conflict account and error message
                if (duplicateSuppliersWithName.size() > 0 && !"Y".equals(forceComplete)) {
                    PartyGroup partyGroup = duplicateSuppliersWithName.iterator().next();
                    Map results = ServiceUtil.returnError(UtilMessage.expandLabel("PurchCreateSupplierDuplicateCheckFail", UtilMisc.toMap("partyId", partyGroup.getPartyId()), locale));
                    results.put("duplicateSuppliersWithName", duplicateSuppliersWithName);
                    return results;
                }
            }
            ModelService createPartyGroup = dctx.getModelService("createPartyGroup");
            Map input = createPartyGroup.makeValid(context, "IN");
            Map serviceResults = dispatcher.runSync("createPartyGroup", input);
            if (ServiceUtil.isError(serviceResults)) return serviceResults;
            supplierPartyId = (String) serviceResults.get("partyId");

            // Create a PartyRole for the resulting supplier partyId with roleTypeId = SUPPLIER
            serviceResults = dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", supplierPartyId, "roleTypeId", "SUPPLIER", "userLogin", userLogin));
            if (ServiceUtil.isError(serviceResults)) return serviceResults;

   
            // Create primary email
            String primaryEmail = (String) context.get("primaryEmail");
            if (UtilValidate.isNotEmpty(primaryEmail)) {
                serviceResults = dispatcher.runSync("createPartyEmailAddress", UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin,
                            "contactMechTypeId", "EMAIL_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_EMAIL", "emailAddress", primaryEmail));
                if (ServiceUtil.isError(serviceResults)) return serviceResults;
            }

            // Create primary web url
            String primaryWebUrl = (String) context.get("primaryWebUrl");
            if (UtilValidate.isNotEmpty(primaryWebUrl)) {
                serviceResults = dispatcher.runSync("createPartyContactMech", UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin,
                            "contactMechTypeId", "WEB_ADDRESS", "contactMechPurposeTypeId", "PRIMARY_WEB_URL", "infoString", primaryWebUrl));
                if (ServiceUtil.isError(serviceResults)) return serviceResults;
            }

            // Create primary telecom number
            String primaryPhoneCountryCode = (String) context.get("primaryPhoneCountryCode");
            String primaryPhoneAreaCode = (String) context.get("primaryPhoneAreaCode");
            String primaryPhoneNumber = (String) context.get("primaryPhoneNumber");
            String primaryPhoneExtension = (String) context.get("primaryPhoneExtension");
            if (UtilValidate.isNotEmpty(primaryPhoneNumber)) {
                input = UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin, "contactMechPurposeTypeId", "PRIMARY_PHONE");
                input.put("countryCode", primaryPhoneCountryCode);
                input.put("areaCode", primaryPhoneAreaCode);
                input.put("contactNumber", primaryPhoneNumber);
                input.put("extension", primaryPhoneExtension);
                serviceResults = dispatcher.runSync("createPartyTelecomNumber", input);
                if (ServiceUtil.isError(serviceResults)) return serviceResults;
            }

            // Create Main Fax Number
            String primaryFaxCountryCode = (String) context.get("primaryFaxCountryCode");
            String primaryFaxAreaCode = (String) context.get("primaryFaxAreaCode");
            String primaryFaxNumber = (String) context.get("primaryFaxNumber");
            String primaryFaxExtension = (String) context.get("primaryFaxExtension");
            if (UtilValidate.isNotEmpty(primaryFaxNumber)) {
                input = UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin, "contactMechPurposeTypeId", "FAX_NUMBER");
                input.put("countryCode", primaryFaxCountryCode);
                input.put("areaCode", primaryFaxAreaCode);
                input.put("contactNumber", primaryFaxNumber);
                input.put("extension", primaryFaxExtension);
                serviceResults = dispatcher.runSync("createPartyTelecomNumber", input);
                if (ServiceUtil.isError(serviceResults)) return serviceResults;
            }
            
            // Create general correspondence postal address
            String generalToName = (String) context.get("generalToName");
            String generalAttnName = (String) context.get("generalAttnName");
            String generalAddress1 = (String) context.get("generalAddress1");
            String generalAddress2 = (String) context.get("generalAddress2");
            String generalCity = (String) context.get("generalCity");
            String generalStateProvinceGeoId = (String) context.get("generalStateProvinceGeoId");
            String generalPostalCode = (String) context.get("generalPostalCode");
            String generalCountryGeoId = (String) context.get("generalCountryGeoId");
            if (UtilValidate.isNotEmpty(generalAddress1)) {
                input = UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin, "contactMechPurposeTypeId", "GENERAL_LOCATION");
                input.put("toName", generalToName);
                input.put("attnName", generalAttnName);
                input.put("address1", generalAddress1);
                input.put("address2", generalAddress2);
                input.put("city", generalCity);
                input.put("stateProvinceGeoId", generalStateProvinceGeoId);
                input.put("postalCode", generalPostalCode);
                input.put("countryGeoId", generalCountryGeoId);
                serviceResults = dispatcher.runSync("createPartyPostalAddress", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return serviceResults;
                }
                String contactMechId = (String) serviceResults.get("contactMechId");

                // Make this address the SHIPPING_LOCATION, SHIP_ORIG_LOCATION, PAYMENT_LOCATION and BILLING_LOCATION
                input = UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin, "contactMechId", contactMechId, "contactMechPurposeTypeId", "SHIPPING_LOCATION");
                serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);
                if (ServiceUtil.isError(serviceResults)) return serviceResults;
                input = UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin, "contactMechId", contactMechId, "contactMechPurposeTypeId", "SHIP_ORIG_LOCATION");
                serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);
                if (ServiceUtil.isError(serviceResults)) return serviceResults;
                input = UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin, "contactMechId", contactMechId, "contactMechPurposeTypeId", "PAYMENT_LOCATION");
                serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);
                if (ServiceUtil.isError(serviceResults)) return serviceResults;
                input = UtilMisc.toMap("partyId", supplierPartyId, "userLogin", userLogin, "contactMechId", contactMechId, "contactMechPurposeTypeId", "BILLING_LOCATION");
                serviceResults = dispatcher.runSync("createPartyContactMechPurpose", input);
                if (ServiceUtil.isError(serviceResults)) return serviceResults;
            }


        } catch (GenericServiceException e) {
            return UtilMessage.createAndLogServiceError(e, "PurchError_CreateSupplierFail", locale, module);
        } catch (RepositoryException e) {
            return UtilMessage.createAndLogServiceError(e, "PurchError_CreateSupplierFail", locale, module);
        }

        result.put("partyId", supplierPartyId);
        return result;
    }

    // See ProductionRunServices.java for more code related to outsourced tasks, especially how this data is used
    public static Map outsourceRoutingTask(DispatchContext dctx, Map context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = UtilCommon.getLocale(context);
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String partyId = (String) context.get("partyId");
        String productId = (String) context.get("productId");
        String productName = (String) context.get("productName");
        String supplierProductName = (String) context.get("supplierProductName");
        Timestamp availableFromDate = (Timestamp) context.get("availableFromDate");
        Timestamp availableThruDate = (Timestamp) context.get("availableThruDate");
        BigDecimal minimumOrderQuantity = (BigDecimal) context.get("minimumOrderQuantity");
        BigDecimal lastPrice = (BigDecimal) context.get("lastPrice");
        String glAccountTypeId = (String) context.get("glAccountTypeId");
        String currencyUomId = (String) context.get("currencyUomId");

        if (UtilValidate.isEmpty(productName) && UtilValidate.isEmpty(supplierProductName)) {
            return ServiceUtil.returnError(UtilMessage.expandLabel("WarehouseError_MissingProductNameOrSupplierProductName", locale));
        }
        String internalName = (UtilValidate.isEmpty(productName) ? supplierProductName : productName);

        String workEffortId = (String) context.get("workEffortId");
        try {
            // ensure this is a routing task
            Map conditions = UtilMisc.toMap("workEffortId", workEffortId);
            GenericValue task = delegator.findByPrimaryKey("WorkEffort", conditions);
            if (task == null) {
                return UtilMessage.createServiceError("WarehouseError_MissingRoutingTask", locale, conditions);
            }
            if (! "ROU_TASK".equals(task.get("workEffortTypeId"))) {
                return UtilMessage.createServiceError("WarehouseError_CannotOutsourceNonRoutingTask", locale, conditions);
            }

            // ensure the supplier exists
            GenericValue role = delegator.findByPrimaryKey("PartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "SUPPLIER"));
            if (role == null) {
                return UtilMessage.createServiceError("OpentapsError_NoSupplierRole", locale, UtilMisc.toMap("partyId", partyId));
            }

            // create the Product
            Map product = UtilMisc.toMap("internalName", internalName, "productTypeId", "SERVICE_CONTRACT_MFG", "userLogin", userLogin);
            if (UtilValidate.isNotEmpty(productId)) product.put("productId", productId);
            product.put("productName", internalName);
            Map results = dispatcher.runSync("createProduct", product);
            if (ServiceUtil.isError(results)) {
                return UtilMessage.createAndLogServiceError(results, ServiceUtil.getErrorMessage(results), locale, module);
            }
            productId = (String) results.get("productId");

            // create a supplier product
            ModelService service = dctx.getModelService("createSupplierProduct");
            Map supplierProduct = service.makeValid(context, "IN");
            supplierProduct.put("productId", productId);
            supplierProduct.put("minimumOrderQuantity", minimumOrderQuantity);
            results = dispatcher.runSync(service.name, supplierProduct);
            if (ServiceUtil.isError(results)) {
                return UtilMessage.createAndLogServiceError(results, ServiceUtil.getErrorMessage(results), locale, module);
            }

            // create the cost component for the price of this supplier product
            Map cost = UtilMisc.toMap("costGlAccountTypeId", glAccountTypeId, "costCustomMethodId", "COST_OUTSRCD_TASK", "currencyUomId", currencyUomId, "userLogin", userLogin);
            results = dispatcher.runSync("createCostComponentCalc", cost);
            if (ServiceUtil.isError(results)) {
                return UtilMessage.createAndLogServiceError(results, ServiceUtil.getErrorMessage(results), locale, module);
            }
            String costComponentCalcId = (String) results.get("costComponentCalcId");

            // associate it with the routing task
            Map costAssoc = UtilMisc.toMap("workEffortId", workEffortId, "costComponentTypeId", "ROUTE_COST", "userLogin", userLogin);
            costAssoc.put("costComponentCalcId", costComponentCalcId);
            costAssoc.put("fromDate", availableFromDate);
            costAssoc.put("thruDate", availableThruDate);
            results = dispatcher.runSync("createWorkEffortCostCalc", costAssoc);
            if (ServiceUtil.isError(results)) {
                return UtilMessage.createAndLogServiceError(results, ServiceUtil.getErrorMessage(results), locale, module);
            }

            // create the work effort good standard template for the routing task, this marks it as an outsourced task
            Map wegs = UtilMisc.toMap("workEffortId", workEffortId, "productId", productId, "workEffortGoodStdTypeId", "ROU_OUTSOURCE_PROD", "userLogin", userLogin);
            wegs.put("fromDate", availableFromDate);
            wegs.put("thruDate", availableThruDate);
            wegs.put("statusId", "WEGS_CREATED");
            wegs.put("estimatedQuantity", new Double(1)); // NOTE: we set this to 1 even though min qty for supplier might be greater, user will deal with this later
            results = dispatcher.runSync("createWorkEffortGoodStandard", wegs);
            if (ServiceUtil.isError(results)) {
                return UtilMessage.createAndLogServiceError(results, ServiceUtil.getErrorMessage(results), locale, module);
            }

            return ServiceUtil.returnSuccess();
        } catch (GeneralException e) {
            return UtilMessage.createAndLogServiceError(e, locale, module);
        }
    }

}
