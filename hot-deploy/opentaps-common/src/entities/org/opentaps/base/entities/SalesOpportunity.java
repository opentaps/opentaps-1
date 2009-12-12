package org.opentaps.base.entities;

/*
 * Copyright (c) 2008 - 2009 Open Source Strategies, Inc.
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

// DO NOT EDIT THIS FILE!  THIS IS AUTO GENERATED AND WILL GET WRITTEN OVER PERIODICALLY WHEN THE DATA MODEL CHANGES
// EXTEND THIS CLASS INSTEAD.

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import javolution.util.FastMap;

import org.opentaps.foundation.entity.Entity;
import org.opentaps.foundation.entity.EntityFieldInterface;
import org.opentaps.foundation.repository.RepositoryException;
import org.opentaps.foundation.repository.RepositoryInterface;
import javax.persistence.*;
import org.hibernate.search.annotations.*;
import java.lang.String;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Auto generated base entity SalesOpportunity.
 */
@javax.persistence.Entity
@Indexed
@Table(name="SALES_OPPORTUNITY")
public class SalesOpportunity extends Entity {
static {
java.util.Map<String, String> fields = new java.util.HashMap<String, String>();
        fields.put("salesOpportunityId", "SALES_OPPORTUNITY_ID");
        fields.put("opportunityName", "OPPORTUNITY_NAME");
        fields.put("description", "DESCRIPTION");
        fields.put("nextStep", "NEXT_STEP");
        fields.put("estimatedAmount", "ESTIMATED_AMOUNT");
        fields.put("estimatedProbability", "ESTIMATED_PROBABILITY");
        fields.put("currencyUomId", "CURRENCY_UOM_ID");
        fields.put("marketingCampaignId", "MARKETING_CAMPAIGN_ID");
        fields.put("dataSourceId", "DATA_SOURCE_ID");
        fields.put("estimatedCloseDate", "ESTIMATED_CLOSE_DATE");
        fields.put("opportunityStageId", "OPPORTUNITY_STAGE_ID");
        fields.put("typeEnumId", "TYPE_ENUM_ID");
        fields.put("createdByUserLogin", "CREATED_BY_USER_LOGIN");
        fields.put("lastUpdatedStamp", "LAST_UPDATED_STAMP");
        fields.put("lastUpdatedTxStamp", "LAST_UPDATED_TX_STAMP");
        fields.put("createdStamp", "CREATED_STAMP");
        fields.put("createdTxStamp", "CREATED_TX_STAMP");
fieldMapColumns.put("SalesOpportunity", fields);
}
  public static enum Fields implements EntityFieldInterface<SalesOpportunity> {
    salesOpportunityId("salesOpportunityId"),
    opportunityName("opportunityName"),
    description("description"),
    nextStep("nextStep"),
    estimatedAmount("estimatedAmount"),
    estimatedProbability("estimatedProbability"),
    currencyUomId("currencyUomId"),
    marketingCampaignId("marketingCampaignId"),
    dataSourceId("dataSourceId"),
    estimatedCloseDate("estimatedCloseDate"),
    opportunityStageId("opportunityStageId"),
    typeEnumId("typeEnumId"),
    createdByUserLogin("createdByUserLogin"),
    lastUpdatedStamp("lastUpdatedStamp"),
    lastUpdatedTxStamp("lastUpdatedTxStamp"),
    createdStamp("createdStamp"),
    createdTxStamp("createdTxStamp");
    private final String fieldName;
    private Fields(String name) { fieldName = name; }
    /** {@inheritDoc} */
    public String getName() { return fieldName; }
    /** {@inheritDoc} */
    public String asc() { return fieldName + " ASC"; }
    /** {@inheritDoc} */
    public String desc() { return fieldName + " DESC"; }
  }

   @org.hibernate.annotations.GenericGenerator(name="SalesOpportunity_GEN",  strategy="org.opentaps.foundation.entity.hibernate.OpentapsIdentifierGenerator")
   @GeneratedValue(generator="SalesOpportunity_GEN")
   @Id
   @DocumentId
   @org.hibernate.search.annotations.Fields( {
       @Field(index=Index.TOKENIZED, store=Store.YES),
     @Field(index=Index.UN_TOKENIZED, store=Store.YES)
   } )
   @Boost(10f)
   @Column(name="SALES_OPPORTUNITY_ID")
   private String salesOpportunityId;
   @org.hibernate.search.annotations.Fields( {
     @Field(index=Index.TOKENIZED, store=Store.YES)
   } )
   @Boost(5f)
   @Column(name="OPPORTUNITY_NAME")
   private String opportunityName;
   @org.hibernate.search.annotations.Fields( {
     @Field(index=Index.TOKENIZED, store=Store.YES)
   } )
   @Boost(1f)
   @Column(name="DESCRIPTION")
   private String description;
   @Column(name="NEXT_STEP")
   private String nextStep;
   @Column(name="ESTIMATED_AMOUNT")
   private BigDecimal estimatedAmount;
   @Column(name="ESTIMATED_PROBABILITY")
   private BigDecimal estimatedProbability;
   @Column(name="CURRENCY_UOM_ID")
   private String currencyUomId;
   @Column(name="MARKETING_CAMPAIGN_ID")
   private String marketingCampaignId;
   @Column(name="DATA_SOURCE_ID")
   private String dataSourceId;
   @Column(name="ESTIMATED_CLOSE_DATE")
   private Timestamp estimatedCloseDate;
   @org.hibernate.search.annotations.Fields( {
       @Field(index=Index.TOKENIZED, store=Store.YES),
     @Field(index=Index.UN_TOKENIZED, store=Store.YES)
   } )
   @Boost(1f)
   @Column(name="OPPORTUNITY_STAGE_ID")
   private String opportunityStageId;
   @Column(name="TYPE_ENUM_ID")
   private String typeEnumId;
   @Column(name="CREATED_BY_USER_LOGIN")
   private String createdByUserLogin;
   @Column(name="LAST_UPDATED_STAMP")
   private Timestamp lastUpdatedStamp;
   @Column(name="LAST_UPDATED_TX_STAMP")
   private Timestamp lastUpdatedTxStamp;
   @Column(name="CREATED_STAMP")
   private Timestamp createdStamp;
   @Column(name="CREATED_TX_STAMP")
   private Timestamp createdTxStamp;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="CURRENCY_UOM_ID", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   
   private Uom uom = null;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="OPPORTUNITY_STAGE_ID", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   
   private SalesOpportunityStage salesOpportunityStage = null;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="TYPE_ENUM_ID", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   
   private Enumeration typeEnumeration = null;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="MARKETING_CAMPAIGN_ID", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   
   private MarketingCampaign marketingCampaign = null;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="CREATED_BY_USER_LOGIN", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   
   private UserLogin userLogin = null;
   @OneToMany(fetch=FetchType.LAZY)
   @JoinColumn(name="SALES_OPPORTUNITY_ID")
   
   private List<InvoiceItem> invoiceItems = null;
   @OneToMany(fetch=FetchType.LAZY)
   @JoinColumn(name="SALES_OPPORTUNITY_ID")
   
   private List<OrderItem> orderItems = null;
   @OneToMany(fetch=FetchType.LAZY, mappedBy="salesOpportunity", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
   @JoinColumn(name="SALES_OPPORTUNITY_ID")
   
   private List<SalesOpportunityCompetitor> salesOpportunityCompetitors = null;
   @OneToMany(fetch=FetchType.LAZY, mappedBy="salesOpportunity", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
   @JoinColumn(name="SALES_OPPORTUNITY_ID")
   
   private List<SalesOpportunityContent> salesOpportunityContents = null;
   @OneToMany(fetch=FetchType.LAZY)
   @JoinColumn(name="SALES_OPPORTUNITY_ID")
   
   private List<SalesOpportunityHistory> salesOpportunityHistorys = null;
   @OneToMany(fetch=FetchType.LAZY, mappedBy="salesOpportunity", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
   @JoinColumn(name="SALES_OPPORTUNITY_ID")
   
   private List<SalesOpportunityQuote> salesOpportunityQuotes = null;
   @OneToMany(fetch=FetchType.LAZY, mappedBy="salesOpportunity", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
   @JoinColumn(name="SALES_OPPORTUNITY_ID")
   @ContainedIn
   private List<SalesOpportunityRole> salesOpportunityRoles = null;
   @OneToMany(fetch=FetchType.LAZY, mappedBy="salesOpportunity", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
   @JoinColumn(name="SALES_OPPORTUNITY_ID")
   
   private List<SalesOpportunityTrckCode> salesOpportunityTrckCodes = null;
   @OneToMany(fetch=FetchType.LAZY, mappedBy="salesOpportunity", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
   @JoinColumn(name="SALES_OPPORTUNITY_ID")
   
   private List<SalesOpportunityWorkEffort> salesOpportunityWorkEfforts = null;

  /**
   * Default constructor.
   */
  public SalesOpportunity() {
      super();
      this.baseEntityName = "SalesOpportunity";
      this.isView = false;
      
      this.primaryKeyNames = new ArrayList<String>();
      this.primaryKeyNames.add("salesOpportunityId");
      this.allFieldsNames = new ArrayList<String>();
      this.allFieldsNames.add("salesOpportunityId");this.allFieldsNames.add("opportunityName");this.allFieldsNames.add("description");this.allFieldsNames.add("nextStep");this.allFieldsNames.add("estimatedAmount");this.allFieldsNames.add("estimatedProbability");this.allFieldsNames.add("currencyUomId");this.allFieldsNames.add("marketingCampaignId");this.allFieldsNames.add("dataSourceId");this.allFieldsNames.add("estimatedCloseDate");this.allFieldsNames.add("opportunityStageId");this.allFieldsNames.add("typeEnumId");this.allFieldsNames.add("createdByUserLogin");this.allFieldsNames.add("lastUpdatedStamp");this.allFieldsNames.add("lastUpdatedTxStamp");this.allFieldsNames.add("createdStamp");this.allFieldsNames.add("createdTxStamp");
      this.nonPrimaryKeyNames = new ArrayList<String>();
      this.nonPrimaryKeyNames.addAll(allFieldsNames);
      this.nonPrimaryKeyNames.removeAll(primaryKeyNames);
  }

  /**
   * Constructor with a repository.
   * @param repository a <code>RepositoryInterface</code> value
   */
  public SalesOpportunity(RepositoryInterface repository) {
      this();
      initRepository(repository);
  }

    /**
     * Auto generated value setter.
     * @param salesOpportunityId the salesOpportunityId to set
     */
    public void setSalesOpportunityId(String salesOpportunityId) {
        this.salesOpportunityId = salesOpportunityId;
    }
    /**
     * Auto generated value setter.
     * @param opportunityName the opportunityName to set
     */
    public void setOpportunityName(String opportunityName) {
        this.opportunityName = opportunityName;
    }
    /**
     * Auto generated value setter.
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
     * Auto generated value setter.
     * @param nextStep the nextStep to set
     */
    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }
    /**
     * Auto generated value setter.
     * @param estimatedAmount the estimatedAmount to set
     */
    public void setEstimatedAmount(BigDecimal estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
    }
    /**
     * Auto generated value setter.
     * @param estimatedProbability the estimatedProbability to set
     */
    public void setEstimatedProbability(BigDecimal estimatedProbability) {
        this.estimatedProbability = estimatedProbability;
    }
    /**
     * Auto generated value setter.
     * @param currencyUomId the currencyUomId to set
     */
    public void setCurrencyUomId(String currencyUomId) {
        this.currencyUomId = currencyUomId;
    }
    /**
     * Auto generated value setter.
     * @param marketingCampaignId the marketingCampaignId to set
     */
    public void setMarketingCampaignId(String marketingCampaignId) {
        this.marketingCampaignId = marketingCampaignId;
    }
    /**
     * Auto generated value setter.
     * @param dataSourceId the dataSourceId to set
     */
    public void setDataSourceId(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }
    /**
     * Auto generated value setter.
     * @param estimatedCloseDate the estimatedCloseDate to set
     */
    public void setEstimatedCloseDate(Timestamp estimatedCloseDate) {
        this.estimatedCloseDate = estimatedCloseDate;
    }
    /**
     * Auto generated value setter.
     * @param opportunityStageId the opportunityStageId to set
     */
    public void setOpportunityStageId(String opportunityStageId) {
        this.opportunityStageId = opportunityStageId;
    }
    /**
     * Auto generated value setter.
     * @param typeEnumId the typeEnumId to set
     */
    public void setTypeEnumId(String typeEnumId) {
        this.typeEnumId = typeEnumId;
    }
    /**
     * Auto generated value setter.
     * @param createdByUserLogin the createdByUserLogin to set
     */
    public void setCreatedByUserLogin(String createdByUserLogin) {
        this.createdByUserLogin = createdByUserLogin;
    }
    /**
     * Auto generated value setter.
     * @param lastUpdatedStamp the lastUpdatedStamp to set
     */
    public void setLastUpdatedStamp(Timestamp lastUpdatedStamp) {
        this.lastUpdatedStamp = lastUpdatedStamp;
    }
    /**
     * Auto generated value setter.
     * @param lastUpdatedTxStamp the lastUpdatedTxStamp to set
     */
    public void setLastUpdatedTxStamp(Timestamp lastUpdatedTxStamp) {
        this.lastUpdatedTxStamp = lastUpdatedTxStamp;
    }
    /**
     * Auto generated value setter.
     * @param createdStamp the createdStamp to set
     */
    public void setCreatedStamp(Timestamp createdStamp) {
        this.createdStamp = createdStamp;
    }
    /**
     * Auto generated value setter.
     * @param createdTxStamp the createdTxStamp to set
     */
    public void setCreatedTxStamp(Timestamp createdTxStamp) {
        this.createdTxStamp = createdTxStamp;
    }

    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getSalesOpportunityId() {
        return this.salesOpportunityId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getOpportunityName() {
        return this.opportunityName;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getNextStep() {
        return this.nextStep;
    }
    /**
     * Auto generated value accessor.
     * @return <code>BigDecimal</code>
     */
    public BigDecimal getEstimatedAmount() {
        return this.estimatedAmount;
    }
    /**
     * Auto generated value accessor.
     * @return <code>BigDecimal</code>
     */
    public BigDecimal getEstimatedProbability() {
        return this.estimatedProbability;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getCurrencyUomId() {
        return this.currencyUomId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getMarketingCampaignId() {
        return this.marketingCampaignId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getDataSourceId() {
        return this.dataSourceId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>Timestamp</code>
     */
    public Timestamp getEstimatedCloseDate() {
        return this.estimatedCloseDate;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getOpportunityStageId() {
        return this.opportunityStageId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getTypeEnumId() {
        return this.typeEnumId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getCreatedByUserLogin() {
        return this.createdByUserLogin;
    }
    /**
     * Auto generated value accessor.
     * @return <code>Timestamp</code>
     */
    public Timestamp getLastUpdatedStamp() {
        return this.lastUpdatedStamp;
    }
    /**
     * Auto generated value accessor.
     * @return <code>Timestamp</code>
     */
    public Timestamp getLastUpdatedTxStamp() {
        return this.lastUpdatedTxStamp;
    }
    /**
     * Auto generated value accessor.
     * @return <code>Timestamp</code>
     */
    public Timestamp getCreatedStamp() {
        return this.createdStamp;
    }
    /**
     * Auto generated value accessor.
     * @return <code>Timestamp</code>
     */
    public Timestamp getCreatedTxStamp() {
        return this.createdTxStamp;
    }

    /**
     * Auto generated method that gets the related <code>Uom</code> by the relation named <code>Uom</code>.
     * @return the <code>Uom</code>
     * @throws RepositoryException if an error occurs
     */
    public Uom getUom() throws RepositoryException {
        if (this.uom == null) {
            this.uom = getRelatedOne(Uom.class, "Uom");
        }
        return this.uom;
    }
    /**
     * Auto generated method that gets the related <code>SalesOpportunityStage</code> by the relation named <code>SalesOpportunityStage</code>.
     * @return the <code>SalesOpportunityStage</code>
     * @throws RepositoryException if an error occurs
     */
    public SalesOpportunityStage getSalesOpportunityStage() throws RepositoryException {
        if (this.salesOpportunityStage == null) {
            this.salesOpportunityStage = getRelatedOne(SalesOpportunityStage.class, "SalesOpportunityStage");
        }
        return this.salesOpportunityStage;
    }
    /**
     * Auto generated method that gets the related <code>Enumeration</code> by the relation named <code>TypeEnumeration</code>.
     * @return the <code>Enumeration</code>
     * @throws RepositoryException if an error occurs
     */
    public Enumeration getTypeEnumeration() throws RepositoryException {
        if (this.typeEnumeration == null) {
            this.typeEnumeration = getRelatedOne(Enumeration.class, "TypeEnumeration");
        }
        return this.typeEnumeration;
    }
    /**
     * Auto generated method that gets the related <code>MarketingCampaign</code> by the relation named <code>MarketingCampaign</code>.
     * @return the <code>MarketingCampaign</code>
     * @throws RepositoryException if an error occurs
     */
    public MarketingCampaign getMarketingCampaign() throws RepositoryException {
        if (this.marketingCampaign == null) {
            this.marketingCampaign = getRelatedOne(MarketingCampaign.class, "MarketingCampaign");
        }
        return this.marketingCampaign;
    }
    /**
     * Auto generated method that gets the related <code>UserLogin</code> by the relation named <code>UserLogin</code>.
     * @return the <code>UserLogin</code>
     * @throws RepositoryException if an error occurs
     */
    public UserLogin getUserLogin() throws RepositoryException {
        if (this.userLogin == null) {
            this.userLogin = getRelatedOne(UserLogin.class, "UserLogin");
        }
        return this.userLogin;
    }
    /**
     * Auto generated method that gets the related <code>InvoiceItem</code> by the relation named <code>InvoiceItem</code>.
     * @return the list of <code>InvoiceItem</code>
     * @throws RepositoryException if an error occurs
     */
    public List<? extends InvoiceItem> getInvoiceItems() throws RepositoryException {
        if (this.invoiceItems == null) {
            this.invoiceItems = getRelated(InvoiceItem.class, "InvoiceItem");
        }
        return this.invoiceItems;
    }
    /**
     * Auto generated method that gets the related <code>OrderItem</code> by the relation named <code>OrderItem</code>.
     * @return the list of <code>OrderItem</code>
     * @throws RepositoryException if an error occurs
     */
    public List<? extends OrderItem> getOrderItems() throws RepositoryException {
        if (this.orderItems == null) {
            this.orderItems = getRelated(OrderItem.class, "OrderItem");
        }
        return this.orderItems;
    }
    /**
     * Auto generated method that gets the related <code>SalesOpportunityCompetitor</code> by the relation named <code>SalesOpportunityCompetitor</code>.
     * @return the list of <code>SalesOpportunityCompetitor</code>
     * @throws RepositoryException if an error occurs
     */
    public List<? extends SalesOpportunityCompetitor> getSalesOpportunityCompetitors() throws RepositoryException {
        if (this.salesOpportunityCompetitors == null) {
            this.salesOpportunityCompetitors = getRelated(SalesOpportunityCompetitor.class, "SalesOpportunityCompetitor");
        }
        return this.salesOpportunityCompetitors;
    }
    /**
     * Auto generated method that gets the related <code>SalesOpportunityContent</code> by the relation named <code>SalesOpportunityContent</code>.
     * @return the list of <code>SalesOpportunityContent</code>
     * @throws RepositoryException if an error occurs
     */
    public List<? extends SalesOpportunityContent> getSalesOpportunityContents() throws RepositoryException {
        if (this.salesOpportunityContents == null) {
            this.salesOpportunityContents = getRelated(SalesOpportunityContent.class, "SalesOpportunityContent");
        }
        return this.salesOpportunityContents;
    }
    /**
     * Auto generated method that gets the related <code>SalesOpportunityHistory</code> by the relation named <code>SalesOpportunityHistory</code>.
     * @return the list of <code>SalesOpportunityHistory</code>
     * @throws RepositoryException if an error occurs
     */
    public List<? extends SalesOpportunityHistory> getSalesOpportunityHistorys() throws RepositoryException {
        if (this.salesOpportunityHistorys == null) {
            this.salesOpportunityHistorys = getRelated(SalesOpportunityHistory.class, "SalesOpportunityHistory");
        }
        return this.salesOpportunityHistorys;
    }
    /**
     * Auto generated method that gets the related <code>SalesOpportunityQuote</code> by the relation named <code>SalesOpportunityQuote</code>.
     * @return the list of <code>SalesOpportunityQuote</code>
     * @throws RepositoryException if an error occurs
     */
    public List<? extends SalesOpportunityQuote> getSalesOpportunityQuotes() throws RepositoryException {
        if (this.salesOpportunityQuotes == null) {
            this.salesOpportunityQuotes = getRelated(SalesOpportunityQuote.class, "SalesOpportunityQuote");
        }
        return this.salesOpportunityQuotes;
    }
    /**
     * Auto generated method that gets the related <code>SalesOpportunityRole</code> by the relation named <code>SalesOpportunityRole</code>.
     * @return the list of <code>SalesOpportunityRole</code>
     * @throws RepositoryException if an error occurs
     */
    public List<? extends SalesOpportunityRole> getSalesOpportunityRoles() throws RepositoryException {
        if (this.salesOpportunityRoles == null) {
            this.salesOpportunityRoles = getRelated(SalesOpportunityRole.class, "SalesOpportunityRole");
        }
        return this.salesOpportunityRoles;
    }
    /**
     * Auto generated method that gets the related <code>SalesOpportunityTrckCode</code> by the relation named <code>SalesOpportunityTrckCode</code>.
     * @return the list of <code>SalesOpportunityTrckCode</code>
     * @throws RepositoryException if an error occurs
     */
    public List<? extends SalesOpportunityTrckCode> getSalesOpportunityTrckCodes() throws RepositoryException {
        if (this.salesOpportunityTrckCodes == null) {
            this.salesOpportunityTrckCodes = getRelated(SalesOpportunityTrckCode.class, "SalesOpportunityTrckCode");
        }
        return this.salesOpportunityTrckCodes;
    }
    /**
     * Auto generated method that gets the related <code>SalesOpportunityWorkEffort</code> by the relation named <code>SalesOpportunityWorkEffort</code>.
     * @return the list of <code>SalesOpportunityWorkEffort</code>
     * @throws RepositoryException if an error occurs
     */
    public List<? extends SalesOpportunityWorkEffort> getSalesOpportunityWorkEfforts() throws RepositoryException {
        if (this.salesOpportunityWorkEfforts == null) {
            this.salesOpportunityWorkEfforts = getRelated(SalesOpportunityWorkEffort.class, "SalesOpportunityWorkEffort");
        }
        return this.salesOpportunityWorkEfforts;
    }

    /**
     * Auto generated value setter.
     * @param uom the uom to set
    */
    public void setUom(Uom uom) {
        this.uom = uom;
    }
    /**
     * Auto generated value setter.
     * @param salesOpportunityStage the salesOpportunityStage to set
    */
    public void setSalesOpportunityStage(SalesOpportunityStage salesOpportunityStage) {
        this.salesOpportunityStage = salesOpportunityStage;
    }
    /**
     * Auto generated value setter.
     * @param typeEnumeration the typeEnumeration to set
    */
    public void setTypeEnumeration(Enumeration typeEnumeration) {
        this.typeEnumeration = typeEnumeration;
    }
    /**
     * Auto generated value setter.
     * @param marketingCampaign the marketingCampaign to set
    */
    public void setMarketingCampaign(MarketingCampaign marketingCampaign) {
        this.marketingCampaign = marketingCampaign;
    }
    /**
     * Auto generated value setter.
     * @param userLogin the userLogin to set
    */
    public void setUserLogin(UserLogin userLogin) {
        this.userLogin = userLogin;
    }
    /**
     * Auto generated value setter.
     * @param invoiceItems the invoiceItems to set
    */
    public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }
    /**
     * Auto generated value setter.
     * @param orderItems the orderItems to set
    */
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    /**
     * Auto generated value setter.
     * @param salesOpportunityCompetitors the salesOpportunityCompetitors to set
    */
    public void setSalesOpportunityCompetitors(List<SalesOpportunityCompetitor> salesOpportunityCompetitors) {
        this.salesOpportunityCompetitors = salesOpportunityCompetitors;
    }
    /**
     * Auto generated value setter.
     * @param salesOpportunityContents the salesOpportunityContents to set
    */
    public void setSalesOpportunityContents(List<SalesOpportunityContent> salesOpportunityContents) {
        this.salesOpportunityContents = salesOpportunityContents;
    }
    /**
     * Auto generated value setter.
     * @param salesOpportunityHistorys the salesOpportunityHistorys to set
    */
    public void setSalesOpportunityHistorys(List<SalesOpportunityHistory> salesOpportunityHistorys) {
        this.salesOpportunityHistorys = salesOpportunityHistorys;
    }
    /**
     * Auto generated value setter.
     * @param salesOpportunityQuotes the salesOpportunityQuotes to set
    */
    public void setSalesOpportunityQuotes(List<SalesOpportunityQuote> salesOpportunityQuotes) {
        this.salesOpportunityQuotes = salesOpportunityQuotes;
    }
    /**
     * Auto generated value setter.
     * @param salesOpportunityRoles the salesOpportunityRoles to set
    */
    public void setSalesOpportunityRoles(List<SalesOpportunityRole> salesOpportunityRoles) {
        this.salesOpportunityRoles = salesOpportunityRoles;
    }
    /**
     * Auto generated value setter.
     * @param salesOpportunityTrckCodes the salesOpportunityTrckCodes to set
    */
    public void setSalesOpportunityTrckCodes(List<SalesOpportunityTrckCode> salesOpportunityTrckCodes) {
        this.salesOpportunityTrckCodes = salesOpportunityTrckCodes;
    }
    /**
     * Auto generated value setter.
     * @param salesOpportunityWorkEfforts the salesOpportunityWorkEfforts to set
    */
    public void setSalesOpportunityWorkEfforts(List<SalesOpportunityWorkEffort> salesOpportunityWorkEfforts) {
        this.salesOpportunityWorkEfforts = salesOpportunityWorkEfforts;
    }

    /**
     * Auto generated method that add item to collection.
     */
    public void addSalesOpportunityCompetitor(SalesOpportunityCompetitor salesOpportunityCompetitor) {
        if (this.salesOpportunityCompetitors == null) {
            this.salesOpportunityCompetitors = new ArrayList<SalesOpportunityCompetitor>();
        }
        this.salesOpportunityCompetitors.add(salesOpportunityCompetitor);
    }
    /**
     * Auto generated method that remove item from collection.
     */
    public void removeSalesOpportunityCompetitor(SalesOpportunityCompetitor salesOpportunityCompetitor) {
        if (this.salesOpportunityCompetitors == null) {
            return;
        }
        this.salesOpportunityCompetitors.remove(salesOpportunityCompetitor);
    }
    /**
     * Auto generated method that clear items from collection.
     */
    public void clearSalesOpportunityCompetitor() {
        if (this.salesOpportunityCompetitors == null) {
            return;
        }
        this.salesOpportunityCompetitors.clear();
    }
    /**
     * Auto generated method that add item to collection.
     */
    public void addSalesOpportunityContent(SalesOpportunityContent salesOpportunityContent) {
        if (this.salesOpportunityContents == null) {
            this.salesOpportunityContents = new ArrayList<SalesOpportunityContent>();
        }
        this.salesOpportunityContents.add(salesOpportunityContent);
    }
    /**
     * Auto generated method that remove item from collection.
     */
    public void removeSalesOpportunityContent(SalesOpportunityContent salesOpportunityContent) {
        if (this.salesOpportunityContents == null) {
            return;
        }
        this.salesOpportunityContents.remove(salesOpportunityContent);
    }
    /**
     * Auto generated method that clear items from collection.
     */
    public void clearSalesOpportunityContent() {
        if (this.salesOpportunityContents == null) {
            return;
        }
        this.salesOpportunityContents.clear();
    }
    /**
     * Auto generated method that add item to collection.
     */
    public void addSalesOpportunityQuote(SalesOpportunityQuote salesOpportunityQuote) {
        if (this.salesOpportunityQuotes == null) {
            this.salesOpportunityQuotes = new ArrayList<SalesOpportunityQuote>();
        }
        this.salesOpportunityQuotes.add(salesOpportunityQuote);
    }
    /**
     * Auto generated method that remove item from collection.
     */
    public void removeSalesOpportunityQuote(SalesOpportunityQuote salesOpportunityQuote) {
        if (this.salesOpportunityQuotes == null) {
            return;
        }
        this.salesOpportunityQuotes.remove(salesOpportunityQuote);
    }
    /**
     * Auto generated method that clear items from collection.
     */
    public void clearSalesOpportunityQuote() {
        if (this.salesOpportunityQuotes == null) {
            return;
        }
        this.salesOpportunityQuotes.clear();
    }
    /**
     * Auto generated method that add item to collection.
     */
    public void addSalesOpportunityRole(SalesOpportunityRole salesOpportunityRole) {
        if (this.salesOpportunityRoles == null) {
            this.salesOpportunityRoles = new ArrayList<SalesOpportunityRole>();
        }
        this.salesOpportunityRoles.add(salesOpportunityRole);
    }
    /**
     * Auto generated method that remove item from collection.
     */
    public void removeSalesOpportunityRole(SalesOpportunityRole salesOpportunityRole) {
        if (this.salesOpportunityRoles == null) {
            return;
        }
        this.salesOpportunityRoles.remove(salesOpportunityRole);
    }
    /**
     * Auto generated method that clear items from collection.
     */
    public void clearSalesOpportunityRole() {
        if (this.salesOpportunityRoles == null) {
            return;
        }
        this.salesOpportunityRoles.clear();
    }
    /**
     * Auto generated method that add item to collection.
     */
    public void addSalesOpportunityTrckCode(SalesOpportunityTrckCode salesOpportunityTrckCode) {
        if (this.salesOpportunityTrckCodes == null) {
            this.salesOpportunityTrckCodes = new ArrayList<SalesOpportunityTrckCode>();
        }
        this.salesOpportunityTrckCodes.add(salesOpportunityTrckCode);
    }
    /**
     * Auto generated method that remove item from collection.
     */
    public void removeSalesOpportunityTrckCode(SalesOpportunityTrckCode salesOpportunityTrckCode) {
        if (this.salesOpportunityTrckCodes == null) {
            return;
        }
        this.salesOpportunityTrckCodes.remove(salesOpportunityTrckCode);
    }
    /**
     * Auto generated method that clear items from collection.
     */
    public void clearSalesOpportunityTrckCode() {
        if (this.salesOpportunityTrckCodes == null) {
            return;
        }
        this.salesOpportunityTrckCodes.clear();
    }
    /**
     * Auto generated method that add item to collection.
     */
    public void addSalesOpportunityWorkEffort(SalesOpportunityWorkEffort salesOpportunityWorkEffort) {
        if (this.salesOpportunityWorkEfforts == null) {
            this.salesOpportunityWorkEfforts = new ArrayList<SalesOpportunityWorkEffort>();
        }
        this.salesOpportunityWorkEfforts.add(salesOpportunityWorkEffort);
    }
    /**
     * Auto generated method that remove item from collection.
     */
    public void removeSalesOpportunityWorkEffort(SalesOpportunityWorkEffort salesOpportunityWorkEffort) {
        if (this.salesOpportunityWorkEfforts == null) {
            return;
        }
        this.salesOpportunityWorkEfforts.remove(salesOpportunityWorkEffort);
    }
    /**
     * Auto generated method that clear items from collection.
     */
    public void clearSalesOpportunityWorkEffort() {
        if (this.salesOpportunityWorkEfforts == null) {
            return;
        }
        this.salesOpportunityWorkEfforts.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void fromMap(Map<String, Object> mapValue) {
        preInit();
        setSalesOpportunityId((String) mapValue.get("salesOpportunityId"));
        setOpportunityName((String) mapValue.get("opportunityName"));
        setDescription((String) mapValue.get("description"));
        setNextStep((String) mapValue.get("nextStep"));
        setEstimatedAmount(convertToBigDecimal(mapValue.get("estimatedAmount")));
        setEstimatedProbability(convertToBigDecimal(mapValue.get("estimatedProbability")));
        setCurrencyUomId((String) mapValue.get("currencyUomId"));
        setMarketingCampaignId((String) mapValue.get("marketingCampaignId"));
        setDataSourceId((String) mapValue.get("dataSourceId"));
        setEstimatedCloseDate((Timestamp) mapValue.get("estimatedCloseDate"));
        setOpportunityStageId((String) mapValue.get("opportunityStageId"));
        setTypeEnumId((String) mapValue.get("typeEnumId"));
        setCreatedByUserLogin((String) mapValue.get("createdByUserLogin"));
        setLastUpdatedStamp((Timestamp) mapValue.get("lastUpdatedStamp"));
        setLastUpdatedTxStamp((Timestamp) mapValue.get("lastUpdatedTxStamp"));
        setCreatedStamp((Timestamp) mapValue.get("createdStamp"));
        setCreatedTxStamp((Timestamp) mapValue.get("createdTxStamp"));
        postInit();
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> mapValue = new FastMap<String, Object>();
        mapValue.put("salesOpportunityId", getSalesOpportunityId());
        mapValue.put("opportunityName", getOpportunityName());
        mapValue.put("description", getDescription());
        mapValue.put("nextStep", getNextStep());
        mapValue.put("estimatedAmount", getEstimatedAmount());
        mapValue.put("estimatedProbability", getEstimatedProbability());
        mapValue.put("currencyUomId", getCurrencyUomId());
        mapValue.put("marketingCampaignId", getMarketingCampaignId());
        mapValue.put("dataSourceId", getDataSourceId());
        mapValue.put("estimatedCloseDate", getEstimatedCloseDate());
        mapValue.put("opportunityStageId", getOpportunityStageId());
        mapValue.put("typeEnumId", getTypeEnumId());
        mapValue.put("createdByUserLogin", getCreatedByUserLogin());
        mapValue.put("lastUpdatedStamp", getLastUpdatedStamp());
        mapValue.put("lastUpdatedTxStamp", getLastUpdatedTxStamp());
        mapValue.put("createdStamp", getCreatedStamp());
        mapValue.put("createdTxStamp", getCreatedTxStamp());
        return mapValue;
    }


}
