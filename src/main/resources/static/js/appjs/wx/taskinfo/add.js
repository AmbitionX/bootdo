$().ready(function() {
	validateRule();
});

$.validator.setDefaults({
	submitHandler : function() {
		save();
	}
});
function save() {
	$.ajax({
		cache : true,
		type : "POST",
		url : "/wx/taskinfo/save",
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
function validateRule() {
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
            }/*,
            taskperiod : {
                required : true
            }*/
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
            }/*,
            taskperiod : {
                required : icon + "请输入任务间隔"
            }*/
		}
	})
}