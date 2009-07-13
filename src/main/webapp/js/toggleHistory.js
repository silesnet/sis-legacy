
var showHistory='block';

function toggleHistory() {

	// toggle var
	if (showHistory == 'none')
		showHistory = 'block';
	else
		showHistory = 'none';

	// execute it		
	document.getElementById('historyTable').style.display = showHistory;
}

toggleHistory();
