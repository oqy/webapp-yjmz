$(function () {
	// sidebar menu dropdown toggle
	$("#dashboard-menu .dropdown-toggle").click(function (e) {
		e.preventDefault();
		var $item = $(this).parent();
		$item.toggleClass("active");
		if ($item.hasClass("active")) {
		  $item.find(".submenu").slideDown("fast");
		} else {
		  $item.find(".submenu").slideUp("fast");
		}
	});
	
	
	// mobile side-menu slide toggler
	var $menu = $("#sidebar-nav");
	$("body").click(function () {
		if ($(this).hasClass("menu")) {
		  $(this).removeClass("menu");
		}
	});
	$menu.click(function(e) {
		e.stopPropagation();
	});
	$("#menu-toggler").click(function (e) {
		e.stopPropagation();
		$("body").toggleClass("menu");
	});
	$(window).resize(function() { 
		$(this).width() > 769 && $("body.menu").removeClass("menu")
	})


	// build all tooltips from data-attributes
	$("[data-toggle='tooltip']").each(function (index, el) {
		$(el).tooltip({
			placement: $(this).data("placement") || 'top'
		});
	});
	
	// toggle all checkboxes from a table when header checkbox is clicked
	$(".table th input:checkbox").click(function () {
		$checks = $(this).closest(".table").find("tbody input:checkbox");
		if ($(this).is(":checked")) {
			$checks.prop("checked", true);
		} else {
			$checks.prop("checked", false);
		}  		
	});
	
	// quirk to fix dark skin sidebar menu because of B3 border-box
	if ($("#sidebar-nav").height() > $(".content").height()) {
	  $("html").addClass("small");
	}


});