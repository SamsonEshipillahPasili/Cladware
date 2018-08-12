var btnHtml = "<span>&nbsp;&nbsp;&nbsp;</span><img src='img/btn_load.gif' /><span>&nbsp;&nbsp;&nbsp;</span>";
var btnHtml2 = "<span>&nbsp;&nbsp;&nbsp;</span><img src='/img/btn_load.gif' /><span>&nbsp;&nbsp;&nbsp;</span>";
// validate fields
function validateLogIn(e) {
    // get the target of the event 
    var userName = $("#username").val();
    var password = $("#password").val();
    if (userName && password) {
        var target = e.target;
        //$(target).html(btnHtml);
        logIn();
    } else {
        // notify user that all fields are required. 
        $("#loginMsg").fadeIn();
    }
}

// log in the user
function logIn(){
    $("#logInForm").submit();
}

function validateRegistration(e) {
    var fullname = $("#fullname").val();
    var email = $("#email").val();
    var password = $("#password").val();
    var confirmPassword = $("#confirmPassword").val();
    var gender = $("#gender").val();
    console.log(gender);

    if (fullname && email && password && confirmPassword) {
        if (fullname === "sam@gmail.com") {
            $("#registerMsg")
                    .text("Username already exists!").fadeIn();
        } else if (password !== confirmPassword) {
            $("#registerMsg")
                    .text("The passwords do not match!").fadeIn();
        } else if (gender === "-- Select Gender --") {
            $("#registerMsg")
                    .text("Please select the gender!").fadeIn();
        } else {
           $("#registrationForm").submit();
        }
    } else {
        // notify user that all fields are required. 
        $("#registerMsg").fadeIn();
    }
}

// show the contribute dialog box
function showContributeDialog(currentPerson) {
    swal({
        title: "Your Contribution",
        text: "Please note that you can only contribute once!",
        type: "input",
        showCancelButton: true,
        closeOnConfirm: false,
        inputPlaceholder: "Enter amount"
    }, function (inputValue) {
        if (inputValue === false)
            return false;
        if (inputValue === "") {
            alert("Enter the amount!");
            return false;
        }
        var url = "/Azra/contribute/" + inputValue;
        $.post(url, function(response){
            if(response === "Ok"){
               swal("Great!", "Your contribution was saved!", "success");
            }else{
                swal("Uh oh!", response, "error");
            }
        });
    });
}

// add a new user to the system
function addUser(event){
    //
    var name = $("#addNameField").val();
    var phoneNumber = $("#addPhoneField").val();
    var gender = $("#addGenderField").val();

    if(name && phoneNumber && gender){
        if(gender === "-- Gender --"){
            $("#addMemberMsg").text("Select the gender, please").fadeIn();
        }else{
            // submit the values to  REST endpoint to add the values
            var serverURL = "/Azra/addMember/" + name + "/" + phoneNumber + "/" + gender + "/";
            var form = $("#addForm");
            form.submit();
            //$(event.target).html(btnHtml2);
        }
    }else{
        $("#addMemberMsg").fadeIn();
    }
}

// delete a user
function deleteUser(event){
     var id = $(event.target).attr("data-username");
    swal({
      title: "Are you sure?",
      text: "The user with the id: " + id + " will be deleted permanently",
      type: "warning",
      showCancelButton: true,
      confirmButtonClass: "btn-danger",
      confirmButtonText: "Ok",
      closeOnConfirm: false
    },
    function(){
        id = "#" + id;
        $(id).click();
    });
}



// update the username
function updateUsername(){
      if($("#newUsernameUp").val() && $("#oldUsernameUp").val()){
           swal({
                  title: "Update your username",
                  text: "Once updated, you will not be able to update your username again.",
                  type: "warning",
                  showCancelButton: true,
                  confirmButtonClass: "btn-success",
                  confirmButtonText: "Okay",
                  closeOnConfirm: false
                },
                function(){
                    $("#updateUsernameBtn").click();
                });
      }else{
          $("#updateUsernameMsg").fadeIn();
      }
}


// update the password
function updatePassword(e){
    var oldPassword = $("#oldPassword").val();
    var newPassword = $("#newPassword").val();
    var newPasswordConfirm = $("#confirmPassword").val();

    if(oldPassword && newPassword && newPasswordConfirm){
        if(newPassword !== newPasswordConfirm){
            $("#updatePasswordMsg").text("The passwords do not match!");
            $("#updatePasswordMsg").fadeIn();
        }else{
            // continue with password update
            $("#updatePasswordForm").submit();
        }
    }else{
        $("#updatePasswordMsg").fadeIn();
    }
}

// update the username
function updatePhoneNumber(){
    var phoneNumber = $("#phoneNumberUp").val();
    var name = $("#nameUp").val();
    if(phoneNumber && name){
        $("#phoneNumberForm").submit();
    }else{
        $("#phoneNumberMsg").fadeIn();
    }
}

// track item photo changes
function trackPhotoChanges(event){
    var target = event.target;
    $("#itemPreview").attr("src", URL.createObjectURL(target.files[0]));
}

// function to validate adding an item
function validateAddItem(e){
    var code = $("#code").val();
    var name = $("#name").val();
    var unitPrice = $("#unitPrice").val();
    var quantity = $("#quantity").val();
    var description = $("#description").val();
    var image = $("#itemPicture").val();

    if(code && name && unitPrice && quantity && description && image){
         // all fields are set.
         $("#addItemForm").submit();
    }else{
        // issue warning message.
        $("#addItemMsg").fadeIn();
    }

}



// load item details
function loadItemDetails(e){
    var code = $(e.target).attr("data-code");
    var imageURI = "/item/img/" + code + "/";
    var url = "/item/" + code;
    $.getJSON(url, function(response){
        $("#modCode").val(response.code);
        $("#modName").val(response.name);
        $("#modUnitPrice").val(response.unitPrice);
        $("#modQuantity").val(response.quantity);
        $("#modQuantity").val(response.quantity);
        $("#modDescription").val(response.description);

        $("#modalImage").attr("src", imageURI);
    });
}


// track update item photo changes
function trackUpdateItemPhotoChanges(e){
    var target = e.target;
    $("#modalImage").attr("src", URL.createObjectURL(target.files[0]));
}

function updateItem(){
    $("#updateItemForm").submit();
}

// function to delete the specified item
function deleteItem(){
    // get the update item form
    var form = $("#updateItemForm");
    // change it's action to point to the 'deleteItem' endpoint
    form.attr("action", "/deleteItem");
    // submit the form
    form.submit();
}



