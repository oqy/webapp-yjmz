// JavaScript Document
jQuery(document).ready(function() {
	jQuery('#add-entry').click(function(){
		entryIndex+=1;
		jQuery(this).closest('.form-group').before('<div class="form-group"><label class="col-md-2 control-label">采购产品：</label><div class="col-md-6"><table class="table table-hover"><tr><td>品名&nbsp;<span class="text-danger">*</span></td><td><input type="text" class="form-control required" id="name'+entryIndex+'" name="entry['+entryIndex+'].name" placeholder="请输入品名"/></td><td width="38"><button title="删除采购项" class="btn btn-danger btn-small rm-entry"><span class="icon-remove"></span></button></td></tr><tr><td>规格</td><td colspan="2"><input type="text" class="form-control" id="standard'+entryIndex+'" name="entry['+entryIndex+'].standard" placeholder="请输入规格"/></td></tr><tr><td><span class="text-danger">*</span>&nbsp;申请采购量</td><td colspan="2"><input type="text" class="form-control required number" id="quantity'+entryIndex+'" name="entry['+entryIndex+'].quantity" placeholder="请输入数量"/></td></tr><tr><td>单价（元）</td><td colspan="2"><input type="text" class="form-control number" id="unitPrice'+entryIndex+'" name="entry['+entryIndex+'].unitPrice" placeholder="请输入单价"/></td></tr><tr><td>备注</td><td colspan="2"><input type="text" class="form-control" id="remark'+entryIndex+'" name="entry['+entryIndex+'].remark" placeholder="请输入备注"/></td></tr></table></div></div>');
		return false;
	});
	
	jQuery('form').on("click",'button.rm-entry', function() {
		if(jQuery('.form-group table').length>1){
  			jQuery(this).closest('.form-group').remove();
  		}
		return false;
	});
	
	jQuery('body').on("click",'a.deleteAttachment', function() {
		if(window.confirm('确定删除所选附件资源？')){
			jQuery(this).parent().remove();
		}
		return false;
	});
});

function uploadCallBack(attachmentId, attachmentName, attachmentUrl, isImage){
	jQuery('#attachmentWrapper').append('<li><span class="'+(isImage=='true'?'icon-picture':'icon-file-text')+'"></span>&nbsp;<a target="_blank" href="'+attachmentUrl+'">'+attachmentName+'</a><input type="hidden" name="attachments" value="'+attachmentId+'" />&nbsp;<a class="deleteAttachment text-danger" href="#" title="删除"><span class="icon-remove"></span></a></li>');
}