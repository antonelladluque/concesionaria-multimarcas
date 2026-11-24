# Genera un archivo stock.csv de demostración con datos variados y válidos
# para el sistema de gestión de la concesionaria.
# Uso: powershell -ExecutionPolicy Bypass -File scripts/generar-stock-demo.ps1

$ErrorActionPreference = 'Stop'

$ruta = Join-Path (Get-Location).Path 'stock.csv'
$aleatorio = [Random]::new(20261124)
$cultura = [Globalization.CultureInfo]::InvariantCulture
$lineas = [System.Collections.Generic.List[string]]::new()

function B64([string]$texto) {
  # El codec Java usa Base64 URL-safe, no el alfabeto estándar.
  $estandar = [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes($texto))
  $estandar.Replace('+', '-').Replace('/', '_')
}

function Numero([double]$valor) {
  $valor.ToString('0.##', $cultura)
}

function Elegir([array]$opciones) {
  $opciones[$aleatorio.Next($opciones.Count)]
}

function Agregar([string]$tipo, [string]$id, [string]$marca, [string]$modelo,
                 [int]$anio, [string]$color, [string]$estado, [double]$km,
                 [double]$precio, [string[]]$extras) {
  $campos = @(
    $tipo, (B64 $id), (B64 $marca), (B64 $modelo), $anio,
    (B64 $color), $estado, (Numero $km), (Numero $precio)
  ) + $extras
  $lineas.Add(($campos -join '|'))
}

$colores = @('Rojo', 'Azul', 'Gris', 'Negro', 'Blanco', 'Plata', 'Verde',
             'Amarillo', 'Naranja', 'Borgoña')
$carrocerias = @('SEDAN', 'HATCHBACK', 'COUPE', 'SUV', 'FAMILIAR', 'CONVERTIBLE')
$cabinas = @('CABINA_SIMPLE', 'DOBLE_CABINA', 'UTILITARIO')
$tracciones = @('TRACCION_4X2', 'TRACCION_4X4')
$estilos = @('NAKED', 'ENDURO', 'TOURING', 'SCOOTER', 'DEPORTIVA', 'CUSTOM')
$cilindradas = @(125, 150, 250, 300, 500, 600, 750, 1000)

$autos = @(
  @('Toyota', 'Corolla'), @('Toyota', 'Camry'), @('Honda', 'Civic'),
  @('Honda', 'Accord'), @('Ford', 'Focus'), @('Ford', 'Mondeo'),
  @('Volkswagen', 'Golf'), @('Volkswagen', 'Vento'), @('Chevrolet', 'Cruze'),
  @('Peugeot', '308'), @('Renault', 'Megane'), @('Fiat', 'Cronos'),
  @('BMW', 'Serie 3'), @('Mercedes-Benz', 'Clase C'), @('Audi', 'A4'),
  @('Hyundai', 'Elantra'), @('Kia', 'Forte'), @('Nissan', 'Sentra'),
  @('Mazda', 'Mazda 3'), @('Citroen', 'C4')
)

$camionetas = @(
  @('Ford', 'Ranger'), @('Toyota', 'Hilux'), @('Chevrolet', 'S10'),
  @('Volkswagen', 'Amarok'), @('Nissan', 'Frontier'), @('Mitsubishi', 'L200'),
  @('Fiat', 'Toro'), @('Renault', 'Alaskan'), @('Isuzu', 'D-Max'),
  @('Jeep', 'Gladiator'), @('Ram', '1500'), @('Chevrolet', 'Silverado'),
  @('Ford', 'F-150'), @('Toyota', 'Land Cruiser'), @('Nissan', 'Patrol'),
  @('Mitsubishi', 'Pajero'), @('Hyundai', 'H-1'), @('Kia', 'Carnival'),
  @('Mercedes-Benz', 'Sprinter'), @('Iveco', 'Daily')
)

$motos = @(
  @('Honda', 'CB500'), @('Yamaha', 'MT-07'), @('Kawasaki', 'Ninja 400'),
  @('Suzuki', 'GSX-R750'), @('Ducati', 'Monster'), @('BMW', 'R 1200 GS'),
  @('Vespa', 'Primavera'), @('Piaggio', 'Liberty'), @('Aprilia', 'RSV4'),
  @('Triumph', 'Bonneville'), @('Harley-Davidson', 'Sportster'),
  @('KTM', '390 Duke'), @('Husqvarna', 'TE 250'), @('Yamaha', 'XMAX'),
  @('Honda', 'PCX'), @('Kawasaki', 'KLR 650'), @('Suzuki', 'V-Strom'),
  @('Benelli', 'TRK 502'), @('CFMOTO', '300NK'), @('Royal Enfield', 'Classic 500')
)

# Registro original creado desde la interfaz web.
Agregar 'AUTOMOVIL' 'WEB-001' 'Toyota' 'Corolla' 2024 'Rojo' 'NUEVO' 0 15500000 @('SEDAN', 4)

# 24 automóviles.
for ($i = 1; $i -le 24; $i++) {
  $pareja = Elegir $autos
  $anio = $aleatorio.Next(2011, 2027)
  $esNuevo = $anio -ge 2025 -and $aleatorio.Next(0, 100) -lt 70
  if ($esNuevo) {
    $anio = $aleatorio.Next(2025, 2027)
    $km = 0
    $estado = 'NUEVO'
  } else {
    $anio = $aleatorio.Next(2011, 2025)
    $km = $aleatorio.Next(2500, 180000)
    $estado = 'USADO'
  }
  $precio = $aleatorio.Next(8000000, 55000000)
  Agregar 'AUTOMOVIL' ('AUTO-{0:D3}' -f $i) $pareja[0] $pareja[1] $anio (Elegir $colores) `
    $estado $km $precio @((Elegir $carrocerias), $aleatorio.Next(2, 6))
}

# 24 camionetas.
for ($i = 1; $i -le 24; $i++) {
  $pareja = Elegir $camionetas
  $esNuevo = $aleatorio.Next(0, 100) -lt 40
  if ($esNuevo) {
    $anio = $aleatorio.Next(2024, 2027)
    $km = 0
    $estado = 'NUEVO'
  } else {
    $anio = $aleatorio.Next(2010, 2025)
    $km = $aleatorio.Next(8000, 260000)
    $estado = 'USADO'
  }
  $precio = $aleatorio.Next(15000000, 75000000)
  Agregar 'CAMIONETA' ('CAM-{0:D3}' -f $i) $pareja[0] $pareja[1] $anio (Elegir $colores) `
    $estado $km $precio @((Elegir $cabinas), (Elegir $tracciones), $aleatorio.Next(800, 3500))
}

# 24 motocicletas.
for ($i = 1; $i -le 24; $i++) {
  $pareja = Elegir $motos
  $esNuevo = $aleatorio.Next(0, 100) -lt 55
  if ($esNuevo) {
    $anio = $aleatorio.Next(2024, 2027)
    $km = 0
    $estado = 'NUEVO'
  } else {
    $anio = $aleatorio.Next(2010, 2025)
    $km = $aleatorio.Next(1000, 120000)
    $estado = 'USADO'
  }
  $precio = $aleatorio.Next(2000000, 26000000)
  Agregar 'MOTOCICLETA' ('MOTO-{0:D3}' -f $i) $pareja[0] $pareja[1] $anio (Elegir $colores) `
    $estado $km $precio @((Elegir $estilos), (Elegir $cilindradas))
}

[IO.File]::WriteAllLines($ruta, $lineas, [Text.UTF8Encoding]::new($false))
Write-Output ("Generados {0} vehiculos en {1}" -f $lineas.Count, $ruta)
