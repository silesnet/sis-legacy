package cz.silesnet.web.mvc;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import cz.silesnet.model.Bill;
import cz.silesnet.model.Customer;
import cz.silesnet.model.Invoicing;
import cz.silesnet.model.enums.Country;
import cz.silesnet.service.BillingManager;
import cz.silesnet.service.HistoryManager;
import cz.silesnet.utils.FilterUtils;
import cz.silesnet.utils.MessagesUtils;
import cz.silesnet.utils.NavigationUtils;

/**
 * Controller for all billing stuff.
 * 
 * @author Richard Sikora
 */
public class BillingController extends MultiActionController {

	protected final Log log = LogFactory.getLog(getClass());

	private BillingManager bMgr;

	private HistoryManager hmgr;

	public void setBillingManager(BillingManager billingManager) {
		bMgr = billingManager;
	}

	public void setHistoryManager(HistoryManager historyManager) {
		hmgr = historyManager;
	}

	public ModelAndView mainBilling(HttpServletRequest request,
			HttpServletResponse response) {
		// show main billing page with some info and actions available
		log.debug("Main billing.");
		Map<String, Object> model = new HashMap<String, Object>();
		// get requested invoicing, can be null
		Invoicing invoicing = null;
		try {
			invoicing = getRequestedInvoicing(request);
		}
		catch (NullPointerException e) {
			// no real invocing required, never mind we can handle this
		}
		Country country = null;
		if (invoicing == null) {
			// get requested country if no invoicing requested, default to CZ
			country = getRequestedCountry(request);
		}
		else {
			// set country from requested invoicing
			country = invoicing.getCountry();
		} // country is determined now
		// get all invoicings for specified country
		List<Invoicing> invoicings = bMgr.getInvoicings(country);
		// when no invoicing requested then try to set default one (first from
		// the list)
		if (invoicing == null && invoicings.size() > 0)
			invoicing = invoicings.get(0);
		// put invoicing related objects into model
		model.put("country", country.getShortName());
		model.put("invoicing", invoicing);
		model.put("invoicings", invoicings);

		// calculate next possible billing date
		// the first of next month
		Calendar cal = new GregorianCalendar();
		// cal.setFirstDayOfWeek(Calendar.MONDAY);
		// cal.add(Calendar.MONTH, 1);
		// cal.set(Calendar.DAY_OF_MONTH, 1);
		// if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
		// cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ) {
		// // !!! use of add() is critical here, if used roll() need then call
		// getTime()
		// // so date fields get computed before setting it to monday!!!
		// cal.add(Calendar.WEEK_OF_MONTH, 1);
		// cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		// }
		// cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		model.put("billingDate", cal.getTime());
		model.put("billingMonth", ((new DecimalFormat("00")).format(cal
				.get(Calendar.MONTH) + 1)));
		model.put("scripts", new String[] { "calendar.js" });

		// add Invoice sendign status string
		model.put("countUnconfirmed", bMgr.getCountByStatus(invoicing, false,
				null, null, null, null));
		model.put("countConfirmed", bMgr.getCountByStatus(invoicing, true,
				false, false, null, null));
		model.put("countUndelivered", bMgr.getCountByStatus(invoicing, true,
				true, false, null, null));
		model.put("countDelivered", bMgr.getCountByStatus(invoicing, true,
				true, true, null, null));
		model.put("countSnail", bMgr.getCountByStatus(invoicing, true, null,
				null, null, true));
		model.put("countSent", bMgr.getCountByStatus(invoicing, true, true,
				null, null, null));
		model.put("countToSend", bMgr.getCountByStatus(invoicing, true, false,
				false, null, null));
		model.put("countAll", bMgr.getCountByStatus(invoicing, true, null,
				null, null, null));
		model.put("sumAll", bMgr.getInvoicingSum(invoicing));
		model.put("invoiceSendingEnabled", bMgr.getSendingEnabled(country));
		if (!AbstractCRUDController.isTablePagination(request)
				&& invoicing != null)
			request.getSession().setAttribute("billingAudit",
					hmgr.getHistory(invoicing));
		return new ModelAndView("billing/mainBilling", model);
	}

	public ModelAndView confirmBill(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Confirming selected bills.");
		List<Bill> bills = getSelectedBills(request);
		for (Bill bill : bills) {
			bill.setIsConfirmed(true);
			bMgr.update(bill);
		}
		MessagesUtils.setCodedSuccessMessage(request,
				"listBills.confirm.success");
		return goBack(request, response);
	}

	public ModelAndView unconfirmBill(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Unconfirming selected bills.");
		List<Bill> bills = getSelectedBills(request);
		for (Bill bill : bills) {
			bill.setIsConfirmed(false);
			bMgr.update(bill);
		}
		MessagesUtils.setCodedSuccessMessage(request,
				"listBills.unconfirm.success");
		return goBack(request, response);
	}

	public ModelAndView deliverBill(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Delivering selected bills.");
		List<Bill> bills = getSelectedBills(request);
		for (Bill bill : bills) {
			bill.setIsSent(true);
			bill.setIsDelivered(true);
			bMgr.update(bill);
		}
		MessagesUtils.setCodedSuccessMessage(request,
				"listBills.deliver.success");
		return goBack(request, response);
	}

	public ModelAndView confirmDelivery(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Confirming bill delivery from customer.");
		Map<String, Object> model = new HashMap<String, Object>();
		String uuid = null;
		Bill bill = null;
		String view = "billing/notFoundBill";
		try {
			uuid = ServletRequestUtils.getStringParameter(request, "uuid");
		}
		catch (ServletRequestBindingException e) {
		}
		if (uuid != null) {
			// we have uuid try to get bill
			bill = bMgr.confirmDelivery(uuid);
		}
		if (bill != null) {
			// we have delivery confirmed bill
			// persist delivery
			bMgr.update(bill);
			// show it
			fetchIvoicedCustomer(bill);
			String localeString = bill.getInvoicedCustomer().getContact()
					.getAddress().getCountry().getLocale().toString();
			view = "billing/printBillTxt_" + localeString;
			model.put("bills", new Bill[] { bill });
			log.info("DELIVERY of bill no " + bill.getNumber()
					+ " CONFIRMED by " + bill.getInvoicedCustomer().getName());
		}
		else {
			// show sorry msg
			model.put("uuid", uuid);
		}
		return new ModelAndView(view, model);
	}

	public ModelAndView emailBill(HttpServletRequest request,
			HttpServletResponse response) throws ServletRequestBindingException {
		log.debug("Emailing bill.");
		Bill bill = getRequestedBill(request);
		try {
			bMgr.email(bill);
			// set success message
			MessagesUtils.setCodedSuccessMessage(request,
					"mainBilling.sendingEmailSuccess", bill.getNumber());
		}
		catch (MailParseException e) {
			// wrong address
			MessagesUtils.setCodedFailureMessage(request,
					"mainBilling.sendingEmailAddressFailure", bill.getNumber());
		}
		catch (MailException e) {
			// sending error
			MessagesUtils.setCodedFailureMessage(request,
					"mainBilling.sendingEmailFailure", bill.getNumber());
		}
		return goBack(request, response);
	}

	public ModelAndView emailBills(HttpServletRequest request,
			HttpServletResponse response) throws ServletRequestBindingException {
		log.debug("Emailing bills to customers.");
		List<Bill> bills = getSelectedBills(request);
		bMgr.emailAll(bills);
		MessagesUtils.setCodedSuccessMessage(request,
				"mainBilling.sendingEmailsSuccess");
		return goBack(request, response);
	}

	public ModelAndView printBillTxt(HttpServletRequest request,
			HttpServletResponse response) throws ServletRequestBindingException {
		log.debug("Printing bill text.");
		Map<String, Object> model = new HashMap<String, Object>();
		Bill bill = getRequestedBill(request);
		fetchIvoicedCustomer(bill);
		String localeString = bill.getInvoicedCustomer().getContact()
				.getAddress().getCountry().getLocale().toString();
		model.put("bills", new Bill[] { bill });
		return new ModelAndView("billing/printBillTxt_" + localeString, model);
	}

	public ModelAndView printBillsTxt(HttpServletRequest request,
			HttpServletResponse response) throws ServletRequestBindingException {
		log.debug("Printing text bills.");
		Map<String, Object> model = new HashMap<String, Object>();
		List<Bill> bills = getSelectedBills(request);
		for (Bill bill : bills) {
			fetchIvoicedCustomer(bill);
		}
		String localeString = "cs";
		if (bills.size() > 0) {
			localeString = bills.get(0).getInvoicedCustomer().getContact()
					.getAddress().getCountry().getLocale().toString();
		}
		model.put("bills", bills);
		return new ModelAndView("billing/printBillTxt_" + localeString, model);
	}

	@SuppressWarnings("unchecked")
	public ModelAndView detailBill(HttpServletRequest request,
			HttpServletResponse response) throws ServletRequestBindingException {
		log.debug("Bill detail.");
		// get bill id from request
		Long id = ServletRequestUtils.getRequiredLongParameter(request,
				"billId");
		log.debug("Received bill id: " + id);
		// get bill
		Bill bill = bMgr.get(id);
		// prepare model using binders for convenience
		ServletRequestDataBinder binder = new ServletRequestDataBinder(bill,
				"bill");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				new SimpleDateFormat("dd.MM.yyyy"), true));
		Map model = binder.getBindingResult().getModel();
		Customer customer = bMgr.fetchCustomer(bill);
		if (Country.PL == customer.getContact().getAddress().getCountry()) {
			model.put("money_key", "money.label.pl");
		}
		else {
			model.put("money_key", "money.label.cz");
		}
		return new ModelAndView("billing/viewBill", model);
	}

	public ModelAndView prepareBills(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Prepare bills STARTED...");
		Date due = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		try {
			String dueStr = ServletRequestUtils.getRequiredStringParameter(
					request, "billingDate");
			dateFormat.setLenient(false);
			due = dateFormat.parse(dueStr);
		}
		catch (Exception e) {
			// set failure message and go back to mainBilling
			MessagesUtils.setCodedFailureMessage(request,
					"mainBilling.dateParseFailure");
			return mainBilling(request, response);
		}
		log.debug("Bills due date: " + due);
		String numbering = ServletRequestUtils.getStringParameter(request,
				"billsNumbering", "");
		try {
			Long.valueOf(numbering);
		}
		catch (NumberFormatException e) {
			// set failure message ang go back to mainBilling
			MessagesUtils.setCodedFailureMessage(request,
					"mainBilling.numberingParseFailure");
			return mainBilling(request, response);
		}
		log.debug("Bills numbering: " + numbering);
		// create Invoicing and invoice customers
		Invoicing invoicing = new Invoicing();
		invoicing.setCountry(getRequestedCountry(request));
		invoicing.setInvoicingDate(due);
		invoicing.setName(invoicing.getProposedName());
		invoicing.setNumberingBase(numbering);
		// persist the invoicing
		bMgr.insertInvoicing(invoicing);
		bMgr.invoice(invoicing);
		log.debug("Prepare bills FINISHED.");
		MessagesUtils.setCodedSuccessMessage(request,
				"mainBilling.prepareBills.success", new Object[] { dateFormat
						.format(due) });
		return new ModelAndView(new RedirectView(request.getContextPath()
				+ "/billing/view.html?action=mainBilling&invoicingId="
				+ invoicing.getId()));
	}

	public ModelAndView sendConfirmed(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Sending confirmed bills.");
		// get confirmed bills
		Invoicing invoicing = getRequestedInvoicing(request);
		List<Bill> bills = bMgr
				.getByStatus(invoicing, true, false, false, null);
		// sent em
		List<Bill> sentBills = bMgr.sendAll(bills);
		// persist bills change (status: sent, not confirmed)
		bMgr.updateAll(sentBills);
		MessagesUtils.setCodedSuccessMessage(request,
				"mainBilling.sendConfirmed.success");
		return new ModelAndView(new RedirectView(request.getContextPath()
				+ "/billing/view.html?action=mainBilling"));
	}

	public ModelAndView exportSentToWinduo(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.debug("Exporting sent bills in Winduo import format.");
		Invoicing invoicing = getRequestedInvoicing(request);
		// set response encoding properly
		response.setCharacterEncoding("Cp1250");
		// use text/plain to display in browser
		response.setContentType("text/csv;charset=windows-1250");
		// dump sent bills to response writer
		// bMgr.exportAllToWinduo(bMgr.getByStatus(null, true, null, false),
		// response.getWriter());
		// because sending is now delayed let's export all confirmed and not
		// archived
		bMgr.exportAllToWinduo(invoicing, bMgr.getByStatus(invoicing, true,
				null, null, null), response.getWriter());
		return null;
	}

	public ModelAndView exportSentToInsert(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.debug("Exporting sent bills in Insert XML format.");
		Invoicing invoicing = getRequestedInvoicing(request);
		// set response encoding properly
		response.setCharacterEncoding("Cp1250");
		// use text/plain to display in browser
		response.setContentType("text/csv;charset=windows-1250");
		// dump sent bills to response writer
		// bMgr.exportAllToWinduo(bMgr.getByStatus(null, true, null, false),
		// response.getWriter());
		// because sending is now delayed let's export all confirmed and not
		// archived
		bMgr.exportAllToInsert(invoicing, bMgr.getByStatus(invoicing, true,
				null, null, null), response.getWriter());
		return null;
	}

	public ModelAndView resendUndelivered(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.debug("Resendig undelivered bills by email.");
		// get sent and undelivered bills
		Invoicing invoicing = getRequestedInvoicing(request);
		List<Bill> bills = bMgr.getByStatus(invoicing, true, true, false, null);
		// sent em
		bMgr.reSendAll(invoicing, bills);
		MessagesUtils.setCodedSuccessMessage(request,
				"mainBilling.resendUndelivered.success");
		return new ModelAndView(new RedirectView(request.getContextPath()
				+ "/billing/view.html?action=mainBilling&invoicingId="
				+ invoicing.getId()));
	}

	// FIXME
	public ModelAndView exportToWinduo(HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			ServletRequestBindingException {
		log.debug("Exporting bill in Winduo import format.");
		Long id = ServletRequestUtils.getRequiredLongParameter(request,
				"billId");
		log.debug("Received bill id: " + id);
		// set response encoding properly
		response.setCharacterEncoding("Cp1250");
		// use text/plain to display in browser
		response.setContentType("text/csv;charset=windows-1250");
		// dump bill to response writer
		List<Bill> bills = new ArrayList<Bill>();
		bills.add(bMgr.get(id));
		// FIXME do it via exportBillToWinDuo(bill, writer), need implement it
		// first
		// koz exportBills generates entries in billing audit!
		bMgr.exportAllToWinduo(
				bMgr.getInvoicing(bills.get(0).getInvoicingId()), bills,
				response.getWriter());
		return null;
	}

	public ModelAndView archiveDelivered(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Archiving delivered bills.");
		// get confirmed, sent and delivered for archivation
		Invoicing invoicing = getRequestedInvoicing(request);
		List<Bill> bills = bMgr.getByStatus(invoicing, true, true, true, false);
		// set archivation
		for (Bill bill : bills)
			bill.setIsArchived(true);
		// persist change
		bMgr.updateAll(bills);
		MessagesUtils.setCodedSuccessMessage(request,
				"mainBilling.archiveDelivered.success");
		return new ModelAndView(new RedirectView(request.getContextPath()
				+ "/billing/view.html?action=mainBilling"));
	}

	public ModelAndView deleteUnconfirmed(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Deleting unconfirmed bills.");
		// get unconfirmed bills and delete em
		Invoicing invoicing = getRequestedInvoicing(request);
		List<Bill> bills = bMgr.getByStatus(invoicing, false, null, null, null);
		bMgr.deleteAll(bills);
		// autdit it and set success message
		MessagesUtils.setCodedSuccessMessage(request,
				"mainBilling.deleteUnconfirmed.success");
		// return to main billing
		return new ModelAndView(new RedirectView(request.getContextPath()
				+ "/billing/view.html?action=mainBilling"));
	}

	public ModelAndView clearAudit(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Clearing billing audit.");
		hmgr.clearBillingAudit();
		MessagesUtils.setCodedSuccessMessage(request,
				"mainBilling.clearAudit.success");
		return new ModelAndView(new RedirectView(request.getContextPath()
				+ "/billing/view.html?action=mainBilling"));
	}

	public ModelAndView toggleSendingInvoices(HttpServletRequest request,
			HttpServletResponse response) {
		boolean newSettingValue = ServletRequestUtils.getBooleanParameter(
				request, "sendingInvoicesFlag", false);
		Country country = getRequestedCountry(request);
		log.info("Changing sending invoices processing to: " + newSettingValue
				+ " for " + country.getShortName());
		bMgr.setSendingEnabled(newSettingValue, country);
		// requires _navPushUrl=1 in request
		return goBack(request, response);
	}

	public ModelAndView showPreparedUnconfirmed(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Showing prepared and not confirmed bills.");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("listBillsTitle", "listBills.title.unconfirmed");
		Invoicing invoicing = getRequestedInvoicing(request);
		if (!AbstractCRUDController.isTablePagination(request))
			request.getSession().setAttribute("billsList",
					bMgr.getByStatus(invoicing, false, null, null, null));
		model.put("invoicing", invoicing);
		model.put("scripts", new String[] { "formUtils.js" });
		model.put("billingTablePagingAction", "showPreparedUnconfirmed");
		return new ModelAndView("billing/listBills", model);
	}

	public ModelAndView showPreparedConfirmed(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Showing prepared and confirmed bills.");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("listBillsTitle", "listBills.title.confirmed");
		// model.put("billsList", bMgr.getByStatus(true, false, false, false));
		// when pagging use session list
		Invoicing invoicing = getRequestedInvoicing(request);
		if (!AbstractCRUDController.isTablePagination(request))
			request.getSession().setAttribute("billsList",
					bMgr.getByStatus(invoicing, true, false, false, null));
		model.put("invoicing", invoicing);
		model.put("scripts", new String[] { "formUtils.js" });
		model.put("billingTablePagingAction", "showPreparedConfirmed");
		return new ModelAndView("billing/listBills", model);
	}

	public ModelAndView showSentUndelivered(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Showing sent and not delivered bills.");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("listBillsTitle", "listBills.title.undelivered");
		Invoicing invoicing = getRequestedInvoicing(request);
		if (!AbstractCRUDController.isTablePagination(request))
			request.getSession().setAttribute("billsList",
					bMgr.getByStatus(invoicing, true, true, false, null));
		model.put("invoicing", invoicing);
		model.put("scripts", new String[] { "formUtils.js" });
		model.put("billingTablePagingAction", "showSentUndelivered");
		return new ModelAndView("billing/listBills", model);
	}

	public ModelAndView showSentDelivered(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Showing sent and delivered bills.");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("listBillsTitle", "listBills.title.delivered");
		Invoicing invoicing = getRequestedInvoicing(request);
		if (!AbstractCRUDController.isTablePagination(request))
			request.getSession().setAttribute("billsList",
					bMgr.getByStatus(invoicing, true, true, true, null));
		model.put("invoicing", invoicing);
		model.put("scripts", new String[] { "formUtils.js" });
		model.put("billingTablePagingAction", "showSentDelivered");
		return new ModelAndView("billing/listBills", model);
	}

	public ModelAndView showSentMail(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Showing bills to be sent via mail.");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("listBillsTitle", "listBills.title.sentMail");
		Invoicing invoicing = getRequestedInvoicing(request);
		if (!AbstractCRUDController.isTablePagination(request))
			request.getSession().setAttribute("billsList",
					bMgr.getBySentMail(invoicing));
		model.put("invoicing", invoicing);
		model.put("scripts", new String[] { "formUtils.js" });
		model.put("billingTablePagingAction", "showSentMail");
		return new ModelAndView("billing/listBills", model);
	}

	public ModelAndView showList(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Showing bills list.");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("listBillsTitle", "listBills.title.list");
		if (!AbstractCRUDController.isTablePagination(request)) {
			// support view with Bills
			// get Bill filter params from session
			Map<String, String> filterMap = FilterUtils.getFilterMap(request,
					"bill.");
			if (filterMap.size() == 0) {
				// no filtering show info
				log.debug("Considered empty filter.");
				model.put("emptyFilter", true);
				request.getSession().setAttribute("billsList", null);
			}
			else {
				// bind given filterMap to example object
				Bill bill = new Bill();
				ServletRequestDataBinder binder = new ServletRequestDataBinder(
						bill);
				// register custom editors if needed here
				// bind session filter data to example bill
				binder.bind(new MutablePropertyValues(filterMap));
				if (log.isDebugEnabled())
					log.debug("Bill findByExample object : " + bill);
				request.getSession().setAttribute("billsList",
						bMgr.getByExample(bill));
			}
		}
		model.put("scripts", new String[] { "formUtils.js" });
		model.put("billingTablePagingAction", "showSentMail");
		return new ModelAndView("billing/listBills", model);
	}

	private Bill getRequestedBill(HttpServletRequest request)
			throws ServletRequestBindingException {
		return bMgr.get(ServletRequestUtils.getRequiredLongParameter(request,
				"billId"));
	}

	private List<Bill> getSelectedBills(HttpServletRequest request) {
		List<Bill> bills = new ArrayList<Bill>();
		for (String idString : getSelectedBillsIdSet(request))
			bills.add(bMgr.get(Long.valueOf(idString)));
		return bills;
	}

	private Invoicing getRequestedInvoicing(HttpServletRequest request) {
		long invoicingId = ServletRequestUtils.getLongParameter(request,
				"invoicingId", 0);
		if (invoicingId == 0)
			throw new NullPointerException("No real invocing required.");
		Invoicing invoicing = bMgr.getInvoicing(invoicingId);
		if (invoicing == null)
			throw new NullPointerException(
					"Requested invoicing does not exist.");
		return invoicing;
	}

	private Country getRequestedCountry(HttpServletRequest request) {
		String countryId = ServletRequestUtils.getStringParameter(request,
				"country", "cz");
		if ("pl".equals(countryId))
			return Country.PL;
		if ("sk".equals(countryId))
			return Country.SK;
		return Country.CZ;
	}

	@SuppressWarnings("unchecked")
	private Iterable<String> getSelectedBillsIdSet(HttpServletRequest request) {
		Map<String, Object> selectedBillsMap = WebUtils
				.getParametersStartingWith(request, "selectedBills_");
		return selectedBillsMap.keySet();
	}

	private void fetchIvoicedCustomer(Bill bill) {
		bill.setInvoicedCustomer(bMgr.fetchCustomer(bill));
	}

	public ModelAndView goBack(HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("Going back.");
		String returnUrl = NavigationUtils.getReturnUrl(request,
				"/billing/view.html?action=mainBilling");
		return new ModelAndView(new RedirectView(returnUrl));
	}
}
