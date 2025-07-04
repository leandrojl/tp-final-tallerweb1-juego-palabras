// buscarPartidas.js

console.log('Endpoint de búsqueda:', URL_BUSCAR);

const btn = document.getElementById("buscarPartidaBtn");
btn.addEventListener("click", function(e) {
    e.preventDefault();
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
                resultadoDiv.innerHTML = '<p class="text-dark fw-semibold p-3">No se encontraron partidas con ese nombre.</p>';
                return;
            }

            // Replicando los estilos de la tabla de lobby.html
            let tabla = `
<div class="table-responsive">
  <table class="table text-center mb-0 shadow-lg rounded-4 overflow-hidden" style="background: linear-gradient(120deg, #fff 0%, #ffe0d2 100%); border: 2.5px solid #ffb86c44;">
    <thead style="background: linear-gradient(90deg, #ffb86c 0%, #a4508b 100%); color: #fff; font-size: 1.1rem;">
      <tr>
        <th><i class="bi bi-hash"></i></th>
        <th><i class="bi bi-card-text"></i> Nombre</th>
        <th><i class="bi bi-translate"></i> Idioma</th>
        <th><i class="bi bi-stars"></i> Comodín</th>
        <th><i class="bi bi-arrow-repeat"></i> Rondas</th>
        <th><i class="bi bi-people-fill"></i> Max</th>
        <th><i class="bi bi-person"></i> Min</th>
        <th><i class="bi bi-activity"></i> Estado</th>
        <th></th>
      </tr>
    </thead>
    <tbody>`;
            data.forEach((p, i) => {
                tabla += `
    <tr style="transition: background 0.2s;"
        onmouseover="this.style.background='#ffb86c22';"
        onmouseout="this.style.background='rgba(255,255,255,0.95)';">
      <td class="fw-bold">${i+1}</td>
      <td class="fw-semibold">${p.nombre}</td>
      <td>${p.idioma}</td>
      <td>
        ${p.permiteComodin
                    ? '<span class="badge bg-warning text-dark"><i class="bi bi-star-fill"></i> Sí</span>'
                    : '<span class="badge bg-secondary">No</span>'}
      </td>
      <td>${p.rondasTotales}</td>
      <td>${p.maximoJugadores}</td>
      <td>${p.minimoJugadores}</td>
      <td>
        ${p.estado === 'ABIERTA'
                    ? '<span class="badge bg-success">Abierta</span>'
                    : `<span class="badge bg-danger">${p.estado}</span>`}
      </td>
      <td>
        <form th:action="@{/sala-de-espera}" method="post" class="d-inline">
                                                <input type="hidden" name="idPartida" th:value="${partida.id}" />
                                                <button type="submit" class="btn btn-sm btn-orange rounded-pill px-3 shadow-sm">
                                                    <i class="bi bi-door-open-fill me-1"></i> Unirse
                                                </button>
        </form>
      </td>
    </tr>`;
            });
            tabla += `</tbody></table></div>`;
            resultadoDiv.innerHTML = tabla;
        })
        .catch(err => console.error('Fetch error:', err));
});