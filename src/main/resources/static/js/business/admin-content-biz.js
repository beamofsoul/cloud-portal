loadTreeRootUrl = "permission/single"; // 获取树控件根节点的url
loadTreeRootParamValue = 1; // 获取树控件根节点数据需要参数值
loadTreeNodeUrl = 'permission/listOfChildren'; // 根据父节点获取树控件中子节点数据的url

let vueContentElementSelector = '#contentContainer'; // html页面中包含树控件标签的div对象id
let vueContentData = {treeData: generateRootNode()}; // 树控件初始化后将其所有数据交给vueContentObject对象的data对象控制
let vueContentComputed = {};
let vueContentMethods = {toggleExpand: toggleExpand, selectChange: selectChange, checkChange: checkChange};

function initializeContentOptions() {
	return {el: vueContentElementSelector, data: vueContentData, computed: vueContentComputed, methods: vueContentMethods};
}

vueContentObject = new Vue(initializeContentOptions());
//vueContentObject.$refs.tree.$children[0].handleExpand(toggleExpand); // 自动展开根节点