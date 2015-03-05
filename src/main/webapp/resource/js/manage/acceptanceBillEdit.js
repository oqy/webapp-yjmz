// JavaScript Document
function uploadCallBack(attachmentId, attachmentName, attachmentUrl, isImage){
	jQuery('#attachmentWrapper').append('<li><span class="'+(isImage=='true'?'icon-picture':'icon-file-text')+'"></span>&nbsp;<a target="_blank" href="'+attachmentUrl+'">'+attachmentName+'</a><input type="hidden" name="attachments" value="'+attachmentId+'" />&nbsp;<a class="deleteAttachment text-danger" href="#" title="删除"><span class="icon-remove"></span></a></li>');
}

jQuery(document).ready(function() {
	jQuery('body').on("click",'a.deleteAttachment', function() {
		if(window.confirm('确定删除所选附件资源？')){
			jQuery(this).parent().remove();
		}
		return false;
	});
});