package cz.silesnet.web.mvc;

import cz.silesnet.model.Label;

import javax.servlet.http.HttpServletRequest;

public class LabelController extends AbstractEntityController<Label> {

  public Label newCommandObject(HttpServletRequest request) {
    Label label = new Label();
    label.setParentId(0L);
    return label;
  }
}
