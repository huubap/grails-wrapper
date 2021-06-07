package grails.init

import org.springframework.boot.cli.compiler.grape.AetherGrapeEngine
import org.springframework.boot.cli.compiler.grape.AetherGrapeEngineFactory
import org.springframework.boot.cli.compiler.grape.DependencyResolutionContext
import org.springframework.boot.cli.compiler.grape.RepositoryConfiguration

/**
 * Created by jameskleeh on 10/31/16.
 */
class RunCommand {

    static void main(String[] args) {
        ClassLoader previousClassLoader = Thread.currentThread().contextClassLoader

        Properties props = new Properties()
        String grailsVersion
        try {
            props.load(new FileInputStream("gradle.properties"))
            grailsVersion = props.getProperty("grailsVersion")
        } catch (IOException e) {
            throw new RuntimeException("Could not determine grails version due to missing properties file")
        }

        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(previousClassLoader)

        List<RepositoryConfiguration> repositoryConfigurations = [new RepositoryConfiguration("grailsCentral", new URI("https://repo.grails.org/artifactory/core"), false)]

        AetherGrapeEngine grapeEngine = AetherGrapeEngineFactory.create(groovyClassLoader, repositoryConfigurations, new DependencyResolutionContext())
        grapeEngine.grab([:], [group: "org.grails", module: "grails-shell", version: grailsVersion])

        Thread.currentThread().setContextClassLoader(groovyClassLoader)

        try {
            groovyClassLoader.loadClass('org.grails.cli.GrailsCli').main(args)
        } finally {
            Thread.currentThread().setContextClassLoader(previousClassLoader)
        }
    }
}
