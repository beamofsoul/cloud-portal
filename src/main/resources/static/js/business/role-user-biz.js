if (vueContentObject) getVueObject().$destroy();

[vuePageSize, loadPageableDataUrl, tableColumnsName, tableColumnsKey, tableButtonsOnEachRow] = [
	10, 
	'pageOfUserRole',
	['用户编号', '用户昵称', '用户名称', '角色名称', '操作'],
	['userId', 'nickname', 'username', 'roleName', 'operation'],
	['clickRowSetRoleButton#设置']
];

parseValuesOnTableEachRow = obj => ({
	id : obj.id,
	userId: obj.userId,
    nickname: obj.nickname,
    username: obj.username,
    roleName: obj.roleName === '' ? '' : obj.roleName
});

[queryFormItemName, queryFormItemKey, queryFormItemType] = [
	['用户编号','用户昵称','用户名称','角色名称'],
	['userId', 'nickname', 'username', 'roleName'],
	['string','string','string','string']
];

const defaultSetRoleFormData = {userId: 0, nickName: '', userName: '', allRoles: [], userRoles: []};
vueContentBeforeCreate = function () {
    customVueContentData = {
        vueSetRoleTreeData: loadSetRoleAllAvailableRole2TreeData(),
        vueSetRoleFormData: defaultSetRoleFormData,
        vueSetRoleCondition: null,
        vueSetRoleModalVisible: false
    };
};

// 自定义 Module
vueContentMethods.loadSetRoleAllAvailableRole2TreeData = loadSetRoleAllAvailableRole2TreeData;
vueContentMethods.onSelectChangeTree = onSelectChangeTree;
vueContentMethods.clickRowSetRoleButton = clickRowSetRoleButton;
vueContentMethods.loadSetRoleUserRole = loadSetRoleUserRole;
vueContentMethods.submitSetRoleForm = submitSetRoleForm;
vueContentMethods.cancelSetRoleForm = cancelSetRoleForm;
vueContentMethods.loadPageableUserRoles = loadPageableUserRoles;

//获取所有可用角色信息列表,并将列表值赋给给角色树控件
function loadSetRoleAllAvailableRole2TreeData() {
	let children = [];
    let curSetRoleTreeData = [{title: '可用角色列表', expand: true, selected: true, children: children}];
    $.igety('available', null, data => loadTreeNodeCallback(children, data));
    children.push(parseNode({id: 0, name: 'no_role'}));
    return curSetRoleTreeData;
}

// 覆盖tree-utils.js中默认的解析每个树节点的方法,并修改节点标题显示样式
parseNode = data => ({id: data.id, title: '<span style="padding: 3px; border-bottom: 1px solid #ddd;">' + data.name + '</span>'});

// 点击角色树控件子节点后,根据所选角色对相应的用户角色关联记录进行筛选
function onSelectChangeTree() {
    let treeData = this.vueSetRoleTreeData[0];
    if (treeData.selected) {
        this.vueSetRoleCondition = null;
        this.doLoadPage();
    } else {
        let selectedRole = treeData.children.find(obj => obj.selected);
        if (selectedRole === undefined) {
            if (this.vueSetRoleCondition === null) {
                treeData.selected = true;
            } else {
                treeData.children.find(obj => obj.id === this.vueSetRoleCondition.ids).selected = true;
            }
        } else {
            this.vueSetRoleCondition = {roleId: selectedRole.id};
        }
        loadPageableUserRoles(0, this[currentPageSizeName], this.vueSetRoleCondition);
    }
}

// 根据分页等查询条件对数据表格中数据进行复合筛选
function loadPageableUserRoles(page, size, conditions) {
    $.igety(loadPageableDataUrl, {page: page, size: size, conditions: JSON.stringify(conditions)}, function (data) {
        vueContentObject.vueTableData = [];
        for (let rowContent of data.content) {
            let row = {};
            for (let key of tableColumnsKey) {
                row[key] = rowContent[key];
            }
            vueContentObject.vueTableData.push(row);
        }
        vueContentObject.vueRecordTotal = data.totalElements;
        setTimeout(toastLoading('正在加载中...', 0), 30);
    });
}

// 点击数据表格中某一列的设置按钮后, 弹出设置窗体并加载相应数据
function clickRowSetRoleButton(index, tableDataName) {
    this.vueSetRoleFormData.allRoles = [];
    let formData = this.vueSetRoleFormData;
    let selectedRow = this.vueTableData[index];
    formData.userId = selectedRow.userId;
    formData.nickName = selectedRow.nickname;
    formData.userName = selectedRow.username;
    this.vueSetRoleTreeData[0].children
    	.filter(child => child.id !== 0)
    	.map(child => formData.allRoles.push({id: child.id, name: clearStyle(child.title)}));
    this.loadSetRoleUserRole(formData.userId);
    this.vueSetRoleModalVisible = true;
}

// 根据传入的用户Id加载其对应的用户角色映射信息
function loadSetRoleUserRole(userId) {
    $.igety("singleOfUserRole/" + userId, null, obj => vueContentObject.vueSetRoleFormData.userRoles = obj.roleName.split(",").slice(0));
}

// 点击用户角色设置弹窗的设置按钮后,将修改的用户角色关系提交到后台进行持久化,并关闭设置弹窗
function submitSetRoleForm() {
    let formData = this.vueSetRoleFormData;
    if (formData.userRoles[0] === '') {
    	formData.userRoles.splice(0, 1);
    }
    let roleId = "";
    for (let userRole of formData.userRoles) {
        for (let role of formData.allRoles) {
            if (userRole === role.name) {
                roleId += ((roleId === "") ? "" : ",") + role.id;
                break;
            }
        }
    }
    $.iputy("updateOfUserRole", {userId: this.vueSetRoleFormData.userId, roleId: roleId}, function (data) {
        vueContentObject.loadPageableUserRoles(vueCurrentPage - 1, vueContentObject[currentPageSizeName], vueContentObject.vueSetRoleCondition);

    });
    this.cancelSetRoleForm();
}
// 点击用户角色设置弹窗的关闭按钮后, 重置该弹窗对应的表单数据, 最后关闭弹窗
function cancelSetRoleForm() {
    this.vueSetRoleFormData = defaultSetRoleFormData;
    this.vueSetRoleModalVisible = false;
}

var vueContentObject = new Vue(initializeContentOptions());

// 清除角色树子节点title的显示样式，使其恢复为原始数据
let clearStyle = title => {
	let leftIndex = title.indexOf('>');
	if (leftIndex > -1)
		title = title.substring(leftIndex + 1);
	let rightIndex = title.indexOf('</');
	if (rightIndex > -1)
		title = title.substring(0, rightIndex);
	return title;
};
