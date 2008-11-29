function checkAll(p_formname) {
	var t_elements = (eval("document." + p_formname + ".elements"));

	for (var i = 0; i < t_elements.length; i++) {
    	if(t_elements[i].type == 'checkbox') {
      		t_elements[i].checked = true;
   		}
  	}
}

function uncheckAll(p_formname) {
	var t_elements = (eval("document." + p_formname + ".elements"));

	for (var i = 0; i < t_elements.length; i++) {
    	if(t_elements[i].type == 'checkbox') {
      		t_elements[i].checked = false;
   		}
  	}
}

function inv_checkAll(p_formname) {
	var t_elements = (eval("document." + p_formname + ".elements"));

	for (var i = 0; i < t_elements.length; i++) {
    	if(t_elements[i].type == 'checkbox') {
      		t_elements[i].checked = ! t_elements[i].checked;
   		}
  	}
}
