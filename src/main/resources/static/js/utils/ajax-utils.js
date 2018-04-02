$(() => {
	
	$.call = url => {
		$.ajax({
	        url: url,
	        success: data => {
	        	return data;
	        }
	    });
	}
	
	$.puty = (url, data, successCallback, errorCallback) => {
		$.ajax({
			headers: {'Accept': 'application/json','Content-Type': 'application/json'},
		    cache: false,
	        async: false,
		    type: 'PUT',
		    url: url,
		    data: JSON.stringify(data),
		    dataType: 'json',
		    success: result => {
		    	if (successCallback) successCallback(result);
		    },
		    error: (XMLHttpRequest, textStatus, errorThrown) => {
		    	console.log(XMLHttpRequest.status+' readyState:'+XMLHttpRequest.readyState +' '+XMLHttpRequest.responseText);
		        if(errorCallback) errorCallback('请求异常(代码:'+XMLHttpRequest.status+')');
		    }
		});
	};
	
	$.posty = (url, data, successCallback, errorCallback) => {
		$.ajax({
			headers: {'Accept': 'application/json','Content-Type': 'application/json'},
		    cache: false,
	        async: false,
		    type: 'POST',
		    url: url,
		    data: JSON.stringify(data),
		    dataType: 'json',
		    success: result => {
		    	if (successCallback) successCallback(result);
		    },
		    error: (XMLHttpRequest, textStatus, errorThrown) => {
		    	console.log(XMLHttpRequest.status+' readyState:'+XMLHttpRequest.readyState +' '+XMLHttpRequest.responseText);
		        if(errorCallback) errorCallback('请求异常(代码:'+XMLHttpRequest.status+')');
		    }
		});
	}
	
	$.deletey = (url, data, successCallback, errorCallback) => {
		$.ajax({
			type : 'DELETE',
			url : url + '/' + data,
			dataType : 'json',
			success: result => {
		    	if (successCallback) successCallback(result);
		    },
		    error: (XMLHttpRequest, textStatus, errorThrown) => {
		    	console.log(XMLHttpRequest.status+' readyState:'+XMLHttpRequest.readyState +' '+XMLHttpRequest.responseText);
		        if(errorCallback) errorCallback('请求异常(代码:'+XMLHttpRequest.status+')');
		    }
		});
	}
	
	$.gety = (url, data, successCallback, errorCallback) => {
		$.ajax({
			headers: {'Accept': 'application/json','Content-Type': 'application/json'},
		    cache: false,
	        async: false,
		    type: 'GET',
		    url: url,
		    data: data,
		    dataType: 'json',
		    success: result => {
		    	if (successCallback) successCallback(result);
		    },
		    error: (XMLHttpRequest, textStatus, errorThrown) => {
		    	console.log(XMLHttpRequest.status+' readyState:'+XMLHttpRequest.readyState +' '+XMLHttpRequest.responseText);
		        if(errorCallback) errorCallback('请求异常(代码:'+XMLHttpRequest.status+')');
		    }
		});
	}
	
	/**
	 * arguments:
	 * 0: url
	 * 1: params
	 * 2: successCallback function
	 * 3: errorCallback function
	 * 4: run as first function
	 */
	$.igety = function() {
		let argumentsLength = arguments.length;
		if (argumentsLength >= 2) {
			
			// 如果该方法有5个参数，那么第五个参数将被期待是一个执行请求操作之前的动态插入函数
			// 在执行真正的请求之前，该动态函数将先一步被执行
			if (argumentsLength == 5) {
				if (arguments[4] && typeof arguments[4] === "function") {
					arguments[4](arguments);
				}
			}
			
			let url = arguments[0];
			if(currentRequestMappingRootPath) {
				url = currentRequestMappingRootPath+"/"+url;
			}
			let errorCallback = argumentsLength < 4 || !arguments[3] ? defaultErrorCallback : arguments[3];
			let params = arguments[1];
			let successCallback = arguments[2];
			switch(arguments.length) {
				case 2:
					$.gety(url, null, params, errorCallback);
					break;
				case 3:
					$.gety(url, params, successCallback, errorCallback);
					break;
				case 4:
				case 5:
					$.gety(url, params, successCallback, errorCallback);
					break;
				default:
					toastError('非法的方法调用: 过少或过多的输入参数');
					break;
			}
		}
	}
	
	$.iposty = (url, data, successCallback, errorCallback) => {
		if(currentRequestMappingRootPath) url = currentRequestMappingRootPath+"/"+url;
		if(!errorCallback) errorCallback = defaultErrorCallback;
		$.posty(url, data, successCallback, errorCallback);
	}
	
	$.iputy = (url, data, successCallback, errorCallback) => {
		if(currentRequestMappingRootPath) url = currentRequestMappingRootPath+"/"+url;
		if(!errorCallback) errorCallback = defaultErrorCallback;
		$.puty(url, data, successCallback, errorCallback);
	};
	
	$.ideletey = (url, data, successCallback, errorCallback) => {
		if(currentRequestMappingRootPath) url = currentRequestMappingRootPath+"/"+url;
		if(!errorCallback) errorCallback = defaultErrorCallback;
		$.deletey(url, data, successCallback, errorCallback);
	}
	
	defaultErrorCallback = errorMessage => toastError(errorMessage);
	
});