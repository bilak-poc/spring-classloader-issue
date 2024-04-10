Reproducing issue with `org.springframework.boot.loader.launch.LaunchedClassLoader`.

### setup

* oracle db is running
* user is created with script [oracle-create-user.sql](./src/main/resources/oracle-create-user.sql)
* datasoruce params are correct [application.yaml](./src/main/resources/application.yaml)
* project is built with ./gradlew assemble
* jar is executed with `java -jar build/libs/app.jar`

### analysis

After closer look into logs, it's visible that `LaunchedClassLoader` has two instances and therefore there might be a
classloading issue. If we would setup `java.util.concurrent.ForkJoinPool.common.threadFactory` only in `Application`
class, then it would probably work. However, there's an issue with oracles' `PhysicalConnection` class which already
initializes the `ForkJoinPool` and therefore we are not able to re-initialize it in `Application` class. 