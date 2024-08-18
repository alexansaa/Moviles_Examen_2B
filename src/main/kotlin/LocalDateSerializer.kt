import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.CompositeDecoder


object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
//        val dateString = value.format(formatter)
        val dateString = formatter.format(value)
        encoder.encodeString(dateString)
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        val dateString = decoder.decodeString()
        return LocalDate.parse(dateString, formatter)
    }
}