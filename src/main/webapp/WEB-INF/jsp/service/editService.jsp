<%@ include file="/WEB-INF/jsp/inc/header.jsp" %>

<%@ include file="/WEB-INF/jsp/inc/message.jsp" %>

<h1><fmt:message key="editService.title"/></h1>

<form name="editService" method="POST" action="${ctx}/service/edit.html">
<s:nestedPath path="service">

<%-- IMPORTANT! sent object id with post 2 proper retrieve backing object. --%>
<c:if test="${!isNew}">
	<input id="serviceId" type="hidden" name="serviceId" value="${service.id}" />
</c:if>
<%-- IMPORTANT! need 2 bind pre set values that are not edited directly,
  -- applies only to NEW command objects, on existing objects,
  -- unedited values are set on binding from persistent srorage. --%>
<c:if test="${isNew}">
	<html:input type="hidden" path="customerId" />
</c:if>

<fmt:message key="${money_label}" var="money_label_str" />

<table>
<tr><td>

<h3><fmt:message key="editService.header.Parameters" /></h3>
<table class="editForm">
  <%-- Name --%>
  <tr>
    <td>
      <span class="valueRequired">*&nbsp;</span>
      <fmt:message key="Service.fName" />&nbsp;
    </td>
    <td>
      <input id="currency" value="${money_label_str}" type="hidden" />
      <html:input path="name" id="serviceName" type="hidden" />
      <html:input path="productId" id="productId" type="hidden" />
      <select id="products">
        <c:forEach items="${products}" var="p">
          <option
            value="${p.id}"
            data-name="${p.name}"
            data-price="${p.price}"
            data-channel="${p.channel}"
            data-can-change-price="${p.canChangePrice}"
            data-country="${p.country}"
          >${p.name}<c:if test="${! p.canChangePrice}"> (${p.price} ${money_label_str})</c:if></option>
        </c:forEach>
      </select>
    </td>
  </tr>
  <%-- AdditionalName --%>
  <app:formInputLine path="additionalName" label="Service.additionalName" size="20" />
  <%-- Frequency --%>
  <app:formEnumLine path="frequency" label="Service.fFrequency" enums="${serviceFrequency}" required="true" />
  <%-- Period from --%>
  <app:formDateLine path="period.from" label="Service.fPeriod.fFrom" required="true" size="10" />
  <%-- Period to --%>
  <app:formDateLine path="period.to" label="Service.fPeriod.fTo" size="10" />
  <%-- Price --%>
  <app:formInputLine path="price" label="Service.fPrice" required="true" size="7" valueType="${money_label_str}" />
  <app:formCheckboxLine path="includeDph" label="Service.fIncludeDph" required="true" />
  <%-- Info --%>
  <app:formTextareaLine path="info" label="Service.fInfo" cols="20" rows="3" />
</table>

</td><td>&nbsp;&nbsp;</td><td>

</td></tr></table>


<%@ include file="/WEB-INF/jsp/inc/sidebar.jsp" %>


<script type="text/javascript">
  var byId = document.getElementById.bind(document);
  var query = document.querySelector.bind(document);

  function productFromOption(option) {
    return {
      id: option.value,
      name: option.getAttribute('data-name'),
      channel: option.getAttribute('data-channel'),
      price: option.getAttribute('data-price'),
      canChangePrice: option.getAttribute('data-can-change-price') === 'true',
      country: option.getAttribute('data-country')
    };
  }

  function selectedProduct() {
    var products = byId('products');
    if (products.selectedIndex == -1) {
      return {};
    }
    return productFromOption(products[products.selectedIndex]);
  }

  function priceInput() {
    return query('input[name="price"]');
  }

  function highlightInputWhenNonStandardProduct(input, productId) {
    input.style['background-color'] = productId === '-1' ? 'red' : 'white';
  }

  function isOneTimeService() {
    return query('select[name="frequency"]').value === '10';
  }

  function isRegularService() {
    return !isOneTimeService();
  }

  function selectServiceProduct() {
    var found = -1;
    var serviceName = byId('serviceName').value;
    var servicePrice = priceInput().value;
    var products = byId('products');
    for (var index = 0; index < products.length; index++) {
      var option = products.options[index];
      var product = productFromOption(option);
      if (serviceName === product.name) {
        if (product.canChangePrice || servicePrice === product.price) {
          products.value = product.id;
          found = index;
        }
        break;
      }
    }
    if (found === -1) {
      if (isOneTimeService()) {
        products.selectedIndex = 0;
      }
      else {
        var extraOption = document.createElement('option');
        extraOption.value = -1;
        extraOption.innerHTML = serviceName +
          ' (' + servicePrice + ' ' + byId('currency').value +')';
        extraOption.setAttribute('data-name', serviceName);
        extraOption.setAttribute('data-price', servicePrice);
        extraOption.setAttribute('data-channel', '');
        extraOption.setAttribute('data-can-change-price', false);
        extraOption.style['background-color'] = 'red';
        products.appendChild(extraOption);
        products.value = -1;
        highlightInputWhenNonStandardProduct(products, '-1');
      }
    }
    q.pub('productUpdated', selectedProduct());
  }

  q.on('productUpdated', byId('productId'), e => {
    e.target.value = e.detail.id;
  });

  q.on('productUpdated', byId('serviceName'), e => {
    e.target.value = e.detail.name;
  });

  q.on('productUpdated', priceInput(), e => {
    var input = e.target;
    var product = e.detail;
    input.value = product.canChangePrice ? input.defaultValue : product.price;
    input.readOnly = !product.canChangePrice;
    input.style['color'] = product.canChangePrice ? 'black' : 'gray';
    highlightInputWhenNonStandardProduct(input, product.id);
  });

  selectServiceProduct();

  byId('products').onchange = e => {
    var product = selectedProduct();
    highlightInputWhenNonStandardProduct(e.target, product.id);
    q.pub('productUpdated', product);
  }

  function validatePrice(price, product) {
    var p = Number(price);
    var errors = [];
    if (p < 0 && isRegularService()) {
      errors.push('<fmt:message key="editService.negativePriceError"/>');
    }
    if (p % 1 != 0) {
      errors.push('<fmt:message key="editService.fractionPriceError"/>');
    }
    if (errors.length > 0) {
      return errors;
    }
    if (isRegularService()) {
      if (product.canChangePrice) {
        if (1 < p && p < ${dedicatedThreshold}) {
          errors.push('<fmt:message key="editService.priceNotInRangeError"><fmt:param value="${dedicatedThreshold}"/><fmt:param value="${serviceCurrency}"/></fmt:message>');
        }
      }
      else {
        if (price !== product.price) {
          errors.push('<fmt:message key="editService.priceNotInSyncWithProduct"/>');
        }
      }
    }
    return errors;
  }

  priceInput().onchange = e => {
    var price = e.target.value;
    var product = selectedProduct();
    var errors = validatePrice(price, product);
    if (errors.length > 0) {
      alert(errors);
      e.target.value = 0;
    }
  }
</script>

<%-- Form submit buttons --%>
<app:formActionButtons confirmDeleteMsg="editService.confirmDelete" />
</s:nestedPath>
</form>

<%@ include file="/WEB-INF/jsp/inc/footer.jsp" %>
