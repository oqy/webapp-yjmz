// JavaScript Document
jQuery(document).ready(function() {
	jQuery('.workflow-config-diagram').click(function(){
		jQuery('#diagramModal #diagramModalLabel').html(jQuery(this).attr('data-config-title'));
		jQuery('#diagramModal .modal-body').html('<img class="img-responsive" src="workFlowConfigList.html?processDefinitionId='+jQuery(this).attr('data-config-id')+'"/>');
		jQuery('#diagramModal').modal('show');
		return false;
	});
});