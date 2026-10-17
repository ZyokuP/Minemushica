# Música del Servidor (Forge 1.20.1)

Reproduce canciones .mp3 guardadas en el SERVIDOR: para todos los jugadores o solo para los que elijas.
Comandos solo para operadores.

## Uso
1. Arranca el servidor una vez: se crea la carpeta `musica/` en la raíz del servidor.
2. Copia ahí tus archivos `.mp3`.
3. Comandos:
   - `/musica cancion`           -> suena para TODOS
   - `/musica cancion Steve`     -> solo para Steve (también `@a`, `@p`, `@r`...)
   - `/musica stop` o `/musica stop Steve`
   - `/musica lista`
   - Si el nombre tiene espacios, ponlo entre comillas: `/musica "mi cancion.mp3"`

## Cómo funciona
El servidor envía la canción al jugador la primera vez (queda guardada en su carpeta `musica_cache/`);
las siguientes veces suena al instante. Respeta los volúmenes "Maestro" y "Música" del juego.

## Obtener el .jar sin instalar nada
1. Sube TODO el contenido de esta carpeta (incluida `.github`) a un repositorio de GitHub.
2. Pestaña Actions -> "Compilar mod" -> descarga **musica-jar**.
3. Usa el .jar que NO termina en `-slim` (el completo incluye el decodificador de MP3).
4. Ponlo en `mods/` del servidor y de cada jugador.

(El decodificador MP3 incluido es JLayer, licencia LGPL.)
