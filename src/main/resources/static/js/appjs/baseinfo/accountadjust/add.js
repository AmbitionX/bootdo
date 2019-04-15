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
		url : "/baseinfo/accountadjust/save",
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
			username : {
				required : true
			},
			isincome : {
				required: true
			},
			dealmoney : {
				required: true,number:true
			},
			remark : {
				required: true
			}
		},
		messages : {
			username : {
				required : icon + "请输入账号"
			},
			isincome : {
				required : icon + "请选择收支类型"
			},
			dealmoney : {
				required : icon + "请输入交易金额"
			},
			isincome : {
				required: icon + "请填写冲减原因"
			}
		}
	})
}