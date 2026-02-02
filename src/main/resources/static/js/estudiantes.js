document.getElementById("formEstudiante")
.addEventListener("submit", function(e){

    let n = document.getElementById("nombre").value.trim();
    let g = document.getElementById("grado").value.trim();

    if(n.length < 3){
        alert("Nombre muy corto");
        e.preventDefault();
    }

    if(g.length < 2){
        alert("Grado inválido");
        e.preventDefault();
    }

});