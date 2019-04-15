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
		url : "/baseinfo/applywithdrawinfo/save",
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
            phone : {
				required : true
			},
            applymoney : {
				number:true,min:10
            },
            paytype : {
                required : true
            },
            account : {
                required : true
            }
		},
		messages : {
            phone : {
                required : icon + "请输入收款账号姓名"
            },
            applymoney : {
				required : icon + "提现金额要大于等于10元"
			},
            paytype : {
                required : icon + "请选择支付方式"
            },
            account : {
                required : icon + "请输入收款账号"
            }
		}
	})
}