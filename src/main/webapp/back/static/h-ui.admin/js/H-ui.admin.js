/* -----------H-ui前端框架-------------
* H-ui.admin.js v3.1
* http://www.h-ui.net/
* Created & Modified by guojunhui
* Date modified 2017.02.03
* Copyright 2013-2017 北京颖杰联创科技有限公司 All rights reserved.
* Licensed under MIT license.
* http://opensource.org/licenses/MIT
*/

$.ajax({
		   url:'SystemBack/logincheck',
		   type:'get',
		   success:function(res){
		   }
});

var num=0,oUl=$("#min_title_list"),hide_nav=$("#Hui-tabNav");

/*获取顶部选项卡总长度*/
function tabNavallwidth(){
	var taballwidth=0,
		$tabNav = hide_nav.find(".acrossTab"),
		$tabNavWp = hide_nav.find(".Hui-tabNav-wp"),
		$tabNavitem = hide_nav.find(".acrossTab li"),
		$tabNavmore =hide_nav.find(".Hui-tabNav-more");
	if (!$tabNav[0]){return}
	$tabNavitem.each(function(index, element) {
        taballwidth += Number(parseFloat($(this).width()+60))
    });
	$tabNav.width(taballwidth+25);
	var w = $tabNavWp.width();
	if(taballwidth+25>w){
		$tabNavmore.show()}
	else{
		$tabNavmore.hide();
		$tabNav.css({left:0});
	}
}

/*左侧菜单响应式*/
function Huiasidedisplay(){
	if($(window).width()>=768){
		$(".Hui-aside").show();
	} 
}
/*获取皮肤cookie*/
function getskincookie(){
	var v = $.cookie("Huiskin");
	var hrefStr=$("#skin").attr("href");
	if(v==null||v==""){
		v="default";
	}
	if(hrefStr!=undefined){
		var hrefRes=hrefStr.substring(0,hrefStr.lastIndexOf('skin/'))+'skin/'+v+'/skin.css';
		$("#skin").attr("href",hrefRes);
	}
}
/*菜单导航*/
function Hui_admin_tab(obj){
	var bStop = false,
		bStopIndex = 0,
		href = $(obj).attr('data-href'),
		title = $(obj).attr("data-title"),
		topWindow = $(window.parent.document),
		show_navLi = topWindow.find("#min_title_list li"),
		iframe_box = topWindow.find("#iframe_box");
	//console.log(topWindow);
	if(!href||href==""){
		alert("data-href不存在，v2.5版本之前用_href属性，升级后请改为data-href属性");
		return false;
	}if(!title){
		alert("v2.5版本之后使用data-title属性");
		return false;
	}
	if(title==""){
		alert("data-title属性不能为空");
		return false;
	}
	show_navLi.each(function() {
		if($(this).find('span').attr("data-href")==href){
			bStop=true;
			bStopIndex=show_navLi.index($(this));
			return false;
		}
	});
	if(!bStop){
		creatIframe(href,title);
		min_titleList();
	}
	else{
		show_navLi.removeClass("active").eq(bStopIndex).addClass("active");			
		iframe_box.find(".show_iframe").hide().eq(bStopIndex).show().find("iframe").attr("src",href);
	}	
}

/*最新tab标题栏列表*/
function min_titleList(){
	var topWindow = $(window.parent.document),
		show_nav = topWindow.find("#min_title_list"),
		aLi = show_nav.find("li");
}

/*创建iframe*/
function creatIframe(href,titleName){
	var topWindow=$(window.parent.document),
		show_nav=topWindow.find('#min_title_list'),
		iframe_box=topWindow.find('#iframe_box'),
		iframeBox=iframe_box.find('.show_iframe'),
		$tabNav = topWindow.find(".acrossTab"),
		$tabNavWp = topWindow.find(".Hui-tabNav-wp"),
		$tabNavmore =topWindow.find(".Hui-tabNav-more");
	var taballwidth=0;
		
	show_nav.find('li').removeClass("active");	
	show_nav.append('<li class="active"><span data-href="'+href+'">'+titleName+'</span><i></i><em></em></li>');
	if('function'==typeof $('#min_title_list li').contextMenu){
		$("#min_title_list li").contextMenu('Huiadminmenu', {
			bindings: {
				'closethis': function(t) {
					var $t = $(t);				
					if($t.find("i")){
						$t.find("i").trigger("click");
					}
				},
				'closeall': function(t) {
					$("#min_title_list li i").trigger("click");
				},
			}
		});
	}else {
		
	}	
	var $tabNavitem = topWindow.find(".acrossTab li");
	if (!$tabNav[0]){return}
	$tabNavitem.each(function(index, element) {
        taballwidth+=Number(parseFloat($(this).width()+60))
    });
	$tabNav.width(taballwidth+25);
	var w = $tabNavWp.width();
	if(taballwidth+25>w){
		$tabNavmore.show()}
	else{
		$tabNavmore.hide();
		$tabNav.css({left:0})
	}	
	iframeBox.hide();
	iframe_box.append('<div class="show_iframe"><div class="loading"></div><iframe frameborder="0" src='+href+'></iframe></div>');
	var showBox=iframe_box.find('.show_iframe:visible');
	showBox.find('iframe').load(function(){
		showBox.find('.loading').hide();
	});
}



/*关闭iframe*/
function removeIframe(){
	var topWindow = $(window.parent.document),
		iframe = topWindow.find('#iframe_box .show_iframe'),
		tab = topWindow.find(".acrossTab li"),
		showTab = topWindow.find(".acrossTab li.active"),
		showBox=topWindow.find('.show_iframe:visible'),
		i = showTab.index();
	tab.eq(i-1).addClass("active");
	tab.eq(i).remove();
	iframe.eq(i-1).show();	
	iframe.eq(i).remove();
}

/*关闭所有iframe*/
function removeIframeAll(){
	var topWindow = $(window.parent.document),
		iframe = topWindow.find('#iframe_box .show_iframe'),
		tab = topWindow.find(".acrossTab li");
	for(var i=0;i<tab.length;i++){
		if(tab.eq(i).find("i").length>0){
			tab.eq(i).remove();
			iframe.eq(i).remove();
		}
	}
}

/*弹出层*/
/*
	参数解释：
	title	标题
	url		请求的url
	id		需要操作的数据id
	w		弹出层宽度（缺省调默认值）
	h		弹出层高度（缺省调默认值）
*/
function layer_show(title,url,w,h){
	if (title == null || title == '') {
		title=false;
	};
	if (url == null || url == '') {
		url="404.html";
	};
	if (w == null || w == '') {
		w=800;
	};
	if (h == null || h == '') {
		h=($(window).height() - 50);
	};
	layer.open({
		type: 2,
		area: [w+'px', h +'px'],
		fix: false, //不固定
		maxmin: true,
		shade:0.4,
		title: title,
		content: url
	});
}
/*关闭弹出框口*/
function layer_close(){
	var index = parent.layer.getFrameIndex(window.name);
	parent.layer.close(index);
}

/*时间*/
function getHTMLDate(obj) {
    var d = new Date();
    var weekday = new Array(7);
    var _mm = "";
    var _dd = "";
    var _ww = "";
    weekday[0] = "星期日";
    weekday[1] = "星期一";
    weekday[2] = "星期二";
    weekday[3] = "星期三";
    weekday[4] = "星期四";
    weekday[5] = "星期五";
    weekday[6] = "星期六";
    _yy = d.getFullYear();
    _mm = d.getMonth() + 1;
    _dd = d.getDate();
    _ww = weekday[d.getDay()];
    obj.html(_yy + "年" + _mm + "月" + _dd + "日 " + _ww);
};

$(function(){
	getHTMLDate($("#top_time"));
	getskincookie();
	//layer.config({extend: 'extend/layer.ext.js'});
	Huiasidedisplay();
	var resizeID;
	$(window).resize(function(){
		clearTimeout(resizeID);
		resizeID = setTimeout(function(){
			Huiasidedisplay();
		},500);
	});
	
	$(".nav-toggle").click(function(){
		$(".Hui-aside").slideToggle();
	});
	$(".Hui-aside").on("click",".menu_dropdown dd li a",function(){
		if($(window).width()<768){
			$(".Hui-aside").slideToggle();
		}
	});
	/*左侧菜单*/
	$(".Hui-aside").Huifold({
		titCell:'.menu_dropdown dl dt',
		mainCell:'.menu_dropdown dl dd',
	});
	
	/*选项卡导航*/
	$(".Hui-aside").on("click",".menu_dropdown a",function(){
		Hui_admin_tab(this);
	});
	
	$(document).on("click","#min_title_list li",function(){
		var bStopIndex=$(this).index();
		var iframe_box=$("#iframe_box");
		$("#min_title_list li").removeClass("active").eq(bStopIndex).addClass("active");
		iframe_box.find(".show_iframe").hide().eq(bStopIndex).show();
	});
	$(document).on("click","#min_title_list li i",function(){
		var aCloseIndex=$(this).parents("li").index();
		$(this).parent().remove();
		$('#iframe_box').find('.show_iframe').eq(aCloseIndex).remove();	
		num==0?num=0:num--;
		tabNavallwidth();
	});
	$(document).on("dblclick","#min_title_list li",function(){
		var aCloseIndex=$(this).index();
		var iframe_box=$("#iframe_box");
		if(aCloseIndex>0){
			$(this).remove();
			$('#iframe_box').find('.show_iframe').eq(aCloseIndex).remove();	
			num==0?num=0:num--;
			$("#min_title_list li").removeClass("active").eq(aCloseIndex-1).addClass("active");
			iframe_box.find(".show_iframe").hide().eq(aCloseIndex-1).show();
			tabNavallwidth();
		}else{
			return false;
		}
	});
	tabNavallwidth();
	
	$('#js-tabNav-next').click(function(){
		num==oUl.find('li').length-1?num=oUl.find('li').length-1:num++;
		toNavPos();
	});
	$('#js-tabNav-prev').click(function(){
		num==0?num=0:num--;
		toNavPos();
	});
	
	function toNavPos(){
		oUl.stop().animate({'left':-num*100},100);
	}
	
	/*换肤*/
	$("#Hui-skin .dropDown-menu a").click(function(){
		var v = $(this).attr("data-val");
		$.cookie("Huiskin", v);
		var hrefStr=$("#skin").attr("href");
		var hrefRes=hrefStr.substring(0,hrefStr.lastIndexOf('skin/'))+'skin/'+v+'/skin.css';
		$(window.frames.document).contents().find("#skin").attr("href",hrefRes);
	});
}); 

$.ajaxSetup( {
	//设置ajax请求结束后的执行动作
	complete : 
	function(XMLHttpRequest, textStatus) {
	// 通过XMLHttpRequest取得响应头，sessionstatus
	var sessionstatus = XMLHttpRequest.getResponseHeader("sessionstatus");
	if (sessionstatus == "TIMEOUT") {
	var win = window;
	while (win != win.top){
	win = win.top;
	}
	win.location.href= XMLHttpRequest.getResponseHeader("CONTEXTPATH");
	}
  }
});

function setTableList(id,pageInfo,pageCallback){
	   var data=pageInfo.list; 
	   $("#"+id+" tbody tr").remove();
	   if(!data) return;
	   var html="";
	   $.each(data,function(index,item){
		   html+="<tr class='text-c'>";
		   
		   //找出所有 需要带的参数
		   var parm={};
		   $("#"+id+" thead tr th").each(function(){
			   var key=$(this).attr('code');
			   if($(this).attr("parm")) parm[key]=item[key];
		   });
		   
		   
		   $("#"+id+" thead tr th").each(function(){
			   //type="checkbox"
			   if($(this).find("input").length>0){
				   html+="<td><input type='checkbox' value='1' name=''></td>"; 
				   return true;
			   }
			   var key=$(this).attr('code');
			   //单个图片列
			   if($(this).attr("isimg")){
				   var url=item[key];
				   html+="<td  href='javascript:;'><img onclick='img_show(\""+url+"\")' style='width:100px;height:100px;cursor:pointer;' src="+item[key]+" ></td>";  
				   return true;
			   }
			   
			   //多个图片
			   if($(this).attr("isimgarr")){
				   var imgList=item[key]; 
				   html+='<td  class="maincolor"><a onclick="imgarr_show('+JSON.stringify(imgList).replace(/"/g,"&"+"#34")+')" >查看</a></td>';  
				   return true;
			   }
			   //隐藏的列
			   if($(this).is(":hidden")){
				   html+="<td style='display:none'>"+(item[key])+"</td>"; 
				   return true;
			   }
			   
			   
			   
			   //按钮列
			   var oper=$(this).attr("oper");
			   if(oper!=undefined){
				  var operarr=oper.split(",");
				  
				  html+="<td>";
				  for(var i=0;i<operarr.length;i++){
					  if(operarr[i]=="edit"){
						  html+='<a style="text-decoration:none" onclick="'+id+'_edit('+JSON.stringify(parm).replace(/"/g,"&"+"#34")+')" class="ml-5"  href="javascript:;" title="编辑"><i class="Hui-iconfont">&#xe6df;</i></a>';
					  }
					  if(operarr[i]=="delete"){
						  html+='<a style="text-decoration:none" onclick="'+id+'_delete('+JSON.stringify(parm).replace(/"/g,"&"+"#34")+')" class="ml-5"  href="javascript:;" title="删除"><i class="Hui-iconfont">&#xe6e2;</i></a>';
					  }
				  }
				  html+="</td>";
				  return true;
			   }
			   
			   html+="<td>"+(key==undefined?'':item[key])+"</td>";   
		   });
		   html+="</tr>";
	   })
	   $("#"+id+" tbody").html(html);
	   
	   $("#total").html(pageInfo.total);
	   //分页
	   if(pageCallback){
		   var html="<div class='dataTables_length' style='float: left;margin-top: 20px;margin-bottom:30px;font-size: 14px;color: #888;'  id='DataTables_Table_0_length'>"+
					"	 <label>显示 "+
					"		<select id="+id+"_select onchange="+id+"_pageSizeChange(this.value) style='width: 56px;height: 26px;border: 1px solid #c5b7b7;' name='DataTables_Table_0_length' aria-controls='DataTables_Table_0' class='select'>"+
					"		   <option value='5'>5</option>"+
					"		   <option value='10'>10</option>"+
					"		   <option value='25'>25</option>"+
					"		   <option value='50'>50</option>"+
					"		   <option value='100'>100</option>"+
					"		 </select> 条"+
					"	  </label>"+
					"	</div>"+
					"	<div id='"+id+"paginationbox' class='paginationbox'>"+
					"			<div  class='page fl'></div>"+
					"	</div>";
		   
	   }
	   $("#"+id).parent().find("div.dataTables_length").remove();
	   $("#"+id+"paginationbox").remove();
	   
	   $("table#"+id).after(html);
	   
	   $("#"+id).parent().find("div.dataTables_length select option[value="+pageInfo.pageSize+"]").attr("selected",true);
	   
	   $("#"+id+"paginationbox div").pagination({
			currentPage: pageInfo.pageNum,
			totalPage: pageInfo.pages,
			isShow: true,
			count: 5,
			homePageText: "首页",
			endPageText: "尾页",
			prevPageText: "上一页",
			nextPageText: "下一页",
			callback: function(current) {
				pageCallback($("#"+id).parent().find("div.dataTables_length select option:selected").val(),current);
			}
		});
}

/*img 查看*/
function img_show(url){
	   var json={
			   "title": "", //相册标题
			   "id": 123, //相册id
			   "start": 0, //初始显示的图片序号，默认0
			   "data": [   //相册包含的图片，数组格式
			     {
			       "alt": "图片",
			       "pid": 666, //图片id
			       "src": url, //原图地址
			       "thumb": "" //缩略图地址
			     }
			   ]
			 }
	      layer.photos({
		    photos: json
		    ,anim: 5 
		  });
}

function imgarr_show(arr){
	   var json={
			   "title": "", //相册标题
			   "id": 123, //相册id
			   "start": 0, //初始显示的图片序号，默认0
			   "data": arr
			 }
	      layer.photos({
		    photos: json
		    ,anim: 5 
		  });
}
