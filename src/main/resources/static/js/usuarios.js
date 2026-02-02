document.getElementById("formUsuario")
.addEventListener("submit", function(e){

    if(username.value.length < 4){
        alert("Usuario inválido");
        e.preventDefault();
    }

});
