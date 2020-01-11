package me.feuerwehr.notification.server

import me.feuerwehr.notification.server.Konf

import com.fasterxml.jackson.databind.ObjectMapper
import com.uchuhimo.konf.*
import com.uchuhimo.konf.source.*
import com.uchuhimo.konf.source.Writer
import com.uchuhimo.konf.source.hocon.toHocon
import com.uchuhimo.konf.source.json.toJson
import com.uchuhimo.konf.source.properties.toProperties
import com.uchuhimo.konf.source.toml.toToml
import com.uchuhimo.konf.source.xml.toXml
import com.uchuhimo.konf.source.yaml.toYaml
import org.slf4j.LoggerFactory
import java.io.*
import java.util.*

class ConfigWrapper(
    var config: Konf = Konf()
) : Konf {
    override fun withLoadTrigger(
        description: String,
        trigger: (config: Config, load: (source: Source) -> Unit) -> Unit
    ) = config.withLoadTrigger(description, trigger)

    override fun withSource(source: Source) = config.withSource(source)

    fun load(file: File) = load(java.io.FileInputStream(file), file.name)
    fun load(stream: InputStream, name: String) =
        load(stream, ConfigFormat.findByEndingOrNull(name.split(".").last()) ?: fallbackFormat)

    fun load(stream: InputStream, format: ConfigFormat) {
        config = format.load(config, stream)
    }

    fun save(stream: OutputStream, format: ConfigFormat) = format.save(config, stream)
    fun save(stream: OutputStream, name: String) =
        save(stream, ConfigFormat.findByEndingOrNull(name.split(".").last()) ?: fallbackFormat)

    fun save(file: File) = save(java.io.FileOutputStream(file), file.name)


    override val itemWithNames: List<Pair<Item<*>, String>>
        get() = config.itemWithNames

    override val mapper: ObjectMapper
        get() = config.mapper
    override val name: String
        get() = config.name
    override val parent: Konf?
        get() = config.parent
    override val sources: Deque<Source>
        get() = config.sources
    override val specs: List<Spec>
        get() = config.specs

    override fun addItem(item: Item<*>, prefix: String) = config.addItem(item, prefix)

    override fun addSpec(spec: Spec) = config.addSpec(spec)

    override fun at(path: String) = config.at(path)

    override fun clear() = config.clear()

    override fun contains(item: Item<*>) = config.contains(item)

    override fun contains(path: Path) = config.contains(path)

    override fun contains(name: String) = config.contains(name)

    override fun containsRequired() = config.containsRequired()

    override fun disable(feature: Feature) = config.disable(feature)

    override fun enable(feature: Feature) = config.enable(feature)

    override fun <T> get(item: Item<T>) = config[item]

    override fun <T> get(name: String) = config.get<T>(name)

    override fun <T> getOrNull(item: Item<T>) = config.getOrNull(item)

    override fun <T> getOrNull(name: String) = config.getOrNull<T>(name)

    override fun isEnabled(feature: Feature) = config.isEnabled(feature)

    override fun iterator() = config.iterator()

    override fun <T> lazySet(item: Item<T>, thunk: (config: ItemContainer) -> T) = config.lazySet(item, thunk)
    override fun <T> lazySet(name: String, thunk: (config: ItemContainer) -> T) = config.lazySet(name, thunk)

    override fun <T> lock(action: () -> T) = config.lock(action)

    override fun nameOf(item: Item<*>) = config.nameOf(item)

    override fun plus(config: Konf) = this.config.plus(config)

    override fun <T> property(item: Item<T>) = config.property(item)

    override fun <T> property(name: String) = config.property<T>(name)
    override fun rawSet(item: Item<*>, value: Any?) = config.rawSet(item, value)

    override fun <T> set(item: Item<T>, value: T) = config.set(item, value)

    override fun <T> set(name: String, value: T) = config.set(name, value)

    override fun toMap() = config.toMap()

    override fun unset(item: Item<*>) = config.unset(item)

    override fun unset(name: String) = config.unset(name)

    override fun validateRequired() = config.validateRequired()

    override fun withFallback(config: Konf) = config.withFallback(config)

    override fun withLayer(name: String) = config.withLayer(name)

    override fun withPrefix(prefix: String) = config.withPrefix(prefix)

    companion object {
        private val fallbackFormat = ConfigFormat.HOCON
        private val logger = LoggerFactory.getLogger("Config")
    }
}

enum class ConfigFormat(private val handler: FormatHandler, private vararg val fileEndings: String) {
    NONE(EmptyFormatHandler),
    JSON(JsonFormatHandler, "json"),
    YAML(YamlFormatHandler, "yml", "yaml"),
    HOCON(HoconFormatHandler, "conf", "hocon"),
    XML(XmlFormatHandler, "xml"),
    PROPERTIES(PropertiesFormatHandler, "properties"),
    TOML(TomlFormatHandler, "toml",/* Looks very similar*/"ini");

    @Throws(IOException::class)
    fun load(config: Config, stream: InputStream) = handler.load(config).inputStream(stream)

    @Throws(IOException::class)
    fun save(config: Config, stream: OutputStream) = handler.save(config).toOutputStream(stream)

    @Throws(IOException::class)
    fun load(config: Config ) = handler.load(config)

    @Throws(IOException::class)
    fun save(config: Config) = handler.save(config)

    private interface FormatHandler {
        @Throws(IOException::class)
        fun load(config: Config): Loader

        @Throws(IOException::class)
        fun save(config: Config): Writer
    }

    private object YamlFormatHandler : FormatHandler {
        override fun load(config: Config) = config.from.yaml
        override fun save(config: Config) = config.toYaml
    }

    private object JsonFormatHandler : FormatHandler {
        override fun load(config: Config) = config.from.json
        override fun save(config: Config) = config.toJson
    }

    private object HoconFormatHandler : FormatHandler {
        override fun load(config: Config) = config.from.hocon
        override fun save(config: Config) = config.toHocon
    }

    private object XmlFormatHandler : FormatHandler {
        override fun load(config: Config) = config.from.xml
        override fun save(config: Config) = config.toXml
    }

    private object TomlFormatHandler : FormatHandler {
        override fun load(config: Config) = config.from.toml
        override fun save(config: Config) = config.toToml
    }

    private object PropertiesFormatHandler : FormatHandler {
        override fun load(config: Config) = config.from.properties
        override fun save(config: Config) = config.toProperties
    }

    private object EmptyFormatHandler : FormatHandler {
        override fun load(config: Config) = Loader(config, VoidLoader)
        override fun save(config: Config) = VoidWriter
    }

    private object VoidWriter : Writer {
        override fun toOutputStream(outputStream: OutputStream) {}
        override fun toWriter(writer: java.io.Writer) {}
    }

    private object VoidLoader : Provider, Source {

        override val info = SourceInfo()
        override val tree: TreeNode = VoidTreeNode

        override fun inputStream(inputStream: InputStream) = this

        override fun reader(reader: Reader) = this
    }

    private object VoidTreeNode : TreeNode {
        override val children: MutableMap<String, TreeNode> = mutableMapOf()
    }

    companion object {
        fun findByEndingOrNull(ending: String) = values().find { format ->
            format.fileEndings.any {
                ending.equals(it, true)
            }
        }
    }
}