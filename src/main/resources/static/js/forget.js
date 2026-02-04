$(function(){
	$("#verifyCodeBtn").click(getVerifyCode);
});

function getVerifyCode() {
    var email = $("#your-email").val();

    if(!email) {
        alert("please enter your email first!");
        return false;
    }

	$.get(
	    CONTEXT_PATH + "/forget/code",
	    {"email":email},
	    function(data) {
	        data = $.parseJSON(data);
	        if(data.code == 0) {
                alert("verify code has been sent, please check your email");
	        } else {
                alert(data.msg);
	        }
	    }
	);
}