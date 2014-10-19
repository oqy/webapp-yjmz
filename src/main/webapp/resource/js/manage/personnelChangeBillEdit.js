// JavaScript Document
jQuery(document).ready(function() {
	jQuery('.datetimepicker').datetimepicker({
		format: 'YYYY-MM-DD',
	    pickTime: false
	});
	
	jQuery('#changeType').change(function(){
		if(jQuery(this).val()=='4'){
			jQuery('#otherChangeType').parent().show();
		}else{
			jQuery('#otherChangeType').parent().val('').hide();
		}
	});
	
	jQuery('#oriSalary,#salaryChangeAmount').keyup(function(){
		calculateNewSalary();
	});
	
	jQuery('#addSalary').change(function(){
		calculateNewSalary();
	});
});
function calculateNewSalary(){
	var oriSalary = Number(jQuery('#oriSalary').val());
	var salaryChangeAmount = Number(jQuery('#salaryChangeAmount').val());
	jQuery('#newSalary').val(jQuery('#addSalary').val()=='1'?oriSalary+salaryChangeAmount:oriSalary-salaryChangeAmount);
}