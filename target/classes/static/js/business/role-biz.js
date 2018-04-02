if (vueContentObject) getVueObject().$destroy();

/******************************************* Table *******************************************/

[vuePageSize, loadPageableDataUrl, tableColumnsName, tableColumnsKey, tableButtonsOnEachRow] = [
	10, 
	'page',
	['', 'ID', '名称', '优先级', '是否可用', '创建日期', '最后修改日期', '操作'],
	['selection','id#sortable','name','priority','available','createdDate','updatedDate','operation'],
	['rowUpdateButton#修改','rowCopyButton#复制','rowDeleteButton#删除']
];

parseValuesOnTableEachRow = obj => ({
		id: obj.id,
		name: obj.name,
        priority: obj.priority,
		available: obj.available ? '启用' : '弃用',
		createdDate: formatDateTime(obj.createdDate),
		updatedDate: formatDateTime(obj.updatedDate)
})

/******************************************* Forms (queryForm, addForm, updateForm, copyForm, delete functionality) *******************************************/

setFormDataObject({id: -1, name: '', priority: '', available: 'true'});

[queryFormItemName, queryFormItemKey, queryFormItemType] = [
	['ID', '名称', '优先级', '是否可用', '创建日期'],
	['id', 'name', 'priority', 'available', 'createdDate'],
	['string', 'string', '0<number<99', 'select#availableDataSelect', 'date']
];

setFormRulesObject({
	'name': [{trigger: 'blur',type: 'string', required: true, min: 2, max: 20, message: '角色名称必须为长度大于等于2小于20的字符串!'}, {validator: this.validateFormRules, trigger: 'blur',unique: 'isRoleNameUnique', message: '角色名称已被占用'}],
	'priority': [{trigger: 'blur',type: 'number', required: true, min: 0, max: 99, message: '角色优先级必须为0-99的数字!'}]
});

//排序方法
vueContentMethods.vueBindTableSortMethod = function (a, b, c, d, e) {
    this.vueTableLoadPageMethod();
    console.log(a);
    console.log(b);
    console.log(e);
    console.log(d);
    console.log(e);
};
// 自定义 Data
vueContentBeforeCreate = function () {
    customVueContentData = {
    	availableDataSelect : [{value: 'true', label: '启用'},{value: 'false', label: '弃用'}],
        vueAllotPermissionForm: {items: []}, //() => {items:[]},
        vueAllotModalVisible: false
    };
};

//hasQueryForm = false; //是否有queryForm

// 向vueContentObject注册自定义方法
vueContentMethods.doAllotButton = doAllotButton;
vueContentMethods.loadAllotPermissionAll = loadAllotPermissionAll;
vueContentMethods.generateItemObj = generateItemObj;
vueContentMethods.loadAllotRolePermission = loadAllotRolePermission;
vueContentMethods.resetAllotForm = resetAllotForm;
vueContentMethods.clickAllotCheckAll = clickAllotCheckAll;
vueContentMethods.changeAllotCheckAll = changeAllotCheckAll;
vueContentMethods.submitAllotForm = submitAllotForm;
vueContentMethods.cancelAllotForm = cancelAllotForm;

//点击'分配权限'按钮，弹出AllotForm Modal
function doAllotButton() {
    var checkedRows = this[currentCheckedTableRowName];
    if (checkedRows.length === 0) {
        toastInfo('至少选中1条记录!');
        return;
    }
    this.loadAllotPermissionAll();
    this.vueAllotModalVisible = true;
}

//调入所有权限信息并初始化
function loadAllotPermissionAll() {
	// 暂存currentRequestMappingRootPath默认值，以便后续对currentRequestMappingRootPath进行再次赋值
	let tempCurrentRequestMappingRootPath = currentRequestMappingRootPath;
	$.igety("permission/available", null, data => {
		let length = data.length;
		if (length !== 0) {
			let currentGroup = data[0].group, currentIds = [], currentNames = [], currentOriginals = [], currentValues = [];
			data.map((each, index, array) => {
				if (currentGroup != each.group) {
					vueContentObject.vueAllotPermissionForm.items.push(
						generateItemObj(false, false, currentGroup, currentIds, currentNames, currentOriginals, currentValues));
					currentGroup = each.group;
					currentIds = [];
					currentNames = [];
					currentOriginals = [];
					currentValues = [];
				}
				currentIds.push(each.id);
				currentNames.push(each.name);
				currentOriginals.push(false);
				currentValues.push(false);
			});
			vueContentObject.vueAllotPermissionForm.items.push(
				generateItemObj(false, false, currentGroup, currentIds, currentNames, currentOriginals, currentValues));
			
			// 恢复currentRequestMappingRootPath默认值
			currentRequestMappingRootPath = tempCurrentRequestMappingRootPath; 
			// 将角色Table控件中被选中记录的id传入后台，用以加载其下所有权限
			vueContentObject.loadAllotRolePermission(vueContentObject.vueCheckedTableRow[0].id);
		}
	}, null, arguments => currentRequestMappingRootPath = null);
	
}

function generateItemObj(indeterminate, checkAll, group, ids, names, originals, values) {
    var obj = {};
    obj.indeterminate = indeterminate;
    obj.checkAll = checkAll;
    obj.group = group;
    obj.ids = ids;
    obj.names = names;
    obj.originals = originals;
    obj.values = values;
    return obj;
}

//将角色Table控件中被选中记录的id传入后台，用以加载其下所有权限
//调入角色相应的权限并更新
function loadAllotRolePermission(roleId) {
	$.igety("single/" + roleId, null, obj => {
		let currentRoleId = obj.id;
		let currentRolePermission = [];
		obj.permissions.map(each => currentRolePermission.push(each.id));
		
		let array = vueContentObject.vueAllotPermissionForm.items;
		array.map((each, index, array) => {
			each.ids.map((idEach, idIndex, idArray) => {
				if (currentRolePermission.indexOf(idEach, 0) != -1)
					each.originals[idIndex] = true;
			});
		});
		vueContentObject.resetAllotForm();
	});
}
//分配权限Modal ReSet
function resetAllotForm() {
//this.$refs[name].resetFields(); 此功能不稳定!
    var arrayObj = this.vueAllotPermissionForm.items;
    for (var i in arrayObj) {
        arrayObj[i].values = arrayObj[i].originals.slice(0);
        this.changeAllotCheckAll(i);
    }
}
//分配权限Modal 手动全选/全释放
function clickAllotCheckAll(index) {
    var arrayItem = this.vueAllotPermissionForm.items[index];
    if (arrayItem.indeterminate) {
        arrayItem.indeterminate = false;
        arrayItem.checkAll = true;
    } else {
        arrayItem.checkAll = !arrayItem.checkAll;
    }
    for (var v in arrayItem.values) {
        arrayItem.values[v] = arrayItem.checkAll;
    }
}
//分配权限Modal 当每单选时,是否判断被全选/全释放
function changeAllotCheckAll(index) {
    var arrayItem = this.vueAllotPermissionForm.items[index];
    var trueCount = 0, falseCount = 0;
    var curLength = arrayItem.values.length;
    for (var i = 0; i < curLength; i++) {
        if (arrayItem.values[i]) {
            trueCount++;
        } else {
            falseCount++;
        }
    }
    arrayItem.indeterminate = (trueCount === curLength || falseCount === curLength) ? false : true;
    arrayItem.checkAll = trueCount === curLength ? true : false;
}
//Allot Modal 提交按钮 - 提交对选中角色分配的权限信息
function submitAllotForm() {
    var roleId = this.vueCheckedTableRow[0].id;
    var permissionIds = [];
    var array = vueContentObject.vueAllotPermissionForm.items;
    vueContentObject.vueAllotPermissionForm.items.map(each => {
    	each.values.map((valueEach, valueIndex) => {
    		if (valueEach) {
    			permissionIds.push(each.ids[valueIndex]);
    		}
    	});
    });
    $.iputy("allot", {roleId: roleId, permissionIds: permissionIds}, isAllotted => toastInfo(isAllotted ? '分配权限成功!' : '分配权限失败!'));
    this.cancelAllotForm();
}

//Modal 取消按钮 / X按钮 - 重制角色分配权限Modal控件的值，并隐藏Modal
function cancelAllotForm() {
    this.vueAllotPermissionForm = {items: []};
    this.vueTableData = this.vueTableData.slice(0);
    this.vueCheckedTableRow = [];
    this.vueAllotModalVisible = false;
}

var vueContentObject = new Vue(initializeContentOptions());