const $ = (id) => document.getElementById(id);

const estado = { vehiculos: [] };
let temporizadorToast;

/** Escapa HTML para evitar inyección desde datos del dominio. */
function esc(valor) {
  return String(valor ?? '').replace(/[&<>"']/g, (caracter) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  })[caracter]);
}

/** Formatea un importe. */
function dinero(valor) {
  return '$' + Number(valor).toLocaleString('es-AR', {
    minimumFractionDigits: 2, maximumFractionDigits: 2
  });
}

/** Llama a la API y lanza un error legible cuando falla. */
async function api(url, opciones = {}) {
  const respuesta = await fetch(url, opciones);
  let datos;
  try {
    datos = await respuesta.json();
  } catch (error) {
    datos = { mensaje: 'El servidor devolvió una respuesta inválida.' };
  }
  if (!respuesta.ok) {
    throw new Error(datos.mensaje || 'Error ' + respuesta.status);
  }
  return datos;
}

/** Convierte un objeto en cuerpo form-urlencoded. */
function cuerpo(datos) {
  const parametros = new URLSearchParams();
  Object.entries(datos).forEach(([clave, valor]) => {
    if (valor !== null && valor !== undefined && valor !== '') {
      parametros.append(clave, valor);
    }
  });
  return parametros.toString();
}

const CABECERA = { 'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8' };

/** Muestra un aviso temporal. */
function aviso(mensaje, esError = false) {
  const nodo = $('toast');
  nodo.textContent = mensaje;
  nodo.classList.toggle('error', esError);
  nodo.classList.add('visible');
  clearTimeout(temporizadorToast);
  temporizadorToast = setTimeout(() => nodo.classList.remove('visible'), 3600);
}

/** Llena un select con opciones del enum. */
function llenarSelect(select, opciones, conservarPrimera = false) {
  const primera = conservarPrimera ? select.options[0].outerHTML : '';
  select.innerHTML = primera + opciones.map((opcion) =>
    `<option value="${esc(opcion.valor)}">${esc(opcion.etiqueta)}</option>`).join('');
}

/** Carga los enums expuestos por el servidor. */
async function cargarOpciones() {
  const opciones = await api('/api/opciones');
  llenarSelect($('filtroTipo'), opciones.tipos, true);
  llenarSelect($('filtroEstado'), opciones.estados, true);
  llenarSelect($('tipo'), opciones.tipos);
  llenarSelect($('estado'), opciones.estados);
  llenarSelect($('carroceria'), opciones.carrocerias);
  llenarSelect($('cabina'), opciones.cabinas);
  llenarSelect($('traccion'), opciones.tracciones);
  llenarSelect($('estilo'), opciones.estilos);
  actualizarCamposEspecificos();
}

/** Muestra solo los campos correspondientes al tipo y estado elegidos. */
function actualizarCamposEspecificos() {
  const tipo = $('tipo').value;
  $('campoCarroceria').hidden = tipo !== 'AUTOMOVIL';
  $('campoPuertas').hidden = tipo !== 'AUTOMOVIL';
  $('campoCabina').hidden = tipo !== 'CAMIONETA';
  $('campoTraccion').hidden = tipo !== 'CAMIONETA';
  $('campoCarga').hidden = tipo !== 'CAMIONETA';
  $('campoEstilo').hidden = tipo !== 'MOTOCICLETA';
  $('campoCilindrada').hidden = tipo !== 'MOTOCICLETA';
  $('campoKm').hidden = $('estado').value !== 'USADO';
}

/** Consulta el stock aplicando los filtros de la pantalla. */
async function listar() {
  const parametros = new URLSearchParams();
  if ($('filtroTipo').value) parametros.set('tipo', $('filtroTipo').value);
  if ($('filtroEstado').value) parametros.set('estado', $('filtroEstado').value);
  if ($('filtroMarca').value.trim()) parametros.set('marca', $('filtroMarca').value.trim());
  estado.vehiculos = await api('/api/vehiculos?' + parametros.toString());
  pintar();
}

/** Actualiza tarjetas, tabla y selector de simulación. */
function pintar() {
  const lista = estado.vehiculos;
  const nuevos = lista.filter((v) => v.estado === 'NUEVO').length;
  const valor = lista.reduce((suma, v) => suma + Number(v.precio), 0);
  $('statTotal').textContent = lista.length;
  $('statNuevos').textContent = nuevos;
  $('statUsados').textContent = lista.length - nuevos;
  $('statValor').textContent = dinero(valor);
  $('contador').textContent = lista.length + ' resultado(s)';

  if (lista.length === 0) {
    $('cuerpoTabla').innerHTML =
      '<tr><td colspan="9" class="vacio">No hay vehículos que coincidan con los filtros.</td></tr>';
  } else {
    $('cuerpoTabla').innerHTML = lista.map((v) => `
      <tr>
        <td><strong>${esc(v.id)}</strong></td>
        <td>${esc(v.marca)} ${esc(v.modelo)}</td>
        <td>${esc(v.anio)}</td>
        <td>${esc(v.color)}</td>
        <td><span class="pill ${v.estado === 'NUEVO' ? 'nuevo' : 'usado'}">${esc(v.estadoEtiqueta)}</span></td>
        <td>${esc(v.tipoEtiqueta)}</td>
        <td class="precio">${dinero(v.precio)}</td>
        <td>${dinero(v.impuesto)}</td>
        <td>
          <div class="acciones-tabla">
            <button class="btn pequeño" data-accion="ficha" data-id="${esc(v.id)}">Ficha</button>
            <button class="btn pequeño" data-accion="editar" data-id="${esc(v.id)}">Editar</button>
            <button class="btn pequeño" data-accion="simular" data-id="${esc(v.id)}">Simular</button>
            <button class="btn pequeño peligro" data-accion="eliminar" data-id="${esc(v.id)}">Baja</button>
          </div>
        </td>
      </tr>`).join('');
  }

  $('simularId').innerHTML = lista.map((v) =>
    `<option value="${esc(v.id)}">${esc(v.id)} — ${esc(v.marca)} ${esc(v.modelo)}</option>`).join('');
}

/** Abre un modal por id. */
function abrir(id) { $(id).hidden = false; }

/** Cierra un modal por id. */
function cerrar(id) { $(id).hidden = true; }

/** Configura los eventos de la interfaz. */
function cablear() {
  $('btnNuevo').addEventListener('click', () => abrir('modalAlta'));
  $('btnSimular').addEventListener('click', () => {
    $('resultadoSimulacion').hidden = true;
    abrir('modalSimular');
  });
  $('btnRecargar').addEventListener('click', async () => {
    try { await listar(); aviso('Stock actualizado.'); } catch (error) { aviso(error.message, true); }
  });
  $('btnFiltrar').addEventListener('click', async () => {
    try { await listar(); } catch (error) { aviso(error.message, true); }
  });
  $('btnLimpiar').addEventListener('click', async () => {
    $('filtroTipo').value = '';
    $('filtroEstado').value = '';
    $('filtroMarca').value = '';
    try { await listar(); } catch (error) { aviso(error.message, true); }
  });

  $('tipo').addEventListener('change', actualizarCamposEspecificos);
  $('estado').addEventListener('change', actualizarCamposEspecificos);

  document.querySelectorAll('[data-cerrar]').forEach((boton) => {
    boton.addEventListener('click', () => cerrar(boton.dataset.cerrar));
  });
  document.querySelectorAll('.modal').forEach((modal) => {
    modal.addEventListener('click', (evento) => {
      if (evento.target === modal) modal.hidden = true;
    });
  });

  $('cuerpoTabla').addEventListener('click', (evento) => {
    const boton = evento.target.closest('button[data-accion]');
    if (!boton) return;
    const vehiculo = estado.vehiculos.find((v) => v.id === boton.dataset.id);
    switch (boton.dataset.accion) {
      case 'ficha':
        $('fichaTexto').textContent = vehiculo.ficha;
        abrir('modalFicha');
        break;
      case 'editar':
        $('editarId').value = vehiculo.id;
        $('editarFicha').textContent = vehiculo.ficha;
        $('editarPrecio').value = vehiculo.precio;
        $('editarColor').value = vehiculo.color;
        abrir('modalEditar');
        break;
      case 'simular':
        $('simularId').value = vehiculo.id;
        $('resultadoSimulacion').hidden = true;
        abrir('modalSimular');
        break;
      case 'eliminar':
        eliminar(vehiculo);
        break;
    }
  });

  $('formAlta').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const datos = Object.fromEntries(new FormData(evento.target).entries());
    try {
      await api('/api/vehiculos', { method: 'POST', headers: CABECERA, body: cuerpo(datos) });
      cerrar('modalAlta');
      evento.target.reset();
      actualizarCamposEspecificos();
      await listar();
      aviso('Vehículo dado de alta correctamente.');
    } catch (error) {
      aviso(error.message, true);
    }
  });

  $('formEditar').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    const id = encodeURIComponent($('editarId').value);
    try {
      const precio = $('editarPrecio').value;
      const color = $('editarColor').value.trim();
      if (precio) {
        await api('/api/vehiculos/' + id + '/precio',
          { method: 'PUT', headers: CABECERA, body: cuerpo({ precio }) });
      }
      if (color) {
        await api('/api/vehiculos/' + id + '/color',
          { method: 'PUT', headers: CABECERA, body: cuerpo({ color }) });
      }
      cerrar('modalEditar');
      await listar();
      aviso('Modificación guardada.');
    } catch (error) {
      aviso(error.message, true);
    }
  });

  $('formSimular').addEventListener('submit', async (evento) => {
    evento.preventDefault();
    try {
      const resultado = await api('/api/simulacion', {
        method: 'POST', headers: CABECERA,
        body: cuerpo({ id: $('simularId').value, descuento: $('simularDescuento').value })
      });
      $('resultadoSimulacion').innerHTML = `
        <div><span>Precio base</span><strong>${dinero(resultado.precioBase)}</strong></div>
        <div><span>Descuento (${resultado.descuento}%)</span><strong>- ${dinero(resultado.precioBase - resultado.precioFinal)}</strong></div>
        <div><span>Precio final</span><strong>${dinero(resultado.precioFinal)}</strong></div>
        <div><span>Impuesto de patente</span><strong>${dinero(resultado.impuesto)}</strong></div>
        <div><span>Valor tasado</span><strong>${dinero(resultado.tasado)}</strong></div>
        <div class="total"><span>Total a cobrar</span><strong>${dinero(resultado.total)}</strong></div>`;
      $('resultadoSimulacion').hidden = false;
    } catch (error) {
      aviso(error.message, true);
    }
  });
}

/** Da de baja un vehículo previa confirmación. */
async function eliminar(vehiculo) {
  if (!confirm('¿Confirmar la baja de ' + vehiculo.id + ' — ' + vehiculo.marca + ' ' + vehiculo.modelo + '?')) return;
  try {
    await api('/api/vehiculos/' + encodeURIComponent(vehiculo.id), { method: 'DELETE' });
    await listar();
    aviso('Vehículo eliminado.');
  } catch (error) {
    aviso(error.message, true);
  }
}

/** Arranque de la aplicación. */
async function iniciar() {
  cablear();
  try {
    await cargarOpciones();
    await listar();
  } catch (error) {
    aviso('No se pudo conectar con el servidor: ' + error.message, true);
  }
}

iniciar();
