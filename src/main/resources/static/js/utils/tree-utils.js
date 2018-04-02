/**
 * 加载树控件根节点需要提供的数据
 */
var loadTreeRootUrl = null;
var loadTreeRootParamValue = null;
var loadTreeRootCallback = (content, obj) => {
	obj.countOfChildren = 1;
	content.push(parseNode(obj));
};

/**
 * 加载树控件根节点下子节点需要提供的数据
 */
var loadTreeNodeUrl = null;
var loadTreeNodeCallback = (content, children) => {
	children.map((each, index, array) => {
		content.push(parseNode(each));
	});
};
//树是否有选中的节点
//var hasCheckedNode;
var checkedNodesObject;
//selected 树节点
var selectedNodeObject;

/**
 * 将输入数据解析成树控件node节点
 */
var parseNode = data => ({id: data.id, expand: data.expand, title: data.name, children: data.countOfChildren != 0 ? [{}] : null});

/**
 * 生成并返回生成的树控件根节点 
 */
var generateRootNode = () => {
	if (!isEverythingOK4Generation()) return;
	var content = [];
	$.igety(loadTreeRootUrl + "/" + loadTreeRootParamValue, null, data => loadTreeRootCallback(content, data), null, (arguments) => currentRequestMappingRootPath = null);
	return content;
}

/**
 * 检查时候生成树控件根节点所需的参数都已准备完毕
 * @return 准备完毕则返回true, 否则返回false
 */
let isEverythingOK4Generation = () => {
	let currentScript = "tree-utils.js";
	let currentFunction = "generateRootNode()";
	let consoleErrorMessage = null;
	
	if (!loadTreeRootUrl) {
		consoleErrorMessage = '初始化树控件时, 未获得根节点访问地址';
	} else if (!loadTreeRootParamValue) {
		consoleErrorMessage = '初始化树控件时, 未获得根节点访问所需参数值';
	}
	
	if (consoleErrorMessage) {
		consoles(consoleErrorMessage, "error", currentScript, currentFunction);
		return false;
	}
	return true;
}

/**
 * 根据输入的父节点加载其下子节点
 * @param parent 父节点
 */
var toggleExpand = parent => {
	if(!parent.expand) {
		//收缩时不需要重新加载数据
		return;
	} else {
		if (!isEverythingOK4LoadChildrenNodes(parent)) return;
		//展开时将新加载数据节点对象覆盖原有对象 ，并将原有对象节点选中状态 赋给新节点对象
		let content = [];
		$.igety(loadTreeNodeUrl + "/" + parent.id, null, data => loadTreeNodeCallback(content,data), null, (arguments) => currentRequestMappingRootPath = null);
		content.map(child => {
			if(selectedNodeObject)
				if(child.id == selectedNodeObject.id)
					child.selected = true;
			
			if(checkedNodesObject)
				checkedNodesObject.map(node => child.checked = node.id === child.id ? true : child.checked);
		});
		parent.children = content;
	}
}

/**
 * 检查时候为树控件某个节点加载其子节点所需的参数都已准备完毕
 * @return 准备完毕则返回true, 否则返回false
 */
let isEverythingOK4LoadChildrenNodes = parent => {
	let currentScript = "tree-utils.js";
	let currentFunction = "toggleExpand(parent)";
	let consoleErrorMessage = null;
	
	if (!loadTreeNodeUrl) {
		consoles('加载节点[' + parent.title + ']的子节点时, 未获得子根节点访问地址', "error", currentScript, currentFunction);
		return false;
	}
	return true;
}

/**
 * 当树控件任何节点被点选中时发生的事件
 * @param node 被点选的节点
 */
var selectChange = node => alert(`selectChange: ${JSON.stringify(node)}`);

/**
 * 当树控件任何节点前复选框被选中时发生的事件
 * @param nodes 一个到多个被点选中复选框的节点集合
 */
var checkChange = nodes => {
	checkedNodesObject = nodes;
//	hasCheckedNode = nodes.length > 0;
};

/**
 * 获取被鼠标选中的节点(非复选框被勾选)
 * @param nodes 被选中的节点集合
 */
var getSelectedNodes = nodes => alert(`selectedNodes: ${JSON.stringify(nodes)}`);

/**
 * 获取复选框被选中的节点集合
 * @param nodes 被选中复选框的节点集合
 */
var getCheckedNodes = nodes => alert(`checkedNodes: ${JSON.stringify(nodes)}`);

/**
 * 根据输入的节点id在某一个节点下获取节点对象，并在原有父节点下删除其数据对象
 * @param id 要查找的节点id
 * @param node 包含该子节点的节点对象
 * @returns 找到的节点对象
 */
var getChildFromNode = (id, node) => {
	let target = null;
	let inCurrentNode = false;
	const children = node.children;
	for (let r in children) {
		let child = children[r];
		if (child.id == id) {
			target = child;
			inCurrentNode = true;
		} else {
			target = getChildFromNode(id, child);
		}
		if (target != null) {
			if (inCurrentNode) {
				node.children.splice(r,1);
				if (node.children.length == 0) node.children = null;
			}
			return target;
		}
	}
}


/**
 * 根据输入的节点id在某一个节点下获取节点对象
 * @param id 要查找的节点id
 * @param node 包含该子节点的节点对象
 * @returns 找到的节点对象
 */
var getChildFromNodeNotDelete = (id, node) => {
	let target = null;
	const children = node.children;
	for (let r in children) {
		let child = children[r];
		if (child.id == id) target = child;
		else target = getChildFromNodeNotDelete(id, child);
		
		if (target != null) {
			return target;
		}
	}
}

/**
 * 根据输入的父节点id在输入的节点对象中查找该父节点对象的位置，并将输入的子节点插入到该父节点对象下
 * @param child 将被插入的子节点对象
 * @param parentId 父节点id
 * @param node 包含该父节点的节点对象
 */
var setChildToNode = (child, parentId, node) => {
	if (node.id != parentId) {
		let children = node.children;
		if (children) {
			for(let r in children) {
				setChildToNode(child, parentId, children[r]);
			}
		}
	} else {
		if (node.children) {
			node.children.push(child);
		} else {
			node.children = [child];
		}
	}
}

/**
 * 根据输入的树对象(来自ref)，禁用其checkbox自动选择上级功能
 * @param obj - 树对象
 */
function disableUpdateData(obj) {
	obj.updateData = function() {};
}

/**
 * 获取树勾选的节点 标题名称
 * @returns 所有勾选的节点名称数组
 */
function getTreeCheckedNodesTitle(){
	let checkedNodesTitleArray = [];
	if(checkedNodesObject)  checkedNodesObject.map(oneCheckedNode => checkedNodesTitleArray.push(oneCheckedNode.title));
	return checkedNodesTitleArray;
}

/**
 * 获取树勾选的节点 id
 * @returns 所有勾选的节点id
 */
function getTreeCheckedNodesId(){
	let checkedNodesIdArray = [];
	if(checkedNodesObject)  checkedNodesObject.map(oneCheckedNode => checkedNodesIdArray.push(oneCheckedNode.id));
	return checkedNodesIdArray;
}

/**
 * 父节点下是否有未勾选的子节点
 * @returns 
 */
function hasNotCheckedChildInParent(){
	let checkedParentNodesIdArray = [];
	let checkedChildNodesIdArray = [];
	if(checkedNodesObject)  checkedNodesObject.map(oneCheckedNode => {
		if(oneCheckedNode.id){
			if(oneCheckedNode.children){
				checkedParentNodesIdArray.push(oneCheckedNode.id);
			}else{
				checkedChildNodesIdArray.push(oneCheckedNode.id);
			}
		}
	});
	return {parentId:checkedParentNodesIdArray,childId:checkedChildNodesIdArray};
}