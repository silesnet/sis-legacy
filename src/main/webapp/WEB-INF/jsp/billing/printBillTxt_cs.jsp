<%@ include file="/WEB-INF/jsp/inc/include.jsp" %>
<%@ page language="Java" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
<Head>
<link href="${ctx}/css/bill.css" type="text/css" rel="stylesheet"/>
<meta http-equiv="content-language" content="cs">
<meta http-equiv="content-Type" content="text/html; charset=windows-1250">
<Title>Doklad od dodavatele IČ:25392280 SilesNet s.r.o.</TITLE>
<script type="text/javascript" src="${ctx}/js/qrcodegen.js"></script>
</Head>
<Body BgColor="White">
<c:forEach items="${bills}" var="bill" varStatus="billStatus">
<CENTER>
<TABLE BORDER=0 WIDTH="100%" FRAME="VOID">
  <TR>
  <TH Align="Left"><FONT Size=4>Daňový doklad</FONT></TH>
  <TH Align="Right"><FONT Size=4>FAKTURA</FONT></TH>
  </TR>
</TABLE>
<TABLE BORDER=1 WIDTH="100%" FRAME="BORDER">
  <TR><TD>
  <TABLE BORDER=0 WIDTH="100%" FRAME="VOID">
    <TR>
      <TD WIDTH="67%" VAlign="Middle">
        <img src="${ctx}/img/logo_silesnet.gif" />
      </TD>
      <TD WIDTH="33%">
        <TABLE BORDER=1 WIDTH="100%" FRAME="BORDER">
          <TR><TD>
            <TABLE BORDER=0 WIDTH="100%" FRAME="BORDER" BGCOLOR="Yellow">
              <TR>
                <TD Align="Left">Číslo dokladu</TD>
                <TH Align="Right">${bill.number}</TH>
              </TR>
            </TABLE>
            <TABLE BORDER=0 WIDTH="100%" FRAME="VOID">
              <TR>
                <TD Align="Left">Variabilní symbol</TD>
                <TD Align="Right">${bill.number}</TD>
              </TR>
            </TABLE>
            <TABLE BORDER=0 WIDTH="100%" FRAME="VOID">
              <TR>
                <TD Align="Left">Konstantní symbol</TD>
                <TD Align="Right">0308</TD>
              </TR>
            </TABLE>
          </TD></TR>
        </TABLE>
      </TD>
    </TR>
  </TABLE>
  </TD></TR>
  <TR><TD>
  <TABLE BORDER=0 WIDTH="100%" FRAME="VOID">
    <TR>
      <TD WIDTH="45%">
        <TABLE BORDER=0 WIDTH="100%" FRAME="VOID">
          <TR>
            <TD Align="Left" ColSpan=2><FONT SIZE=2>Dodavatel</FONT></TD>
            <TD Align="Left"><FONT SIZE=2><B>SilesNet s.r.o.</B></FONT></TD>
          </TR>
          <TR>
            <TD></TD>
            <TD></TD>
            <TD Align="Left"><FONT SIZE=2>Ostravská 584/12</FONT></TD>
          </TR>
          <TR>
            <TD Align="Right"><FONT SIZE=2>737 01</FONT></TD>
            <TD></TD>
            <TD Align="Left"><FONT SIZE=2>Český Těšín</FONT></TD>
          </TR>
          <TR>
            <TD COLSPAN=3>
              <TABLE BORDER=0 WIDTH="100%">
                <TR>
                  <TD WIDTH="50%" Align="Left">
                    <B>IČ: </B>25392280
                  </TD>
                  <TD WIDTH="50%" Align="Right">
                    <B>DIČ: </B>CZ25392280
                  </TD>
                </TR>
              </TABLE>
            </TD>
          </TR>
        </TABLE>
      </TD>
      <TD WIDTH="55%">
        <TABLE BORDER=1 WIDTH="100%" FRAME="BORDER" BgColor="#F0F0F0">
          <TR>
            <TD Align="Center" VAlign="Middle">
              <TABLE BORDER=0 WIDTH="100%" FRAME="VOID">
                <TR>
                  <TD Align="Left" ColSpan=3><FONT SIZE=2>Odběratel</FONT></TD>
                </TR>
                <TR>
                  <TD Align="Left" ColSpan=3><HR></TD>
                </TR>
                <TR>
                  <TD></TD><TD></TD>
                  <TD Align="Left"><B>${bill.invoicedCustomer.name}</B></TD>
                </TR>
<c:if test="${!empty bill.invoicedCustomer.supplementaryName}">
                <TR>
                  <TD></TD><TD></TD>
                  <TD Align="Left"><B>${bill.invoicedCustomer.supplementaryName}</B></TD>
                </TR>
</c:if>                
                <TR>
                  <TD></TD><TD></TD>
                  <TD Align="Left"><B></B></TD>
                </TR>
                <TR>
                  <TD></TD><TD></TD>
                  <TD Align="Left">${bill.invoicedCustomer.contact.address.street}</TD>
                </TR>
                <TR>
                  <TD Align="Right">${bill.invoicedCustomer.contact.address.postalCode}</TD><TD></TD>
                  <TD Align="Left">${bill.invoicedCustomer.contact.address.city}</TD>
                </TR>
                <TR>
                  <TD Align="Left" ColSpan=3><HR></TD>
                </TR>
                <TR>
                  <TD Align="Left" ColSpan=3>
                    <TABLE Width="100%" Border=0>
                      <TR>
                        <TD Width="50%" Align="Left">
                          <B>IČ: </B>${bill.invoicedCustomer.exportPublicId}
                        </TD>
                        <TD Width="50%" Align="Right">
                          <B>DIČ: </B>${bill.invoicedCustomer.DIC}
                        </TD>
                      </TR>
                    </TABLE>
                  </TD>
                </TR>
              </TABLE>
            </TD>
          </TR>
        </TABLE>
      </TD>
    </TR>
  </TABLE>
  </TD></TR>
</TABLE>
  <TABLE BORDER=0 WIDTH="100%">
    <TR>
      <TD Width="60%">
        <TABLE BORDER=1 WIDTH="100%" BgColor="Yellow">
          <TR><TD>
          <FONT SIZE=3>Na účet: <B>75583001/5500</B></FONT>
          </TD></TR>
        </TABLE>
        <TABLE BORDER=0 WIDTH="100%">
          <TR><TD>
IBAN: CZ9755000000000075583001
          </TD></TR>
          <TR><TD>
Společnost je zapsána v obch. rejstříku
ved. u Kraj. soudu v Ostravě v oddílu C, vložce číslo 17812
          </TD></TR>
        </TABLE>
      </TD>
      <TD Width="40%">
        <TABLE BORDER=0 WIDTH="100%">
          <TR>
            <TD>
              <FONT SIZE=3>Datum splatnosti</FONT>
            </TD>
            <TD>
              <TABLE BORDER=1 WIDTH="100%" BgColor="Yellow">
                <TR><TD Align="Right"><FONT SIZE=3><fmt:formatDate value="${bill.purgeDate}" pattern="dd.MM.yyyy" /></FONT></TD></TR>
              </TABLE>
            </TD>
          </TR>
          <TR>
            <TD>Způsob úhrady</TD>
            <TD><B>
                Převodním příkazem
            </B></TD>
          </TR>
          <TR>
            <TD>Datum vystavení</TD>
            <TD Align="Right"><fmt:formatDate value="${bill.billingDate}" pattern="dd.MM.yyyy" /></TD>
          </TR>
          <TR>
            <TD NOWRAP>Datum uskutečnění plnění</TD>
            <TD Align="Right"><fmt:formatDate value="${bill.billingDate}" pattern="dd.MM.yyyy" /></TD>
          </TR>
        </TABLE>
      </TD>
    </TR>
  </TABLE>
<BR><BR><TABLE BORDER=0 WIDTH="100%" FRAME="VOID" BGColor="#F0F0F0">
<TR><TH ALIGN="LEFT" NOWRAP>  Popis položky  </TH><TH ALIGN="RIGHT" NOWRAP>  Cena za MJ  </TH><TH ALIGN="RIGHT" NOWRAP>    </TH><TH ALIGN="LEFT" NOWRAP>    </TH><TH ALIGN="RIGHT" NOWRAP>  Celkem  </TH><TH ALIGN="RIGHT" NOWRAP>  DPH  </TH><TH ALIGN="RIGHT" NOWRAP>  Celkem  </TH></TR>
<TR><TH ALIGN="LEFT" NOWRAP>    </TH><TH ALIGN="RIGHT" NOWRAP>  bez DPH  </TH><TH ALIGN="RIGHT" NOWRAP>  Množství  </TH><TH ALIGN="LEFT" NOWRAP>  MJ  </TH><TH ALIGN="RIGHT" NOWRAP>  bez DPH  </TH><TH ALIGN="RIGHT" NOWRAP>  %  </TH><TH ALIGN="RIGHT" NOWRAP>  s DPH  </TH></TR>
<TR><TD ALIGN="CENTER" COLSPAN=7 ><HR></TD></TR>
<TR><TD ALIGN="LEFT" COLSPAN=7 NOWRAP>  Na základě smlouvy Vám fakturujeme poskytování   </TD></TR>
<TR><TD ALIGN="LEFT" COLSPAN=7 NOWRAP>  služby za období&nbsp;${bill.period.periodString}:  </TD></TR>
<TR><TD ALIGN="LEFT" COLSPAN=7 NOWRAP>    </TD></TR>
<i18n:locale language="cs">
<c:forEach items="${bill.items}" var="item">
<c:set var="amount"><i18n:formatNumber value="${item.amount}" pattern="0.0###" /></c:set>
<TR><TD ALIGN="LEFT" NOWRAP>  <c:if test="${item.isDisplayUnit}">Služba -&nbsp;</c:if>${item.text}  </TD><TD ALIGN="RIGHT" NOWRAP>  <app:currency value="${item.price}" />  </TD><TD ALIGN="RIGHT" NOWRAP>  ${fn:replace(amount, ",", ".")}  </TD><TD ALIGN="LEFT" NOWRAP>  <c:if test="${item.isDisplayUnit}">měs.</c:if>  </TD><TD ALIGN="RIGHT" NOWRAP>  <app:currency value="${item.net}" />  </TD><TD ALIGN="RIGHT" NOWRAP>  ${item.bill.vat}  </TD><TD ALIGN="RIGHT" NOWRAP>  <app:currency value="${item.brt}" />  </TD></TR>
</c:forEach>
</i18n:locale>
<TR><TD COLSPAN=7><BR></TD></TR>
<TR><TD Align="Right" ColSpan=6>Celkem bez DPH </TD><TD Align="Right"><app:currency value="${bill.net}" /></TD></TR>
<TR><TD Align="Right" ColSpan=6>Bez DPH po zaokrouhlení </TD><TD Align="Right"><app:currency value="${bill.netRounded}" /></TD></TR>
<TR><TD Align="Right" ColSpan=6>DPH </TD><TD Align="Right"><app:currency value="${bill.vatRounded}" /></TD></TR>
<TR><TD Align="Right" ColSpan=6>Celkem s DPH </TD><TD Align="Right"><app:currency value="${bill.brt}" /></TD></TR>
<TR><TD COLSPAN=7><BR></TD></TR>
<TR>
  <TD ALIGN="RIGHT" ColSpan=5 VAlign="Middle">
    <TABLE Width="100%" Border=0 CellPadding=4>
      <TR>
        <TH Align="Right">CELKEM K PROPLACENÍ </TH>
      </TR>
    </TABLE><BR>
  </TD>
  <TD ALIGN="Center" ColSpan=2>
    <TABLE Width="100%" Border=1 CellPadding=4>
      <TR>
        <TH NoWrap Align="Right" VAlign="Middle" BgColor="Yellow"><FONT SIZE=4><app:currency value="${bill.brt}" /> Kč</FONT></TH>
      </TR>
    </TABLE><BR>
  </TD>
</TR>
</Table>
</CENTER>
<DIV Align="Left">
Za opožděnou platbu výše uvedené částky je odběratel povinen uhradit smluvní pokutu ve výši 0.05% z dlužné částky za každý den prodlení.<BR>
<BR><CENTER>
<TABLE BORDER=0 WIDTH="100%" FRAME="VOID">
<TR>
    <TD>
      <canvas id='qrcode-canvas'></canvas>
    </TD>
    <TD Width="30%">
         <TABLE BORDER=1 BgColor="#F0F0F0">
           <TR>
             <TH ALIGN="Center">Sazba DPH</TH><TH ALIGN="Center">Základ (v Kč)</TH><TH ALIGN="Center">DPH (v Kč)</TH>
           </TR>
           <TR>
             <TH ALIGN="Center">${bill.vat}%</TH><TD ALIGN="Right"><app:currency value="${bill.netRounded}" /></TD><TD ALIGN="Right"><app:currency value="${bill.vatRounded}" /></TD>
           </TR>
         </TABLE>
    </TD>
    <TD Width="25%">
      <TABLE BORDER=0>
        <TR><TH ALIGN="Left">Vystavil:</TH><TD>Alžběta Kolberová</TD><TR>
        <TR><TH ALIGN="Left">Telefon:</TH><TD>558 711 585</TD><TR>
        <TR><TH ALIGN="Left">eMail:</TH><TD>kolberova@silesnet.cz</TD><TR>
      </TABLE>
    </TD>
    <td width="25%">
        <img src="${ctx}/img/bill_signature.gif" />
    </td>
</TR>
</Table><BR>
</CENTER>
</DIV>
<c:if test="${!billStatus.last}">
	<br style="page-break-after: always" />
</c:if>
</c:forEach>
<script type="text/javascript">
  var qrCode = "SPD*1.0*ACC:CZ9755000000000075583001+RZBCCZPP*AM:${bill.brt}*CC:CZK*MSG:SilesNet faktura ${bill.number}*X-INV:${bill.number}*X-KS:0308*X-VS:${bill.number}";
  var QRC = qrcodegen.QrCode;
  var qr = QRC.encodeText(qrCode, QRC.Ecc.MEDIUM);
  var canvas = document.getElementById('qrcode-canvas');
  qr.drawCanvas(2, 2, canvas);
</script>
</Body>
</HTML>