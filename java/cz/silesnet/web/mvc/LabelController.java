package cz.silesnet.web.mvc;

import javax.servlet.http.HttpServletRequest;

import cz.silesnet.model.Label;

public class LabelController extends AbstractEntityController<Label> {

	public Label newCommandObject(HttpServletRequest request) {
		Label label = new Label();
		label.setParentId(0L);
		return label;
	}
}
