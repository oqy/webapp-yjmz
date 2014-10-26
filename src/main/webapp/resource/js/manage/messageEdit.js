// JavaScript Document
jQuery(document).ready(function() {
	jQuery('input[name="massSending"]').click(function() {
		if(jQuery(this).attr('id')=='select-department'){
			jQuery('.user-selection').hide().find('input').attr('disabled',true);
			jQuery('.department-selection').show().find('input').attr('disabled',false);
		}else if(jQuery(this).attr('id')=='select-user'){
			jQuery('.department-selection').hide().find('input').attr('disabled',true);
			jQuery('.user-selection').show().find('input').attr('disabled',false);
		}else{
			jQuery('.department-selection').hide().find('input').attr('disabled',true);
			jQuery('.user-selection').hide().find('input').attr('disabled',true);
		}
	});
});