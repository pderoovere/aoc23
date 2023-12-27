import java.io.File

enum class PulseType {
    HIGH, LOW;
}

data class Pulse(val from: String, val to: String, val type: PulseType)


interface Module {
    val name: String
    val destinations: List<String>
    fun process(pulse: Pulse): List<Pulse>
}

class BroadcastModule(override val name: String, override val destinations: List<String>) : Module {
    override fun process(pulse: Pulse) = destinations.map {
        Pulse(name, it, pulse.type)
    }
}

class FlipFlopModule(override val name: String, override val destinations: List<String>) : Module {

    private var on = false

    override fun process(pulse: Pulse): List<Pulse> {
        if (pulse.type == PulseType.LOW) {
            on = !on
            val type = if (on) PulseType.HIGH else PulseType.LOW
            return destinations.map { Pulse(name, it, type) }
        }
        return emptyList()
    }
}

class ConjunctionModule(override val name: String, override val destinations: List<String>) : Module {

    private val lastPulses = mutableMapOf<String, PulseType>()

    fun initializeSenders(senders: List<String>) {
        for (sender in senders) {
            lastPulses[sender] = PulseType.LOW
        }
    }

    override fun process(pulse: Pulse): List<Pulse> {
        lastPulses[pulse.from] = pulse.type
        return if (lastPulses.all { it.value == PulseType.HIGH }) {
            destinations.map { Pulse(name, it, PulseType.LOW) }
        } else {
            destinations.map { Pulse(name, it, PulseType.HIGH) }
        }
    }
}

class DummyModule : Module {
    override val name = "output"
    override val destinations = emptyList<String>()
    override fun process(pulse: Pulse): List<Pulse> {
        return emptyList()
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
}

val modulesByName = modules.associateBy { it.name }
modules.filterIsInstance<ConjunctionModule>().forEach { conjunctionModule ->
    conjunctionModule.initializeSenders(modules.filter { it.destinations.contains(conjunctionModule.name) }
        .map { it.name })
}

val pulses = mutableListOf<Pulse>()
var countLow = 0
var countHigh = 0
(0..<1000).forEach { _ ->
    pulses.add(Pulse("button", "broadcaster", PulseType.LOW))
    countLow++
    while (pulses.isNotEmpty()) {
        val pulse = pulses.removeAt(0)
        val module = modulesByName[pulse.to] ?: DummyModule()
        val newPulses = module.process(pulse)
        countLow += newPulses.count { it.type == PulseType.LOW }
        countHigh += newPulses.count { it.type == PulseType.HIGH }
        pulses.addAll(newPulses)
    }
}

println(countLow)
println(countHigh)
println(countLow * countHigh)