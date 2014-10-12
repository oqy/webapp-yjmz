jQuery(document).ready(function() {
	jQuery('body').append('<div class="modal fade" id="imgViewModal" tabindex="-1" role="dialog" aria-labelledby="imgViewModalLabel" aria-hidden="true"><div class="modal-dialog"><div class="modal-content"><div class="modal-header"><button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button><h4 class="modal-title" id="imgViewModalLabel">Modal title</h4></div><div class="modal-body"><img class="img-responsive"/> </div><div class="modal-footer"><a id="viewOriginalImg" target="_blank" class="btn btn-primary" href="#">查看原图</a><button type="button" class="btn btn-default" data-dismiss="modal">关闭</button></div></div></div></div>');
	
	jQuery('a.modal-view').click(function(){
		jQuery('#imgViewModal .modal-body img').attr('src',jQuery(this).find('img').attr('src'));
		jQuery('#imgViewModalLabel').html(jQuery(this).find('img').attr('title'));
		jQuery('#imgViewModal #viewOriginalImg').attr('href',jQuery(this).attr('href'));
		jQuery('#imgViewModal').modal();
		return false;
	});
});