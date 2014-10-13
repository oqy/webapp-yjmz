// JavaScript Document
var callBackFunctionName;
jQuery(document).ready(function(){
	jQuery('body').append('<div class="modal fade" id="attachmentUploadModal" tabindex="-1" role="dialog" aria-labelledby="attachmentUploadModalLabel" aria-hidden="true"><div class="modal-dialog"><div class="modal-content"><div class="modal-header"><button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button><h4 class="modal-title" id="attachmentUploadModalLabel">上传附件</h4></div><div class="modal-body"><iframe id="attachmentUploadPage" frameborder="0" height="45" width="100%" src="attachmentUpload.html" hspace="0" vspace="0" marginheight="0" marginwidth="0" scrolling="no"></iframe></div><div class="modal-footer"><button type="button" name="attachmentUploadButton" id="attachmentUploadButton" class="btn btn-primary" data-loading-text="上传中，请稍后...">上传</button><button type="button" class="btn btn-default" data-dismiss="modal">取消</button></div></div></div></div>');
								
	jQuery(document).find('[class*=attachmentUpload]').not(':hidden').click(function(){
		var rulesParsing = jQuery(this).attr('class');
		var getCallBackFunctions = /attachmentUpload\[(.*)\]/.exec(rulesParsing);
		if (getCallBackFunctions === null)
			return false;
		
		var str = getCallBackFunctions[1];
		var rules = str.split(/\[|,|\]/);
		callBackFunctionName=rules[0];
		jQuery('#attachmentUploadModal').modal();
	});				
	
	jQuery('#attachmentUploadButton').click(function(){
		if(jQuery(document.getElementById('attachmentUploadPage').contentWindow.document.body).find('#uploadForm #file').val()==''){
			jQuery(document.getElementById('attachmentUploadPage').contentWindow.document.body).find('#uploadForm #file').focus();
			return false;
		}
		jQuery(document.getElementById('attachmentUploadPage').contentWindow.document.body).find('#uploadForm').submit();
		jQuery(this).button('loading')
	});
});

function attachmentUploadCallBack(attachmentId, attachmentName, attachmentUrl, isImage){
	jQuery('#attachmentUploadModal').modal('hide');
	jQuery('#attachmentUploadButton').button('reset')
	if(attachmentId!=null){
		eval(callBackFunctionName+"('"+attachmentId+"','"+attachmentName+"','"+attachmentUrl+"', '"+isImage+"')");
	}else{
		alert('抱歉，附件上传出错，请检查文件大小是否超过限定值，并稍后再试！');
	}
}
