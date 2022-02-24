package pl.tapo24.tapo24.controller


import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.kotlin.InfluxDBClientKotlinFactory
import com.influxdb.client.write.Point
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.tapo24.tapo24.dao.FavoritesOffensesRepository
import pl.tapo24.tapo24.dao.Repository.InstallationStatusRepository
import pl.tapo24.tapo24.dao.Repository.ModuleClickedRepository
import pl.tapo24.tapo24.dao.Repository.VersionsRepository
import pl.tapo24.tapo24.dao.UniqueInstallationIdRepository
import pl.tapo24.tapo24.dao.entity.*
import pl.tapo24.tapo24.others.gen_UID
import java.time.Instant
import java.util.*
import javax.validation.Valid

@CrossOrigin(origins = ["*"])
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api/installation")
class TapoController(private val UniqueInstallationIdRepository: UniqueInstallationIdRepository,
                     private val VersionsRepository:VersionsRepository,
                     private val InstallationStatusRepository:InstallationStatusRepository) {

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
    @GetMapping("/get_last_versions")
    fun getLastVersion():Versions {
        return if (VersionsRepository.findFirstByOrderByIdDesc().version_number.isNullOrEmpty()) Versions(0,"")
        else VersionsRepository.findFirstByOrderByIdDesc()
    }


    @PostMapping("/install_Param")
    fun setLastStart(@Valid @RequestBody data: InstallationStatus): ResponseEntity<InstallationStatus> {
        val version = VersionsRepository.findFirstByOrderByIdDesc().version_number
        val unixTime = System.currentTimeMillis() / 1000L
        return if (InstallationStatusRepository.existsByUID(data.UID)){
            InstallationStatusRepository.updateLast_startByUID(UID = data.UID, last_start = unixTime)
            InstallationStatusRepository.updateVersion_numberByUID(UID = data.UID, version_number = version)
            ResponseEntity(InstallationStatusRepository.findByUID(data.UID), HttpStatus.OK)
        }
        // updateLast_startByUID(last_start = data.last_start, UID = data.UID)
        else {
            data.last_start = unixTime
            data.version_number = version
            InstallationStatusRepository.save(data)
            ResponseEntity(data, HttpStatus.OK)
        }
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
                .time(Instant.now(), WritePrecision.NS)
            val writeApi = client.getWriteKotlinApi()
            writeApi.writePoint(point)
        }
        client.close()

    }
}
