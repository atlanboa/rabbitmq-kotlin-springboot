rootProject.name = "rabbitmq"
include("producer")
include("consumer")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}