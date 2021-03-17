function order(){

}

$(document).ready(function() {
	$('.btn-cartin-act').click(function(){
		var num = $(this).attr('data-num');
		var ordernum = $('#ordernum').val();
		var procode = $('#procode'+num).val();
		var qty = $('#qty'+num).val();
		$.ajax({
		   type: 'POST',
		   datatype: 'json',
		   data:{procode:procode,qty:qty,ordernum:ordernum},
		   url: 'orderInsert',
		   success: function(data) {
		      alert("담기가 완료되었습니다.");
		   },
		   error: function(xhr, status, error) {
		      alert('ajax error : ' + xhr.status + error);
		   }
		});

	});
	
	$('#nofirmorderlist').DataTable({
		deferRender: true,
		scrollY: 360,
		scrollCollapse: true
	});



});