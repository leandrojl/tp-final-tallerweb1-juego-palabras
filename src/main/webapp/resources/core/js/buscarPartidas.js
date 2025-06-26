
// buscarPartidas.js

console.log('Endpoint de búsqueda:', URL_BUSCAR);

const btn = document.getElementById("buscarPartidaBtn");
btn.addEventListener("click", function(e) {
    e.preventDefault();  // evita cualquier submit de formulario
    const nombre = document.getElementById("nombrePartida").value;

    const fetchUrl = `${URL_BUSCAR}?nombre=${encodeURIComponent(nombre)}`;
    console.log('Haciendo fetch a:', fetchUrl);

    fetch(fetchUrl)
        .then(res => {
            if (!res.ok) throw new Error('HTTP ' + res.status);
            return res.json();
        })
        .then(data => {
            console.log('Datos recibidos:', data);
            const resultadoDiv = document.getElementById("resultadoBusqueda");
            if (!data || data.length === 0) {
                resultadoDiv.innerHTML = '<p class="text-warning">No hay partidas para ese nombre</p>';
                return;
            }
            // Aquí tu lógica para pintar la tabla…
            let tabla = `
        <table class="table table-dark table-striped text-center">
          <thead>
            <tr>
              <th>#</th><th>Nombre</th><th>Idioma</th><th>Comodín?</th>
              <th>Rondas</th><th>Max Jug.</th><th>Min Jug.</th><th>Estado</th>
            </tr>
          </thead><tbody>`;
            data.forEach((p, i) => {
                tabla += `
          <tr>
            <td>${i+1}</td>
            <td>${p.nombre}</td>
            <td>${p.idioma}</td>
            <td>${p.permiteComodin}</td>
            <td>${p.rondasTotales}</td>
            <td>${p.maximoJugadores}</td>
            <td>${p.minimoJugadores}</td>
            <td>${p.estado}</td>
          </tr>`;
            });
            tabla += `</tbody></table>`;
            resultadoDiv.innerHTML = tabla;
        })
        .catch(err => console.error('Fetch error:', err));
});
