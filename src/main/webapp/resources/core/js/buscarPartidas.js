
    const btn = document.getElementById("buscarPartidaBtn");
    btn.addEventListener("click", function() {
    const nombre = document.getElementById("nombrePartida").value;

    fetch(`/lobby/buscarPartidaPorNombre?nombre=${encodeURIComponent(nombre)}`)
    .then(response => response.json())
    .then(data => {
    const resultadoDiv = document.getElementById("resultadoBusqueda");
    if (!data || data.length === 0) {
    resultadoDiv.innerHTML = '<p class="text-warning">No hay partidas para ese nombre</p>';
    return;
}

    let tabla = `
                    <table class="table table-dark table-striped text-center">
                        <thead>
                            <tr>
                                <th>#</th><th>Nombre de la Partida</th><th>Idioma</th><th>Permite comodin?</th><th>Ronda Totales</th><th>Maximo Jugadores</th><th>Minimo Jugadores</th><th>Estado</th>
                            </tr>
                        </thead><tbody>`;

    data.forEach((partida, index) => {
    tabla += `
                        <tr>
                            <td>${index + 1}</td>
                            <td>${partida.nombre}</td>
                            <td>${partida.idioma}</td>
                            <td>${partida.permiteComodin}</td>
                            <td>${partida.rondasTotales}</td>
                            <td>${partida.maximoJugadores}</td>
                            <td>${partida.minimoJugadores}</td>
                            <td>${partida.estado}</td>
                        </tr>`;
});
    tabla += `</tbody></table>`;
    resultadoDiv.innerHTML = tabla;
});
});
