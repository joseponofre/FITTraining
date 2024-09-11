package com.joseponofre.fittraining

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Locale

// Clase FITTrainingSQlite que extiende de SQLiteOpenHelper y desde donde se maneja toda la base de datos (creación de tablas, inserción y manipulación de datos, etc).
class FITTrainingSQLite(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FITTraining.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Crea la tabla Usuarios
        val createUsuarios = """ CREATE TABLE Usuarios (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombreUsuario TEXT NOT NULL,
            imagenUsuario TEXT NOT NULL,
            contrasenya TEXT NOT NULL
        )""".trimIndent()

        db?.execSQL(createUsuarios)
        // Crea la tabla Ejercicios
        val createEjercicios = """ CREATE TABLE Ejercicios (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            imagenEjercicio INTEGER,
            nombreEjercicio TEXT NOT NULL,
            instruccionesEjercicio TEXT,
            videoEjercicio INTEGER
        )""".trimIndent()

        db?.execSQL(createEjercicios)
        // Crea la tabla Entrenamientos
        val createEntrenamientos = """ CREATE TABLE Entrenamientos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            diaEntrenamiento DATE NOT NULL,
            horaEntrenamiento TIME NOT NULL,
            duracionEntrenamiento INTEGER NOT NULL,
            numEjercicios INTEGER NOT NULL,
            totalCalorias INTEGER NOT NULL,
            idUsuario INTEGER,
            FOREIGN KEY (idUsuario) REFERENCES Usuarios(id)
        )""".trimIndent()

        db?.execSQL(createEntrenamientos)
        // Crea la tabla Comidas
        val createComidas = """ CREATE TABLE Comidas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            imagenComida INTEGER,
            nombreComida TEXT NOT NULL,
            tipoComida TEXT,
            ingredientes TEXT,
            calorias INTEGER,
            carbohidratos INTEGER,
            proteina INTEGER,
            grasas INTEGER
        )""".trimIndent()

        db?.execSQL(createComidas)

    }
    // En caso de actualización...
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Elimina la tabla Ejercicios y la tabla Comidas
        db?.execSQL("DROP TABLE IF EXISTS Ejercicios")
        db?.execSQL("DROP TABLE IF EXISTS Comidas")

        onCreate(db)
    }
    // Función para crear un nuevo usuario en la tabla Usuarios
    fun insertarNuevoUsuario(usuario: UsuariosDataClass): Long {
        val dbUsuarios = writableDatabase

        val values = ContentValues().apply {
            put("nombreUsuario", usuario.nombreUsuario.toString())
            put("imagenUsuario", usuario.imagenUsuario)
            put("contrasenya", usuario.contrasenya.toString())
        }

        val idUsuario = dbUsuarios.insert("Usuarios", null, values)
        dbUsuarios.close()
        return idUsuario
    }
    // Función para eliminar un usuario de la tabla Usuarios
    fun eliminarUsuario (idUsuario: Int): Int {

        val db = writableDatabase
        return db.delete("Usuarios", "id = ?", arrayOf(idUsuario.toString()))
    }

    // Función que devuelve la lista de usuarios (todos los usuarios creados en la bbdd que comparten el mismo dispositivo)
    fun getListaUsuarios(): List<UsuariosDataClass> {
        val usuariosList = mutableListOf<UsuariosDataClass>()
        val dbUsu = this.readableDatabase
        val selectQuery = "SELECT * FROM Usuarios ORDER BY nombreUsuario DESC"
        val cursor: Cursor = dbUsu.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val nombreUsuario =
                    cursor.getString(cursor.getColumnIndexOrThrow("nombreUsuario"))
                val imagenUsuario = cursor.getString(cursor.getColumnIndexOrThrow("imagenUsuario"))
                val contrasenya =
                    cursor.getString(cursor.getColumnIndexOrThrow("contrasenya"))
                val usuario = UsuariosDataClass(nombreUsuario, imagenUsuario, contrasenya)
                usuariosList.add(usuario)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return usuariosList
    }
    // Función que devuelve el nombre del usuario a partir de su id de usuario
    fun obtenerNombreUsuarioPorId(userId: Int): String? {
        val db = readableDatabase
        val selectQuery = "SELECT nombreUsuario FROM Usuarios WHERE id = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString()))

        var nombreUsuario: String? = null

        if (cursor.moveToFirst()) {
            nombreUsuario = cursor.getString(cursor.getColumnIndexOrThrow("nombreUsuario"))
        }

        cursor.close()
        return nombreUsuario
    }
    // Función que devuelve el id de usuario a partir de su nombre
    fun obtenerIdUsuarioPorNombre(nombreUsuario: String): Int {

        val db = readableDatabase
        val selectQuery = "SELECT id FROM Usuarios WHERE nombreUsuario = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(nombreUsuario))

        var userId = -1

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        }
        cursor.close()
        return userId
    }


    // Función que comprueba que la contraseña introducida coincide con el nombre de usuario, devuelve true si la contraseña coincide.
    fun verificarContrasenya(nombreUsuario: String, contrasenya: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM Usuarios WHERE nombreUsuario = '$nombreUsuario' AND contrasenya = '$contrasenya'"
        val cursor = db.rawQuery(query, null)
        val resultado = cursor.count > 0
        cursor.close()

        return resultado
    }

    // Función que guarda un nuevo entrenamiento en la bbdd
    fun insertarNuevoEntrenamiento(entrenamiento: EntrenamientosDataClass): Long {
        val dbEntrenamientos = writableDatabase

        val values = ContentValues().apply {
            put("diaEntrenamiento", entrenamiento.diaEntrenamiento.toString())
            put("horaEntrenamiento", entrenamiento.horaEntrenamiento.toString())
            put("duracionEntrenamiento", entrenamiento.duracionEntrenamiento)
            put("numEjercicios", entrenamiento.numEjercicios)
            put("totalCalorias", entrenamiento.totalCalorias)
            put("idUsuario", entrenamiento.idUsuario)
        }

        val idEntrenamiento = dbEntrenamientos.insert("Entrenamientos", null, values)
        dbEntrenamientos.close()
        return idEntrenamiento
    }
    // Función que elimina todos los entrenamientos de un determinado usuario
    fun eliminarEntrenamientosPorUsuario (userId: Int): Int {

        val db = this.writableDatabase
        return db.delete("Entrenamientos", "idUsuario = ?", arrayOf(userId.toString()))

    }
    // Función que devuelve el número total de ejercicios guardados en la bbdd
    private fun getNumeroEjercicios(): Int {
        val db = this.readableDatabase
        val selectQuery = "SELECT count(*) as numeroEjercicios FROM Ejercicios"
        val cursor = db.rawQuery(selectQuery, null)
        var numeroEjercicios = -1

        cursor.use {
            if (it.moveToFirst()) {
                numeroEjercicios = it.getInt(it.getColumnIndexOrThrow("numeroEjercicios"))
            }
        }

        cursor.close()

        return numeroEjercicios
    }
    // función que devuelve la lista de ejercicios guardados en la bbdd
    fun getListaEjercicios(): List<EjerciciosDataClass> {
        val ejerciciosList = mutableListOf<EjerciciosDataClass>()
        val dbEjer = this.readableDatabase
        val selectQuery = "SELECT * FROM Ejercicios ORDER BY nombreEjercicio DESC"
        val cursor: Cursor = dbEjer.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val imagenEjercicio = cursor.getInt(cursor.getColumnIndexOrThrow("imagenEjercicio"))
                val nombreEjercicio =
                    cursor.getString(cursor.getColumnIndexOrThrow("nombreEjercicio"))
                val instruccionesEjercicio =
                    cursor.getString(cursor.getColumnIndexOrThrow("instruccionesEjercicio"))
                val videoEjercicio = cursor.getInt(cursor.getColumnIndexOrThrow("videoEjercicio"))
                val ejercicio =
                    EjerciciosDataClass(imagenEjercicio, nombreEjercicio, instruccionesEjercicio, videoEjercicio)
                ejerciciosList.add(ejercicio)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return ejerciciosList
    }

    // función que devuelve los entrenamientos realizados por un usuario determinado (recibe el idUsuario como parámetro).
    fun getEntrenamientosPorUsuario(idUsuario: Int): List<EntrenamientosDataClass> {

        val entrenamientosList = mutableListOf<EntrenamientosDataClass>()
        val dbEntrenesUser = this.readableDatabase
        val consultaEntrenesUser = "SELECT * FROM Entrenamientos WHERE idUsuario = ? ORDER BY diaEntrenamiento ASC"
        val cursorUser: Cursor = dbEntrenesUser.rawQuery(consultaEntrenesUser, arrayOf(idUsuario.toString()))

        if (cursorUser.moveToFirst()) {
            do {

                val fechaEntreno =
                    cursorUser.getString(cursorUser.getColumnIndexOrThrow("diaEntrenamiento"))
                val fecha = SimpleDateFormat("EEE MMM dd hh:mm:ss zzzz yyyy", Locale.ENGLISH)
                val diaEntrenamiento = fecha.parse(fechaEntreno)

                val horaEntrenamiento =
                    cursorUser.getString(cursorUser.getColumnIndexOrThrow("horaEntrenamiento"))
                val horaEntrene = LocalTime.parse(horaEntrenamiento)
                val duracionEntrenamiento =
                    cursorUser.getInt(cursorUser.getColumnIndexOrThrow("duracionEntrenamiento"))
                val numEjercicios = cursorUser.getInt(cursorUser.getColumnIndexOrThrow("numEjercicios"))
                val totalCalorias = cursorUser.getInt(cursorUser.getColumnIndexOrThrow("totalCalorias"))
                val idUser = cursorUser.getInt(cursorUser.getColumnIndexOrThrow("idUsuario"))

                val entrenamiento = EntrenamientosDataClass(
                    diaEntrenamiento!!,
                    horaEntrene,
                    duracionEntrenamiento,
                    numEjercicios,
                    totalCalorias,
                    idUser,
                )
                entrenamientosList.add(entrenamiento)
            } while (cursorUser.moveToNext())
        }

        cursorUser.close()
        return entrenamientosList
    }

    // función que inserta en la tabla Ejercicios todos los ejercicios que viene predefinidos en la aplicación
    fun insertarEjerciciosPredefinidos() {
        val dbEjercicios = writableDatabase
        try {
            val numeroEjercicios = getNumeroEjercicios()
            // Para evitar que se cree la tabla Ejercicios y se dupliquen cada vez que se inicia la app, cheque que el numEjercicios sea 0.
            if (numeroEjercicios == 0) {
                val ejerciciosPredefinidos = listOf(
                    EjerciciosDataClass(
                        R.drawable.i_air_bike,
                        "Abdominales bicicleta",
                        "Acuéstate boca arriba en una esterilla de yoga o en una superficie cómoda. Mantén la espalda baja pegada al suelo. Levanta las piernas y mantenlas ligeramente flexionadas. Eleva el torso del suelo y rótalo para llevar el codo derecho hacia la rodilla izquierda. Alterna el movimiento con el otro codo y rodilla. Mantén un ritmo constante y controlado. Recuerda mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. Los abdominales bicicleta son un ejercicio eficaz para trabajar los músculos abdominales y oblicuos. Si sientes dolor en la parte baja de la espalda, detén el ejercicio y consulta a un profesional de la salud. ¡Disfruta del entrenamiento!.",
                        R.raw.v_air_bike
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_burpee,
                        "Burpee",
                        "Empieza de pie con los pies separados al ancho de los hombros. Agáchate y coloca las manos en el suelo, justo delante de tus pies. Apoya el peso en las manos y salta con los pies hacia atrás, llevándolos a una posición de plancha en la que mantengas el cuerpo en línea recta. Desde la posición de plancha lleva los pies hacia adelante regresando a la posición de cuclillas y desde allí salta explosivamente hacia arriba. Repite el movimiento de manera fluida y controlada. Recuerda mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. El burpee es un ejercicio muy completo que trabaja todo el cuerpo combinando fuerza y cardio. ¡Disfruta del entrenamiento!.",
                        R.raw.v_burpee
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_burpee_long_jump_with_push_up,
                        "Burpee con flexión y salto adelante",
                        "Empieza de pie con los pies separados al ancho de los hombros. Agáchate y coloca las manos en el suelo, justo delante de tus pies. Apoya el peso en las manos y salta con los pies hacia atrás, llevándolos a una posición de plancha en la que mantengas el cuerpo en línea recta. Baja el pecho hacia el suelo manteniendo los codos cerca del cuerpo. Desde la posición de plancha lleva los pies hacia adelante regresando a la posición de cuclillas para ponerte de pie y desde allí salta explosivamente hacia adelante. Repite el movimiento de manera fluida y controlada. Recuerda mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. Este ejercicio es excelente para mejorar la fuerza, la resistencia y la capacidad cardiovascular. ¡Disfruta del entrenamiento!.",
                        R.raw.v_burpee_long_jump_with_push_up
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_burpee_push_up_mountain_climber,
                        "Burpees con flexión y escaladores",
                        "Empieza de pie con los pies separados al ancho de los hombros. Agáchate y coloca las manos en el suelo, justo delante de tus pies. Apoya el peso en las manos y salta con los pies hacia atrás, llevándolos a una posición de plancha en la que mantengas el cuerpo en línea recta. Baja el pecho hacia el suelo manteniendo los codos cerca del cuerpo. Una vez llegues al punto más bajo, empuja con fuerza hacia arriba extendiendo completamente los brazos y volviendo a la posición de plancha. A continuación lleva las rodillas al pecho de manera alterna. Desde la posición de plancha lleva los pies hacia adelante regresando a la posición de cuclillas y ponte de pie regresando a la posición inicial, quedándote de pie con los pies separados a la altura de los hombros. Repite el movimiento de manera fluida y controlada. Recuerda mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. Este ejercicio es excelente para mejorar la fuerza, la resistencia y la capacidad cardiovascular. ¡Disfruta del entrenamiento!.",
                        R.raw.v_burpee_push_up_mountain_climber
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_butt_kicks,
                        "Patadas al glúteo",
                        "Comienza de pie con los pies separados al ancho de los hombros. Levanta una pierna hacia atrás para tocar el talón en el glúteo de manera explosiva. Vuelve a flexionar la rodilla y baja la pierna hacia el suelo de manera controlada. Repite el mismo movimiento con la otra pierna, alternando entre pierna izquiera y derecha en cada repetición, como si quisieras correr sin moverte del sitio. Manten una buena postura todo el ejercicio, con la espalda recta y los hombros hacia atrás. Recuerda mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. Este ejercicio mejora la capacidad cardiovascular. ¡Disfruta del entrenamiento!.",
                        R.raw.v_butt_kicks
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_close_grip_push_up,
                        "Flexiones con agarre cerrado",
                        "Colócate boca abajo en el suelo, con las manos debajo de los hombros y los dedos apuntando hacia adelante. Las manos deben estar cerca entre sí, con los pulgares y los dedos índices formando un triángulo. Flexiona los codos y baja el cuerpo hacia el suelo manteniendo una línea recta desde la cabeza hasta los talones. Baja hasta que tu pecho toque el suelo, o hasta donde sea cómodo para ti. Una vez llegues al punto más bajo, empuja con fuerza hacia arriba extendiendo completamente los brazos y volviendo a la posición inicial. Recuerda mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. Este ejercicio mejora la fuerza de brazos y torso. ¡Disfruta del entrenamiento!.",
                        R.raw.v_close_grip_push_up
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_cossack_squats,
                        "Sentadilla cosaca",
                        "Comienza de pie con los pies separados más allá del ancho de los hombros y los dedos de los pies apuntando ligeramente hacia afuera. Inicia el movimiento llevando el peso hacia un lado, doblando la rodilla de esa pierna mientras extiendes la otra pierna completamente hacia el lado opuesto. Baja el cuerpo hacia la pierna flexionada, manteniendo la espalda recta y el pecho erguido. Una vez que hayas alcanzado la posición más baja, empuja hacia arriba con la pierna flexionada para volver a la posición inicial, utilizando los músculos de la pierna flexionada para levantar el cuerpo. Repite el movimiento en el lado opuesto, llevando el peso hacia la otra pierna. Recuerda mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. Este ejercicio trabaja la movilidad y fuerza de las piernas. ¡Disfruta del entrenamiento!.",
                        R.raw.v_cossack_squats
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_degree_heel_touch,
                        "Toque de tacones acostado",
                        "Acuéstate boca arriba en una esterilla de yoga o en una superficie cómoda.  Levanta las piernas del suelo, manteniéndolas rectas y juntas. Eleva el torso del suelo, llevando los hombros hacia las rodillas. Contrayendo los músculos abdominales, trata de alcanzar el talón del pie derecho con los dedos de la mano derecha, tocándolo suavemente o llegando lo más cerca posible sin forzar. Vuelve a la posición inicial y repite por el otro lado de forma alterna. Recuerda mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. Este ejercicio trabaja la fuerza del core. ¡Disfruta del entrenamiento!.",
                        R.raw.v_degree_heel_touch
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_double_jump_squat,
                        "Sentadilla doble con salto",
                        "Comienza de pie con los pies separados al ancho de los hombros y los dedos de los pies ligeramente apuntando hacia afuera. Mantén la espalda recta y los brazos relajados a los lados del cuerpo o colocados frente a ti para ayudarte a mantener el equilibrio. Inicia el movimiento bajando el cuerpo hacia abajo como si estuvieras sentándote en una silla. Flexiona las rodillas y las caderas mientras mantienes la espalda recta y el pecho levantado. Baja como mínimo hasta que tus muslos estén paralelos al suelo o hasta donde sea cómodo para ti. Desde la posición más baja empuja con las piernas hacia arriba y antes de llevar arriba vuelve a bajar todo lo que puedas para realizar una segunda sentadilla. Desde la posición más baja realiza un salto explosivo hacia arriba, extendiendo completamente las piernas mientras te elevas del suelo. Repite el proceso recordando mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. Este ejercicio trabaja la fuerza de las piernas y mejora la capacidad cardiovascular. ¡Disfruta del entrenamiento!.",
                        R.raw.v_double_jump_squat
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_dynamic_plank,
                        "Plancha dinámica",
                        "Colocar tus mano directamente debajo de tus hombros y extender las piernas hacia atrás, apoyándote en los dedos de los pies. Mantén tu cuerpo en una línea recta desde la cabeza hasta los talones, y activa tus músculos abdominales y glúteos para mantener una postura estable. Desde la posición de plancha con las manos, comienza a bajar uno de los codos hacia el suelo, manteniendo el cuerpo estable y evitando que las caderas se hundan o se levanten. Una vez que el codo toque el suelo, baja el otro codo hasta el suelo. A continuación empuja con fuerza para devolver uno de los codos a la posición inicial y a continuación el otro codo. Recuerda mantener una alineación adecuada del cuerpo y evitar que la espalda se arquee o se hunda durante el movimiento. La plancha dinámica alternando apoyo de codos es un ejercicio desafiante que puede fortalecer significativamente tus músculos centrales y mejorar tu estabilidad. ¡Disfruta del entrenamiento!.",
                        R.raw.v_dynamic_plank
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_forward_lunge,
                        "Zancada frontal",
                        "Comienza de pie con los pies juntos y los brazos relajados a los lados del cuerpo. Da un paso largo hacia adelante con una pierna, asegurándote de mantener el torso recto y la espalda erguida. Baja el cuerpo doblando ambas rodillas hasta que la rodilla trasera toque el suelo o hasta el máximo que puedas. Empuja con fuerza el pie delantero contra el suelo y lleva el cuerpo de vuelta a la posición inicial, utilizando los músculos de las piernas y los glúteos para realizar el movimiento. Una vez hayas a regresado a la posición inicial, repite el proceso con la otra pierna, dando un paso largo hacia adelante. La zancada frontal es un excelente ejercicio para fortalecer los músculos de las piernas, incluyendo los cuádriceps, los isquiotibiales y los glúteos, así como para mejorar el equilibrio y la estabilidad. Recuerda mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. ¡Disfruta de tus zancadas frontales y de cómo se fortalecen tus piernas!.",
                        R.raw.v_forward_lunge
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_front_leg_lift_under_knee_tap,
                        "Palmada bajo la rodilla",
                        "Ponte erguido con los pies separados al ancho de los hombros. Levanta una pierna del suelo manteniendo el torso recto y el abdomen contraído para mantener el equilibrio. Mientras mantienes la pierna levantada, lleva las manos hacia abajo para dar una palmada. Baja la pierna y las manos de manera controlada hacia el suelo, manteniendo el equilibrio y la postura erguida. Repite el movimiento alternando entre una pierna y la otra, realizando la palmada bajo las rodillas en cada repetición. Recuerda mantener una postura adecuada y evitar arquear la espalda durante el ejercicio para prevenir lesiones. Este ejercicio puede ser desafiante para el equilibrio, así que concéntrate en mantener una base estable y controlar el movimiento en todo momento. ¡Disfruta del ejercicio y siente cómo trabajan tus músculos abdominales y flexores de la cadera!.",
                        R.raw.v_front_leg_lift_under_knee_tap
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_high_knee_squat,
                        "Sentadilla con elevación de rodillas",
                        "Comienza de pie con los pies separados al ancho de los hombros y los dedos de los pies ligeramente apuntando hacia afuera. Mantén la espalda recta y los brazos relajados a los lados del cuerpo o colocados frente a ti para ayudarte a mantener el equilibrio. Inicia el movimiento bajando el cuerpo hacia abajo como si estuvieras sentándote en una silla. Flexiona las rodillas y las caderas mientras mantienes la espalda recta y el pecho levantado. Baja como mínimo hasta que tus muslos estén paralelos al suelo o hasta donde sea cómodo para ti. Desde la posición más baja empuja con las piernas hacia arriba y levanta la pierna derecha llevando la rodilla hacia el pecho. Repite la sentadilla pero esta vez al subir alterna las piernas subiendo la rodilla izquierda hacia el pecho. Recuerda mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. Este movimiento puede ser desafiante, así que escucha a tu cuerpo y ajusta la intensidad según sea necesario. ¡Disfruta del ejercicio y de cómo fortalece tus piernas y tu core!.",
                        R.raw.v_high_knee_squat
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_jack_burpee_with_push_up,
                        "Jack burpee con flexión",
                        "Empieza de pie con los pies separados al ancho de los hombros. Agáchate y coloca las manos en el suelo, justo delante de tus pies. Apoya el peso en las manos y salta con los pies hacia atrás, llevándolos a una posición de plancha en la que mantengas el cuerpo en línea recta. Baja el pecho hacia el suelo manteniendo los codos cerca del cuerpo. Una vez llegues al punto más bajo, empuja con fuerza hacia arriba extendiendo completamente los brazos y volviendo a la posición de plancha. Apóyate fuerte con las manos y haz un movimiento de abrir y cerrar las piernas de manera explosiva. Desde la posición de plancha lleva los pies hacia adelante regresando a la posición de cuclillas y desde allí salta explosivamente hacia arriba. Repite el movimiento de manera fluida y controlada. Recuerda mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. Este ejercicio es excelente para mejorar la fuerza, la resistencia y la capacidad cardiovascular. ¡Disfruta del entrenamiento!.",
                        R.raw.v_jack_burpee_with_push_up
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_jump_skip_rope,
                        "Salto a la comba",
                        "Empieza con los pies juntos y separados a la altura de los hombros. Gira las muñecas como si movieras una comba imaginaria y salta con ambos pies juntos, manteniendo un ritmo constante. Comienza con un ritmo lento y aumenta la velocidad a medida que te sientas más cómodo. Este ejercicio es una excelente forma de mejorar la capacidad cardiovascular. ¡Disfruta del entrenamiento!.",
                        R.raw.v_jump_skip_rope
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_jump_squat,
                        "Sentadilla con salto",
                        "Comienza de pie con los pies separados a la anchura de los hombros. Inicia el movimiento bajando el cuerpo hacia abajo como si estuvieras sentándote en una silla. Flexiona las rodillas y las caderas mientras mantienes la espalda recta y el pecho levantado. Baja como mínimo hasta que tus muslos estén paralelos al suelo o hasta donde sea cómodo para ti. Desde la posición más baja de la sentadilla realiza un salto explosivo hacia arriba, extendiendo las piernas mientras te elevas del suelo. Aterriza suavemente con las rodillas ligeramente flexionadas para absorber el impacto, con los pies separados a la anchura de los hombros para mantener la estabilidad. Recuerda mantener una postura adecuada y evitar que la espalda se arquee durante el ejercicio para prevenir lesiones. Las sentadillas con salto son un ejercicio de alta intensidad que puede ser desafiante, así que escucha a tu cuerpo y ajusta la intensidad según sea necesario. ¡Disfruta del ejercicio y de cómo se fortalecen tus piernas y tu core!.",
                        R.raw.v_jump_squat
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_jumping_jacks,
                        "Jumping jacks",
                        "Empieza de pie con los pies juntos y los brazos pegados al cuerpo. Da un pequeño salto hacia arriba y, al mismo tiempo, separa los pies hacia los lados a una anchura mayor que la de tus hombros. Mientras saltas y separas los pies, levanta los brazos hacia arriba por encima de la cabeza.  Invierte el movimiento, saltando de nuevo para juntar los pies y al mismo tiempo baja los brazos a los lados del cuerpo. Repite el movimiento de forma fluida y continua, alternando entre separar y juntar los pies mientras mueves los brazos arriba y abajo. Recuerda mantener una buena postura durante todo el ejercicio para evitar lesiones. Los jumping jacks son una excelente manera de mejorar la capacidad cardiovascular. ¡Diviértete y disfruta del movimiento!.",
                        R.raw.v_jumping_jacks
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_leg_raises,
                        "Elevación de piernas",
                        "Acuéstate boca arriba en una esterilla de yoga o en una superficie cómoda. Extiende las piernas completamente y coloca los brazos a los lados del cuerpo con las palmas hacia abajo. Mantén la espalda pegada al suelo en todo momento. Manteniendo las piernas rectas, levántalas hacia arriba del suelo hasta que estén perpendiculares al suelo o lo más alto posible sin arquear la espalda baja. Una vez que las piernas estén en su punto más alto, bájalas de manera controlada hacia el suelo, evitando dejar que toquen el suelo completamente. Mantén la tensión en los abdominales durante todo el movimiento. Recuerda mantener una postura adecuada y evitar arquear la espalda durante el ejercicio para prevenir lesiones. Las elevaciones de piernas son un excelente ejercicio para fortalecer los músculos abdominales inferiores y mejorar la estabilidad del core. ¡Disfruta del ejercicio y de cómo trabaja tu core!",
                        R.raw.v_leg_raises
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_lying_open_close,
                        "Abrir y cerrar piernas acostado",
                        "Acuéstate boca arriba en una esterilla de yoga o en una superficie cómoda. Extiende las piernas completamente y coloca los brazos a los lados del cuerpo con las palmas hacia abajo. Mantén la espalda pegada al suelo en todo momento. Comienza el movimiento con las piernas ligeramente elevadas evitando que toquen el suelo durante todo el ejercicio. Procede a abrir y cerrar las piernas, focalizándote en mantener la tensión en el core. ¡Disfruta del ejercicio y de cómo se fortalecen tus abdominales!.",
                        R.raw.v_lying_open_close
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_lying_toe_touch,
                        "Toque de punta de pies acostado",
                        "Acuéstate boca arriba en una esterilla de yoga o en una superficie cómoda. Manten ambos pies del suelo, extendiendo las rodillas hasta que tus muslos estén alineados verticalmente con el suelo. Contrae tus abdominales para flexionar tu tronco, levantando tus hombros del suelo e intentanto tocal con tus dedos las puntas de tus pies. Baja a la posición inicial y repite el movimiento. Este ejercicio es excelente para fortalecer los abdominales. ¡Disfruta del entrenamiento!.",
                        R.raw.v_lying_toe_touch
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_mountain_climber,
                        "Escaladores",
                        "Comienza en una posición de plancha alta con las manos colocadas en el suelo directamente debajo de los hombros y los brazos extendidos. Mantén el cuerpo en una línea recta desde la cabeza hasta los talones, y activa los músculos abdominales y del core para estabilizar la postura. Inicia el ejercicio llevando una rodilla hacia el pecho, manteniendo el otro pie en su lugar en la posición de plancha. Vuelve a llevar la pierna que acabas de mover hacia atrás a su posición inicial. Repite el movimiento con la otra pierna  y alterna entre pierna izquiera y derecha en cada repetición. Asegúrate de mantener una alineación adecuada del cuerpo y evitar que la cadera se levante o hunda demasiado durante el ejercicio. Los escaladores son un ejercicio versátil qua aúna trabajo cardiovascular y de fuerza, enfocado principalmente en abdominales, brazos, hombros y piernas.  ¡Disfruta del desafío y de cómo trabajan varios grupos musculares al mismo tiempo!",
                        R.raw.v_mountain_climber
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_pike_push_up,
                        "Flexiones en pica",
                        "Comienza en una posición de plancha alta con las manos colocadas en el suelo un poco más anchas que el ancho de los hombros y los brazos extendidos. Eleva las caderas hacia arriba formando un ángulo de aproximadamente 45 grados con el cuerpo, manteniendo las piernas y la espalda rectas. Asegúrate de que tus manos estén firmemente apoyadas en el suelo y que tus dedos estén apuntando hacia adelante. Mantén el core contraído y los glúteos activados para estabilizar tu cuerpo durante todo el ejercicio. Flexiona los codos y baja la cabeza hacia el suelo mientras mantienes las caderas elevadas. Intenta bajar lentamente hasta que la cabeza toque el suelo entre tus manos o lo máximo que puedas. A continuación, empuja con fuerza hacia arriba con los brazos para volver a la posición inicial, manteniendo las caderas elevadas y las piernas lo más rectas posible. Las flexiones en pica son un excelente ejercicio para fortalecer los hombros y los tríceps. ¡Disfruta del entrenamiento!.",
                        R.raw.v_pike_push_up
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_push_up,
                        "Flexiones",
                        "Comienza en una posición de plancha alta con las manos colocadas en el suelo ligeramente más anchas que el ancho de los hombros y los brazos extendidos. Mantén el cuerpo en una línea recta desde la cabeza hasta los talones, y activa los músculos abdominales y del core para estabilizar la postura. Flexiona los codos y baja el cuerpo hacia el suelo manteniendo la alineación del cuerpo. Baja el pecho hasta tocar el suelo o lo máximo que puedas. Evita dejar caer el cuerpo de manera brusca o permitir que la espalda se arquee. Una vez el pecho toque el suelo o esté cerca de él, empuja con fuerza hacia arriba con los brazos para volver a la posición inicial de plancha con los brazos completamente extendidos. Mantén una buena técnica durante todo el ejercicio para evitar lesiones. Mantén el cuerpo en línea recta y los codos cerca del cuerpo durante el movimiento. Las flexiones son un excelente ejercicio para fortalecer los músculos del torso y los brazos, y pueden ser una parte importante de tu rutina de entrenamiento de fuerza. ¡Disfruta del ejercicio y de cómo se fortalece tu cuerpo!",
                        R.raw.v_push_up
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_rear_lunge,
                        "Zancada trasera",
                        "Comienza de pie con los pies separados al ancho de los hombros y las manos en las caderas o a los lados del cuerpo para mantener el equilibrio. Da un paso hacia atrás con una pierna, manteniendo el peso del cuerpo en la pierna delantera. La pierna trasera debe permanecer recta mientras la rodilla de la pierna delantera se flexiona, formando un ángulo de aproximadamente 90 grados. Baja lentamente el cuerpo hacia el suelo doblando la rodilla de la pierna delantera. Mantén la espalda recta y el torso erguido durante todo el movimiento. Continúa bajando hasta que la rodilla de la pierna delantera esté en línea con el tobillo y la rodilla de la pierna trasera casi toque el suelo, manteniendo el equilibrio y la estabilidad en todo momento. Desde la posición más baja de la zancada, empuja con fuerza hacia arriba a través del talón de la pierna delantera para volver a la posición inicial. Utiliza los músculos de las piernas y los glúteos para impulsarte hacia arriba. Repite el mismo movimiento con la otra pierna y continúa alternando entre una pierna y la otra. Recuerda mantener una técnica adecuada durante todo el ejercicio para evitar lesiones y maximizar los beneficios. Las zancadas traseras son un excelente ejercicio para fortalecer las piernas y los glúteos, y pueden ser parte de tu rutina de entrenamiento de fuerza regular. ¡Disfruta del ejercicio y del trabajo en tus músculos inferiores!.",
                        R.raw.v_rear_lunge
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_shoulder_tap,
                        "Plancha con toque de hombros",
                        "Comienza en una posición de plancha alta con las manos colocadas en el suelo directamente debajo de los hombros y los brazos extendidos. Mantén el cuerpo en una línea recta desde la cabeza hasta los talones, y activa los músculos abdominales y del core para estabilizar la postura. Levanta una mano del suelo y toca el hombro opuesto con esa mano. Mantén el cuerpo lo más quieto posible y evita mover las caderas o el torso mientras realizas el toque de hombros.  Vuelve a colocar la mano en el suelo y repite el movimiento con la otra mano, tocando el hombro opuesto. Alterna entre las manos, manteniendo un ritmo constante y controlado. Mantén el equilibrio y la estabilidad durante todo el ejercicio. Evita balancear el cuerpo o inclinarte hacia un lado mientras realizas el toque de hombros. Mantén una buena técnica durante todo el ejercicio para evitar lesiones. Mantén el cuerpo en línea recta y los músculos del core activados en todo momento. La plancha con toque de hombros es un ejercicio que desafía la fuerza y la estabilidad del core y los hombros. ¡Disfruta del desafío y de cómo se fortalece tu cuerpo!.",
                         R.raw.v_shoulder_tap
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_sit_up,
                        "Abdominales",
                        "Acuéstate boca arriba en una esterilla de yoga o una superficie cómoda, con las rodillas dobladas y los pies apoyados en el suelo, manteniendo los brazos hacia atrás con las manos detrás de la cabeza. Contrae los músculos abdominales para levantar el torso del suelo y acercar el pecho hacia las rodillas. Evita tirar del cuello con las manos o mover la cabeza hacia adelante, ya que esto puede causar tensión en el cuello y la espalda. Alcanza el punto más alto posible donde sientas la contracción máxima en los abdominales. Baja lentamente el torso hacia el suelo, manteniendo el control del movimiento en todo momento. Evita dejar caer el torso hacia el suelo, ya que esto puede causar lesiones en la espalda baja. Una vez que llegues al suelo, repite el movimiento intentando mantener una técnica adecuada en todo momento para evitar lesiones y maximizar los beneficios del ejercicio. Este ejercicio es excelente para fortalecer los músculos abdominales. ¡Disfruta del ejercicio y de cómo se fortalece tu core!. ",
                        R.raw.v_sit_up
                        ),
                    EjerciciosDataClass(
                        R.drawable.i_squat_tip_toe,
                        "Sentadilla levantando los pies",
                        "Comienza de pie con los pies separados más allá del ancho de los hombros y los dedos de los pies apuntando ligeramente hacia afuera. Inicia el movimiento bajando el cuerpo hacia abajo como si estuvieras sentándote en una silla. Flexiona las rodillas y las caderas mientras mantienes la espalda recta y el pecho levantado. Baja como mínimo hasta que tus muslos estén paralelos al suelo o hasta donde sea cómodo para ti. Desde esta posición, levanta y baja el talón de uno de tus pies y, a continuación, haz lo mismo con el talón del otro pie. Repite el movimiento alternando entre un pie y el otro. Mantén en todo momento el core contraído para mejorar la estabilidad. Esta variante de la sentadilla es un excelente ejercicio para fortalecer los cuádriceps, los glúteos y los abdominales. ¡Disfruta del ejercicio y de cómo trabaja tus músculos!.",
                        R.raw.v_squat_tip_toe
                        ),
                    EjerciciosDataClass(
                        R.drawable.i_sumo_squat,
                        "Sentadilla sumo",
                        "Comienza de pie con los pies separados más allá del ancho de los hombros y los dedos de los pies ligeramente hacia afuera, adoptando una posición similar a la de un luchador de sumo.  Mantén la espalda recta, el pecho levantado y los hombros hacia atrás. Activa los músculos abdominales para estabilizar el torso durante todo el ejercicio. Flexiona las rodillas y las caderas para bajar el cuerpo hacia el suelo, manteniendo los pies firmemente plantados en el suelo. Mantén las rodillas alineadas con los dedos de los pies durante todo el movimiento. Baja el cuerpo hacia abajo hasta que los muslos estén paralelos al suelo o tan cerca como puedas llegar cómodamente. Cuando alcances el punto más bajo empuja con fuerza con las piernas para volver a la posición inicial de pie, extendiendo las rodillas y las caderas completamente. Mantén el equilibrio y la estabilidad durante todo el movimiento. La sentadilla sumo es un excelente ejercicio para fortalecer los músculos de las piernas y los glúteos, y puede ser una parte importante de tu rutina de entrenamiento de fuerza. ¡Disfruta del ejercicio y de cómo trabaja tus músculos!. ",
                        R.raw.v_sumo_squat
                        ),
                    EjerciciosDataClass(
                        R.drawable.i_superman,
                        "Superman",
                        "Acuéstate boca abajo en una esterilla de yoga o una superficie cómoda, con los brazos hacia adelante y las piernas extendidas. Mantén el cuerpo completamente recto y alineado desde la cabeza hasta los pies. Desde esta posición, levanta simultáneamente los brazos y las piernas del suelo, manteniendo los músculos de los glúteos y la espalda baja contraídos. A continuación, baja lentamente los brazos y las piernas de vuelta al suelo, manteniendo el control del movimiento en todo momento. Este ejercicio es excelente para fortalecer la espalda baja y los músculos del core, y puede ser una parte valiosa de tu rutina de entrenamiento de fuerza. ¡Disfruta del ejercicio y de cómo se fortalece tu cuerpo!.",
                        R.raw.v_superman
                        ),
                    EjerciciosDataClass(
                        R.drawable.i_twisting_crunch,
                        "Abdominales con rotación",
                        "Acuéstate boca arriba sobre una esterilla de yoga o una superficie cómoda, con las manos detrás de la cabeza y las rodillas dobladas con los pies apoyados en el suelo. Contrae los músculos abdominales y levanta el torso del suelo, intentando alcanzar con el codo derecho la rodilla izquierda mientras giras el torso. Mantén la contracción en los abdominales y siente cómo trabajan los oblicuos durante el giro. Luego, regresa lentamente a la posición inicial, manteniendo el control del movimiento. Repite el movimiento, esta vez llevando el codo izquierdo hacia la rodilla derecha para trabajar los oblicuos del lado opuesto. Alternan los lados en cada repetición para trabajar ambos conjuntos de oblicuos por igual. Mantén una buena técnica en todo momento para evitar lesiones. Evita tirar del cuello con las manos y asegúrate de realizar el movimiento de torsión desde los músculos abdominales y los oblicuos, en lugar de solo mover los codos. Este es un excelente ejercicio para fortalecer los músculos abdominales y los oblicuos, y puede ser una parte importante de tu rutina de entrenamiento de fuerza del core. ¡Disfruta del ejercicio y de cómo trabaja tus músculos abdominales laterales!.",
                        R.raw.v_twisting_crunch
                        ),
                    EjerciciosDataClass(
                        R.drawable.i_wide_hand_push_up,
                        "Flexiónes con agarre ancho",
                        "Comienza en una posición de plancha alta con las manos colocadas en el suelo a una anchura casi el doble que la de tus hombros. Los brazos deben estar extendidos con los dedos apuntanto ligeramente hacia afuera. Mantén el cuerpo en una línea recta desde la cabeza hasta los talones, y activa los músculos abdominales y del core para estabilizar la postura. Flexiona los codos y baja el cuerpo hacia el suelo manteniendo la alineación del cuerpo. Baja el pecho hasta tocar el suelo o lo máximo que puedas. Evita dejar caer el cuerpo de manera brusca o permitir que la espalda se arquee. Una vez el pecho toque el suelo o esté cerca de él, empuja con fuerza hacia arriba con los brazos para volver a la posición inicial de plancha con los brazos completamente extendidos. Mantén una buena técnica durante todo el ejercicio para evitar lesiones. Mantén el cuerpo en línea recta y los codos cerca del cuerpo durante el movimiento. Las flexiones con agarre ancho son un excelente ejercicio para fortalecer la parte superior del cuerpo, especialmente los pectorales, tríceps y hombros. ¡Disfruta del ejercicio!.",
                        R.raw.v_wide_hand_push_up
                    ),
                    EjerciciosDataClass(
                        R.drawable.i_wide_to_narrow_push_up,
                        "Flexión alternando agarres",
                        "Comienza en una posición de plancha alta con las manos colocadas en el suelo un poco más anchas que el ancho de los hombros y los brazos extendidos. Mantén el cuerpo en una línea recta desde la cabeza hasta los talones, y activa los músculos abdominales y del core para estabilizar la postura.  Baja el pecho hacia el suelo manteniendo los codos ligeramente alejados del cuerpo y manteniendo la alineación del cuerpo. A continuación, empuja con fuerza hacia arriba a través de los brazos para volver a la posición inicial. Luego, mueve las manos hacia adentro, colocándolas justo debajo de los hombros para un agarre más estrecho. Realiza otra flexión con las manos colocadas en posición estrecha. Baja el cuerpo hacia el suelo manteniendo los codos pegados al cuerpo y los tríceps más activados. Después de completar la flexión con las manos en posición estrecha, empuja con fuerza hacia arriba para volver a la posición inicial. Luego, mueve las manos hacia afuera nuevamente para un agarre más ancho. Repite el ejercicio alternando entre una agarre más ancho y uno más estrecho, manteniendo un ritmo constante y controlado. Las flexiones alternando agarres son excelentes para trabajar diferentes áreas de los músculos del pecho, los hombros y los tríceps, y pueden ser una parte valiosa de tu rutina de entrenamiento de fuerza. ¡Disfruta del ejercicio y de cómo trabajan tus músculos superiores!",
                        R.raw.v_wide_to_narrow_push_up
                        )
                )

                for (ejercicio in ejerciciosPredefinidos) {
                    val values = ContentValues().apply {
                        put("imagenEjercicio", ejercicio.imagenEjercicio)
                        put("nombreEjercicio", ejercicio.nombreEjercicio)
                        put("instruccionesEjercicio", ejercicio.instruccionesEjercicio)
                        put("videoEjercicio", ejercicio.videoEjercicio)
                    }

                    val idEjercicio = dbEjercicios.insert("Ejercicios", null, values)
                    Log.d("FITTrainingSQLite", "Ejercicio insertado con ID: $idEjercicio")
                }
            }
        } catch (e: Exception) {
            Log.e("FITTrainingSQLite", "Error al insertar ejercicios predefinidos: ${e.message}")
        } finally {
            dbEjercicios.close()
        }
    }
    // función que devuelve el número total de comidas (platos) que existen en la bbdd
    private fun getNumeroComidas(): Int {
        val dbComidas = this.readableDatabase
        val selectQuery = "SELECT count(*) as numeroComidas FROM Comidas"
        val cursor = dbComidas.rawQuery(selectQuery, null)
        var numeroComidas = -1

        cursor.use {
            if (it.moveToFirst()) {
                numeroComidas = it.getInt(it.getColumnIndexOrThrow("numeroComidas"))
            }
        }

        cursor.close()

        return numeroComidas
    }
    // función para insertar en la tabla Comidas todas los platos que vienen predefinidos en la bbdd
    fun insertarComidas() {
        val dbComidas = writableDatabase
        try {
            val numeroComidas = getNumeroComidas()
            // Para evitar que se cree la tabla Comidas y se duplique cada vez que se inicia la app, chequea que el numComidas sea 0.
            if (numeroComidas == 0) {
                val listaComidas = listOf(
                    ComidasDataClass(
                        R.drawable.desayuno_bagels_rellenos_guacamole_huevos_fritos,
                        "Bagels rellenos de guacamole y huevos fritos",
                        tipoComida = ComidasDataClass.TipoComida.DESAYUNO,
                        "* 3 bagels \n" +
                                "* 3 huevos fritos \n" +
                                "* 60 gramos de guacamole",
                        calorias = 1070,
                        carbohidratos = 150,
                        proteina = 50,
                        grasas = 30
                    ),
                    ComidasDataClass(
                        R.drawable.desayuno_tortilla_espinacas_cherrys,
                        "Tortilla de espinacas y champiñones acompañado de tomates cherry",
                        tipoComida = ComidasDataClass.TipoComida.DESAYUNO,
                        "* 2 huevos \n" +
                                " * 40 gramos de espinacas \n" +
                                " * 80 gramos de champiñones \n" +
                                " * 4 tomates cherry",
                        calorias = 560,
                        carbohidratos = 9,
                        proteina = 30,
                        grasas = 45
                    ),
                    ComidasDataClass(
                        R.drawable.desayuno_tortilla_francesa_cherrys_tostadas_guacamole,
                        "Tortilla francesa con tomates cherry y tostadas de guacamole",
                        tipoComida = ComidasDataClass.TipoComida.DESAYUNO,
                        "* 2 huevos \n" +
                                " * 90 gramos de pan tostado \n" +
                                " * 40 gramos de guacamole",
                        calorias = 500,
                        carbohidratos = 55,
                        proteina = 25,
                        grasas = 45
                    ),
                    ComidasDataClass(
                        R.drawable.desayuno_tostadas_guacamole_champinones,
                        "Tostadas de guacamole y champiñones",
                        tipoComida = ComidasDataClass.TipoComida.DESAYUNO,
                        "* 90 gramos de pan tostado \n" +
                                " * 40 gramos de guacamole \n" +
                                " * 80 gramos de champiñones",
                        calorias = 435,
                        carbohidratos = 55,
                        proteina = 13,
                        grasas = 40
                    ),
                    ComidasDataClass(
                        R.drawable.desayuno_tostadas_huevos_duros_guacamole_pesto,
                        "Tostadas con huevos duros, guacamole y salsa de pesto",
                        tipoComida = ComidasDataClass.TipoComida.DESAYUNO,
                        "* 90 gramos de pan tostado \n" +
                                " * 40 gramos de guacamole \n" +
                                " * 2 huevos duros \n" +
                                " * 2 cucharaditas de salsa de pesto",
                        calorias = 565,
                        carbohidratos = 55,
                        proteina = 26,
                        grasas = 27
                    ),
                    ComidasDataClass(
                        R.drawable.desayuno_wrap_tortilla_francesa_verduras,
                        "Wrap de tortilla francesa con guacamole y verduras",
                        tipoComida = ComidasDataClass.TipoComida.DESAYUNO,
                        "* Una tortita de wrap \n" +
                                " * 2 huevos \n" +
                                " * 20 gramos de cuacamole \n" +
                                " * 50 gramos de pimientos rojos \n" +
                                " * 50 gramos de champiñones \n" +
                                " * 20 gramos de espinacas",
                        calorias = 750,
                        carbohidratos = 35,
                        proteina = 18,
                        grasas = 60
                    ),
                    ComidasDataClass(
                        R.drawable.desayuno_yogur_avena_fruta,
                        "Yogur con avena y fruta variada",
                        tipoComida = ComidasDataClass.TipoComida.DESAYUNO,
                        "* 1 yogur \n" +
                                " * 1 plátano \n" +
                                " * 4-5 fresas \n" +
                                " * 20-25 arándanos \n" +
                                " * 50 gramos de copos de avena",
                        calorias = 435,
                        carbohidratos = 75,
                        proteina = 15,
                        grasas = 8
                    ),
                    ComidasDataClass(
                        R.drawable.comida_arroz_gambas_brocoli,
                        "Arroz con gambas y brócoli en salsa de soja",
                        tipoComida = ComidasDataClass.TipoComida.COMIDA,
                        "* 100 gramos de arroz blanco \n" +
                                " * 80 gramos de gambas \n" +
                                " * 100 gramos de brócoli \n" +
                                " * 2 cucharadas de salsa de soja",
                        calorias = 520,
                        carbohidratos = 95,
                        proteina = 30,
                        grasas = 2
                    ),
                    ComidasDataClass(
                        R.drawable.comida_arroz_garbanzos_salsa_curry_pan_naan,
                        "Arroz con garbanzos en salsa tikka masala y pan naan",
                        tipoComida = ComidasDataClass.TipoComida.COMIDA,
                        "* 50 gramos de arroz blanco \n" +
                                " * 80 gramos de garbanzos \n" +
                                " * 4 cucharadas de salsa tikka masala \n" +
                                " * 50 gramos de pan naan",
                        calorias = 800,
                        carbohidratos = 115,
                        proteina = 40,
                        grasas = 20
                    ),
                    ComidasDataClass(
                        R.drawable.comida_arroz_pollo_brocoli_sesamo,
                        "Arroz con pollo en salsa de soja, brócoli y semillas de sésamo",
                        tipoComida = ComidasDataClass.TipoComida.COMIDA,
                        "* 100 gramos de arroz blanco \n" +
                                " * 100 gramos de pechuga de pollo en dados \n" +
                                " * 150 gramos de brócoli \n" +
                                " * 2 cucharadas de salsa de soja \n" +
                                " * 1 cucharada de semillas de sésamo",
                        calorias = 675,
                        carbohidratos = 95,
                        proteina = 40,
                        grasas = 15
                    ),
                    ComidasDataClass(
                        R.drawable.comida_ensalada_pasta,
                        "Ensalada de pasta con salsa de pesto",
                        tipoComida = ComidasDataClass.TipoComida.COMIDA,
                        "100 gramos de pasta \n" +
                                " * 50 gramos de queso mozzarella \n" +
                                " * 150 gramos de tomates cherrys \n" +
                                " * 4 cucharadas de salsa pesto",
                        calorias = 780,
                        carbohidratos = 85,
                        proteina = 30,
                        grasas = 35
                    ),
                    ComidasDataClass(
                        R.drawable.comida_noodles_pollo_verduras,
                        "Noodles con pollo y verduras",
                        tipoComida = ComidasDataClass.TipoComida.COMIDA,
                        "* 100 gramos de noodles \n" +
                                " * 100 gramos de brócoli \n" +
                                " * 100 gramos de pechuga de pollo \n" +
                                " * 50 gramos de pimiento rojo \n" +
                                " * 4 cucharadas de salsa de soja",
                        calorias = 470,
                        carbohidratos = 55,
                        proteina = 35,
                        grasas = 12
                    ),
                    ComidasDataClass(
                        R.drawable.comida_pasta_champinones_espinacas,
                        "Pasta con champiñones y espinacas",
                        tipoComida = ComidasDataClass.TipoComida.COMIDA,
                        "* 100 gramos de pasta \n" +
                                " * 80 gramos de champiñones \n" +
                                " * 40 gramos de espinacas",
                        calorias = 445,
                        carbohidratos = 85,
                        proteina = 15,
                        grasas = 5
                    ),
                    ComidasDataClass(
                        R.drawable.comida_pimientos_rellenos,
                        "Pimientos rellenos de arroz, carne picada y verduras",
                        tipoComida = ComidasDataClass.TipoComida.COMIDA,
                        "* 2 pimientos pequeños \n" +
                                " * 100 gramos de carne picada \n" +
                                " * 100 gramos de arroz \n" +
                                " * 80 gramos de champiñones \n" +
                                " * 50 gramos de queso",
                        calorias = 770,
                        carbohidratos = 95,
                        proteina = 35,
                        grasas = 30
                    ),
                    ComidasDataClass(
                        R.drawable.cena_brochetas_pollo_verduras,
                        "Brochetas de pollo con verduras",
                        tipoComida = ComidasDataClass.TipoComida.CENA,
                        "* 125 gramos de pollo \n" +
                                " * 5 tomates cherry \n" +
                                " * 100 gramos de calabacín \n" +
                                " * 2 rábanos",
                        calorias = 275,
                        carbohidratos = 10,
                        proteina = 35,
                        grasas = 10
                    ),
                    ComidasDataClass(
                        R.drawable.cena_ensalada_cesar_pollo_crujiente,
                        "Ensalada césar con pollo crujiente",
                        tipoComida = ComidasDataClass.TipoComida.CENA,
                        "* 80 gramos de lechuga \n" +
                                " * 125 gramos de pollo crujiente \n" +
                                " * 4 cucharadas de salsa césar",
                        calorias = 385,
                        carbohidratos = 15,
                        proteina = 25,
                        grasas = 25
                    ),
                    ComidasDataClass(
                        R.drawable.cena_huevos_patatas_asadas_espinacas,
                        "Huevos fritos con patatas asadas y espinacas",
                        tipoComida = ComidasDataClass.TipoComida.CENA,
                        "* 2 huevos fritos \n" +
                                " * 150 gramos de patatas asadas \n" +
                                " * 80 gramos de espinacas",
                        calorias = 350,
                        carbohidratos = 35,
                        proteina = 25,
                        grasas = 12
                    ),
                    ComidasDataClass(
                        R.drawable.cena_pollo_barbacoa_brocoli_patatas_asadas,
                        "Pollo con salsa barbacoa, brócoli y patatas asadas",
                        tipoComida = ComidasDataClass.TipoComida.CENA,
                        "* 100 gramos de pollo \n" +
                                " * 100 gramos dde patatas asadas \n" +
                                " * 100 gramos de brócoli \n" +
                                " * 2 cucharadas de salsa barbacoa",
                        calorias = 370,
                        carbohidratos = 35,
                        proteina = 35,
                        grasas = 10
                    ),
                    ComidasDataClass(
                        R.drawable.cena_pollo_judias_verdes,
                        "Pollo con judías verdes",
                        tipoComida = ComidasDataClass.TipoComida.CENA,
                        "* 150 gramos de pechuga de pollo \n" +
                                " * 150 gramos de judías verdes",
                        calorias = 270,
                        carbohidratos = 10,
                        proteina = 35,
                        grasas = 10
                    ),
                    ComidasDataClass(
                        R.drawable.cena_salmon_horno_esparragos,
                        "Salmón al horno con espárragos",
                        tipoComida = ComidasDataClass.TipoComida.CENA,
                        "* 125 gramos de salmón \n * 150 gramos de espárragos verdes",
                        calorias = 255,
                        carbohidratos = 5,
                        proteina = 25,
                        grasas = 15
                    ),
                    ComidasDataClass(
                        R.drawable.cena_wrap_salmon_pepino_queso_untar,
                        "Wrap de salmón ahumado, queso de untar y pepino",
                        tipoComida = ComidasDataClass.TipoComida.CENA,
                        "* Una tortita de wrap \n" +
                                " * 50 gramos de salmón ahumado \n" +
                                " * 80 gramos de pepino \n" +
                                " * 20 gramos de crema de queso para untar",
                        calorias = 255,
                        carbohidratos = 25,
                        proteina = 12,
                        grasas = 12
                    )
                )

                for (comida in listaComidas) {
                    val values = ContentValues().apply {
                        put("imagenComida", comida.imagenComida)
                        put("nombreComida", comida.nombreComida)
                        put("tipoComida", comida.tipoComida.name)
                        put("ingredientes", comida.ingredientes)
                        put("calorias", comida.calorias)
                        put("carbohidratos", comida.carbohidratos)
                        put("proteina", comida.proteina)
                        put("grasas", comida.grasas)
                    }

                    val idComidas = dbComidas.insert("Comidas", null, values)
                    Log.d("FITTrainingSQLite", "Comida insertada con ID: $idComidas")
                }


            }
        } catch (e: Exception) {
            Log.e("FITTrainingSQLite", "Error al insertar las comidas: ${e.message}")
        } finally {
            dbComidas.close()
        }
    }
    // función que devuelve la lista de comidas
    fun getListaComidas(): List<ComidasDataClass> {
        val comidasList = mutableListOf<ComidasDataClass>()
        val dbComidas = this.readableDatabase
        val selectQuery = "SELECT * FROM Comidas ORDER BY nombreComida DESC"
        val cursor: Cursor = dbComidas.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val imagenComida = cursor.getInt(cursor.getColumnIndexOrThrow("imagenComida"))
                val nombreComida =
                    cursor.getString(cursor.getColumnIndexOrThrow("nombreComida"))
                val tipoComidaString =
                    cursor.getString(cursor.getColumnIndexOrThrow("tipoComida"))
                val tipoComida = ComidasDataClass.TipoComida.valueOf(tipoComidaString)
                val ingredientes = cursor.getString(cursor.getColumnIndexOrThrow("ingredientes"))
                val calorias = cursor.getInt(cursor.getColumnIndexOrThrow("calorias"))
                val carbohidratos = cursor.getInt(cursor.getColumnIndexOrThrow("carbohidratos"))
                val proteina = cursor.getInt(cursor.getColumnIndexOrThrow("proteina"))
                val grasas = cursor.getInt(cursor.getColumnIndexOrThrow("grasas"))

                val comida = ComidasDataClass(
                    imagenComida,
                    nombreComida,
                    tipoComida,
                    ingredientes,
                    calorias,
                    carbohidratos,
                    proteina,
                    grasas
                )
                comidasList.add(comida)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return comidasList
    }
}
