# Cheat sheet: `.form` / GridLayoutManager

Referencia rápida para tocar los `.form` a mano sin abrir el editor
visual. Buscá la propiedad, mirá qué hace, ponela.

## `<grid>` (el contenedor)

```xml
<grid layout-manager="GridLayoutManager" row-count="2" column-count="3"
      hgap="8" vgap="0" same-size-horizontally="false" same-size-vertically="false">
```

- `row-count` / `column-count` — cuántas filas/columnas tiene la grilla.
- `hgap` / `vgap` — espacio EN PÍXELES entre celdas, horizontal/vertical.
  `-1` = usa el default del layout (~4-5px, no es cero). Si querés
  espacio exactamente cero entre filas/columnas, poné `0` explícito.
- `same-size-horizontally` / `...vertically` — si `true`, todas las
  columnas (o filas) miden lo mismo. Casi siempre `false`.
- `<margin top="14" left="16" bottom="14" right="16"/>` — padding
  interno alrededor de TODO el grid (como `padding` en CSS).

## `<grid>` dentro de `<constraints>` (cada celda)

Esto es lo que vas a tocar el 90% de las veces:

```xml
<grid row="0" column="1" row-span="1" col-span="1"
      vsize-policy="0" hsize-policy="3" anchor="8" fill="1" indent="0"/>
```

- `row` / `column` — posición de la celda (empieza en 0).
- `row-span` / `col-span` — cuántas filas/columnas ocupa (para
  "fusionar" celdas, ej. un ícono con `row-span="2"`).
- `hsize-policy` / `vsize-policy` — qué tan "elástica" es la celda:
  | valor | significa |
  |---|---|
  | `0` | fijo, tamaño = preferred del componente (no crece ni se achica) |
  | `1` | puede crecer y achicarse, pero no lo pide |
  | `2` | puede achicarse y QUIERE crecer |
  | `3` | puede crecer/achicarse y QUIERE crecer (la más elástica, la que "se come" el espacio sobrante) |
  | `6` | como el 3, usado normalmente en spacers |

  **Tip clave:** si tenés varias columnas/filas y una se ve más ancha
  o centrada de lo que debería, es casi siempre porque NINGUNA celda
  tiene `hsize-policy="3"` — el layout reparte el sobrante entre todas
  por igual. Poné `3` en la celda que SÍ querés que absorba el espacio
  extra (ej: el título) y dejá `0` en las que deben quedarse en su
  tamaño natural (ej: un ícono, un botón).

- `anchor` — dónde se pega el componente DENTRO de su celda cuando la
  celda es más grande que él (con `fill="0"`). **No es
  GridBagConstraints**, es un enum propio:
  | valor | posición |
  |---|---|
  | `0` | CENTER |
  | `1` | NORTH |
  | `2` | NORTHEAST |
  | `3` | EAST |
  | `4` | SOUTHEAST |
  | `5` | SOUTH |
  | `6` | SOUTHWEST |
  | `7` | WEST |
  | `8` | NORTHWEST (arriba-izquierda) |

- `fill` — si el componente se ESTIRA para llenar la celda:
  | valor | significa |
  |---|---|
  | `0` | NONE — mantiene su tamaño natural |
  | `1` | HORIZONTAL — se estira en ancho |
  | `2` | VERTICAL — se estira en alto |
  | `3` | BOTH |

  Ojo: `fill="1"` + `anchor="8"` en un JLabel NO garantiza que el
  texto quede pegado a la izquierda si la celda es gigante — hay que
  además llamar `setHorizontalAlignment(SwingConstants.LEFT)` en el
  label, porque el label se estira pero el texto adentro se sigue
  centrando por defecto.

- `indent` — sangría extra, casi nunca se toca.

## Spacers (`<hspacer>` / `<vspacer>`)

Componentes invisibles que ocupan una celda entera, usados para
empujar todo lo demás hacia los lados. Típicamente con
`hsize-policy`/`vsize-policy` en `6` o `1` (quieren crecer).

```xml
<vspacer>
  <constraints>
    <grid row="3" column="0" vsize-policy="6" hsize-policy="1" fill="2"/>
  </constraints>
</vspacer>
```

Patrón común: un `vspacer` al final de una columna de tarjetas para
que todo se quede pegado arriba y el sobrante de alto se lo coma el
spacer, no las tarjetas.

## Checklist rápido cuando algo se ve mal

- **Elemento centrado quiere ir a un lado** → la celda con
  `hsize-policy="3"` no es la del elemento, o falta
  `setHorizontalAlignment` en el componente.
- **Espacio de más entre filas aunque `vgap="0"`** → probablemente un
  componente con `row-span` (ej. un ícono) es más alto que la suma de
  las filas que fusiona, y el layout reparte ese extra entre ellas.
  Achicá el componente o fijale `preferredSize`.
- **Columna/fila no se achica cuando debería** → tiene
  `hsize-policy`/`vsize-policy` en `0` cuando debería ser `1` o `3`.
- **Quiero separación exacta entre dos elementos, no depender del
  layout** → usá un spacer explícito en vez de jugar con `hgap`/`vgap`.
