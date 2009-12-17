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
import java.lang.Long;
import java.lang.String;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Auto generated base entity PartySupplementalData.
 */
@javax.persistence.Entity
@Indexed
@Table(name="PARTY_SUPPLEMENTAL_DATA")
public class PartySupplementalData extends Entity {
static {
java.util.Map<String, String> fields = new java.util.HashMap<String, String>();
        fields.put("partyId", "PARTY_ID");
        fields.put("parentPartyId", "PARENT_PARTY_ID");
        fields.put("departmentName", "DEPARTMENT_NAME");
        fields.put("generalProfTitle", "GENERAL_PROF_TITLE");
        fields.put("companyName", "COMPANY_NAME");
        fields.put("companyNameLocal", "COMPANY_NAME_LOCAL");
        fields.put("annualRevenue", "ANNUAL_REVENUE");
        fields.put("currencyUomId", "CURRENCY_UOM_ID");
        fields.put("numberEmployees", "NUMBER_EMPLOYEES");
        fields.put("industryEnumId", "INDUSTRY_ENUM_ID");
        fields.put("ownershipEnumId", "OWNERSHIP_ENUM_ID");
        fields.put("sicCode", "SIC_CODE");
        fields.put("tickerSymbol", "TICKER_SYMBOL");
        fields.put("importantNote", "IMPORTANT_NOTE");
        fields.put("primaryPostalAddressId", "PRIMARY_POSTAL_ADDRESS_ID");
        fields.put("primaryTelecomNumberId", "PRIMARY_TELECOM_NUMBER_ID");
        fields.put("primaryEmailId", "PRIMARY_EMAIL_ID");
        fields.put("lastUpdatedStamp", "LAST_UPDATED_STAMP");
        fields.put("lastUpdatedTxStamp", "LAST_UPDATED_TX_STAMP");
        fields.put("createdStamp", "CREATED_STAMP");
        fields.put("createdTxStamp", "CREATED_TX_STAMP");
fieldMapColumns.put("PartySupplementalData", fields);
}
  public static enum Fields implements EntityFieldInterface<PartySupplementalData> {
    partyId("partyId"),
    parentPartyId("parentPartyId"),
    departmentName("departmentName"),
    generalProfTitle("generalProfTitle"),
    companyName("companyName"),
    companyNameLocal("companyNameLocal"),
    annualRevenue("annualRevenue"),
    currencyUomId("currencyUomId"),
    numberEmployees("numberEmployees"),
    industryEnumId("industryEnumId"),
    ownershipEnumId("ownershipEnumId"),
    sicCode("sicCode"),
    tickerSymbol("tickerSymbol"),
    importantNote("importantNote"),
    primaryPostalAddressId("primaryPostalAddressId"),
    primaryTelecomNumberId("primaryTelecomNumberId"),
    primaryEmailId("primaryEmailId"),
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

   @org.hibernate.annotations.GenericGenerator(name="PartySupplementalData_GEN",  strategy="org.opentaps.foundation.entity.hibernate.OpentapsIdentifierGenerator")
   @GeneratedValue(generator="PartySupplementalData_GEN")
   @Id
   @DocumentId
   @org.hibernate.search.annotations.Fields( {
       @Field(index=Index.TOKENIZED, store=Store.NO),
     @Field(index=Index.UN_TOKENIZED, store=Store.YES)
   } )
   @Boost(1f)
   @Column(name="PARTY_ID")
   private String partyId;
   @Column(name="PARENT_PARTY_ID")
   private String parentPartyId;
   @Column(name="DEPARTMENT_NAME")
   private String departmentName;
   @Column(name="GENERAL_PROF_TITLE")
   private String generalProfTitle;
   @Column(name="COMPANY_NAME")
   private String companyName;
   @Column(name="COMPANY_NAME_LOCAL")
   private String companyNameLocal;
   @Column(name="ANNUAL_REVENUE")
   private BigDecimal annualRevenue;
   @Column(name="CURRENCY_UOM_ID")
   private String currencyUomId;
   @Column(name="NUMBER_EMPLOYEES")
   private Long numberEmployees;
   @Column(name="INDUSTRY_ENUM_ID")
   private String industryEnumId;
   @Column(name="OWNERSHIP_ENUM_ID")
   private String ownershipEnumId;
   @Column(name="SIC_CODE")
   private String sicCode;
   @Column(name="TICKER_SYMBOL")
   private String tickerSymbol;
   @Column(name="IMPORTANT_NOTE")
   private String importantNote;
   @Column(name="PRIMARY_POSTAL_ADDRESS_ID")
   private String primaryPostalAddressId;
   @Column(name="PRIMARY_TELECOM_NUMBER_ID")
   private String primaryTelecomNumberId;
   @Column(name="PRIMARY_EMAIL_ID")
   private String primaryEmailId;
   @Column(name="LAST_UPDATED_STAMP")
   private Timestamp lastUpdatedStamp;
   @Column(name="LAST_UPDATED_TX_STAMP")
   private Timestamp lastUpdatedTxStamp;
   @Column(name="CREATED_STAMP")
   private Timestamp createdStamp;
   @Column(name="CREATED_TX_STAMP")
   private Timestamp createdTxStamp;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="INDUSTRY_ENUM_ID", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   
   private Enumeration industryEnumeration = null;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="OWNERSHIP_ENUM_ID", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   
   private Enumeration ownershipEnumeration = null;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="PARTY_ID", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   @IndexedEmbedded(depth = 2)
   private Party party = null;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="PARENT_PARTY_ID", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   @IndexedEmbedded(depth = 2)
   private Party parentParty = null;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="CURRENCY_UOM_ID", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   
   private Uom uom = null;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="PRIMARY_POSTAL_ADDRESS_ID", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   
   private ContactMech contactMechPostalAddressContactMech = null;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="PRIMARY_TELECOM_NUMBER_ID", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   
   private ContactMech contactMechTelecomNumberContactMech = null;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="PRIMARY_EMAIL_ID", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   
   private ContactMech contactMechEmailContactMech = null;

  /**
   * Default constructor.
   */
  public PartySupplementalData() {
      super();
      this.baseEntityName = "PartySupplementalData";
      this.isView = false;
      
      this.primaryKeyNames = new ArrayList<String>();
      this.primaryKeyNames.add("partyId");
      this.allFieldsNames = new ArrayList<String>();
      this.allFieldsNames.add("partyId");this.allFieldsNames.add("parentPartyId");this.allFieldsNames.add("departmentName");this.allFieldsNames.add("generalProfTitle");this.allFieldsNames.add("companyName");this.allFieldsNames.add("companyNameLocal");this.allFieldsNames.add("annualRevenue");this.allFieldsNames.add("currencyUomId");this.allFieldsNames.add("numberEmployees");this.allFieldsNames.add("industryEnumId");this.allFieldsNames.add("ownershipEnumId");this.allFieldsNames.add("sicCode");this.allFieldsNames.add("tickerSymbol");this.allFieldsNames.add("importantNote");this.allFieldsNames.add("primaryPostalAddressId");this.allFieldsNames.add("primaryTelecomNumberId");this.allFieldsNames.add("primaryEmailId");this.allFieldsNames.add("lastUpdatedStamp");this.allFieldsNames.add("lastUpdatedTxStamp");this.allFieldsNames.add("createdStamp");this.allFieldsNames.add("createdTxStamp");
      this.nonPrimaryKeyNames = new ArrayList<String>();
      this.nonPrimaryKeyNames.addAll(allFieldsNames);
      this.nonPrimaryKeyNames.removeAll(primaryKeyNames);
  }

  /**
   * Constructor with a repository.
   * @param repository a <code>RepositoryInterface</code> value
   */
  public PartySupplementalData(RepositoryInterface repository) {
      this();
      initRepository(repository);
  }

    /**
     * Auto generated value setter.
     * @param partyId the partyId to set
     */
    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }
    /**
     * Auto generated value setter.
     * @param parentPartyId the parentPartyId to set
     */
    public void setParentPartyId(String parentPartyId) {
        this.parentPartyId = parentPartyId;
    }
    /**
     * Auto generated value setter.
     * @param departmentName the departmentName to set
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    /**
     * Auto generated value setter.
     * @param generalProfTitle the generalProfTitle to set
     */
    public void setGeneralProfTitle(String generalProfTitle) {
        this.generalProfTitle = generalProfTitle;
    }
    /**
     * Auto generated value setter.
     * @param companyName the companyName to set
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    /**
     * Auto generated value setter.
     * @param companyNameLocal the companyNameLocal to set
     */
    public void setCompanyNameLocal(String companyNameLocal) {
        this.companyNameLocal = companyNameLocal;
    }
    /**
     * Auto generated value setter.
     * @param annualRevenue the annualRevenue to set
     */
    public void setAnnualRevenue(BigDecimal annualRevenue) {
        this.annualRevenue = annualRevenue;
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
     * @param numberEmployees the numberEmployees to set
     */
    public void setNumberEmployees(Long numberEmployees) {
        this.numberEmployees = numberEmployees;
    }
    /**
     * Auto generated value setter.
     * @param industryEnumId the industryEnumId to set
     */
    public void setIndustryEnumId(String industryEnumId) {
        this.industryEnumId = industryEnumId;
    }
    /**
     * Auto generated value setter.
     * @param ownershipEnumId the ownershipEnumId to set
     */
    public void setOwnershipEnumId(String ownershipEnumId) {
        this.ownershipEnumId = ownershipEnumId;
    }
    /**
     * Auto generated value setter.
     * @param sicCode the sicCode to set
     */
    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }
    /**
     * Auto generated value setter.
     * @param tickerSymbol the tickerSymbol to set
     */
    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }
    /**
     * Auto generated value setter.
     * @param importantNote the importantNote to set
     */
    public void setImportantNote(String importantNote) {
        this.importantNote = importantNote;
    }
    /**
     * Auto generated value setter.
     * @param primaryPostalAddressId the primaryPostalAddressId to set
     */
    public void setPrimaryPostalAddressId(String primaryPostalAddressId) {
        this.primaryPostalAddressId = primaryPostalAddressId;
    }
    /**
     * Auto generated value setter.
     * @param primaryTelecomNumberId the primaryTelecomNumberId to set
     */
    public void setPrimaryTelecomNumberId(String primaryTelecomNumberId) {
        this.primaryTelecomNumberId = primaryTelecomNumberId;
    }
    /**
     * Auto generated value setter.
     * @param primaryEmailId the primaryEmailId to set
     */
    public void setPrimaryEmailId(String primaryEmailId) {
        this.primaryEmailId = primaryEmailId;
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
    public String getPartyId() {
        return this.partyId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getParentPartyId() {
        return this.parentPartyId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getDepartmentName() {
        return this.departmentName;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getGeneralProfTitle() {
        return this.generalProfTitle;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getCompanyName() {
        return this.companyName;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getCompanyNameLocal() {
        return this.companyNameLocal;
    }
    /**
     * Auto generated value accessor.
     * @return <code>BigDecimal</code>
     */
    public BigDecimal getAnnualRevenue() {
        return this.annualRevenue;
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
     * @return <code>Long</code>
     */
    public Long getNumberEmployees() {
        return this.numberEmployees;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getIndustryEnumId() {
        return this.industryEnumId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getOwnershipEnumId() {
        return this.ownershipEnumId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getSicCode() {
        return this.sicCode;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getTickerSymbol() {
        return this.tickerSymbol;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getImportantNote() {
        return this.importantNote;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getPrimaryPostalAddressId() {
        return this.primaryPostalAddressId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getPrimaryTelecomNumberId() {
        return this.primaryTelecomNumberId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getPrimaryEmailId() {
        return this.primaryEmailId;
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
     * Auto generated method that gets the related <code>Enumeration</code> by the relation named <code>IndustryEnumeration</code>.
     * @return the <code>Enumeration</code>
     * @throws RepositoryException if an error occurs
     */
    public Enumeration getIndustryEnumeration() throws RepositoryException {
        if (this.industryEnumeration == null) {
            this.industryEnumeration = getRelatedOne(Enumeration.class, "IndustryEnumeration");
        }
        return this.industryEnumeration;
    }
    /**
     * Auto generated method that gets the related <code>Enumeration</code> by the relation named <code>OwnershipEnumeration</code>.
     * @return the <code>Enumeration</code>
     * @throws RepositoryException if an error occurs
     */
    public Enumeration getOwnershipEnumeration() throws RepositoryException {
        if (this.ownershipEnumeration == null) {
            this.ownershipEnumeration = getRelatedOne(Enumeration.class, "OwnershipEnumeration");
        }
        return this.ownershipEnumeration;
    }
    /**
     * Auto generated method that gets the related <code>Party</code> by the relation named <code>Party</code>.
     * @return the <code>Party</code>
     * @throws RepositoryException if an error occurs
     */
    public Party getParty() throws RepositoryException {
        if (this.party == null) {
            this.party = getRelatedOne(Party.class, "Party");
        }
        return this.party;
    }
    /**
     * Auto generated method that gets the related <code>Party</code> by the relation named <code>ParentParty</code>.
     * @return the <code>Party</code>
     * @throws RepositoryException if an error occurs
     */
    public Party getParentParty() throws RepositoryException {
        if (this.parentParty == null) {
            this.parentParty = getRelatedOne(Party.class, "ParentParty");
        }
        return this.parentParty;
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
     * Auto generated method that gets the related <code>ContactMech</code> by the relation named <code>ContactMechPostalAddressContactMech</code>.
     * @return the <code>ContactMech</code>
     * @throws RepositoryException if an error occurs
     */
    public ContactMech getContactMechPostalAddressContactMech() throws RepositoryException {
        if (this.contactMechPostalAddressContactMech == null) {
            this.contactMechPostalAddressContactMech = getRelatedOne(ContactMech.class, "ContactMechPostalAddressContactMech");
        }
        return this.contactMechPostalAddressContactMech;
    }
    /**
     * Auto generated method that gets the related <code>ContactMech</code> by the relation named <code>ContactMechTelecomNumberContactMech</code>.
     * @return the <code>ContactMech</code>
     * @throws RepositoryException if an error occurs
     */
    public ContactMech getContactMechTelecomNumberContactMech() throws RepositoryException {
        if (this.contactMechTelecomNumberContactMech == null) {
            this.contactMechTelecomNumberContactMech = getRelatedOne(ContactMech.class, "ContactMechTelecomNumberContactMech");
        }
        return this.contactMechTelecomNumberContactMech;
    }
    /**
     * Auto generated method that gets the related <code>ContactMech</code> by the relation named <code>ContactMechEmailContactMech</code>.
     * @return the <code>ContactMech</code>
     * @throws RepositoryException if an error occurs
     */
    public ContactMech getContactMechEmailContactMech() throws RepositoryException {
        if (this.contactMechEmailContactMech == null) {
            this.contactMechEmailContactMech = getRelatedOne(ContactMech.class, "ContactMechEmailContactMech");
        }
        return this.contactMechEmailContactMech;
    }

    /**
     * Auto generated value setter.
     * @param industryEnumeration the industryEnumeration to set
    */
    public void setIndustryEnumeration(Enumeration industryEnumeration) {
        this.industryEnumeration = industryEnumeration;
    }
    /**
     * Auto generated value setter.
     * @param ownershipEnumeration the ownershipEnumeration to set
    */
    public void setOwnershipEnumeration(Enumeration ownershipEnumeration) {
        this.ownershipEnumeration = ownershipEnumeration;
    }
    /**
     * Auto generated value setter.
     * @param party the party to set
    */
    public void setParty(Party party) {
        this.party = party;
    }
    /**
     * Auto generated value setter.
     * @param parentParty the parentParty to set
    */
    public void setParentParty(Party parentParty) {
        this.parentParty = parentParty;
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
     * @param contactMechPostalAddressContactMech the contactMechPostalAddressContactMech to set
    */
    public void setContactMechPostalAddressContactMech(ContactMech contactMechPostalAddressContactMech) {
        this.contactMechPostalAddressContactMech = contactMechPostalAddressContactMech;
    }
    /**
     * Auto generated value setter.
     * @param contactMechTelecomNumberContactMech the contactMechTelecomNumberContactMech to set
    */
    public void setContactMechTelecomNumberContactMech(ContactMech contactMechTelecomNumberContactMech) {
        this.contactMechTelecomNumberContactMech = contactMechTelecomNumberContactMech;
    }
    /**
     * Auto generated value setter.
     * @param contactMechEmailContactMech the contactMechEmailContactMech to set
    */
    public void setContactMechEmailContactMech(ContactMech contactMechEmailContactMech) {
        this.contactMechEmailContactMech = contactMechEmailContactMech;
    }


    /** {@inheritDoc} */
    @Override
    public void fromMap(Map<String, Object> mapValue) {
        preInit();
        setPartyId((String) mapValue.get("partyId"));
        setParentPartyId((String) mapValue.get("parentPartyId"));
        setDepartmentName((String) mapValue.get("departmentName"));
        setGeneralProfTitle((String) mapValue.get("generalProfTitle"));
        setCompanyName((String) mapValue.get("companyName"));
        setCompanyNameLocal((String) mapValue.get("companyNameLocal"));
        setAnnualRevenue(convertToBigDecimal(mapValue.get("annualRevenue")));
        setCurrencyUomId((String) mapValue.get("currencyUomId"));
        setNumberEmployees((Long) mapValue.get("numberEmployees"));
        setIndustryEnumId((String) mapValue.get("industryEnumId"));
        setOwnershipEnumId((String) mapValue.get("ownershipEnumId"));
        setSicCode((String) mapValue.get("sicCode"));
        setTickerSymbol((String) mapValue.get("tickerSymbol"));
        setImportantNote((String) mapValue.get("importantNote"));
        setPrimaryPostalAddressId((String) mapValue.get("primaryPostalAddressId"));
        setPrimaryTelecomNumberId((String) mapValue.get("primaryTelecomNumberId"));
        setPrimaryEmailId((String) mapValue.get("primaryEmailId"));
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
        mapValue.put("partyId", getPartyId());
        mapValue.put("parentPartyId", getParentPartyId());
        mapValue.put("departmentName", getDepartmentName());
        mapValue.put("generalProfTitle", getGeneralProfTitle());
        mapValue.put("companyName", getCompanyName());
        mapValue.put("companyNameLocal", getCompanyNameLocal());
        mapValue.put("annualRevenue", getAnnualRevenue());
        mapValue.put("currencyUomId", getCurrencyUomId());
        mapValue.put("numberEmployees", getNumberEmployees());
        mapValue.put("industryEnumId", getIndustryEnumId());
        mapValue.put("ownershipEnumId", getOwnershipEnumId());
        mapValue.put("sicCode", getSicCode());
        mapValue.put("tickerSymbol", getTickerSymbol());
        mapValue.put("importantNote", getImportantNote());
        mapValue.put("primaryPostalAddressId", getPrimaryPostalAddressId());
        mapValue.put("primaryTelecomNumberId", getPrimaryTelecomNumberId());
        mapValue.put("primaryEmailId", getPrimaryEmailId());
        mapValue.put("lastUpdatedStamp", getLastUpdatedStamp());
        mapValue.put("lastUpdatedTxStamp", getLastUpdatedTxStamp());
        mapValue.put("createdStamp", getCreatedStamp());
        mapValue.put("createdTxStamp", getCreatedTxStamp());
        return mapValue;
    }


}
