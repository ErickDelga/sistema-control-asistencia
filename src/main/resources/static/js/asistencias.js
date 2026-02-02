document.getElementById("formAsistencia")
.addEventListener("submit", function(e){

    if(document.getElementById("estudianteId").value === ""){
        alert("Seleccione estudiante");
        e.preventDefault();
    }

});