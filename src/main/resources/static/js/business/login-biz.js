if (vueContentObject) getVueObject().$destroy();

[vuePageSize, loadPageableDataUrl, tableColumnsName, tableColumnsKey] = [
	10, 
	'page',
	['ID','用户','电子邮箱','操作系统','浏览器(版本)','IP地址','品牌','型号','屏幕尺寸','登录时间'],
	['id','user','email','operatingSystem','browser','ipAddress','brand','model','screenSize','createdDate']
];

parseValuesOnTableEachRow = obj => ({
	id: obj.id,
	user: obj.user.nickname,
	email: obj.user.email,
	operatingSystem: obj.operatingSystem,
	browser: obj.browser,
	ipAddress: obj.ipAddress,
	brand: obj.brand ? obj.brand : '-',
	model: obj.model ? obj.model : '-',
	screenSize: obj.screenSize ? obj.screenSize : '-',
	createdDate:formatDateTime(obj.createdDate)
});

[queryFormItemName, queryFormItemKey, queryFormItemType] = [
	['ID','用户编号','电子邮箱','操作系统','浏览器','IP地址','品牌','型号','屏幕尺寸'],
	['id','userId','email','operatingSystem','browser','ipAddress','brand','model','screenSize'],
	['string','string','string','string','string','string','string','string','string']
];

var vueContentObject = new Vue(initializeContentOptions());
