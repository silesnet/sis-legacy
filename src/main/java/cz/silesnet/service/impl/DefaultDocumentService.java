package cz.silesnet.service.impl;

import cz.silesnet.model.Bill;
import cz.silesnet.service.BillingManager;
import cz.silesnet.service.DocumentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DefaultDocumentService implements DocumentService {
  protected final Log log = LogFactory.getLog(getClass());

  private BillingManager billingManager;

  public void setBillingManager(BillingManager billingManager) {
    this.billingManager = billingManager;
  }

  public InputStream invoicePdfStream(String uuid) {
    Bill bill = billingManager.confirmDelivery(uuid);
    long billId = bill.getId();
    log.info("getting html of " + billId);

    return new ByteArrayInputStream(uuid.getBytes());
  }
}
