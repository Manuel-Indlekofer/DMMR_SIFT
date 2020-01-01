package steps

interface Step<in I, out O> {
    fun process(input: I): O
}