package cz.silesnet.web.mvc;

import cz.silesnet.model.Entity;
import cz.silesnet.model.Historic;
import cz.silesnet.service.HistoryManager;
import cz.silesnet.service.PersistenceManager;
import cz.silesnet.utils.ControllerUtils;
import cz.silesnet.utils.FilterUtils;
import cz.silesnet.utils.MessagesUtils;
import cz.silesnet.utils.NavigationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class AbstractEntityController<E extends Entity> extends
    MultiActionController {

  // Member fields ------------------------------------------------

  protected final Log log = LogFactory.getLog(getClass());

  protected PersistenceManager<E> persistenceManager;

  private HistoryManager historyManager;

  private String commandName = "command";

  private String commandsName;

  private String commandIdParam;

  private String commandNewFlag = "isNew";

  private String formView;

  private String listView;

  // Main methods -------------------------------------------------

  public ModelAndView showForm(HttpServletRequest request,
                               HttpServletResponse response) {
    log.debug("Showing form.");
    // get fresh command object
    E command = formBackingObject(request);
    // prepare command object binder
    ServletRequestDataBinder binder = createBinder(command);
    // prepare form model
    Map model = referenceFormDataInternal(request, binder
        .getBindingResult());
    // proceed to form view
    return new ModelAndView(getFormView(), model);
  }

  public ModelAndView showErrorForm(HttpServletRequest request,
                                    HttpServletResponse response,
                                    ServletRequestBindingException bindingException) {
    log.debug("Showing error form.");
    // retrieve BindException
    BindException errors = (BindException) bindingException.getRootCause();
    // prepare form model with errors
    Map model = referenceFormDataInternal(request, errors);
    // proceed to form view
    return new ModelAndView(getFormView(), model);
  }

  public ModelAndView goBack(HttpServletRequest request,
                             HttpServletResponse response) {
    log.debug("Navigating back.");
    // get return url
    String returnUrl = NavigationUtils.getReturnUrl(request);
    // if no return url available default to showList()
    return returnUrl != null ? new ModelAndView(new RedirectView(returnUrl))
        : showList(request, response);
  }

  public ModelAndView showList(HttpServletRequest request,
                               HttpServletResponse response) {
    log.debug("Default showList() implementation with filtering.");
    HashMap<String, Object> model = new HashMap<String, Object>();
    // reference commands list if needed
    if (!NavigationUtils.isTablePagination(request)) {
      // get commands list from persistence manager trying to utilize
      // filter
      E filterObject = getFilterCommandObject(request);
      List<E> commands = null;
      if (filterObject != null)
        commands = persistenceManager.getByExample(filterObject);
      else
        commands = persistenceManager.getAll();
      // reference command objects as session attribute
      request.getSession().setAttribute(getCommandsName(), commands);
      log.debug("Commands list referenced as session attribute: "
          + getCommandsName());
    }
    // reference common view data
    ControllerUtils
        .putAll(model, referenceViewData(request, getListView()));
    return new ModelAndView(getListView(), model);
  }

  public ModelAndView insert(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
    log.debug("Inserting new entity.");
    // get validated entity
    E command = bindAndValidate(request);
    // persist it
    persistenceManager.insert(command);
    // set success message
    MessagesUtils.setCodedSuccessMessage(request,
        getFormMessage("insertSuccess"), command.getId());
    // force list refresh in case of showList()
    NavigationUtils.setForceListRefresh(request, true);
    return goBack(request, response);
  }

  public ModelAndView update(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
    log.debug("Updating entity.");
    // get validated entity
    E command = bindAndValidate(request);
    // persist changes
    persistenceManager.update(command);
    // set success message
    MessagesUtils.setCodedSuccessMessage(request,
        getFormMessage("updateSuccess"), command.getId());
    // force list refresh in case of showList()
    NavigationUtils.setForceListRefresh(request, true);
    return goBack(request, response);
  }

  public ModelAndView delete(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
    log.debug("Deleting entity.");
    // get entity
    E command = formBackingObject(request);
    // persist changes
    persistenceManager.delete(command);
    // set success message
    MessagesUtils.setCodedSuccessMessage(request,
        getFormMessage("deleteSuccess"), command.getId());
    // dispose url of deleted object from navigation stact
    NavigationUtils.getReturnUrl(request);
    // force list refresh in case of showList()
    NavigationUtils.setForceListRefresh(request, true);
    return goBack(request, response);
  }

  public ModelAndView cancel(HttpServletRequest request,
                             HttpServletResponse response) {
    log.debug("Canceling form.");
    return goBack(request, response);
  }

  // Command object methods ---------------------------------------

  protected Boolean isFormBackingObjectNew(HttpServletRequest request) {
    // if there is no commad
    return request.getParameter(getCommandIdParam()) == null ? true : false;
  }

  protected Long getCommandId(HttpServletRequest request) {
    return ServletRequestUtils.getLongParameter(request,
        getCommandIdParam(), 0);
  }

  public abstract E newCommandObject(HttpServletRequest request);

  protected E formBackingObject(HttpServletRequest request) {
    E command = null;
    if (isFormBackingObjectNew(request))
      command = newCommandObject(request);
    else {
      command = persistenceManager.get(getCommandId(request));
    }
    return command;
  }

  // Binding command object methods -------------------------------

  protected final ServletRequestDataBinder createBinder(E command) {
    // have new binder on command object
    ServletRequestDataBinder binder = new ServletRequestDataBinder(command,
        getCommandName());
    // init binder with custom editors
    initBinder(binder);
    return binder;
  }

  protected void initBinder(ServletRequestDataBinder binder) {
    log
        .debug("Default initializing binder with custom editors (Integer, Long, Date).");
    // Integers
    binder.registerCustomEditor(Integer.class, new CustomNumberEditor(
        Integer.class, true));
    // Long numbers
    binder.registerCustomEditor(Long.class, new CustomNumberEditor(
        Long.class, true));
    // Dates
    binder.registerCustomEditor(Date.class, new CustomDateEditor(
        new SimpleDateFormat("dd.MM.yyyy"), true));
  }

  protected void validate(E command, BindingResult bindingResult) {
    log.debug("Default validating command object (none).");
  }

  protected void onBind(HttpServletRequest request, E command) {
    log.debug("Default post binding command processing (none).");
  }

  protected void onBindAndValidate(HttpServletRequest request, E command) {
    log
        .debug("Default post binding and validation command processing (none).");
  }

  private final E bind(HttpServletRequest request, boolean validate)
      throws Exception {
    log.debug("Binding request parameters to command object.");
    // retrieve command object
    E command = formBackingObject(request);

    // have new binder for command object and bind it
    ServletRequestDataBinder binder = createBinder(command);
    binder.bind(request);
    // custom command object post processing
    onBind(request, command);
    // validate if needed
    if (validate) {
      validate(command, binder.getBindingResult());
      // custom command object post processing
      onBindAndValidate(request, command);
    }
    // raise ServletRequestBindingException on errors
    binder.closeNoCatch();
    // return command
    return command;
  }

  protected E bind(HttpServletRequest request) throws Exception {
    return bind(request, false);
  }

  protected E bindAndValidate(HttpServletRequest request) throws Exception {
    return bind(request, true);
  }

  // Data reference methods ---------------------------------------

  protected Map referenceCommandData(HttpServletRequest request, E command) {
    log.debug("Referencing command object data (default, none).");
    return null;
  }

  protected Map referenceCommand(E command) {
    return referenceCommand(command, getCommandName());
  }

  protected Map referenceCommand(E command, String commandName) {
    log.debug("Referencing command object throught binder: " + commandName);
    ServletRequestDataBinder binder = new ServletRequestDataBinder(command,
        commandName);
    initBinder(binder);
    return binder.getBindingResult().getModel();
  }

  protected Map referenceCommandAudit(E command) {
    log.debug("Referencing command object history.");
    if (!(command instanceof Historic))
      return null;
    Historic historic = (Historic) command;
    HashMap<String, Object> model = new HashMap<String, Object>();
    if (historyManager != null && historic.getHistoryId() != null)
      model.put("historyRecord", historyManager.getHistory(historic));
    return model;
  }

  protected Map referenceFormData(HttpServletRequest request, E command) {
    log.debug("Referencing general form data (default, none).");
    return null;
  }

  protected Map referenceViewData(HttpServletRequest request, String viewLong) {
    log
        .debug("Referencing general view data (command, commands, commandId, view).");
    HashMap<String, Object> model = new HashMap<String, Object>();
    model.put("command", getCommandName());
    model.put("commands", getCommandsName());
    model.put("commandId", getCommandIdParam());
    if (viewLong != null)
      model.put("view", getSimpleView(viewLong).toString());
    return model;
  }

  @SuppressWarnings("unchecked")
  private final Map referenceFormDataInternal(HttpServletRequest request,
                                              BindingResult bindingResult) {
    // get model from errors that include command object
    Map model = bindingResult.getModel();
    // set commnadNew flag
    model.put(getCommandNewFlag(), isFormBackingObjectNew(request));
    // get command object from model
    E command = (E) model.get(getCommandName());
    // reference form command object data
    ControllerUtils.putAll(model, referenceCommandData(request, command));
    // reference command object history
    ControllerUtils.putAll(model, referenceCommandAudit(command));
    // reference general form data
    ControllerUtils.putAll(model, referenceFormData(request, command));
    // reference general data
    ControllerUtils
        .putAll(model, referenceViewData(request, getFormView()));
    return model;
  }

  // Other methods ------------------------------------------------

  @SuppressWarnings("unchecked")
  public List<E> getSelected(HttpServletRequest request) {
    // get map of attributes starting with select.command. without prefix
    // (expecting id there)
    Map<String, Object> selectedEntitiesMap = WebUtils
        .getParametersStartingWith(request, "select."
            + getCommandName() + ".");
    List<E> entities = new ArrayList<E>();
    // iterate over keys (id) and add corresponding objects to resulting
    // list
    for (String idStr : selectedEntitiesMap.keySet()) {
      log.debug(idStr);
      entities.add(persistenceManager.get(Long.valueOf(idStr)));
    }
    return entities;
  }

  public E newFilterCommandObject(HttpServletRequest request) {
    log
        .debug("Instatiating new filter command object, default newCommandObject()");
    return newCommandObject(request);
  }

  public void onFilter(HttpServletRequest request, E filterObject) {
    log
        .debug("Custom modifications to bound filterCommandObject (defaut: none).");
  }

  public E getFilterCommandObject(HttpServletRequest request) {
    log.debug("Creating filter command object");
    // get filter map for specific command name
    Map<String, String> filterMap = FilterUtils.getFilterMap(request,
        getCommandName() + ".");
    if (filterMap.size() == 0)
      // nothink to filter here
      return null;
    // get new filter command object
    E filterObject = newFilterCommandObject(request);
    // get new binder
    ServletRequestDataBinder binder = new ServletRequestDataBinder(
        filterObject);
    // init binder with custom editors
    initBinder(binder);
    // bind filter map to filter object
    binder.bind(new MutablePropertyValues(filterMap));
    // allow custom filterObject modifications
    onFilter(request, filterObject);
    if (log.isDebugEnabled())
      log.debug("FilterCommandObject: " + filterObject);
    return filterObject;
  }

  public StringBuffer getSimpleView(String view) {
    return new StringBuffer(view.substring(view.lastIndexOf("/") + 1));
  }

  protected String getFormMessage(String suffix) {
    return getSimpleView(getFormView()).append(".").append(suffix)
        .toString();
  }

  // Accessors ----------------------------------------------------

  public String getCommandName() {
    return commandName;
  }

  public void setCommandName(String commandName) {
    this.commandName = commandName;
  }

  public String getCommandNewFlag() {
    return commandNewFlag;
  }

  public void setCommandNewFlag(String commandNewFlag) {
    this.commandNewFlag = commandNewFlag;
  }

  public String getCommandIdParam() {
    return commandIdParam == null ? getCommandName() + "Id"
        : commandIdParam;
  }

  public void setCommandIdParam(String commandIdParam) {
    this.commandIdParam = commandIdParam;
  }

  public String getFormView() {
    return formView == null ? getCommandName() + "/" + getCommandName()
        + "Form" : formView;
  }

  public void setFormView(String formView) {
    this.formView = formView;
  }

  public String getListView() {
    return listView == null ? getCommandName() + "/" + getCommandName()
        + "List" : listView;
  }

  public void setListView(String listView) {
    this.listView = listView;
  }

  public PersistenceManager<E> getPersistenceManager() {
    return persistenceManager;
  }

  public void setPersistenceManager(PersistenceManager<E> persistenceManager) {
    this.persistenceManager = persistenceManager;
  }

  public HistoryManager getHistoryManager() {
    return historyManager;
  }

  public void setHistoryManager(HistoryManager historyManager) {
    this.historyManager = historyManager;
  }

  protected String getCommandsName() {
    return commandsName == null ? getCommandName() + "s" : commandsName;
  }

  public void setCommandsName(String commandsName) {
    this.commandsName = commandsName;
  }
}