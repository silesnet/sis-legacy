<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ page language="Java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<Head>
<link href="${ctx}/css/bill.css" type="text/css" rel="stylesheet"/>
<meta http-equiv="content-language" content="cs">
<meta http-equiv="content-Type" content="text/html; charset=windows-1250">
<Title>Faktura SilesNet Polska Sp. z o.o.</TITLE>
</Head>
<Body BgColor="White">
<c:forEach items="${bills}" var="bill" varStatus="billStatus">
<CENTER>
<TABLE BORDER=0 WIDTH="100%" FRAME="VOID">
  <TR>
  <TD Align="Left" width="48%" valign="top">
  	<img src="${ctx}/img/logo_silesnet.gif" />
		<br />
		SilesNet Polska Sp. z o.o.<br />
		43-400 Cieszyn, ul. Motelowa 16<br />
		Tel.: 500 67 33 11, NIP: 896-138-01-08<br />
		Bank Zachodni WBK S.A. 1 O. w Cieszynie,<br />
		65109017820000000103270891<br /><br /><br />
  </TD>
  <td width="4%">&nbsp;</td>
  <TD Align="Right" width="48%" valign="top">
	<table width="70%">
		<tr><td align="center" bgcolor="#cccccc" style="border: 1px solid;">Miejsce wystawienia:</td></tr>
		<tr><td align="center">Cieszyn</td></tr>
		<tr><td align="center" bgcolor="#cccccc" style="border: 1px solid;">Data sprzedaży:</td></tr>
		<tr><td align="center"><fmt:formatDate value="${bill.billingDate}" pattern="dd.MM.yyyy" /></td></tr>
		<tr><td align="center" bgcolor="#cccccc" style="border: 1px solid;">Data wystawienia:</td></tr>
		<tr><td align="center"><fmt:formatDate value="${bill.billingDate}" pattern="dd.MM.yyyy" /></td></tr>
	</table>
  </TD>
  </TR>
    <TR>
  <TD Align="Left" width="48%" valign="top">
	<table width="100%">
		<tr><td align="center" bgcolor="#cccccc" style="border: 1px solid;">Sprzedawca:</td></tr>
		<tr><td>
			SilesNet Polska Sp. z o.o.<br />
			ul. Motelowa 16<br />
			43-400 Cieszyn<br />
			NIP: 896-138-01-08<br />
		</td></tr>
	</table>
  </TD>
  <td width="4%">&nbsp;</td>
  <TD Align="Right" width="48%" valign="top">
	<table width="100%">
		<tr><td align="center" bgcolor="#cccccc" style="border: 1px solid;">Nabywca:</td></tr>
		<tr><td>
			${bill.invoicedCustomer.name}<br />
			<c:if test="${!empty bill.invoicedCustomer.supplementaryName}">
            	${bill.invoicedCustomer.supplementaryName}<br />
			</c:if>
            ${bill.invoicedCustomer.contact.address.street}<br />
            ${bill.invoicedCustomer.contact.address.postalCode}&nbsp;${bill.invoicedCustomer.contact.address.city}<br />
            NIP:&nbsp;${bill.invoicedCustomer.DIC}
		</td></tr>
	</table>
  </TD>
  </TR>
  <tr>
  <td align="center" colspan="3">
  	<br />
  	<font size="5">Faktura VAT&nbsp;${bill.numberPL}&nbsp;oryginał</font>
  	<br /><br />
  	<table width="100%" border="1" cellspacing="0" cellpadding="0">
  		<tr bgcolor="#cccccc" align="center">
	  		<td>Lp</td>
	  		<td>Nazwa</td>
	  		<td>PKWiU</td>
	  		<td>Ilość</td>
	  		<td>j.m.</td>
	  		<td>Rabat [%]</td>
	  		<td>Cena netto</td>
	  		<td>VAT [%]</td>
	  		<td>Wartość netto</td>
	  		<td>VAT</td>
	  		<td>Wartość brutto</td>
  		</tr>
	<i18n:locale language="pl">
	<c:forEach items="${bill.items}" var="item" varStatus="status">
	<c:set var="amount"><i18n:formatNumber value="${item.amount}" pattern="0.0###" /></c:set>
		<TR align="center" valign="top">
			<td>${status.count}</td>
			<TD ALIGN="LEFT" NOWRAP>${item.text}<c:if test="${item.isDisplayUnit}"><br/><i>abonament&nbsp;${bill.period.periodString}</i></c:if></TD>
			<td>6120Z</td>
			<TD ALIGN="RIGHT" NOWRAP>${fn:replace(amount, ",", ".")}</TD>
			<TD NOWRAP><c:if test="${item.isDisplayUnit}">mies.</c:if></TD>
			<td ALIGN="RIGHT">0.00</td>
			<TD ALIGN="RIGHT" NOWRAP><app:currency value="${item.price}" /></TD>
			<TD ALIGN="RIGHT" NOWRAP>${item.bill.vat}</TD>
			<TD ALIGN="RIGHT" NOWRAP><app:currency value="${item.linePrice}" /></TD>
			<td ALIGN="RIGHT" NOWRAP><app:currency value="${item.lineVat}" /></td>
			<TD ALIGN="RIGHT" NOWRAP><app:currency value="${item.linePriceVat}" /> </TD>
		</TR>
	</c:forEach>
	</i18n:locale>
  	</table>
  	<br />
  	</td></tr>
	<tr><td align="center" colspan="3">
  	<table width="60%" align="right" border="1" cellspacing="0" cellpadding="0">
 		<tr bgcolor="#cccccc" align="center">
			<td>według stawki VAT</td>
			<td>wartość netto</td>
			<td>kwota VAT</td>
			<td>wartość brutto</td>
		</tr>
 		<tr align="center">
			<td ALIGN="LEFT" NOWRAP>Podstawowy podatek VAT&nbsp;${bill.vat}%</td>
			<td ALIGN="RIGHT" NOWRAP><app:currency value="${bill.totalPrice}" /></td>
			<td ALIGN="RIGHT" NOWRAP><app:currency value="${bill.billVat}" /></td>
			<td ALIGN="RIGHT" NOWRAP><app:currency value="${bill.totalPriceVatNotRounded}" /></td>
		</tr>
 		<tr align="center">
			<td ALIGN="RIGHT" NOWRAP>Razem:</td>
			<td ALIGN="RIGHT" NOWRAP><app:currency value="${bill.totalPrice}" /></td>
			<td ALIGN="RIGHT" NOWRAP><app:currency value="${bill.billVat}" /></td>
			<td ALIGN="RIGHT" NOWRAP><app:currency value="${bill.totalPriceVatNotRounded}" /></td>
		</tr>
  	</table>
  	<br /><br /><br />
  	</td></tr>
	<tr><td align="center" colspan="3">
  	<table width="50%" align="right">
 		<tr bgcolor="#cccccc">
			<td ALIGN="LEFT" NOWRAP><font size="3"><b>Razem do zapłaty:</b></font></td>
			<td ALIGN="RIGHT" NOWRAP><font size="3"><b><app:currency value="${bill.totalPriceVatNotRounded}" />&nbsp;PLN</b></font></td>
		</tr>
  	</table>
  </td></tr>
  <tr><td align="left" colspan="3">
  	<font size="2"><b>
  		Pozostało do zapłaty:&nbsp;<app:currency value="${bill.totalPriceVatNotRounded}" />&nbsp;PLN<br />
  		W terminie:&nbsp;<fmt:formatDate value="${bill.purgeDate}" pattern="dd.MM.yyyy" />
  	</b></font>
  	<p><b>
      Powyższa faktura dotyczy abonamentu za miesiąc w którym została wystawiona.
  	<br />
      W tytule płatności prosimy podać numer(y) faktury której płatność dotyczy.
    </b></p>
    <br />
  </td></tr>
  <tr>
  <TD Align="Left" width="48%" valign="top">
	<table width="80%">
		<tr><td align="center" bgcolor="#cccccc" style="border: 1px solid;">Wystawił(a):</td></tr>
		<tr><td align="center">Monika Mierva-Serwus</td></tr>
	</table>
  </TD>
  <td>&nbsp;</td>
  <TD Align="Right" width="48%" valign="top">
	<table width="80%">
		<tr><td align="center" bgcolor="#cccccc" style="border: 1px solid;">Odebrał(a):</td></tr>
		<tr><td align="center">&nbsp;<br /><br /><br /><br /></td></tr>
		<tr><td align="center"><font size="1">Podpis osoby upoważnionej<br />do odbioru faktury VAT</font></td></tr>
	</table>
  </TD>
  </tr>
</TABLE>
</CENTER>
<c:if test="${!billStatus.last}">
	<br style="page-break-after: always" />
</c:if>
</c:forEach>
</Body>
</HTML>
