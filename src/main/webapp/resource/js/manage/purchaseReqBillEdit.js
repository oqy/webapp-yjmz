// JavaScript Document
jQuery(document).ready(function() {
	jQuery('#add-entry').click(function(){
		entryIndex+=1;
		jQuery('tbody').append('<tr><td class="col-md-3"><input type="text" class="form-control required" id="name'+entryIndex+'" name="entry['+entryIndex+'].name" placeholder="请输入品名"/></td><td class="col-md-2"><input type="text" class="form-control" id="standard'+entryIndex+'" name="entry['+entryIndex+'].standard" placeholder="请输入规格"/></td><td class="col-md-2"><input type="text" class="form-control required number" id="quantity'+entryIndex+'" name="entry['+entryIndex+'].quantity" placeholder="请输入数量"/></td><td class="col-md-2"><input type="text" class="form-control required number" id="unitPrice'+entryIndex+'" name="entry['+entryIndex+'].unitPrice" placeholder="请输入单价"/></td><td class="col-md-2"><input type="text" class="form-control" id="remark'+entryIndex+'" name="entry['+entryIndex+'].remark" placeholder="请输入备注"/></td><td class="col-md-1"><a role="button" href="#" class="btn btn-danger btn-sm rm-entry" title="删除采购项"><span class="icon-minus"></span></a></td></tr>');
		return false;
	});
	
	jQuery('tbody').on("click",'a.rm-entry', function() {
		if(jQuery('tbody tr').length>1){
  			jQuery(this).parent().parent().remove();
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
	jQuery('#attachmentWrapper').append('<li><span class="'+(isImage=='true'?'icon-picture':'icon-file-text')+'"></span>&nbsp;<a target="_blank" href="'+attachmentUrl+'">'+attachmentName+'</a><input type="hidden" name="attachments" value="'+attachmentId+'" />&nbsp;<a class="deleteAttachment text-danger" href="#" title="ɾ��"><span class="icon-remove"></span></a></li>');
}