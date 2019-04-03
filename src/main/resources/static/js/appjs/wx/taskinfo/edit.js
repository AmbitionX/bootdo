$().ready(function() {
	var tasktype = $('#tasktype').val();
	if(tasktype =="1"){
		validateRuleRead();
	}else if (tasktype == "3"){
		validataConcern();
	}
});

function selectChange() {
	var tasktype = $('#tasktype').val();
	if(tasktype == "1"){
		var tasktype = $('#tasktype').val();
		validateRuleRead();
	}else if (tasktype == "3"){
		validataConcern();
	}

}
$.validator.setDefaults({
	submitHandler : function() {
		update();
	}
});
function update() {

	var tasktype = $('#tasktype').val();
	if(tasktype == "1"){
		var url = $('#url').val();
		if(url==""){
			parent.layer.alert("请输入 阅读链接url ");
			return;
		}
	}else if (tasktype == "3"){
		var wxid = $('#wxid').val();
		if(wxid==""){
			parent.layer.alert("请输入 公众号ID ");
			return;
		}
	}

	$.ajax({
		cache : true,
		type : "POST",
		url : "/wx/taskinfo/update",
		data : $('#signupForm').serialize(),// 你的formid
		async : false,
		error : function(request) {
			parent.layer.alert("Connection error");
		},
		success : function(data) {
			if (data.code == 0) {
				parent.layer.msg("操作成功");
				parent.reLoad();
				var index = parent.layer.getFrameIndex(window.name); // 获取窗口索引
				parent.layer.close(index);

			} else {
				parent.layer.alert(data.msg)
			}

		}
	});

}
function validateRuleRead() {
	document.getElementById("wxiddiv").style.display="none";//隐藏
	document.getElementById("urldiv").style.display="";//显示
	var icon = "<i class='fa fa-times-circle'></i> ";
	$("#signupForm").validate({
		rules : {
			url : {
				required : true
			},
			tasktype : {
				required : true
			},
			price : {
				required : true
			},
			num : {
				required : true
			},
			taskperiod : {
				required : true
			},wxid : {
				required : false
			}
		},
		messages : {
			url : {
				required : icon + "请输入链接url"
			},
			tasktype : {
				required : icon + "请选择任务类型"
			},
			price : {
				required : icon + "请输入单价"
			},
			num : {
				required : icon + "请输入操作数量"
			},
			taskperiod : {
				required : icon + "请输入任务间隔"
			},
			wxid : {
				required : icon + "请输入公众号ID"
			}
		}
	})
}

function validataConcern() {
	document.getElementById("urldiv").style.display="none";//隐藏
	document.getElementById("wxiddiv").style.display="";//显示
	var icon = "<i class='fa fa-times-circle'></i> ";
	$("#signupForm").validate({
		rules : {
			url : {
				required : false
			},
			tasktype : {
				required : true
			},
			price : {
				required : true
			},
			num : {
				required : true
			},
			taskperiod : {
				required : true
			},wxid : {
				required : true
			}
		},
		messages : {
			url : {
				required : icon + "请输入链接url"
			},
			tasktype : {
				required : icon + "请选择任务类型"
			},
			price : {
				required : icon + "请输入单价"
			},
			num : {
				required : icon + "请输入操作数量"
			},
			taskperiod : {
				required : icon + "请输入任务间隔"
			},
			wxid : {
				required : icon + "请输入公众号ID"
			}
		}
	})
}