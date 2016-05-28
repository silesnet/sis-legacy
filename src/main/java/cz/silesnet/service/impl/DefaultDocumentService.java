package cz.silesnet.service.impl;

import cz.silesnet.dao.BillDAO;
import cz.silesnet.model.Bill;
import cz.silesnet.service.BillingManager;
import cz.silesnet.service.DocumentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class DefaultDocumentService implements DocumentService {
  protected final Log log = LogFactory.getLog(getClass());

  private BillDAO billDAO;

  public void setBillDAO(BillDAO billDAO) {
    this.billDAO = billDAO;
  }

  public InputStream invoicePdfStream(String uuid) {
    Bill bill = billDAO.get(uuid);
    long billId = bill.getId();
    log.info("getting html of " + billId);

    return new ByteArrayInputStream(uuid.getBytes());
  }
}
