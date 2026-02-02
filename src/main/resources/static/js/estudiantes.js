document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("formEstudiante");

    if (!form) return;

    form.addEventListener("submit", function (e) {

        let nombre = document.getElementById("nombre").value.trim();
        let grado = document.getElementById("grado").value.trim();

        let errores = [];

        // ✅ Validaciones
        if (nombre.length < 3) {
            errores.push("Nombre debe tener al menos 3 caracteres");
        }

        if (grado.length < 2) {
            errores.push("Grado inválido");
        }

        // ✅ Si hay errores → detener envío
        if (errores.length > 0) {
            e.preventDefault();
            alert(errores.join("\n"));
            return;
        }

        // ✅ UX Semana 12 → feedback visual
        const btn = form.querySelector("button");

        if (btn) {
            btn.disabled = true;
            btn.innerText = "Guardando...";
        }

    });

});