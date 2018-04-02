function consoles(message, type, scriptName, functionName) {
	let sign = "[系统提示] ";
	let connector = " => ";
	if (message) {
		message = sign + scriptName + connector + functionName + connector + message;
		if ('log' === type || !type) {
			console.log(message);
		} else if ('error' === type) {
			console.error(message);
		}
	}
}