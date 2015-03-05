// JavaScript Document
jQuery(document).ready(function() {
	jQuery('.mark-read').click(function() {
		jQuery.getJSON('markMessageRead.do?messageId='+jQuery(this).attr('id'));
		jQuery(this).parent().parent().find('.badge').remove();
		var unReadCount = Number(jQuery('.navbar .count').html());
		if(unReadCount>1){
			jQuery('.navbar .count').html(Number(jQuery('.navbar .count').html())-1);
		}else{
			jQuery('.navbar .count').remove();
		}
	});
});