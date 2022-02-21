package pl.tapo24.tapo24.controller


import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.influxdb.client.write.Point
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import pl.tapo24.tapo24.dao.FavoritesOffensesRepository
import pl.tapo24.tapo24.dao.Repository.ModuleClickedRepository
import pl.tapo24.tapo24.dao.UniqueInstallationIdRepository
import pl.tapo24.tapo24.dao.entity.FavoritesOffenses
import pl.tapo24.tapo24.dao.entity.ModuleClicked
import pl.tapo24.tapo24.dao.entity.UniqueInstallationId
import pl.tapo24.tapo24.others.gen_UID
import java.time.Instant
import java.util.*
import javax.validation.Valid


@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api")
class TapoController(private val UniqueInstallationIdRepository: UniqueInstallationIdRepository) {

    @GetMapping("/get_UID")
    fun getUidInstallation(): Any {
        do {
            val UID = gen_UID()
            if (UniqueInstallationIdRepository.findUID(UID)?.UID.isNullOrEmpty()) {
                UniqueInstallationIdRepository.save(UniqueInstallationId(0, UID))
                return Collections.singletonMap("UID", UID)
            }
        } while (true)
    }



}
@RestController
class TapoFavorite(private val FavoritesOffensesRepository: FavoritesOffensesRepository){
    @PostMapping("/put_favorite")
    fun putFavorite(@Valid @RequestBody data: FavoritesOffenses){
        var repo = FavoritesOffensesRepository.findUID(data.UID)
        if (repo?.UID.isNullOrEmpty()) {
           FavoritesOffensesRepository.save(data)
       } else {
           repo!!.favorite = data.favorite
            FavoritesOffensesRepository.save(repo)
        }
        return
    }
}
@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/api")
class TapoModuleClicked(private  val  ModuleClicked: ModuleClickedRepository) {
    @Value("\${spring.influx.token}")
    private val token: String? = null
    //@CrossOrigin(origins = ["http://localhost:8082"])
    @PostMapping("/module_clicked")
    fun putModuleCLicked(@Valid @RequestBody data: ModuleClicked){
        data.moduleName = data.moduleName.lowercase()
        if (data.moduleName.indexOf(".pdf", 0) > 0){
            write("used_pdf",data.moduleName)
        } else {
            write("used_module",data.moduleName)
        }

        return
    }
    fun write(bucket: String, module: String) = runBlocking {

        // You can generate an API token from the "API Tokens Tab" in the UI
        val org = "Tapo24"

        val client = InfluxDBClientKotlinFactory.create("http://51.83.179.244:8062", token?.toCharArray() ?: "".toCharArray(), org, bucket)
        client.use {
            val point = Point
                .measurement("clicked_on")
                .addTag("module_name",module)
                .addField(module, 1)
                .time(Instant.now(), WritePrecision.NS);
            val writeApi = client.getWriteKotlinApi()
            writeApi.writePoint(point)
        }
        client.close()

    }
}
