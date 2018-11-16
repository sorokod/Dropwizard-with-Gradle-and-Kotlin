import com.codahale.metrics.health.HealthCheck

class HWHealthCheck(private val template: String) : HealthCheck() {

    override fun check(): Result {
        return when(String.format(template, "TEST").contains("TEST")) {
            false -> Result.unhealthy("template doesn't include a name")
            true -> Result.healthy()
        }
    }
}
