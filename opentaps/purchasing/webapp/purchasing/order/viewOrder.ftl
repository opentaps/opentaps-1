<#--
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
-->

<#-- 
Instead of using a screen to do the layout, we use an ftl.  
This is because doing multicolumn layout in CSS as OFBiz does is really
difficult (CSS is weak in this respect).

This may also be the platform for refactoring order header. 

Notice how we replaced the screens.render with #include directives.  These subscreens do not have
any special data that is not set up in orderview.bsh, so the screen render call is redundant.
If special data is needed, either add a new bsh to run after orderview.bsh or include in orderview.bsh.
The whole idea is to keep things simple and not use the screen widget where unnecessary.
-->

<#if orderHeader?has_content && orderHeader.orderTypeId == "PURCHASE_ORDER">
  <#if !hasPermission>
    <p class="tableheadtext">${uiLabelMap.OpentapsError_PermissionDenied}</p>
  <#else>
      
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="50%" valign="top">
          <@include location="component://opentaps-common/webapp/common/order/orderInfo.ftl"/>
          <@include location="component://opentaps-common/webapp/common/order/orderTerms.ftl"/>
        </td>
        <td width="10" nowrap="nowrap">&nbsp;</td>
        <td width="50%" valign="top">
          <@include location="component://purchasing/webapp/purchasing/order/orderview/orderContactInfo.ftl"/>
        </td>
      </tr>
    </table>

    <#include "orderview/orderitems.ftl"/>
    <#include "orderview/ordernotes.ftl"/>
    <#include "orderview/transitions.ftl"/>
    <!-- Uncomment to use Crm2 application
    <@include location="component://crm2/webapp/crm2/activities/activitiesWidget.ftl"/>
    -->
  </#if>
<#else>
  <p class="tableheadtext">${uiLabelMap.CrmOrderNotFound}</p>
</#if>
