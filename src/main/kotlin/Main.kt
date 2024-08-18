import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.io.File
import java.time.LocalDate
import java.util.*
import javax.xml.crypto.Data

import java.io.FileNotFoundException


object Main {
    private val scanner = Scanner(System.`in`)
    private val dataFileName = "data.csv"
//    private val materias: MutableMap<Int, Materia> = HashMap()
//    private val temas: MutableMap<Int, Tema> = HashMap()
//    private var materiaIdCounter = 1
//    private var temaIdCounter = 1

    @JvmStatic
    fun main(args: Array<String>) {
//        DataStore.deleteFile(dataFileName)
        DataStore.loadFromFile(dataFileName)
        var running: Boolean = true
        while (running) {
            printMenu()
            val choice = scanner.nextLine().toInt()
            when (choice) {
                1 -> listMaterias()
                2 -> listTemas()
                3 -> createMateria()
                4 -> createTema()
                5 -> deleteMateria()
                6 -> deleteTema()
                7 -> updateMateria()
                8 -> updateTema()
                9 -> DataStore.deleteFile(dataFileName)
                10 -> running = false
                else -> println("Invalid choice, please try again.")
            }
        }
        DataStore.saveToFile(dataFileName)
    }

    private fun printMenu() {
        println("1. List Materias")
        println("2. List Temas")
        println("3. Create Materia")
        println("4. Create Tema")
        println("5. Delete Materia")
        println("6. Delete Tema")
        println("7. Update Materia")
        println("8. Update Tema")
        println("9. Delete Data File")
        println("10. Save & Exit")
        print("Enter your choice: ")
    }

    private fun listMaterias() {
        println("Listing all Materias:")
        for (materia in DataStore.materias.values) {
            println(materia)
        }
    }

    private fun listTemas() {
        println("Listing all Temas:")
        for (tema in DataStore.temas.values) {
            println(tema)
        }
    }

    private fun createMateria() {
        print("Enter Materia name: ")
        val name = scanner.nextLine()
        print("Enter activo/inctivo (Ej: true - to activate): ")
        val activeness = scanner.nextLine().toBoolean()
        print("Enter Cost (Ej: 150.00): ")
        val cost = scanner.nextLine().toDouble()

        val materia = Materia(DataStore.materiaIdCounter++, name, LocalDate.now(), activeness, cost)
        DataStore.materias[materia.id] = (materia)
        println("Materia created: $materia")
    }

    private fun createTema() {
        print("Enter Tema name: ")
        val name = scanner.nextLine()
        print("Enter Materia ID for this Tema: ")
        val materiaId = scanner.nextLine().toInt()
        val materia = DataStore.materias[materiaId]
        print("Enter materia dificultty over a rate from 0 to 5 (Ej: 3.5): ")
        val materiaDifficulty = scanner.nextLine().toDouble()
        print("Enter activo/inctivo (Ej: true - to activate): ")
        val activeness = scanner.nextLine().toBoolean()

        if (materia != null) {
            val tema = Tema(DataStore.temaIdCounter++, name, LocalDate.now(), activeness, materiaDifficulty,materiaId)
            DataStore.temas[tema.id] = (tema)
            materia.addTemaId(tema.id)
            println("Tema created: $tema")
        } else {
            println("Materia ID not found.")
        }
    }

    private fun deleteMateria() {
        print("Enter Materia ID to delete: ")
        val id = scanner.nextLine().toInt()
        val materia = DataStore.materias[id]
        if (materia != null) {
            val tmpMateria = materia.temas.toList()
            val tmpMaerialId = materia.id

            for (temaId in tmpMateria) {
                materia.removeTemaId(temaId)
                DataStore.temas.remove(temaId)
            }

            DataStore.materias.remove(tmpMaerialId)
            println("Materia deleted: $tmpMateria")
        } else {
            println("Materia ID not found.")
        }
    }

    private fun deleteTema() {
        print("Enter Tema ID to delete: ")
        val id = scanner.nextLine().toInt()
        val tema = DataStore.temas[id]
        if (tema != null) {
            val myMateria = DataStore.materias[tema.materiaId]
            if (myMateria != null) {
                myMateria.removeTemaId(tema.id)
                DataStore.temas.remove(tema.id)
            }
            println("Tema deleted: $tema")
        } else {
            println("Tema ID not found.")
        }
    }

    private fun updateMateria() {
        print("Enter Materia ID to update: ")
        val id = scanner.nextLine().toInt()
        val materia = DataStore.materias[id]
        if (materia != null) {
            print("Enter new Materia name: ")
            val newName = scanner.nextLine()
            print("Enter activo/inctivo (Ej: true - to activate): ")
            val activeness = scanner.nextLine().toBoolean()
            print("Enter Cost (Ej: 150.00): ")
            val cost = scanner.nextLine().toDouble()

            materia.name = newName
            materia.isActive = activeness
            materia.cost = cost
            println("Materia updated: $materia")
        } else {
            println("Materia ID not found.")
        }
    }

    private fun updateTema() {
        print("Enter Tema ID to update: ")
        val id = scanner.nextLine().toInt()
        val tema = DataStore.temas[id]
        if (tema != null) {
            print("Enter new Tema name: ")
            val newName = scanner.nextLine()
            print("Enter materia dificultty over a rate from 0 to 5 (Ej: 3.5): ")
            val difficulty = scanner.nextLine().toDouble()
            print("Enter activo/inctivo (Ej: true - to activate): ")
            val activeness = scanner.nextLine().toBoolean()

            tema.name = newName
            tema.difficulty = difficulty
            tema.isActive = activeness
            println("Tema updated: $tema")
        } else {
            println("Tema ID not found.")
        }
    }
}

object DataStore {
    val materias: MutableMap<Int, Materia> = HashMap()
    val temas: MutableMap<Int, Tema> = HashMap()
    var materiaIdCounter = 0
    var temaIdCounter = 0

    fun deleteFile(fileName: String) {
        val file = File(fileName)

        if (file.exists()) {
            if (file.delete()) {
                println("File '$fileName' deleted successfully.")
                materias.clear()
                temas.clear()
                materiaIdCounter = 0
                temaIdCounter = 0
            } else {
                println("Failed to delete file '$fileName'.")
            }
        } else {
            println("File '$fileName' does not exist.")
        }
    }

    fun saveToFile(fileName: String) {
        val data = Data(materias.values.toList(), temas.values.toList(), materiaIdCounter, temaIdCounter)
        val json = Json {
            serializersModule = SerializersModule {
                contextual(LocalDateSerializer)
            }
        }.encodeToString(data)
        File(fileName).writeText(json)
    }

    fun loadFromFile(fileName: String) {
        try {
            val json = File(fileName).readText()
            val data = Json {
                serializersModule = SerializersModule {
                    contextual(LocalDateSerializer)
                }
            }.decodeFromString<Data>(json)
            materias.clear()
            temas.clear()
            data.materias.forEach { materias[it.id] = it }
            data.temas.forEach { temas[it.id] = it }
//            data.materias.forEach { materia ->
//                materia.temas.forEach { tema ->
//                    temas[tema.id] = tema
//                }
//            }
            materiaIdCounter = data.MateriasCounter
            temaIdCounter = data.TemasCounter
        } catch (e: FileNotFoundException) {
            // File not found, create a new empty file
            File(fileName).writeText("""{"materias": [], "temas": [], "MateriasCounter": 0, "TemasCounter": 0}""")
        }
    }

    @Serializable
    private data class Data(val materias: List<Materia>, val temas: List<Tema>, val MateriasCounter: Int, val TemasCounter: Int)
}

@Serializable
data class Materia(val id: Int, var name: String, @Contextual var creationDate: LocalDate, var isActive: Boolean, var cost: Double) {
    val temas: MutableList<Int> = ArrayList()

    fun addTemaId(temaId: Int) {
        temas.add(temaId)
    }

    fun removeTemaId(temaId: Int) {
        temas.remove(temaId)
    }

    override fun toString(): String {
        return "Materia{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creation date='" + creationDate + '\'' +
                ", active='" + isActive + '\'' +
                ", cost='USD " + cost + '\'' +
                ", temas=" + temas +
                '}'
    }
}

@Serializable
data class Tema(val id: Int, var name: String, @Contextual var CreationDate: LocalDate, var isActive: Boolean, var difficulty: Double, val materiaId: Int) {
    override fun toString(): String {
        return "Tema{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creation date='" + CreationDate + '\'' +
                ", active='" + isActive + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", materia=" + materiaId +
                '}'
    }
}