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
 * Auto generated base entity ProductPriceChange.
 */
@javax.persistence.Entity
@Table(name="PRODUCT_PRICE_CHANGE")
public class ProductPriceChange extends Entity {
static {
java.util.Map<String, String> fields = new java.util.HashMap<String, String>();
        fields.put("productPriceChangeId", "PRODUCT_PRICE_CHANGE_ID");
        fields.put("productId", "PRODUCT_ID");
        fields.put("productPriceTypeId", "PRODUCT_PRICE_TYPE_ID");
        fields.put("productPricePurposeId", "PRODUCT_PRICE_PURPOSE_ID");
        fields.put("currencyUomId", "CURRENCY_UOM_ID");
        fields.put("productStoreGroupId", "PRODUCT_STORE_GROUP_ID");
        fields.put("fromDate", "FROM_DATE");
        fields.put("thruDate", "THRU_DATE");
        fields.put("price", "PRICE");
        fields.put("oldPrice", "OLD_PRICE");
        fields.put("changedDate", "CHANGED_DATE");
        fields.put("changedByUserLogin", "CHANGED_BY_USER_LOGIN");
        fields.put("lastUpdatedStamp", "LAST_UPDATED_STAMP");
        fields.put("lastUpdatedTxStamp", "LAST_UPDATED_TX_STAMP");
        fields.put("createdStamp", "CREATED_STAMP");
        fields.put("createdTxStamp", "CREATED_TX_STAMP");
fieldMapColumns.put("ProductPriceChange", fields);
}
  public static enum Fields implements EntityFieldInterface<ProductPriceChange> {
    productPriceChangeId("productPriceChangeId"),
    productId("productId"),
    productPriceTypeId("productPriceTypeId"),
    productPricePurposeId("productPricePurposeId"),
    currencyUomId("currencyUomId"),
    productStoreGroupId("productStoreGroupId"),
    fromDate("fromDate"),
    thruDate("thruDate"),
    price("price"),
    oldPrice("oldPrice"),
    changedDate("changedDate"),
    changedByUserLogin("changedByUserLogin"),
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

   @org.hibernate.annotations.GenericGenerator(name="ProductPriceChange_GEN",  strategy="org.opentaps.foundation.entity.hibernate.OpentapsIdentifierGenerator")
   @GeneratedValue(generator="ProductPriceChange_GEN")
   @Id
   @Column(name="PRODUCT_PRICE_CHANGE_ID")
   private String productPriceChangeId;
   @Column(name="PRODUCT_ID")
   private String productId;
   @Column(name="PRODUCT_PRICE_TYPE_ID")
   private String productPriceTypeId;
   @Column(name="PRODUCT_PRICE_PURPOSE_ID")
   private String productPricePurposeId;
   @Column(name="CURRENCY_UOM_ID")
   private String currencyUomId;
   @Column(name="PRODUCT_STORE_GROUP_ID")
   private String productStoreGroupId;
   @Column(name="FROM_DATE")
   private Timestamp fromDate;
   @Column(name="THRU_DATE")
   private Timestamp thruDate;
   @Column(name="PRICE")
   private BigDecimal price;
   @Column(name="OLD_PRICE")
   private BigDecimal oldPrice;
   @Column(name="CHANGED_DATE")
   private Timestamp changedDate;
   @Column(name="CHANGED_BY_USER_LOGIN")
   private String changedByUserLogin;
   @Column(name="LAST_UPDATED_STAMP")
   private Timestamp lastUpdatedStamp;
   @Column(name="LAST_UPDATED_TX_STAMP")
   private Timestamp lastUpdatedTxStamp;
   @Column(name="CREATED_STAMP")
   private Timestamp createdStamp;
   @Column(name="CREATED_TX_STAMP")
   private Timestamp createdTxStamp;
   private transient ProductPrice productPrice = null;
   @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
   @JoinColumn(name="CHANGED_BY_USER_LOGIN", insertable=false, updatable=false)
   @org.hibernate.annotations.Generated(
      org.hibernate.annotations.GenerationTime.ALWAYS
   )
   
   private UserLogin relatedChangedByUserLogin = null;

  /**
   * Default constructor.
   */
  public ProductPriceChange() {
      super();
      this.baseEntityName = "ProductPriceChange";
      this.isView = false;
      
      this.primaryKeyNames = new ArrayList<String>();
      this.primaryKeyNames.add("productPriceChangeId");
      this.allFieldsNames = new ArrayList<String>();
      this.allFieldsNames.add("productPriceChangeId");this.allFieldsNames.add("productId");this.allFieldsNames.add("productPriceTypeId");this.allFieldsNames.add("productPricePurposeId");this.allFieldsNames.add("currencyUomId");this.allFieldsNames.add("productStoreGroupId");this.allFieldsNames.add("fromDate");this.allFieldsNames.add("thruDate");this.allFieldsNames.add("price");this.allFieldsNames.add("oldPrice");this.allFieldsNames.add("changedDate");this.allFieldsNames.add("changedByUserLogin");this.allFieldsNames.add("lastUpdatedStamp");this.allFieldsNames.add("lastUpdatedTxStamp");this.allFieldsNames.add("createdStamp");this.allFieldsNames.add("createdTxStamp");
      this.nonPrimaryKeyNames = new ArrayList<String>();
      this.nonPrimaryKeyNames.addAll(allFieldsNames);
      this.nonPrimaryKeyNames.removeAll(primaryKeyNames);
  }

  /**
   * Constructor with a repository.
   * @param repository a <code>RepositoryInterface</code> value
   */
  public ProductPriceChange(RepositoryInterface repository) {
      this();
      initRepository(repository);
  }

    /**
     * Auto generated value setter.
     * @param productPriceChangeId the productPriceChangeId to set
     */
    public void setProductPriceChangeId(String productPriceChangeId) {
        this.productPriceChangeId = productPriceChangeId;
    }
    /**
     * Auto generated value setter.
     * @param productId the productId to set
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }
    /**
     * Auto generated value setter.
     * @param productPriceTypeId the productPriceTypeId to set
     */
    public void setProductPriceTypeId(String productPriceTypeId) {
        this.productPriceTypeId = productPriceTypeId;
    }
    /**
     * Auto generated value setter.
     * @param productPricePurposeId the productPricePurposeId to set
     */
    public void setProductPricePurposeId(String productPricePurposeId) {
        this.productPricePurposeId = productPricePurposeId;
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
     * @param productStoreGroupId the productStoreGroupId to set
     */
    public void setProductStoreGroupId(String productStoreGroupId) {
        this.productStoreGroupId = productStoreGroupId;
    }
    /**
     * Auto generated value setter.
     * @param fromDate the fromDate to set
     */
    public void setFromDate(Timestamp fromDate) {
        this.fromDate = fromDate;
    }
    /**
     * Auto generated value setter.
     * @param thruDate the thruDate to set
     */
    public void setThruDate(Timestamp thruDate) {
        this.thruDate = thruDate;
    }
    /**
     * Auto generated value setter.
     * @param price the price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    /**
     * Auto generated value setter.
     * @param oldPrice the oldPrice to set
     */
    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }
    /**
     * Auto generated value setter.
     * @param changedDate the changedDate to set
     */
    public void setChangedDate(Timestamp changedDate) {
        this.changedDate = changedDate;
    }
    /**
     * Auto generated value setter.
     * @param changedByUserLogin the changedByUserLogin to set
     */
    public void setChangedByUserLogin(String changedByUserLogin) {
        this.changedByUserLogin = changedByUserLogin;
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
    public String getProductPriceChangeId() {
        return this.productPriceChangeId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getProductId() {
        return this.productId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getProductPriceTypeId() {
        return this.productPriceTypeId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getProductPricePurposeId() {
        return this.productPricePurposeId;
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
    public String getProductStoreGroupId() {
        return this.productStoreGroupId;
    }
    /**
     * Auto generated value accessor.
     * @return <code>Timestamp</code>
     */
    public Timestamp getFromDate() {
        return this.fromDate;
    }
    /**
     * Auto generated value accessor.
     * @return <code>Timestamp</code>
     */
    public Timestamp getThruDate() {
        return this.thruDate;
    }
    /**
     * Auto generated value accessor.
     * @return <code>BigDecimal</code>
     */
    public BigDecimal getPrice() {
        return this.price;
    }
    /**
     * Auto generated value accessor.
     * @return <code>BigDecimal</code>
     */
    public BigDecimal getOldPrice() {
        return this.oldPrice;
    }
    /**
     * Auto generated value accessor.
     * @return <code>Timestamp</code>
     */
    public Timestamp getChangedDate() {
        return this.changedDate;
    }
    /**
     * Auto generated value accessor.
     * @return <code>String</code>
     */
    public String getChangedByUserLogin() {
        return this.changedByUserLogin;
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
     * Auto generated method that gets the related <code>ProductPrice</code> by the relation named <code>ProductPrice</code>.
     * @return the <code>ProductPrice</code>
     * @throws RepositoryException if an error occurs
     */
    public ProductPrice getProductPrice() throws RepositoryException {
        if (this.productPrice == null) {
            this.productPrice = getRelatedOne(ProductPrice.class, "ProductPrice");
        }
        return this.productPrice;
    }
    /**
     * Auto generated method that gets the related <code>UserLogin</code> by the relation named <code>ChangedByUserLogin</code>.
     * @return the <code>UserLogin</code>
     * @throws RepositoryException if an error occurs
     */
    public UserLogin getRelatedChangedByUserLogin() throws RepositoryException {
        if (this.relatedChangedByUserLogin == null) {
            this.relatedChangedByUserLogin = getRelatedOne(UserLogin.class, "ChangedByUserLogin");
        }
        return this.relatedChangedByUserLogin;
    }

    /**
     * Auto generated value setter.
     * @param productPrice the productPrice to set
    */
    public void setProductPrice(ProductPrice productPrice) {
        this.productPrice = productPrice;
    }
    /**
     * Auto generated value setter.
     * @param relatedChangedByUserLogin the relatedChangedByUserLogin to set
    */
    public void setRelatedChangedByUserLogin(UserLogin relatedChangedByUserLogin) {
        this.relatedChangedByUserLogin = relatedChangedByUserLogin;
    }


    /** {@inheritDoc} */
    @Override
    public void fromMap(Map<String, Object> mapValue) {
        preInit();
        setProductPriceChangeId((String) mapValue.get("productPriceChangeId"));
        setProductId((String) mapValue.get("productId"));
        setProductPriceTypeId((String) mapValue.get("productPriceTypeId"));
        setProductPricePurposeId((String) mapValue.get("productPricePurposeId"));
        setCurrencyUomId((String) mapValue.get("currencyUomId"));
        setProductStoreGroupId((String) mapValue.get("productStoreGroupId"));
        setFromDate((Timestamp) mapValue.get("fromDate"));
        setThruDate((Timestamp) mapValue.get("thruDate"));
        setPrice(convertToBigDecimal(mapValue.get("price")));
        setOldPrice(convertToBigDecimal(mapValue.get("oldPrice")));
        setChangedDate((Timestamp) mapValue.get("changedDate"));
        setChangedByUserLogin((String) mapValue.get("changedByUserLogin"));
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
        mapValue.put("productPriceChangeId", getProductPriceChangeId());
        mapValue.put("productId", getProductId());
        mapValue.put("productPriceTypeId", getProductPriceTypeId());
        mapValue.put("productPricePurposeId", getProductPricePurposeId());
        mapValue.put("currencyUomId", getCurrencyUomId());
        mapValue.put("productStoreGroupId", getProductStoreGroupId());
        mapValue.put("fromDate", getFromDate());
        mapValue.put("thruDate", getThruDate());
        mapValue.put("price", getPrice());
        mapValue.put("oldPrice", getOldPrice());
        mapValue.put("changedDate", getChangedDate());
        mapValue.put("changedByUserLogin", getChangedByUserLogin());
        mapValue.put("lastUpdatedStamp", getLastUpdatedStamp());
        mapValue.put("lastUpdatedTxStamp", getLastUpdatedTxStamp());
        mapValue.put("createdStamp", getCreatedStamp());
        mapValue.put("createdTxStamp", getCreatedTxStamp());
        return mapValue;
    }


}