// JavaScript Document
jQuery(document).ready(function() {
	jQuery('#add-entry').click(function(){
		entryIndex+=1;
		jQuery('tbody').append('<tr><td class="col-md-3"><input type="text" class="form-control" id="name'+entryIndex+'" name="entry['+entryIndex+'].name" placeholder="请输入品名"/></td><td class="col-md-2"><input type="text" class="form-control" id="standard'+entryIndex+'" name="entry['+entryIndex+'].standard" placeholder="请输入规格"/></td><td class="col-md-2"><input type="text" class="form-control" id="quantity'+entryIndex+'" name="entry['+entryIndex+'].quantity" placeholder="请输入数量"/></td><td class="col-md-2"><input type="text" class="form-control" id="unitPrice'+entryIndex+'" name="entry['+entryIndex+'].unitPrice" placeholder="请输入单价"/></td><td class="col-md-2"><input type="text" class="form-control" id="remark'+entryIndex+'" name="entry['+entryIndex+'].remark" placeholder="请输入备注"/></td><td class="col-md-1"><a role="button" href="#" class="btn btn-danger btn-sm rm-entry" title="删除采购项"><span class="icon-minus"></span></a></td></tr>');
		return false;
	});
	
	jQuery('tbody').on("click",'a.rm-entry', function() {
  		jQuery(this).parent().parent().remove();
		return false;
	});
});