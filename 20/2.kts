import java.io.File

enum class PulseType {
    HIGH, LOW;
}

data class Pulse(val from: String, val to: String, val type: PulseType)


interface Module {
    val name: String
    val destinations: List<String>
    fun process(pulse: Pulse): List<Pulse>
    var lastOutgoingPulseType: PulseType?
    fun reset()
}

data class BroadcastModule(override val name: String, override val destinations: List<String>) : Module {
    override var lastOutgoingPulseType: PulseType? = null
    override fun process(pulse: Pulse): List<Pulse> {
        lastOutgoingPulseType = pulse.type
        return destinations.map {
            Pulse(name, it, pulse.type)
        }
    }

    override fun reset() {
        lastOutgoingPulseType = null
    }
}

class FlipFlopModule(override val name: String, override val destinations: List<String>) : Module {

    private var on = false
    override var lastOutgoingPulseType: PulseType? = null

    override fun process(pulse: Pulse): List<Pulse> {
        if (pulse.type == PulseType.LOW) {
            on = !on
            val type = if (on) PulseType.HIGH else PulseType.LOW
            lastOutgoingPulseType = type
            return destinations.map { Pulse(name, it, type) }
        }
        return emptyList()
    }

    override fun reset() {
        on = false
        lastOutgoingPulseType = null
    }
}

class ConjunctionModule(override val name: String, override val destinations: List<String>) : Module {

    private val lastPulses = mutableMapOf<String, PulseType>()
    override var lastOutgoingPulseType: PulseType? = null

    fun initializeSenders(senders: List<String>) {
        for (sender in senders) {
            lastPulses[sender] = PulseType.LOW
        }
    }

    override fun process(pulse: Pulse): List<Pulse> {
        lastPulses[pulse.from] = pulse.type
        return if (lastPulses.all { it.value == PulseType.HIGH }) {
            lastOutgoingPulseType = PulseType.LOW
            destinations.map { Pulse(name, it, PulseType.LOW) }
        } else {
            lastOutgoingPulseType = PulseType.HIGH
            destinations.map { Pulse(name, it, PulseType.HIGH) }
        }
    }

    override fun reset() {
        for (sender in lastPulses.keys) {
            lastPulses[sender] = PulseType.LOW
        }
        lastOutgoingPulseType = null
    }
}

class DummyModule(override val name: String) : Module {
    override var lastOutgoingPulseType: PulseType? = null
    override val destinations = emptyList<String>()
    override fun process(pulse: Pulse): List<Pulse> {
        return emptyList()
    }

    override fun reset() {
        lastOutgoingPulseType = null
    }
}

val modules = File("input").readLines().map {
    val (from, toStr) = it.split(" -> ").take(2)
    val to = toStr.split(", ")
    when (from[0]) {
        'b' -> BroadcastModule(from, to)
        '%' -> FlipFlopModule(from.drop(1), to)
        '&' -> ConjunctionModule(from.drop(1), to)
        else -> throw IllegalArgumentException("Unknown module type: $from")
    }
} + DummyModule("rx")

modules.filterIsInstance<ConjunctionModule>().forEach { conjunctionModule ->
    conjunctionModule.initializeSenders(modules.filter { it.destinations.contains(conjunctionModule.name) }
        .map { it.name })
}
val modulesByName = modules.associateBy { it.name }

fun cyclesUntilHigh(end: String): Long {
    modules.forEach { it.reset() }
    val pulses = mutableListOf<Pulse>()
    var result = 0L
    val endModule = modulesByName[end]!!
    while (endModule.lastOutgoingPulseType != PulseType.HIGH) {
        pulses.add(Pulse("button", "broadcaster", PulseType.LOW))
        result++
        while (pulses.isNotEmpty()) {
            val pulse = pulses.removeAt(0)
            val module = modulesByName[pulse.to] ?: throw IllegalArgumentException("Unknown module: ${pulse.to}")
            if (endModule.lastOutgoingPulseType == PulseType.HIGH) {
                return result
            }
            val newPulses = module.process(pulse)
            pulses.addAll(newPulses)
        }
    }
    return result
}

fun gcd(a: Long, b: Long): Long {
    if (b == 0L) return a
    return gcd(b, a % b)
}

fun lcm(a: Long, b: Long): Long {
    return a / gcd(a, b) * b
}

// Knowledge about endNodes structure: see input_graphviz.svg
val endNodes =
    modules.filter { it.destinations.contains(modules.single { it.destinations.contains("rx") }.name) }.map { it.name }
endNodes.map { cyclesUntilHigh(it) }.reduce { acc, cycle -> lcm(acc, cycle) }.let(::println)
